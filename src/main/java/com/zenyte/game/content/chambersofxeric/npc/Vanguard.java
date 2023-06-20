package com.zenyte.game.content.chambersofxeric.npc;

import com.zenyte.game.RuneDate;
import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.room.VanguardRoom;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.zenyte.game.content.chambersofxeric.room.VanguardRoom.*;

/**
 * @author Kris | 18. nov 2017 : 16:56.39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class Vanguard extends RaidNPC<VanguardRoom> implements CombatScript {

	public Vanguard(final Raid raid, final VanguardRoom room, final int id, final Location tile) {
		super(raid, room, id, tile);
		setCantInteract(true);
		setAggressionDistance(7);
		combat = new VanguardCombatHandler(this);
		this.setForceAggressive(true);
	}

	private boolean updatedStats;
	
	@Override
	public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        val stage = room.getStage();
        if (id == NpcId.VANGUARD_7528 && stage != 2) {
            return false;
        }
        return super.addWalkStep(nextX, nextY, lastX, lastY, check);
    }

	@Override
    public boolean isPathfindingEventAffected() {
        return false;
    }

	@Override
	public void processNPC() {
        if (getId() == NpcId.VANGUARD) {
            for (final Player player : room.getRaid().getPlayers()) {
                if (player.getPlane() == getPlane() && player.getLocation().getDistance(getLocation()) < 7) {
                    room.launch();
                    break;
                }
            }
            return;
        } else {
            if (!isCantInteract()) {
                super.processNPC();
			}
			if (room.getNextSwitch() < Utils.currentTimeMillis()) {
				room.switchLocations();
			}
		}
		if (isCantInteract()) {
            for (final Player p : room.getRaid().getPlayers()) {
                if (p.getPlane() != getPlane()) {
                    continue;
                }
                if (Utils.collides(p.getX(), p.getY(), p.getSize(), getX(), getY(), getSize())) {
                    p.applyHit(new Hit(this, Utils.random(1, 5), HitType.REGULAR));
                }
            }
        }
	}

    @Override
    public void autoRetaliate(final Entity source) {

    }

	@Override
    public void setTransformation(final int id) {
        nextTransformation = id;
        setId(id);
        size = definitions.getSize();
        updateFlags.flag(UpdateFlag.TRANSFORMATION);
        if (!updatedStats && id >= NpcId.VANGUARD_7527 && id <= NpcId.VANGUARD_7529) {
            updatedStats = true;
            updateCombatDefinitions();
        }
    }

	@Override
	public void applyHit(final Hit hit) {
		super.applyHit(hit);
		if (isCantInteract()) {
			hit.setDamage(0);
			if (hit.getSource() instanceof Player) {
				((Player) hit.getSource()).sendMessage("The Vanguard is protected by its shell.");
			}
		}
	}

    @Override
    public void setInteractingWith(final Entity entity) {

    }

    @Override
    public boolean isAttackable(final Entity e) {
        return !isCantInteract();
    }

    @Override
    public float getXpModifier(final Hit hit) {
        if (isCantInteract()) {
            return 0;
        }
        return 1;
    }

	@Override
	public void sendDeath() {
		val vanguards = room.getVanguards();
        int amount = (int) (vanguards[0].getCombatDefinitions().getHitpoints() * 0.33F); //Makes the window 40% if raid is under 5 scale
        if (getRaid().getOriginalPlayers().size() < 5) {
            amount = (int) (vanguards[0].getCombatDefinitions().getHitpoints() * 0.4F);
        }
		val zero = vanguards[0];
		val one = vanguards[1];
		val two = vanguards[2];
		if (!zero.isDead() && !one.isDead() && !two.isDead()) {
			if (zero.getHitpoints() + amount < one.getHitpoints() || zero.getHitpoints() - amount > one.getHitpoints()
					|| one.getHitpoints() + amount < two.getHitpoints() || one.getHitpoints() - amount > two.getHitpoints()
					|| two.getHitpoints() + amount < zero.getHitpoints() || two.getHitpoints() - amount > zero.getHitpoints()) {
				setHitpoints(1);
				return;
			}
		}
		if (room.getStage() == 2) {
			heal(1);
			return;
		}
		room.clearCrystal();
        val source = getMostDamagePlayerCheckIronman();
        onDeath(source);
        if (!room.getPlayers().isEmpty()) {
            val players = room.getPlayers();
            for (val player : players) {
                if (!player.getBooleanAttribute("elite-combat-achievement26")) {
                    val oldVanguardCount = player.getAttributes().getOrDefault("amount_of_vanguards_killed", "0").toString();
                    player.getAttributes().put("amount_of_vanguards_killed", oldVanguardCount.contains("0") ? "1" : oldVanguardCount.contains("1") ? "2" : "3");
                    val newVanguardCount = player.getAttributes().get("amount_of_vanguards_killed").toString();
                    if (newVanguardCount.equals("1")) {
                        player.getAttributes().put("time_when_first_vanguards_was_killed", RuneDate.currentTimeMillis());
                    } else if (newVanguardCount.equals("3")) {
                        long timeOfFirstKill = 0;
                        if (player.getAttributes().get("time_when_first_vanguards_was_killed") instanceof Long) {
                            timeOfFirstKill = (long) player.getAttributes().get("time_when_first_vanguards_was_killed");
                        }
                        val timeNow = RuneDate.currentTimeMillis();
                        if (timeOfFirstKill + 10000L >= timeNow) {
                            player.putBooleanAttribute("elite-combat-achievement26", true);
                            EliteTasks.sendEliteCompletion(player, 26);
                        }
                        player.sendMessage("You killed the first and last vanguard " + Colour.RED.wrap((int) (timeNow - timeOfFirstKill)) + " milliseconds apart");
                        player.getAttributes().put("amount_of_vanguards_killed", "0");
                    }
                }
                boolean allDead = true;
                for (final Vanguard vang : vanguards) {
                    if (!vang.isDead() && !vang.isFinished()) {
                        allDead = false;
                    }
                }
                if (!player.getBooleanAttribute("elite-combat-achievement27") && !VanguardRoom.hasHealed() && allDead) {
                    player.putBooleanAttribute("elite-combat-achievement27", true);
                    EliteTasks.sendEliteCompletion(player, 27);
                }
            }
        }
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (ticks == 0) {
                    if (getId() == NpcId.VANGUARD_7529) {
                        setAnimation(magicFallAnimation);
                    } else if (getId() == NpcId.VANGUARD_7527) {
                        setAnimation(meleeFallAnimation);
                    } else {
                        setAnimation(rangedFallAnimation);
                    }
                } else if (ticks == 1) {
                    val spawnDefinitions = combatDefinitions.getSpawnDefinitions();
                    setAnimation(spawnDefinitions.getDeathAnimation());
                    val sound = spawnDefinitions.getDeathSound();
                    if (sound != null && source != null) {
                        source.sendSound(sound);
                    }
                } else if (ticks == deathDelay + 1) {
                    onFinish(source);
                    stop();
                    return;
                }
                ticks++;
            }
        }, 0, 1);
	}

	@Override
	protected void drop(final Location tile) {
        val killer = getDropRecipient();
        if (killer == null) {
            return;
        }
        onDrop(killer);
        dropItem(killer, new Item(ItemId.XERICS_AID_4_20984, raid.getOriginalPlayers().size() > 8 ? 2 : 1));
        val potion = getId() == NpcId.VANGUARD_7529 ? ItemId.KODAI_4_20948
                : getId() == NpcId.VANGUARD_7528 ? ItemId.TWISTED_4_20936
                : ItemId.ELDER_4_20924;
        dropItem(killer, new Item(potion));
        if (Utils.random(3) == 0) {
            dropItem(killer, new Item(ItemId.OVERLOAD_4_20996));
        }
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0.33;
    }

    @Override
    public double getMeleePrayerMultiplier() {
        return 0.33;
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.33;
    }

    private static final Animation magicAttack = new Animation(7436);
    private static final Animation meleeAttack = new Animation(7441);
    private static final Animation rangedAttack = new Animation(7446);
    private static final Projectile magicProj = new Projectile(1331, 43, 0, 15, 85, 75, 0, 0);
    private static final Projectile rangedProj = new Projectile(1332, 20, 0, 30, 25, 30, 64, 0);
    private static final Graphics magicExplosion = new Graphics(659);
    private static final Graphics rangedExplosion = new Graphics(305);
    private static final SoundEffect meleeSound = new SoundEffect(3308, 10, 0);
    private static final SoundEffect rangedSound = new SoundEffect(360, 10, 0);
    private static final SoundEffect magicStartSound = new SoundEffect(3530, 10, 0);
    private static final SoundEffect magicEndSound = new SoundEffect(156, 10, 0);


    private final Set<Location> getRandomTiles(@NotNull final Entity target) {
        val set = new ObjectOpenHashSet<Location>();
        set.add(new Location(target.getLocation()));
        int count = 1000;
        while(--count > 0) {
            if (set.size() >= 3) {
                break;
            }
            val tile = target.getLocation().transform(Utils.random(-3, 3), Utils.random(-3, 3), 0);
            if (World.isFloorFree(tile, 1) && Utils.findMatching(set, t -> t.matches(tile)) == null && !Utils.collides(getX(), getY(), getSize(), tile.getX(), tile.getY(), 1)) {
                set.add(tile);
            }
        }
        return set;
    }

    @Override
    public int attack(final Entity target) {
        if (getId() == NpcId.VANGUARD_7529) {
            setAnimation(magicAttack);
            World.sendSoundEffect(getMiddleLocation(), magicStartSound);
            getRandomTiles(target).forEach(tile -> {
                val first = tile.matches(target);
                val targetTile = first ? new Location(target.getLocation()) : tile;
                val delay = World.sendProjectile(this, targetTile, magicProj);
                getPossibleTargets(EntityType.PLAYER).forEach(t -> {
                    if (t.matches(targetTile)) {
                        delayHit(Vanguard.this, delay, t, magic(t, (int) (22 * (raid.isChallengeMode() ? 1.5F : 1F))));
                    }
                });
                WorldTasksManager.schedule(() -> {
                    World.sendGraphics(magicExplosion, targetTile);
                    World.sendSoundEffect(targetTile, magicEndSound);
                }, delay);
            });
        } else if (getId() == NpcId.VANGUARD_7528) {
            setAnimation(rangedAttack);
            getRandomTiles(target).forEach(tile -> {
                val first = tile.matches(target);
                val targetTile = first ? new Location(target.getLocation()) : tile;
                World.sendProjectile(this, targetTile, rangedProj);
                getPossibleTargets(EntityType.PLAYER).forEach(t -> {
                    if (t.matches(targetTile)) {
                        delayHit(Vanguard.this, 1, t, ranged(t, (int) (22 * (raid.isChallengeMode() ? 1.5F : 1F))));
                    }
                });
                WorldTasksManager.schedule(() -> {
                    World.sendGraphics(rangedExplosion, targetTile);
                    World.sendSoundEffect(targetTile, rangedSound);
                }, 1);
            });
        } else if (getId() == NpcId.VANGUARD_7527) {
            setAnimation(meleeAttack);
            World.sendSoundEffect(getMiddleLocation(), meleeSound);
            for (int i = 0; i < 3; i++) {
                delayHit(this, 0, target, melee(target, (int) (18 * (raid.isChallengeMode() ? 1.5F : 1F))));
            }
        }
        return 4;
    }

    private static final class VanguardCombatHandler extends NPCCombat {

		VanguardCombatHandler(final NPC npc) {
			super(npc);
		}

		@Override
		public void setTarget(final Entity target) {
			if (npc.isCantInteract()) {
				return;
			}
			super.setTarget(target);
		}
	}

}
