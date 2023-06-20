package com.zenyte.game.content.chambersofxeric.npc;

import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.npc.combat.tekton.TektonCombatScript;
import com.zenyte.game.content.chambersofxeric.npc.combat.tekton.TektonEngageScript;
import com.zenyte.game.content.chambersofxeric.npc.combat.tekton.TektonRetreatScript;
import com.zenyte.game.content.chambersofxeric.npc.combat.tekton.TektonSmithScript;
import com.zenyte.game.content.chambersofxeric.room.TektonRoom;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCDLoader;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCombatDefinitions;
import com.zenyte.game.world.entity.npc.combatdefs.StatType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 19. nov 2017 : 2:42.20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class Tekton extends RaidNPC<TektonRoom> {
	
	private static final Animation enterCombatStance = new Animation(7478);
	private static final Animation enragedEnterCombatStance = new Animation(7488);
	private static final Animation riseFromAnvil = new Animation(7474);
	private static final Animation enragedRiseFromAnvil = new Animation(7487);
	private static final Animation leaveCombatStance = new Animation(7479);
	private static final Animation enragedLeaveCombatStance = new Animation(7489);
	private static final Animation stab = new Animation(7482);
	private static final Animation enragedStab = new Animation(7493);
	private static final Animation slash = new Animation(7483);
	private static final Animation enragedSlash = new Animation(7494);
	private static final Animation crush = new Animation(7484);
	private static final Animation enragedCrush = new Animation(7492);
	private static final Animation leaningToAnvil = new Animation(7475);

	public Tekton(final Raid raid, final Direction direction, final TektonRoom room, final Location tile) {
		super(raid, room, NpcId.TEKTON, tile);
		setDirection(direction.getDirection());
		spawnDirection = direction;
		this.aggressionDistance = 32;
		setForceAggressive(true);
	}

    public void setFreezeDelay(final long time) {
        if (time < System.currentTimeMillis()) {
            return;
        }
        room.getPlayers().forEach(player -> player.sendMessage("<col=<col=fc02e7>>Tekton shrugs off the binding on him as if it weren't there.</col>"));
    }

    @Override
    protected void setStats() {
        val cachedDefs = NPCCombatDefinitions.clone(getId(), NPCCDLoader.get(getId()));
        val challengeModeMultiplier = raid.isChallengeMode() ? 1.5D : 1D;
        val decreasedChallengeModeMultiplier = raid.isChallengeMode() ? 1.2D : 1D;
        val statDefinitions = cachedDefs.getStatDefinitions();
        for (val aggressiveStat : aggressiveStats) {
            statDefinitions.set(aggressiveStat, Math.max( 1,
                    (int) Math.floor(statDefinitions.get(aggressiveStat) * aggressiveLevelMultiplier
                            * (aggressiveStat == StatType.MAGIC ? decreasedChallengeModeMultiplier : challengeModeMultiplier))));
        }
        statDefinitions.set(StatType.DEFENCE, Math.max(1, (int) Math.floor(statDefinitions.get(StatType.DEFENCE) * defenceMultiplier * decreasedChallengeModeMultiplier)));
        setCombatDefinitions(cachedDefs);
        combatDefinitions.setHitpoints(Math.max(1, (int) Math.floor(cachedDefs.getHitpoints() * hitpointsMultiplier * challengeModeMultiplier)));
        setHitpoints(combatDefinitions.getHitpoints());
    }
	
	@Getter
	@Setter
	private int enrageTime;
	@Getter @Setter private Entity target;
	@Getter @Setter private WorldObject block;
	@Getter private boolean approached;
	@Getter @Setter private boolean enteredCombat;

	public boolean isEnraged() {
		return enrageTime > 0;
	}
	
	@Override
	public void autoRetaliate(final Entity source) {
		
	}

    @Override
    public boolean isPathfindingEventAffected() {
        return false;
    }
	
	@Override
	public boolean isFreezeable() {
		return false;
	}
	
	@Override
	public void applyHit(final Hit hit) {
	    if (hit.getHitType() == HitType.RANGED) {
	        hit.setDamage(0);
        }
		if (getId() == 7545) {
			hit.setDamage(0);
			val source = hit.getSource();
			if (source instanceof Player) {
				((Player) source).sendMessage("Tekton's anvil protects him from your attacks.");
			}
		}
		super.applyHit(hit);
	}

    @Override
    public float getXpModifier(final Hit hit) {
        return hit.getHitType() == HitType.RANGED || getId() == 7545 ? 0 : 1;
    }

	public Direction faceLocation(final Location location) {
		val middle = getMiddleLocation();
		val direction = Utils.getFaceDirection(location.getX() - middle.getX(), location.getY() - middle.getY());
		if (direction >= 256 && direction < 768) {
			faceDirection(Direction.WEST);
			return Direction.WEST;
		} else if (direction >= 768 && direction < 1280) {
			faceDirection(Direction.NORTH);
			return Direction.NORTH;
		} else if (direction >= 1280 && direction < 1792) {
			faceDirection(Direction.EAST);
			return Direction.EAST;
		} else {
			faceDirection(Direction.SOUTH);
			return Direction.SOUTH;
		}
	}

	private boolean started;
	@Getter @Setter private boolean boostedStats;

	@Override
	public boolean checkProjectileClip(final Player player) {
		return false;
	}
	
	@Override
	public void processNPC() {
		if (enrageTime > 0) {
			enrageTime--;
			if (enrageTime == 0) {
				setTransformation(id == 7543 ? 7541 : 7542);
				val combat = getCombatDefinitions();
				boostedStats = false;
                combat.getStatDefinitions().set(StatType.DEFENCE,
                        (int) (combat.getStatDefinitions().get(StatType.DEFENCE) / 1.3F));
			}
		}
		if (hasWalkSteps()) {
			for (final Player p : raid.getPlayers()) {
				if (Utils.collides(getX(), getY(), getSize(), p.getX(), p.getY(), p.getSize())) {
					p.applyHit(new Hit(Utils.random(1, 3), HitType.DEFAULT));
				}
			}
		}
		if (started) {
			return;
		}
		for (final Player p : raid.getPlayers()) {
		    if (collides(p.getLocation())) {
                approached = true;
                started = true;
				this.getTemporaryAttributes().put("amount of smiths", 0);
				p.putBooleanAttribute("has_taken_damage_from_tekton", false);
                room.spawnFire();
                execute("engage");
                return;
            }
		}
	}

	private boolean collides(@NotNull final Location tile) {
		if (tile.getPlane() != getPlane()) {
			return false;
		}
	    return tile.getX() >= getX() - 3 && tile.getX() < (getX() + getSize() + 3)
                && tile.getY() >= getY() - 3 && tile.getY() < (getY() + getSize() + 3);
    }
	
	@Override
	public void sendDeath() {
		room.clearCrystal();
		super.sendDeath();
		World.removeObject(block);
	}

	public boolean isRoomEmpty() {
	    for (val player : room.getPlayers()) {
	        if (player.getLocation().withinDistance(room.getSafeTile(), 1)) {
	            continue;
            }
	        return false;
        }
		return true;
	}

    @Override
    public double getMeleePrayerMultiplier() {
        return 0.5;
    }

	public void resetToDefaultState() {
		setTransformation(7540);
		started = false;
	}

	public void animate(final String instruction) {
		if (isDead() || isFinished()) {
			return;
		}
		switch (instruction) {
		case "enter combat stance":
			setAnimation(isEnraged() ?
                         enragedEnterCombatStance :
                         enterCombatStance);
			return;
		case "leave combat stance":
			setAnimation(isEnraged() ?
                         enragedLeaveCombatStance :
                         leaveCombatStance);
			return;
		case "rise from anvil":
			setAnimation(isEnraged() ?
                         enragedRiseFromAnvil :
                         riseFromAnvil);
			return;
		case "lean to anvil":
			setAnimation(leaningToAnvil);
			return;
		case "stab":
			setAnimation(isEnraged() ?
                         enragedStab :
                         stab);
			return;
		case "slash":
			setAnimation(isEnraged() ?
                         enragedSlash :
                         slash);
			return;
		case "crush":
			setAnimation(isEnraged() ?
                         enragedCrush :
                         crush);
			return;
		default:
			System.err.println("Missing tekton animation: " + instruction);
        }
	}

	public void execute(final String script) {
		switch (script) {
		case "return to anvil":
			new TektonRetreatScript().execute(this);
			return;
		case "engage":
			new TektonEngageScript().execute(this);
			return;
		case "combat":
			new TektonCombatScript().execute(this);
			return;
		case "smith at anvil":
			new TektonSmithScript().execute(this);
			return;
		default:
			System.err.println("Missing tekton script: " + script);
        }
	}

	private static final Item[] potions = new Item[] {
            new Item(20996, 2),
            new Item(20972, 1),
            new Item(20960, 1)
    };

	@Override
	protected void drop(final Location tile) {
		World.spawnObject(new WorldObject(30022, 10, 0, new Location(getMiddleLocation())));
		val killer = getDropRecipient();
		if (killer == null) {
			return;
		}
		if (Integer.parseInt(this.getTemporaryAttributes().get("amount of smiths").toString()) < 2) {
			if (!killer.getBooleanAttribute("master-combat-achievement15")) {
				killer.putBooleanAttribute("master-combat-achievement15", true);
				MasterTasks.sendMasterCompletion(killer, 15);
			}
		}
		if (killer.getBooleanAttribute("has_taken_damage_from_tekton")
				&& !killer.getBooleanAttribute("master-combat-achievement25")) {
			killer.putBooleanAttribute("master-combat-achievement25", true);
			MasterTasks.sendMasterCompletion(killer, 25);
		}
		onDrop(killer);
		announce(killer);
		dropItem(killer, new Item(20910, 5));
		for (val potion : potions) {
		    dropItem(killer, new Item(potion));
        }
		val size = getRaid().getOriginalPlayers().size() - 1;
		val base = 70;
		val additional = Math.round(370F / 99F * size);
		if (Utils.random(base + additional - 1) == 0) {
			dropItem(killer, new Item(6573));
			raid.setOnyxDropMessage(killer.getUsername() + ", " + System.currentTimeMillis());
		}
	}
}
