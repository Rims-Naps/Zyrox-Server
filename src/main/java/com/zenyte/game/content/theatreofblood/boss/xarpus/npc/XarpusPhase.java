package com.zenyte.game.content.theatreofblood.boss.xarpus.npc;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * @author Chris
 * @since August 25 2020
 */
@RequiredArgsConstructor
public abstract class XarpusPhase {
    protected final Xarpus xarpus;
    protected final MutableInt ticks = new MutableInt();

    public XarpusPhase process() {
        if (isPhaseComplete()) {
            val nextPhase = advance();
            if (nextPhase != null) {
                nextPhase.onPhaseStart();
                nextPhase.onTick();
                return nextPhase;
            }
        }
        onTick();
        ticks.increment();
        return this;
    }

    abstract void onPhaseStart();

    abstract void onTick();

    abstract boolean isPhaseComplete();

    abstract XarpusPhase advance();
}