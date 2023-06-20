package com.zenyte.game.content.theatreofblood.boss.nylocas.npc;

import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.theatreofblood.boss.nylocas.NylocasRoom;
import com.zenyte.game.content.theatreofblood.boss.nylocas.model.NylocasType;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Tommeh | 6/18/2020 | 8:56 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class NylocasVasilias extends TheatreNPC<NylocasRoom> implements CombatScript {

    private static final Animation spawnAnimation = new Animation(8075);
    private static final Animation magicAnimation = new Animation(7990);
    private static final Animation meleeAnimation = new Animation(8004);
    private static final Animation rangedAnimation = new Animation(8001);

    private static final Projectile magicProjectile = new Projectile(1610, 45, 25, 30, 10, 30, 0, 1);
    private static final Projectile rangedProjectile = new Projectile(1561, 30, 20, 30, 10, 30, 0, 5);

    @Getter
    private NylocasType type;
    private int ticks;

    public NylocasVasilias(final NylocasRoom room) {
        super(room.getRaid(), room, NpcId.NYLOCAS_VASILIAS, room.getLocation(3294, 4247, 0), Direction.SOUTH);
        type = NylocasType.MELEE;
        setDeathDelay(3);
        setIntelligent(true);
    }

    @Override
    public float getXpModifier(final Hit hit) {
        return type != null && !hit.getHitType().equals(type.getAcceptableHitType()) ? 0 : 1;
    }

    @Override
    public void setId(final int id) {
        super.setId(id);
        type = NylocasType.get(id);
    }

    @Override
    protected void removeHitpoints(final Hit hit) {
        super.removeHitpoints(hit);
        room.refreshHealthBar(raid);
    }

    @Override
    public void heal(final int amount) {
        super.heal(amount);
        room.refreshHealthBar(raid);
    }

    @Override
    public void setTransformation(final int id) {
        nextTransformation = id;
        setId(id);
        size = definitions.getSize();
        updateFlags.flag(UpdateFlag.TRANSFORMATION);
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.25;
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0.25;
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        super.handleIngoingHit(hit);
        if (hit.getSource() != null) {
            setTarget(hit.getSource());
        }
    }

    private void transform() {
        val types = new ArrayList<NylocasType>(Arrays.asList(NylocasType.values));
        types.remove(type);

        val random = types.get(Utils.random(types.size() - 1));
        type = random;
        //setForceTalk(new ForceTalk("transforming into " + type));

        setAnimation(Animation.STOP);
        setTransformation(type.getIds()[2]);
        combat.setCombatDelay(4);

        for (val m : raid.getParty().getTargetableMembers()) {
            val member = RaidingParty.getPlayer(m);
            if (member == null) {
                continue;
            }
            member.cancelCombat();
        }
        ticks = 0;
    }

    @Override
    public void processNPC() {
        super.processNPC();


        /*val members = new ArrayList<String>(raid.getParty().getMembers());
        if (previousTarget instanceof Player) {
            members.remove(((Player) previousTarget).getUsername());
        }
        Collections.shuffle(members);
        for (val m : members) {
            val member = RaidingParty.getPlayer(m);
            if (member != null) {
                setTarget(member);
                break;
            }
        }*/

        if (combat.getTarget() == null) {
            return;
        }

        ticks++;
        if (!isDead() && !isFinished()) {
            if (ticks % 10 == 0) {
                transform();
            }
        }
    }

    @Override
    public int attack(final Entity target) {
        if (type == NylocasType.MELEE) {
            setAnimation(meleeAnimation);
            delayHit(0, target, new Hit(this, getRandomMaxHit(this, combatDefinitions.getMaxHit(), STAB, target), HitType.MELEE));
            if (target instanceof Player) {
                val player = (Player) target;
                if (!player.getPrayerManager().isActive(Prayer.PROTECT_FROM_MELEE)) {
                    for (val p : room.getPlayers()) {
                        p.putBooleanAttribute("PerfectNylocas", false);
                    }
                }
            }
        } else if (type == NylocasType.MAGIC) {
            setAnimation(magicAnimation);
            delayHit(World.sendProjectile(this, target, magicProjectile), target, new Hit(this, getRandomMaxHit(this, combatDefinitions.getMaxHit(), MAGIC, target), HitType.MAGIC));
            if (target instanceof Player) {
                val player = (Player) target;
                if (!player.getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC)) {
                    for (val p : room.getPlayers()) {
                        p.putBooleanAttribute("PerfectNylocas", false);
                    }
                }
            }
        } else if (type == NylocasType.RANGED) {
            setAnimation(rangedAnimation);
            delayHit(World.sendProjectile(this, target, rangedProjectile), target, new Hit(this, getRandomMaxHit(this, combatDefinitions.getMaxHit(), RANGED, target), HitType.RANGED));
            if (target instanceof Player) {
                val player = (Player) target;
                if (!player.getPrayerManager().isActive(Prayer.PROTECT_FROM_MISSILES)) {
                    for (val p : room.getPlayers()) {
                        p.putBooleanAttribute("PerfectNylocas", false);
                    }
                }
            }
        }
        return combatDefinitions.getAttackSpeed();
    }

    @Override
    public void finish() {
        for (val p : room.getPlayers()) {
            if (!raid.getSpectators().contains(p.getUsername())) {
                if (p.getBooleanAttribute("PerfectNylocas")
                        && !p.getBooleanAttribute("master-combat-achievement60")) {
                    p.putBooleanAttribute("master-combat-achievement60", true);
                    MasterTasks.sendMasterCompletion(p, 60);
                }
            }
        }
        super.finish();
        if(getRaid().getParty().getTargetablePlayers().size() > 0) {
            room.onCompletion();
        }
    }
}
