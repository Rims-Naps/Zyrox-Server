package com.zenyte.game.content.chambersofxeric.map;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 15. nov 2017 : 22:13.24
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public final class WrapperChunk extends DynamicArea {

    /**
     * The height level from which the room is copied, and to which it is copied.
     */
    @Getter
    private final int fromPlane, toPlane;
    /**
     * The rotation of this wrapper chunk.
     */
    private final int rotation;

    WrapperChunk(final int sizeX, final int sizeY, final int regionX, final int regionY, final int chunkX, final int chunkY, final int fromPlane, final int toPlane,
                 final int rotation) {
        super(sizeX, sizeY, regionX, regionY, chunkX, chunkY);
        this.fromPlane = fromPlane;
        this.toPlane = toPlane;
        this.rotation = rotation;
    }

    @Override
    public void constructRegion() {
        if (constructed)
            return;
        try {
            constructed = true;
            MapBuilder.copySquare(area, 1, staticChunkX, staticChunkY, fromPlane, chunkX, chunkY, toPlane, rotation);
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @Override
    public String name() {
        return "Wrapper chunk";
    }

    @Override
    public void constructed() {

    }

    @Override
    protected void cleared() {

    }

    @Override
    public void enter(final Player player) {

    }

    @Override
    public void leave(final Player player, boolean logout) {

    }

}
