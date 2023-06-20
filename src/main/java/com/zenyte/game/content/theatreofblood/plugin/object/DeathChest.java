package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class DeathChest implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if(option.equals("Claim")) {
            if(player.getRetrievalService().getContainer().isEmpty() || player.getRetrievalService().getType() != ItemRetrievalService.RetrievalServiceType.THEATRE_OF_BLOOD) {
                player.getDialogueManager().start(new Dialogue(player) {
                    @Override
                    public void buildDialogue() {
                        plain("The chest seems to be empty. If it did have any of your items, but <br> you died before collecting them, they'll now be lost.");
                    }
                });
            } else {
                GameInterface.ITEM_RETRIEVAL_SERVICE.open(player);
            }
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{32656};
    }
}
