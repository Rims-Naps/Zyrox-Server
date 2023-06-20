package mgi.types.config;

import com.zenyte.Game;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.*;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.File;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;

/**
 * @author Kris | 12. dets 2017 : 1:19.15
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public final class ObjectDefinitions implements Definitions, Cloneable, TransmogrifiableType {

    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String name;
    public static ObjectDefinitions[] definitions;
    @Getter
    @Setter
    private int varbit;
    @Getter
    @Setter
    private int optionsInvisible;
    @Getter
    @Setter
    private int[] models;
    @Getter
    @Setter
    private int[] types;
    @Getter @Setter
    private int[] transformedIds;
    @Getter
    @Setter
    private int ambientSoundId;
    @Getter
    private int varp;
    @Getter
    private int supportItems;
    @Getter
    private int[] anIntArray100;
    @Getter
    private int mapIconId;
    @Getter
    @Setter
    private int sizeX;
    @Getter
    @Setter
    private int clipType;
    @Getter
    private boolean isRotated;
    @Getter
    @Setter
    private int sizeY;
    @Getter
    @Setter
    private boolean projectileClip;
    @Getter
    @Setter
    private int anInt455;
    @Getter
    private boolean nonFlatShading;
    @Getter
    @Setter
    private int contouredGround;
    @Getter
    private int anInt456;
    @Getter
    private boolean modelClipped;
    @Getter
    private int ambient;
    @Getter
    @Setter
    private String[] options;
    @Getter
    @Setter
    private int contrast;
    @Getter
    private int anInt457;
    @Getter
    private boolean hollow;
    @Getter
    @Setter
    private int animationId;
    @Getter
    @Setter
    private int modelSizeX;
    @Getter
    private int decorDisplacement;
    @Getter
    @Setter
    private int modelSizeHeight;
    @Getter
    @Setter
    private int modelSizeY;
    @Getter
    @Setter
    private int[] modelColours;
    @Getter
    private boolean clipped;
    @Getter
    private short[] modelTexture;
    @Getter
    private int mapSceneId;
    @Getter
    @Setter
    private int[] replacementColours;
    @Getter
    @Setter
    private int offsetX;
    @Getter
    private short[] replacementTexture;
    @Getter
    @Setter
    private int offsetHeight;
    @Getter
    @Setter
    private int offsetY;
    @Getter
    private boolean obstructsGround;
    @Getter
    @Setter
    private int accessBlockFlag;
    @Getter
    @Setter
    private int finalTransformation;
    @Getter
    @Setter
    private Int2ObjectOpenHashMap<Object> parameters;

    public ObjectDefinitions() {
        setDefaults();
    }

    public ObjectDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        setDefaults();
        decode(buffer);
    }

    public static ObjectDefinitions getOrThrow(final int id) {
        val object = get(id);
        if (object == null) {
            throw new IllegalStateException();
        }
        return object;
    }

    public static ObjectDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length) {
            return null;
        }

        return definitions[id];
    }

    @Override
    public int defaultId() {
        return finalTransformation;
    }

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val configs = cache.getArchive(ArchiveType.CONFIGS);
        val objects = configs.findGroupByID(GroupType.OBJECT);
        definitions = new ObjectDefinitions[objects.getHighestFileId()];//Hard cap at 40k for now.
        for (int id = 0; id < objects.getHighestFileId(); id++) {
            val file = objects.findFileByID(id);
            if (file == null) {
                continue;
            }
            val buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            definitions[id] = new ObjectDefinitions(id, buffer);
        }
    }

    private void setDefaults() {
        name = "null";
        sizeX = 1;
        sizeY = 1;
        clipType = 2;
        projectileClip = true;
        optionsInvisible = -1;
        contouredGround = -1;
        nonFlatShading = false;
        modelClipped = false;
        animationId = -1;
        decorDisplacement = 16;
        ambient = 0;
        contrast = 0;
        options = new String[5];
        mapIconId = -1;
        mapSceneId = -1;
        isRotated = false;
        clipped = true;
        modelSizeX = 128;
        modelSizeHeight = 128;
        modelSizeY = 128;
        offsetX = 0;
        offsetHeight = 0;
        offsetY = 0;
        obstructsGround = false;
        hollow = false;
        supportItems = -1;
        varbit = -1;
        varp = -1;
        ambientSoundId = -1;
        anInt455 = 0;
        anInt456 = 0;
        anInt457 = 0;
    }

    public boolean containsOption(final int i, final String option) {
        if (options == null || options[i] == null || options.length <= i) {
            return false;
        }
        return options[i].equals(option);
    }

    public boolean containsOption(final String o) {
        if (options == null) {
            return false;
        }
        for (final String option : options) {
            if (option == null) {
                continue;
            }
            if (option.equalsIgnoreCase(o)) {
                return true;
            }
        }
        return false;
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
        switch (opcode) {
            case 1:
                var size = buffer.readUnsignedByte();
                if (size > 0) {
                    types = new int[size];
                    models = new int[size];
                    for (int index = 0; index < size; index++) {
                        models[index] = buffer.readUnsignedShort();
                        types[index] = buffer.readUnsignedByte();
                    }
                }
                return;
            case 2:
                name = buffer.readString();
                return;
            case 5:
                size = buffer.readUnsignedByte();
                if (size > 0) {
                    types = null;
                    models = new int[size];
                    for (int index = 0; index < size; index++) {
                        models[index] = buffer.readUnsignedShort();
                    }
                    return;
                }
                return;
            case 14:
                sizeX = buffer.readUnsignedByte();
                return;
            case 15:
                sizeY = buffer.readUnsignedByte();
                return;
            case 17:
                clipType = 0;
                projectileClip = false;
                return;
            case 18:
                projectileClip = false;
                return;
            case 19:
                optionsInvisible = buffer.readUnsignedByte();
                return;
            case 21:
                contouredGround = 0;
                return;
            case 22:
                nonFlatShading = true;
                return;
            case 23:
                modelClipped = true;
                return;
            case 24:
                animationId = buffer.readUnsignedShort();
                if (animationId == 65535) {
                    animationId = -1;
                }
                return;
            case 27:
                clipType = 1;
                return;
            case 28:
                decorDisplacement = buffer.readUnsignedByte();
                return;
            case 29:
                ambient = buffer.readByte();
                return;
            case 39:
                contrast = buffer.readByte() * 25;
                return;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                options[opcode - 30] = buffer.readString();
                if (options[opcode - 30].equalsIgnoreCase("Hidden")) {
                    options[opcode - 30] = null;
                }
                return;
            case 40:
                size = buffer.readUnsignedByte();
                modelColours = new int[size];
                replacementColours = new int[size];
                for (int count = 0; count < size; count++) {
                    modelColours[count] = (short) buffer.readUnsignedShort();
                    replacementColours[count] = (short) buffer.readUnsignedShort();
                }
                return;
            case 41:
                size = buffer.readUnsignedByte();
                modelTexture = new short[size];
                replacementTexture = new short[size];
                for (int count = 0; count < size; count++) {
                    modelTexture[count] = (short) buffer.readUnsignedShort();
                    replacementTexture[count] = (short) buffer.readUnsignedShort();
                }
                return;
            case 62:
                isRotated = true;
                return;
            case 64:
                clipped = false;
                return;
            case 65:
                modelSizeX = buffer.readUnsignedShort();// useless.
                return;
            case 66:
                modelSizeHeight = buffer.readUnsignedShort();// useless
                return;
            case 67:
                modelSizeY = buffer.readUnsignedShort();// useless
                return;
            case 68:
                mapSceneId = buffer.readUnsignedShort();
                return;
            case 69:
                accessBlockFlag = buffer.readUnsignedByte();
                return;
            case 70:
                offsetX = buffer.readShort();
                return;
            case 71:
                offsetHeight = buffer.readShort();
                return;
            case 72:
                offsetY = buffer.readShort();
                return;
            case 73:
                obstructsGround = true;
                return;
            case 74:
                hollow = true;
                return;
            case 75:
                supportItems = buffer.readUnsignedByte();
                return;
            case 77:
            case 92:
                varbit = buffer.readUnsignedShort();
                if (varbit == 65535) {
                    varbit = -1;
                }
                varp = buffer.readUnsignedShort();
                if (varp == 65535) {
                    varp = -1;
                }
                finalTransformation = -1;
                if (opcode == 92) {
                    finalTransformation = buffer.readUnsignedShort();
                    if (finalTransformation == 65535) {
                        finalTransformation = -1;
                    }
                }
                size = buffer.readUnsignedByte();
                transformedIds = new int[size + 2];
                for (int index = 0; index <= size; index++) {
                    transformedIds[index] = buffer.readUnsignedShort();
                    if (transformedIds[index] == 65535) {
                        transformedIds[index] = -1;
                    }
                }
                transformedIds[size + 1] = finalTransformation;
                return;
            case 78:
                ambientSoundId = buffer.readUnsignedShort();
                anInt455 = buffer.readUnsignedByte();
                return;
            case 79:
                anInt456 = buffer.readUnsignedShort();
                anInt457 = buffer.readUnsignedShort();
                anInt455 = buffer.readUnsignedByte();
                size = buffer.readUnsignedByte();
                anIntArray100 = new int[size];
                for (int count = 0; count < size; count++) {
                    anIntArray100[count] = buffer.readUnsignedShort();
                }
                return;
            case 81:
                contouredGround = buffer.readUnsignedByte() * 256;
                return;
            case 82:
                mapIconId = buffer.readUnsignedShort();
                return;
            case 249:
                parameters = buffer.readParameters();
                return;
            default:
                System.err.println("UNSUPPORTED OBJECT OPCODE: " + opcode);
                return;
        }
    }

    public String getOption(final int option) {
        if (options == null || options.length < option || option == 0) {
            return "";
        }
        return options[option - 1];
    }

    @Override
    public ByteBuffer encode() {
        val buffer = new ByteBuffer(1024);
        if (types != null) {
            buffer.writeByte(1);
            buffer.writeByte(types.length);
            for (int count = 0; count < types.length; count++) {
                buffer.writeShort(models[count]);
                buffer.writeByte(types[count]);
            }
        }
        if (!name.equals("null")) {
            buffer.writeByte(2);
            buffer.writeString(name);
        }
        if (models != null) {
            buffer.writeByte(5);
            buffer.writeByte(models.length);
            if (models.length > 0) {
                for (val model : models) {
                    buffer.writeShort(model);
                }
            }
        }
        if (sizeX != 1) {
            buffer.writeByte(14);
            buffer.writeByte(sizeX);
        }
        if (sizeY != 1) {
            buffer.writeByte(15);
            buffer.writeByte(sizeY);
        }
        if (clipType == 0 && !projectileClip) {
            buffer.writeByte(17);
        }
        if (!projectileClip) {
            buffer.writeByte(18);
        }
        if (optionsInvisible != -1) {
            buffer.writeByte(19);
            buffer.writeByte(optionsInvisible);
        }
        if (contouredGround == 0) {
            buffer.writeByte(21);
        }
        if (nonFlatShading) {
            buffer.writeByte(22);
        }
        if (modelClipped) {
            buffer.writeByte(23);
        }
        if (animationId != -1) {
            buffer.writeByte(24);
            buffer.writeShort(animationId);
        }
        if (clipType == 1) {
            buffer.writeByte(27);
        }
        if (decorDisplacement != 16) {
            buffer.writeByte(28);
            buffer.writeByte(decorDisplacement);
        }
        if (ambient != 0) {
            buffer.writeByte(29);
            buffer.writeByte(ambient);
        }
        for (int index = 0; index < 5; ++index) {
            if (options[index] == null) {
                continue;
            }
            buffer.writeByte((30 + index));
            val option = options[index];
            buffer.writeString(option);
        }
        if (contrast != 0) {
            buffer.writeByte(39);
            buffer.writeByte((contrast / 25));
        }
        if (modelColours != null && replacementColours != null && modelColours.length != 0 && replacementColours.length != 0) {
            buffer.writeByte(40);
            buffer.writeByte(modelColours.length);
            for (int index = 0; index < modelColours.length; ++index) {
                buffer.writeShort(modelColours[index]);
                buffer.writeShort(replacementColours[index]);
            }
        }
        if (modelTexture != null && replacementTexture != null && modelTexture.length != 0 && replacementTexture.length != 0) {
            buffer.writeByte(41);
            buffer.writeByte(modelTexture.length);
            for (int index = 0; index < modelTexture.length; ++index) {
                buffer.writeShort(modelTexture[index]);
                buffer.writeShort(replacementTexture[index]);
            }
        }
        if (isRotated) {
            buffer.writeByte(62);
        }
        if (!clipped) {
            buffer.writeByte(64);
        }
        if (modelSizeX != 128) {
            buffer.writeByte(65);
            buffer.writeShort(modelSizeX);
        }
        if (modelSizeHeight != 128) {
            buffer.writeByte(66);
            buffer.writeShort(modelSizeHeight);
        }
        if (modelSizeY != 128) {
            buffer.writeByte(67);
            buffer.writeShort(modelSizeY);
        }
        if (mapSceneId != -1) {
            buffer.writeByte(68);
            buffer.writeShort(mapSceneId);
        }
        if (accessBlockFlag != 0) {
            buffer.writeByte(69);
            buffer.writeByte(accessBlockFlag);
        }
        if (offsetX != 0) {
            buffer.writeByte(70);
            buffer.writeShort(offsetX);
        }
        if (offsetHeight != 0) {
            buffer.writeByte(71);
            buffer.writeShort(offsetHeight);
        }
        if (offsetY != 0) {
            buffer.writeByte(72);
            buffer.writeShort(offsetY);
        }
        if (obstructsGround) {
            buffer.writeByte(73);
        }
        if (hollow) {
            buffer.writeByte(74);
        }
        if (supportItems != -1) {
            buffer.writeByte(75);
            buffer.writeByte(supportItems);
        }
        if (ambientSoundId != -1) {
            buffer.writeByte(78);
            buffer.writeShort(ambientSoundId);
            buffer.writeByte(anInt455);
        }
        if (anIntArray100 != null && anIntArray100.length != 0) {
            buffer.writeByte(79);
            buffer.writeShort(anInt456);
            buffer.writeShort(anInt457);
            buffer.writeByte(anInt455);
            buffer.writeByte(anIntArray100.length);
            for (val value : anIntArray100) {
                buffer.writeShort(value);
            }
        }
        if (contouredGround != -1) {
            buffer.writeByte(81);
            buffer.writeByte(contouredGround / 256);
        }
        if (mapIconId != -1) {
            buffer.writeByte(82);
            buffer.writeShort(mapIconId);
        }
        if (transformedIds != null) {
            buffer.writeByte(77);
            buffer.writeShort(varbit);
            buffer.writeShort(varp);

            buffer.writeByte((transformedIds.length - 2));
            for (int i = 0; i <= transformedIds.length - 2; ++i) {
                buffer.writeShort(transformedIds[i]);
            }

            buffer.writeByte(92);
            buffer.writeShort(varbit);
            buffer.writeShort(varp);
            buffer.writeShort(finalTransformation);

            buffer.writeByte((transformedIds.length - 2));
            for (int i = 0; i <= transformedIds.length - 2; ++i) {
                buffer.writeShort(transformedIds[i]);
            }
        }
        if (parameters != null && !parameters.isEmpty()) {
            buffer.writeByte(249);
            buffer.writeParameters(parameters);
        }
        buffer.writeByte(0);
        return buffer;
    }

    @Override
    public void pack() {
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.OBJECT).addFile(new File(id, encode()));
    }

    @Override
    public int getVarbitId() {
        return varbit;
    }

    @Override
    public int getVarpId() {
        return varp;
    }

    @Override
    public int[] getTransmogrifiedIds() {
        return this.transformedIds;
    }

    public void setOption(final int index, final String option) {
        if (options == null) {
            options = new String[5];
        }
        options[index] = option.isEmpty() ? null : option;
    }
}
