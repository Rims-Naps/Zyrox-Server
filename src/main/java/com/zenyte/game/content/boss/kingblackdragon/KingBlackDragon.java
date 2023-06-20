package com.zenyte.game.content.boss.kingblackdragon;

import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.Toxins;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.npc.impl.slayer.dragons.Dragonfire;
import com.zenyte.game.world.entity.npc.impl.slayer.dragons.DragonfireProtection;
import com.zenyte.game.world.entity.npc.impl.slayer.dragons.DragonfireType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import lombok.val;

/**
 * @author Kris | 23. apr 2018 : 15:46.37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class KingBlackDragon extends NPC implements Spawnable, CombatScript {

    private boolean wasHitByStabWeapon;

    public KingBlackDragon(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
        this.aggressionDistance = 64;
        this.maxDistance = 64;
        this.attackDistance = 10;
        wasHitByStabWeapon = true;
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        if (wasHitByStabWeapon) {
            val source = hit.getSource();
            if (source instanceof Player) {
                val player = (Player) hit.getSource();
                if (player.getCombatDefinitions().getAttackType() != AttackType.STAB) {
                    wasHitByStabWeapon = false;
                }
            }
        }
        super.handleIngoingHit(hit);
    }

    @Override
    public int getRespawnDelay() {
        return BossRespawnTimer.KING_BLACK_DRAGON.getTimer().intValue();
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return name.equals("king black dragon");
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                if (player.getPrayerManager().isActive(Prayer.PROTECT_FROM_MELEE) && !player.getBooleanAttribute("medium-combat-achievement21")) {
                    player.putBooleanAttribute("medium-combat-achievement21", true);
                    MediumTasks.sendMediumCompletion(player, 21);
                }
                if ((DragonfireProtection.getProtection(player).contains(DragonfireProtection.SUPER_ANTIFIRE_POTION)
                        || DragonfireProtection.getProtection(player).contains(DragonfireProtection.ANTIFIRE_POTION))
                        && (DragonfireProtection.getProtection(player).contains(DragonfireProtection.ANTI_DRAGON_SHIELD)
                        || DragonfireProtection.getProtection(player).contains(DragonfireProtection.DRAGONFIRE_SHIELD))
                        && !player.getBooleanAttribute("medium-combat-achievement30")) {
                    player.putBooleanAttribute("medium-combat-achievement30", true);
                    MediumTasks.sendMediumCompletion(player, 30);
                }
                if (wasHitByStabWeapon && !player.getBooleanAttribute("medium-combat-achievement31")) {
                    player.putBooleanAttribute("medium-combat-achievement31", true);
                    MediumTasks.sendMediumCompletion(player, 31);
                }
                wasHitByStabWeapon = true;
                if (player.getNumericAttribute("kbd_kc_on_instance_creation").intValue() + 9 <= player.getNotificationSettings().getKillcount("king black dragon")
                        && !player.getBooleanAttribute("hard-combat-achievement46")
                        && player.getArea() instanceof KingBlackDragonInstance) {
                    player.putBooleanAttribute("hard-combat-achievement46", true);
                    HardTasks.sendHardCompletion(player, 46);
                }
            }
        }
    }

    private static final Projectile DRAGONFIRE_PROJ = new Projectile(393, 40, 30, 40, 15, 28, 0, 5);
    private static final Projectile POISON_PROJ = new Projectile(394, 40, 30, 40, 15, 28, 0, 5);
    private static final Projectile FREEZING_PROJ = new Projectile(395, 40, 30, 40, 15, 28, 0, 5);
    private static final Projectile SHOCKING_PROJ = new Projectile(396, 40, 30, 40, 15, 28, 0, 5);

    private static final Graphics DRAGONFIRE_GFX = new Graphics(430, 0, 90);
    private static final Graphics POISON_GFX = new Graphics(429, 0, 90);
    private static final Graphics FREEZING_GFX = new Graphics(431, 0, 90);
    private static final Graphics SHOCKING_GFX = new Graphics(428, 0, 90);

    private static final Animation ATTACK_ANIM = new Animation(80);
    private static final Animation SECONDARY_ATTACK_ANIM = new Animation(91);
    private static final Animation DRAGONFIRE_ANIM = new Animation(81);

    @Override
    public int attack(final Entity target) {
        if (!(target instanceof Player))
            return 0;
        val npc = this;
        val player = (Player) target;
        final int random = Utils.random(isWithinMeleeDistance(npc, target) ? 2 : 1);
        if (random == 0) {
            npc.setAnimation(DRAGONFIRE_ANIM);
            World.sendProjectile(npc, target, DRAGONFIRE_PROJ);
            val perk = player.getPerkManager().isValid(PerkWrapper.BACKFIRE);
            val modifier = !perk ? 1 : Math.max(0, Utils.randomDouble() - 0.25F);
            val dragonfire = new Dragonfire(DragonfireType.STRONG_DRAGONFIRE, 65,
                    DragonfireProtection.getProtection(player));
            val deflected = !perk ? 0 : ((int) Math.floor(dragonfire.getMaximumDamage() * modifier));
            delayHit(npc, DRAGONFIRE_PROJ.getTime(npc, target), target,
                    new Hit(npc, Utils.random(Math.max(0, dragonfire.getDamage() - deflected)), HitType.REGULAR).onLand(hit -> {
                        player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's fiery breath"));
                        PlayerCombat.appendDragonfireShieldCharges(player);
                        target.setGraphics(DRAGONFIRE_GFX);
                        if (perk) {
                            dragonfire.backfire(npc, player, 0, deflected);
                        }
                    }));
        } else if (random == 2) {
            if (Utils.random(1) == 0) {
                npc.setAnimation(ATTACK_ANIM);
            } else {
                npc.setAnimation(SECONDARY_ATTACK_ANIM);
            }
            delayHit(npc, 0, target, new Hit(npc, getRandomMaxHit(npc, 25, MELEE, target), HitType.MELEE));
        } else {
            val atk = Utils.random(2);
            switch (atk) {
                case 0: {
                    npc.setAnimation(DRAGONFIRE_ANIM);
                    World.sendProjectile(npc, target, POISON_PROJ);
                    val perk = player.getPerkManager().isValid(PerkWrapper.BACKFIRE);
                    val modifier = !perk ? 1 : Math.max(0, Utils.randomDouble() - 0.25F);

                    val dragonfire = new Dragonfire.DragonfireBuilder(DragonfireType.STRONG_DRAGONFIRE, 65,
                            DragonfireProtection.getProtection(player)) {
                        @Override
                        public int getDamage() {
                            val tier = getAccumulativeTier();
                            return tier == 0F ? 65 : tier == 0.25F ? 60 : tier == 0.5F ? 35 : tier == 0.75F ? 25 : 10;
                        }
                    };
                    val deflected = !perk ? 0 : ((int) Math.floor(dragonfire.getMaximumDamage() * modifier));

                    delayHit(npc, POISON_PROJ.getTime(npc, target), target,
                            new Hit(npc, Utils.random(Math.max(0, dragonfire.getDamage() - deflected)), HitType.REGULAR).onLand(hit -> {
                                player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's poisonous breath"));
                                if (Utils.random(3) == 0) {
                                    target.getToxins().applyToxin(Toxins.ToxinType.POISON, 8);
                                }
                                if (perk) {
                                    dragonfire.backfire(npc, player, 0, deflected);
                                }
                                target.setGraphics(POISON_GFX);
                                PlayerCombat.appendDragonfireShieldCharges(player);
                            }));
                    break;
                }
                case 1: {
                    npc.setAnimation(DRAGONFIRE_ANIM);
                    World.sendProjectile(npc, target, FREEZING_PROJ);
                    val perk = player.getPerkManager().isValid(PerkWrapper.BACKFIRE);
                    val modifier = !perk ?
                                   1 :
                                   Math.max(0, Utils.randomDouble() - 0.25F);

                    val dragonfire = new Dragonfire.DragonfireBuilder(DragonfireType.STRONG_DRAGONFIRE, 65,
                            DragonfireProtection.getProtection(player)) {
                        @Override
                        public int getDamage() {
                            val tier = getAccumulativeTier();
                            return tier == 0F ? 65 : tier == 0.25F ? 60 : tier == 0.5F ? 35 : tier == 0.75F ? 25 : 10;
                        }
                    };
                    val deflected = !perk ? 0 : ((int) Math.floor(dragonfire.getMaximumDamage() * modifier));
                    delayHit(npc, FREEZING_PROJ.getTime(npc, target), target, new Hit(npc, Utils.random(Math.max(0, dragonfire.getDamage() - deflected)), HitType.REGULAR).onLand(hit -> {
                        target.setGraphics(FREEZING_GFX);
                        PlayerCombat.appendDragonfireShieldCharges(player);
                        player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's icy breath"));
                        if (perk) {
                            dragonfire.backfire(npc, player, 0, deflected);
                        }
                        if (Utils.random(3) == 0) {
                            player.freeze(16, 0, entity -> player.sendMessage("The dragon's icy attack freezes you."));
                        }
                    }));
                    break;
                }
                case 2: {
                    npc.setAnimation(DRAGONFIRE_ANIM);
                    World.sendProjectile(npc, target, SHOCKING_PROJ);
                    val perk = player.getPerkManager().isValid(PerkWrapper.BACKFIRE);
                    val modifier = !perk ?
                                   1 :
                                   Math.max(0, Utils.randomDouble() - 0.25F);
                    val dragonfire = new Dragonfire.DragonfireBuilder(DragonfireType.STRONG_DRAGONFIRE, 65,
                            DragonfireProtection.getProtection(player)) {
                        @Override
                        public int getDamage() {
                            val tier = getAccumulativeTier();
                            return tier == 0F ? 65 : tier == 0.25F ? 60 : tier == 0.5F ? 35 : tier == 0.75F ? 25 : 10;
                        }
                    };
                    val deflected = !perk ? 0 : ((int) Math.floor(dragonfire.getMaximumDamage() * modifier));
                    delayHit(npc, SHOCKING_PROJ.getTime(npc, target), target, new Hit(npc, Utils.random(Math.max(0, dragonfire.getDamage() - deflected)), HitType.REGULAR).onLand(hit -> {
                        target.setGraphics(SHOCKING_GFX);
                        PlayerCombat.appendDragonfireShieldCharges(player);
                        player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's shocking breath"));
                        if (perk) {
                            dragonfire.backfire(npc, player, 0, deflected);
                        }
                        if (Utils.random(3) == 0) {
                            player.getSkills().drainCombatSkills(2);
                            player.sendMessage("The dragon's shocking attack drains your stats.");
                        }
                    }));
                    break;
                }
            }
        }
        return 4;
    }
}
