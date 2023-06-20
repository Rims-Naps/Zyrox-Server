package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ChargeExtension;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.ContainerWrapper;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Kris | 18/01/2019 18:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SanguinestiStaff extends ItemPlugin implements ChargeExtension {
    @Override
    public void handle() {
        bind("Charge", (player, item, slotId) -> player.sendMessage("Your staff must be charged using a special " + "vyre well found at Ver Sinhaza."));
        bind("Uncharge", (player, item, slotId) -> {
            if (player.getDuel() != null && player.getDuel().inDuel()) {
                player.sendMessage("You can't do this during a duel.");
                return;
            }
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    plain("You will not be able to re-obtain your vials of blood and blood runes.");
                    options("Uncharge the staff?<br><col=ff0000>Alternatively you can unload the charges back into the well.", new DialogueOption("Yes.", () -> {
                        //Verify the existence of the item.
                        if (player.getInventory().getItem(slotId) == item) {
                            item.setCharges(0);
                            item.setId(22481);
                            player.getInventory().refresh(slotId);
                            player.sendMessage("You uncharge your staff.");
                        }
                    }), new DialogueOption("No."));
                }
            });
        });
    }

    @Override
    public int[] getItems() {
        return new int[] { 22481, 22323 };
    }

    @Override
    public void removeCharges(Player player, Item item, ContainerWrapper wrapper, int slotId, int amount) {
        item.setCharges(Math.max(0, item.getCharges() - amount));
        if (item.getCharges() <= 0) {
            item.setId(22481);
            player.getEquipment().refreshAll();
            player.getCombatDefinitions().refresh();
        }
    }
}
