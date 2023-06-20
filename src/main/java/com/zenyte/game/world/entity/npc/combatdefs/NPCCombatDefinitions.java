package com.zenyte.game.world.entity.npc.combatdefs;

import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Skills;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.EnumSet;

/**
 * @author Kris | 05/11/2018 01:24
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Setter
public class NPCCombatDefinitions {

    private int id;
    private int hitpoints = 1;
    private int attackSpeed;
    private int slayerLevel;
    private int attackDistance;
    private int aggressionDistance;
    private int maximumDistance;
    private Entity.EntityType targetType = Entity.EntityType.PLAYER;
    private EnumSet<ImmunityType> immunityTypes;
    private AggressionType aggressionType;
    private MonsterType monsterType;
    private EnumSet<WeaknessType> weaknesses;
    private ToxinDefinitions toxinDefinitions;
    private StatDefinitions statDefinitions;
    private AttackDefinitions attackDefinitions;
    private BlockDefinitions blockDefinitions;
    private SpawnDefinitions spawnDefinitions;

    public static NPCCombatDefinitions clone(final int id, final NPCCombatDefinitions other) {
        val defs = new NPCCombatDefinitions();
        if (other != null) {
            defs.id = other.id;
            defs.hitpoints = other.hitpoints;
            defs.attackSpeed = other.attackSpeed;
            defs.slayerLevel = other.slayerLevel;
            defs.attackDistance = other.attackDistance;
            defs.aggressionDistance = other.aggressionDistance;
            defs.maximumDistance = other.maximumDistance;
            defs.targetType = other.targetType;
            defs.immunityTypes = other.immunityTypes == null ? null : EnumSet.copyOf(other.immunityTypes);
            defs.aggressionType = other.aggressionType;
            defs.monsterType = other.monsterType;
            defs.weaknesses = other.weaknesses == null ? null : EnumSet.copyOf(other.weaknesses);
            defs.toxinDefinitions = other.toxinDefinitions == null ? null : other.toxinDefinitions.clone();
            defs.statDefinitions = other.statDefinitions == null ? new StatDefinitions() :
                    other.statDefinitions.clone();
            updateBaseDefinitions(defs, other);
        } else {
            defs.id = id;
            defs.statDefinitions = new StatDefinitions();
            updateBaseDefinitions(defs, null);
        }
        return defs;
    }

    public static final void updateBaseDefinitions(final NPCCombatDefinitions defs, final NPCCombatDefinitions other) {
        defs.attackDefinitions = AttackDefinitions.construct(other == null ? null : other.attackDefinitions);
        defs.blockDefinitions = BlockDefinitions.construct(other == null ? null : other.blockDefinitions);
        defs.spawnDefinitions = SpawnDefinitions.construct(other == null ? null : other.spawnDefinitions);
    }

    public boolean isMelee() {
        return attackDefinitions.getType().isMelee();
    }

    public boolean isMagic() {
        return attackDefinitions.getType().isMagic();
    }

    public boolean isRanged() {
        return attackDefinitions.getType().isRanged();
    }

    public boolean isAggressive() {
        return aggressionType == AggressionType.AGGRESSIVE || aggressionType == AggressionType.ALWAYS_AGGRESSIVE;
    }

    public boolean isAlwaysAggressive() {
        return aggressionType == AggressionType.ALWAYS_AGGRESSIVE;
    }

    public int getMaxHit() {
        return attackDefinitions.getMaxHit();
    }

    public boolean isVenomImmune() {
        return immunityTypes != null && immunityTypes.contains(ImmunityType.VENOM);
    }

    public boolean isPoisonImmune() {
        return immunityTypes != null && immunityTypes.contains(ImmunityType.POISON);
    }

    public AttackType getAttackType() {
        return attackDefinitions.getType();
    }

    public boolean isUndead() {
        return MonsterType.UNDEAD.equals(monsterType);
    }

    public boolean containsWeakness(final WeaknessType type) {
        return weaknesses != null && weaknesses.contains(type);
    }

    public void setAttackStyle(final String type) {
        switch(type) {
            case "Melee":
                attackDefinitions.setType(attackDefinitions.getDefaultMeleeType());
                return;
            case "Ranged":
                attackDefinitions.setType(AttackType.RANGED);
                return;
            case "Magic":
                attackDefinitions.setType(AttackType.MAGIC);
                return;
            case "Stab":
                attackDefinitions.setType(AttackType.STAB);
                return;
            case "Slash":
                attackDefinitions.setType(AttackType.SLASH);
                return;
            case "Crush":
                attackDefinitions.setType(AttackType.CRUSH);
                return;
            default:
                throw new RuntimeException("Unable to find type for value: " + type);
        }
    }

    public StatType getAttackStatType() {
        switch(attackDefinitions.getType()) {
            case STAB:
                return StatType.ATTACK_STAB;
            case SLASH:
                return StatType.ATTACK_SLASH;
            case CRUSH:
                return StatType.ATTACK_CRUSH;
            case RANGED:
                return StatType.ATTACK_RANGED;
            case MAGIC:
                return StatType.ATTACK_MAGIC;
            case MELEE:
                return StatType.ATTACK_CRUSH;
        }
        throw new IllegalArgumentException();
    }

    public void setAttackStyle(AttackType type) {
        if (type == AttackType.MELEE) {
            val defaultDefinitions = NPCCDLoader.get(getId());
            if (defaultDefinitions != null && defaultDefinitions.getAttackStyle().isMelee()) {
                type = defaultDefinitions.getAttackStyle();
            } else
                type = AttackType.CRUSH;
        }
        attackDefinitions.setType(type);
    }

    public Animation getAttackAnim() {
        return attackDefinitions.getAnimation();
    }

    public Animation getDeathAnim() {
        return spawnDefinitions.getDeathAnimation();
    }

    public Animation getBlockAnim() {
        return blockDefinitions.getAnimation();
    }

    public AttackType getAttackStyle() {
        return attackDefinitions.getType();
    }

    /**
     * Drains the specified skill for a given percentage and returns the amount it drained for.
     * @param skill the skill to drain.
     * @param percentage the percentage to drain the skill for.
     * @return the amount of levels that was successfully drained.
     */
    public final int drainSkill(final int skill, final double percentage, final int minimumDrain) {
        val index = getIndex(skill);
        if (index == -1)
            return 0;
        val stats = statDefinitions.getCombatStats();
        val currentLevel = stats[index];
        val amt = Math.max((int) (currentLevel * (percentage / 100F)), minimumDrain);
        val newLevel = currentLevel - amt;
        stats[index] = newLevel;
        return amt;
    }

    /**
     * Drains the specified skill for a given amount and returns the amount drained; cannot go below zero.
     * @param skill the skill to drain.
     * @param amount the amount to drain the skill for.
     * @return the amount of levels that was successfully drained.
     */
    public final int drainSkill(final int skill, final int amount) {
        val index = getIndex(skill);
        if (index == -1)
            return 0;
        val stats = statDefinitions.getCombatStats();
        val currentLevel = stats[index];
        val amt = (currentLevel - amount) < 0 ? currentLevel : amount;
        val newLevel = currentLevel - amt;
        stats[index] = newLevel;
        return amt;
    }

    public void resetStats() {
        val cachedDefs = NPCCDLoader.get(id);
        if (cachedDefs == null) {
            return;
        }
        statDefinitions = cachedDefs.statDefinitions.clone();
    }

    private static final int[] statArray = new int[] {
            Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC
    };

    private int getIndex(final int skill) {
        return ArrayUtils.indexOf(statArray, skill);
    }

}
