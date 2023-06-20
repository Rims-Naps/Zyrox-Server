package com.zenyte.game.world.entity.npc.combat.impl.fremennikisles;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combatdefs.AggressionType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

import static com.zenyte.game.world.entity.npc.NpcId.*;

/**
 * @author Kris | 09/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class IceTroll extends NPC implements Spawnable {

    private static final IntArrayList list = new IntArrayList(new int[]
        {
            ICE_TROLL, ICE_TROLL_649, ICE_TROLL_650, ICE_TROLL_651, ICE_TROLL_652, ICE_TROLL_653, ICE_TROLL_654,
            ICE_TROLL_698, ICE_TROLL_699, ICE_TROLL_700, ICE_TROLL_701, ICE_TROLL_702, ICE_TROLL_703, ICE_TROLL_704,
            ICE_TROLL_705, ICE_TROLL_RUNT, ICE_TROLL_MALE, ICE_TROLL_FEMALE, ICE_TROLL_GRUNT, ICE_TROLL_KING,
            ICE_TROLL_RUNT_5823, ICE_TROLL_MALE_5824, ICE_TROLL_FEMALE_5825, ICE_TROLL_GRUNT_5826, ICE_TROLL_RUNT_5828,
            ICE_TROLL_MALE_5829, ICE_TROLL_FEMALE_5830, ICE_TROLL_GRUNT_5831, ICE_TROLL_KING_6356
        });

    public IceTroll(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
        setTargetType(EntityType.NPC);
    }

    @Override
    public NPC spawn() {
        super.spawn();
        this.combatDefinitions.setAggressionType(AggressionType.ALWAYS_AGGRESSIVE);
        return this;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity target) {
        return target instanceof HonourGuard;
    }

    @Override
    public boolean validate(int id, String name) {
        return list.contains(id);
    }

    @Override
    public boolean checkAggressivity() {
        if (!forceAggressive) {
            if (!combatDefinitions.isAggressive()) {
                return false;
            }
        }
        getPossibleTargets(targetType);
        if (!possibleTargets.isEmpty()) {
            this.resetWalkSteps();
            val target = possibleTargets.get(Utils.random(possibleTargets.size() - 1));
            setTarget(target);
        }
        return true;
    }

    @Override
    protected boolean isPotentialTarget(final Entity entity) {
        val entityX = entity.getX();
        val entityY = entity.getY();
        val entitySize = entity.getSize();

        val x = getX();
        val y = getY();
        val size = getSize();

        val currentTime = Utils.currentTimeMillis();
        return !entity.isMaximumTolerance() && (entity.isMultiArea() || entity.getAttackedBy() == this || (entity.getAttackedByDelay() <= currentTime && entity.getFindTargetDelay() <= currentTime))
                && (!isProjectileClipped(entity, combatDefinitions.isMelee()) || Utils.collides(x, y, size, entityX, entityY, entitySize))
                && (forceAggressive || combatDefinitions.isAlwaysAggressive() || combatDefinitions.isAggressive()) && isAcceptableTarget(entity);
    }
}