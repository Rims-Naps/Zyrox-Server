package com.zenyte.game.world.entity.player.action.combat;

import com.google.common.collect.ImmutableList;
import com.zenyte.Constants;
import com.zenyte.Game;
import com.zenyte.game.content.chambersofxeric.npc.IceDemon;
import com.zenyte.game.content.skills.hunter.npc.ImplingNPC;
import com.zenyte.game.content.skills.magic.spells.MagicSpell;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Entity.EntityType;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.npc.combatdefs.StatType;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.AttackStyle.AttackExperienceType;
import com.zenyte.game.world.entity.player.action.combat.magic.*;
import com.zenyte.game.world.entity.player.action.combat.melee.*;
import com.zenyte.game.world.entity.player.action.combat.ranged.*;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.area.apeatoll.Greegree;
import com.zenyte.game.world.region.area.plugins.HitProcessPlugin;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.item.DragonfireShield;
import com.zenyte.utils.ProjectileUtils;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.val;
import mgi.types.config.enums.EnumDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.LongPredicate;

import static com.zenyte.game.world.entity.player.action.combat.AmmunitionDefinitions.*;

public abstract class PlayerCombat extends Action {

    public static final List<Integer> INSTANT_SPEC_WEAPONS = ImmutableList.of(4153, 12848, 20557, 1377, 11920, 12797, 13242, 13243, 13244, 20014, 6739, 13241, 20011, 21028, 21031, 21033, 20849, 21207,
            35, 11791, 12904, 11037, 22296);

    /**
     * Animations which stall when in PvP combat, but do not stall when in PvM combat.
     */
    public static final Int2IntMap nonstallingToStallingAnimations = Int2IntMaps.unmodifiable(new Int2IntOpenHashMap() {{
        put(5248, 5245);
        put(387, 3044);
        put(4410, 8147);
        put(7554, 6600);
        put(7552, 4230);
        put(7555, 7218);
        put(7617, 929);
        put(7618, 2779);
        put(7558, 2614);
        put(7557, 4199);
        put(7556, 7222);
    }});

    public static final Int2IntMap stallingToNonStallingAnimations = Int2IntMaps.unmodifiable(new Int2IntOpenHashMap() {{
        for (val entry : nonstallingToStallingAnimations.int2IntEntrySet()) {
            put(entry.getIntValue(), entry.getIntKey());
        }
    }});

    public static final int getAttackAnimation(final boolean pvp, final int animationId) {
        if (pvp) {
            return nonstallingToStallingAnimations.getOrDefault(animationId, animationId);
        }
        return stallingToNonStallingAnimations.getOrDefault(animationId, animationId);
    }
//added 3rd age bow here

    private static final String[] SPECIAL_RANGED_AMMUNITION = new String[]{"knife", "thrownaxe", "dart", "throwing " +
            "axe", "morrigan's javelin", "chinchompa", "toktz-xil-ul", "holy water", "mud pie", "crystal bow",
            "starter bow",
            "craw's bow", "bow of faerdhinen", "bow of faerdhinen (c)"};
    private static final String[] RANGED_WEAPONS = new String[]{"bow", "javelin", "thrownaxe", "throwing axe",
            "knife", "chinchompa", "toktz-xil-ul", "holy water", "dart", "ballista", "blowpipe", "seercull", "mud " +
            "pie"};
    private static final IntSet nonRangedWeapons = new IntOpenHashSet(new int[] {
            ItemId.KITCHEN_KNIFE
    });
    private static final Animation DRAGONFIRE_SPECIAL_ANIM = new Animation(6696);
    private static final Graphics DRAGONFIRE_START_GFX = new Graphics(1165);
    private static final Graphics DRAGONFIRE_HIT_GFX = new Graphics(1167, 0, 96);
    private static final Projectile DRAGONFIRE_PROJ = new Projectile(1166, 25, 25, 80, 15, 10, 0, 5);
    private static final Graphics WYVERN_DRAGONFIRE_START_GFX = new Graphics(1401);
    private static final Projectile WYVERN_DRAGONFIRE_PROJ = new Projectile(500, 25, 25, 80, 15, 10, 0, 5);
    private static final Graphics WYVERN_DRAGONFIRE_HIT_GFX = new Graphics(367, 0, 96);
    private static final EnumDefinitions SPECIAL_ENUM = EnumDefinitions.get(906);
    @Getter
    protected final String name;
    @Getter
    protected Entity target;
    protected float accuracyModifier = 1;
    protected float maxhitModifier = 1;
    protected boolean minimapFlag;

    public PlayerCombat(final Entity target) {
        this.target = target;
        name = target.getEntityType() == EntityType.NPC ? ((NPC) target).getDefinitions().getName().toLowerCase() : null;
    }

    @Override
    protected void onInterruption() {

    }

