package com.zenyte.game.content.kebos.alchemicalhydra.plugins;

import com.zenyte.game.content.kebos.alchemicalhydra.instance.AlchemicalHydraInstance;
import com.zenyte.game.music.Music;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * @author Tommeh | 02/11/2019 | 16:36
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Slf4j
public class AlchemicalDoor implements ObjectAction {

    private static final WorldObject[] graphicalDoors = {
            new WorldObject(34555, 10, 1, new Location(1356, 10259, 0)),
            new WorldObject(34556, 10, 3, new Location(1356, 10258, 0)),
    };

    @Override
    public void handle(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (player.isMovementLocked(true)) {
            return;
        }
        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object), getRunnable(player, object, name, optionId, option), getDelay()));
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val area = player.getArea();
        if (!(area instanceof AlchemicalHydraInstance)) {
            return;
        }
        val instance = (AlchemicalHydraInstance) area;
        if (player.getX() > object.getX() && !(instance.getHydra().isDead() || instance.getHydra().isFinished())) {
            player.sendMessage("The door seems to be jammed!");
            return;
        }
        if (option.equals("Open")) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    options("Enter the laboratory? The door is notorious for getting jammed.", "Yes", "No")
                            .onOptionOne(() -> open(player));
                }
            });
        } else if (option.equals("Quick-open")) {
            open(player);
        }
    }

    private static void open(final Player player) {
        val instance = (AlchemicalHydraInstance) player.getArea();
        for (val door : instance.getDoors()) {
            World.removeObject(door);
        }
        for (val door : instance.getGraphicalDoors()) {
            World.spawnObject(door);
        }
        player.addWalkSteps(player.getX() >= instance.getX(1356) ? (player.getX() - 1) : (player.getX() + 1), player.getY(), 5,false);
        player.lock(2);
        WorldTasksManager.schedule(() -> {
            for (val door : instance.getDoors()) {
                World.spawnObject(door);
            }
            for (val door : instance.getGraphicalDoors()) {
                World.removeObject(door);
            }
            instance.setEntered(true);
            player.getBossTimer().startTracking("Alchemical Hydra");
            player.getMusic().unlock(Music.get("Alchemical Attack!"));
            player.putBooleanAttribute("has_taken_damage_from_hydra", false);
            player.putBooleanAttribute("NoPressureTask", true);
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{"Alchemical door"};
    }
}
