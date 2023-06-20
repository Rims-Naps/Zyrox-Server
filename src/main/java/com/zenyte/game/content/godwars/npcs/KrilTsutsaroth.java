package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.boss.cerberus.SummonedSoul;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.godwars.instance.SaradominInstance;
import com.zenyte.game.content.godwars.instance.ZamorakInstance;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.*;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

/**
 * @author Tommeh | 26 mrt. 2018 : 16:55:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class KrilTsutsaroth extends GodwarsBossNPC implements Spawnable, CombatScript {

    private boolean hasBeenHitBySpecial;
    private boolean hasBeenHitByMelee;

    private static final Animation meleeAnimation = new Animation(6948);
    private static final Animation magicAnimation = new Animation(6950);
    private static final Projectile projectile = new Projectile(1155, 41, 16, 30, 5, 10, 0, 5);
    private static final ForceTalk forceTalk = new ForceTalk("YARRRRRRR!");
    private static final String message = "K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.";
    private static final SoundEffect magicSound = new SoundEffect(3866, 10, 0);

    public KrilTsutsaroth(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
        if (isAbstractNPC() || tile.getX() >= 6400) return;
        setMinions(new GodwarsBossMinion[]{
                new GodwarsBossMinion(NpcId.TSTANON_KARLAK, new Location(2929, 5327, 2), Direction.SOUTH, 5),
                new ZaklNGritch(NpcId.ZAKLN_GRITCH, new Location(2921, 5327, 2), Direction.SOUTH, 5),
                new BalfrugKreeyath(NpcId.BALFRUG_KREEYATH, new Location(2923, 5324, 2), Direction.SOUTH, 5),
        });
        hasBeenHitBySpecial = false;
        hasBeenHitByMelee = false;
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
            if (deathCount >= 3 && !player.getBooleanAttribute("hard-combat-achievement24")) {
                player.putBooleanAttribute("hard-combat-achievement24", true);
                HardTasks.sendHardCompletion(player, 24);
            }
            if (!hasBeenHitBySpecial && !player.getBooleanAttribute("hard-combat-achievement36")) {
                player.putBooleanAttribute("hard-combat-achievement36", true);
                HardTasks.sendHardCompletion(player, 36);
            }
            if (player.getEquipment().getItem(EquipmentSlot.WEAPON) != null) {
                if ((player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("silverlight")
                        || player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("darklight")
                        || player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("arclight"))
                        && !player.getBooleanAttribute("hard-combat-achievement44")) {
                    player.putBooleanAttribute("hard-combat-achievement44", true);
                    HardTasks.sendHardCompletion(player, 44);
                }
            }
            if (!hasBeenHitByMelee && !player.getBooleanAttribute("elite-combat-achievement49")) {
                player.putBooleanAttribute("elite-combat-achievement49", true);
                EliteTasks.sendEliteCompletion(player, 49);
            }
            if (!player.getBooleanAttribute("grandmaster-combat-achievement11")
                    && !player.getBooleanAttribute("has_taken_damage_during_kril")) {
                player.putBooleanAttribute("grandmaster-combat-achievement11", true);
                GrandmasterTasks.sendGrandmasterCompletion(player, 11);
            }
            if (player.getAttributes().containsKey("kril_kc_on_instance_creation")) {
                if (player.getNumericAttribute("kril_kc_on_instance_creation").intValue() + 19 <= player.getNotificationSettings().getKillcount("k'ril tsutsaroth")
                        && !player.getBooleanAttribute("grandmaster-combat-achievement34")
                        && player.getArea() instanceof ZamorakInstance) {
                    player.putBooleanAttribute("grandmaster-combat-achievement34", true);
                    GrandmasterTasks.sendGrandmasterCompletion(player, 34);
                }
            }
        }
    }

    @Override
    BossRespawnTimer timer() {
        return BossRespawnTimer.KRIL_TSUTSAROTH;
    }

    public KrilTsutsaroth(final GodwarsBossMinion[] minions, final int id, final Location tile, final Direction direction, final int radius) {
        this(id, tile, direction, radius);
        setMinions(minions);
    }

    @Override
    int diaryFlag() {
        return 0x8;
    }

    @Override
    ForceTalk[] getQuotes() {
        return null;
    }

    @Override
    public GodType type() {
        return GodType.ZAMORAK;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == NpcId.KRIL_TSUTSAROTH;
    }

    @Override
    public int attack(final Entity target) {
        val style = Utils.random(15);
        if (style == 15 && target instanceof Player && ((Player) target).getPrayerManager().isActive(Prayer.PROTECT_FROM_MELEE)) {
            setAnimation(magicAnimation);
            setForceTalk(forceTalk);
            val player = (Player) target;
            player.sendMessage(message);
            hasBeenHitBySpecial = true;
            player.getPrayerManager().drainPrayerPoints(player.getEquipment().getId(EquipmentSlot.SHIELD) == ItemId.SPECTRAL_SPIRIT_SHIELD ? player.getPrayerManager().getPrayerPoints() / 4:player.getPrayerManager().getPrayerPoints() / 2);
            delayHit(this, 0, target, new Hit(this, getRandomMaxHit(this, 49, MELEE, target), HitType.REGULAR));
        } else if (style < 10) {
            setAnimation(meleeAnimation);
            delayHit(this, 0, target, new Hit(this, getRandomMaxHit(this, 47, AttackType.SLASH, target), HitType.MELEE));
            target.getToxins().applyToxin(Toxins.ToxinType.POISON, 16);
            hasBeenHitByMelee = true;
        } else {
            World.sendSoundEffect(getMiddleLocation(), magicSound);
            setAnimation(magicAnimation);
            for (val t : getPossibleTargets(EntityType.PLAYER)) {
                World.sendProjectile(this, t, projectile);
                int damage = getRandomMaxHit(this, 30, MAGIC, t);
                if (damage > 0) {
                    damage = Utils.random(10, 30);
                }
                //k'ril deals a minimum of 10 damage upon successful hit; for even distribution, we re-calc it.
                delayHit(this, projectile.getTime(this, t), t, new Hit(this, damage, HitType.MAGIC));
            }
        }
        return getCombatDefinitions().getAttackSpeed();
    }

    @Override
    public NPC spawn() {
        for (val player : this.getPossibleTargets(EntityType.PLAYER)) {
            val p = (Player) player;
            p.putBooleanAttribute("has_taken_damage_during_kril", false);
        }
        hasBeenHitByMelee = false;
        hasBeenHitBySpecial = false;
        return super.spawn();
    }
}
