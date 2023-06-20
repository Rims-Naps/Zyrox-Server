package com.zenyte.game.content.kebos.konar.plugins.objects;

import com.zenyte.game.content.kebos.konar.map.KaruulmSlayerDungeon;
import com.zenyte.game.content.kebos.konar.map.KaruulmSlayerDungeonLobby;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.GlobalAreaManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author Tommeh | 14/10/2019 | 19:48
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class RocksObstacle implements ObjectAction {

    private static final Animation climb = new Animation(839);

    @Getter
    @RequiredArgsConstructor
    private enum Rocks {
        NORTH(0, (player, object) -> {
            if (player.getY() < object.getY()) { //go north
                return player.getLocation().transform(0, 2, 0);
            } else { //go south
                return player.getLocation().transform(0, -2, 0);
            }
        }),
        WEST(3, (player, object) -> {
            if (player.getX() < object.getX()) { //go east
                return player.getLocation().transform(2, 0, 0);
            } else { //go west
                return player.getLocation().transform(-2, 0, 0);
            }
        }),
        EAST(1, (player, object) -> {
            if (player.getX() < object.getX()) { //go east
                return player.getLocation().transform(2, 0, 0);
            } else { //go west
                return player.getLocation().transform(-2, 0, 0);
            }
        });

        private final int rotation;
        private final BiFunction<Player, WorldObject, Location> handler;

        private static final Set<Rocks> all = EnumSet.allOf(Rocks.class);
        private static final Map<Integer, Rocks> rocks = new HashMap<>(all.size());

        static {
            for (val r : all) {
                rocks.put(r.rotation, r);
            }
        }

        public static Rocks get(final int rotation) {
            return rocks.get(rotation);
        }

    }

    @Override
    public int getDelay() {
        return 2;
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val rotation = object.getRotation();
        val rocks = Rocks.get(rotation);
        if (rocks == null) {
            return;
        }
        val handler = rocks.getHandler();
        val destination = handler.apply(player, object);
        val currentTile = new Location(player.getLocation());
        val direction = Utils.getFaceDirection(destination.getX() - currentTile.getX(), destination.getY() - currentTile.getY());
        if (object.getId() == 34548) { //rocks that lead to the alchemical hydra
            climb(player, destination, direction, true);
            return;
        }
        if (KaruulmSlayerDungeon.isProtectedAgainstHeat(player) || !GlobalAreaManager.getArea(KaruulmSlayerDungeonLobby.class).getPlayers().contains(player)) {
            climb(player, destination, direction, true);
        } else {
            player.getDialogueManager().start(new Dialogue(player) {

                @Override
                public void buildDialogue() {
                    plain("Warning: The heat of the ground beyond this point can burn you as<br><br>you walk upon it. It is recommended you wear appropriate boots for<br><br>this.");
                    options("Proceed regardless?", "Yes.", "No.")
                            .onOptionOne(() -> climb(player, destination, direction, false));
                }
            });
        }
    }


    private static void climb(final Player player, final Location destination, final int direction, final boolean protection) {
        player.lock(3);
        player.setAnimation(climb);
        player.autoForceMovement(destination, 0, 60, direction);
        WorldTasksManager.schedule(new WorldTask() {

            int ticks;
            @Override
            public void run() {
                if (ticks == 0) {
                    player.sendFilteredMessage("You climb over the rocks.");
                } else if (ticks == 2) {
                    if (!protection) {
                        player.sendFilteredMessage("The heat of the dungeon floor rapidly burns you.");
                    }
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 34544 };
    }
}
