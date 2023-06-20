package com.zenyte.game.content.magicstorageunit;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 15/09/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MagicStorageUnitObject implements ObjectAction, ItemOnObjectAction {

    private static final int MAGIC_STORAGE_UNIT_OBJECT_ID = 35024;

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val unit = player.getMagicStorageUnit();
        //If player hasn't paid to unlock it yet
        if (unit.getUnlockPayment() == 0) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    val cost = player.getGameMode() == GameMode.ULTIMATE_IRON_MAN ? MagicStorageUnit.STORAGE_UNIT_UIM_UNLOCK_COST : MagicStorageUnit.STORAGE_UNIT_NORMAL_UNLOCK_COST;
                    plain("Unlocking the magic storage unit lets you place hundreds of various items and item sets inside it. You will be able to add and remove your items from the storage unit for" +
                            " free. The cost to unlock the storage unit is " + Utils.format(cost) + " coins.");
                    plain("Once Construction skill is released, you will no longer be able to deposit items in this storage unit. You will still be able to withdraw whatever you had put in them " +
                            "though. Upon withdrawing the last remaining item from the storage unit, you will be refunded the original cost to unlock the storage unit.");
                    options("Unlock the magic storage unit?", new DialogueOption("Yes.", () -> {
                        val inventory = player.getInventory();
                        if (!inventory.containsItem(ItemId.COINS_995, cost)) {
                            setKey(10);
                            return;
                        }
                        inventory.deleteItem(ItemId.COINS_995, cost);
                        unit.setUnlockPayment(cost);
                        unit.refeshVarbit(player);
                    }), new DialogueOption("No."));

                    plain(10, "You do not have enough coins to unlock the storage unit. You need at least " + Utils.format(cost) + " coins.");
                }
            });
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("What would you like to search?",
                        new DialogueOption("Armour case", () -> MagicStorageInterface.view(player, StorageUnitType.ARMOUR_CASE)),
                        new DialogueOption("Cape rack", () -> MagicStorageInterface.view(player, StorageUnitType.CAPE_RACK)),
                        new DialogueOption("Toy box", () -> MagicStorageInterface.view(player, StorageUnitType.TOY_BOX)),
                        new DialogueOption("Treasure chest", () -> MagicStorageInterface.view(player, StorageUnitType.TREASURE_CHEST)),
                        new DialogueOption("More...", key(5)));
                options(5, "What would you like to search?",
                        new DialogueOption("Magic wardrobe", () -> MagicStorageInterface.view(player, StorageUnitType.MAGIC_WARDROBE)),
                        new DialogueOption("Fancy dress box", () -> MagicStorageInterface.view(player, StorageUnitType.FANCY_DRESS_BOX)),
                        new DialogueOption("Pets", () -> MagicStorageInterface.view(player, StorageUnitType.PETS)),
                        new DialogueOption("Monster Pets", () -> MagicStorageInterface.view(player, StorageUnitType.MONSTERPETS)),
                        new DialogueOption("back...", key(1)));

            }
        });
    }

    @Override
    public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object) {
        val unit = player.getMagicStorageUnit();
        //If player hasn't paid to unlock it yet
        if (unit.getUnlockPayment() == 0) {
            player.sendMessage("You need to unlock the magic storage unit first.");
            return;
        }
        player.getMagicStorageUnit().store(player, item.getId());
    }

    @Override
    public Object[] getItems() {
        return StorageUnitCollection.getSingleton().getAddableItemSet().toArray(new Integer[0]);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                MAGIC_STORAGE_UNIT_OBJECT_ID
        };
    }
}
