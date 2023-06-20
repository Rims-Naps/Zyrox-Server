package com.zenyte.game.content.tournament.plugins;

import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.pathfinding.RouteStrategy;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectHandler;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Tommeh | 31/05/2019 | 20:17
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TournamentBarrier implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (player.getY() < object.getY()) {
            /*if (!player.getInventory().getContainer().isEmpty() || !player.getEquipment().getContainer().isEmpty()) {
                player.getDialogueManager().start(new NPCChat(player, 10011, "You cannot enter the tournament lobby while you're still carrying items!"));
                return;
            }*/
            val obj = player.getArea();
            if (!(obj instanceof TournamentLobby)) {
                return;
            }
            val area = (TournamentLobby) obj;
            if (area.getTournament().isFinished()) {
                player.sendMessage("The tournament has already ended. Come back another time.");
                return;
            }
            if (area.getTournament().getRound() > 0) {
                player.sendMessage("You will not be allowed to re-enter the tournament since it has already begun.");
                return;
            }
            player.lock(3);
            player.setRunSilent(3);
            player.addWalkSteps(player.getX(), object.getY() + 1, 2, false);
        } else {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    options("Are you sure you'd like to leave the tournament?", "Yes.", "No.")
                            .onOptionOne(() -> {
                                player.lock(2);
                                player.setRunSilent(2);
                                player.addWalkSteps(player.getX(), object.getY() - 1, 2, false);
                            });
                }
            });

        }
    }

    @Override
    public void handle(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object, 0, RouteStrategy.BLOCK_FLAG_NORTH | RouteStrategy.BLOCK_FLAG_SOUTH),
                getRunnable(player, object, name,
                        optionId, option), getDelay()));
    }

    @Override
    public Runnable getRunnable(final Player player, final WorldObject object, final String name, final int optionId,
                                final String option) {
        return () -> {
            val existingObject = World.getObjectWithId(object, object.getId());
            if (existingObject == null || player.getPlane() != object.getPlane()) {
                return;
            }
            player.stopAll();
            player.setFaceLocation(player.getLocation().transform(0, 2, 0));
            if (!ObjectHandler.handleOptionClick(player, optionId, object)) {
                return;
            }
            handleObjectAction(player, object, name, optionId, option);
        };
    }

    @Override
    public int getDelay() {
        return 1;
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 35005 };
    }
}
