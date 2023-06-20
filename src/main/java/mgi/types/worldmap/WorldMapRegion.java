package mgi.types.worldmap;

import com.zenyte.Game;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import mgi.utilities.ByteBuffer;

/**
 * @author Tommeh | 4-12-2018 | 18:45
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@ToString(callSuper = true)
@Setter
public class WorldMapRegion extends WorldMapNode {

    public int compositeMapCheck, areaCheck, expectedRegionX, expectedRegionY;

    public int a, b;

    public void decode(final ByteBuffer compositeMapBuffer) {
        compositeMapCheck = compositeMapBuffer.readUnsignedByte();
        if (compositeMapCheck != 0) {
            throw new IllegalStateException();
        } else {
            super.minPlane = compositeMapBuffer.readUnsignedByte();
            super.maxPlane = compositeMapBuffer.readUnsignedByte();
            super.centerRegionX = compositeMapBuffer.readUnsignedShort();
            super.centerRegionY = compositeMapBuffer.readUnsignedShort();
            super.regionX = compositeMapBuffer.readUnsignedShort();
            super.regionY = compositeMapBuffer.readUnsignedShort();

            a = compositeMapBuffer.readBigSmart();
            b = compositeMapBuffer.readBigSmart();

            val archive = Game.getCacheMgi().getArchive(18);
            val group = archive.findGroupByID(a);
            val file = group.findFileByID(b);
            val areaBuffer = file.getData();
            areaBuffer.setPosition(0);

            super.maxPlane = Math.min(maxPlane, 4);
            super.underlays = new short[1][64][64];
            super.overlays = new short[maxPlane][64][64];
            super.overlayShapes = new byte[maxPlane][64][64];
            super.overlayRotations = new byte[maxPlane][64][64];
            super.objects = new WorldMapGameObject[maxPlane][64][64][];

            areaCheck = areaBuffer.readUnsignedByte();
            if (areaCheck != 0) {
                throw new IllegalStateException();
            } else {
                expectedRegionX = areaBuffer.readUnsignedByte();
                expectedRegionY = areaBuffer.readUnsignedByte();
                if (expectedRegionX == regionX && expectedRegionY == regionY) {
                    for (int x = 0; x < 64; ++x) {
                        for (int y = 0; y < 64; ++y) {
                            this.decodeTile(x, y, areaBuffer);
                        }
                    }
                } else {
                    throw new IllegalStateException();
                }
            }
        }
    }
}
