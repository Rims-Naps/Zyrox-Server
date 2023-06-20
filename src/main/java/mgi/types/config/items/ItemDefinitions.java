package mgi.types.config.items;

import com.zenyte.Game;
import com.zenyte.GameEngine;
import com.zenyte.game.content.grandexchange.JSONGEItemDefinitionsLoader;
import com.zenyte.game.item.ItemExamineLoader;
import com.zenyte.game.parser.impl.ItemRequirements;
import com.zenyte.game.parser.impl.JSONItemDefinitionsLoader;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentType;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.events.ItemDefinitionsLoadedEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.File;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Kris | 22. jaan 2018 : 21:35.27
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
public final class ItemDefinitions implements Definitions, Cloneable {

    private static final ItemDefinitions DEFAULT = new ItemDefinitions(-1);
    @Getter
    private static final IntOpenHashSet packedIds = new IntOpenHashSet();

    public static ItemDefinitions[] definitions;

    public static final int getSellPrice(final int itemId) {
        val definitions = ItemDefinitions.get(itemId);
        if (definitions == null) {
            return 0;
        }
        final boolean noted = definitions.isNoted();
        final int id = noted ? definitions.getNotedId() : itemId;
        val gePrice = JSONGEItemDefinitionsLoader.lookup(id);
        return gePrice != null && gePrice.getPrice() != 0 ? gePrice.getPrice() : definitions.getPrice();
    }

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val configs = cache.getArchive(ArchiveType.CONFIGS);
        val items = configs.findGroupByID(GroupType.ITEM);
        definitions = new ItemDefinitions[items.getHighestFileId()];
        for (int id = 0; id < items.getHighestFileId(); id++) {
            val file = items.findFileByID(id);
            if (file == null) {
                continue;
            }
            val buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            definitions[id] = new ItemDefinitions(id, buffer);
        }

