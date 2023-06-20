package com.zenyte.game.world.entity.npc.combatdefs;

import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.Toxins;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.npc.OldNPCCombatDefinitions;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.EnumSet;

/**
 * @author Kris | 05/11/2018 01:24
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Setter
public class MonsterCombatDefinition {

    private int id;
    private int hitpoints;
    private int attackSpeed;
    private int slayerLevel;
    private int attackDistance;
    private int aggressionDistance;
    private int maximumDistance;
    private Entity.EntityType targetType;
    private EnumSet<ImmunityType> immunityTypes;
    private AggressionType aggressionType;
    private MonsterType monsterType;
    private EnumSet<WeaknessType> weaknesses;
    private ToxinDefinitions toxinDefinitions;
    private StatDefinitions statDefinitions;
    private AttackDefinitions attackDefinitions;
    private BlockDefinitions blockDefinitions;
    private SpawnDefinitions spawnDefinitions;

    void append(final CombatDefRework.OSRSDef def) {
        this.hitpoints = def.getHitpoints();
        this.statDefinitions = def.getStatDefinitions();

        if (this.attackDefinitions == null) {
            this.attackDefinitions = new AttackDefinitions();
            attackDefinitions.animation = Animation.STOP;
            attackDefinitions.type = AttackType.CRUSH;
            attackDistance = 4;
        }

        this.attackDefinitions.maxHit = def.getMaxHit();
        this.attackSpeed = 10 - def.getAttackSpeed();
        this.attackDefinitions.type = def.getStyle();
        if (def.getStyle() == AttackType.CRUSH || def.getStyle() == AttackType.SLASH || def.getStyle() ==
                AttackType.STAB) {
            attackDefinitions.startSound = null;
            attackDistance = 4;
        }
        if (def.getImmunityTypes() != null) {
            if (this.immunityTypes == null) {
                this.immunityTypes = EnumSet.noneOf(ImmunityType.class);
            }
            this.immunityTypes.addAll(def.getImmunityTypes());
        }
        if (def.getWeaknessTypes() != null) {
            if (this.weaknesses == null) {
                this.weaknesses = EnumSet.noneOf(WeaknessType.class);
            }
            this.weaknesses.addAll(def.getWeaknessTypes());
        }
    }

    MonsterCombatDefinition construct(final OldNPCCombatDefinitions existingDefinitions) {
        this.id = existingDefinitions.getId();
        this.hitpoints = existingDefinitions.getHitpoints();
        this.attackSpeed = existingDefinitions.getAttackSpeed();//TODO Note: The speed is already converted.
        this.slayerLevel = existingDefinitions.getSlayerRequirement();
        this.aggressionDistance = existingDefinitions.getAttackStyle().equalsIgnoreCase("Melee") ? 4 : 8;
        this.maximumDistance = 16;
        targetType = Entity.EntityType.PLAYER;
        if (existingDefinitions.isPoisonImmune() || existingDefinitions.isVenomImmune()) {
            immunityTypes = EnumSet.noneOf(ImmunityType.class);
            if (existingDefinitions.isPoisonImmune()) {
                immunityTypes.add(ImmunityType.POISON);
            }
            if (existingDefinitions.isVenomImmune()) {
                immunityTypes.add(ImmunityType.VENOM);
            }
        }
        if (existingDefinitions.isAggressive()) {
            aggressionType = AggressionType.AGGRESSIVE;
        }
        if (existingDefinitions.isUndead()) {
            this.monsterType = MonsterType.UNDEAD;
        }
        if (existingDefinitions.getWeaknesses() != null && !existingDefinitions.getWeaknesses().isEmpty()) {
            weaknesses = EnumSet.noneOf(WeaknessType.class);
            for (val name : existingDefinitions.getWeaknesses()) {
                val lowerCase = name.toLowerCase();
                switch(lowerCase) {
                    case "air":
                        weaknesses.add(WeaknessType.AIR);
                        break;
                    case "water":
                        weaknesses.add(WeaknessType.WATER);
                        break;
                    case "earth":
                        weaknesses.add(WeaknessType.EARTH);
                        break;
                    case "fire":
                        weaknesses.add(WeaknessType.FIRE);
                        break;
                    case "arrows":
                        weaknesses.add(WeaknessType.ARROWS);
                        break;
                    case "brutal arrows":
                        weaknesses.add(WeaknessType.BRUTAL_ARROWS);
                        break;
                    case "thrown":
                        weaknesses.add(WeaknessType.THROWABLES);
                        break;
                    case "holy water":
                        weaknesses.add(WeaknessType.HOLY_WATER);
                        break;
                    case "keris":
                        weaknesses.add(WeaknessType.KERIS);
                        break;
                    case "brine sabre":
                        weaknesses.add(WeaknessType.BRINE_SABRE);
                        break;
                    case "arclight":
                    case "darklight":
                    case "silverlight":
                        weaknesses.add(WeaknessType.DEMONBANE_WEAPONS);
                        break;
                    case "dragon hunter crossbow":
                        weaknesses.add(WeaknessType.DRAGONBANE_WEAPONS);
                        break;
                }
            }
        }
        if (existingDefinitions.isPoisonous()) {
            toxinDefinitions = new ToxinDefinitions();
            toxinDefinitions.type = Toxins.ToxinType.POISON;
            toxinDefinitions.damage = 4;
        }
        this.statDefinitions = new StatDefinitions();
        statDefinitions.combatStats = existingDefinitions.getCombatStats();
        System.arraycopy(existingDefinitions.getCombatBonuses(), 0, statDefinitions.aggressiveStats, 0, 5);
        System.arraycopy(existingDefinitions.getCombatBonuses(), 5, statDefinitions.defensiveStats, 0, 5);
        statDefinitions.otherBonuses[0] = existingDefinitions.getCombatBonuses()[10];
        statDefinitions.otherBonuses[1] = existingDefinitions.getCombatBonuses()[11];

        this.blockDefinitions = new BlockDefinitions();
        blockDefinitions.animation = existingDefinitions.getBlockAnim();

        this.spawnDefinitions = new SpawnDefinitions();
        spawnDefinitions.respawnDelay = 25;
        spawnDefinitions.deathAnimation = existingDefinitions.getDeathAnim();

        this.attackDefinitions = new AttackDefinitions();
        attackDefinitions.animation = existingDefinitions.getAttackAnim();
        attackDefinitions.type = existingDefinitions.getAttackStyle().equalsIgnoreCase("Magic") ? AttackType.MAGIC :
                existingDefinitions.getAttackStyle().equalsIgnoreCase("Ranged") ? AttackType.RANGED : AttackType.CRUSH;
        attackDefinitions.maxHit = existingDefinitions.getMaxHit();
        attackDistance = attackDefinitions.type == AttackType.CRUSH ? 4 : 8;
        return this;
    }

    /**
     * The enum will be supporting more types in the future.
     */
    enum MonsterType {
        UNDEAD
    }

    enum WeaknessType {
        AIR,
        WATER,
        EARTH,
        FIRE,
        ARROWS,
        BRUTAL_ARROWS,
        THROWABLES,
        HOLY_WATER,
        KERIS,
        BRINE_SABRE,
        DEMONBANE_WEAPONS,
        DRAGONBANE_WEAPONS
    }

    enum ImmunityType {
        POISON,
        VENOM
    }

    enum AggressionType {
        AGGRESSIVE,
        ALWAYS_AGGRESSIVE
    }

    @Getter
    @Setter
    static class ToxinDefinitions {
        private Toxins.ToxinType type;
        private int damage;
    }

    @Getter
    @Setter
    static class StatDefinitions {

        int[] combatStats = new int[5];
        int[] aggressiveStats = new int[5];
        int[] defensiveStats = new int[5];
        int[] otherBonuses = new int[3];

        void set(final StatType type, final int value) {
            val arrays = new int[][] { combatStats, aggressiveStats, defensiveStats, otherBonuses };
            arrays[type.ordinal() / 5][type.index()] = value;
        }

    }

    enum StatType {
        ATTACK, STRENGTH, DEFENCE, MAGIC, RANGED,
        ATTACK_STAB, ATTACK_SLASH, ATTACK_CRUSH, ATTACK_MAGIC, ATTACK_RANGED,
        DEFENCE_STAB, DEFENCE_SLASH, DEFENCE_CRUSH, DEFENCE_MAGIC, DEFENCE_RANGED,
        MELEE_STRENGTH_BONUS, RANGED_STRENGTH_BONUS, MAGIC_STRENGTH_BONUS;

        /** Returns index in the respective stat definition array. Order of the enum must remain the same. */
        int index() {
            return ordinal() % 5;
        }
    }

    enum AttackType {
        STAB,
        SLASH,
        CRUSH,
        RANGED,
        MAGIC
    }

    @Getter
    @Setter
    static class AttackDefinitions {
        private AttackType type;
        private int maxHit;
        private Animation animation = Animation.STOP;
        private SoundEffect startSound;
        private SoundEffect impactSound;
        private Projectile projectile;
        private Graphics impactGraphics;
        private Graphics drawbackGraphics;
    }

    @Getter
    @Setter
    static class BlockDefinitions {
        private Animation animation = Animation.STOP;
        private SoundEffect sound;
    }

    @Getter
    @Setter
    static class SpawnDefinitions {
        private int respawnDelay;
        private Animation deathAnimation = Animation.STOP;
        private Animation spawnAnimation = Animation.STOP;
        private SoundEffect deathSound;
        private SoundEffect spawnSound;
    }

}