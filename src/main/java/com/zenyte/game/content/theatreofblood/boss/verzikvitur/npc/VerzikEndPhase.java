package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Cresinkel
 */

public class VerzikEndPhase extends VerzikPhase {


    private final Location throneLocation;

    public VerzikEndPhase(VerzikVitur verzik, Location throneLocation) {
        super(verzik, 5);
        this.throneLocation = throneLocation;
    }

    @Override
    public void onPhaseStart() {
        TheatreOfBloodRaid raid = verzik.getRaid();
        for (val p : raid.getParty().getPlayers()) {
            p.getVarManager().sendVar(3806, 0);
        }
        raid.complete();
        WorldObject newThrone = new WorldObject(32737, 10, 0, throneLocation);
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if(ticks == 0) {
                    verzik.resetFreeze();
                    verzik.reset();
                    verzik.cancelCombat();
                    verzik.setAnimation(Animation.STOP);
                }
                if(ticks == 1) {
                    verzik.setTransformation(NpcId.VERZIK_VITUR_8375);
                }
                if(ticks == 4) {
                    verzik.finish();
                    World.removeObject(new WorldObject(32686, 10, 0, throneLocation.transform(-1,0,0)));
                    World.spawnObject(newThrone);
                }
                if(ticks == 5) {
                    World.sendObjectAnimation(newThrone, new Animation(8108));
                }
                if(ticks == 7) {
                    World.removeObject(new WorldObject(32737, 10, 0, throneLocation));
                    World.spawnObject(new WorldObject(32738, 10, 0, throneLocation));
                    stop();
                }
                ticks++;
            }
        }, 1, 1);
    }


    @Override
    public void onTick() {

    }

    @Override
    public boolean isPhaseComplete() {
        return false;
    }

    @Override
    public VerzikPhase advance() {
        return null;
    }

}
