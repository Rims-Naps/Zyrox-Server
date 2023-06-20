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
public class BowOfFaerdhinenRecoloring implements ItemOnItemAction {

    @AllArgsConstructor
    public static enum Crystals {

        ITHELL(23927, 32170, "ithell"),
        IORWERTH(23929, 32172, "iorwerth"),
        TRAHAEARN(23931, 32158, "trahaearn"),
        CADARN(23933, 32160, "cadarn"),
        CRWYS(23935, 32168, "crwys"),
        HEFIN(23939, 32164, "hefin"),
        AMLODD(23941, 32166, "amlodd"),
        CALEN(32176, 32162, "calen"),
        ZENYTE(32178, 32174, "zenyte");

        public static final Crystals[] VALUES = values();
        public static final Int2ObjectOpenHashMap<Crystals> MAPPED_VALUES = new Int2ObjectOpenHashMap<Crystals>(VALUES.length);
        static {
            for (val value : VALUES) {
                MAPPED_VALUES.put(value.crystalId, value);
                MAPPED_VALUES.put(value.recolouredBofaId, value);
            }
        }
        private final int crystalId;
        @Getter private final int recolouredBofaId;
        private final String crystalName;
    }

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val bofa = from.getId() == 30596 ? from : to;
        val crystal = bofa == from ? to : from;
        val name = Crystals.MAPPED_VALUES.get(crystal.getId());
        if (name == null) {
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                item(new Item(name.recolouredBofaId), Colour.RED + "WARNING!" + Colour.END
                        + " changing the colour of your Bof of Faerdhinen will result in you losing the " + name.crystalName + " crystal. Are you sure?");
                options(TITLE, "Yes.", "No.").onOptionOne(() -> {
                    val inventory = player.getInventory();
                    inventory.deleteItem(fromSlot, from);
                    inventory.deleteItem(toSlot, to);
                    inventory.addItem(new Item(name.recolouredBofaId));
                    player.sendMessage("You infuse your Bow of Faerdhinen with the power of " + name.crystalName + ".");
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
        return new ItemPair[] { new ItemPair(30596, Crystals.ITHELL.crystalId), new ItemPair(30596, Crystals.IORWERTH.crystalId), new ItemPair(30596, Crystals.TRAHAEARN.crystalId),
                new ItemPair(30596, Crystals.CADARN.crystalId), new ItemPair(30596, Crystals.CRWYS.crystalId), new ItemPair(30596, Crystals.HEFIN.crystalId),
                new ItemPair(30596, Crystals.AMLODD.crystalId), new ItemPair(30596, Crystals.CALEN.crystalId), new ItemPair(30596, Crystals.ZENYTE.crystalId) };
    }

}
