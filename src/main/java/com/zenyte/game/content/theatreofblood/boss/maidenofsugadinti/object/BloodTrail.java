package com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.object;

import com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.npc.MaidenOfSugadinti;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Corey
 * @since 31/05/2020
 */
public final class BloodTrail extends WorldObject {
    
    private final MaidenOfSugadinti maiden;
    private final Location tile;
    
    private int ticks = 31;
    
    public BloodTrail(final MaidenOfSugadinti maiden, final Location tile) {
        super(32984, 10, Utils.random(3), tile);
        this.maiden = maiden;
        this.tile = tile;
    }
    
    public boolean process() {
        if (maiden.dead()) {
            return false;
        }
        switch (--ticks) {
            case 30:
                maiden.addSplat(tile);
                World.spawnObject(this);
                return true;
            case 0:
                remove();
                return false;
            default:
                return true;
        }
    }
    
    public void remove() {
        maiden.removeSplat(tile);
        World.removeObject(this);
    }
    
    public void resetTimer() {
        ticks = 30;
    }
    
}
