package com.zenyte.plugins.dialogue;

import com.zenyte.game.content.consumables.Consumable;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.LootingBag;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Tommeh | 24 apr. 2018 | 17:23:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DestroyItemDialogue extends Dialogue {

    private final Item item;
    private final int slot;

    public DestroyItemDialogue(final Player player, final Item item, final int slot) {
        super(player);
        this.item = item;
        this.slot = slot;
    }

    @Override
    public void buildDialogue() {
        destroyItem(item, getMessage(item)).onYes(() -> {
            val inventory = player.getInventory();
            val inSlot = inventory.getItem(slot);
            player.sendSound(2381);
            if (inSlot == item) {
                inventory.deleteItem(slot, item);
                player.log(LogLevel.INFO, "Destroying item '" + item + "'.");
            } else {
                for (val entry : inventory.getContainer().getItems().int2ObjectEntrySet()) {
                    if (entry.getValue() == item) {
                        inventory.deleteItem(entry.getIntKey(), item);
                        player.log(LogLevel.INFO, "Destroying item '" + item + "'.");
                        break;
                    }
                }
            }
            handle();
        });
    }

    private String getMessage(final Item item) {
        val id = item.getId();
        if (id == 22711) {
            return "You can get another Collection log from the Collector in the Varrock Museum.";
        }
        if (id == ItemId.SKELETON_BOOTS || id == ItemId.SKELETON_GLOVES || id == ItemId.SKELETON_LEGGINGS || id == ItemId.SKELETON_MASK || id == ItemId.SKELETON_SHIRT) {
            return "You can reclaim this item from Diango in Draynor Village.";
        }
        return "Destroying is a permanent process. You will be able to reacquire the item where you obtained it in the first place.";
    }

    private void handle() {
        if (item.getId() == 13639) { //seed box
            player.getSeedBox().clear();
        } else if (item.getId() == 13226) { //herb sack
            player.getHerbSack().clear();
        } else if (item.getId() == 12020) { //gem bag
            player.getGemBag().clear();
        } else if (item.getId() == 12791) { //rune pouch
            player.getRunePouch().clear();
        } else if (LootingBag.isBag(item.getId())) { //looting bag
            val inWilderness = player.inArea("Wilderness");
            if (inWilderness) {
                for (val entry : player.getLootingBag().getContainer().getItems().int2ObjectEntrySet()) {
                    val item = entry.getValue();
                    val consumable = item.getDefinitions().containsOption("Eat") || item.getDefinitions().containsOption("Drink") || Consumable.consumables.containsKey(item.getId());
                    World.spawnFloorItem(item, player, !consumable && item.isTradable() ? -1 : 300, item.isTradable() ? 500 : -1);
                }
            }
            player.getLootingBag().clear();
            player.getLootingBag().setOpen(false);
        } else if (item.getId() == 21143) {
            player.addAttribute("dodgy necklace uses", 0);
        } else if (item.getId() == 21160) {
            player.addAttribute("amulet of bounty uses", 0);
        } else if (item.getId() == 21163) {
            player.addAttribute("amulet of chemistry uses", 0);
        }
    }

}
