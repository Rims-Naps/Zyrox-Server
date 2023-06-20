package com.zenyte.game.content.treasuretrails.plugins;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.follower.impl.MiscPet;
import com.zenyte.game.content.treasuretrails.ClueLevel;
import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.content.treasuretrails.rewards.ClueReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.area.Entrana;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Kris | 25/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ClueCasket extends ItemPlugin {

    @Override
    public void handle() {
        bind("Open", (player, item, container, slotId) ->  {
            val tier = Objects.requireNonNull(ClueReward.getTier(item.getId()));
            Item mimic = null;
            int mimicSlot = -1;
            if (item.getNumericAttribute("The Mimic").intValue() == 0 && TreasureTrail.isMimicEnabled(player)) {
                if (tier == ClueLevel.ELITE || tier == ClueLevel.MASTER) {
                    val rate = tier == ClueLevel.MASTER ? 15 : 35;
                    if (Utils.random(rate - 1) == 0) {
                        mimic = new Item(ItemId.MIMIC);
                        mimic.setAttribute("The Mimic", 1);
                        mimic.setAttribute("The Mimic rolls", tier == ClueLevel.MASTER ? 6 : 5);
                        mimic.setAttribute("The Mimic original casket", item.getId());
                        player.getInventory().deleteItem(item.getId(), 1);
                        val full = !player.getInventory().hasFreeSlots();
                        player.getInventory().addOrDrop(mimic);
                        Item finalMimic1 = mimic;
                        val entry = player.getInventory().getContainer().getItems().int2ObjectEntrySet().stream().filter(it -> it.getValue().equals(finalMimic1)).findAny();
                        if (entry.isPresent()) {
                            //Set mimic instance to the item object in inventory, as a copy is made whenever an item is added to the inventory.
                            mimicSlot = entry.get().getIntKey();
                            mimic = player.getInventory().getItem(mimicSlot);

                        }

                        //Handle exception case.
                        if (full) {
                            player.sendMessage("You found a Mimic!");
                            player.getInterfaceHandler().closeInterfaces();
                            return;
                        }
                    }
                }
            }
            val isMimic = mimic != null;
            if (isMimic) {
                val finalMimic = Objects.requireNonNull(mimic);
                val finalMimicSlot = mimicSlot;
                player.getDialogueManager().start(new Dialogue(player) {
                    @Override
                    public void buildDialogue() {
                        options("Do you want a chance of a Mimic boss fight?",
                                new DialogueOption("Yes, I want a chance of a Mimic boss fight.", () -> {
                                    if (!player.getInventory().containsItem(finalMimic)) {
                                        return;
                                    }
                                    finalMimic.setAttribute("The Mimic initialized", 1);
                                    player.sendMessage("The chest turned out to be the Mimic!");
                                }),
                                new DialogueOption("No, I don't want those.", () -> {
                                    if (!player.getInventory().containsItem(finalMimic)) {
                                        return;
                                    }
                                    player.getInventory().deleteItem(finalMimicSlot, finalMimic);
                                    val casketId = finalMimic.getNumericAttribute("The Mimic original casket").intValue();
                                    ClueCasket.open(player, new Item(casketId), Objects.requireNonNull(ClueReward.getTier(casketId)));
                                }));
                    }
                });
                return;
            }

            if(!keepCasket(player)) {
                player.getInventory().deleteItem(new Item(item.getId()));
            } else {
                player.sendMessage(Colour.RS_GREEN.wrap("You're in luck! You managed to find another casket inside!"));
            }
            open(player, item, tier);
        });
    }

    public boolean keepCasket(Player player) {
        if(Utils.random(0, 100) < getChance(player)) {
            return true;
        }
        return false;
    }

    private int getChance(Player p) {
        switch(p.getMemberRank()) {
            case SAPPHIRE_MEMBER:
                return 3;
            case EMERALD_MEMBER:
                return 6;
            case RUBY_MEMBER:
                return 9;
            case DIAMOND_MEMBER:
                return 12;
            case DRAGONSTONE_MEMBER:
                return 15;
            case ONYX_MEMBER:
                return 18;
            case ZENYTE_MEMBER:
                return 21;
            default:
            case NONE:
                return 0;
        }
    }

    static final void open(@NotNull final Player player, @NotNull final Item item, @NotNull final ClueLevel tier) {
        val rewards = Objects.requireNonNull(ClueReward.getTable(item.getId()));
        val loot = rewards.roll(player.inArea(Entrana.class));
        player.sendMessage("Well done, you've completed the Treasure Trail!");
        val tierString = tier.toString().toLowerCase();
        val count = player.getNumericAttribute("completed " + tierString + " treasure trails").intValue() + 1;
        player.addAttribute("completed " + tierString + " treasure trails", count);
        player.sendMessage(Colour.RED.wrap("You have completed " + count + " " + tierString + " Treasure Trail" + (count == 1 ? "" : "s") + "."));
        val requirement = tier.getMilestoneRequirement();
        if (requirement != -1 && requirement <= count) {
            tier.getMilestoneRewardConsumer().accept(player);
        }
        long value = 0;
        for (val it : loot) {
            value += it.getSellPrice() * it.getAmount();
            WorldBroadcasts.broadcast(player, BroadcastType.TREASURE_TRAILS, it.getId(), tierString);
        }
        player.sendMessage(Colour.RED.wrap("Your treasure is worth around " + Utils.format(value) + " gold!"));
        player.getTemporaryAttributes().put("treasure trails loot", loot);
        if (tier == ClueLevel.MASTER) {
            MiscPet.BLOODHOUND.roll(player, 999);
        }
        player.getMusic().playJingle(193);
        GameInterface.CLUE_SCROLL_REWARD.open(player);
    }

    @Override
    public int[] getItems() {
        return new int[] {
                ItemId.REWARD_CASKET_BEGINNER, ItemId.REWARD_CASKET_EASY, ItemId.REWARD_CASKET_MEDIUM, ItemId.REWARD_CASKET_HARD, ItemId.REWARD_CASKET_ELITE, ItemId.REWARD_CASKET_MASTER
        };
    }
}
