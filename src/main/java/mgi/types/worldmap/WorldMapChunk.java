package mgi.types.worldmap;

import com.zenyte.Game;
import lombok.ToString;
import lombok.val;
import mgi.utilities.ByteBuffer;


/**
 * @author Tommeh | 4-12-2018 | 19:24
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@ToString(callSuper = true)
public class WorldMapChunk extends WorldMapNode {

    public int chunkX, chunkY;
    public int field332, field328;
    public int check1, check2, expectedRegionX, expectedRegionY, expectedChunkX, expectedChunkY;

    public int a, b;

    public void decode(final ByteBuffer compositeMapBuffer) {
        check1 = compositeMapBuffer.readUnsignedByte();
        super.minPlane = compositeMapBuffer.readUnsignedByte();
        super.maxPlane = compositeMapBuffer.readUnsignedByte();
        super.centerRegionX = compositeMapBuffer.readUnsignedShort();
        super.centerRegionY = compositeMapBuffer.readUnsignedShort();
        this.field332 = compositeMapBuffer.readUnsignedByte();
        this.field328 = compositeMapBuffer.readUnsignedByte();
        super.regionX = compositeMapBuffer.readUnsignedShort();
        super.regionY = compositeMapBuffer.readUnsignedShort();
        this.chunkX = compositeMapBuffer.readUnsignedByte();
        this.chunkY = compositeMapBuffer.readUnsignedByte();
        super.maxPlane = Math.min(super.maxPlane, 4);
        super.underlays = new short[1][64][64];
        super.overlays = new short[super.maxPlane][64][64];
        super.overlayShapes = new byte[super.maxPlane][64][64];
        super.overlayRotations = new byte[super.maxPlane][64][64];
        super.objects = new WorldMapGameObject[super.maxPlane][64][64][];

        a = compositeMapBuffer.readBigSmart();
        b = compositeMapBuffer.readBigSmart();

        val archive = Game.getCacheMgi().getArchive(18);
        val group = archive.findGroupByID(a);
        val file = group.findFileByID(b);
        val areaBuffer = file.getData();

        check2 = areaBuffer.readUnsignedByte();
        expectedRegionX = areaBuffer.readUnsignedByte();
        expectedRegionY = areaBuffer.readUnsignedByte();
        expectedChunkX = areaBuffer.readUnsignedByte();
        expectedChunkY = areaBuffer.readUnsignedByte();
        if (expectedRegionX == super.regionX && expectedRegionY == super.regionY && expectedChunkX == this.chunkX && expectedChunkY == this.chunkY) {
            for (int chunkXOffset = 0; chunkXOffset < 8; ++chunkXOffset) {
                for (int chunkYOffset = 0; chunkYOffset < 8; ++chunkYOffset) {
                    this.decodeTile(chunkXOffset + this.chunkX * 8, chunkYOffset + this.chunkY * 8, areaBuffer);
                }
            }
        }
    }
}
