package com.zenyte.game.world.entity.npc.impl.kalphite;

import com.zenyte.game.content.achievementdiary.diaries.DesertDiary;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.NPCDrops;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 7 apr. 2018 | 18:23:45
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public class KalphiteQueen extends NPC implements CombatScript, Spawnable {

    private static final int PHASE1_NPC = 963, PHASE2_NPC = 965;
    private static final Graphics PHASE_SWITCH_GFX = new Graphics(1055);
    private static final Animation PHASE_SWITCH_ANIM = new Animation(6270);
    private static final Projectile MAGIC_PROJ_P1 = new Projectile(280, 50, 30, 60, 15, 28, 10, 5);
    private static final Projectile MAGIC_PROJ_P2 = new Projectile(280, 80, 30, 60, 15, 28, 10, 5);
    private static final Projectile RANGED_PROJ_P1 = new Projectile(288, 40, 30, 35, 15, 8, 10, 5);
    private static final Projectile RANGED_PROJ_P2 = new Projectile(289, 40, 30, 35, 15, 8, 10, 5);

    private static final Graphics MAGIC_GFX_P1 = new Graphics(278);
    private static final Graphics MAGIC_GFX_P2 = new Graphics(279);
    private static final Graphics MAGIC_HIT_GFX = new Graphics(281);

    private static final Animation MAGIC_ANIM_P1 = new Animation(1173);
    private static final Animation MAGIC_ANIM_P2 = new Animation(6234);
    private static final Animation RANGED_ANIM_P1 = new Animation(6240);
    private static final Animation RANGED_ANIM_P2 = new Animation(6234);

    private int ticks;

    public KalphiteQueen(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
        this.maxDistance = 50;
        this.radius = 50;
        this.attackDistance = 10;
        this.aggressionDistance = 15;
    }

    @Override
    public int getRespawnDelay() {
        return 50;
    }

    @Override
    public void setRespawnTask() {
        if (!isFinished()) {
            reset();
            finish();
        }
        WorldTasksManager.schedule(() -> {
            setId(PHASE1_NPC);
            checkMultiArea();
            updateLocation();
            spawn();
        }, getRespawnDelay());
        this.getTemporaryAttributes().remove("verac's hits only");
    }

    protected void drop(final Location tile) {
        val killer = getDropRecipient();
        if (killer == null) {
            return;
        }
        onDrop(killer);
        val processors = DropProcessorLoader.get(id);
        if (processors != null) {
            for (val processor : processors) {
                processor.onDeath(this, killer);
            }
        }
        val drops = NPCDrops.getTable(getId());
        if (drops == null) {
            return;
        }
        NPCDrops.forEach(drops, drop -> dropItem(killer, drop, tile));
        val item = Utils.getRandomElement(supplies);
        dropItem(killer, item, tile, false);
    }

    private static final Item[] supplies = new Item[]{
            new Item(7946, 3),
            new Item(385, 2),
            new Item(11936, 2),
            new Item(6685),
            new Item(2434, 2),
            new Item(3024),
            new Item(12699),
            new Item(169),
            new Item(183)
    };

    @Override
    public boolean isTolerable() {
        return false;
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (id == PHASE2_NPC) {
            if (getCombat().underCombat()) {
                ticks = 0;
            }
            if (++ticks >= 50) {
                setTransformation(PHASE1_NPC);
                cancelCombat();
            }
        }
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        if ((id == PHASE1_NPC && (hit.getHitType().equals(HitType.RANGED) || hit.getHitType().equals(HitType.MAGIC))) || id == PHASE2_NPC && hit.getHitType().equals(HitType.MELEE) && !hit.containsAttribute("ignore protect melee")) {
            hit.setDamage(hit.getDamage() / 2);
        }
        val player = (Player) hit.getSource();
        if (!player.getBooleanAttribute("elite-combat-achievement60")) {
            val weapon = player.getWeapon().getName().toLowerCase();
            if (!(weapon.contains("verac"))) {
                this.getTemporaryAttributes().put("verac's hits only", false);
            }
        }
        super.handleIngoingHit(hit);
    }

    @Override
    public float getXpModifier(final Hit hit) {
        if ((id == PHASE1_NPC && (hit.getHitType().equals(HitType.RANGED) || hit.getHitType().equals(HitType.MAGIC))) || id == PHASE2_NPC && hit.getHitType().equals(HitType.MELEE) && !hit.containsAttribute("ignore protect melee")) {
            return 0;
        }
        return 1;
    }

    @Override
    public void sendDeath() {
        val source = getMostDamagePlayerCheckIronman();
        if (id == PHASE2_NPC) {
            super.sendDeath();
            if (source != null) {
                source.getAchievementDiaries().update(DesertDiary.KILL_KALPHITE_QUEEN);
                source.getNotificationSettings().increaseKill("Kalphite Queen");
                source.getNotificationSettings().sendBossKillCountNotification("Kalphite Queen");
                val defLevel = super.getCombatDefinitions().getStatDefinitions().getCombatStats()[2];
                if (defLevel < 300 && !source.getBooleanAttribute("hard-combat-achievement37")) {
                    source.putBooleanAttribute("hard-combat-achievement37", true);
                    HardTasks.sendHardCompletion(source, 37);
                }
                if (source.getTemporaryAttributes().getOrDefault("vengeance_hit", false).equals(true) && !source.getBooleanAttribute("elite-combat-achievement38")) {
                    source.putBooleanAttribute("elite-combat-achievement38", true);
                    EliteTasks.sendEliteCompletion(source, 38);
                }
                if (!this.getTemporaryAttributes().containsKey("verac's hits only") && !source.getBooleanAttribute("elite-combat-achievement60")) {
                    source.putBooleanAttribute("elite-combat-achievement60", true);
                    EliteTasks.sendEliteCompletion(source, 60);
                }
            }
        } else {
            setCantInteract(true);
            setAnimation(getCombatDefinitions().getDeathAnim());
            lock();
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;
                KalphiteQueen queen;

                @Override
                public void run() {
                    if (ticks == 1) {
                        val stats = combatDefinitions.getStatDefinitions().getCombatStats();
                        reset();
                        finish();
                        queen = new KalphiteQueen(PHASE2_NPC, getLocation(), Direction.SOUTH, 4);//TODO fix direction
                        queen.setDirection(getDirection());
                        queen.spawn();
                        queen.lock();
                        queen.setCantInteract(true);
                        queen.setGraphics(PHASE_SWITCH_GFX);
                        queen.setAnimation(PHASE_SWITCH_ANIM);
                        queen.combatDefinitions.getStatDefinitions().setCombatStats(stats);
                    } else if (ticks == 8) {
                        if (queen == null) {
                            return;
                        }
                        queen.unlock();
                        queen.setCantInteract(false);
                        stop();
                    }
                    ticks++;
                }

            }, 0, 1);
        }
    }

    @Override
    public int attack(final Entity target) {
        val random = Utils.random(100);
        String style;
        if (random < 50) {
            style = "Magic";
        } else if (random < 80) {
            style = "Ranged";
        } else {
            style = "Melee";
            if (!isWithinMeleeDistance(this, target)) {
                //calcFollow(target, -1, true, false, false);
                style = Utils.random(1) == 0 ? "Magic" : "Ranged";
            }
        }
        //getCombatDefinitions().setAttackStyle(style);
        switch (style) {
            case "Magic": {
                val phase1 = getId() == PHASE1_NPC;
                val projectile = phase1 ?
                                 MAGIC_PROJ_P1 :
                                 MAGIC_PROJ_P2;
                setAnimation(phase1 ?
                             MAGIC_ANIM_P1 :
                             MAGIC_ANIM_P2);
                setGraphics(phase1 ?
                            MAGIC_GFX_P1 :
                            MAGIC_GFX_P2);
                getPossibleTargets(EntityType.PLAYER).forEach((player) -> {
                    delayHit(this, World.sendProjectile(this, player, projectile), player, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), AttackType.MAGIC,
                            target), HitType.MAGIC).onLand(hit -> player.setGraphics(MAGIC_HIT_GFX)));
                });
            }
            break;
            case "Ranged":
                val phase1 = getId() == PHASE1_NPC;
                val projectile = phase1 ?
                                 RANGED_PROJ_P1 :
                                 RANGED_PROJ_P2;
                setAnimation(phase1 ?
                             RANGED_ANIM_P1 :
                             RANGED_ANIM_P2);
                getPossibleTargets(EntityType.PLAYER).forEach((player) -> delayHit(this, World.sendProjectile(this, player, projectile), player, new Hit(this, Utils.random(getCombatDefinitions().getMaxHit()), HitType.RANGED)));
                break;
            default:
                setAnimation(getCombatDefinitions().getAttackAnim());
                delayHit(this, 0, target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MELEE, target), HitType.MELEE));
                break;
        }
        return getCombatDefinitions().getAttackSpeed();
    }

    @Override
    public boolean validate(final int id, final String name) {
        return name.equals("kalphite queen");
    }

}
