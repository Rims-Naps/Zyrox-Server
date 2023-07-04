package com.zenyte.game.world.entity.player.action.combat;

import com.zenyte.game.content.boss.grotesqueguardians.boss.Dawn;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Entity.EntityType;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.Toxins.ToxinType;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.pathfinding.events.player.CombatEntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.PredictedEntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.region.area.plugins.EntityAttackPlugin;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;

import static com.zenyte.game.world.entity.player.action.combat.AttackStyle.AttackExperienceType.*;

/**
 * @author Kris | 5. jaan 2018 : 2:03.26
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class MeleeCombat extends PlayerCombat {

    private int extraSpace;

    public MeleeCombat(final Entity target) {
        super(target);
    }

    @Override
    int getAttackDistance() {
        return 0;
    }

    @Override
    int fireProjectile() {
        return 0;
    }

    @Override
    public Hit getHit(final Player player, final Entity target, final double accuracyModifier, final double passiveModifier, double activeModifier, final boolean ignorePrayers) {
        val hit = new Hit(player, getRandomHit(player, target, getMaxHit(player, passiveModifier, activeModifier, ignorePrayers), accuracyModifier), HitType.MELEE);
        if (player.getCombatDefinitions().isUsingSpecial()) {
            hit.putAttribute("using special", true);
        }
        return hit;
    }

    private static final int[] obsidianWeaponry = new int[]{
            6527, 6528, 6523, 6525, 6526, 20554, ItemId.TZHAARKETOM_T
    };

    @Override
    public int getMaxHit(final Player player, final double passiveModifier, double activeModifier, final boolean ignorePrayers) {
        val attackType = player.getCombatDefinitions().getAttackExperienceType();
        float boost = CombatUtilities.hasFullMeleeVoid(player, true) ? 1.12F
      : CombatUtilities.hasFullMeleeVoid(player, false) ? 1.1F : 1;
        val weaponId = player.getEquipment().getId(EquipmentSlot.WEAPON);
        if (weaponId == 22978 && CombatUtilities.isDraconic(target)) {
            boost += 0.2F;
        }

        val attackstyle = player.getCombatDefinitions().getAttackStyle();
        if(attackstyle.getType() == AttackType.CRUSH) {
            val inquisCount = CombatUtilities.countInquisitorSet(player);
            boost += (inquisCount == 3 ? 0.025F : inquisCount == 2 ? 0.01F : inquisCount == 1 ? 0.005 : 0);
        }

        val a = (Math.floor(player.getSkills().getLevel(Skills.STRENGTH) * player.getPrayerManager().getSkillBoost(Skills.STRENGTH))
                + (attackType == STRENGTH_XP ? 3 : attackType == SHARED_XP ? 1 : 0) + 8)
                * (boost);

        val b = (float) player.getBonuses().getBonus(10);
        var result = Math.floor(0.5F + a * (b + 64F) / 640F);

        val amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        if ((amuletId == 4081 || amuletId == 12017 || amuletId == 10588 || amuletId == 12018) && (CombatUtilities.SALVE_AFFECTED_NPCS.contains(name) || CombatUtilities.isUndeadCombatDummy(target))) {
            result *= (amuletId == 4081 || amuletId == 12017) ? (7F / 6F) : 1.2F;
        } else if (player.getSlayer().isCurrentAssignment(target) || CombatUtilities.isUndeadCombatDummy(target)) {
            val helmId = player.getEquipment().getId(EquipmentSlot.HELMET);
            val definitions = ItemDefinitions.get(helmId);
            val name = definitions == null ? null : definitions.getName().toLowerCase();
            if (name != null && (name.contains("black mask") || name.contains("slayer helm"))) {
                result *= (7F / 6F);
            }
        }

        result = Math.floor(result);
        result = Math.floor(result * passiveModifier);

        if (weaponId == 22545 && player.getWeapon().getCharges() > 1000 && target instanceof NPC && ((NPC) target).isInWilderness()) {
            result *= 1.5F;
        }

        val wieldingObsidianWeapon = ArrayUtils.contains(obsidianWeaponry, weaponId);
        if (wieldingObsidianWeapon && CombatUtilities.hasFullObisidian(player)) {
            result *= 1.1F;
        }

        result = Math.floor(result);

        if (!ignorePrayers) {
            if (target instanceof Player) {
                if (((Player) target).getPrayerManager().isActive(Prayer.PROTECT_FROM_MELEE)) {
                    result *= target.getMeleePrayerMultiplier();
                    result = Math.floor(result);
                }
            }
        }

        //Darklight or arclight
        if ((weaponId == 6746 || weaponId == 19675) && CombatUtilities.isDemon(target)) {
            result *= weaponId == 6746 ? 1.6F : 1.7F;
        }

        //Berserker necklace
        if ((amuletId == ItemId.BERSERKER_NECKLACE || amuletId == ItemId.BERSERKER_NECKLACE_OR) && wieldingObsidianWeapon) {
            result *= 1.2F;
        }

        //Dharok's set effect
        else if (CombatUtilities.hasFullBarrowsSet(player, "Dharok's")) {
            result *= CombatUtilities.getDharokModifier(player);
        }
        //Gadderhammer
        else if (weaponId == 7668 && CombatUtilities.isShade(target)) {
            result *= Utils.random(99) < 5 ? 2F : 1.25F;
        }
        //Keris
        else if (weaponId >= 10581 && weaponId <= 10584 && CombatUtilities.isKerisAffected(target)) {
            if (Utils.random(50) == 0) {
                result *= 3F;
                player.sendMessage("You slip your dagger through a chink in the creature's chitin, landing a vicious blow.");
            } else {
                result *= (4F / 3F);
            }
        }

        result *= activeModifier;

        result = Math.floor(result);

        //Castle wars bracelet effect
        if (player.getTemporaryAttributes().containsKey("castle wars bracelet effect")
                && player.inArea("Castle Wars")) {
            if (target instanceof Player) {
                val targetWeapon = ((Player) target).getEquipment().getId(EquipmentSlot.WEAPON);
                if (targetWeapon == 4037 || targetWeapon == 4039) {
                    result = Math.floor(result * 1.2F);
                }
            }
        }

        if (target instanceof Player) {
            val tp = ((Player) target);
            if (tp.getVariables().getTime(TickVariable.POWER_OF_DEATH) > 0) {
                if (weaponId == 11791 || weaponId == 12904) {
                   result *= 0.5F;
                }
            }
        }

        return (int) Math.floor(result);
    }

    @Override
    public final int getRandomHit(final Player player, final Entity target, final int maxhit, final double modifier) {
        return getRandomHit(player, target, maxhit, modifier, player.getCombatDefinitions().getAttackType());
    }

    private AttackType getSpecialType() {
        final SpecialAttack special = SpecialAttack.SPECIAL_ATTACKS.get(player.getEquipment().getId(EquipmentSlot.WEAPON.getSlot()));
        if (special == null) {
            return player.getCombatDefinitions().getAttackType();
        }
        return special.getAttackType();
    }

    @Override
    public int getRandomHit(final Player player, final Entity target, final int maxhit, final double modifier, final com.zenyte.game.world.entity.npc.combatdefs.AttackType oppositeIndex) {
        val type = player.getCombatDefinitions().getAttackExperienceType();
        val attackType = player.getCombatDefinitions().isUsingSpecial() ? getSpecialType() :
                player.getCombatDefinitions().getAttackType();

        float boost = CombatUtilities.hasFullMeleeVoid(player, true) ? 1.125F : CombatUtilities.hasFullMeleeVoid(player,
                false) ? 1.1F : 1F;

        val inquisCount = CombatUtilities.countInquisitorSet(player);
        boost += (inquisCount == 3 ? 0.025F : inquisCount == 2 ? 0.01F : inquisCount == 1 ? 0.005 : 0);

        val weaponId = player.getEquipment().getId(EquipmentSlot.WEAPON);
        if (weaponId == ItemId.DRAGON_HUNTER_LANCE && CombatUtilities.isDraconic(target)) {
            boost += 0.2F;
        }


        val a = Math.floor(Math.floor(player.getSkills().getLevel(Skills.ATTACK) * player.getPrayerManager().getSkillBoost(Skills.ATTACK))
                + (type == ATTACK_XP ? 3 : type == SHARED_XP ? 1 : 0) + 8F)
                * (boost);
        var b = player.getBonuses().getBonus(attackType.ordinal());
        if (player.getEquipment().getId(EquipmentSlot.AMULET) == ItemId.AMULET_OF_AVARICE && (CombatUtilities.isRevenant(target))) {
            b *= 1.20F;
        }
        var result = a * (b + 64F);
        val amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
        if ((amuletId == 4081 || amuletId == 12017 || amuletId == 10588 || amuletId == 12018) && CombatUtilities.SALVE_AFFECTED_NPCS.contains(name)) {
            result *= (amuletId == 4081 || amuletId == 12017) ? (7F / 6F) : 1.2F;
        } else if (player.getSlayer().isCurrentAssignment(target)) {
            val helmId = player.getEquipment().getId(EquipmentSlot.HELMET);
            val definitions = ItemDefinitions.get(helmId);
            val name = definitions == null ? null : definitions.getName().toLowerCase();
            if (name != null && (name.contains("black mask") || name.contains("slayer helm"))) {
                result *= (7F / 6F);
            }
        }

        result = Math.floor(result);
        result *= modifier;

        if (weaponId == 22545 && player.getWeapon().getCharges() > 1000 && target instanceof NPC && ((NPC) target).isInWilderness()) {
            if(player.getEquipment().getId(EquipmentSlot.AMULET) == ItemId.AMULET_OF_AVARICE && (CombatUtilities.isRevenant(target))) {
                result *= 1.7F;
            } else {
                result *= 1.5F;
            }
        } else if(player.getEquipment().getId(EquipmentSlot.AMULET) == ItemId.AMULET_OF_AVARICE && (CombatUtilities.isRevenant(target))) {
            result *= 1.2F;
        }

        if ((weaponId == 6746 || weaponId == 19675) && CombatUtilities.isDemon(target)) {
            result *= weaponId == 6746 ? 1.6F : 1.7F;
        }

        if (ArrayUtils.contains(obsidianWeaponry, weaponId) && CombatUtilities.hasFullObisidian(player)) {
            result *= 1.1F;
        }

        result = Math.floor(result);

        if (CombatUtilities.isCombatDummy(target)) {
            return maxhit;
        }
        val targetRoll = getTargetDefenceRoll(player, target, oppositeIndex);
        val accuracy = result > targetRoll ? (1F - (targetRoll + 2F) / (2F*(result + 1F))) : (result / (2F*(targetRoll + 1F)));
        sendDebug(accuracy, maxhit);
        if (accuracy < Utils.randomDouble()) {
            return 0;
        }
        return Utils.random(maxhit);
    }

    @Override
    public boolean process() {
        return initiateCombat(player);
    }

    @Override
    public int processWithDelay() {
        if (!target.startAttacking(player, CombatType.MELEE)) {
            return -1;
        }
        if (!isWithinAttackDistance()) {
            return 0;
        }
        if (!canAttack()) {
            return -1;
        }
        val area = player.getArea();
        if (area instanceof PlayerCombatPlugin) {
            ((PlayerCombatPlugin) area).onAttack(player, target, "Melee");
        }
        addAttackedByDelay(player, target);
        val delay = special();
        if (delay != -2) {
            player.putBooleanAttribute("used_special", true);
            return delay == SpecialAttackScript.WEAPON_SPEED ? getSpeed() : delay;
        }
        player.putBooleanAttribute("used_special", false);
        sendSoundEffect();
        val hit = getHit(player, target, 1, 1, 1, false);
        extra(hit);
        addPoisonTask(hit.getDamage(), 0);
        delayHit(0, hit);
        if (hit.getDamage() > 0 && player.getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.ARCLIGHT) {
            player.getChargesManager().removeCharges(player.getWeapon(), 1, player.getEquipment().getContainer(), EquipmentSlot.WEAPON.getSlot());
        }
        animate();
        player.getChargesManager().removeCharges(DegradeType.OUTGOING_HIT);
        resetFlag();
        addExtraEffect(hit);
        checkIfShouldTerminate();
        return getSpeed();
    }

    public void addExtraEffect(Hit hit) {
        val amulet = player.getAmulet();
        if (amulet == null) return;
        val amuletId = amulet.getId();
        switch(amuletId) {
            case ItemId.AMULET_OF_BLOOD_FURY_FULL:
            case ItemId.AMULET_OF_BLOOD_FURY_100:
            case ItemId.AMULET_OF_BLOOD_FURY_75:
            case ItemId.AMULET_OF_BLOOD_FURY_50:
            case ItemId.AMULET_OF_BLOOD_FURY_25:
                hit.onLand(ignored -> {
                    if (hit.getDamage() == 0)  {
                        return;
                    }
                    player.getChargesManager().removeCharges(amulet, 1, player.getEquipment().getContainer(), EquipmentSlot.AMULET.getSlot());
                    if (Utils.random(4) != 0) { //rolls 0-4 (5 numbers) --> 1/5 or 20%
                        return;
                    }
                    target.setGraphics(new Graphics(2002));
                    player.heal(Math.max((int) (hit.getDamage() * 0.3), 1));
                });
                break;
        }
    }
    protected void extra(final Hit hit) {

    }

    protected int special() {
        if (!player.getCombatDefinitions().isUsingSpecial())
            return -2;
        return useSpecial(player, SpecialType.MELEE);
    }

    @Override
    public boolean start() {
        extraSpace = isExtendedMeleeDistance(player) ? 1 : 0;
        player.setCombatEvent(new CombatEntityEvent(player, new PredictedEntityStrategy(target)));
        player.setLastTarget(target);
        if (target.getEntityType() == EntityType.NPC) {
            val npc = (NPC) target;
            val id = npc.getId();
            if (id >= 3162 && id <= 3183 || id == 7037 || id == 6587 || npc instanceof Dawn) {
                player.sendMessage("You cannot use melee against this creature.");
                return false;
            }
        }

        if (player.isFrozen()) {
            player.sendMessage("A magical force stops you from moving.");
        }


        player.setFaceEntity(target);
        if (initiateCombat(player)) {
            return true;
        }
        player.setFaceEntity(null);
        return false;
    }

    protected void addPoisonTask(final int damage, final int delay) {
        if (damage <= 0) {
            return;
        }
        val weapon = player.getEquipment().getItem(EquipmentSlot.WEAPON);
        if (weapon == null) {
            return;
        }
        val name = weapon.getName();
        if (CombatUtilities.isWearingSerpentineHelmet(player) && target.getEntityType() == EntityType.NPC) {
            if (Utils.random(!name.contains("(p") ? 5 : 1) == 0) {
                WorldTasksManager.scheduleOrExecute(() -> target.getToxins().applyToxin(ToxinType.VENOM, 6), delay);
            }
        } else {
            if (!name.contains("(p")) {
                return;
            }
            WorldTasksManager.scheduleOrExecute(() -> target.getToxins().applyToxin(ToxinType.POISON,
                    name.contains("p++") ? 6 : name.contains("p+") ? 5 : 4), delay);
        }
    }

    protected void animate() {
        val id = player.getEquipment().getAttackAnimation(player.getCombatDefinitions().getStyle());
        val animation = new Animation(getAttackAnimation(target instanceof Player, id == 393 ? (player.getShield() == null ? 414 : id) : id));
        player.setAnimation(animation);
    }

    protected boolean canAttack() {
        if (!attackable())
            return false;
        if (!target.canAttack(player)) {
            return false;
        }
        val area = player.getArea();
        if ((area instanceof EntityAttackPlugin && !((EntityAttackPlugin) area).attack(player, target))) {
            return false;
        }
        return (!(area instanceof PlayerCombatPlugin) || ((PlayerCombatPlugin) area).processCombat(player, target, "Melee")) && player.getControllerManager().processPlayerCombat(target, "Melee");
    }

    protected int getSpeed() {
        val weapon = player.getWeapon();
        if (weapon == null)
            return 3;
        return 9 - (weapon.getDefinitions().getAttackSpeed());
    }

    protected boolean initiateCombat(final Player player) {
        if (player.isDead() || player.isFinished() || player.isLocked() || player.isStunned() || player.isFullMovementLocked()) {
            return false;
        }
        if (target.isFinished() || target.isCantInteract() || target.isDead()) {
            return false;
        }
        val distanceX = player.getX() - target.getX();
        val distanceY = player.getY() - target.getY();
        val size = target.getSize();
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
        if (player.isFrozen() || player.isMovementLocked(false)) {
            return true;
        }

        if (!canInitiate()) {
            return false;
        }

        if (!target.hasWalkSteps() && Utils.collides(player.getX(), player.getY(), player.getSize(), target.getX(), target.getY(), target.getSize())) {
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
        player.resetWalkSteps();
        val nextLocation = target.getNextPosition(target.isRun() ? 2 : 1);
        if (player.isProjectileClipped(target, extraSpace <= 0 && !(player.getDuel() != null && player.getDuel().inDuel()))
                || !(withinRange(target, extraSpace, target.getSize()))
                || target.hasWalkSteps() &&
                (target instanceof Player
                        || !Utils.collides(player.getX(), player.getY(), player.getSize(), nextLocation.getX(), nextLocation.getY(), target.getSize()))) {
            appendWalksteps();
        }
        if (!player.hasWalkSteps() && !isWithinAttackDistance()) {
            player.sendMessage("I can't reach that!");
            return false;
        }
        return true;
    }

    protected boolean canInitiate() {
        return true;
    }

    protected boolean isWithinAttackDistance() {
        if (target.checkProjectileClip(player)
                && isProjectileClipped(true, extraSpace <= 0 && !(player.getDuel() != null && player.getDuel().inDuel()))) {
            return false;
        }
        val nextTile = target.getNextLocation();
        val tile = nextTile != null ? nextTile : target.getLocation();
        val distanceX = player.getX() - tile.getX();
        val distanceY = player.getY() - tile.getY();
        val size = target.getSize();
        var maxDistance = extraSpace;
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

    protected void resetFlag() {
        if (!minimapFlag) {
            return;
        }
        player.getPacketDispatcher().resetMapFlag();
        minimapFlag = false;
    }

    protected void sendSoundEffect() {
        val weaponId = player.getEquipment().getId(EquipmentSlot.WEAPON);
        val sound = CombatSoundEffect.getSound(weaponId);
        if (sound == null) {
            val fallbackSound = CombatSoundEffect.getDefaultSoundEffect(weaponId);
            if (fallbackSound == null) {
                return;
            }
            World.sendSoundEffect(new Location(player.getLocation()), fallbackSound);
            return;
        }
        World.sendSoundEffect(new Location(player.getLocation()), sound.getSound());
    }
}
