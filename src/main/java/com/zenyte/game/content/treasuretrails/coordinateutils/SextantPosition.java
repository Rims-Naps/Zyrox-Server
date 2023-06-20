package com.zenyte.game.content.treasuretrails.coordinateutils;

import com.google.common.base.Preconditions;
import lombok.Data;

/**
 * @author Kris | 02/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public final class SextantPosition {

    static final int BASE_FRAME_POSITION = 934;
    static final int BASE_ARM_POSITION = 949;
    static final int FRAME_POSITIONS_COUNT = 15;
    static final int ARM_POSITIONS_COUNT = 58;


    SextantPosition(final int framePosition, final int armPosition) {
        Preconditions.checkArgument(framePosition >= 0);
        Preconditions.checkArgument(armPosition >= 0);
        Preconditions.checkArgument(framePosition < FRAME_POSITIONS_COUNT);
        Preconditions.checkArgument(armPosition < ARM_POSITIONS_COUNT);
        this.framePosition = framePosition;
        this.armPosition = armPosition;
    }

    private final int framePosition;
    private final int armPosition;

}
