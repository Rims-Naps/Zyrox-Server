package mgi.types.worldmap;

import lombok.Getter;
import lombok.ToString;
import mgi.utilities.ByteBuffer;


/**
 * @author Tommeh | 2-12-2018 | 19:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@ToString
public class ChunkWMArea implements WorldMapType {

    private int plane;
    private int numberOfPlanes;
    private int minRegionX;
    private int minRegionY;
    private int maxRegionX;
    private int maxRegionY;
    private int minChunkX;
    private int minChunkY;
    private int maxChunkX;
    private int maxChunkY;

    @Override
    public void decode(final ByteBuffer buffer) {
        plane = buffer.readUnsignedByte();
        numberOfPlanes = buffer.readUnsignedByte();
        minRegionX = buffer.readUnsignedShort();
        minChunkX = buffer.readUnsignedByte();
        minRegionY = buffer.readUnsignedShort();
        minChunkY = buffer.readUnsignedByte();
        maxRegionX = buffer.readUnsignedShort();
        maxChunkX = buffer.readUnsignedByte();
        maxRegionY = buffer.readUnsignedShort();
        maxChunkY = buffer.readUnsignedByte();
    }

    @Override
    public void encode(final ByteBuffer buffer) {
        buffer.writeByte(3);
        buffer.writeByte(plane);
        buffer.writeByte(numberOfPlanes);
        buffer.writeShort(minRegionX);
        buffer.writeByte(minChunkX);
        buffer.writeShort(minRegionY);
        buffer.writeByte(minChunkY);
        buffer.writeShort(maxRegionX);
        buffer.writeByte(maxChunkX);
        buffer.writeShort(maxRegionY);
        buffer.writeByte(maxChunkY);
    }
}