        for (int id = 0; id < items.getHighestFileId(); id++) {
            val defs = get(id);
            if (defs == null || defs.notedTemplate == -1) {
                continue;
            }
            defs.toNote();
        }
        GameEngine.appendPostLoadTask(() -> PluginManager.post(new ItemDefinitionsLoadedEvent()));
    }

    public static boolean isValid(final int id) {
        return id >= 0 && id < definitions.length;
    }

    public static boolean isInvalid(final int id) {
        return id < 0 || id >= definitions.length;
    }

    public final List<String> printFields() {
        final List<String> strings = new ArrayList<String>(getClass().getDeclaredFields().length);
        for (final Field field : getClass().getDeclaredFields()) {
            if ((field.getModifiers() & 8) != 0) {
                continue;
            }
            try {
                final Object val = getValue(field);
                if (val == DEFAULT) {
                    continue;
                }
                final String[] fieldName = field.getName().split("(?=[A-Z])");
                final StringBuilder fieldBuilder = new StringBuilder();
                fieldBuilder.append(Utils.formatString(fieldName[0]));
                for (int i = 1; i < fieldName.length; i++) {
                    fieldBuilder.append(" " + (fieldName[i].length() == 1 ? fieldName[i].toUpperCase() : fieldName[i].toLowerCase()));
                }
                strings.add(fieldBuilder.toString() + ": " + val);

                System.out.println(fieldBuilder.toString() + ": " + val);
            } catch (final Throwable e) {
                log.error(Strings.EMPTY, e);
            }
        }
        return strings;
    }

    private final Object getValue(final Field field) throws Throwable {
        field.setAccessible(true);
        final Class<?> type = field.getType();

        if (field.get(this) == null || field.get(this).equals(DEFAULT.getClass().getDeclaredField(field.getName()).get(DEFAULT))) {
            return DEFAULT;
        }

        if (type == int[][].class) {
            return Arrays.toString((int[][]) field.get(this));
        } else if (type == int[].class) {
            return Arrays.toString((int[]) field.get(this));
        } else if (type == byte[].class) {
            return Arrays.toString((byte[]) field.get(this));
        } else if (type == short[].class) {
            return Arrays.toString((short[]) field.get(this));
        } else if (type == double[].class) {
            return Arrays.toString((double[]) field.get(this));
        } else if (type == float[].class) {
            return Arrays.toString((float[]) field.get(this));
        } else if (type == String[].class) {
            if (field.get(this) == null) {
                return "null";
            }
            return "[" + String.join(", ", (String[]) field.get(this)) + "]";
        } else if (type == Object[].class) {
            return Arrays.toString((Object[]) field.get(this));
        }
        return field.get(this);
    }

    @Getter
    @Setter
    private String name;
    @Getter
    private transient String lowercaseName;
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String[] inventoryOptions;
    @Getter
    private String[] groundOptions;
    @Getter
    @Setter
    private boolean grandExchange;
    @Getter
    private boolean isMembers;
    @Setter
    private int isStackable;
    @Setter
    private int price;
    @Getter
    @Setter
    private int notedTemplate;
    @Getter
    @Setter
    private int notedId;
    @Getter
    private int bindTemplateId;
    @Getter
    private int bindId;
    @Getter
    @Setter
    private int placeholderTemplate;
    @Getter
    @Setter
    private int placeholderId;
    @Getter
    @Setter
    private int[] stackIds;
    @Getter
    @Setter
    private int[] stackAmounts;
    @Getter
    @Setter
    private int maleOffset;
    @Getter
    @Setter
    private int primaryMaleHeadModelId;
    @Getter
    @Setter
    private int secondaryMaleHeadModelId;
    @Getter
    @Setter
    private int primaryMaleModel;
    @Getter
    @Setter
    private int secondaryMaleModel;
    @Getter
    @Setter
    private int tertiaryMaleModel;
    @Getter @Setter
    private int femaleOffset;
    @Getter
    @Setter
    private int primaryFemaleHeadModelId;
    @Getter
    @Setter
    private int secondaryFemaleHeadModelId;
    @Getter
    @Setter
    private int primaryFemaleModel;
    @Getter
    @Setter
    private int secondaryFemaleModel;
    @Getter
    @Setter
    private int tertiaryFemaleModel;
    @Getter
    @Setter
    private int inventoryModelId;
    @Getter
    private int shiftClickIndex;
    @Getter
    private int teamId;
    @Getter
    @Setter
    private int zoom;
    @Getter
    @Setter
    private int offsetX;
    @Getter
    @Setter
    private int offsetY;
    @Getter
    @Setter
    private int modelPitch;
    @Getter
    @Setter
    private int modelRoll;
    @Getter
    @Setter
    private int modelYaw;
    @Getter
    private int resizeX;
    @Getter
    private int resizeY;
    @Getter
    private int resizeZ;
    @Getter
    @Setter
    private short[] originalColours;
    @Getter
    @Setter
    private short[] replacementColours;
    @Getter
    private short[] originalTextureIds;
    @Getter
    private short[] replacementTextureIds;
    @Getter
    private int ambient;
    @Getter
    private int contrast;
    @Getter
    @Setter
    private Int2ObjectOpenHashMap<Object> parameters;
    @Getter
    @Setter
    private String examine;
    @Getter
    private float weight;
    @Getter
    @Setter
    private int slot = -1;
    // @Getter
    // private HashMap<Integer, Integer> requirements;
    @Getter
    private int[] bonuses;
    @Getter
    private EquipmentType equipmentType;
    @Getter
    private boolean twoHanded;
    @Getter
    private int blockAnimation;
    @Getter
    private int standAnimation = RenderAnimation.STAND;
    @Getter
    private int walkAnimation = RenderAnimation.WALK;
    @Getter
    private int runAnimation = RenderAnimation.RUN;
    @Getter
    private int standTurnAnimation = RenderAnimation.STAND_TURN;
    @Getter
    private int rotate90Animation = RenderAnimation.ROTATE90;
    @Getter
    private int rotate180Animation = RenderAnimation.ROTATE180;
    @Getter
    private int rotate270Animation = RenderAnimation.ROTATE270;
    @Getter
    private int accurateAnimation;
    @Getter
    private int aggressiveAnimation;
    @Getter
    private int controlledAnimation;
    @Getter
    private int defensiveAnimation;
    @Getter
    private int attackSpeed;
    @Getter
    private int interfaceVarbit;
    @Getter
    private int normalAttackDistance;
    @Getter
    private int longAttackDistance;

    public int getNotedOrDefault() {
        if (isNoted() || notedId == -1) {
            return id;
        }
        return notedId;
    }

    public int getUnnotedOrDefault() {
        if (isNoted()) {
            return notedId;
        }
        return id;
    }

    public ItemRequirements.ItemRequirement getRequirements() {
        return ItemRequirements.getRequirement(id);
    }

    public boolean containsOption(final String option) {
        if (inventoryOptions == null) {
            return false;
        }
        for (final String o : inventoryOptions) {
            if (o == null || !o.equalsIgnoreCase(option)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public int getSlotForOption(final String option) {
        if (inventoryOptions == null) {
            return -1;
        }
        for (int i = 0; i < inventoryOptions.length; i++) {
            val o = inventoryOptions[i];
            if (o == null || !o.equalsIgnoreCase(option)) {
                continue;
            }
            return i + 1;
        }
        return -1;
    }

    public String getOption(final int option) {
        if (inventoryOptions == null) {
            return null;
        }
        if (option < 0 || option >= inventoryOptions.length) {
            return null;
        }
        return inventoryOptions[option];
    }

    private static boolean loaded;

    public static final void loadDefinitions() {
        if (loaded) {
            return;
        }
        loaded = true;
        val list = new ArrayList<Callable<Void>>();
        list.add(() -> {
            try {
                new JSONItemDefinitionsLoader().parse();
            } catch (Throwable throwable) {
                log.error(Strings.EMPTY, throwable);
            }
            return null;
        });

        list.add(() -> {
            try {
                new ItemExamineLoader().parse();
            } catch (Throwable throwable) {
                log.error(Strings.EMPTY, throwable);
            }
            return null;
        });

        list.add(() -> {
            try {
                ItemRequirements.parse();
            } catch (Throwable throwable) {
                log.error(Strings.EMPTY, throwable);
            }
            return null;
        });

        val pool = ForkJoinPool.commonPool();
        pool.invokeAll(list);
        list.clear();

        try {
            val cache = Game.getCacheMgi();
            val configs = cache.getArchive(ArchiveType.CONFIGS);
            val items = configs.findGroupByID(GroupType.ITEM);
            val length = items.getHighestFileId();
            for (int itemId = 0; itemId < length; itemId++) {
                val def = get(itemId);
                if (def == null) {
                    continue;
                }
                val examine = ItemExamineLoader.DEFINITIONS.get(itemId);
                if (examine != null) {
                    def.examine = examine.getExamine();
                }
                val jsonDefs = JSONItemDefinitionsLoader.lookup(itemId);
                if (jsonDefs == null) {
                    continue;
                }
                val wearDef = jsonDefs.getEquipmentDefinition();

                def.weight = jsonDefs.getWeight();
                def.slot = jsonDefs.getSlot();
                // if (jsonDefs.getTradable() != null) {
                // def.tradable = jsonDefs.getTradable();
                // }
                def.equipmentType = jsonDefs.getEquipmentType();
                if (wearDef != null) {
                    // def.requirements = wearDef.getRequirements();
                    final String bonuses = wearDef.getBonuses();
                    final String[] splitBonuses = bonuses.split(", ");
                    try {
                        def.bonuses = new int[splitBonuses.length];
                        for (int i = 0; i < splitBonuses.length; i++) {
                            def.bonuses[i] = Integer.valueOf(splitBonuses[i]);
                        }
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                    final WieldableDefinition wieldDef = wearDef.getWeaponDefinition();
                    if (wieldDef != null) {
                        def.twoHanded = wieldDef.isTwoHanded();
                        if (wieldDef.getBlockAnimation() != 0) {
                            def.blockAnimation = wieldDef.getBlockAnimation();
                        }
                        if (wieldDef.getStandAnimation() != 0) {
                            def.standAnimation = wieldDef.getStandAnimation();
                        }
                        if (wieldDef.getWalkAnimation() != 0) {
                            def.walkAnimation = wieldDef.getWalkAnimation();
                        }
                        if (wieldDef.getRunAnimation() != 0) {
                            def.runAnimation = wieldDef.getRunAnimation();
                        }
                        if (wieldDef.getStandTurnAnimation() != 0) {
                            def.standTurnAnimation = wieldDef.getStandTurnAnimation();
                        }
                        if (wieldDef.getRotate90Animation() != 0) {
                            def.rotate90Animation = wieldDef.getRotate90Animation();
                        }
                        if (wieldDef.getRotate180Animation() != 0) {
                            def.rotate180Animation = wieldDef.getRotate180Animation();
                        }
                        if (wieldDef.getRotate270Animation() != 0) {
                            def.rotate270Animation = wieldDef.getRotate270Animation();
                        }
                        def.accurateAnimation = wieldDef.getAccurateAnimation();
                        def.aggressiveAnimation = wieldDef.getAggressiveAnimation();
                        def.controlledAnimation = wieldDef.getControlledAnimation();
                        def.defensiveAnimation = wieldDef.getDefensiveAnimation();
                        def.attackSpeed = wieldDef.getAttackSpeed();
                        def.interfaceVarbit = wieldDef.getInterfaceVarbit();
                        def.normalAttackDistance = wieldDef.getNormalAttackDistance();
                        def.longAttackDistance = wieldDef.getLongAttackDistance();
                    }
                }
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public ItemDefinitions(final int id) {
        this.id = id;
        setDefaults();
    }

    public ItemDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        setDefaults();
        decode(buffer);
    }

    @Override
    public void decode(final ByteBuffer buffer) {
        while (true) {
            val opcode = buffer.readUnsignedByte();
            /*if(this.id == 25781) {
                System.out.println("Reading opcode: " + opcode);
            }*/
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
                inventoryModelId = buffer.readUnsignedShort();
                return;
            case 2:
                name = buffer.readString();
                lowercaseName = name.toLowerCase();
                return;
            case 4:
                zoom = buffer.readUnsignedShort();
                return;
            case 5:
                modelPitch = buffer.readUnsignedShort(); //aka xan2d
                return;
            case 6:
                modelRoll = buffer.readUnsignedShort(); //aka yan2d
                return;
            case 7:
                offsetX = buffer.readUnsignedShort(); //aka xOffset2d
                if (offsetX > 32767) {
                    offsetX -= 65536;
                }
                return;
            case 8:
                offsetY = buffer.readUnsignedShort(); //aka yOffset2d
                if (offsetY > 32767) {
                    offsetY -= 65536;
                }
                return;
            case 9:
                String unknown = buffer.readString();
                System.out.println("Item " + this.id + " unknown string:" + unknown);
                return;
            case 11:
                isStackable = 1;
                return;
            case 12:
                price = buffer.readInt();
                return;
            case 16:
                isMembers = true;
                return;
            case 23:
                primaryMaleModel = buffer.readUnsignedShort();
                maleOffset = buffer.readUnsignedByte();
                return;
            case 24:
                secondaryMaleModel = buffer.readUnsignedShort();
                return;
            case 25:
                primaryFemaleModel = buffer.readUnsignedShort();
                femaleOffset = buffer.readUnsignedByte();
                return;
            case 26:
                secondaryFemaleModel = buffer.readUnsignedShort();
                return;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                groundOptions[opcode - 30] = buffer.readString();
                if (groundOptions[opcode - 30].equalsIgnoreCase("Hidden")) {
                    groundOptions[opcode - 30] = null;
                }
                return;
            case 35: //op0
            case 36: //op1
            case 37:
            case 38:
            case 39:
                inventoryOptions[opcode - 35] = buffer.readString();
                return;
            case 40: {
                val amount = buffer.readUnsignedByte();
                originalColours = new short[amount];
                replacementColours = new short[amount];
                for (int index = 0; index < amount; index++) {
                    originalColours[index] = (short) (buffer.readUnsignedShort());
                    replacementColours[index] = (short) (buffer.readUnsignedShort());
                }
                return;
            }
            case 41: {
                val amount = buffer.readUnsignedByte();
                originalTextureIds = new short[amount];
                replacementTextureIds = new short[amount];
                for (int index = 0; index < amount; index++) {
                    originalTextureIds[index] = (short) (buffer.readUnsignedShort());
                    replacementTextureIds[index] = (short) (buffer.readUnsignedShort());
                }
                return;
            }
            case 42:
                shiftClickIndex = buffer.readByte();
                return;
            case 65:
                grandExchange = true;
                return;
            case 78:
                tertiaryMaleModel = buffer.readUnsignedShort();
                return;
            case 79:
                tertiaryFemaleModel = buffer.readUnsignedShort();
                return;
            case 90:
                primaryMaleHeadModelId = buffer.readUnsignedShort();
                return;
            case 91:
                primaryFemaleHeadModelId = buffer.readUnsignedShort();
                return;
            case 92:
                secondaryMaleHeadModelId = buffer.readUnsignedShort();
                return;
            case 93:
                secondaryFemaleHeadModelId = buffer.readUnsignedShort();
                return;
            case 94:
                int category = buffer.readUnsignedShort();
                return;
            case 95:
                modelYaw = buffer.readUnsignedShort();
                return;
            case 97:
                notedId = buffer.readUnsignedShort();
                return;
            case 98:
                notedTemplate = buffer.readUnsignedShort();
                return;
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
                if (stackIds == null) {
                    stackIds = new int[10];
                    stackAmounts = new int[10];
                }
                stackIds[opcode - 100] = buffer.readUnsignedShort();
                stackAmounts[opcode - 100] = buffer.readUnsignedShort();
                return;
            case 110:
                resizeX = buffer.readUnsignedShort();
                return;
            case 111:
                resizeY = buffer.readUnsignedShort();
                return;
            case 112:
                resizeZ = buffer.readUnsignedShort();
                return;
            case 113:
                ambient = buffer.readByte();
                return;
            case 114:
                contrast = buffer.readByte();
                return;
            case 115:
                teamId = buffer.readUnsignedByte();
                return;
            case 139:
                bindId = buffer.readUnsignedShort();
                return;
            case 140:
                bindTemplateId = buffer.readUnsignedShort();
                return;
            case 148:
                placeholderId = buffer.readUnsignedShort();
                return;
            case 149:
                placeholderTemplate = buffer.readUnsignedShort();
                return;
            case 249:
                parameters = buffer.readParameters();
                return;
        }
    }

    /*switch (opcode) {
            case 1:
                inventoryModelId = buffer.readUnsignedShort();
                return;
            case 2:
                name = buffer.readString();
                lowercaseName = name.toLowerCase();
                return;
            case 4:
                zoom = buffer.readUnsignedShort();
                return;
            case 5:
                modelPitch = buffer.readUnsignedShort();
                return;
            case 6:
                modelRoll = buffer.readUnsignedShort();
                return;
            case 7:
                offsetX = buffer.readUnsignedShort();
                if (offsetX > 32767) {
                    offsetX -= 65536;
                }
                return;
            case 8:
                offsetY = buffer.readUnsignedShort();
                if (offsetY > 32767) {
                    offsetY -= 65536;
                }
                return;
            case 11:
                isStackable = 1;
                return;
            case 12:
                price = buffer.readInt();
                return;
            case 16:
                isMembers = true;
                return;
            case 23:
                primaryMaleModel = buffer.readUnsignedShort();
                maleOffset = buffer.readUnsignedByte();
                return;
            case 24:
                secondaryMaleModel = buffer.readUnsignedShort();
                return;
            case 25:
                primaryFemaleModel = buffer.readUnsignedShort();
                femaleOffset = buffer.readUnsignedByte();
                return;
            case 26:
                secondaryFemaleModel = buffer.readUnsignedShort();
                return;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                groundOptions[opcode - 30] = buffer.readString();
                if (groundOptions[opcode - 30].equalsIgnoreCase("Hidden")) {
                    groundOptions[opcode - 30] = null;
                }
                return;
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
                inventoryOptions[opcode - 35] = buffer.readString();
                return;
            case 40: {
                val amount = buffer.readUnsignedByte();
                originalColours = new short[amount];
                replacementColours = new short[amount];
                for (int index = 0; index < amount; index++) {
                    originalColours[index] = (short) (buffer.readUnsignedShort());
                    replacementColours[index] = (short) (buffer.readUnsignedShort());
                }
                return;
            }
            case 41: {
                val amount = buffer.readUnsignedByte();
                originalTextureIds = new short[amount];
                replacementTextureIds = new short[amount];
                for (int index = 0; index < amount; index++) {
                    originalTextureIds[index] = (short) (buffer.readUnsignedShort());
                    replacementTextureIds[index] = (short) (buffer.readUnsignedShort());
                }
                return;
            }
            case 42:
                shiftClickIndex = buffer.readByte();
                return;
            case 65:
                grandExchange = true;
                return;
            case 78:
                tertiaryMaleModel = buffer.readUnsignedShort();
                return;
            case 79:
                tertiaryFemaleModel = buffer.readUnsignedShort();
                return;
            case 90:
                primaryMaleHeadModelId = buffer.readUnsignedShort();
                return;
            case 91:
                primaryFemaleHeadModelId = buffer.readUnsignedShort();
                return;
            case 92:
                secondaryMaleHeadModelId = buffer.readUnsignedShort();
                return;
            case 93:
                secondaryFemaleHeadModelId = buffer.readUnsignedShort();
                return;
            case 95:
                modelYaw = buffer.readUnsignedShort();
                return;
            case 97:
                notedId = buffer.readUnsignedShort();
                return;
            case 98:
                notedTemplate = buffer.readUnsignedShort();
                return;
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
                if (stackIds == null) {
                    stackIds = new int[10];
                    stackAmounts = new int[10];
                }
                stackIds[opcode - 100] = buffer.readUnsignedShort();
                stackAmounts[opcode - 100] = buffer.readUnsignedShort();
                return;
            case 110:
                resizeX = buffer.readUnsignedShort();
                return;
            case 111:
                resizeY = buffer.readUnsignedShort();
                return;
            case 112:
                resizeZ = buffer.readUnsignedShort();
                return;
            case 113:
                ambient = buffer.readByte();
                return;
            case 114:
                contrast = buffer.readByte();
                return;
            case 115:
                teamId = buffer.readUnsignedByte();
                return;
            case 139:
                bindId = buffer.readUnsignedShort();
                return;
            case 140:
                bindTemplateId = buffer.readUnsignedShort();
                return;
            case 148:
                placeholderId = buffer.readUnsignedShort();
                return;
            case 149:
                placeholderTemplate = buffer.readUnsignedShort();
                return;
            case 249:
                parameters = buffer.readParameters();
                return;
        }*/

    private void setDefaults() {
        name = lowercaseName = "null";
        zoom = 2000;
        modelPitch = 0;
        modelRoll = 0;
        modelYaw = 0;
        offsetX = 0;
        offsetY = 0;
        isStackable = 0;
        price = 1;
        isMembers = false;
        groundOptions = new String[]{null, null, "Take", null, null};
        inventoryOptions = new String[]{null, null, null, null, "Drop"};
        shiftClickIndex = -2;
        primaryMaleModel = -1;
        secondaryMaleModel = -1;
        maleOffset = 0;
        primaryFemaleModel = -1;
        secondaryFemaleModel = -1;
        femaleOffset = 0;
        tertiaryMaleModel = -1;
        tertiaryFemaleModel = -1;
        primaryMaleHeadModelId = -1;
        secondaryMaleHeadModelId = -1;
        primaryFemaleHeadModelId = -1;
        secondaryFemaleHeadModelId = -1;
        notedId = -1;
        notedTemplate = -1;
        resizeX = 128;
        resizeY = 128;
        resizeZ = 128;
        ambient = 0;
        contrast = 0;
        teamId = 0;
        grandExchange = false;
        bindId = -1;
        bindTemplateId = -1;
        placeholderId = -1;
        placeholderTemplate = -1;
    }

    public static ItemDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length) {
            //throw new RuntimeException("Invalid item requested.");
            return null;//cant throw an exception because so much code is unable to handle exceptions.
        }

        return definitions[id];
    }

    public static ItemDefinitions getOrThrow(final int id) {
        if (id < 0 || id >= definitions.length) {
            throw new RuntimeException("Invalid item requested.");
        }

        return definitions[id];
    }

    public static final String nameOf(final int id) {
        val def = get(id);
        return def == null ? "null" : Utils.getOrDefault(def.getName(), "null");
    }

    public boolean isPlaceholder() {
        return placeholderTemplate != -1;
    }

    public boolean isNoted() {
        return notedTemplate != -1;
    }

    public boolean isStackable() {
        return isStackable == 1 || isNoted();
    }

    public int getPrice() {
        if (isNoted()) {
            return get(getNotedId()).getPrice();
        }
        return price;
    }

    private void toNote() {
        final ItemDefinitions realItem = get(notedId);
        isMembers = realItem.isMembers;
        price = realItem.price;
        name = realItem.name;
        grandExchange = realItem.grandExchange;
        isStackable = 1;
    }

    public int getHighAlchPrice() {
        return (int) (getPrice() * 0.6);
    }

    public String getStringParam(final int key) {
        if (parameters == null) {
            return "null";
        }
        val object = parameters.get(key);
        if (!(object instanceof String)) {
            return "null";
        }
        return (String) object;
    }

    public int getIntParam(final int key) {
        if (parameters == null) {
            return -1;
        }
        val object = parameters.get(key);
        if (!(object instanceof Integer)) {
            return -1;
        }
        return (Integer) object;
    }

    public boolean containsParamByValue(final Object value) {
        if (parameters == null) {
            return false;
        }
        val iterator = parameters.values().iterator();
        val lowercaseValue = value.toString().toLowerCase();
        while (iterator.hasNext()) {
            val next = iterator.next();
            if (next.toString().toLowerCase().equals(lowercaseValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public mgi.utilities.ByteBuffer encode() {
        val buffer = new ByteBuffer(512);
        buffer.writeByte(1);
        buffer.writeShort(inventoryModelId);
        if (!name.equals("null") && notedTemplate == -1) {
            buffer.writeByte(2);
            buffer.writeString(name);
        }
        if (zoom != 2000) {
            buffer.writeByte(4);
            buffer.writeShort(zoom);
        }
        if (modelPitch != 0) {
            buffer.writeByte(5);
            buffer.writeShort(modelPitch);
        }
        if (modelRoll != 0) {
            buffer.writeByte(6);
            buffer.writeShort(modelRoll);
        }
        if (offsetX != 0) {
            buffer.writeByte(7);
            buffer.writeShort(offsetX);
        }
        if (offsetY != 0) {
            buffer.writeByte(8);
            buffer.writeShort(offsetY);
        }
        if (isStackable == 1 && notedTemplate == -1) {
            buffer.writeByte(11);
        }
        if (price != 1 && notedTemplate == -1) {
            buffer.writeByte(12);
            buffer.writeInt(price);
        }
        if (isMembers && notedTemplate == -1) {
            buffer.writeByte(16);
        }
        if (primaryMaleModel != -1) {
            buffer.writeByte(23);
            buffer.writeShort(primaryMaleModel);
            buffer.writeByte(maleOffset);
        }
        if (secondaryMaleModel != -1) {
            buffer.writeByte(24);
            buffer.writeShort(secondaryMaleModel);
        }
        if (primaryFemaleModel != -1) {
            buffer.writeByte(25);
            buffer.writeShort(primaryFemaleModel);
            buffer.writeByte(femaleOffset);
        }
        if (secondaryFemaleModel != -1) {
            buffer.writeByte(26);
            buffer.writeShort(secondaryFemaleModel);
        }
        if (groundOptions != null) {
            for (int index = 0; index < 5; index++) {
                if (groundOptions[index] != null) {
                    buffer.writeByte((30 + index));
                    buffer.writeString(groundOptions[index]);
                }
            }
        }
        for (int index = 0; index < 5; index++) {
            if (inventoryOptions[index] != null) {
                buffer.writeByte((35 + index));
                buffer.writeString(inventoryOptions[index]);
            }
        }

        if (originalColours != null && replacementColours != null && originalColours.length != 0
                && replacementColours.length != 0) {
            buffer.writeByte(40);
            buffer.writeByte(originalColours.length);
            for (int index = 0; index < originalColours.length; index++) {
                buffer.writeShort(originalColours[index]);
                buffer.writeShort(replacementColours[index]);
            }
        }

        if (originalTextureIds != null && replacementTextureIds != null && originalTextureIds.length != 0
                && replacementTextureIds.length != 0) {
            buffer.writeByte(41);
            buffer.writeByte(originalTextureIds.length);
            for (int index = 0; index < originalTextureIds.length; index++) {
                buffer.writeShort(originalTextureIds[index]);
                buffer.writeShort(replacementTextureIds[index]);
            }
        }
        if (shiftClickIndex != -1) {
            buffer.writeByte(42);
            buffer.writeByte(shiftClickIndex);
        }
        if (grandExchange) {
            buffer.writeByte(65);
        }
        if (tertiaryMaleModel != -1) {
            buffer.writeByte(78);
            buffer.writeShort(tertiaryMaleModel);
        }
        if (tertiaryFemaleModel != -1) {
            buffer.writeByte(79);
            buffer.writeShort(tertiaryFemaleModel);
        }
        if (primaryMaleHeadModelId != -1) {
            buffer.writeByte(90);
            buffer.writeShort(primaryMaleHeadModelId);
        }
        if (primaryFemaleHeadModelId != -1) {
            buffer.writeByte(91);
            buffer.writeShort(primaryFemaleHeadModelId);
        }
        if (secondaryMaleHeadModelId != -1) {
            buffer.writeByte(92);
            buffer.writeShort(secondaryMaleHeadModelId);
        }
        if (secondaryFemaleHeadModelId != -1) {
            buffer.writeByte(93);
            buffer.writeShort(secondaryFemaleHeadModelId);
        }
        if (modelYaw != -1) {
            buffer.writeByte(95);
            buffer.writeShort(modelYaw);
        }
        if (notedId != -1) {
            buffer.writeByte(97);
            buffer.writeShort(notedId);
        }
        if (notedTemplate != -1) {
            buffer.writeByte(98);
            buffer.writeShort(notedTemplate);
        }
        if (stackIds != null && stackAmounts != null && stackIds.length != 0 && stackAmounts.length != 0) {
            for (int index = 0; index < stackIds.length; index++) {
                if (stackIds[index] != 0 || stackAmounts[index] != 0) {
                    buffer.writeByte((100 + index));
                    buffer.writeShort(stackIds[index]);
                    buffer.writeShort(stackAmounts[index]);
                }

            }
        }
        if (resizeX != 128) {
            buffer.writeByte(110);
            buffer.writeShort(resizeX);
        }
        if (resizeY != 128) {
            buffer.writeByte(111);
            buffer.writeShort(resizeY);
        }
        if (resizeZ != 128) {
            buffer.writeByte(112);
            buffer.writeShort(resizeZ);
        }
        if (ambient != 1) {
            buffer.writeByte(113);
            buffer.writeByte(ambient);
        }
        if (contrast != 1) {
            buffer.writeByte(114);
            buffer.writeByte(contrast);
        }
        if (teamId != 1) {
            buffer.writeByte(115);
            buffer.writeByte(teamId);
        }
        if (bindId != -1) {
            buffer.writeByte(139);
            buffer.writeShort(bindId);
        }
        if (bindTemplateId != -1) {
            buffer.writeByte(140);
            buffer.writeShort(bindTemplateId);
        }
        if (placeholderId != -1) {
            buffer.writeByte(148);
            buffer.writeShort(placeholderId);
        }
        if (placeholderTemplate != -1) {
            buffer.writeByte(149);
            buffer.writeShort(placeholderTemplate);
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
        if (!packedIds.add(id)) {
            log.info("Overlapping an item in cachepacking: " + id);
        }
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.ITEM).addFile(new File(id, encode()));
    }

    public void setOption(final int index, final String option) {
        if (inventoryOptions == null) {
            inventoryOptions = new String[5];
        }
        inventoryOptions[index] = option.isEmpty() ? null : option;
    }

}