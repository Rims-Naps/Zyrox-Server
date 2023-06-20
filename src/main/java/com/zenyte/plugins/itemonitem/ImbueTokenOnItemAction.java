package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.enums.ImbueableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import mgi.types.config.items.ItemDefinitions;
import com.zenyte.plugins.dialogue.ItemChat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

/**
 * @author Tommeh | 17-2-2019 | 23:41
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ImbueTokenOnItemAction implements ItemOnItemAction {

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val token = from.getId() == 11681 ? from : to;
        val item = from.getId() != 11681 ? from : to;
        if(!(from.getId() == 11681 || to.getId() == 11681)) {
            return;
        }
        val imbueable = ImbueableItem.get(from.getId()) == null ? ImbueableItem.get(to.getId()) : ImbueableItem.get(from.getId());
        if (imbueable == null) {
            player.getDialogueManager().start(new ItemChat(player, token, "You can't imbue this item. Try something else."));
            return;
        }
        val name = ItemDefinitions.get(imbueable.getNormal()).getName();
        player.getDialogueManager().start(new Dialogue(player) {

            @Override
            public void buildDialogue() {
                options("Are you sure you want to imbue your <col=00080>" + name + "</col>?", "Yes.", "No, not yet.")
                        .onOptionOne(() -> {
                            val charges = item.getCharges();
                            val imbued = new Item(imbueable.getImbued(), 1, charges);
                            setKey(5);
                            player.getInventory().deleteItem(token);
                            player.getInventory().deleteItem(imbueable.getNormal(), 1);
                            player.getInventory().addItem(imbued);
                        });
                doubleItem(5, new Item(imbueable.getImbued()), token, "You have successfully imbued your " + name + ".");
            }
        });
    }

    @Override
    public int[] getItems() {
        val list = new IntArrayList();
        for (val item : ImbueableItem.values) {
            list.add(item.getNormal());
        }
        list.add(11681);
        return list.toArray(new int[list.size()]);
    }
}