    private static final boolean isSpecialRangedAmmunition(final int id, final String name) {
        if (nonRangedWeapons.contains(id)) {
            return false;
        }
        for (final String s : SPECIAL_RANGED_AMMUNITION) {
            if (name.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private static final boolean isRangedWeapon(final int id, final String name) {
        if (nonRangedWeapons.contains(id)) {
            return false;
        }
        for (final String s : RANGED_WEAPONS) {
            if (name.contains(s)) {
                return true;
            }
        }
        return false;
    }

    protected final void sendDebug(final double accuracy, final int maximumHit) {
        if (player.getTemporaryAttributes().get("combat debug") == Boolean.TRUE) {
            player.sendMessage("Accuracy: " + accuracy + ", maximum hit: " + maximumHit);
        }
    }

    public static final void appendDragonfireShieldCharges(final Player player) {
        val id = player.getEquipment().getId(EquipmentSlot.SHIELD);
        if (id == 11283 || id == 11284 || id == 22002 || id == 22003 || id == 21633 || id == 21634) {
            val charges = player.getShield().getCharges();
            if (charges < 50) {
                player.setAnimation(new Animation(6695));
                player.setGraphics(new Graphics(1164));

                val shield = player.getShield();
                shield.setCharges(charges + 1);
                if (id == 11284) {
                    shield.setId(11283);
                } else if (id == 22003) {
                    shield.setId(22002);
                } else if (id == 21634) {
                    shield.setId(21633);
                }
                player.getEquipment().refresh(EquipmentSlot.SHIELD.getSlot());
            }
        }
    }

    private static final boolean withinGraniteMaulTriggerDistance(@NotNull final Player player, @NotNull final Position targetPosition, final int targetSize) {
        val target = targetPosition.getPosition();
        val distanceX = player.getX() - target.getX();
        val distanceY = player.getY() - target.getY();
        val npcSize = player.getSize();
        int maximumDistance = 0;
        if (player.hasWalkSteps()) {
            maximumDistance += player.isRun() ? 2 : 1;
        }
        return !(distanceX > targetSize + maximumDistance
                || distanceY > targetSize + maximumDistance
                || distanceX < -npcSize - maximumDistance
                || distanceY < -npcSize - maximumDistance);
    }

    public static final void performInstantSpecial(final Player player) {
        final Item weapon = player.getWeapon();
        if (weapon == null) {
            return;
        }
        switch (weapon.getId()) {
            case 4153:
            case 12848:
            case 20557:
                if (player.getCombatDefinitions().getSpecialEnergy() < 50) {
                    player.getCombatDefinitions().setSpecial(false, true);
                    player.sendMessage("You don't have enough special energy.");
                    return;
                }
                val currentCycle = Game.getCurrentCycle();
                val attribute = player.getTemporaryAttributes().get("cached granite maul specials");
                val cachedMaulSpecials = attribute instanceof LongArrayList ? (LongArrayList) attribute : new LongArrayList();
                player.addTemporaryAttribute("cached granite maul specials", cachedMaulSpecials);
                val usingSpecial = player.getCombatDefinitions().isUsingSpecial();
                //If this special reference turned special attack off(thus being a second click)
                if (!usingSpecial) {
                    //And if the last time the special was clicked was at least one game tick ago
                    if (!cachedMaulSpecials.isEmpty()) {
                        if (cachedMaulSpecials.getLong(cachedMaulSpecials.size() - 1) < currentCycle) {
                            //Let's clear out any cached specials as the user has toggled special off intentionally.
                            cachedMaulSpecials.clear();
                            return;
                        }
                    }
                }

                //Any 'cached' special attack that happened in the past shall be removed.
                cachedMaulSpecials.removeIf((LongPredicate) value -> value < currentCycle);
                cachedMaulSpecials.add(currentCycle);
                val actionManager = player.getActionManager();
                if (usingSpecial && cachedMaulSpecials.size() == 1 && actionManager.getActionDelay() <= 0 && !actionManager.wasInCombatThisTick()) {
                    player.sendFilteredMessage("Warning: Since the maul's special attack is an instant attack, it will be wasted when used on a first strike.");
                }
                player.addPostProcessRunnable(() -> {
                    val lastTarget = player.getLastTarget();
                    if (lastTarget == null || lastTarget.isNulled() || lastTarget.isDead() || lastTarget.isFinished()) {
                        return;
                    }
                    //If the player has no pending combat delay from the last attack, the maul will not go off immediately.
                    if (actionManager.getActionDelay() <= 0 && !actionManager.wasInCombatThisTick()) {
                        return;
                    }
                    if (player.getActionManager().getAction() instanceof PlayerCombat || !withinGraniteMaulTriggerDistance(player, lastTarget, lastTarget.getSize())) {
                        return;
                    }
                    //If there's an odd number of special attacks, re-initiate combat towards the last target.
                    if ((cachedMaulSpecials.size() & 0x1) == 0x1) {
                        PlayerCombat.attackEntity(player, lastTarget, null);
                    }
                });
                return;
            case 20849:
            case 21207:
                if (player.getCombatDefinitions().getSpecialEnergy() < 25) {
                    player.sendMessage("You don't have enough special energy.");
                    player.getCombatDefinitions().setSpecial(false, true);
                    return;
                }
                if (player.getCombatDefinitions().isUsingSpecial()) {
                    player.getTemporaryAttributes().put("dragonThrownaxe", player.getActionManager().getActionDelay());
                    if (player.getActionManager().getActionDelay() > 0) {
                        player.getActionManager().setActionDelay(0);
                    }
                } else {
                    val delay = player.getTemporaryAttributes().get("dragonThrownaxe");
                    if (delay instanceof Integer) {
                        val del = (int) delay;
                        if (del > player.getActionManager().getActionDelay()) {
                            player.getActionManager().setActionDelay(del);
                        }
                    }
                }
                return;
            case 11037:
                if (!player.getInterfaceHandler().isVisible(169)) {
                    player.sendMessage("You need to be underwater to use the special attack.");
                    player.getCombatDefinitions().setSpecial(false, true);
                    return;
                }
                return;
            default:
                useInstantSpecial(player);
        }
    }

    public static final void magicAttack(final Player player, final Entity entity, final CombatSpell spell, final boolean autocast) {
        val isFreezingImpling = (spell == CombatSpell.BIND || spell == CombatSpell.SNARE || spell == CombatSpell.ENTANGLE) && entity instanceof ImplingNPC;
        if (!isFreezingImpling && !entity.canAttack(player)) {
            return;
        }
        val castType = autocast ? MagicCombat.CastType.AUTO_CAST : MagicCombat.CastType.MANUAL_CAST;
        if (spell == CombatSpell.TRIDENT_OF_THE_SEAS) {
            player.getActionManager().setAction(new SeasTridentCombat(entity, spell, castType));
            return;
        } else if (spell == CombatSpell.TRIDENT_OF_THE_SWAMP) {
            player.getActionManager().setAction(new SwampTridentCombat(entity, spell, castType));
            return;
        } else if (spell == CombatSpell.SANGUINESTI_STAFF) {
            player.getActionManager().setAction(new SanguinestiStaffCombat(entity, spell, castType));
            return;
        } else if (spell == CombatSpell.STARTER_STAFF) {
            player.getActionManager().setAction(new StarterStaffCombat(entity, spell, castType));
            return;
        }
        player.getActionManager().setAction(new MagicCombat(entity, spell, castType));
    }

    public final boolean isExtendedMeleeDistance(final Player player) {
        val weapon = player.getWeapon();
        if (weapon == null) {
            return false;
        }
        if(getTarget() instanceof NPC) {
            NPC targetNpc = (NPC) getTarget();
            if(targetNpc.getNextTransformation() == NpcId.VERZIK_VITUR_8370) {
                return true;
            }
        }
        return weapon.getName().toLowerCase().contains("halberd") || weapon.getName().toLowerCase().contains("scythe of vitur");
    }

    public int getTileDistance(final boolean predicting) {
        val fromTile = player.getNextPosition(!predicting ? 0 : player.isRun() ? 2 : 1);
        val distanceX = fromTile.getX() - target.getX();
        val distanceY = fromTile.getY() - target.getY();
        val size = target.getSize();
        val distX = distanceX > size ? (distanceX - size) : distanceX < -1 ? (distanceX + 1) : 0;
        val distY = distanceY > size ? (distanceY - size) : distanceY < -1 ? (distanceY + 1) : 0;
        return Math.max(Math.abs(distX), Math.abs(distY));
    }

    abstract int getAttackDistance();

    protected final boolean colliding() {
        if (target.hasWalkSteps())
            return false;
        val distanceX = player.getX() - target.getX();
        val distanceY = player.getY() - target.getY();
        val size = target.getSize();
        return distanceX < size && distanceX > -1 && distanceY < size && distanceY > -1;
    }

    protected boolean isProjectileClipped(final boolean checkFurther, final boolean checkMelee) {
        return ProjectileUtils.isProjectileClipped(player, target, player.getNextPosition(checkFurther ? (player.isRun() ? 2 : 1) : 0), target.getLocation(), checkMelee, target.ignoreUnderneathProjectileCheck());
    }

    protected boolean pathfind() {
        val maxDistance = getAttackDistance();
        player.resetWalkSteps();

        if (target.checkProjectileClip(player) && isProjectileClipped(false, false)
                || !withinRange(target, maxDistance, target.getSize())
                || (target.hasWalkSteps() && !withinRange(target.getNextPosition(target.isRun() ? 2 : 1), maxDistance, target.getSize()))) {
            appendWalksteps();
        }
        return true;
    }

    protected void checkIfShouldTerminate() {
        if (CombatUtilities.isCombatDummy(target)) {
            WorldTasksManager.schedule(() -> {
                if (player.getActionManager().getAction() == this) {
                    player.getActionManager().forceStop();
                }
            });
        }
    }

    protected final void appendWalksteps() {
        minimapFlag = true;
        player.getCombatEvent().process();
    }

    protected boolean isWithinAttackDistance() {
        val checkProjectile = target.checkProjectileClip(player);
        if (checkProjectile && ProjectileUtils.isProjectileClipped(player, target, player.getLocation(), target, false)) {
            return false;
        }
        int maxDistance = getAttackDistance();
        val nextLocation = target.getLocation();
        if ((player.isFrozen() || player.isStunned())
                && (Utils.collides(player.getX(), player.getY(), player.getSize(), nextLocation.getX(), nextLocation.getY(), target.getSize())
                || !withinRange(target, maxDistance, target.getSize()))) {
            return false;
        }
        final int distanceX = player.getX() - target.getX();
        final int distanceY = player.getY() - target.getY();
        final int size = target.getSize();
        return distanceX <= size + maxDistance && distanceX >= -1 - maxDistance && distanceY <= size + maxDistance && distanceY >= -1 - maxDistance;
    }

    protected void extra() {

    }

    protected void resetFlag() {
        if (!minimapFlag) {
            return;
        }
        player.getPacketDispatcher().resetMapFlag();
        minimapFlag = false;
    }

    abstract int fireProjectile();

    protected void notifyIfFrozen() {
        if (!player.isFrozen()) {
            return;
        }
        val distanceX = player.getX() - target.getX();
        val distanceY = player.getY() - target.getY();
        val size = target.getSize();
        val maxDistance = getAttackDistance();
        val projectileClipped = player.isProjectileClipped(target, false);
        if (projectileClipped || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance
                || distanceY < -1 - maxDistance) {
            player.sendMessage("A magical force stops you from moving.");
        }
    }

    public static final void attackEntity(final Player player, final Entity entity, final MagicSpell spell) {
        val weapon = player.getWeapon();
        if (!player.getControllerManager().canAttack(entity)) {
            return;
        }
        if (player.isLocked()) {
            return;
        }

        player.faceEntity(entity);

        if (entity instanceof NPC) {
            val npc = (NPC) entity;
            if (npc.getCombatDefinitions().getSlayerLevel() > Math.max(1, player.getSkills().getLevel(Skills.SLAYER))) {
                player.sendMessage("Your Slayer level is not high enough to harm this monster.");
                return;
            }
        }

        if (weapon != null) {
            if (weapon.getId() == 21015) {
                if (spell != null) {
                    player.sendMessage("Your bulwark gets in the way.");
                    return;
                }
                if (player.getCombatDefinitions().getStyle() != 0) {
                    player.sendMessage("You cannot attack with the Dinh's bulwark using block!");
                    return;
                }
            }
            if (Greegree.MAPPED_VALUES.get(weapon.getId()) != null) {
                player.getDialogueManager().start(new PlainChat(player, "You cannot attack as a monkey."));
                return;
            }
        }
        if (spell != null) {
            if (!(spell instanceof CombatSpell)) {
                return;
            }
            val combatSpell = (CombatSpell) spell;
            magicAttack(player, entity, combatSpell, false);
            return;
        }
        if (weapon == null || weapon.getId() == 6818 || weapon.getId() == ItemId.HUNTING_KNIFE) {
            player.getActionManager().setAction(new MeleeCombat(entity));
            return;
        } else if (player.getCombatDefinitions().getAutocastSpell() != null) {
            magicAttack(player, entity, player.getCombatDefinitions().getAutocastSpell(), true);
            return;
        }
        if (weapon.getId() == 22335) {
            magicAttack(player, entity, CombatSpell.STARTER_STAFF, true);
            return;
        }

        if (weapon.getId() == 22323) {
            magicAttack(player, entity, CombatSpell.SANGUINESTI_STAFF, true);
            return;
        } else if (weapon.getId() == 32314) {
            magicAttack(player, entity, CombatSpell.SANGUINESTI_STAFF, true);
            return;
        } else if (weapon.getId() == 11905 || weapon.getId() == 11907 || weapon.getId() == ItemId.TRIDENT_OF_THE_SEAS_E) {
            magicAttack(player, entity, CombatSpell.TRIDENT_OF_THE_SEAS, true);
            return;
        } else if (weapon.getId() == 11908 || weapon.getId() == 22290) {
            player.sendMessage("The weapon has no charges left. You need death runes, chaos runes, fire runes and coins to charge it.");
            return;
        } else if (weapon.getId() == ItemId.TRIDENT_OF_THE_SWAMP || weapon.getId() == ItemId.TRIDENT_OF_THE_SWAMP_E) {
            magicAttack(player, entity, CombatSpell.TRIDENT_OF_THE_SWAMP, true);
            return;
        } else if (weapon.getId() == ItemId.DAWNBRINGER) {
            magicAttack(player, entity, CombatSpell.DAWNBRINGER_STAFF, true);
            return;
        }
        final String weaponName = weapon.getName().toLowerCase();
        final int ammo = player.getEquipment().getId(isSpecialRangedAmmunition(weapon.getId(), weaponName) ? EquipmentSlot.WEAPON.getSlot() : EquipmentSlot.AMMUNITION.getSlot());
        final AmmunitionDefinitions defs = AmmunitionDefinitions.getDefinitions(ammo);
        if (ArrayUtils.contains(SpecialAttack.SHOVE.getWeapons(), weapon.getId())) {
            player.getActionManager().setAction(new DragonSpearCombat(entity));
        } else if (weaponName.endsWith("ballista")) {
            player.getActionManager().setAction(new JavelinRangedCombat(entity, defs));
        } else if (weaponName.equals("dragon hunter crossbow")) {
            player.getActionManager().setAction(new DragonHunterCrossbowCombat(entity, defs));
        } else if (weaponName.equals("dragon thrownaxe")) {
            player.getActionManager().setAction(new DragonThrownaxeCombat(entity, defs));
        } else if (weaponName.contains("karil")) {
            player.getActionManager().setAction(new KarilsRangedCombat(entity, defs));
        } else if (weaponName.contains("crystal bow")) {
            player.getActionManager().setAction(new CrystalBowRangedCombat(entity, defs));
        } else if (weaponName.equals("starter bow")) {
            player.getActionManager().setAction(new StarterBowRangedCombat(entity, STARTER_ARROW));
        } else if (weaponName.contains("bow of faerdhinen")) {
            /*player.getActionManager().setAction(new StarterBowRangedCombat(entity, STARTER_ARROW));
        } else if (weaponName.contains("3rd age bow")) {*/
            player.getActionManager().setAction(new CrawsBowCombat(entity, AmmunitionDefinitions.CRAWS_BOW_ARROW));
        } else if (weaponName.contains("craw's bow")) {
            player.getActionManager().setAction(new CrawsBowCombat(entity, AmmunitionDefinitions.CRAWS_BOW_ARROW));
        } else if ((weaponName.contains("salamander") || weaponName.equals("swamp lizard"))) {
            player.getActionManager().setAction(new SalamanderCombat(entity, defs));
        } else if (weaponName.equals("toxic blowpipe")) {
            player.getActionManager().setAction(new BlowpipeRangedCombat(entity, defs));
        } else if (weaponName.equals("dark bow")) {
            player.getActionManager().setAction(new DarkBowRangedCombat(entity, defs));
        } else if (defs == RED_CHINCHOMPA || defs == BLACK_CHINCHOMPA || defs == GREY_CHINCHOMPA) {
            player.getActionManager().setAction(new ChinchompaRangedCombat(entity, defs));
        } else if (isRangedWeapon(weapon.getId(), weaponName)) {
            player.getActionManager().setAction(new RangedCombat(entity, defs));
        } else if (weaponName.equals("granite maul")) {
            player.getActionManager().setAction(new GraniteMaulCombat(entity));
        } else if (weaponName.contains("scythe of vitur") || weaponName.equalsIgnoreCase("christmas scythe")) {
            player.getActionManager().setAction(new ScytheOfViturCombat(entity));
        } else if (weaponName.equals("dinh's bulwark")) {
            player.getActionManager().setAction(new DinhsBulwarkCombat(entity));
        } else if (weaponName.contains("guthan")) {
            player.getActionManager().setAction(new GuthanCombat(entity));
        } else if (weaponName.contains("verac")) {
            player.getActionManager().setAction(new VeracCombat(entity));
        } else if (weaponName.contains("torag")) {
            player.getActionManager().setAction(new ToragCombat(entity));
        } else {
            player.getActionManager().setAction(new MeleeCombat(entity));
        }
    }

    public static void addAttackedByDelay(final Entity player, final Entity target) {
        //Yes, pj timer is 4.8 seconds in osrs; Confirmed. Dark bow fights can be interfered by PJing cus dbow attack speed is longer.
        target.setAttackedBy(player);
        target.setAttackedByDelay(Utils.currentTimeMillis() + 4800);
        if (player instanceof Player) {
            if (target instanceof Player) {
                val var = ((Player) player).getVarManager();
                if (var.getValue(1075) != target.getIndex()) {
                    var.sendVar(1075, target.getIndex());
                }
            }
        }
    }

    public static void addAttackingDelay(final Entity player) {
        player.setAttackingDelay(Utils.currentTimeMillis() + 4800);
    }

    protected static final int getRequiredSpecial(final Player player) {
        final Item weapon = player.getWeapon();
        if (weapon == null) {
            return 0;
        }
        return SPECIAL_ENUM.getIntValue(weapon.getId()) / 10;
    }

    public static final void useInstantSpecial(final Player player) {
        final SpecialAttack special = SpecialAttack.SPECIAL_ATTACKS.get(player.getEquipment().getId(EquipmentSlot.WEAPON.getSlot()));
        if (special == null) {
            player.getCombatDefinitions().setSpecial(false, true);
            return;
        }
        final int specialEnergy = getRequiredSpecial(player);
        if (player.getCombatDefinitions().getSpecialEnergy() < specialEnergy) {
            player.sendMessage("You don't have enough special energy.");
            player.getCombatDefinitions().setSpecial(false, true);
            return;
        }
        player.getCombatDefinitions().setSpecial(false, true);
        player.getCombatDefinitions().setSpecialEnergy(player.getCombatDefinitions().getSpecialEnergy() - specialEnergy);
        player.setAnimation(special.getAnimation());
        if (special.getGraphics() != null) {
            player.setGraphics(special.getGraphics());
        }
        special.getAttack().attack(player, null, null);
    }

    public static final int getTargetDefenceRoll(final Entity attacker, final Entity target, AttackType type) {
        double effectiveLevel;
        int equipmentBonus;
        if (target.getEntityType() == EntityType.PLAYER) {
            val t = (Player) target;
            effectiveLevel = t.getSkills().getLevel(Skills.DEFENCE);
            //We multiply by all prayer bonuses since you can only have one styles' prayers enabled at once, making
            //it safe to do so.
            effectiveLevel *= t.getPrayerManager().getSkillBoost(Skills.DEFENCE);
            effectiveLevel *= t.getPrayerManager().getRangedBoost(Skills.DEFENCE);
            effectiveLevel = Math.floor(effectiveLevel);

            val attackType = t.getCombatDefinitions().getAttackExperienceType();
            effectiveLevel += (attackType == AttackExperienceType.DEFENCE_XP || attackType == AttackExperienceType.MAGIC_DEFENCE_XP || attackType == AttackExperienceType.RANGED_DEFENCE_XP) ? 3 : attackType == AttackExperienceType.SHARED_XP ? 1 : 0;
            effectiveLevel += 8;

            equipmentBonus = t.getBonuses().getDefenceBonus(type);

            val amuletId = t.getEquipment().getId(EquipmentSlot.AMULET);
            if (equipmentBonus > 0 && (amuletId == 12851 || amuletId == 12853) && CombatUtilities.hasFullBarrowsSet(t, "Torag's")) {
                val missingHealth = t.getMaxHitpoints() - t.getHitpoints();
                float defaultPercentage = 1;
                defaultPercentage += (missingHealth / 100F);
                equipmentBonus *= defaultPercentage;
            }

            if (type == AttackType.MAGIC) {
                effectiveLevel = Math.floor(effectiveLevel * 0.3F);
                double magicEffectiveDefence = t.getSkills().getLevel(Skills.MAGIC);
                magicEffectiveDefence *= t.getPrayerManager().getMagicBoost(Skills.DEFENCE);
                magicEffectiveDefence = Math.floor(magicEffectiveDefence);
                magicEffectiveDefence *= 0.7F;

                if (attacker.getEntityType() == EntityType.PLAYER) {
                    val player = (Player) attacker;
                    val ringId = player.getEquipment().getId(EquipmentSlot.RING);
                    if (ringId == ItemId.BRIMSTONE_RING && Utils.random(3) == 0) {
                        player.sendFilteredMessage(Colour.RED.wrap("You have reduced your opponent's magic defense."));
                        magicEffectiveDefence *= 0.9;
                    }
                }
                effectiveLevel += magicEffectiveDefence;
            }

            val bonus = effectiveLevel * (equipmentBonus + 64);
            if (attacker instanceof Player) {
                return (int) (bonus * Constants.defenceMultiplier);
            }
            return (int) bonus;
        } else {
            val npc = (NPC) target;
            effectiveLevel = npc.getCombatDefinitions().getStatDefinitions().get(type == AttackType.MAGIC && !(npc instanceof IceDemon) ? StatType.MAGIC : StatType.DEFENCE);
            equipmentBonus = npc.getCombatDefinitions().getStatDefinitions().get(StatType.getDefenceType(type));
            if (type == AttackType.MAGIC && attacker.getEntityType() == EntityType.PLAYER) {
                val player = (Player) attacker;
                val ringId = player.getEquipment().getId(EquipmentSlot.RING);
                if (ringId == ItemId.BRIMSTONE_RING && Utils.random(3) == 0) {
                    player.sendFilteredMessage(Colour.RED.wrap("You have reduced your opponent's magic defense."));
                    effectiveLevel *= 0.9;
                }
            }
        }
        return (int) (effectiveLevel * (equipmentBonus + 64));
    }

    @Override
    public void stop() {
        val faceEntity = player.getFaceEntity();
        val lastDelay = player.getLastFaceEntityDelay();
        WorldTasksManager.schedule(() -> {
            if (player.getFaceEntity() == faceEntity && player.getLastFaceEntityDelay() == lastDelay) {
                player.setFaceEntity(null);
            }
        });
    }

    public abstract Hit getHit(Player player, final Entity target, final double accuracyModifier, final double passiveModifier, double activeModifier, final boolean ignorePrayers);

    public abstract int getRandomHit(Player player, final Entity target, final int maxhit, final double modifier);

    public abstract int getRandomHit(Player player, final Entity target, final int maxhit, final double modifier, final AttackType oppositeIndex);

    public abstract int getMaxHit(final Player player, final double passiveModifier, double activeModifier, final boolean ignorePrayers);

    public final void delayHit(final int delay, final Hit... hits) {
        delayHit(target, delay, hits);
    }

    @Override
    public boolean interruptedByCombat() {
        return false;
    }

    private final SoundEffect DRAGONFIRE_SHIELD_START_SOUND = new SoundEffect(3761, 10, 0);
    private final SoundEffect DRAGONFIRE_SHIELD_END_SOUND = new SoundEffect(161, 10, -1);
    private final SoundEffect ANCIENT_WYVERN_SHIELD_END_SOUND = new SoundEffect(170, 10, -1);

    protected final boolean handleDragonfireShields(final Player player, final boolean execute) {
        val temporaryAttributes = player.getTemporaryAttributes();
        if (temporaryAttributes.get("dragonfireBurst") == null) {
            return false;
        }

        if (!ArrayUtils.contains(DragonfireShield.DRAGONFIRE_SHIELDS, player.getEquipment().getId(EquipmentSlot.SHIELD))) {
            temporaryAttributes.remove("dragonfireBurst");
            return false;
        }
        if (player.getNumericTemporaryAttribute("dragonfireBurstDelay").longValue() > Utils.currentTimeMillis()) {
            temporaryAttributes.remove("dragonfireBurst");
            player.sendMessage("Your shield hasn't finished recharging yet.");
            return false;
        }

        if (!Utils.isOnRange(player.getX(), player.getY(), player.getSize(), target.getX(), target.getY(), target.getSize(), 10)) {
            return false;
        }
        if (!execute) {
            return true;
        }
        temporaryAttributes.remove("dragonfireBurst");
        val shield = player.getEquipment().getItem(EquipmentSlot.SHIELD);
        val isWyvernShield = shield.getName().equals("Ancient wyvern shield");
        temporaryAttributes.put("dragonfireBurstDelay", Utils.currentTimeMillis() + 120000);
        World.sendSoundEffect(player, DRAGONFIRE_SHIELD_START_SOUND);
        player.setAnimation(DRAGONFIRE_SPECIAL_ANIM);
        player.setGraphics(isWyvernShield ? WYVERN_DRAGONFIRE_START_GFX : DRAGONFIRE_START_GFX);
        player.getChargesManager().removeCharges(shield, 1, player.getEquipment().getContainer(),
                EquipmentSlot.SHIELD.getSlot());
        val clientTicks =
                (isWyvernShield ? WYVERN_DRAGONFIRE_PROJ : DRAGONFIRE_PROJ).getProjectileDuration(player.getLocation(),
                        target);
        val areaSound = isWyvernShield ? ANCIENT_WYVERN_SHIELD_END_SOUND : DRAGONFIRE_SHIELD_END_SOUND;
        World.sendSoundEffect(target, new SoundEffect(areaSound.getId(),
                areaSound.getRadius(), clientTicks));
        World.scheduleProjectile(player, target, isWyvernShield ? WYVERN_DRAGONFIRE_PROJ : DRAGONFIRE_PROJ).schedule(() -> {
            target.setGraphics(isWyvernShield ? WYVERN_DRAGONFIRE_HIT_GFX : DRAGONFIRE_HIT_GFX);
            val damage = getRandomHit(player, target, 25, 1, AttackType.MAGIC);
            val hit = new Hit(player, damage, HitType.REGULAR);
            hit.putAttribute("dfs special", true);
            delayHit(0, hit);
            if (isWyvernShield && damage > 0) {
                target.freezeWithNotification(8);
            }
        });
        return true;
    }

    protected final boolean attackable() {
        if (target.isMultiArea())
            return true;
        if (!player.canAttackInSingleZone(target)) {
            player.sendMessage("You are already in combat.");
            return false;
        } else if (!target.canAttackInSingleZone(player)) {
            player.sendMessage("That " + (target instanceof Player ? "player" : "npc") + " is already in combat.");
            return false;
        }
        return true;
    }

    protected boolean withinRange(final Position targetPosition, int maximumDistance, final int targetSize) {
        val target = targetPosition.getPosition();
        val distanceX = player.getX() - target.getX();
        val distanceY = player.getY() - target.getY();
        val npcSize = player.getSize();

        if (player.hasWalkSteps()) {
            maximumDistance += player.isRun() ? 2 : 1;
        }

        if (distanceX == -npcSize - maximumDistance && distanceY == -npcSize - maximumDistance
                || distanceX == targetSize + maximumDistance && distanceY == targetSize + maximumDistance
                || distanceX == -npcSize - maximumDistance && distanceY == targetSize + maximumDistance
                || distanceX == targetSize + maximumDistance && distanceY == -npcSize - maximumDistance) {
            return false;
        }

        return !(distanceX > targetSize + maximumDistance
                || distanceY > targetSize + maximumDistance
                || distanceX < -npcSize - maximumDistance
                || distanceY < -npcSize - maximumDistance);
    }

    public final int useSpecial(final Player player, final SpecialType type) {
        final SpecialAttack special = SpecialAttack.SPECIAL_ATTACKS.get(player.getEquipment().getId(EquipmentSlot.WEAPON.getSlot()));
        if (special == null) {
            player.getCombatDefinitions().setSpecial(false, true);
            return -2;
        }
        final int specialEnergy = getRequiredSpecial(player);
        if (player.getCombatDefinitions().getSpecialEnergy() < specialEnergy) {
            player.sendMessage("You don't have enough special energy.");
            player.getCombatDefinitions().setSpecial(false, true);
            return -2;
        }
        if (special.getType() != type) {
            return -2;
        }
        player.setAnimation(special.getAnimation());
        if (special.getGraphics() != null) {
            player.setGraphics(special.getGraphics());
        }
        special.getAttack().attack(player, this, target);
        player.getCombatDefinitions().setSpecial(false, true);
        player.getCombatDefinitions().setSpecialEnergy(player.getCombatDefinitions().getSpecialEnergy() - specialEnergy);
        return special.getDelay();
    }

    public final void delayHit(final Entity target, int delay, final Hit... hits) {
        if (hits.length > 0 && hits[0] != null) {
            addAttackedByDelay(hits[0].getSource(), target);
            addAttackingDelay(hits[0].getSource());
        }
        if (target.getEntityType() == EntityType.PLAYER) {
            if (player.getPid() < ((Player) target).getPid()) {
                delay -= 1;
            }
        }

        val area = player.getArea();
        
        for (final Hit hit : hits) {
            if (hit == null) {
                continue;
            }
            if (hit.getWeapon() == null) {
                hit.setWeapon(player.getWeapon());
            }
            if (target instanceof NPC) {
                val npc = (NPC) target;
                val cap = npc.getDamageCap();
                if (cap >= 0 && hit.getDamage() > cap) {
                    hit.setDamage(cap);
                }
            }
            val xpModifier = target.getXpModifier(hit);
            if (area instanceof HitProcessPlugin) {
                if (!((HitProcessPlugin) area).hit(player, target, hit, xpModifier)) {
                    continue;
                }
            }
            final int damage = hit.getDamage() > target.getHitpoints() ?
                    target.getHitpoints() :
                    hit.getDamage();
            final AttackStyle style = player.getCombatDefinitions().getAttackStyle();
            player.getSkills().addXp(Skills.HITPOINTS, damage * 1.33f * xpModifier, true, player.isIronman() && target instanceof Player);
            AttackExperienceType type = style.getExperienceType();
            if (player.getCombatDefinitions().getAutocastSpell() != null) {
                if (player.getCombatDefinitions().isDefensiveAutocast()) {
                    type = AttackExperienceType.MAGIC_DEFENCE_XP;
                } else {
                    type = AttackExperienceType.MAGIC_XP;
                }
            } else if (hit.getHitType() == HitType.MAGIC && (type != AttackExperienceType.MAGIC_XP && type != AttackExperienceType.MAGIC_DEFENCE_XP)) {
                type = AttackExperienceType.MAGIC_XP;
            }
            if (hit.containsAttribute("dfs special")) {
                type = target instanceof Player ? AttackExperienceType.DEFENCE_XP : AttackExperienceType.MAGIC_DEFENCE_XP;
            }
            switch (type) {
                case ATTACK_XP:
                    player.getSkills().addXp(Skills.ATTACK, 4 * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    break;
                case STRENGTH_XP:
                    player.getSkills().addXp(Skills.STRENGTH, 4 * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    break;
                case DEFENCE_XP:
                    player.getSkills().addXp(Skills.DEFENCE, 4 * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    break;
                case RANGED_XP:
                    player.getSkills().addXp(Skills.RANGED, 4 * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    break;
                case MAGIC_XP:
                    player.getSkills().addXp(Skills.MAGIC, 2 * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    break;
                case RANGED_DEFENCE_XP:
                    player.getSkills().addXp(Skills.RANGED, 2 * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    player.getSkills().addXp(Skills.DEFENCE, 2 * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    break;
                case MAGIC_DEFENCE_XP:
                    player.getSkills().addXp(Skills.MAGIC, 1.33f * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    player.getSkills().addXp(Skills.DEFENCE, damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    break;
                case SHARED_XP:
                    player.getSkills().addXp(Skills.ATTACK, 1.33f * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    player.getSkills().addXp(Skills.STRENGTH, 1.33f * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    player.getSkills().addXp(Skills.DEFENCE, 1.33f * damage * xpModifier, true, player.isIronman() && target instanceof Player);
                    break;
                default:
                    break;
            }
        }

        for (val hit : hits) {
            target.scheduleHit(hit.getSource(), hit, delay - 1);
        }
    }

    public final void attackTarget(final Set<Entity> targets, final MultiAttack perform) {
        final Entity realTarget = target;
        for (final Entity t : targets) {
            target = t;
            if (!perform.attack(realTarget)) {
                break;
            }
        }
        target = realTarget;
    }

    public final Set<Entity> getMultiAttackTargets(final Player player) {
        return getMultiAttackTargets(player, 1, 9);
    }

    public Set<Entity> getMultiAttackTargets(final Player player, final int maxDistance, final int maxAmtTargets) {
        val multi = target.isMultiArea();
        Set<Entity> possibleTargets = new ObjectOpenHashSet<>();
        possibleTargets.add(target);
        if (multi) {
            val targets = CharacterLoop.find(target.getLocation(), 1, Entity.class, entity -> isPotentialTarget(player, target.getLocation(), entity));
            val tile = target.getLocation();
            for (int i = targets.size() - 1; i >= 0; i--) {
                val e = targets.get(i);
                if (e == player || e == target) {
                    continue;
                }
                val t = e.getLocation();
                if (!t.withinDistance(tile.getX(), tile.getY(), 1))
                    continue;
                if (e instanceof Player) {
                    if (!player.canHit((Player) e))
                        continue;
                }
                possibleTargets.add(e);
                if (possibleTargets.size() >= maxAmtTargets) {
                    break;
                }
            }
        }
        return possibleTargets;
    }

    protected boolean isPotentialTarget(final Entity source, final Location tile, final Entity entity) {
        val entityX = entity.getX();
        val entityY = entity.getY();
        val entitySize = entity.getSize();

        val x = tile.getX();
        val y = tile.getY();
        val size = 1;

        return entity != source && !entity.isDead() && !entity.isMaximumTolerance() && (entity.isMultiArea() || entity.getAttackedBy() == source) && (!ProjectileUtils.isProjectileClipped(null, entity, tile, entity.getLocation(), false, true)
                || Utils.collides(x, y, size, entityX, entityY, entitySize)) && (!(entity instanceof NPC) || ((NPC) entity).isAttackableNPC()) && (!(entity instanceof Player) || ((Player) entity).isCanPvp());
    }

    public interface MultiAttack {
        boolean attack(final Entity originalTarget);
    }

}
