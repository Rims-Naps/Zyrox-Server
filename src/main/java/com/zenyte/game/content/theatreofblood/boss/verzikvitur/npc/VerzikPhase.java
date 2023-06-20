package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableInt;

@RequiredArgsConstructor
public abstract class VerzikPhase {

    protected final VerzikVitur verzik;
    protected final MutableInt ticks = new MutableInt();
    protected final int ordinal;

    public VerzikPhase process() {
        if(isPhaseComplete()) {
            val nextPhase = advance();
            if(nextPhase != null) {
                nextPhase.onPhaseStart();
                nextPhase.onTick();
                return nextPhase;
            }
        }
        onTick();
        ticks.increment();
        return this;
    }

    protected void resetTicks() {
        this.ticks.setValue(0);
    }

    public abstract void onPhaseStart();

    public abstract void onTick();

    public abstract boolean isPhaseComplete();

    public abstract VerzikPhase advance();

    public int getOrdinal() {
        return this.ordinal;
    }
}
