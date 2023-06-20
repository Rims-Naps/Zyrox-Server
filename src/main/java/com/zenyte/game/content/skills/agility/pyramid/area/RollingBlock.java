package com.zenyte.game.content.skills.agility.pyramid.area;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.WorldObject;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class RollingBlock {

    @AllArgsConstructor
    private enum RollingBlockObject {
        FIRST_BLOCK(new ImmutableLocation(3354, 2841, 0)),
        SECOND_BLOCK(new ImmutableLocation(3374, 2835, 0)),
        THIRD_BLOCK(new ImmutableLocation(3368, 2849, 1)),
        FOURTH_BLOCK(new ImmutableLocation(3048, 4699, 1)),
        FIFTH_BLOCK(new ImmutableLocation(3044, 4699, 2));

        private final ImmutableLocation swTile;
        private static final RollingBlockObject[] values = values();
    }

    private static final Animation diveAnim = new Animation(1115);
    private static final Animation slipAnim = new Animation(3064);

    public static final void roll(@NotNull final Player player, final int x, final int y) {
        if (player.isLocked()) {
            return;
        }
        val playerZ = player.getPlane();
        for (val block : RollingBlockObject.values) {
            val tile = block.swTile;
            val blockZ = tile.getPlane();
            if (playerZ != blockZ + 1) {
                continue;
            }
            val blockX = tile.getX();
            val blockY = tile.getY();
            if (!Utils.collides(x, y, 1, blockX, blockY, 2)) {
                continue;
            }
            val stoneObject = World.getObjectWithType(tile, 10);
            player.lock();
            player.faceObject(stoneObject);
            val xDiff = player.getX() - x;
            val yDiff = player.getY() - y;
            val distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
            WorldTasksManager.schedule(() -> {
                player.getVarManager().sendBit(stoneObject.getDefinitions().getVarbitId(), true);
                player.sendSound(new SoundEffect(1396));
                val level = player.getSkills().getLevel(Skills.AGILITY);
                val baseRequirement = 30;
                val baseChance = 75;//Base chance % to not fail minimum level.
                val neverFailLevel = 70;
                val adjustmentPercentage = 100 - baseChance;
                val successPerLevel = (float) adjustmentPercentage / ((float) neverFailLevel - baseRequirement);
                val successChance = baseChance + Math.max(0, (level - baseRequirement)) * successPerLevel;
                val success = Utils.random(100) < successChance;

                val dirDiff = Math.abs(stoneObject.getFaceDirection().getDirection() -
                        Direction.getNPCDirection(player.getRoundedDirection()).getDirection());
                val forward = dirDiff <= 257 || dirDiff >= 1791;
                if (forward && success) {
                    success(player, stoneObject);
                } else {
                    failure(player, stoneObject, !forward);
                }
            }, distance < 2 ? 0 : 1);
            break;
        }
    }

    private static void success(final Player player, final WorldObject object) {
        player.sendSound(new SoundEffect(2455, 1, 25));
        player.setAnimation(diveAnim);
        val forceMovement = new ForceMovement(player.getLocation().transform(object.getFaceDirection(), 2), 30,
                object.getFaceDirection().getDirection());
        player.setForceMovement(forceMovement);

        WorldTasksManager.schedule(() -> {
            player.getVarManager().sendBit(object.getDefinitions().getVarbitId(), false);
            player.setLocation(forceMovement.getToFirstTile());
            player.getSkills().addXp(Skills.AGILITY, 12);
            player.unlock();
        }, 1);
    }

    private static void failure(final Player player, final WorldObject blockObject, final boolean reverse) {
        player.setAnimation(slipAnim);
        val forceMovement = createSlipMovement(player.getLocation(), blockObject);
        player.setForceMovement(forceMovement);

        WorldTasksManager.schedule(() -> {
            player.getVarManager().sendBit(blockObject.getDefinitions().getVarbitId(), false);
            player.setLocation(AgilityPyramidArea.getLowerTile(forceMovement.getToFirstTile().transform(0, 0, 1)));
            player.applyHit(new Hit(reverse ? 1 : 6, HitType.REGULAR));
            player.unlock();
        }, 1);
    }

    private static ForceMovement createSlipMovement(final Location location, final WorldObject object) {
        val dir = object.getRotation() == 1 ? Direction.WEST : object.getRotation() == 0 ? Direction.SOUTH :
                object.getRotation() == 2 ? Direction.NORTH : Direction.EAST;
        val destination = location.transform(dir, 2).transform(0, 0, -1);
        return new ForceMovement(destination, 60, dir.getDirection());
    }
}