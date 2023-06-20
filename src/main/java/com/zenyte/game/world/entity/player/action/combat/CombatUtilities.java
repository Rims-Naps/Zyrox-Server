package com.zenyte.game.world.entity.player.action.combat;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combat.impl.CombatDummy;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.npc.combatdefs.StatType;
import com.zenyte.game.world.entity.npc.impl.Shade;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CombatUtilities {

    public static final Animation CAST = new Animation(711);
    public static final Animation SURGE_CAST = new Animation(7855);
    public static final Animation DEBUFF_CAST = new Animation(710);
    public static final Animation ANCIENT_SINGLE_CAST = new Animation(1978);
    public static final Animation ANCIENT_MULTI_CAST = new Animation(1979);
    public static final Animation CHINCHOMPA_THROW_ANIM = new Animation(2779);

    public static final Graphics CHINCHOMPA_GFX = new Graphics(157, 0, 50);
    public static final Graphics TORAGS_SET_GFX = new Graphics(399);
    public static final Graphics GUTHANS_SET_GFX = new Graphics(398);
    public static final Graphics VERACS_SET_GFX = new Graphics(-1);
    public static final Graphics KARILS_SET_GFX = new Graphics(401, 0, 96);
    public static final Graphics AHRIMS_SET_GFX = new Graphics(400, 0, 96);
    private static final String[] SALVE_NPCS = new String[] { "aberrant spectre", "abhorrent spectre", "deviant spectre",
            "repugnant spectre", "ankou", "banshee", "screaming banshee", "twisted banshee", "crawling hand", "crushing hand", "ghast",
            "ghost", "greater skeleton hellhound", "mummy", "monkey zombie", "revenant imp", "revenant goblin", "revenant pyrefiend",
            "revenant hobgoblin", "revenant cyclops", "revenant hellhound", "revenant demon", "revenant ork", "revenant dark beast",
            "revenant knight", "revenant dragon", "shade", "loar shade", "phrin shade", "riyl shade", "asyn shade", "fiyr shade",
            "skeleton", "skeleton brute", "skeleton hellhound", "vet'ion", "skeleton mage", "skeleton thug", "skeleton warlord", "skogre",
            "summoned zombie", "tarn razorlor", "the draugen", "tortured soul", "undead chicken", "undead cow", "undead one", "vorkath",
            "zogre", "zombie", "zombie rat", "<col=00ffff>undead combat dummy</col>", "skeletal mystic", "pestilent bloat" };
    private static final String[] REVENANT_NPCS = {"revenant imp", "revenant goblin", "revenant pyrefiend",
            "revenant hobgoblin", "revenant cyclops", "revenant hellhound", "revenant demon", "revenant ork", "revenant dark beast",
            "revenant knight", "revenant dragon"};
    private static final String[] DEMON_NPCS = {
            "imp", "lesser demon", "greater demon", "black demon", "ice demon", "abyssal demon", "greater abyssal demon", "tortured gorilla",
            "demonic gorilla", "nechryael", "abyssal sire", "k'ril tsutsaroth", "balfrug kreeyath", "tstanon karlak", "zakl'n gritch", "skotizo",
            "<col=00ffff>undead combat dummy</col>", "cerberus"
    };
    public static final List<String> SALVE_AFFECTED_NPCS = Arrays.asList(SALVE_NPCS);

    private static final String[] BARROWS_NAMES = { "Verac's", "Ahrim's", "Karil's", "Dharok's", "Guthan's", "Torag's" };

    public static final boolean isFireNPC(final NPC target) {
        final String name = target.getDefinitions().getName().toLowerCase();
        return name.contains("dragon") && !name.contains("baby") || name.equals("fire giant") || name.equals("pyrefiend") || name.equals("fire elemental") || name.contains("tzhaar-");
    }

    public static final boolean isCombatDummy(@NotNull final Entity target) {
        return target instanceof CombatDummy;
    }

    public static final boolean isUndeadCombatDummy(@NotNull final Entity target) {
        return target instanceof CombatDummy && ((CombatDummy) target).getId() == 10020;
    }

    public static final boolean isDraconic(@NotNull final Entity  entity) {
        if (!(entity instanceof NPC)) {
            return false;
        }
        val npc = (NPC) entity;
        val name = npc.getDefinitions().getName().toLowerCase();
        return (!name.contains("elvarg") && !name.contains("revenant")) && (name.contains("dragon") || name.contains("wyvern") || name.contains("wyrm") || name.contains("drake") || name.contains("hydra") || name.contains("great olm") || name.contains("left claw") || name.contains("right claw") || name.contains("vorkath"));
    }

    public static final boolean hasFullMeleeVoid(final Player player, final boolean eliteOnly) {
        final int helm = player.getEquipment().getId(EquipmentSlot.HELMET.getSlot());
        final int body = player.getEquipment().getId(EquipmentSlot.PLATE.getSlot());
        final int legs = player.getEquipment().getId(EquipmentSlot.LEGS.getSlot());
        final int gloves = player.getEquipment().getId(EquipmentSlot.HANDS.getSlot());
        return helm == 11665 && gloves == 8842 && (body == 8839 && !eliteOnly || body == 13072)
                && (legs == 8840 && !eliteOnly || legs == 13073);
    }

    public static final boolean hasFullBarrowsSet(final Player player, final String name) {
        val helm = player.getHelmet();
        val chest = player.getChest();
        val legs = player.getLegs();
        val weapon = player.getWeapon();
        if (helm == null || chest == null || legs == null || weapon == null) {
            return false;
        }
        val helmName = helm.getName();
        val chestName = chest.getName();
        val legsName = legs.getName();
        val weaponName = weapon.getName();
        if (helmName.endsWith(" 0") || chestName.endsWith(" 0") || legsName.endsWith(" 0") || weaponName.endsWith(" 0")) {
            return false;
        }
        return helmName.startsWith(name) && chestName.startsWith(name) && legsName.startsWith(name) && weaponName.startsWith(name);
    }

    public static boolean hasAnyBarrowsSet(final Player player) {
        for (val name : BARROWS_NAMES) {
            if (hasFullBarrowsSet(player, name)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isWearingSerpentineHelmet(final Player player) {
        val helm = player.getEquipment().getId(EquipmentSlot.HELMET);
        return helm == 12931 || helm == 13199 || helm == 13197;
    }

    public static final boolean hasFullJusticiarSet(final Player player) {
        val helm = player.getHelmet();
        val chest = player.getChest();
        val legs = player.getLegs();
        if (helm == null || chest == null || legs == null) {
            return false;
        }
        val name = "Justiciar";
        return helm.getName().startsWith(name) && chest.getName().startsWith(name) && legs.getName().startsWith(name);
    }

    public static final int countInquisitorSet(final Player player) {
        final int helm = player.getEquipment().getId(EquipmentSlot.HELMET.getSlot());
        final int chest = player.getEquipment().getId(EquipmentSlot.PLATE.getSlot());
        final int legs = player.getEquipment().getId(EquipmentSlot.LEGS.getSlot());
        val name = "Inquisitor";
        int count = 0;
        if (helm == 24419) {
            count++;
        }
        if (chest == 24420) {
            count++;
        }
        if (legs == 24421) {
            count++;
        }
        return count;
    }

    public static final float getDharokModifier(final Player player) {
        final int max = player.getMaxHitpoints();
        final int current = player.getHitpoints();
        if (current > max)
            return 1;
        return 1 + ((max - current) / 100f);
    }

    public static final boolean hasFullObisidian(final Player player) {
        final int helm = player.getEquipment().getId(EquipmentSlot.HELMET.getSlot());
        final int body = player.getEquipment().getId(EquipmentSlot.PLATE.getSlot());
        final int legs = player.getEquipment().getId(EquipmentSlot.LEGS.getSlot());
        return helm == 21298 && body == 21301 && legs == 21304;
    }

    public static final boolean hasFullRangedVoid(final Player player, final boolean eliteOnly) {
        final int helm = player.getEquipment().getId(EquipmentSlot.HELMET.getSlot());
        final int body = player.getEquipment().getId(EquipmentSlot.PLATE.getSlot());
        final int legs = player.getEquipment().getId(EquipmentSlot.LEGS.getSlot());
        final int gloves = player.getEquipment().getId(EquipmentSlot.HANDS.getSlot());
        return helm == 11664 && gloves == 8842 && (body == 8839 && !eliteOnly || body == 13072)
                && (legs == 8840 && !eliteOnly || legs == 13073);
    }

    public static boolean hasCrystalHelm(final Player player) {
        final int helm = player.getEquipment().getId(EquipmentSlot.HELMET.getSlot());
        if (helm == -1) {
            return false;
        }
        final String name = player.getEquipment().getItem(EquipmentSlot.HELMET.getSlot()).getName();
        return helm == ItemId.CRYSTAL_HELM_FULL || helm == ItemId.CRYSTAL_HELM_100 || helm == ItemId.CRYSTAL_HELM_50 || (name.contains("slayer helm") && name.endsWith("(i)"));
    }

    public static boolean hasCrystalBody(final Player player) {
        final int body = player.getEquipment().getId(EquipmentSlot.PLATE.getSlot());
        if (body == -1) {
            return false;
        }
        return body == ItemId.CRYSTAL_BODY_FULL || body == ItemId.CRYSTAL_BODY_100 || body == ItemId.CRYSTAL_BODY_50;
    }

    public static boolean hasCrystalLegs(final Player player) {
        final int legs = player.getEquipment().getId(EquipmentSlot.LEGS.getSlot());
        if (legs == -1) {
            return false;
        }
        return legs == ItemId.CRYSTAL_LEGS_FULL || legs == ItemId.CRYSTAL_LEGS_100 || legs == ItemId.CRYSTAL_LEGS_50;
    }

    public static final boolean hasFullMagicVoid(final Player player, final boolean eliteOnly) {
        final int helm = player.getEquipment().getId(EquipmentSlot.HELMET.getSlot());
        final int body = player.getEquipment().getId(EquipmentSlot.PLATE.getSlot());
        final int legs = player.getEquipment().getId(EquipmentSlot.LEGS.getSlot());
        final int gloves = player.getEquipment().getId(EquipmentSlot.HANDS.getSlot());
        return helm == 11663 && gloves == 8842 && (body == 8839 && !eliteOnly || body == 13072)
                && (legs == 8840 && !eliteOnly || legs == 13073);
    }

    public static final boolean canCastCrumbleUndead(@NotNull final NPC target) {
        val name = target.getDefinitions().getName().toLowerCase();
        return name.contains("skeleton") || name.contains("zombie") || name.contains("ghost")
                || isShade(target) || name.contains("zombified spawn");
    }

    static final boolean isShade(@NotNull final Entity target) {
        if (!(target instanceof NPC))
            return false;
        val npc = (NPC) target;
        val id = npc.getId();
        return id == 5633 || id == 6740 || id == 7258 || ArrayUtils.contains(Shade.shades, id) || ArrayUtils.contains(Shade.shadows, id);
    }

    static final boolean isKerisAffected(@NotNull final Entity target) {
        if (!(target instanceof NPC))
            return false;
        val npc = (NPC) target;
        val name = npc.getDefinitions().getName().toLowerCase();
        return name.contains("kalphite") || name.contains("scarab") || name.contains("locust rider");
    }

    public static final boolean isDemon(@NotNull final Entity target) {
        if (!(target instanceof NPC)) {
            return false;
        }
        val npc = (NPC) target;
        val name = npc.getDefinitions().getName().toLowerCase();
        return ArrayUtils.contains(DEMON_NPCS, name);
    }

    public static final boolean isRevenant(@NotNull final Entity target) {
        if (!(target instanceof NPC)) {
            return false;
        }
        val npc = (NPC) target;
        val name = npc.getDefinitions().getName().toLowerCase();
        return ArrayUtils.contains(REVENANT_NPCS, name);
    }

    public static void delayHit(final NPC npc, int delay, final Entity target, final Hit... hits) {
        if (npc != null) {
            npc.getCombat().addAttackedByDelay(target);
        }
        boolean melee = false;
        for (int i = hits.length - 1; i >= 0; i--) {
            if (hits[i].getHitType() == HitType.MELEE) {
                melee = true;
                break;
            }
        }
        if (melee) {
            if (delay == 0) {
                delay = -1;
            }
        }
        for (val hit : hits) {
            target.scheduleHit(npc, hit, delay);
        }
    }

    public static void processHit(final Entity target, final Hit hit) {
        val delay = target.getProtectionDelay();
        if (hit.getScheduleTime() < delay) {
            return;
        }
        if (hit.getSource() instanceof NPC) {
            final NPC npc = (NPC) hit.getSource();
            if (!npc.applyDamageFromHitsAfterDeath() && (npc.isDead() || npc.isFinished()) || target.isDead() || target.isFinished()) {
                return;
            }
            npc.handleOutgoingHit(target, hit);
            if(canRetaliateAgainst(npc.getId())) {
                target.autoRetaliate(npc);
            }
        } else {
            val source = hit.getSource();
            if (source != null) {
                if (source.isDead() || source.isFinished()) {
                    return;
                }
                source.handleOutgoingHit(target, hit);
                target.autoRetaliate(source);
            }
        }
        target.applyHit(hit);
        val consumer = hit.getOnLandConsumer();
        if (consumer != null) {
            consumer.accept(hit);
        }
    }

    public static boolean isWithinMeleeDistance(final NPC npc, final Entity target) {
        val distanceX = npc.getX() - target.getX();
        val distanceY = npc.getY() - target.getY();
        val npcSize = npc.getSize();
        val targetSize = target.getSize();
        if (distanceX == -npcSize && distanceY == -npcSize
                || distanceX == targetSize && distanceY == targetSize
                || distanceX == -npcSize && distanceY == targetSize
                || distanceX == targetSize && distanceY == -npcSize) {
            return false;
        }
        return distanceX >= -npcSize && distanceX <= 1 && distanceY >= -npcSize && distanceY <= 1;
    }

    public static boolean isWithinMeleeDistance(final Location location, final int size, final Entity target) {
        val distanceX = location.getX() - target.getX();
        val distanceY = location.getY() - target.getY();
        val targetSize = target.getSize();
        if (distanceX == -size && distanceY == -size
                || distanceX == targetSize && distanceY == targetSize
                || distanceX == -size && distanceY == targetSize
                || distanceX == targetSize && distanceY == -size) {
            return false;
        }
        return distanceX >= -size && distanceX <= 1 && distanceY >= -size && distanceY <= 1;
    }

    public static int getRandomMaxHit(final NPC npc, final int maxHit, final AttackType attackStyle,
                                      final Entity target) {
        return getRandomMaxHit(npc, maxHit, attackStyle, attackStyle, target);
    }

    public static int getRandomMaxHit(final NPC npc, int maxHit, final AttackType attackStyle,
                                      AttackType targetStyle, final Entity target) {
        val statDefs = npc.getCombatDefinitions().getStatDefinitions();
        val effectiveLevel = statDefs.get(attackStyle.isRanged() ? StatType.RANGED :
                attackStyle.isMagic() ? StatType.MAGIC : StatType.ATTACK) + 8;
        val accuracyBoost = statDefs.get(StatType.getAttackType(attackStyle));
        final int roll = effectiveLevel * (accuracyBoost + 64);
        final int targetRoll = PlayerCombat.getTargetDefenceRoll(npc, target, targetStyle);
        double accuracy;

        if (roll > targetRoll) {
            accuracy = 1 - (targetRoll + 2d) / (2 * (roll + 1));
        } else {
            accuracy = roll / (2d * (targetRoll + 1d));
        }

        if (accuracy < Utils.randomDouble()) {
            return 0;
        }

        //maxHit *= attackStyle.isMelee() ? target.getMeleePrayerMultiplier() : attackStyle.isRanged() ? target.getRangedPrayerMultiplier() : target.getMagicPrayerMultiplier();
        //This is an implementation for tick-eating. Some exceptional monsters such as the great olm and zuk from inferno will bypass this feature.
        int damage = Utils.random(maxHit);
        if (npc.isTickEdible()) {
            if (damage > target.getHitpoints()) {
                damage = target.getHitpoints();
            }
        }
        return damage;
    }

    //These two methods are used in the case that you need to know if the npc splashes or hits
    public static int getRandomMaxHit(final NPC npc, int maxHit, final Entity target, final boolean isHit) {
        if(isHit) {
            return 0;
        }

        int damage = Utils.random(maxHit);
        if (npc.isTickEdible()) {
            if (damage > target.getHitpoints()) {
                damage = target.getHitpoints();
            }
        }
        return damage;
    }

    public static boolean isHit(final NPC npc, final AttackType attackStyle,
                                     AttackType targetStyle, final Entity target) {

        val statDefs = npc.getCombatDefinitions().getStatDefinitions();
        val effectiveLevel = statDefs.get(attackStyle.isRanged() ? StatType.RANGED :
                attackStyle.isMagic() ? StatType.MAGIC : StatType.ATTACK) + 8;
        val accuracyBoost = statDefs.get(StatType.getAttackType(attackStyle));
        final int roll = effectiveLevel * (accuracyBoost + 64);
        final int targetRoll = PlayerCombat.getTargetDefenceRoll(npc, target, targetStyle);
        double accuracy;

        if (roll > targetRoll) {
            accuracy = 1 - (targetRoll + 2d) / (2 * (roll + 1));
        } else {
            accuracy = roll / (2d * (targetRoll + 1d));
        }

        return (accuracy < Utils.randomDouble());
    }

    private static boolean canRetaliateAgainst(int id) {
        switch(id) {
            case 3621:
            case 3622:
            case 3623:
            case 3624:
                return false;
            default:
                return true;
        }
    }
}
