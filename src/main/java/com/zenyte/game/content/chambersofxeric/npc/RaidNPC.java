package com.zenyte.game.content.chambersofxeric.npc;

import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.map.RaidArea;
import com.zenyte.game.content.consumables.Consumable;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combatdefs.ImmunityType;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCDLoader;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCombatDefinitions;
import com.zenyte.game.world.entity.npc.combatdefs.StatType;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.val;

import java.util.EnumSet;

/**
 * @author Kris | 13. mai 2018 : 18:49:43
 * @param <T> type of the room in which the monster resides.
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class RaidNPC<T extends RaidArea> extends NPC {

	public RaidNPC(final Raid raid, final T room, final int id, final Location tile) {
		super(id, tile, Direction.SOUTH, 5);
		this.maxDistance = 32;
		this.raid = raid;
		this.room = room;
        val size = raid.getOriginalPlayers().size();
        val combatHitpointsOffset = Math.max(59, Math.min(raid.getCombatLevel(), 126));
        val combatHitpointsMultiplier = combatHitpointsOffset / 126F;
        val combatOffset = Math.max(75, Math.min(raid.getCombatLevel(), 126));
        val combatMultiplier = combatOffset / 126F;
        aggressiveLevelMultiplier = (size == 1 ? 1 : (1D + (0.07D * (1D + (Math.floor(size / 5D))) + (0.01D * (size - 1D))))) * combatMultiplier;
        defenceMultiplier = (1 + (0.01D * (size - 1))) * combatMultiplier;
        hitpointsMultiplier = (1 + (Math.floor(size / 2D))) * combatHitpointsMultiplier;
	}

	@Override
    protected void updateCombatDefinitions() {
        super.updateCombatDefinitions();
        if (raid != null) {
            setStats();
            if (isToxinImmune()) {
                this.combatDefinitions.setImmunityTypes(EnumSet.allOf(ImmunityType.class));
            }
        }
    }

    protected boolean isToxinImmune() {
	    return true;
    }

    public boolean grantPoints() {
	    return true;
    }

    @Override
    public void setTransformation(final int id) {
        nextTransformation = id;
        setId(id);
        size = definitions.getSize();
        updateFlags.flag(UpdateFlag.TRANSFORMATION);
    }

	double aggressiveLevelMultiplier;
	double defenceMultiplier;
	protected double hitpointsMultiplier;

	StatType[] aggressiveStats = new StatType[] {
	    StatType.ATTACK, StatType.STRENGTH, StatType.MAGIC, StatType.RANGED
    };

	public float getPointsMultiplier(final Hit hit) {
	    return getXpModifier(hit);
    }

	protected void setStats() {
        val cachedDefs = NPCCombatDefinitions.clone(getId(), NPCCDLoader.get(getId()));
	    val challengeModeMultiplier = raid.isChallengeMode() ? 1.5D : 1D;
	    val statDefinitions = cachedDefs.getStatDefinitions();
        for (val aggressiveStat : aggressiveStats) {
            statDefinitions.set(aggressiveStat, Math.max( 1, (int) Math.floor(statDefinitions.get(aggressiveStat) * aggressiveLevelMultiplier * challengeModeMultiplier)));
        }
        statDefinitions.set(StatType.DEFENCE, Math.max(1, (int) Math.floor(statDefinitions.get(StatType.DEFENCE) * defenceMultiplier * challengeModeMultiplier)));
        setCombatDefinitions(cachedDefs);
        combatDefinitions.setHitpoints(Math.max(1, (int) Math.floor(cachedDefs.getHitpoints() * hitpointsMultiplier * challengeModeMultiplier)));
        setHitpoints(combatDefinitions.getHitpoints());
    }

    protected int getMaxHit(final int base) {
        val challengeModeMultiplier = raid.isChallengeMode() ? 1.5D : 1D;
	    return (int) Math.floor(base * aggressiveLevelMultiplier * challengeModeMultiplier);
    }
	
	@Getter protected final Raid raid;
	@Getter protected final T room;

	void announce(final Player killer) {
		raid.sendGlobalMessage("As the " + getDefinitions().getName() + " is slain, supplies are dropped for " + killer.getName() + ".");
	}

    @Override
    protected boolean isPotentialTarget(final Entity entity) {
        val entityX = entity.getX();
        val entityY = entity.getY();
        val entitySize = entity.getSize();
        val x = getX();
        val y = getY();
        val size = getSize();
        val currentTime = Utils.currentTimeMillis();

        return !entity.isMaximumTolerance() && (entity.isMultiArea() || entity.getAttackedBy() == this || (entity.getAttackedByDelay() <= currentTime && entity.getFindTargetDelay() <= currentTime)) && (!isProjectileClipped(entity, combatDefinitions.isMelee())
                || Utils.collides(x, y, size, entityX, entityY, entitySize)) && (forceAggressive || combatDefinitions.isAlwaysAggressive()
                || entity instanceof NPC && ((NPC) entity).getDefinitions().containsOption("Attack")) && isAcceptableTarget(entity) && (!(entity instanceof Player) || !isTolerable() || !((Player) entity).isTolerant(getLocation()));
    }

    @Override
    protected void spawnDrop(final Item item, final Location tile, final Player killer) {
        val id = item.getId();
        val qualifies = id == ItemId.NOXIFER_SEED || id == ItemId.BUCHU_SEED || id == ItemId.GOLPAR_SEED || Consumable.gourdDrinks.containsKey(id);
        if (killer.isIronman() && qualifies) {
            World.spawnFloorItem(item, tile, -1, null, null, invisibleDropTicks(), visibleDropTicks(), true);
            return;
        }
        World.spawnFloorItem(item, tile, killer, invisibleDropTicks(), visibleDropTicks());
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
	public void setRespawnTask() { }
	

}
