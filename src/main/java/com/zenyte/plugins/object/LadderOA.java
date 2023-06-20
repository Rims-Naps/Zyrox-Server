package com.zenyte.plugins.object;

import com.google.common.eventbus.Subscribe;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.Ladder;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.OptionDialogue;
import com.zenyte.plugins.events.ServerLaunchEvent;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Kris | 19. veebr 2018 : 17:21.50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class LadderOA implements ObjectAction {

	public static final Animation CLIMB_UP = new Animation(828);
	public static final Animation CLIMB_DOWN = new Animation(827);

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId,
			final String option) {
		final Ladder ladder = Ladder.LADDER_MAP.get(object.getPositionHash());
        switch (option.toLowerCase()) {
            case "climb":
                if (ladder == null) {
                    player.getDialogueManager().start(new OptionDialogue(player, "Which way do you want to go?",
                            new String[]{"Climb up", "Climb down", "Cancel."},
                            new Runnable[]{
                                    () -> climb(player, object, new Location(player.getX(), player.getY(), object.getPlane() + 1), CLIMB_UP, null),
                                    () -> climb(player, object, new Location(player.getX(), player.getY(), object.getPlane() - 1), CLIMB_DOWN, null),
                                    null
                            }));
                } else {
                    climb(player, object, ladder.getTo(), ladder.getTo().getPlane() < player.getPlane() ?
                                                          CLIMB_DOWN :
                                                          CLIMB_UP, ladder);
                }
                break;
            case "climb up":
            case "climb-up":
                if (ladder == null) {
                    climb(player, object, new Location(player.getX(), player.getY(), (player.getPlane() + 1) & 0x3), CLIMB_UP, null);
                } else {
                    climb(player, object, ladder.getTo(), CLIMB_UP, ladder);
                }
                break;
            case "climb down":
            case "climb-down":
                if (ladder == null) {
                    climb(player, object, new Location(player.getX(), player.getY(), (player.getPlane() - 1) & 0x3), CLIMB_DOWN, null);
                } else {
                    climb(player, object, ladder.getTo(), CLIMB_DOWN, ladder);
                }
                break;
        }
	}

	private void climb(final Player player, final WorldObject originalObject, final Location tile, final Animation anim, Ladder ladder) {
		if (tile.withinDistance(originalObject, 10)) {
			final val object = World.getObjectWithType(new Location(originalObject.getX(), originalObject.getY(), tile.getPlane()), 10);
			if (object == null) {
				player.sendMessage("Looks like this ladder doesn't lead anywhere.");
				return;
			}
		}
		if (ladder == null) {
		    val obj = World.getObjectWithType(new Location(originalObject.getX(), originalObject.getY(), tile.getPlane()), originalObject.getType());
		    if (obj == null || player.getPlane() == 3 && tile.getPlane() == 0 || player.getPlane() == 0 && tile.getPlane() == 3) {
		        enlistInvalidLadder(originalObject);
		        player.sendMessage("Undefined ladder detected; Submitting automatic bug report.");
		        return;
            }
        }
		player.lock(2);
		player.setAnimation(anim);
		WorldTasksManager.schedule(() -> player.setLocation(tile));
	}

	private static final ObjectSet<String> invalidLadders = ObjectSets.synchronize(new ObjectOpenHashSet<>());

	@Subscribe
    public static final void onServerLaunch(final ServerLaunchEvent event) {
        try {
            invalidLadders.addAll(Arrays.asList(World.getGson().fromJson(new BufferedReader(new FileReader("data/invalid ladders.json")), String[].class)));
        } catch (FileNotFoundException e) {
            log.error(Strings.EMPTY, e);
        }
    }

	private static final void enlistInvalidLadder(@NotNull final WorldObject ladder) {
	    if (!ladder.isMapObject()) {
	        return;
        }
	    if (!invalidLadders.add(ladder.toString())) {
	        return;
        }
        CoresManager.slowExecutor.submit(() -> {
           val json = World.getGson().toJson(invalidLadders);
           try (val writer = new PrintWriter(new java.io.File("data/invalid ladders.json"))) {
               writer.write(json);
           } catch (Exception e) {
               log.error(Strings.EMPTY, e);
           }
        });
    }

	@Override
	public int getDelay() {
		return 1;
	}

	@Override
	public Object[] getObjects() {
		final Set<Object> list = new ObjectOpenHashSet<>(Ladder.VALUES.length);
		list.add("ladder");
		list.add("Bamboo ladder");
		for (final Ladder ladder : Ladder.VALUES) {
			list.add(ladder.getLadder().getId());
		}
		return list.toArray();
	}

}
