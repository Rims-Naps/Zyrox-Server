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
public class SaeldorRecoloring implements ItemOnItemAction {

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
        val saeldor = from.getId() == 30763 ? from : to;
        val crystal = saeldor == from ? to : from;
        val name = Crystals.MAPPED_VALUES.get(crystal.getId());
        if (name == null) {
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                item(new Item(name.recolouredSaeldorId), Colour.RED + "WARNING!" + Colour.END
                        + " changing the colour of your Blade of Saeldor will result in you losing the " + name.crystalName + " crystal. Are you sure?");
                options(TITLE, "Yes.", "No.").onOptionOne(() -> {
                    val inventory = player.getInventory();
                    inventory.deleteItem(fromSlot, from);
                    inventory.deleteItem(toSlot, to);
                    inventory.addItem(new Item(name.recolouredSaeldorId));
                    player.sendMessage("You infuse your Blade of Saeldor with the power of " + name.crystalName + ".");
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
        return new ItemPair[] { new ItemPair(30763, Crystals.ITHELL.crystalId), new ItemPair(30763, Crystals.IORWERTH.crystalId), new ItemPair(30763, Crystals.TRAHAEARN.crystalId),
                new ItemPair(30763, Crystals.CADARN.crystalId), new ItemPair(30763, Crystals.CRWYS.crystalId), new ItemPair(30763, Crystals.HEFIN.crystalId),
                new ItemPair(30763, Crystals.AMLODD.crystalId), new ItemPair(30763, Crystals.CALEN.crystalId), new ItemPair(30763, Crystals.ZENYTE.crystalId) };
    }

}
