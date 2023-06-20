package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Tommeh | 6/4/2020 | 6:06 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class FormidablePassageObject implements ObjectAction {

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (option.equals("Enter")) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    options("Are you ready to proceed?", "Yes.", "No - Stay here.")
                            .onOptionOne(() -> enter(player));
                }
            });
        } else {
            enter(player);
        }
    }

    private static void enter(final Player player) {
        val party = VerSinhazaArea.getParty(player);
        if (party == null) {
            return;
        }
        val raid = party.getRaid();
        if (raid == null) {
            return;
        }
        if (!(player.getArea() instanceof TheatreArea)) {
            return;
        }
        val room = (TheatreArea) player.getArea();
        room.handlePassage(player);
    }

    @Override
    public void handle(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object, object.getId() == ObjectId.TREASURE_ROOM ? 1 : 0), getRunnable(player, object, name, optionId,
                option), getDelay()));
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {ObjectId.FORMIDABLE_PASSAGE, ObjectId.DOOR_32751, ObjectId.TREASURE_ROOM};
    }
}
