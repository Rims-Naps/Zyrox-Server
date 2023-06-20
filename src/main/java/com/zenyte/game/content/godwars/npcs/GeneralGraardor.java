package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.godwars.instance.BandosInstance;
import com.zenyte.game.content.godwars.instance.SaradominInstance;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 25. okt 2017 : 11:17.15
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public class GeneralGraardor extends GodwarsBossNPC implements Spawnable, CombatScript {

    private int perfectMinionKills;

    private static final ForceTalk[] quotes = new ForceTalk[]{
            new ForceTalk("Death to our enemies!"), new ForceTalk("Brargh!"), new ForceTalk("Break their bones!"), new ForceTalk("For the glory of Bandos!"),
            new ForceTalk("Split their skulls!"), new ForceTalk("We feast on the bones of our enemies tonight!"), new ForceTalk("CHAAARGE"), new ForceTalk("Crush them underfoot!"),
            new ForceTalk("All glory to Bandos!"), new ForceTalk("GRRRAAAAAR!"), new ForceTalk("FOR THE GLORY OF THE BIG HIGH WAR GOD!")
    };

    public GeneralGraardor(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
        if (isAbstractNPC() || tile.getX() >= 6400) return;
        setMinions(new GodwarsBossMinion[]{
                new GodwarsBossMinion(NpcId.SERGEANT_STRONGSTACK, new Location(2868, 5362, 2), Direction.SOUTH, 5),
                new SergeantSteelwill(NpcId.SERGEANT_STEELWILL, new Location(2872, 5354, 2), Direction.SOUTH, 5),
                new SergeantGrimspike(NpcId.SERGEANT_GRIMSPIKE, new Location(2871, 5359, 2), Direction.SOUTH, 5),
        });
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source instanceof Player) {
            val player = (Player) source;
            int deathCount = 0;
            val minions = super.minions;
            for (int i = 0; i < 3; i++) {
                val minion = minions[i];
                if (minion.isDead() || minion.isFinished() || minion.getHitpoints() == 0) {
                    deathCount++;
                }
                if (deathCount >= 3 && !player.getBooleanAttribute("hard-combat-achievement23")) {
                    player.putBooleanAttribute("hard-combat-achievement23", true);
                    HardTasks.sendHardCompletion(player, 23);
                }
            }
            if ((super.isStunned() || super.isFrozen() || super.isMovementRestricted()) && !player.getBooleanAttribute("hard-combat-achievement26")) {
                player.putBooleanAttribute("hard-combat-achievement26", true);
                HardTasks.sendHardCompletion(player, 26);
            }
            if (!player.getBooleanAttribute("elite-combat-achievement35") && !player.getBooleanAttribute("has_taken_damage_from_bandos")) {
                player.putBooleanAttribute("elite-combat-achievement35", true);
                EliteTasks.sendEliteCompletion(player, 35);
            }
            if (!player.getBooleanAttribute("grandmaster-combat-achievement8")
                    && !player.getBooleanAttribute("has_taken_damage_during_bandos_from_minions")
                    && !player.getBooleanAttribute("has_taken_damage_from_bandos")) {
                player.putBooleanAttribute("grandmaster-combat-achievement8", true);
                GrandmasterTasks.sendGrandmasterCompletion(player, 8);
            }
            if (!player.getBooleanAttribute("grandmaster-combat-achievement9")
                    && !player.getBooleanAttribute("has_taken_damage_during_bandos_from_minions")) {
                perfectMinionKills++;
            } else {
                perfectMinionKills = 0;
            }
            if (perfectMinionKills == 2) {
                player.putBooleanAttribute("grandmaster-combat-achievement9", true);
                GrandmasterTasks.sendGrandmasterCompletion(player, 9);
            }
            if (player.getAttributes().containsKey("graardor_kc_on_instance_creation")) {
                if (player.getNumericAttribute("graardor_kc_on_instance_creation").intValue() + 14 <= player.getNotificationSettings().getKillcount("general graardor")
                        && !player.getBooleanAttribute("grandmaster-combat-achievement33")
                        && player.getArea() instanceof BandosInstance) {
                    player.putBooleanAttribute("grandmaster-combat-achievement33", true);
                    GrandmasterTasks.sendGrandmasterCompletion(player, 33);
                }
            }

        }
    }

    @Override
    BossRespawnTimer timer() {
        return BossRespawnTimer.GENERAL_GRAARDOR;
    }

    public GeneralGraardor(final GodwarsBossMinion[] minions, final int id, final Location tile, final Direction direction, final int radius) {
        this(id, tile, direction, radius);
        setMinions(minions);
    }

    @Override
    ForceTalk[] getQuotes() {
        return quotes;
    }

    @Override
    int diaryFlag() {
        return 0x1;
    }

    @Override
    public GodType type() {
        return GodType.BANDOS;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == NpcId.GENERAL_GRAARDOR;
    }

    private static final Projectile projectile = new Projectile(1202, 41, 16, 30, 5, 10, 0, 5);
    private static final Animation meleeAnimation = new Animation(7018);
    private static final Animation rangedAnimation = new Animation(7021);

    @Override
    public int attack(final Entity target) {
        val npc = this;
        if (Utils.random(2) != 0) {
            npc.setAnimation(meleeAnimation);
            delayHit(npc, 0, target, new Hit(npc, getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), MELEE, target), HitType.MELEE));
        } else {
            npc.setAnimation(rangedAnimation);
            for (final Entity t : npc.getPossibleTargets(EntityType.PLAYER)) {
                int damage = getRandomMaxHit(npc, 35, RANGED, t);
                //graardor deals a minimum of 15 damage upon successful hit; for even distribution, we re-calc it.
                if (damage > 0) {
                    damage = Utils.random(15, 35);
                }
                delayHit(npc, World.sendProjectile(npc, t, projectile), t, new Hit(npc, damage, HitType.RANGED));
            }
        }
        return npc.getCombatDefinitions().getAttackSpeed();
    }

    @Override
    public NPC spawn() {
        for (val player : this.getPossibleTargets(EntityType.PLAYER)) {
            val p = (Player) player;
            p.putBooleanAttribute("has_taken_damage_from_bandos", false);
            p.putBooleanAttribute("has_taken_damage_during_bandos_from_minions", false);
        }
        return super.spawn();
    }
}
