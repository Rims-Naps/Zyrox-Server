package mgi.types.config.npcs;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.KryoObjectInput;
import com.zenyte.Constants;
import com.zenyte.Game;
import com.zenyte.game.world.entity.masks.RenderType;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import mgi.tools.parser.TypeProperty;
import mgi.types.Definitions;
import mgi.types.config.TransmogrifiableType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.*;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.File;
import mgi.utilities.ByteBuffer;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public class NPCDefinitions implements Definitions, Cloneable, TransmogrifiableType, RenderType {

    public static NPCDefinitions[] definitions;
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String name;
    @Getter
    private transient String lowercaseName;
    @Getter
    @Setter
    private String[] options;
    @Setter
    @Getter
    private String[] filteredOptions;
    @Getter
    private int filterFlag;
    @Getter
    @Setter
    private int varp, varbit;
    @Getter
    @Setter
    private int[] transmogrifiedIds, models, chatModels;
    @Getter
    @Setter
    private int standAnimation, walkAnimation, rotate90Animation, rotate180Animation, rotate270Animation;
    @Getter
    @Setter
    private int size, combatLevel;
    @Getter
    @Setter
    private boolean minimapVisible, visible, clickable, clippedMovement, isFamiliar;
    @Getter
    @Setter
    private int resizeX, resizeY, direction, headIcon, ambience, contrast;
    @Getter
    @Setter
    private short[] originalColours, replacementColours, originalTextures, replacementTextures;
    @Getter
    private int field3568, field3580;
    @Getter
    @Setter
    private Int2ObjectOpenHashMap<Object> parameters;
    @Getter
    private int finalTransmogrification;

    @Override
    public int defaultId() {
        return finalTransmogrification;
    }

    public NPCDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        setDefaults();
        decode(buffer);
    }

    public NPCDefinitions clone() throws CloneNotSupportedException {
        return (NPCDefinitions) super.clone();
    }

    public static NPCDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length) {
            return null;
        }

        return definitions[id];
    }

    public static NPCDefinitions getOrThrow(final int id) {
        val definitions = NPCDefinitions.definitions[id];
        if (definitions == null) {
            throw new RuntimeException("NPCDefinitions missing for id: " + id);
        }
        return definitions;
    }

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val configs = cache.getArchive(ArchiveType.CONFIGS);
        val npcs = configs.findGroupByID(GroupType.NPC);
        definitions = new NPCDefinitions[20000];
        for (int id = 0; id < npcs.getHighestFileId(); id++) {
            val file = npcs.findFileByID(id);
            if (file == null) {
                continue;
            }
            val buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            buffer.setPosition(0);
            definitions[id] = new NPCDefinitions(id, buffer);
        }
    }

    public static void filter() {
        if (/*Constants.WORLD_PROFILE.isDevelopment() && */!Constants.SPAWN_MODE)
            return;
        System.err.println("Filtering definitions.");
        for (val definition : definitions) {
            if (definition == null)
                continue;
            for (int i = 0; i < 5; i++) {
                val option = definition.filteredOptions[i];
                if (Constants.SPAWN_MODE) {
                    if (i == 0) {
                        definition.filteredOptions[i] = "Teleport to me";
                        definition.filterFlag |= 1 << i;
                        continue;
                    }
                    if (i == 1) {
                        definition.filteredOptions[i] = "Set radius";
                        definition.filterFlag |= 1 << i;
                        continue;
                    }
                    if (i == 4) {
                        definition.filteredOptions[i] = "Remove spawn";
                        definition.filterFlag |= 1 << i;
                        continue;
                    }
                }
                if (option == null)
                    continue;
                if (NPCPlugin.getHandler(definition.id, option) == null) {
                    definition.filteredOptions[i] = null;
                    definition.filterFlag |= 1 << i;
                }

            }
        }
    }

    private void setDefaults() {
        name = lowercaseName = "null";
        size = 1;
        standAnimation = -1;
        walkAnimation = -1;
        rotate180Animation = -1;
        rotate90Animation = -1;
        rotate270Animation = -1;
        field3568 = -1;
        field3580 = -1;
        options = new String[5];
        filteredOptions = new String[5];
        minimapVisible = true;
        combatLevel = -1;
        resizeX = 128;
        resizeY = 128;
        visible = false;
        ambience = 0;
        contrast = 0;
        headIcon = -1;
        direction = 32;
        varbit = -1;
        varp = -1;
        clickable = true;
        clippedMovement = true;
        isFamiliar = false;
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
            case 1: {
                val size = buffer.readUnsignedByte();
                models = new int[size];
                for (int i = 0; i < size; i++) {
                    models[i] = buffer.readUnsignedShort();
                }
                return;
            }
            case 2:
                name = buffer.readString();
                lowercaseName = name.toLowerCase();
                return;
            case 12:
                size = buffer.readUnsignedByte();
                return;
            case 13:
                standAnimation = buffer.readUnsignedShort();
                return;
            case 14:
                walkAnimation = buffer.readUnsignedShort();
                return;
            case 15:
                field3568 = buffer.readUnsignedShort();
                return;
            case 16:
                field3580 = buffer.readUnsignedShort();
                return;
            case 17:
                walkAnimation = buffer.readUnsignedShort();
                rotate180Animation = buffer.readUnsignedShort();
                rotate90Animation = buffer.readUnsignedShort();
                rotate270Animation = buffer.readUnsignedShort();
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
                filteredOptions[opcode - 30] = options[opcode - 30];
                return;
            case 40: {
                final int size = buffer.readUnsignedByte();
                originalColours = new short[size];
                replacementColours = new short[size];
                for (int i = 0; i < size; i++) {
                    originalColours[i] = (short) (buffer.readUnsignedShort());
                    replacementColours[i] = (short) (buffer.readUnsignedShort());
                }
                return;
            }
            case 41: {
                val size = buffer.readUnsignedByte();
                originalTextures = new short[size];
                replacementTextures = new short[size];
                for (int i = 0; i < size; i++) {
                    originalTextures[i] = (short) (buffer.readUnsignedShort());
                    replacementTextures[i] = (short) (buffer.readUnsignedShort());
                }
                return;
            }
            case 60: {
                val size = buffer.readUnsignedByte();
                chatModels = new int[size];
                for (int i = 0; i < size; i++) {
                    chatModels[i] = buffer.readUnsignedShort();
                }
                return;
            }
            case 93:
                minimapVisible = false;
                return;
            case 95:
                combatLevel = buffer.readUnsignedShort();
                return;
            case 97:
                resizeX = buffer.readUnsignedShort();
                return;
            case 98:
                resizeY = buffer.readUnsignedShort();
                return;
            case 99:
                visible = true;
                return;
            case 100:
                ambience = buffer.readByte();
                return;
            case 101:
                contrast = buffer.readByte();
                return;
            case 102:
                headIcon = buffer.readUnsignedShort();
                return;
            case 103:
                direction = buffer.readUnsignedShort();
                return;
            case 106:
            case 118: {
                varbit = buffer.readUnsignedShort();
                if (varbit == 65535) {
                    varbit = -1;
                }
                varp = buffer.readUnsignedShort();
                if (varp == 65535) {
                    varp = -1;
                }
                finalTransmogrification = -1;
                if (opcode == 118) {
                    finalTransmogrification = buffer.readUnsignedShort();
                    if (finalTransmogrification == 65535) {
                        finalTransmogrification = -1;
                    }
                }
                final int size = buffer.readUnsignedByte();
                transmogrifiedIds = new int[size + 2];
                for (int int_3 = 0; int_3 <= size; int_3++) {
                    transmogrifiedIds[int_3] = buffer.readUnsignedShort();
                    if (transmogrifiedIds[int_3] == 65535) {
                        transmogrifiedIds[int_3] = -1;
                    }
                }
                transmogrifiedIds[size + 1] = finalTransmogrification;
                return;
            }
            case 107:
                clippedMovement = false;
                return;
            case 109:
                clickable = false;
                return;
            case 111:
                isFamiliar = true;
                return;
            case 249:
                parameters = buffer.readParameters();
                return;
        }
    }

    public String getOption(final int option) {
        if (options == null || options.length < option || option == 0) {
            return "";
        }
        return options[option - 1];
    }

    public boolean containsOptionCaseSensitive(final String option) {
        return ArrayUtils.contains(options, option);
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
    public mgi.utilities.ByteBuffer encode() {
        val buffer = new ByteBuffer(4 * 1024);
        if (models != null) {
            buffer.writeByte(1);
            buffer.writeByte(models.length);
            for (int index = 0; index < models.length; index++) {
                buffer.writeShort(models[index]);
            }
        }
        if (!name.equals("null")) {
            buffer.writeByte(2);
            buffer.writeString(name);
        }
        if (size != 0) {
            buffer.writeByte(12);
            buffer.writeByte(size);
        }
        if (standAnimation != -1) {
            buffer.writeByte(13);
            buffer.writeShort(standAnimation == 65535 ? -1 : standAnimation);
        }
        val extendedWalkAnimations = (rotate90Animation & 0xFFFF) != 65535 || (rotate180Animation & 0xFFFF) != 65535 || (rotate270Animation & 0xFFFF) != 65535;
        if (walkAnimation != -1 && !extendedWalkAnimations) {
            buffer.writeByte(14);
            buffer.writeShort(walkAnimation == 65535 ? -1 : walkAnimation);
        }
        if (field3568 != -1) {
            buffer.writeByte(15);
            buffer.writeShort(field3568);
        }
        if (field3580 != -1) {
            buffer.writeByte(16);
            buffer.writeShort(field3580);
        }
        if (extendedWalkAnimations) {
            buffer.writeByte(17);
            buffer.writeShort(walkAnimation == 65535 ? -1 : walkAnimation);
            buffer.writeShort(rotate180Animation == 65535 ? -1 : rotate180Animation);
            buffer.writeShort(rotate90Animation == 65535 ? -1 : rotate90Animation);
            buffer.writeShort(rotate270Animation == 65535 ? -1 : rotate270Animation);
        }
        for (int index = 0; index < 5; index++) {
            if (options[index] != null && !options[index].equals("Hidden")) {
                buffer.writeByte((30 + index));
                buffer.writeString(options[index]);
            }
        }
        if (originalColours != null && replacementColours != null && originalColours.length != 0 && replacementColours.length != 0) {
            buffer.writeByte(40);
            buffer.writeByte(originalColours.length);
            for (int index = 0; index < originalColours.length; index++) {
                buffer.writeShort(originalColours[index]);
                buffer.writeShort(replacementColours[index]);
            }
        }

        if (originalTextures != null && replacementTextures != null && originalTextures.length != 0 && replacementTextures.length != 0) {
            buffer.writeByte(41);
            buffer.writeByte(originalTextures.length);
            for (int index = 0; index < originalTextures.length; index++) {
                buffer.writeShort(originalTextures[index]);
                buffer.writeShort(replacementTextures[index]);
            }
        }
        if (chatModels != null) {
            buffer.writeByte(60);
            buffer.writeByte(chatModels.length);
            for (int index = 0; index < chatModels.length; index++) {
                buffer.writeShort(chatModels[index]);
            }
        }
        if (!minimapVisible) {
            buffer.writeByte(93);
        }
        if (combatLevel != -1) {
            buffer.writeByte(95);
            buffer.writeShort(combatLevel);
        }
        if (resizeX != 0) {
            buffer.writeByte(97);
            buffer.writeShort(resizeX);
        }
        if (resizeY != 0) {
            buffer.writeByte(98);
            buffer.writeShort(resizeY);
        }
        if (visible) {
            buffer.writeByte(99);
        }
        if (ambience != 0) {
            buffer.writeByte(100);
            buffer.writeByte(ambience);
        }
        if (contrast != 0) {
            buffer.writeByte(101);
            buffer.writeByte(contrast);
        }
        if (headIcon != -1) {
            buffer.writeByte(102);
            buffer.writeShort(headIcon);
        }
        if (direction != -1) {
            buffer.writeByte(103);
            buffer.writeShort(direction);
        }
        if (!clippedMovement) {
            buffer.writeByte(107);
        }
        if (!clickable) {
            buffer.writeByte(109);
        }
        if (isFamiliar) {
            buffer.writeByte(111);
        }
        if (transmogrifiedIds != null && transmogrifiedIds.length > 0) {
            buffer.writeByte(106);
            buffer.writeShort(varbit == -1 ? 65535 : varbit);
            buffer.writeShort(varp == -1 ? 65535 : varp);
            buffer.writeByte(transmogrifiedIds.length - 2);
            for (int index = 0; index <= transmogrifiedIds.length - 2; index++) {
                buffer.writeShort(transmogrifiedIds[index] == -1 ? 65535 : transmogrifiedIds[index]);
            }
        }
        if (transmogrifiedIds != null) {
            buffer.writeByte(118);
            buffer.writeShort(varbit == -1 ? 65535 : varbit);
            buffer.writeShort(varp == -1 ? 65535 : varp);
            buffer.writeShort(transmogrifiedIds[transmogrifiedIds.length - 1] == -1 ? 65535 : transmogrifiedIds[transmogrifiedIds.length - 1]);
            buffer.writeByte(transmogrifiedIds.length - 2);
            for (int index = 0; index <= transmogrifiedIds.length - 2; index++) {
                buffer.writeShort(transmogrifiedIds[index] == -1 ? 65535 : transmogrifiedIds[index]);
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
        definitions[id] = this;
        try {
            Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.NPC).addFile(new File(id, encode()));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(id);
        }
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
    public int getStand() {
        return this.standAnimation;
    }

    @Override
    public int getStandTurn() {
        return -1;
    }

    @Override
    public int getWalk() {
        return this.walkAnimation;
    }

    @Override
    public int getRotate180() {
        return this.rotate180Animation;
    }

    @Override
    public int getRotate90() {
        return this.rotate90Animation;
    }

    @Override
    public int getRotate270() {
        return this.rotate270Animation;
    }

    @Override
    public int getRun() {
        return this.walkAnimation;
    }

    public void setOption(final int index, final String option) {
        if (options == null) {
            options = new String[5];
        }
        options[index] = option.isEmpty() ? null : option;
    }

    public void setFilteredOption(final int index, final String option) {
        if (filteredOptions == null) {
            filteredOptions = new String[5];
        }
        filteredOptions[index] = option.isEmpty() ? null : option;
    }

    public NPCDefinitions copy() {
        Kryo kryo = new Kryo();
        kryo.register(NPCDefinitions.class);
        kryo.register(int[].class);
        kryo.register(short[].class);
        kryo.register(String[].class);
        return kryo.copy(this);
    }
}
