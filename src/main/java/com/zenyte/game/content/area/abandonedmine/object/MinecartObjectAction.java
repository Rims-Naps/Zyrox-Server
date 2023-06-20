package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class MinecartObjectAction implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                plain("The minecart is empty, apart from a few spiders. Yuk!");
            }
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {4974};
    }
}
