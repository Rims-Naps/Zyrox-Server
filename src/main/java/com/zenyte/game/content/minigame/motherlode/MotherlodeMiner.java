package com.zenyte.game.content.minigame.motherlode;

import com.zenyte.game.content.skills.mining.MiningDefinitions;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCObjectEvent;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.GlobalAreaManager;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;

import static com.zenyte.game.world.entity.npc.NpcId.*;
import static com.zenyte.game.content.skills.mining.actions.Mining.ROCKFALL_EXPLOSION;
import static com.zenyte.game.content.skills.mining.actions.Mining.ROCKFALL_PROJECTILE;

/**
 * @author Kris | 01/07/2019 15:26
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MotherlodeMiner extends NPC implements Spawnable {

    private static final Animation mining = new Animation(4021);
    private static final ForceTalk paydirt = new ForceTalk("Pay-dirt!");
    private static final int[] ids = new int[]{
            MINER_6567, MINER_6565, MINER_6645, MINER_5813, MINER_5606, MINER_6571, MINER_6570, MINER_6572, MINER_6569,
            MINER_6568, MINER_5814
    };
    private OreVein currentVein;
    private int delay;
    public MotherlodeMiner(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
    }

    @Override
    public boolean validate(final int id, final String name) {
        return ArrayUtils.contains(ids, id);
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    public void processNPC() {
        val moving = hasWalkSteps();
        if (!moving && (currentVein == null || !currentVein.exists())) {
            findVein().ifPresent(vein -> {
                setRouteEvent(new NPCObjectEvent(this, new ObjectStrategy(currentVein = vein)));
                delay = 2;
            });
            return;
        }

        if (!moving && !(currentVein == null || !currentVein.exists())) {
            if (getLocation().withinDistance(currentVein, 1)) {
                if (delay > 0) {
                    delay--;
                    return;
                }
                setFaceLocation(currentVein);
                setAnimation(mining);
                if (Utils.random(5) == 0) {
                    setForceTalk(paydirt);
                }
            } else {
                val rockfall = findRockfall();
                if (rockfall.isPresent()) {
                    val rock = rockfall.get();
                    if (rock.exists()) {
                        setAnimation(mining);
                        setFaceLocation(rock);
                        WorldTasksManager.schedule(() -> {
                            if (rock.exists()) {
                                World.removeObject(rock);
                                WorldTasksManager.schedule(() -> {
                                    val elements = new int[]{-1, 1};
                                    World.sendProjectile(rock.transform(elements[Utils.random(elements.length - 1)], elements[Utils.random(elements.length - 1)], 0), rock, ROCKFALL_PROJECTILE);
                                    World.sendProjectile(rock.transform(elements[Utils.random(elements.length - 1)], elements[Utils.random(elements.length - 1)], 0), rock, ROCKFALL_PROJECTILE);
                                    WorldTasksManager.schedule(() -> {
                                        CharacterLoop.forEach(rock, 1, Entity.class, entity -> {
                                            if (Utils.collides(rock.getX(), rock.getY(), 1, entity.getX(), entity.getY(), entity.getSize())) {
                                                if (entity instanceof Player) {
                                                    entity.applyHit(new Hit(Utils.random(1, 4), HitType.DEFAULT));
                                                }
                                                if (entity instanceof Player) {
                                                    entity.setRouteEvent(new ObjectEvent(((Player) entity), new ObjectStrategy(rock), null));
                                                } else {
                                                    entity.setRouteEvent(new NPCObjectEvent(((NPC) entity), new ObjectStrategy(rock)));
                                                }
                                            }
                                        });
                                        World.sendGraphics(ROCKFALL_EXPLOSION, rock);
                                        World.spawnObject(rock);
                                    });
                                }, MiningDefinitions.OreDefinitions.ROCKFALL.getTime());
                            }
                            findVein().ifPresent(vein -> {
                                setRouteEvent(new NPCObjectEvent(this, new ObjectStrategy(currentVein = vein)));
                                delay = 2;
                            });
                        });
                    }
                } else {
                    currentVein = null;
                }
            }
        } else {
            super.processNPC();
        }
    }

    private final Optional<WorldObject> findRockfall() {
        for (val rockfallEntry : GlobalAreaManager.getArea(MotherlodeArea.class).getRockfallMap().int2ObjectEntrySet()) {
            val obj = rockfallEntry.getValue();
            if (!obj.exists() || !obj.withinDistance(getLocation(), 1)) {
                continue;
            }
            return Optional.of(obj);
        }
        return Optional.empty();
    }

    private final Optional<OreVein> findVein() {
        OreVein vein = null;
        val area = GlobalAreaManager.getArea(MotherlodeArea.class);
        int distance = Integer.MAX_VALUE;
        val npcX = getX();
        val npcY = getY();
        val map = UpperMotherlodeArea.polygon.contains(getLocation()) ?
                  area.getHigherOreMap() :
                  area.getLowerOreMap();
        for (val entry : map.int2ObjectEntrySet()) {
            val hash = entry.getIntKey();
            val x = Location.getX(hash);
            val y = Location.getY(hash);
            val currentDistance = Utils.getDistance(npcX, npcY, x, y);
            if (currentDistance < distance) {
                val currentVein = entry.getValue();
                if (currentVein.exists()) {
                    vein = currentVein;
                    distance = currentDistance;
                }
            }
        }
        return Optional.ofNullable(vein);
    }
}
