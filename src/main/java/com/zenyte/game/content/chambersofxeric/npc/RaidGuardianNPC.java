package com.zenyte.game.content.chambersofxeric.npc;

import com.zenyte.cores.WorldThread;
import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.room.GuardiansRoom;
import com.zenyte.game.content.skills.mining.MiningDefinitions;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.CombatScriptsHandler;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 17. nov 2017 : 3:25.23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class RaidGuardianNPC extends RaidNPC<GuardiansRoom> implements CombatScript {

	public RaidGuardianNPC(final Raid raid, final GuardiansRoom room, final int id, final Location tile) {
		super(raid, room, id, tile);
		freeze(Integer.MAX_VALUE);
		combat = new GuardianCombatHandler(this);
	}

	@Override
    public void setFaceEntity(final Entity entity) {

    }

    @Override
    public void flinch() {
        if (flinchTime > WorldThread.WORLD_CYCLE) {
            return;
        }
        val attackSpeed = combatDefinitions.getAttackSpeed();
        combat.setCombatDelay(attackSpeed / 2);
        flinchTime = WorldThread.WORLD_CYCLE + attackSpeed;
    }

    @Override
    public void autoRetaliate(final Entity source) {
        for (val guardian : room.getNpcs()) {
            guardian._autoRetaliate(source);
        }
    }

    private void _autoRetaliate(final Entity source) {
        if (combat.getTarget() == source) return;
        if (!combat.isForceRetaliate()) {
            val target = combat.getTarget();
            if (target != null) {
                if (target instanceof Player) {
                    val player = (Player) target;
                    if (player.getActionManager().getAction() instanceof PlayerCombat) {
                        val combat = (PlayerCombat) player.getActionManager().getAction();
                        if (combat.getTarget() == this) {
                            return;
                        }
                    }
                } else {
                    val npc = (NPC) target;
                    if (npc.getCombat().getTarget() == this) return;
                }
            }
        }
        randomWalkDelay = 1;
        resetWalkSteps();
        val previousTarget = combat.getTarget();
        combat.setTarget(source);
        if (previousTarget == null && combat.getCombatDelay() == 0) {
            combat.setCombatDelay(2);
        }
    }

	@Override
	public boolean checkProjectileClip(final Player player) {
		return false;
	}

	private float getDamageModifier(@NotNull final Hit hit) {
	    val source = hit.getSource();
	    if (!(source instanceof Player) || !(hit.getWeapon() instanceof Item)) {
	        return 0F;
        }
	    val player = (Player) source;
	    val pickaxe = MiningDefinitions.PickaxeDefinitions.get(player, false);
        return pickaxe.map(pickaxeResult -> (50F + player.getSkills().getLevel(Skills.MINING) + pickaxeResult.getDefinitions().getLevel()) / 150F).orElse(0F);
    }

    @Override
    public float getXpModifier(final Hit hit) {
	    val damageModifier = getDamageModifier(hit);
        hit.setDamage((int) (hit.getDamage() * damageModifier));
        return damageModifier;
    }

    @Override
    public float getPointsMultiplier(final Hit hit) {
        return getDamageModifier(hit);
    }
	
	@Override
	public final void processNPC() {
		if (combat.process()) {
			return;
		}
		checkAggressivity();
	}

	@Override
	public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
		return false;
	}
	
	@Override
	public void sendDeath() {
		val defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setAnimation(null);
		room.finish();
		WorldTasksManager.schedule(new WorldTask() {
			private int loop;
			@Override
			public void run() {
				if (loop == 0) {
					setAnimation(defs.getDeathAnim());
				} else if (loop == 1) {
					setTransformation(getId() + 2);
                    val index = room.getNpcs().indexOf(RaidGuardianNPC.this);
					drop(index == -1 ? getMiddleLocation() : (room.getLocation(GuardiansRoom.guardianSpawnTiles[room.getIndex()][2 + (index == 0 ? 1 : 0)])));
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}


    public static final Animation attack = new Animation(1203);
    private static final Animation stomp = new Animation(4278);
    private static final Graphics gfx = new Graphics(71, 0, 30);
    private static final Projectile projectile = new Projectile(645, 255, 10, 15, 0, 15, 64, 0);
    private static final SoundEffect meleeSound = new SoundEffect(2533, 10, 0);
    private static final SoundEffect rangedSound = new SoundEffect(360, 10, 30);

    @Override
    public int attack(final Entity target) {
        val face = GuardiansRoom.faceTiles[room.getIndex()];
        setFaceLocation(room.getLocation(face[getId() - 7569]));
        val style = Utils.findMatching(raid.getPlayers(), p -> isWithinMeleeDistance(this, p)) == null ? 1 : Utils.random(1);
        if (style == 0) {
            setAnimation(attack);
            World.sendSoundEffect(getLocation(), meleeSound);
            delayHit(0, target, melee(target, getMaxHit(20)));
        } else {
            stomp(target.getLocation());
        }
        return combatDefinitions.getAttackSpeed();
    }

    public void stomp(@NotNull final Location tile) {
        setAnimation(stomp);
        val loc = new Location(tile);
        World.sendProjectile(this, loc, projectile);
        World.sendSoundEffect(getLocation(), rangedSound);
        WorldTasksManager.schedule(() -> {
            World.sendGraphics(gfx, loc);
            for (val p : raid.getPlayers()) {
                if (p.getLocation().withinDistance(loc, 1)) {
                    delayHit(-1, p, new Hit(this, Utils.random(getMaxHit(15)), HitType.REGULAR));
                    p.putBooleanAttribute("has_taken_damage_from_guardian_in_raid", true);
                }
            }
        });
    }

    private static final class GuardianCombatHandler extends NPCCombat {

		GuardianCombatHandler(final RaidGuardianNPC npc) {
			super(npc);
		}
		
		@Override
		public void setTarget(final Entity target) {
			this.target = target;
			if (!checkAll()) {
				removeTarget();
            }
		}
		
		@Override
		public boolean checkAll() {
			val target = this.target;
			if (target == null) {
				return false;
			}

            return !npc.isFinished() && !npc.isDead() && !target.isFinished() && !target.isDead() && npc.getPlane() == target.getPlane();
        }
		
		@Override
		public int combatAttack() {
			val target = this.target;
			if (target == null) {
				return 0;
			}
			val distanceX = target.getX() - npc.getX();
			val distanceY = target.getY() - npc.getY();
			val size = npc.getSize();
			if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
				return 0;
			}
			return CombatScriptsHandler.specialAttack(npc, target);
		}
		
	}
	
}
