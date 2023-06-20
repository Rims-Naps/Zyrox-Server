package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.godwars.instance.SaradominInstance;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import lombok.val;

/**
 * @author Tommeh | 26 mrt. 2018 : 16:55:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class CommanderZilyana extends GodwarsBossNPC implements Spawnable, CombatScript {

    private boolean everyHitWasMelee = true;
    private boolean wasRicochet;

    private static final ForceTalk[] quotes = new ForceTalk[]{
            new ForceTalk("Death to the enemies of the light!"), new ForceTalk("Slay the evil ones!"), new ForceTalk("Saradomin lend me strength!"), new ForceTalk("By the power of Saradomin!"),
            new ForceTalk("May Saradomin be my sword!"), new ForceTalk("Good will always triumph!"), new ForceTalk("Forward! Our allies are with us!"), new ForceTalk("Saradomin is with us!"),
            new ForceTalk("In the name of Saradomin!"), new ForceTalk("All praise Saradomin!"), new ForceTalk("Attack! Find the Godsword!")
    };

    public CommanderZilyana(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
        if (isAbstractNPC() || tile.getX() >= 6400) return;
        setMinions(new GodwarsBossMinion[]{
                new GodwarsBossMinion(NpcId.STARLIGHT, new Location(2901, 5264, 0), Direction.SOUTH, 5),
                new Growler(NpcId.GROWLER, new Location(2897, 5263, 0), Direction.SOUTH, 5),
                new Bree(NpcId.BREE, new Location(2895, 5265, 0), Direction.SOUTH, 5),
        });
        everyHitWasMelee = true;
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
            }
            if (deathCount >= 3 && !player.getBooleanAttribute("hard-combat-achievement22")) {
                player.putBooleanAttribute("hard-combat-achievement22", true);
                HardTasks.sendHardCompletion(player, 22);
            }
            if (everyHitWasMelee && !player.getBooleanAttribute("elite-combat-achievement54")) {
                player.putBooleanAttribute("elite-combat-achievement54", true);
                EliteTasks.sendEliteCompletion(player, 54);
            }
            if (wasRicochet && !player.getBooleanAttribute("master-combat-achievement37")) {
                player.putBooleanAttribute("master-combat-achievement37", true);
                MasterTasks.sendMasterCompletion(player, 37);
            }
            if (!player.getBooleanAttribute("grandmaster-combat-achievement7")
                    && !player.getBooleanAttribute("has_taken_damage_during_zilyana")) {
                player.putBooleanAttribute("grandmaster-combat-achievement7", true);
                GrandmasterTasks.sendGrandmasterCompletion(player, 7);
            }
            if (player.getAttributes().containsKey("zilyana_kc_on_instance_creation")) {
                if (player.getNumericAttribute("zilyana_kc_on_instance_creation").intValue() + 49 <= player.getNotificationSettings().getKillcount("commander zilyana")
                        && !player.getBooleanAttribute("grandmaster-combat-achievement32")
                        && player.getArea() instanceof SaradominInstance) {
                    player.putBooleanAttribute("grandmaster-combat-achievement32", true);
                    GrandmasterTasks.sendGrandmasterCompletion(player, 32);
                }
            }
        }
        everyHitWasMelee = true;
    }

    @Override
    BossRespawnTimer timer() {
        return BossRespawnTimer.COMMANDER_ZILYANA;
    }

    public CommanderZilyana(final GodwarsBossMinion[] minions, final int id, final Location tile, final Direction direction, final int radius) {
        this(id, tile, direction, radius);
        setMinions(minions);
        everyHitWasMelee = true;
    }

    @Override
    ForceTalk[] getQuotes() {
        return quotes;
    }

    @Override
    int diaryFlag() {
        return 0x2;
    }

    @Override
    public GodType type() {
        return GodType.SARADOMIN;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == NpcId.COMMANDER_ZILYANA;
    }

    private static final Animation meleeAnimation = new Animation(6967);
    private static final Animation magicAnimation = new Animation(6970);
    private static final Graphics magicGraphics = new Graphics(1221);
    private static final SoundEffect meleeAttackSound = new SoundEffect(3876, 10, 0);
    private static final SoundEffect specialHittingSound = new SoundEffect(3887, 10, 30);

    @Override
    public int attack(final Entity target) {
        val npc = this;
        val style = Utils.random(2);
        if (style < 2) {
            npc.setAnimation(meleeAnimation);
            World.sendSoundEffect(getMiddleLocation(), meleeAttackSound);
            delayHit(npc, 0, target, new Hit(npc, getRandomMaxHit(npc, 31, MELEE, target), HitType.MELEE));
        } else {
            npc.freeze(2);
            npc.setAnimation(magicAnimation);
            for (val t : npc.getPossibleTargets(EntityType.PLAYER)) {
                int damage = getRandomMaxHit(npc, 20, MAGIC, t);
                //zilyana deals a minimum of 10 damage upon successful hit; for even distribution, we re-calc it.
                if (damage > 0) {
                    damage = Utils.random(10, 20);
                }
                delayHit(npc, 0, t, new Hit(npc, damage, HitType.MAGIC).onLand(hit -> t.setGraphics(magicGraphics)));
                World.sendSoundEffect(new Location(target.getLocation()), specialHittingSound);
            }
        }
        return npc.getCombatDefinitions().getAttackSpeed();
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        val player = (Player) hit.getSource();
        if (hitpoints == this.getMaxHitpoints()) {
            wasRicochet = true;
        }
        if (!player.getBooleanAttribute("elite-combat-achievement54")) {
            val type = hit.getHitType();
            if (type != HitType.MELEE) {
                everyHitWasMelee = false;
            }
        }
        if (hit.getAttributes().getOrDefault("notMainFocusedTarget", false).equals(false)) {
            wasRicochet = false;
        }
        super.handleIngoingHit(hit);
    }

    @Override
    public NPC spawn() {
        for (val player : this.getPossibleTargets(EntityType.PLAYER)) {
            val p = (Player) player;
            p.putBooleanAttribute("has_taken_damage_during_zilyana", false);
        }
        return super.spawn();
    }

}
