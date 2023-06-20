package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 09/09/2019 14:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class VorkathsHeadOnCape implements PairedItemOnItemPlugin {
    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val head = from.getId() == ItemId.VORKATHS_HEAD_21907 ? from : to;
        val cape = from == head ? to : from;
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                val attrKey = "assembler_effect_on_" + cape.getName().toLowerCase().replace(" ", "_").replace("(t)", Strings.EMPTY);
                if (player.getNumericAttribute(attrKey).intValue() != 0) {
                    doubleItem(head, cape, "You've already infused the cape with Vorkath's head - the cape will collect arrows identically to the Ava's assembler.");
                    return;
                }
                doubleItem(head, cape, Colour.RED.wrap("WARNING: Infusing the cape with the head does NOT provide you an assembler max cape - it simply adds the assembler ammunition retrieval " +
                        "effect on the cape without changing the appearance!"));
                doubleItem(head, cape, "Infusing the cape will consume the Vorkath's head and will provide your cape with the assembler effect. Should you lose the cape on death, the effect is " +
                        "permanently lost on that cape, and another head must be used.");
                doubleItem(new Item(ItemId.ASSEMBLER_MAX_CAPE), new Item(ItemId.BANK_FILLER),
                        Colour.RED.wrap("If you wish to get an Assembler max cape, you must make an Ava's assembler first and use " +
                        "that on your max cape, instead of the head itself."));
                options("Imbue the cape?", new DialogueOption("Yes", () -> {
                    val inventory = player.getInventory();
                    if (inventory.getItem(fromSlot) != from || inventory.getItem(toSlot) != to) {
                        return;
                    }
                    inventory.deleteItem(head);
                    player.addAttribute(attrKey, 1);
                    player.sendMessage("Your " + cape.getName().toLowerCase().replace("(t)", Strings.EMPTY) + " now has the effects of an assembler.");
                }), new DialogueOption("No"));
            }
        });
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                ItemPair.of(ItemId.VORKATHS_HEAD_21907, ItemId.RANGING_CAPE),
                ItemPair.of(ItemId.VORKATHS_HEAD_21907, ItemId.RANGING_CAPET),
                ItemPair.of(ItemId.VORKATHS_HEAD_21907, ItemId.MAX_CAPE_13342)
        };
    }
}
