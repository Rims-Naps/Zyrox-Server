package com.zenyte.game.item;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.boss.corporealbeast.CorporealBeastDynamicArea;
import com.zenyte.game.content.consumables.Consumable;
import com.zenyte.game.content.event.easter2020.area.EggPlantArea;
import com.zenyte.game.content.event.easter2020.area.RabbitWarrenArea;
import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.PetWrapper;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.area.LandOfSnowArea;
import com.zenyte.game.world.region.area.plugins.DropPlugin;
import com.zenyte.plugins.dialogue.DestroyItemDialogue;
import com.zenyte.plugins.dialogue.followers.PetFishDropD;
import com.zenyte.utils.StaticInitializer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

/**
 * @author Tom
 */
@StaticInitializer
@Slf4j
public class ItemActionHandler {

    public static final ItemPlugin DEFAULT_ACTION;

    static {
        DEFAULT_ACTION = new ItemPlugin() {
            @Override
            public void handle() {

            }

            @Override
            public int[] getItems() {
                return new int[0];
            }
        };
        DEFAULT_ACTION.setDefaultHandlers();
    }

    public static final void handle(final Player player, final int itemId, final int slotId, final int option) {
        if (option == 6) {
            log.info("[" + itemId + "]" + " Item examine: " + ItemDefinitions.getOrThrow(itemId).getName() + ".");
            Examine.sendItemExamine(player, itemId);
            return;
        }
        if (player.isLocked() || player.isFullMovementLocked() || !player.getInterfaceHandler().isPresent(GameInterface.INVENTORY_TAB)) {
            return;
        }
        player.stopAll(false, true, true);
        final Item item = player.getInventory().getItem(slotId);
        if (item == null || item.getId() != itemId) {
            return;
        }
        val optionName = Objects.requireNonNull(item.getDefinitions().getOption(option - 1));
        val action = ItemPlugin.getPlugin(itemId);
        val handler = action.getHandler(optionName);
        if (handler != null) {
            val pluginClassName = action.getClass().getSimpleName();
            log.info("[" + (pluginClassName.isEmpty() ? "Absent" : pluginClassName) + "]" + " " + item.getName() + ":" +
                    " " + item.getId() + " x " + item.getAmount() +
                    ", Slot: " + slotId + ", Option: " + (optionName + " [" + option + "]"));
            handler.handle(player, item, player.getInventory().getContainer(), slotId);
            return;
        }
        log.info("Item option: " + item.getId() + " x " + item.getAmount() + ", " + item.getName() + ", " + slotId +
                ", " + option);
        player.sendMessage("Nothing interesting happens.");
    }

    public static final Int2ObjectOpenHashMap<ItemPlugin> INT_ACTIONS = new Int2ObjectOpenHashMap<ItemPlugin>();

