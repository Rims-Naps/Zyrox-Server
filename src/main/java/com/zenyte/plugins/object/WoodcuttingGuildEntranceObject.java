package com.zenyte.plugins.object;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.DoubleDoor;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;

/**
 * @author Tommeh | 6 jun. 2018 | 17:10:52
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class WoodcuttingGuildEntranceObject implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val inside = player.inArea("Woodcutting Guild");
        if (!inside && player.getSkills().getLevel(Skills.WOODCUTTING) < 60) {
            player.getDialogueManager().start(new PlainChat(player, "You need a Woodcutting level of at least 60 to enter the guild."));
            return;
        }
        val gate = DoubleDoor.handleGraphicalDoubleDoor(player, object, null);
        val objX = object.getX();
        val objY = object.getY();
        player.lock();
        player.setRunSilent(true);
        player.addWalkSteps(objX, objY, -1, false);
        player.addWalkSteps(objX + (inside ? (objX == 1562 ? 0 : 1) : (objX == 1562 ? 1 : 0)), objY, -1, false);
        WorldTasksManager.schedule(() -> {
            player.unlock();
            player.setRunSilent(false);
            DoubleDoor.handleGraphicalDoubleDoor(player, object, gate);
        }, player.getWalkSteps().size());
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{28851, 28852};
    }

}
