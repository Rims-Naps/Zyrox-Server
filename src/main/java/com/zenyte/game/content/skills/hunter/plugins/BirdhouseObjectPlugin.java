package com.zenyte.game.content.skills.hunter.plugins;

import com.zenyte.game.content.follower.impl.MiscPet;
import com.zenyte.game.content.skills.hunter.BirdHousePosition;
import com.zenyte.game.content.skills.hunter.node.BirdHouseState;
import com.zenyte.game.content.skills.hunter.node.BirdHouseType;
import com.zenyte.game.content.skills.hunter.object.Birdhouse;
import com.zenyte.game.content.skills.woodcutting.actions.BirdNests;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlainChat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * @author Kris | 24/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BirdhouseObjectPlugin implements ItemOnObjectAction, ObjectAction {

    private static final Animation animation = new Animation(827);
    private static final int[] birdPetId = {30151, 30150, 30152};
    private static final int[] godNests = {BirdNests.Nests.BLUE_EGG.getNestItemId(), BirdNests.Nests.RED_EGG.getNestItemId(), BirdNests.Nests.GREEN_EGG.getNestItemId()};

    @Override
    public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object) {
        val hunter = player.getHunter();
        val existingBirdhouse = hunter.findBirdhouse(object.getId());
        if (existingBirdhouse.isPresent()) {
            player.sendMessage("You've already placed a bird house here.");
            return;
        }
        build(player, item, OptionalInt.of(slot), object);
    }

    private final void build(@NotNull final Player player, @NotNull final Item item, @NotNull final OptionalInt slot, final WorldObject object) {
        val inventory = player.getInventory();
        if (!inventory.containsItem(item)) {
            return;
        }
        val hunter = player.getHunter();
        val type = BirdHouseType.findThroughBirdhouse(item.getId()).orElseThrow(RuntimeException::new);
        val position = BirdHousePosition.findPosition(object.getId()).orElseThrow(RuntimeException::new);
        if (player.getSkills().getLevelForXp(Skills.HUNTER) < type.getHunterRequirement()) {
            player.getDialogueManager().start(new PlainChat(player, "You need at least level " + type.getHunterRequirement() + " Hunter to place that."));
            return;
        }
        val birdhouse = new Birdhouse(player).setState(BirdHouseState.EMPTY).setType(type).setPosition(position).setSeedsFilled(0);
        hunter.addBirdhouse(birdhouse);
        player.setAnimation(animation);
        player.sendSound(2433);
        if (slot.isPresent()) {
            inventory.deleteItem(slot.getAsInt(), item);
        } else {
            inventory.deleteItem(item);
        }
    }

    @Override
    public Object[] getItems() {
        val list = new ObjectArrayList<>();
        for (val birdhouse : BirdHouseType.getValues()) {
            list.add(birdhouse.getBirdhouseId());
        }
        return list.toArray();
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Build")) {
            val bestBirdhouse = findBestBirdhouse(player);
            if (!bestBirdhouse.isPresent()) {
                player.getDialogueManager().start(new PlainChat(player, "You do not have a birdhouse with the required hunter level to build here."));
                return;
            }
            build(player, new Item(bestBirdhouse.get().getBirdhouseId()), OptionalInt.empty(), object);
            return;
        }
        val birdhouse = player.getHunter().findBirdhouse(object.getId()).orElseThrow(RuntimeException::new);
        switch(option) {
            case "Seeds":
                checkSeeds(player, birdhouse);
                break;
            case "Interact":
                player.getDialogueManager().start(new Dialogue(player) {
                    @Override
                    public void buildDialogue() {
                        options(new DialogueOption("Check seed levels.", () -> checkSeeds(player, birdhouse)),
                                new DialogueOption("Dismantle my trap.", () -> dismantle(player, birdhouse)),
                                new DialogueOption("Do nothing."));
                    }
                });
                break;
            case "Empty":
            case "Dismantle":
                if (option.equals("Dismantle")) {
                    dismantleWithWarning(player, birdhouse);
                } else {
                    dismantle(player, birdhouse);
                }
                break;
            default:
                throw new IllegalStateException(option);
        }
    }

    private final void dismantleWithWarning(@NotNull final Player player, @NotNull final Birdhouse birdhouse) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options(Colour.RED.wrap("You will lose any birds and seed in the birdhouse."),
                        new DialogueOption("Yes - dismantle and " + Colour.RED.wrap("discard the birdhouse and contents."), () -> dismantle(player, birdhouse)),
                        new DialogueOption("No, leave it be."));
            }
        });
    }

    private static final SoundEffect dismantleSound = new SoundEffect(2632, 0, 30);

    private final void dismantle(@NotNull final Player player, @NotNull final Birdhouse birdhouse) {
        val timeRequired = TimeUnit.MINUTES.toMillis(birdhouse.getLengthInMinutes());
        val startTime = birdhouse.getFillTime();
        val currentTime = System.currentTimeMillis();
        //If the trap hasn't been set up yet, let's just force the progress to be 0.
        val progress = startTime == Long.MAX_VALUE ? 1 : Math.max(0, Math.min(1, (((double) currentTime - (startTime == 0 ? currentTime : startTime)) / timeRequired)));
        val seedsRemaining = 10 - (int) Math.floor(10 * progress);
        val seedsUsed = 10 - seedsRemaining;

        val list = new ObjectArrayList<Item>();
        list.add(new Item(ItemId.CLOCKWORK));
        if (progress == 1) {
            val type = birdhouse.getType();
            val chanceOfNest = type.getChanceOfNest();
            list.add(new Item(ItemId.FEATHER, Utils.random(4, 6) * 10));
            if (type.getBirdhouseId() == ItemId.REDWOOD_BIRD_HOUSE) {
                player.getDailyChallengeManager().update(SkillingChallenge.LOOT_REDWOOD_BIRDHOUSE);
            }
            for (int i = 0; i < seedsUsed; i++) {
                val isGodNest = Utils.random(95) == 0;
                if (Utils.randomDouble() > chanceOfNest && !isGodNest) {
                    continue;
                }
                if(isGodNest) {
                    list.add(new Item(godNests[Utils.random(2)]));
                } else {
                    list.add(new Item(BirdNests.Nests.rollRandomNest(false).getNestItemId()));
                }
            }
            if (Utils.random(399) == 0) {
                val nestConstant = ClueItem.pseudoRandomNestConstant();
                if (nestConstant.isPresent()) {
                    val skillingItem = new Item(nestConstant.getAsInt());
                    list.add(skillingItem);
                    val name = skillingItem.getName().toLowerCase();
                    val itemTypeName = name.substring(0, name.indexOf(" ("));
                    val clueTypeName = name.substring(name.indexOf(" (") + 2, name.length() - 1);
                    val prefix = Utils.getAOrAn(clueTypeName);
                    player.sendMessage(Colour.RED.wrap("You find " + prefix + " " + clueTypeName + " " + itemTypeName + "!"));
                }
            }
            if(Utils.random(999) == 0) {
                int petid = birdPetId[Utils.random(2)];
                boolean haspet = player.containsItem(petid);
                for(int i = 0;i<100 && haspet;i++) {
                    petid = birdPetId[Utils.random(2)];
                    haspet = player.containsItem(petid);
                    if(i==99){
                        petid = 0;
                        break;
                    }
                }
                if(petid != 0){
                    val pet = (petid == 30151 ? MiscPet.ZAMORAK_HAWK : petid == 30150 ? MiscPet.SARADOMIN_OWL : MiscPet.GUTHIX_RAPTOR);
                    val petitem = new Item(petid);
                    list.add(petitem);
                    WorldBroadcasts.broadcast(player, BroadcastType.PET, pet);
                }
            }
            World.spawnFloorItem(new Item(ItemId.RAW_BIRD_MEAT, seedsUsed), player);
            player.getSkills().addXp(Skills.HUNTER, type.getHunterExperience() * seedsUsed);
        } else {
            player.sendFilteredMessage("You dismantle the trap.");
        }
        val inventory = player.getInventory();
        list.forEach(inventory::addOrDrop);
        player.setAnimation(animation);
        player.sendSound(2433);
        player.sendSound(dismantleSound);
        player.getHunter().removeBirdhouse(birdhouse);
    }

    private final void checkSeeds(@NotNull final Player player, @NotNull final Birdhouse birdhouse) {
        player.getDialogueManager().finish();
        val state = birdhouse.getState();
        switch(state) {
            case EMPTY:
            case IN_PROGRESS:
                val timeRequired = TimeUnit.MINUTES.toMillis(birdhouse.getLengthInMinutes());
                val startTime = birdhouse.getFillTime();
                if (startTime == 0) {
                    player.getDialogueManager().start(new PlainChat(player, "Your birdhouse seed level is: " + birdhouse.getSeedsFilled() + "/10. <col=a50000>It must be full of seed before " +
                            "it<br><br><col=a50000>will start catching birds."));
                    return;
                }
                val currentTime = System.currentTimeMillis();
                val progress = (((double) currentTime - startTime) / timeRequired);
                val seedsRemaining = 9 - (int) Math.floor(9 * progress);
                val endTime = startTime + timeRequired;
                val secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(endTime - currentTime);
                val seconds = secondsRemaining % 60;
                val minutes = secondsRemaining / 60;
                player.getDialogueManager().start(new PlainChat(player, "Your birdhouse trap is set and consuming seed. Seed level is: " + seedsRemaining + "/10. Time remaining: " +
                        "" + minutes + " minute" + (minutes == 1 ? "" : "s") + ", " + seconds + " second" + (seconds == 1 ? "" : "s") + "."));
                break;
            case FINISHED:
                player.getDialogueManager().start(new PlainChat(player, "Your birdhouse trap has run out of seed. It is ready to dismantle to<br><br>see what is has caught."));
                break;
        }
    }

    private final Optional<BirdHouseType> findBestBirdhouse(@NotNull final Player player) {
        val inventory = player.getInventory();
        val level = player.getSkills().getLevelForXp(Skills.HUNTER);
        BirdHouseType bestType = null;
        for (int i = 0; i < 28; i++) {
            val item = inventory.getItem(i);
            if (item == null) {
                continue;
            }
            val birdhouse = BirdHouseType.findThroughBirdhouse(item.getId());
            if (!birdhouse.isPresent()) {
                continue;
            }
            val type = birdhouse.get();
            if (type.getHunterRequirement() > level) {
                continue;
            }
            if (bestType == null || type.ordinal() > bestType.ordinal()) {
                bestType = type;
            }
        }
        return Optional.ofNullable(bestType);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                30565, 30566, 30567, 30568
        };
    }
}
