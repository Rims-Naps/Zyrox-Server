package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.VerzikRoom;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.entity.npc.combatdefs.ImmunityType;
import com.zenyte.game.world.object.WorldObject;

import java.util.EnumSet;

/**
 * @author Cresinkel
 */
public class Web extends NPC {

    private final TheatreOfBloodRaid raid;

    public Web(int id, Location tile, Direction facing, int radius, TheatreOfBloodRaid raid) {
        super(id, tile, facing, radius);
        this.combat = new NPCCombat(this) {
            @Override
            public void setTarget(final Entity target) { }
            @Override
            public void forceTarget(final Entity target) { }
        };
        this.raid = raid;
    }



    @Override
    protected boolean isMovableEntity() {
        return false;
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    public boolean isMultiArea() {
        return true;
    }

    @Override
    protected void updateCombatDefinitions() {
        super.updateCombatDefinitions();
        combatDefinitions.setHitpoints(10);
        combatDefinitions.setImmunityTypes(EnumSet.allOf(ImmunityType.class));
        setHitpoints(combatDefinitions.getHitpoints());
    }

    @Override
    public NPC spawn() {
        Web npc = (Web) super.spawn();
        npc.setSpawned(true);
        WorldObject webObject = new WorldObject(40037, 10, 0, getLocation());
        World.spawnObject(webObject);
        WorldTasksManager.schedule(() -> {
            if (isDead() || isFinished()) {
                return;
            }
            if (raid.getActiveRoom().isCompleted()) {
                World.removeObject(World.getObjectWithId(getLocation(),40037));
                finish();
                return;
            }
            if (((VerzikRoom) raid.getActiveRoom()).getVerzik().getPhase().getOrdinal() != 4) {
                World.removeObject(World.getObjectWithId(getLocation(),40037));
                finish();
                return;
            }
            VerzikPhase3 phase3 = (VerzikPhase3) ((VerzikRoom) raid.getActiveRoom()).getVerzik().getPhase();
            phase3.removeWeb(this, false);
            World.removeObject(World.getObjectWithId(getLocation(),40037));
            finish();
        }, 20);
        return npc;
    }

    @Override
    public void onFinish(final Entity source) {
        VerzikPhase3 phase3 = (VerzikPhase3) ((VerzikRoom) raid.getActiveRoom()).getVerzik().getPhase();
        phase3.removeWeb(this, true);
        World.removeObject(World.getObjectWithId(getLocation(),40037));
        super.onFinish(source);
    }
}
