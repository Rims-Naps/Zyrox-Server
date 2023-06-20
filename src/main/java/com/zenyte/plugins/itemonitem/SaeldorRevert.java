package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * @author Cresinkel
 */
public class SaeldorRevert implements ItemOnItemAction {

    @AllArgsConstructor
    public static enum Crystals {

        ITHELL(23927, 32146, "ithell"),
        IORWERTH(23929, 32142, "iorwerth"),
        TRAHAEARN(23931, 32144, "trahaearn"),
        CADARN(23933, 32150, "cadarn"),
        CRWYS(23935, 32152, "crwys"),
        HEFIN(23939, 32148, "hefin"),
        AMLODD(23941, 32154, "amlodd"),
        CALEN(32176, 32156, "calen"),
        ZENYTE(32178, 32140, "zenyte");

        public static final Crystals[] VALUES = values();
        public static final Int2ObjectOpenHashMap<Crystals> MAPPED_VALUES = new Int2ObjectOpenHashMap<Crystals>(VALUES.length);
        static {
            for (val value : VALUES) {
                MAPPED_VALUES.put(value.crystalId, value);
                MAPPED_VALUES.put(value.recolouredSaeldorId, value);
            }
        }
        private final int crystalId;
        @Getter private final int recolouredSaeldorId;
        private final String crystalName;
    }

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val meilyr = from.getId() == 23937 ? from : to;
        val bow = meilyr == from ? to : from;
        val name = Crystals.MAPPED_VALUES.get(bow.getId());
        if (name == null) {
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                item(new Item(name.recolouredSaeldorId), Colour.RED + "WARNING!" + Colour.END
                        + " reverting the colour of your Blade of Saeldor to the original will result in you losing the Meilyr crystal. Are you sure?");
                options(TITLE, "Yes.", "No.").onOptionOne(() -> {
                    val inventory = player.getInventory();
                    inventory.deleteItem(fromSlot, from);
                    inventory.deleteItem(toSlot, to);
                    inventory.addItem(new Item(30763));
                    player.sendMessage("You revert your Blade of Saeldor with the power of the Crystal of Meilyr.");
                });
            }
        });
    }

    @Override
    public int[] getItems() {
        return null;
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] { new ItemPair(23937, Crystals.ITHELL.recolouredSaeldorId), new ItemPair(23937, Crystals.IORWERTH.recolouredSaeldorId), new ItemPair(23937, Crystals.TRAHAEARN.recolouredSaeldorId),
                new ItemPair(23937, Crystals.CADARN.recolouredSaeldorId), new ItemPair(23937, Crystals.CRWYS.recolouredSaeldorId), new ItemPair(23937, Crystals.HEFIN.recolouredSaeldorId),
                new ItemPair(23937, Crystals.AMLODD.recolouredSaeldorId), new ItemPair(23937, Crystals.CALEN.recolouredSaeldorId), new ItemPair(23937, Crystals.ZENYTE.recolouredSaeldorId) };
    }

}
