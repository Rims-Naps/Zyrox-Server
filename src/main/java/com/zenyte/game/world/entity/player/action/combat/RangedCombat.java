package com.zenyte.game.world.entity.player.action.combat;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.minigame.inferno.npc.impl.zuk.TzKalZuk;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Entity.EntityType;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.Toxins.ToxinType;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.npc.combatdefs.StatType;
import com.zenyte.game.world.entity.pathfinding.events.player.CombatEntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.PredictedEntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.region.area.plugins.EntityAttackPlugin;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.var;
import mgi.types.config.AnimationDefinitions;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 14. okt 2017 : 19:51.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class RangedCombat extends PlayerCombat {

    private final int[] BOFAARRAY = new int[] {30596, 32158, 32160, 32162, 32164, 32166, 32168, 32170, 32172, 32174};

    @AllArgsConstructor
    private enum AmmunitionPreserver {

        AVAS_ATTRACTOR(10498, 16),
        AVAS_ACCUMULATOR(10499, 8),
        AVAS_ASSEMBLER(22109, 0),
        ASSEMBLER_MAX_CAPE(21898, 0);

        private final int id, amount;
        private static final Int2IntOpenHashMap map = new Int2IntOpenHashMap();

        static {
            for (val id : SkillcapePerk.RANGED.getCapes()) {
                map.put(id, 8);
            }
            for (val entry : values()) {
                map.put(entry.id, entry.amount);
            }
        }

    }

    private enum EnchantedBoltSpecial {
        LUCKY_LIGHTNING(new SoundEffect(2918, 15, 0), new Graphics(749), 0.05F, 0.055F, 0.05F, 0.055F, 9236, 21932),
        EARTHS_FURY(new SoundEffect(2916, 15, 0), new Graphics(755), 0.06F, 0.066F, 0.06F, 0.066F, 9237, 21934),
        SEA_CURSE(new SoundEffect(2920, 15, 0), new Graphics(750), 0.06F, 0.066F, 0.06F, 0.066F, 9238, 21936),
        DOWN_TO_EARTH(new SoundEffect(2914, 15, 0), new Graphics(757), 0.04F, 0.044F, 0F, 0F, 9239, 21938),//n
        CLEAR_MIND(new SoundEffect(2912, 15, 0), new Graphics(751), 0.05F, 0.055F, 0F, 0F, 9240, 21940),//n
        MAGICAL_POISON(new SoundEffect(2919, 15, 0), new Graphics(752), 0.54F, 0.594F, 0.55F, 0.605F, 9241, 21942),//n
        BLOOD_FORFEIT(new SoundEffect(2911, 15, 0), new Graphics(754), 0.11F, 0.121F, 0.06F, 0.066F, 9242, 21944),
        ARMOUR_PIERCING(new SoundEffect(2913, 15, 0), new Graphics(758), 0.05F, 0.055F, 0.1F, 0.11F, 9243, 21946),
        DRAGONS_BREATH(new SoundEffect(2915, 15, 0), new Graphics(756), 0.06F, 0.066F, 0.06F, 0.066F, 9244, 21948),
        LIFE_LEECH(new SoundEffect(2917, 15, 0), new Graphics(753), 0.1F, 0.11F, 0.11F, 0.121F, 9245, 21950);

        private final float pvpProcChance, pvpKandarinProcChance, pvmProcChance, pvmKandarinProcChance;
        private final SoundEffect sound;
        private final Graphics graphics;
        private final int[] bolts;

        private static final Int2ObjectMap<EnchantedBoltSpecial> map = new Int2ObjectOpenHashMap<>();

        static {
            for (val value : values()) {
                for (val bolt : value.bolts) {
                    map.put(bolt, value);
                }
            }
        }

        EnchantedBoltSpecial(final SoundEffect sound, final Graphics graphics,  final float pvpProcChance, final float pvpKandarinProcChance, final float pvmProcChance,
                             final float pvmKandarinProcChance,
                             final int... bolts) {
            this.sound = sound;
            this.graphics = graphics;
            this.pvpProcChance = pvpProcChance;
            this.pvpKandarinProcChance = pvpKandarinProcChance;
            this.pvmProcChance = pvmProcChance;
            this.pvmKandarinProcChance = pvmKandarinProcChance;
            this.bolts = bolts;
        }

        private static final EnchantedBoltSpecial get(@NotNull final Player player, @NotNull final Entity target) {
            val element = map.get(player.getEquipment().getId(EquipmentSlot.AMMUNITION));
            if (element == null || !element.isEffective(player, target)) {
                return null;
            }
            return element;
        }

        private final boolean isEffective(@NotNull final Player player, @NotNull final Entity target) {
            val ammunitionId = player.getEquipment().getId(EquipmentSlot.AMMUNITION);
            val ammunition = AmmunitionDefinitions.getDefinitions(ammunitionId);
            if (ammunition == null || !ammunition.isCompatible(player.getEquipment().getId(EquipmentSlot.WEAPON))) {
                return false;
            }
            val diary = DiaryReward.KANDARIN_HEADGEAR3.eligibleFor(player);
            return Utils.randomDouble() < (target instanceof Player ? (diary ? pvpKandarinProcChance : pvpProcChance) : (diary ? pvmKandarinProcChance : pvmProcChance));
        }
    }

    protected static final int BREAK_CHANCE = 20;
    protected AmmunitionDefinitions ammunition;
    private EnchantedBoltSpecial boltSpecial;

    public RangedCombat(final Entity target, final AmmunitionDefinitions defs) {
        super(target);
        ammunition = defs;
    }

    @Override
    public final Hit getHit(final Player player, final Entity target, final double accuracyModifier, final double passiveModifier, double activeModifier, final boolean ignorePrayers) {
        return new Hit(player, getRandomHit(player, target, getMaxHit(player, passiveModifier, 1, ignorePrayers), accuracyModifier), HitType.RANGED);
    }

    @Override
    public int getMaxHit(final Player player, final double specialModifier, double activeModifier, final boolean ignorePrayers) {

        float boost = CombatUtilities.hasFullRangedVoid(player, true) ? 1.125F : CombatUtilities.hasFullRangedVoid(player,
                false) ? 1.1F : 1F;


        val weaponId = player.getEquipment().getId(EquipmentSlot.WEAPON);
        if ((weaponId == 21012 || weaponId == 32197  || weaponId == 32199) && CombatUtilities.isDraconic(target)) {
            boost += 0.3F;

        }

        val a = (Math.floor(player.getSkills().getLevel(Skills.RANGED)
                * player.getPrayerManager().getRangedBoost(Skills.STRENGTH))
                + (player.getCombatDefinitions().getStyle() == 0 ? 3 : 0) + 8)
                * (boost);

        val b = (float) player.getBonuses().getBonus(11);
        var result = Math.floor(0.5F + a * (b + 64F) / 640F);

        val amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        if ((amuletId == 12017 || amuletId == 12018) && (CombatUtilities.SALVE_AFFECTED_NPCS.contains(name) || CombatUtilities.isUndeadCombatDummy(target))) {
            result *= amuletId == 12017 ? 1.15F : 1.2F;
        } else if (player.getSlayer().isCurrentAssignment(target) || CombatUtilities.isUndeadCombatDummy(target)) {
            val helmId = player.getEquipment().getId(EquipmentSlot.HELMET);
            val definitions = ItemDefinitions.get(helmId);
            val name = definitions == null ? null : definitions.getName().toLowerCase();
            if (name != null && (name.contains("black mask") || name.contains("slayer helm")) && name.endsWith("(i)")) {
                result *= 1.15F;
            }
        }

        val weaponId2 = player.getEquipment().getId(EquipmentSlot.WEAPON);
        if (ArrayUtils.contains(BOFAARRAY,weaponId2)) {
            if (CombatUtilities.hasCrystalHelm(player)) {
                result *= 1.025F;
            }
            if (CombatUtilities.hasCrystalBody(player)) {
                result *= 1.075F;
            }
            if (CombatUtilities.hasCrystalLegs(player)) {
                result *= 1.05F;
            }
        }

        result = Math.floor(result);

        if (boltSpecial == EnchantedBoltSpecial.ARMOUR_PIERCING) {
            result *= 1.15F;
        } else if (boltSpecial == EnchantedBoltSpecial.LIFE_LEECH) {
            if (!CombatUtilities.SALVE_AFFECTED_NPCS.contains(name)) {
                result *= 1.2F;
            }
        }

        result *= getTwistedBowMaxModifier();

        result = Math.floor(result);

        if (!ignorePrayers) {
            if (target instanceof Player && ((Player) target).getPrayerManager().isActive(Prayer.PROTECT_FROM_MISSILES)) {
                result *= target.getRangedPrayerMultiplier();
                result = Math.floor(result);
            }
        }

        result *= specialModifier;

        result = Math.floor(result);

        if (boltSpecial == EnchantedBoltSpecial.DRAGONS_BREATH) {
            if (isDragonsBeath()) {
                result += player.getSkills().getLevel(Skills.RANGED) * 0.2F;
            }
        } else if (boltSpecial == EnchantedBoltSpecial.LUCKY_LIGHTNING) {
            result += player.getSkills().getLevel(Skills.RANGED) * 0.1F;
        } else if (boltSpecial == EnchantedBoltSpecial.SEA_CURSE) {
            target.setGraphics(new Graphics(750));
            int divider = 20;
            if (target instanceof Player) {
                val targetWeapon = ((Player) target).getEquipment().getId(EquipmentSlot.WEAPON.getSlot());
                if (targetWeapon == 1387 || targetWeapon == 1393 || targetWeapon == 1401 || targetWeapon == 3054) {
                    divider = 15;
                } else if (targetWeapon == 1383 || targetWeapon == 1395 || targetWeapon == 1403) {
                    divider = -1;
                }
            } else {
                if (CombatUtilities.isFireNPC((NPC) target)) {
                    divider = 15;
                }
            }
            if (divider != -1) {
                result += (float) player.getSkills().getLevel(Skills.RANGED) / divider;
            }
        }

        //Castle wars bracelet effect
        if (player.getTemporaryAttributes().containsKey("castle wars bracelet effect")
                && player.inArea("Castle Wars")) {
            if (target instanceof Player) {
                val targetWeapon = ((Player) target).getEquipment().getId(EquipmentSlot.WEAPON);
                if (targetWeapon == 4037 || targetWeapon == 4039) {
                    result = result * 1.2F;
                }
            }
        }

        return (int) Math.floor(result);
    }

    @Override
    public final int getRandomHit(final Player player, final Entity target, final int maxhit, final double modifier) {
        return getRandomHit(player, target, maxhit, modifier, AttackType.RANGED);
    }

    @Override
    public int getRandomHit(final Player player, final Entity target, final int maxhit, final double modifier, final AttackType oppositeIndex) {
        float boost = CombatUtilities.hasFullRangedVoid(player, true) ? 1.125F : CombatUtilities.hasFullRangedVoid(player,
                false) ? 1.1F : 1F;


        val weaponId = player.getEquipment().getId(EquipmentSlot.WEAPON);
        if ((weaponId == 21012 || weaponId == 32197 || weaponId == 32199) && CombatUtilities.isDraconic(target)) {
            boost += 0.3F;
        }

        val a =
                Math.floor(Math.floor(player.getSkills().getLevel(Skills.RANGED) * player.getPrayerManager().getRangedBoost(Skills.ATTACK))
                + (player.getCombatDefinitions().getStyle() == 0 ? 3 : 0) + 8F)
                * (boost);
        val b = player.getBonuses().getBonus(4);

        var result = a * (b + 64F);

        val amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        if ((amuletId == 12017 || amuletId == 12018) && CombatUtilities.SALVE_AFFECTED_NPCS.contains(name)) {
            result *= amuletId == 12017 ? 1.15F : 1.2F;
        } else if (player.getSlayer().isCurrentAssignment(target)) {
            val helmId = player.getEquipment().getId(EquipmentSlot.HELMET);
            val definitions = ItemDefinitions.get(helmId);
            val name = definitions == null ? null : definitions.getName().toLowerCase();
            if (name != null && (name.contains("black mask") || name.contains("slayer helm"))) {
                result *= 1.15F;
            }
        }

        val weaponId2 = player.getEquipment().getId(EquipmentSlot.WEAPON);
        if (ArrayUtils.contains(BOFAARRAY,weaponId2)) {
            if (CombatUtilities.hasCrystalHelm(player)) {
                result += 0.05F;
            }
            if (CombatUtilities.hasCrystalBody(player)) {
                result += 0.15F;
            }
            if (CombatUtilities.hasCrystalLegs(player)) {
                result += 0.10F;
            }
        }

        result = Math.floor(result);
        result *= modifier;
        result *= getTwistedBowAccuracyModifier();

        result = Math.floor(result);

        val targetRoll = getTargetDefenceRoll(player, target, oppositeIndex);
        double accuracy = result > targetRoll ? (1F - (targetRoll + 2F) / (2F*(result + 1F))) : (result / (2F*(targetRoll + 1F)));
        if (boltSpecial == EnchantedBoltSpecial.ARMOUR_PIERCING) {
            accuracy = 1;
        }
        if (CombatUtilities.isCombatDummy(target)) {
            return maxhit;
        }
        sendDebug(accuracy, maxhit);
        if (accuracy < Utils.randomDouble()) {
            return 0;
        }
        return Utils.random(maxhit);
    }

    private final double getTwistedBowMaxModifier() {
        if (player.getWeapon() != null) {
            if (player.getWeapon().getId() == 20997) {
                val mod = 250;
                var magicModifier = 0;
                if (target.getEntityType() == EntityType.NPC) {
                    val npc = (NPC) target;
                    magicModifier = target instanceof TzKalZuk ? npc.getCombatDefinitions().getStatDefinitions().getAggressiveStat(StatType.MAGIC) : npc.getCombatDefinitions().getStatDefinitions().get(StatType.MAGIC);
                } else {
                    magicModifier = ((Player) target).getSkills().getLevel(Skills.MAGIC);
                }
                magicModifier = Math.min(mod, magicModifier);
                val a = 250;
                val b = ((3F * magicModifier) - 14F) / 100F;
                val c = Math.pow((((3F * magicModifier) / 10F) - 140), 2) / 100F;
                return Math.min(player.getRaid().isPresent() ? 350 : 250, a + b - c) / 100F;
            }
        }
        return 1;
    }

    private final double getTwistedBowAccuracyModifier() {
        if (player.getWeapon() != null) {
            if (player.getWeapon().getId() == 20997) {
                val mod = 250;
                var magicModifier = 0;
                if (target.getEntityType() == EntityType.NPC) {
                    val npc = (NPC) target;
                    magicModifier = target instanceof TzKalZuk ? npc.getCombatDefinitions().getStatDefinitions().getAggressiveStat(StatType.MAGIC) : npc.getCombatDefinitions().getStatDefinitions().get(StatType.MAGIC);
                } else {
                    magicModifier = ((Player) target).getSkills().getLevel(Skills.MAGIC);
                }
                magicModifier = Math.min(mod, magicModifier);
                val a = 140;
                val b = ((3F * magicModifier) - 10F) / 100F;
                val c = Math.pow((((3F * magicModifier) / 10F) - 100), 2) / 100F;
                return Math.min(140, a + b - c) / 100F;
            }
        }
        return 1;
    }

    @Override
    public boolean process() {
        return initiateCombat(player);
    }

    @Override
    protected boolean isWithinAttackDistance() {
        if (target.checkProjectileClip(player) && isProjectileClipped(true, false)) {
            return false;
        }
        val nextTile = target.getNextLocation();
        val tile = nextTile != null ? nextTile : target.getLocation();
        val distanceX = player.getX() - tile.getX();
        val distanceY = player.getY() - tile.getY();
        val size = target.getSize();
        var maxDistance = getAttackDistance();
        val nextLocation = target.getNextPosition(target.isRun() ? 2 : 1);
        if ((player.isFrozen() || player.isStunned())
                && (Utils.collides(player.getX(), player.getY(), player.getSize(), nextLocation.getX(), nextLocation.getY(), target.getSize())
                || !withinRange(target, maxDistance, target.getSize()))) {
            return false;
        }
        if (player.hasWalkSteps()) {
            val dist = getTileDistance(false);
            val postWalkDistance = getTileDistance(true);
            //If the player is about to move, but his movement doesn't help him progress towards the target as much as it could, we only append as many tiles as the player moves
            //towards the target.
            maxDistance += Math.min(player.isRun() ? 2 : 1, Math.abs(postWalkDistance - dist));
        }
        return distanceX <= size + maxDistance && distanceX >= -1 - maxDistance && distanceY <= size + maxDistance && distanceY >= -1 - maxDistance;
    }

    @Override
    public int processWithDelay() {
        if (!isWithinAttackDistance()) {
            return 0;
        }
        if (!canAttack()) {
            return -1;
        }
        boltSpecial = EnchantedBoltSpecial.get(player, target);

        addAttackedByDelay(player, target);
        val area = player.getArea();
        if (area instanceof PlayerCombatPlugin) {
            ((PlayerCombatPlugin) area).onAttack(player, target, "Ranged");
        }
        if (player.getCombatDefinitions().isUsingSpecial()) {
            val delay = useSpecial(player, SpecialType.RANGED);
            if (delay == SpecialAttackScript.WEAPON_SPEED) {
                return getWeaponSpeed();
            }
            if (delay >= 0) {
                return delay;
            }
            player.putBooleanAttribute("used_special", true);
        }
        player.putBooleanAttribute("used_special", false);
        extra();
        animate();
        val ticks = this.fireProjectile();

        if (ammunition.getSoundEffect() != null) {
            player.getPacketDispatcher().sendSoundEffect(ammunition.getSoundEffect());
        }

        resetFlag();
        val hit = getHit(player, target, 1, 1, 1, false);
        applyBoltSpecials(hit);
        if (hit.getDamage() > 0) {
            addPoisonTask(ticks);
        }
        delayHit(ticks, hit);
        drawback();
        dropAmmunition(ticks, !ammunition.isRetrievable());
        checkIfShouldTerminate();
        return getWeaponSpeed();
    }

    private final void applyBoltSpecials(@NotNull final Hit hit) {
        if (boltSpecial == EnchantedBoltSpecial.LUCKY_LIGHTNING || boltSpecial == EnchantedBoltSpecial.SEA_CURSE || boltSpecial == EnchantedBoltSpecial.ARMOUR_PIERCING) {
            target.setGraphics(boltSpecial.graphics);
            World.sendSoundEffect(new Location(target.getLocation()), boltSpecial.sound);
        } else if (boltSpecial == EnchantedBoltSpecial.DOWN_TO_EARTH) {
            if (target instanceof Player) {
                val p = (Player) target;
                val level = p.getSkills().getLevel(Skills.MAGIC);
                if (level > 0) {
                    p.getSkills().setLevel(Skills.MAGIC, level - 1);
                }
                target.setGraphics(boltSpecial.graphics);
                World.sendSoundEffect(new Location(target.getLocation()), boltSpecial.sound);
            }
        } else if (boltSpecial == EnchantedBoltSpecial.CLEAR_MIND) {
            if (target instanceof Player) {
                val p = (Player) target;
                val points = player.getPrayerManager().getPrayerPoints();
                val drained = p.getPrayerManager().drainPrayerPoints(points / 20);
                player.getPrayerManager().restorePrayerPoints(drained);
                target.setGraphics(boltSpecial.graphics);
                World.sendSoundEffect(new Location(target.getLocation()), boltSpecial.sound);

            }
        } else if (boltSpecial == EnchantedBoltSpecial.MAGICAL_POISON) {
            target.setGraphics(boltSpecial.graphics);
            World.sendSoundEffect(new Location(target.getLocation()), boltSpecial.sound);
            target.getToxins().applyToxin(ToxinType.POISON, 5);
        } else if (boltSpecial == EnchantedBoltSpecial.BLOOD_FORFEIT) {
            if (player.getHitpoints() * 0.1F >= 1) {
                target.setGraphics(boltSpecial.graphics);
                World.sendSoundEffect(new Location(target.getLocation()), boltSpecial.sound);
                player.applyHit(new Hit(Math.min(99, (int) (player.getHitpoints() * 0.1F)), HitType.DEFAULT));
                hit.setDamage((int) Math.min(100, target.getHitpoints() * 0.2F));
            }
        } else if (boltSpecial == EnchantedBoltSpecial.DRAGONS_BREATH) {
            if (isDragonsBeath()) {
                target.setGraphics(boltSpecial.graphics);
                World.sendSoundEffect(new Location(target.getLocation()), boltSpecial.sound);
            }
        } else if (boltSpecial == EnchantedBoltSpecial.LIFE_LEECH) {
            if (!CombatUtilities.SALVE_AFFECTED_NPCS.contains(name)) {
                target.setGraphics(boltSpecial.graphics);
                World.sendSoundEffect(new Location(target.getLocation()), boltSpecial.sound);
                player.heal((int) (hit.getDamage() * 0.25F));
            }
        } else if (boltSpecial == EnchantedBoltSpecial.EARTHS_FURY) {
            if (Utils.randomDouble() >= (target instanceof Player ? (((Player) target).getSkills().getLevel(Skills.AGILITY) / 200F) : 0)) {
                target.setGraphics(boltSpecial.graphics);
                target.setAnimation(new Animation(4172));
                target.stun(8);
                target.resetWalkSteps();
                World.sendSoundEffect(new Location(target.getLocation()), boltSpecial.sound);
            }
        }
    }

    private boolean isDragonsBeath() {
        if (target instanceof Player) {
            val p = (Player) target;
            val shield = p.getEquipment().getId(EquipmentSlot.SHIELD.getSlot());
            return p.getVariables().getTime(TickVariable.ANTIFIRE) <= 0
                    && p.getVariables().getTime(TickVariable.SUPER_ANTIFIRE) <= 0 && shield != 1540 && shield != 8282 && shield != 11283 && shield != 11284;
        } else if (target.getEntityType() == Entity.EntityType.NPC) {
            return !CombatUtilities.isFireNPC((NPC) target);
        }
        return true;
    }

    @Override
    public boolean start() {
        player.setCombatEvent(new CombatEntityEvent(player, new PredictedEntityStrategy(target)));
        player.setLastTarget(target);
        notifyIfFrozen();
        setModifiers();
        if (!checkPreconditions())
            return false;
        player.setFaceEntity(target);
        if (initiateCombat(player)) {
            return true;
        }
        player.setFaceEntity(null);
        return false;
    }

    protected boolean checkPreconditions() {
        if (ammunition == null) {
            player.sendMessage("You don't have any ammunition.");
            return false;
        } else if (!ammunition.isCompatible(player.getEquipment().getId(EquipmentSlot.WEAPON))) {
            player.sendMessage("You cannot use that ammunition with this weapon.");
            return false;
        } else if (!ammunition.isWeapon() && player.getEquipment().getId(EquipmentSlot.AMMUNITION) == 9419) {
            player.sendMessage("You cannot use grapple-tipped bolts for combat.");
            return false;
        }
        return true;
    }

    protected void addPoisonTask(final int delay) {
        val weapon = player.getEquipment()
                .getItem(ammunition.isWeapon() ? EquipmentSlot.WEAPON.getSlot() : EquipmentSlot.AMMUNITION.getSlot());
        if (weapon == null) {
            return;
        }
        val name = weapon.getName();
        if (!name.contains("(p")) {
            return;
        }

        if (CombatUtilities.isWearingSerpentineHelmet(player) && target.getEntityType() == EntityType.NPC && (Utils.random(1) == 0)) {
            WorldTasksManager.schedule(() -> target.getToxins().applyToxin(ToxinType.VENOM, 6), delay);
        } else if (Utils.random(3) == 0) {
            WorldTasksManager.schedule(
                    () -> target.getToxins().applyToxin(ToxinType.POISON, name.contains("p++") ? 6 : name.contains("p+") ? 5 : 4), delay);
        }
    }

    protected void animate() {
        player.setAnimation(new Animation(getAttackAnimation(target instanceof Player, player.getEquipment().getAttackAnimation(player.getCombatDefinitions().getStyle()))));
    }

    protected final boolean canAttack() {
        if (!attackable())
            return false;
        if (!target.canAttack(player)) {
            return false;
        }
        val area = player.getArea();
        if ((area instanceof EntityAttackPlugin && !((EntityAttackPlugin) area).attack(player, target))) {
            return false;
        }
        if ((area instanceof PlayerCombatPlugin && !((PlayerCombatPlugin) area).processCombat(player, target, "Ranged")) || !player.getControllerManager().processPlayerCombat(target, "Ranged")) {
            return false;
        }
        return !target.isDying();
    }

    protected boolean outOfAmmo() {
        if(ammunition == null) {
            return true;
        }
        return ammunition.isWeapon() ? player.getWeapon() == null : player.getAmmo() == null;
    }

    protected void drawback() {
        if (ammunition.getDrawbackGfx() != null) {
            player.setGraphics(ammunition.getDrawbackGfx());
        }
    }

    protected void dropAmmunition(final int delay, final boolean destroy) {
        if (ammunition == null) {
            return;
        }
        val slot = ammunition.isWeapon() ? EquipmentSlot.WEAPON : EquipmentSlot.AMMUNITION;
        val slotId = slot.getSlot();
        val ammo = player.getEquipment().getItem(slotId);
        if (ammo == null) {
            return;
        }
        val dropChance = getAmmunitionDropChance();
        val roll = Utils.random(100);
        val equipment = player.getEquipment();
        if (destroy || roll <= BREAK_CHANCE || roll <= (BREAK_CHANCE + dropChance)) {
            val ammoAmount = ammo.getAmount();
            if (ammoAmount > 1) {
                ammo.setAmount(ammoAmount - 1);
            } else {
                equipment.set(slot, null);
            }
            equipment.refresh(slotId);
            if (destroy || roll <= BREAK_CHANCE) {
                return;
            }
        }

        if (roll <= (BREAK_CHANCE + dropChance)) {
            val location = new Location(target.getNextPosition(target.isRun() ? 2 : 1));
            WorldTasksManager.schedule(() -> {
                val item = new Item(ammo.getId());
                val duel = player.getDuel();
                if (duel != null) {
                    duel.getAmmunitions().get(player).add(item);
                } else {
                    World.spawnFloorItem(item, !World.isFloorFree(location, 1) ? new Location(player.getLocation()) :
                            location, 20, player, player, 300, 500);
                }
            }, delay);
        }
    }

    protected int fireProjectile() {
        return fireProjectile(ammunition.getProjectile());
    }

    protected int fireProjectile(final Projectile projectile) {
        if (projectile == null) {
            return 0;
        }
        val animation = player.getAnimation();
        AnimationDefinitions definitions = animation == null ? null : AnimationDefinitions.get(animation.getId());
        val startTile = definitions == null || definitions.getMergedBoneGroups() != null ? player.getNextPosition(player.isRun() ? 2 : 1) : new Location(player.getLocation());
        World.sendProjectile(startTile, target, projectile);
        return projectile.getTime(startTile, target);
    }

    protected int getAmmunitionDropChance() {
        val cape = player.getCape();
        if (cape != null && hasVorkathHeadEffect(cape)) {
            return AmmunitionPreserver.map.getOrDefault(22109, 100);
        }
        return AmmunitionPreserver.map.getOrDefault(player.getEquipment().getId(EquipmentSlot.CAPE), 100);
    }

    private boolean hasVorkathHeadEffect(final Item cape) {
        if (!(cape.getId() == ItemId.RANGING_CAPE || cape.getId() == ItemId.RANGING_CAPET || cape.getId() == ItemId.MAX_CAPE_13342)) {
            return false;
        }
        val attrKey = "assembler_effect_on_" + cape.getName().toLowerCase().replace(" ", "_").replace("(t)", Strings.EMPTY);
        return player.getNumericAttribute(attrKey).intValue() != 0;
    }

    @Override
    protected int getAttackDistance() {
        val definitions = ItemDefinitions.get(player.getEquipment().getId(EquipmentSlot.WEAPON));
        if (definitions == null) {
            return 5;
        }
        return (player.getCombatDefinitions().getStyle() == 3 ? definitions.getLongAttackDistance()
                : definitions.getNormalAttackDistance());
    }

    protected int getWeaponSpeed() {
        val definitions = ItemDefinitions.get(player.getEquipment().getId(EquipmentSlot.WEAPON));
        if (definitions == null) {
            return 5;
        }
        return (player.getCombatDefinitions().getStyle() == 1 ? 8 : 9) - definitions.getAttackSpeed();
    }

    protected boolean initiateCombat(final Player player) {
        if (player.isDead() || player.isFinished() || player.isLocked() || player.isStunned()) {
            return false;
        }
        if (target.isDead() || target.isFinished() || target.isCantInteract()) {
            return false;
        }
        if (outOfAmmo()) {
            player.sendMessage("You've ran out of ammo!");
            return false;
        }
        val distanceX = player.getX() - target.getX();
        val distanceY = player.getY() - target.getY();
        val size = target.getSize();
        if (outOfAmmo())
            return false;
        val viewDistance = player.getViewDistance();
        if (player.getPlane() != target.getPlane() || distanceX > size + viewDistance || distanceX < -1 - viewDistance
                || distanceY > size + viewDistance || distanceY < -1 - viewDistance) {
            return false;
        }
        if (target.getEntityType() == EntityType.PLAYER) {
            if (!player.isCanPvp() || !((Player) target).isCanPvp()) {
                player.sendMessage("You can't attack someone in a safe zone.");
                return false;
            }
        }
        if (player.isFrozen() || player.getMovementLock() > Utils.currentTimeMillis()) {
            return true;
        }

        if (colliding()) {
            player.getCombatEvent().process();
            return true;
        }
        if (handleDragonfireShields(player, false)) {
            if (!canAttack()) {
                return false;
            }
            handleDragonfireShields(player, true);
            player.getActionManager().addActionDelay(4);
            return true;
        }
        return pathfind();
    }


    protected void setModifiers() {
        float modifier = 0;
        val amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        if ((amuletId == 12017 || amuletId == 12018) && (CombatUtilities.SALVE_AFFECTED_NPCS.contains(name) || CombatUtilities.isUndeadCombatDummy(target))) {
            modifier = amuletId == 12017 ? 0.15F : 0.2F;
        } else {
            if (player.getSlayer().isCurrentAssignment(target) || CombatUtilities.isUndeadCombatDummy(target)) {
                val helmId = player.getEquipment().getId(EquipmentSlot.HELMET);
                val definitions = ItemDefinitions.get(helmId);
                val name = definitions == null ? null : definitions.getName().toLowerCase();
                if (name != null && (name.contains("black mask") || name.contains("slayer helm")) && name.endsWith("(i)")) {
                    modifier = 0.15f;
                }
            }
        }
        if (CombatUtilities.hasFullRangedVoid(player, true)) {
            maxhitModifier += 0.12F;
        } else if (CombatUtilities.hasFullRangedVoid(player, false)) {
            maxhitModifier += 0.1F;
        }

        if (CombatUtilities.hasCrystalHelm(player)) {
            maxhitModifier += 0.15F;
            accuracyModifier += 0.30F;
        }

        if (maxhitModifier > 1) {
            accuracyModifier += maxhitModifier - 1;
        }
        maxhitModifier += modifier;
        accuracyModifier += modifier;

    }

}