    public static final void add(final Class<?> c) {
        try {
            final Object o = c.newInstance();
            if (!(o instanceof ItemPlugin)) {
                return;
            }
            final ItemPlugin action = (ItemPlugin) o;
            for (val item : action.getItems()) {
                val old = INT_ACTIONS.put(item, action);
                if (old != null) {
                    log.info("Overriding item plugin: " + item + ", " + old.getClass().getSimpleName() + " with " + c.getSimpleName() + "!");
                }
            }
            action.handle();
            action.setDefaultHandlers();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }
    
    private static final SoundEffect OTHER_SOUND = new SoundEffect(2739);

    public static final void dropItem(final Player player, final String option, final int slotId, final int invisibleDelay, final int visibleDelay) {
        val item = player.getInventory().getItem(slotId);
        if (item == null) {
            return;
        }
        /*
        if (player.getName().toLowerCase().startsWith("tob beta") && item.getId() != 22516) {
            for (val p : World.getPlayers()) {
                if (p.isStaff()) {
                    p.sendMessage("Player '" + player.getName() + "' tried to drop item: " + item.getName() + "!");
                }
                if (Objects.equals(p.getName(), player.getName())) {
                    p.sendMessage("You can not drop items filthy beta tester (besides dawnbringer)");
                }
            }
            return;
        }
        */
        val area = player.getArea();
        if ((area instanceof DropPlugin && !((DropPlugin) area).drop(player, item)) || !player.getControllerManager().canDropItem(item)) {
            player.log(LogLevel.INFO, "Area-Dropping item '" + item + "' at " + player.getLocation() + ".");
            return;
        }
        if (option.equals("Destroy")) {
            player.getDialogueManager().start(new DestroyItemDialogue(player, item, slotId));
            return;
        }
        if (PetWrapper.getByItem(item.getId()) != null) {
            if (player.inArea(LandOfSnowArea.class)) {
                player.sendMessage("Your follower does not seem to be too fond of the festivities and won't come out.");
                return;
            }
            if (player.inArea(RabbitWarrenArea.class) || player.inArea(EggPlantArea.class)) {
                player.sendMessage("Your follower isn't interested in coming out with all those machines making noise.");
                return;
            }
            if (player.getFollower() != null) {
                player.sendMessage("You already have a follower!");
                return;
            }
            if (player.inArea("Corporeal Beast cavern") || player.getArea() instanceof CorporealBeastDynamicArea) {
                player.sendMessage("Your follower hides in fear and won't come out.");
                return;
            }
            val pet = PetWrapper.getByItem(item.getId());
            if (pet.petId() == -1) {
                player.getDialogueManager().start(new PetFishDropD(player, item, slotId));
                return;
            }
            player.getInventory().deleteItem(slotId, item);
            player.setFollower(new Follower(pet.petId(), player));
            player.setAnimation(PetWrapper.DROP_ANIMATION);
            return;
        }
        val degradableItem = DegradableItem.ITEMS.get(item.getId());
        if (degradableItem != null) {
            if (player.getAttributes().containsKey("Ignore charged item drop message")) {
                degrade(player, item, slotId, false, invisibleDelay, visibleDelay);
                return;
            }
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    item(item, "Dropping this item will completely degrade it. Are you sure you wish to do so?");
                    options("Drop the item?",
                            new DialogueOption("Yes.", () -> degrade(player, item, slotId, false, invisibleDelay, visibleDelay)),
                            new DialogueOption("Yes, don't ask this for any charged item again.", () -> degrade(player, item, slotId, true, invisibleDelay, visibleDelay)),
                            new DialogueOption("No."));
                }
            });
            return;
        }

        if (player.getTemporaryAttributes().remove("threshold warning bypass") == null) {
            val threshold = player.getNumericAttribute(Setting.TRESHOLD_DROP_WARNING_VALUE.toString()).intValue();
            if (player.getSettings().valueOf(Setting.LOOT_DROP_WARNING_NOTIFICATIONS) == 1 && threshold > 0 && item.getSellPrice() >= threshold) {
                player.getDialogueManager().start(new Dialogue(player) {
                    @Override
                    public void buildDialogue() {
                        item(item, "This item you are trying to drop is considered " + Colour.RS_RED.wrap("valuable") + ". " +
                                "Are you absolutely sure you want to drop it?");
                        options(item.getName() + ": Really drop it?", new DialogueOption("Drop it.", () -> {
                            player.getTemporaryAttributes().put("threshold warning bypass", true);
                            dropItem(player, option, slotId, invisibleDelay, visibleDelay);
                        }), new DialogueOption("No, don't drop it."));
                    }
                });
                return;
            }
        }
        player.getInventory().deleteItem(slotId, item);
        player.getInterfaceHandler().closeInterfaces();
        player.getPacketDispatcher().sendSoundEffect(OTHER_SOUND);
        if (area instanceof DropPlugin) {
            if (!((DropPlugin) area).dropOnGround(player, item)) {
                return;
            }
        }
        player.log(LogLevel.INFO, "Dropping item '" + item + "' at " + player.getLocation() + ".");
        if (player.inArea("Wilderness")) {
            val consumable = item.getDefinitions().containsOption("Eat") || item.getDefinitions().containsOption("Drink") || Consumable.consumables.containsKey(item.getId());
            World.spawnFloorItem(item, player, !consumable && item.isTradable() ? -1 : invisibleDelay, item.isTradable() ? visibleDelay : -1);
        } else {
            if(item.getId() != 13423 && item.getId() != 13424 && item.getId() != 13425) {
                World.spawnFloorItem(item, player, invisibleDelay, item.isTradable() ? visibleDelay : -1);
            } else {
                player.sendMessage("You can find more seeds on the table in Farmer Gricoller's house.");
            }
        }
    }

    private static final void degrade(final Player player, final Item item, final int slotId, final boolean ignoreMessages, final int invisibleDelay, final int visibleDelay) {
        if (player.getInventory().getItem(slotId) != item) {
            return;
        }
        if (ignoreMessages) {
            player.getAttributes().put("Ignore charged item drop message", true);
        }
        val area = player.getArea();
        if (area instanceof DropPlugin) {
            if (!((DropPlugin) area).dropOnGround(player, item)) {
                return;
            }
        }
        val degraded = DegradableItem.getCompletelyDegradedId(item.getId());
        player.getInventory().deleteItem(slotId, item);
        player.getInterfaceHandler().closeInterfaces();
        player.getPacketDispatcher().sendSoundEffect(OTHER_SOUND);
        if (degraded != -1) {
            if (player.inArea("Wilderness")) {
                val definitions = ItemDefinitions.getOrThrow(degraded);
                val consumable = definitions.containsOption("Eat") || definitions.containsOption("Drink") || Consumable.consumables.containsKey(degraded);
                if (consumable) {
                    World.spawnFloorItem(new Item(degraded, item.getAmount()), player, invisibleDelay + visibleDelay, -1);
                } else {
                    World.spawnFloorItem(new Item(degraded, item.getAmount()), player, item.isTradable() ? -1 : invisibleDelay, item.isTradable() ? visibleDelay : -1);
                }
            } else {
                World.spawnFloorItem(new Item(degraded, item.getAmount()), player, invisibleDelay, item.isTradable() ? visibleDelay : -1);
            }
        }
    }

}
