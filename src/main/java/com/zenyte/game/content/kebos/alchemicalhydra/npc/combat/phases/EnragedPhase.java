package com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.phases;

import com.zenyte.game.content.kebos.alchemicalhydra.npc.AlchemicalHydra;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.HydraPhaseSequence;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.utils.ProjectileUtils;
import lombok.Setter;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tommeh | 02/11/2019 | 20:12
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class EnragedPhase implements HydraPhaseSequence {

    private static final Animation rangedAttackAnim = new Animation(8256);
    private static final Animation magicAttackAnim = new Animation(8255);

    private int attacks;
    private boolean initialSpecial;
    private final List<Location> poisonTiles = new ArrayList<>(5);

    @Setter
    private AttackType previousStyle;

    @Override
    public int autoAttack(final AlchemicalHydra hydra, final Player player) {
        val style = previousStyle.equals(AttackType.RANGED) ? AttackType.MAGIC : AttackType.RANGED;
        val maxHit = (int) (hydra.getMaxHit() * hydra.getAttackModifier());
        if (style.equals(AttackType.MAGIC)) {
            hydra.setAnimation(magicAttackAnim);
            hydra.delayHit(World.sendProjectile(hydra, player, magicAttackProj), player, new Hit(hydra, hydra.getRandomMaxHit(hydra, maxHit, AttackType.MAGIC, player), HitType.MAGIC));
        } else {
            hydra.setAnimation(rangedAttackAnim);
            hydra.delayHit(World.sendProjectile(hydra, player, rangedAttackProj), player, new Hit(hydra, hydra.getRandomMaxHit(hydra, maxHit, AttackType.RANGED, player), HitType.RANGED));
        }
        previousStyle = style;
        attacks++;
        hydra.incrementAutoAttacks();
        return hydra.getCombatDefinitions().getAttackSpeed();
    }

    @Override
    public boolean attack(final AlchemicalHydra hydra, final Player player) {
        if (attacks == 3 && !initialSpecial || attacks == 9) {
            val pools = Utils.random(4, 5);
            val base = new Location(player.getLocation());
            val tiles = new ArrayList<Location>(pools);
            Collections.shuffle(PoisonPhase.offsets);
            poisonTiles.clear();
            tiles.add(base);
            for (val offset : PoisonPhase.offsets) {
                val tile = base.transform(offset[0], offset[1], 0);
                if (tiles.contains(tile) || !World.isFloorFree(tile, 1) || ProjectileUtils.isProjectileClipped(null, null, player, tile, true)) {
                    continue;
                }
                tiles.add(tile);
                if (tiles.size() == pools) {
                    break;
                }
            }
            hydra.setAnimation(Utils.random(1) == 0 ? rangedAttackAnim : magicAttackAnim);
            val hydraCenter = hydra.getMiddleLocation();
            for (val tile : tiles) {
                World.sendProjectile(hydra, tile, PoisonPhase.poisonAttackProj);
                val direction = hydra.getRoundedDirection(Utils.getFaceDirection(tile.getX() - hydraCenter.getX(), tile.getY() - hydraCenter.getY()), 1024);
                val directionConstant = Utils.findMatching(Direction.values, dir -> dir.getNPCDirection() == direction);
                assert directionConstant != null;
                val graphics = PoisonPhase.directionMap.get(directionConstant);
                assert graphics != null;
                WorldTasksManager.schedule(new TickTask() {

                    @Override
                    public void run() {
                        if (ticks == 0) {
                            World.sendGraphics(PoisonPhase.poisonSplashGfx, tile);
                        } else if (ticks == 1) {
                            if (!player.getLocation().matches(tile) && player.getLocation().withinDistance(tile, 1)) {
                                applyPoison(hydra, player);
                                AlchemicalHydra.setHasBeenHitByPoison(true);
                            }
                            poisonTiles.add(tile);
                            World.sendGraphics(graphics, tile);
                        } else if (ticks == 17) {
                            poisonTiles.remove(tile);
                            stop();
                        }
                        ticks++;
                    }
                }, PoisonPhase.poisonAttackProj.getTime(hydra, tile), 0);
            }
            if (attacks == 3) {
                initialSpecial = true;
                attacks = 0;
            } else if (attacks == 9) {
                attacks = 0;
            }
            return true;
        }
        autoAttack(hydra, player);
        return false;
    }

    @Override
    public void process(final AlchemicalHydra hydra, final Player player) {
        for (val tile : poisonTiles) {
            if (player.getLocation().matches(tile)) {
                applyPoison(hydra, player);
                AlchemicalHydra.setHasBeenHitByPoison(true);
            }
        }
    }

    @Override
    public void handleIngoingHit(final AlchemicalHydra hydra, final Hit hit) {
        //no transformation on last phase so leave empty for now
    }

    @Override
    public int getHitpointsThreshold(AlchemicalHydra hydra) {
        return 0;
    }
}