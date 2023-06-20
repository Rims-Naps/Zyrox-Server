package mgi.types.worldmap;

import com.zenyte.Game;
import mgi.types.Definitions;
import lombok.*;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.utilities.ByteBuffer;

/**
 * @author Tommeh | 6-12-2018 | 23:21
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@ToString
@Setter
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
public class MapElementDefinitions implements Definitions {

    private static MapElementDefinitions[] definitions;

    private int id;
    private int spriteId;
    private int field3306;//always -1
    private String text;
    private int colour;
    private int textSize;
    private String[] options;
    private String optionName;
    private int[] field3312;//always null
    private int field3313;//always 2147483647
    private int field3314;//always 2147483647
    private int field3315;//always -2147483648
    private int field3316;//always -2147483648
    private int horizontalAlignment;
    private int verticalAlignment;
    private int[] field3307;//always null
    private byte[] field3320;//always null

    //Used for getting string value from enum 1713
    private int tooltipId;

    public int getGroupId() {
        return id << 8 | 10;//10 = index of the string, Open is first so it's 10; effectively 10 + index.
    }

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val configs = cache.getArchive(ArchiveType.CONFIGS);
        val labels = configs.findGroupByID(GroupType.MAP_LABELS);
        definitions = new MapElementDefinitions[labels.getHighestFileId()];
        for (int id = 0; id < labels.getHighestFileId(); id++) {
            val file = labels.findFileByID(id);
            if (file == null) {
                continue;
            }
            val buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            definitions[id] = new MapElementDefinitions(id, buffer);
        }
    }

    public static MapElementDefinitions get(final int id) {
        return definitions[id];
    }

    public MapElementDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        this.tooltipId = -1;
        this.spriteId = -1;
        this.field3306 = -1;
        this.textSize = 0;
        this.horizontalAlignment = 1;
        this.verticalAlignment = 1;
        this.options = new String[5];
        this.field3313 = Integer.MAX_VALUE;
        this.field3314 = Integer.MAX_VALUE;
        this.field3315 = Integer.MIN_VALUE;
        this.field3316 = Integer.MIN_VALUE;
        decode(buffer);
    }

    @Override
    public void decode(final ByteBuffer buffer) {
        while (true) {
            val opcode = buffer.readUnsignedByte();
            if (opcode == 0) {
                return;
            }
            decode(buffer, opcode);
        }
    }

    @Override
    public void decode(final ByteBuffer buffer, final int opcode) {
        int var3;
        int var4;
        switch(opcode) {
            case 1:
                spriteId = buffer.readBigSmart();
                return;
            case 2:
                field3306 = buffer.readBigSmart();
                return;
            case 3:
                text = buffer.readString();
                return;
            case 4:
                colour = buffer.readMedium();
                return;
            case 5:
                buffer.readMedium();
                return;
            case 6:
                textSize = buffer.readUnsignedByte();
                return;
            case 7:
                var3 = buffer.readUnsignedByte();
                return;
            case 8:
                buffer.readByte();
                return;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                options[opcode - 10] = buffer.readString();
                return;
            case 15:
                var3 = buffer.readUnsignedByte();
                field3312 = new int[var3 * 2];
                for (int index = 0; index < var3 * 2; index++) {
                    field3312[index] = buffer.readShort();
                }
                
                buffer.readInt();
                var4 = buffer.readUnsignedByte();
                field3307 = new int[var4];
                
                for (int index = 0; index < field3307.length; index++) {
                    field3307[index] = buffer.readInt();
                }
                
                field3320 = new byte[var3];
                
                for (int index = 0; index < var3; index++) {
                    field3320[index] = buffer.readByte();
                }
                return;
            case 17:
                optionName = buffer.readString();
                return;
            case 18:
                buffer.readBigSmart();
                return;
            case 19:
                tooltipId = buffer.readUnsignedShort();
                return;
            case 21:
            case 22:
                buffer.readInt();
                return;
            case 23:
                buffer.readByte();
                buffer.readByte();
                buffer.readByte();
                return;
            case 24:
                buffer.readShort();
                buffer.readShort();
                return;
            case 25:
                buffer.readBigSmart();
                return;
            case 28:
                buffer.readByte();
                return;
            case 29:
                horizontalAlignment = buffer.readUnsignedByte();
                return;
            case 30:
                verticalAlignment = buffer.readUnsignedByte();
                return;
        }
    }

    @Override
    public ByteBuffer encode() {
        val buffer = new ByteBuffer(4096);
        buffer.writeByte(1);
        buffer.writeBigSmart(spriteId);

        buffer.writeByte(2);
        buffer.writeBigSmart(field3306);

        buffer.writeByte(3);
        buffer.writeString(text);

        buffer.writeByte(4);
        buffer.writeMedium(colour);

        buffer.writeByte(5);
        buffer.writeMedium(-1);

        buffer.writeByte(6);
        buffer.writeByte(textSize);

        buffer.writeByte(7);
        buffer.writeByte(-1);

        buffer.writeByte(8);
        buffer.writeByte(-1);

        for (int opcode = 10; opcode <= 14; opcode++) {
            if (options[opcode - 10] != null) {
                buffer.writeByte(opcode);
                buffer.writeString(options[opcode - 10]);
            }
        }

        /*
        buffer.writeByte(15);
        if (field3312 != null) {
            buffer.writeByte((field3312.length / 2));
            for (int index = 0; index < field3312.length / 2; index++) {
                buffer.writeShort(field3312[index]);
            }
        }
        buffer.putInt(-1);
        if (field3307 != null) {
            buffer.writeByte(field3307.length);
            for (int index = 0; index < field3307.length; index++) {
                buffer.putInt(field3307[index]);
            }
        }

        if (field3320 != null) {
            for (int index = 0; index < field3312.length; index++) {
                buffer.put(field3320[index]);
            }
        }*/

        if (optionName != null) {
            buffer.writeByte(17);
            buffer.writeString(optionName);
        }

        buffer.writeByte(18);
        buffer.writeBigSmart(-1);

        buffer.writeByte(19);
        buffer.writeShort(tooltipId);

        buffer.writeByte(21);
        buffer.writeInt(-1);

        buffer.writeByte(23);
        buffer.writeByte(-1);
        buffer.writeByte(-1);
        buffer.writeByte(-1);

        buffer.writeByte(24);
        buffer.writeShort(-1);
        buffer.writeShort(-1);

        buffer.writeByte(25);
        buffer.writeBigSmart(-1);

        buffer.writeByte(28);
        buffer.writeByte(-1);

        buffer.writeByte(29);
        buffer.writeByte(horizontalAlignment);

        buffer.writeByte(30);
        buffer.writeByte(verticalAlignment);

        buffer.writeByte(0);
        return buffer;
    }
}
