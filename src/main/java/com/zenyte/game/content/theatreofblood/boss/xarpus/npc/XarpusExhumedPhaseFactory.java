package com.zenyte.game.content.theatreofblood.boss.xarpus.npc;

import org.jetbrains.annotations.NotNull;

/**
 * @author Chris
 * @since September 02 2020
 */
public class XarpusExhumedPhaseFactory {
    public static XarpusExhumedPhase getPhase(@NotNull final Xarpus xarpus, final int partySize) {
        switch (partySize) {
            case 1:
                return new XarpusExhumedPhase(xarpus, 7, 20, 13, 9);
            case 2:
                return new XarpusExhumedPhase(xarpus, 8, 16, 8, 9);
            case 3:
                return new XarpusExhumedPhase(xarpus, 12, 12, 8, 9);
            case 4:
                return new XarpusExhumedPhase(xarpus, 15, 9, 4, 9);
            case 5:
                return new XarpusExhumedPhase(xarpus, 18, 6, 4, 9);
            default:
                throw new IllegalArgumentException("Invalid party size of " + partySize);
        }
    }
}
