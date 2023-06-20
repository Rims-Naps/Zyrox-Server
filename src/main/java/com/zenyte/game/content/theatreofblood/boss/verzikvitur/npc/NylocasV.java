package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.theatreofblood.boss.verzikvitur.VerzikRoom;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.model.NylocasTypeV;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.CombatScriptsHandler;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCCollidingEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class NylocasV extends TheatreNPC<VerzikRoom> implements CombatScript {

    public static final Class[] params = {
            VerzikRoom.class, Location.class, Direction.class,
    };

    private static final Animation magicAnimation = new Animation(7990);
    private static final Animation meleeAnimation = new Animation(8004);
    private static final Animation rangedAnimation = new Animation(8001);

    private NylocasTypeV type;
    @Getter
    private List<String> immunity;
    private boolean exploding, startTargetSequence;
    private int ticks = 0;
    private boolean waitingToExplode = false;

    public NylocasV(final VerzikRoom room, final int id, final Location location, final Direction direction, NylocasTypeV type) {
        super(room.getRaid(), room, id, location, direction);
        this.type = type;
        immunity = new ArrayList<>();
        setTargetType(EntityType.PLAYER);
        getCombatDefinitions().setAttackSpeed(1);
        combat = new NPCCombat(this) {
            @Override
            public int combatAttack() {
                if (target == null || target.isDead() || target.getLocation().getDistance(getLocation()) >= 64
                        || (target.getNextLocation() != null && target.getNextLocation().getDistance(getLocation()) >= 64)) {
                    return 0;
                }
                val melee = npc.getCombatDefinitions().isMelee();
                var distance = melee || npc.isForceFollowClose() ? 0 : npc.getAttackDistance();
                if (target.hasWalkSteps()) {
                    distance++;
                }
                if (Utils.collides(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize())) {
                    return 0;
                }
                if (outOfRange(target, distance, target.getSize(), melee)) {
                    return 0;
                }
                addAttackedByDelay(target);
                return CombatScriptsHandler.specialAttack(npc, target);
            }

            @Override
            public void setCombatDelay(int combatDelay) {
                super.setCombatDelay(0);
            }


            @Override
            protected boolean checkAll() {
                if (target.isFinished() || npc.isDead() || npc.isFinished()) {
                    return false;
                }
                if (target.isDead() || npc.isMovementRestricted()) {
                    return true;
                }
                if (colliding()) {
                    npc.setRouteEvent(new NPCCollidingEvent(npc, new EntityStrategy(target)));
                    return true;
                }
                return appendMovement();
            }
        };
    }

    @Override
    public NPC spawn() {
        NylocasV npc = (NylocasV) super.spawn();
        WorldTasksManager.schedule(() -> {
            if (isDead() || isFinished()) {
                return;
            }
            explode();
        }, 38);
        return npc;
    }

    @Override
    public float getXpModifier(final Hit hit) {
        val source = hit.getSource();
        if (!(source instanceof Player)) {
            return 1;
        }

        if (this instanceof NylocasAthanatosV || this instanceof NylocasMatomenosV) {
            return 1;
        }
        val player = (Player) source;
        return isImmune(player, hit) ? 0 : 1;
    }

    @Override
    public void setId(final int id) {
        super.setId(id);
        type = NylocasTypeV.get(id);
    }

    @Override
    public void setTransformation(final int id) {
        nextTransformation = id;
        setId(id);
        size = definitions.getSize();
        updateFlags.flag(UpdateFlag.TRANSFORMATION);
    }

    @Override
    public int attack(final Entity target) {
        if(getId() != NpcId.NYLOCAS_ATHANATOS) {
            if(!exploding && !isLocked()) {
                explode();
            }
        }
        return 1;
    }


    @Override
    public void applyHit(Hit hit) {
        if(!(getId() == NpcId.NYLOCAS_ATHANATOS || getId() == NpcId.NYLOCAS_MATOMENOS_8385)) {
            if(hit.getSource().getEntityType() == EntityType.PLAYER) {
                Player p = (Player) hit.getSource();
                if (isImmune(p, hit)) {
                    hit.setDamage(0);
                }
            }
        } else if (getId() == NpcId.NYLOCAS_ATHANATOS) {
            if(hit.getSource().getEntityType() == EntityType.PLAYER) {
                Player p = (Player)hit.getSource();
                Item wep = p.getEquipment().getItem(EquipmentSlot.WEAPON);
                if(CombatUtilities.isWearingSerpentineHelmet(p) || (wep != null && (wep.getDefinitions().getName().contains("(p)") || wep.getDefinitions().getName().contains("(p+)") ||
                        wep.getDefinitions().getName().contains("(p++)") || wep.getName().contains("Toxic") || wep.getName().contains("Abyssal tentacle")))) {
                    waitingToExplode = true;
                    setGraphics(new Graphics(1590, 0, 64));
                    setHitpoints(0);
                    WorldTasksManager.schedule(() -> {
                        explode();
                    });
                }
            }
        } else {
            // MATOMENOS (dont think its needed to have something special happen)
        }

        super.applyHit(hit);
    }

    @Override
    protected void setStats() {
        val partySize = getRaid().getParty().getSize();

        if (!(this.getId() == 8384 || this.getId() == 8385)) {
            if (partySize == 3) {
                combatDefinitions.setHitpoints(8);
            } else if (partySize == 4) {
                combatDefinitions.setHitpoints(9);
            } else if (partySize == 5) {
                combatDefinitions.setHitpoints(11);
            } else if (partySize == 2) {
                combatDefinitions.setHitpoints(7);
            } else if (partySize == 1) {
                combatDefinitions.setHitpoints(6);
            }
            setHitpoints(combatDefinitions.getHitpoints());
        } else {
            super.setStats();
        }
    }

    @Override
    public boolean freeze(final int freezeTicks, final int immunityTicks, @Nullable final Consumer<Entity> onFreezeConsumer) {
        val freeze = super.freeze(freezeTicks, immunityTicks, onFreezeConsumer);
        combat.setCombatDelay((int) TimeUnit.SECONDS.toTicks(5));
        return freeze;
    }

    @Override
    public void setTarget(final Entity target) {
        if (target == null) {
            return;
        }
        combat.setTarget(target);
    }


    public void explode() {
        exploding = true;
        waitingToExplode = false;
        lock();
        setCantInteract(true);
        setAnimation(type.getExplosionAnimation());
        WorldTasksManager.schedule(() -> onFinish(null), type == NylocasTypeV.MATOMENOS ? 2 : (type.getExplosionAnimation().getDuration() / 600) + 1);
        WorldTasksManager.schedule(() -> {
            if(!(getId() == NpcId.NYLOCAS_ATHANATOS || getId() == NpcId.NYLOCAS_MATOMENOS_8385)) {
                for (Player p : raid.getParty().getTargetablePlayers()) {
                    int tileDistance = p.getLocation().getTileDistance(this.getMiddleLocation());
                    if(tileDistance > 3) {
                        continue;
                    }
                    p.applyHit(new Hit(this, tileDistance > 2 ? 0 : tileDistance == 2 ? 8 : tileDistance == 1 ? 32 : 68, type == NylocasTypeV.MAGIC ? HitType.MAGIC : type == NylocasTypeV.MELEE ? HitType.MELEE : HitType.RANGED));
                    for (val p2 : room.getPlayers()) {
                        p2.putBooleanAttribute("PopItTask", false);
                    }
                }
            } else if (getId() == NpcId.NYLOCAS_ATHANATOS) {
                 if (this.getHitpoints() <= 0) {
                    getRoom().getVerzik().applyHit(new Hit(null, Utils.random(70, 75), HitType.POISON));
                } else {
                     World.sendProjectile(getMiddleLocation(), getRoom().getVerzik().getMiddleLocation(), new Projectile(1588, 5, 70, 3, 2, 21, 0, 1));
                     World.sendSoundEffect(getRoom().getVerzik().getMiddleLocation(), new SoundEffect(1514, 10));
                     getRoom().getVerzik().applyHit(new Hit(null, this.getHitpoints(), HitType.HEALED));
                }
            } else if (getId() == NpcId.NYLOCAS_MATOMENOS_8385 && this.getHitpoints() > 0) {
                    World.sendProjectile(getMiddleLocation(), getRoom().getVerzik().getMiddleLocation(), new Projectile(1587, 5, 70, 3, 2, 21, 0, 1));
                    World.sendSoundEffect(getRoom().getVerzik().getMiddleLocation(), new SoundEffect(1514, 10));
                    getRoom().getVerzik().applyHit(new Hit(null, this.getHitpoints(), HitType.HEALED));
            }
            exploding = false;
        });
    }


    public boolean isImmune(final Player player, final Hit hit) {
        return immunity.contains(player.getUsername()) || (type != null && !hit.getHitType().equals(type.getAcceptableHitType()));
    }

    @Override
    public void processNPC() {
        super.processNPC();

        if(getRaid().getParty( ).getTargetableMembers().size() < 1) {
            return;
        }

        if (getRoom().getVerzik().getPhase().getOrdinal() == 4) { //change to 4 if p3 isn't released, 5 if it is
            this.finish();
        }

        if (isFrozen() || startTargetSequence) {
            return;
        }
        if(getId() == NpcId.NYLOCAS_ATHANATOS && ticks % 4 == 0) {
            if((!exploding || !waitingToExplode) && !isDead() && !isFinished() && getAnimation() != NylocasTypeV.PURPLY.getExplosionAnimation()) {
                World.sendProjectile(getMiddleLocation(), getRoom().getVerzik().getMiddleLocation(), new Projectile(1588, 5, 70, 3, 2, 21, 0, 1));
                World.sendSoundEffect(getRoom().getVerzik().getMiddleLocation(), new SoundEffect(1514, 10));
                getRoom().getVerzik().applyHit(new Hit(null, 9, HitType.HEALED));
            }
        }
        ticks++;
    }

    @Override
    public void sendDeath() {
        if (exploding) {
            return;
        } else if(waitingToExplode) {
            return;
        } else {
            explode();
            return;
        }
        //super.sendDeath();
    }

    @Override
    protected void onFinish(final Entity source) {
        if(exploding || waitingToExplode) {
            return;
        } else {
            super.onFinish(source);
        }

    }
}