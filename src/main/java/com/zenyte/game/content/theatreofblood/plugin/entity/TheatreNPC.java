package com.zenyte.game.content.theatreofblood.plugin.entity;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc.*;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combatdefs.ImmunityType;
import lombok.Getter;
import lombok.val;

import java.util.EnumSet;

/**
 * @author Corey
 * @since 26/05/2020
 */
public class TheatreNPC<T extends TheatreArea> extends NPC {
    
    @Getter
    protected final TheatreOfBloodRaid raid;
    @Getter
    protected final T room;
    
    public TheatreNPC(final TheatreOfBloodRaid raid, final T room, final int id, final Location tile, final Direction facing) {
        super(id, tile, facing, 0);
        this.raid = raid;
        this.room = room;
        this.spawned = true;
        this.supplyCache = false;
    }
    
    public TheatreNPC(final TheatreOfBloodRaid raid, final T room, final int id, final Location tile) {
        this(raid, room, id, tile, Direction.SOUTH);
    }

    @Override
    public void processNPC() {
        /*if (raid.getParty().getAlivePlayers().size() < 1 || !room.isStarted()) {
            return;

        }*/
        /*
        if(room.isStarted() && !room.isCompleted() && !raid.isCompleted()) {
            room.setDuration(room.getDuration() + 1);
            World.sendMessage(MessageType.FILTERABLE, room.getDuration() + "");
        }
        */
        if (room.getRoom().getWave() == 6) {
            if (this instanceof NylocasMatomenosV
                    || this instanceof NylocasAthanatosV
                    || this instanceof NylocasHagiosV
                    || this instanceof NylocasIschyrosV
                    || this instanceof NylocasToxobolosV) {
                if (this.isFrozen()) {
                    for (val p : room.getPlayers()) {
                        p.putBooleanAttribute("PopItTask", false);
                    }
                }
            }
        }
        super.processNPC();
    }

    @Override
    protected void updateCombatDefinitions() {
        super.updateCombatDefinitions();
        if (raid != null) {
            if(id == NpcId.VERZIK_VITUR_8370 || id == NpcId.VERZIK_VITUR_8372 || true) {
                setStats();
            }

            if (isToxinImmune()) {
                this.combatDefinitions.setImmunityTypes(EnumSet.allOf(ImmunityType.class));
            }
        }
    }

    /*@Override
    public int getMaxHitpoints() {
        val maxHitpoints = super.getMaxHitpoints();
        val partySize = getRaid().getParty().getSize();
        if (partySize <= 3) {
            return (int) Math.floor(maxHitpoints * 0.75);
        } else if (partySize == 4) {
            return (int) Math.floor(maxHitpoints * 0.875);
        }
        return maxHitpoints > 0 ? maxHitpoints : 1;
    }*/
    
    protected void setStats() {
        val maxHitpoints = getMaxHitpoints();
        val partySize = getRaid().getParty().getSize();
        if(!(this instanceof VerzikVitur)) {
            if (partySize == 1) {
                combatDefinitions.setHitpoints((int) Math.floor(maxHitpoints * 0.40));
            } else if (partySize == 2) {
                combatDefinitions.setHitpoints((int) Math.floor(maxHitpoints * 0.60));
            } else if (partySize == 3) {
                combatDefinitions.setHitpoints((int) Math.floor(maxHitpoints * 0.75));
            } else if (partySize == 4) {
                combatDefinitions.setHitpoints((int) Math.floor(maxHitpoints * 0.875));
            }
            setHitpoints(combatDefinitions.getHitpoints());
        } else {
            setHitpoints(getMaxHitpoints());
        }

    }
    
    @Override
    public boolean isTolerable() {
        return false;
    }
    
    @Override
    public boolean isEntityClipped() {
        return false;
    }
    
    protected boolean isToxinImmune() {
        return true;
    }
    
    @Override
    public void setRespawnTask() {
    }
    
}
