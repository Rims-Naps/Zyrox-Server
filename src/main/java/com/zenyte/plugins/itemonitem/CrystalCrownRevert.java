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
public class CrystalCrownRevert implements ItemOnItemAction {

    @AllArgsConstructor
    public static enum Crystals {

        ITHELL(23927, 23913, "ithell"),
        IORWERTH(23929, 23915, "iorwerth"),
        TRAHAEARN(23931, 32180, "trahaearn"),
        CADARN(23933, 23921, "cadarn"),
        CRWYS(23935, 32182, "crwys"),
        HEFIN(23939, 23919, "hefin"),
        AMLODD(23941, 23917, "amlodd"),
        CALEN(32176, 23923, "calen"),
        ZENYTE(32178, 23925, "zenyte");

        public static final Crystals[] VALUES = values();
        public static final Int2ObjectOpenHashMap<Crystals> MAPPED_VALUES = new Int2ObjectOpenHashMap<Crystals>(VALUES.length);
        static {
            for (val value : VALUES) {
                MAPPED_VALUES.put(value.crystalId, value);
                MAPPED_VALUES.put(value.recolouredCrownId, value);
            }
        }
        private final int crystalId;
        @Getter private final int recolouredCrownId;
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
                item(new Item(name.recolouredCrownId), Colour.RED + "WARNING!" + Colour.END
                        + " reverting the colour of your Crystal crown to the original will result in you losing the Meilyr crystal. Are you sure?");
                options(TITLE, "Yes.", "No.").onOptionOne(() -> {
                    val inventory = player.getInventory();
                    inventory.deleteItem(fromSlot, from);
                    inventory.deleteItem(toSlot, to);
                    inventory.addItem(new Item(23911));
                    player.sendMessage("You revert your Crystal crown with the power of the Crystal of Meilyr.");
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
        return new ItemPair[] { new ItemPair(23937, Crystals.ITHELL.recolouredCrownId), new ItemPair(23937, Crystals.IORWERTH.recolouredCrownId), new ItemPair(23937, Crystals.TRAHAEARN.recolouredCrownId),
                new ItemPair(23937, Crystals.CADARN.recolouredCrownId), new ItemPair(23937, Crystals.CRWYS.recolouredCrownId), new ItemPair(23937, Crystals.HEFIN.recolouredCrownId),
                new ItemPair(23937, Crystals.AMLODD.recolouredCrownId), new ItemPair(23937, Crystals.CALEN.recolouredCrownId), new ItemPair(23937, Crystals.ZENYTE.recolouredCrownId) };
    }

}
