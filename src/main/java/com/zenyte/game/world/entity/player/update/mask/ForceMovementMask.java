package com.zenyte.game.world.entity.player.update.mask;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.update.UpdateMask;
import com.zenyte.network.io.RSBuffer;

/**
 * @author Kris | 7. mai 2018 : 16:56:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ForceMovementMask extends UpdateMask {

    @Override
    public UpdateFlag getFlag() {
        return UpdateFlag.FORCE_MOVEMENT;
    }

    @Override
    public void writePlayer(final RSBuffer buffer, final Player player, final Player processedPlayer) {
        ForceMovement forceMovement = processedPlayer.getForceMovement();
        if (forceMovement == null) {
            forceMovement = new ForceMovement(processedPlayer.getLocation(), 1, 0);
        }
        final Location first = forceMovement.getToFirstTile();
        final Location second = forceMovement.getToSecondTile();
        final int x = processedPlayer.getX();
        final int y = processedPlayer.getY();

        buffer.write128Byte(first == null ? 0 : first.getX() - x);
        buffer.writeByte128(first == null ? 0 : first.getY() - y);

        buffer.writeByte128(second == null ? 0 : second.getX() - x);
        buffer.write128Byte(second == null ? 0 : second.getY() - y);
        buffer.writeShort128(forceMovement.getFirstTileTicketDelay());
        buffer.writeShortLE(forceMovement.getSecondTileTicketDelay());
        buffer.writeShort(forceMovement.getDirection());
    }

}
