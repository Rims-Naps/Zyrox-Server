package com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.npc;

import com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.MaidenOfSugadintiRoom;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NpcId;
import lombok.Getter;

/**
 * @author Corey
 * @since 26/05/2020
 */
public class NylocasMatomenos extends TheatreNPC<MaidenOfSugadintiRoom> {

    
    private final MaidenOfSugadinti maiden;
    @Getter
    private boolean dying;
    
    public NylocasMatomenos(final MaidenOfSugadinti maiden, final Location tile) {
        super(maiden.getRaid(), maiden.getRoom(), NpcId.NYLOCAS_MATOMENOS, maiden.getRoom().getLocation(tile));
        this.maiden = maiden;
        setFaceEntity(maiden);
        setDeathDelay(2);
    }
    
    @Override
    public void processNPC() {
        if (dying) {
            return;
        }
    
        if (!hasWalkSteps() && !isFrozen() && Utils.getDistance(getX(), getY(), maiden.getMiddleLocation().getX(), maiden.getMiddleLocation().getY()) < 6) {
            if (!dying) {
                absorb();
            }
            return;
        }
    
        if (!isFrozen() && !hasWalkSteps()) {
            addWalkSteps(maiden.getLocation().getX(), maiden.getLocation().getY());
        }
    }
    
    @Override
    public void sendDeath() {
        super.sendDeath();
        dying = true;
    }
    
    private void absorb() {
        maiden.absorbNylocas(this);
        setHitpoints(0);
    }
    
    @Override
    public void autoRetaliate(final Entity source) {
    }
}





























