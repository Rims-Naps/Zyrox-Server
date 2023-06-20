package com.zenyte.plugins.itemonobject;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;

/**
 * @author Kris | 18/01/2019 18:59
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class VyreWellUnload implements ItemOnObjectAction {

    private static final int MAX_WELL_CHARGE = 1000000;

    @Override
    public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                item(item, "Unload your " + (item.getName().toLowerCase()) + "'s charges into the well?");
                options("Unload the charges?",
                        new DialogueOption("Yes.", () -> unload(player, item)),
                        new DialogueOption("No."));
            }
        });
    }

    private static final void unload(final Player player, final Item item) {
        val existingCharges = Math.min(player.getNumericAttribute("vyre well charges").intValue(), MAX_WELL_CHARGE);
        val charges = item.getCharges();
        if (existingCharges >= MAX_WELL_CHARGE) {
            player.getDialogueManager().start(new PlainChat(player, "The well can't hold anymore charges."));
            return;
        } else if (item.getCharges() < 100) {
            player.getDialogueManager().start(new ItemChat(player, item,
                    "Your weapon hasn't got enough charges to deposit back into the well."));
            return;
        }
        val fillAmount = Math.min(MAX_WELL_CHARGE - existingCharges, charges / 100);
        player.addAttribute("vyre well charges", existingCharges + fillAmount);
        VarCollection.VYRE_WELL.update(player);
        item.setCharges(item.getCharges() - (fillAmount * 100));
        val name = item.getName().toLowerCase();
        if (item.getCharges() <= 0) {
            if (item.getId() == 22325) {
                item.setId(22486);
            } else if (item.getId() == 22323) {
                item.setId(22481);
            }
        }
        player.getInventory().refreshAll();
        player.getDialogueManager().start(new ItemChat(player, item,
                "You unload " + Utils.format(fillAmount * 100) + " charges from the " + name + "."));
    }

    @Override
    public Object[] getItems() {
        return new Object[] {
                22323, 22325
        };
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                33085, 33091, 32985, 32654, 33081, 33082
        };
    }
}
