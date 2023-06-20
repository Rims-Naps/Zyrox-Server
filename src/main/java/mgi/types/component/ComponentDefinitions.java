package mgi.types.component;

import com.zenyte.Game;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Utils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Kris | 12. dets 2017 : 5:46.29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 * profile</a>}
 */
@ToString
@Slf4j
public class ComponentDefinitions implements Definitions, Cloneable {

    public static final int LEFT = 0, CENTER = 1, RIGHT = 2;
    public static final int FONT_SMALL = 494, FONT_REGULAR = 495, FONT_BOLD = 496, FONT_LARGE_STYLE = 497;
    public static final Map<Integer, ComponentDefinitions> definitions = new HashMap<>(5000);
    private static final ComponentDefinitions EXAMPLE = new ComponentDefinitions();
    private static final Object DEFAULT = new Object();
    @Getter
    @Setter
    public String name;
    @Getter
    @Setter
    public int interfaceId, componentId;
    @Getter
    @Setter
    public boolean isIf3;
    @Getter
    @Setter
    public int type;
    @Getter
    @Setter
    public int contentType;
    @Getter
    @Setter
    public int xMode;
    @Getter
    @Setter
    public int yMode;
    @Getter
    @Setter
    public int widthMode;
    @Getter
    @Setter
    public int heightMode;
    @Getter
    @Setter
    public int x;
    @Getter
    @Setter
    public int y;
    @Getter
    @Setter
    public int width;
    @Getter
    @Setter
    public int height;
    @Getter
    @Setter
    public int parentId;
    @Getter
    @Setter
    public boolean hidden;
    @Getter
    @Setter
    public int scrollWidth;
    @Getter
    @Setter
    public int scrollHeight;
    @Getter
    public int color;
    @Getter
    @Setter
    public boolean filled;
    @Getter
    @Setter
    public int opacity;
    @Getter
    @Setter
    public int lineWidth;
    @Getter
    @Setter
    public int spriteId;
    @Getter
    @Setter
    public int textureId;
    @Getter
    @Setter
    public boolean spriteTiling;
    @Getter
    @Setter
    public int borderType;
    @Getter
    @Setter
    public int shadowColor;
    @Getter
    @Setter
    public boolean flippedVertically;
    @Getter
    @Setter
    public boolean flippedHorizontally;
    @Getter
    @Setter
    public int modelType;
    @Getter
    @Setter
    public int modelId;
    @Getter
    @Setter
    public int offsetX2d;
    @Getter
    @Setter
    public int offsetY2d;
    @Getter
    @Setter
    public int rotationX;
    @Getter
    @Setter
    public int rotationZ;
    @Getter
    @Setter
    public int rotationY;
    @Getter
    @Setter
    public int modelZoom;
    @Getter
    @Setter
    public int font;
    @Getter
    @Setter
    public String text;
    @Getter
    @Setter
    public String alternateText;
    @Getter
    @Setter
    public boolean textShadowed;
    @Getter
    @Setter
    public int xPitch;
    @Getter
    @Setter
    public int yPitch;
    @Getter
    @Setter
    public int[] xOffsets;
    @Getter
    @Setter
    public String[] configActions;
    @Getter
    @Setter
    public int accessMask;
    @Getter
    @Setter
    public String opBase;
    @Getter
    @Setter
    public String[] actions;
    @Getter
    @Setter
    public int dragDeadZone;
    @Getter
    @Setter
    public int dragDeadTime;
    @Getter
    @Setter
    public boolean dragRenderBehavior;
    @Getter
    @Setter
    public String targetVerb;
    @Getter
    @Setter
    public Object[] onLoadListener;
    @Getter
    @Setter
    public Object[] onClickListener;
    @Getter
    @Setter
    public Object[] onClickRepeatListener;
    @Getter
    @Setter
    public Object[] onReleaseListener;
    @Getter
    @Setter
    public Object[] onHoldListener;
    @Getter
    @Setter
    public Object[] onMouseOverListener;
    @Getter
    @Setter
    public Object[] onMouseRepeatListener;
    @Getter
    @Setter
    public Object[] onMouseLeaveListener;
    @Getter
    @Setter
    public Object[] onDragListener;
    @Getter
    @Setter
    public Object[] onDragCompleteListener;
    @Getter
    @Setter
    public Object[] onTargetEnterListener;
    @Getter
    @Setter
    public Object[] onTargetLeaveListener;
    @Getter
    @Setter
    public Object[] onVarTransmitListener;
    @Getter
    @Setter
    public int[] varTransmitTriggers;
    @Getter
    @Setter
    public Object[] onInvTransmitListener;
    @Getter
    @Setter
    public int[] invTransmitTriggers;
    @Getter
    @Setter
    public Object[] onStatTransmitListener;
    @Getter
    @Setter
    public int[] statTransmitTriggers;
    @Getter
    @Setter
    public Object[] onTimerListener;
    @Getter
    @Setter
    public Object[] onOpListener;
    @Getter
    @Setter
    public Object[] onScrollWheelListener;
    @Getter
    @Setter
    public int[][] dynamicValues;
    @Getter
    @Setter
    public int[] valueCompareType;
    @Getter
    @Setter
    public String spellName;
    @Getter
    @Setter
    public String tooltip;
    @Getter
    @Setter
    public int[] itemIds;
    @Getter
    @Setter
    public int[] itemQuantities;
    @Getter
    @Setter
    public List<ComponentDefinitions> children;
    @Getter
    @Setter
    public boolean noClickThrough;
    @Getter
    @Setter
    public int menuType;
    @Getter
    @Setter
    public int alternateTextColor;
    @Getter
    @Setter
    public int hoveredTextColor;
    @Getter
    @Setter
    public int alternateHoveredTextColor;
    @Getter
    @Setter
    public boolean lineDirection;
    @Getter
    @Setter
    public int alternateSpriteId;
    @Getter
    @Setter
    public int field2840;
    @Getter
    @Setter
    public int alternateModelId;
    @Getter
    @Setter
    public int animation;
    @Getter
    @Setter
    public int alternateAnimation;
    @Getter
    @Setter
    public int modelHeightOverride;
    @Getter
    @Setter
    public boolean orthogonal;
    @Getter
    @Setter
    public int lineHeight;
    @Getter
    @Setter
    public int xAllignment;
    @Getter
    @Setter
    public int yAllignment;
    @Getter
    @Setter
    public int[] yOffsets;
    @Getter
    @Setter
    public int[] sprites;
    @Getter
    @Setter
    public int[] requiredValues;
    @Getter
    @Setter
    public int hoveredSiblingId;
    @Getter
    @Setter
    private Map<String, Object[]> hooks;

    public ComponentDefinitions() {
        setDefaults();
    }

    public ComponentDefinitions(final int id, final ByteBuffer buffer) {
        interfaceId = id >> 16;
        componentId = id & 0xFFFF;
        setDefaults();
        val data = buffer.getBuffer();
        if (data != null && data.length > 0) {
            isIf3 = data[0] == -1;
            if (isIf3) {
                decodeIf3(buffer);
            } else {
                decode(buffer);
            }
        }
    }

    public ComponentDefinitions clone() throws CloneNotSupportedException {
        return (ComponentDefinitions) super.clone();
    }

    private static final Object getValue(final Object object, final Field field) throws Throwable {
        field.setAccessible(true);
        final Class<?> type = field.getType();
        if (field.get(object) == null) {
            return DEFAULT;
        }
        if (type == int[][].class) {
            return Arrays.toString((int[][]) field.get(object));
        } else if (type == int[].class) {
            return Arrays.toString((int[]) field.get(object));
        } else if (type == byte[].class) {
            return Arrays.toString((byte[]) field.get(object));
        } else if (type == short[].class) {
            return Arrays.toString((short[]) field.get(object));
        } else if (type == double[].class) {
            return Arrays.toString((double[]) field.get(object));
        } else if (type == float[].class) {
            return Arrays.toString((float[]) field.get(object));
        } else if (type == String[].class) {
            if (field.get(object) == null) {
                return "null";
            }
            return "[" + String.join(", ", (String[]) field.get(object)) + "]";
        } else if (type == Object[].class) {
            return Arrays.toString((Object[]) field.get(object));
        }
        return field.get(object);
    }
    
    public static void add(final ComponentDefinitions component) {
        val bitpacked = component.getInterfaceId() << 16 | component.getComponentId();
        definitions.put(bitpacked, component);
    }
    
    public static final ComponentDefinitions get(final int id, final int componentId) {
        return definitions.get(id << 16 | componentId);
    }
    
    public static final List<ComponentDefinitions> getComponents(final int id) {
        val components = new ArrayList<ComponentDefinitions>();
        for (val entry : definitions.entrySet()) {
            val bitpacked = entry.getKey();
            if (bitpacked >> 16 == id) {
                components.add(entry.getValue());
            }
        }
        return components;
    }
    
    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val interfaces = cache.getArchive(ArchiveType.INTERFACES);
        for (int interfaceId = 0; interfaceId < interfaces.getHighestGroupId(); interfaceId++) {
            val interfaceGroup = interfaces.findGroupByID(interfaceId);
            if (interfaceGroup == null) {
                continue;
            }
            for (int componentId = 0; componentId < interfaceGroup.getHighestFileId(); componentId++) {
                val file = interfaceGroup.findFileByID(componentId);
                if (file == null) {
                    continue;
                }
                val buffer = file.getData();
                if (buffer == null) {
                    continue;
                }
                buffer.setPosition(0);
                val id = interfaceId << 16 | componentId;
                definitions.put(id, new ComponentDefinitions(id, buffer));
            }
        }
    }

    public void printFields() {
        for (final Field field : getClass().getDeclaredFields()) {
            if ((field.getModifiers() & 8) != 0) {
                continue;
            }
            try {
                final Object val = getValue(this, field);
                final Object defaultVal = getValue(EXAMPLE, field);
                if (val == defaultVal || val.equals(defaultVal)) {
                    continue;
                }
                if (val == DEFAULT) {
                    continue;
                }
                final String[] fieldName = field.getName().split("(?=[A-Z])");
                final StringBuilder fieldBuilder = new StringBuilder();
                fieldBuilder.append(Utils.formatString(fieldName[0]));
                for (int i = 1; i < fieldName.length; i++) {
                    fieldBuilder.append(" " + (fieldName[i].length() == 1 ? fieldName[i].toUpperCase() : fieldName[i].toLowerCase()));
                }
                //strings.enqueue(fieldBuilder.toString() + ": " + val);

                System.out.println(fieldBuilder.toString() + ": " + val);
            } catch (final Throwable e) {
                log.error(Strings.EMPTY, e);
            }
        }
        System.out.println("--------------------------END OF COMPONENT------------------------");
    }

    private void setDefaults() {
        isIf3 = false;
        menuType = 0;
        contentType = 0;
        xMode = 0;
        yMode = 0;
        widthMode = 0;
        heightMode = 0;
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        parentId = -1;
        hidden = false;
        scrollWidth = 0;
        scrollHeight = 0;
        color = 0;
        alternateTextColor = 0;
        hoveredTextColor = 0;
        alternateHoveredTextColor = 0;
        filled = false;
        opacity = 0;
        lineWidth = 1;
        lineDirection = false;
        spriteId = -1;
        alternateSpriteId = -1;
        textureId = 0;
        spriteTiling = false;
        borderType = 0;
        shadowColor = 0;
        modelType = 1;
        modelId = -1;
        field2840 = 1;
        alternateModelId = -1;
        animation = -1;
        alternateAnimation = -1;
        offsetX2d = 0;
        offsetY2d = 0;
        rotationX = 0;
        rotationZ = 0;
        rotationY = 0;
        modelZoom = 100;
        modelHeightOverride = 0;
        orthogonal = false;
        font = -1;
        text = "";
        alternateText = "";
        lineHeight = 0;
        xAllignment = 0;
        yAllignment = 0;
        textShadowed = false;
        xPitch = 0;
        yPitch = 0;
        accessMask = 0;
        opBase = "";
        dragDeadZone = 0;
        dragDeadTime = 0;
        dragRenderBehavior = false;
        targetVerb = "";
        hoveredSiblingId = -1;
        spellName = "";
        tooltip = "Ok";
        noClickThrough = false;
        children = new ArrayList<>();
    }

    @Override
    public void decode(final ByteBuffer buffer) {
        type = buffer.readUnsignedByte();
        menuType = buffer.readUnsignedByte();
        contentType = buffer.readUnsignedShort();
        x = buffer.readShort();
        y = buffer.readShort();
        width = buffer.readUnsignedShort();
        height = buffer.readUnsignedShort();
        opacity = buffer.readUnsignedByte();
        parentId = buffer.readUnsignedShort();
		if (parentId == 65535) {
			parentId = -1;
		} else {
			parentId += -1 & -65536;
		}
        hoveredSiblingId = buffer.readUnsignedShort();
        if (hoveredSiblingId == 65535) {
            hoveredSiblingId = -1;
        }

        val var2 = buffer.readUnsignedByte();
        int var3;
        if (var2 > 0) {
            valueCompareType = new int[var2];
            requiredValues = new int[var2];

            for (var3 = 0; var3 < var2; ++var3) {
                valueCompareType[var3] = buffer.readUnsignedByte();
                requiredValues[var3] = buffer.readUnsignedShort();
            }
        }

        var3 = buffer.readUnsignedByte();
        int var4;
        int var5;
        int var6;
        if (var3 > 0) {
            dynamicValues = new int[var3][];

            for (var4 = 0; var4 < var3; ++var4) {
                var5 = buffer.readUnsignedShort();
                dynamicValues[var4] = new int[var5];

                for (var6 = 0; var6 < var5; ++var6) {
                    dynamicValues[var4][var6] = buffer.readUnsignedShort();
                    if (dynamicValues[var4][var6] == 65535) {
                        dynamicValues[var4][var6] = -1;
                    }
                }
            }
        }

        if (type == 0) {
            scrollHeight = buffer.readUnsignedShort();
            hidden = buffer.readUnsignedByte() == 1;
        }

        if (type == 1) {
            buffer.readShort();
            buffer.readByte();
        }

        if (type == 2) {
            itemIds = new int[width * height];
            itemQuantities = new int[height * width];
            var4 = buffer.readUnsignedByte();
            if (var4 == 1) {
                accessMask |= 268435456;
            }

            var5 = buffer.readUnsignedByte();
            if (var5 == 1) {
                accessMask |= 1073741824;
            }

            var6 = buffer.readUnsignedByte();
            if (var6 == 1) {
                accessMask |= Integer.MIN_VALUE;
            }

            final int var7 = buffer.readUnsignedByte();
            if (var7 == 1) {
                accessMask |= 536870912;
            }

            xPitch = buffer.readUnsignedByte();
            yPitch = buffer.readUnsignedByte();
            xOffsets = new int[20];
            yOffsets = new int[20];
            sprites = new int[20];

            int var8;
            for (var8 = 0; var8 < 20; ++var8) {
                val var9 = buffer.readUnsignedByte();
                if (var9 == 1) {
                    xOffsets[var8] = buffer.readShort();
                    yOffsets[var8] = buffer.readShort();
                    sprites[var8] = buffer.readInt();
                } else {
                    sprites[var8] = -1;
                }
            }

            configActions = new String[5];

            for (var8 = 0; var8 < 5; ++var8) {
                val var11 = buffer.readString();
                if (var11.length() > 0) {
                    configActions[var8] = var11;
                    accessMask |= 1 << var8 + 23;
                }
            }
        }

        if (type == 3) {
            filled = buffer.readUnsignedByte() == 1;
        }

        if (type == 4 || type == 1) {
            xAllignment = buffer.readUnsignedByte();
            yAllignment = buffer.readUnsignedByte();
            lineHeight = buffer.readUnsignedByte();
            font = buffer.readUnsignedShort();
            if (font == 65535) {
                font = -1;
            }

            textShadowed = buffer.readUnsignedByte() == 1;
        }

        if (type == 4) {
            text = buffer.readString();
            alternateText = buffer.readString();
        }

        if (type == 1 || type == 3 || type == 4) {
            color = buffer.readInt();
        }

        if (type == 3 || type == 4) {
            alternateTextColor = buffer.readInt();
            hoveredTextColor = buffer.readInt();
            alternateHoveredTextColor = buffer.readInt();
        }

        if (type == 5) {
            spriteId = buffer.readInt();
            alternateSpriteId = buffer.readInt();
        }

        if (type == 6) {
            modelType = 1;
            modelId = buffer.readUnsignedShort();
            if (modelId == 65535) {
                modelId = -1;
            }

            field2840 = 1;
            alternateModelId = buffer.readUnsignedShort();
            if (alternateModelId == 65535) {
                alternateModelId = -1;
            }

            animation = buffer.readUnsignedShort();
            if (animation == 65535) {
                animation = -1;
            }

            alternateAnimation = buffer.readUnsignedShort();
            if (alternateAnimation == 65535) {
                alternateAnimation = -1;
            }

            modelZoom = buffer.readUnsignedShort();
            rotationX = buffer.readUnsignedShort();
            rotationY = buffer.readUnsignedShort();
        }

        if (type == 7) {
            itemIds = new int[height * width];
            itemQuantities = new int[width * height];
            xAllignment = buffer.readUnsignedByte();
            font = buffer.readUnsignedShort();
            if (font == 65535) {
                font = -1;
            }

            textShadowed = buffer.readUnsignedByte() == 1;
            color = buffer.readInt();
            xPitch = buffer.readShort();
            yPitch = buffer.readShort();
            var4 = buffer.readUnsignedByte();
            if (var4 == 1) {
                accessMask |= 1073741824;
            }

            configActions = new String[5];

            for (var5 = 0; var5 < 5; ++var5) {
                val var10 = buffer.readString();
                if (var10.length() > 0) {
                    configActions[var5] = var10;
                    accessMask |= 1 << var5 + 23;
                }
            }
        }

        if (type == 8) {
            text = buffer.readString();
        }

        if (menuType == 2 || type == 2) {
            targetVerb = buffer.readString();
            spellName = buffer.readString();
            var4 = buffer.readUnsignedShort() & 63;
            accessMask |= var4 << 11;
        }

        if (menuType == 1 || menuType == 4 || menuType == 5 || menuType == 6) {
            tooltip = buffer.readString();
            if (tooltip.length() == 0) {
                if (menuType == 1) {
                    tooltip = "Ok";
                }

                if (menuType == 4) {
                    tooltip = "Select";
                }

                if (menuType == 5) {
                    tooltip = "Select";
                }

                if (menuType == 6) {
                    tooltip = "Continue";
                }
            }
        }

        if (menuType == 1 || menuType == 4 || menuType == 5) {
            accessMask |= 4194304;
        }

        if (menuType == 6) {
            accessMask |= 1;
        }

    }

    @Override
    public void decode(final ByteBuffer buffer, final int opcode) {

    }

    private void decodeIf3(final ByteBuffer buffer) {
        buffer.readByte();
        type = buffer.readUnsignedByte();
        contentType = buffer.readUnsignedShort();
        x = buffer.readShort();
        y = buffer.readShort();
        width = buffer.readUnsignedShort();
        if (type == 9) {
            height = buffer.readShort();
        } else {
            height = buffer.readUnsignedShort();
        }

        widthMode = buffer.readByte();
        heightMode = buffer.readByte();
        xMode = buffer.readByte();
        yMode = buffer.readByte();
        parentId = buffer.readUnsignedShort();
		if (parentId == 65535) {
			parentId = -1;
		} else {
			parentId += -1 & -65536;
		}

        hidden = buffer.readUnsignedByte() == 1;
        if (type == 0) {
            scrollWidth = buffer.readUnsignedShort();
            scrollHeight = buffer.readUnsignedShort();
            noClickThrough = buffer.readUnsignedByte() == 1;
        }

        if (type == 5) {
            spriteId = buffer.readInt();
            textureId = buffer.readUnsignedShort();
            spriteTiling = buffer.readUnsignedByte() == 1;
            opacity = buffer.readUnsignedByte();
            borderType = buffer.readUnsignedByte();
            shadowColor = buffer.readInt();
            flippedVertically = buffer.readUnsignedByte() == 1;
            flippedHorizontally = buffer.readUnsignedByte() == 1;
        }

        if (type == 6) {
            modelType = 1;
            modelId = buffer.readUnsignedShort();
            if (modelId == 65535) {
                modelId = -1;
            }

            offsetX2d = buffer.readShort();
            offsetY2d = buffer.readShort();
            rotationX = buffer.readUnsignedShort();
            rotationY = buffer.readUnsignedShort();
            rotationZ = buffer.readUnsignedShort();
            modelZoom = buffer.readUnsignedShort();
            animation = buffer.readUnsignedShort();
            if (animation == 65535) {
                animation = -1;
            }

            orthogonal = buffer.readUnsignedByte() == 1;
            buffer.readShort();
            if (widthMode != 0) {
                modelHeightOverride = buffer.readUnsignedShort();
            }

            if (heightMode != 0) {
                buffer.readShort();
            }
        }

        if (type == 4) {
            font = buffer.readUnsignedShort();
            if (font == 65535) {
                font = -1;
            }

            text = buffer.readString();
            lineHeight = buffer.readUnsignedByte();
            xAllignment = buffer.readUnsignedByte();
            yAllignment = buffer.readUnsignedByte();
            textShadowed = buffer.readUnsignedByte() == 1;
            color = buffer.readInt();
        }

        if (type == 3) {
            color = buffer.readInt();
            filled = buffer.readUnsignedByte() == 1;
            opacity = buffer.readUnsignedByte();
        }

        if (type == 9) {
            lineWidth = buffer.readUnsignedByte();
            color = buffer.readInt();
            lineDirection = buffer.readUnsignedByte() == 1;
        }

        accessMask = buffer.readMedium();
        opBase = buffer.readString();
        val var2 = buffer.readUnsignedByte();
        if (var2 > 0) {
            actions = new String[var2];

            for (int var3 = 0; var3 < var2; ++var3) {
                actions[var3] = buffer.readString();
            }
        }

        dragDeadZone = buffer.readUnsignedByte();
        dragDeadTime = buffer.readUnsignedByte();
        dragRenderBehavior = buffer.readUnsignedByte() == 1;
        targetVerb = buffer.readString();
        onLoadListener = decodeListener(buffer);
        onMouseOverListener = decodeListener(buffer);//mousefocus
        onMouseLeaveListener = decodeListener(buffer);//mouseunfocus
        onTargetLeaveListener = decodeListener(buffer);
        onTargetEnterListener = decodeListener(buffer);
        onVarTransmitListener = decodeListener(buffer);
        onInvTransmitListener = decodeListener(buffer);
        onStatTransmitListener = decodeListener(buffer);
        onTimerListener = decodeListener(buffer);
        onOpListener = decodeListener(buffer);
        onMouseRepeatListener = decodeListener(buffer);//mousefocusedlistener
        onClickListener = decodeListener(buffer);
        onClickRepeatListener = decodeListener(buffer);
        onReleaseListener = decodeListener(buffer);
        onHoldListener = decodeListener(buffer);
        onDragListener = decodeListener(buffer);
        onDragCompleteListener = decodeListener(buffer);
        onScrollWheelListener = decodeListener(buffer);
        varTransmitTriggers = decodeTransmitList(buffer);
        invTransmitTriggers = decodeTransmitList(buffer);
        statTransmitTriggers = decodeTransmitList(buffer);
    }

    private Object[] decodeListener(final ByteBuffer buffer) {
        val int_0 = buffer.readUnsignedByte();
        if (int_0 == 0) {
            return null;
        } else {
            val objects_0 = new Object[int_0];

            for (int int_1 = 0; int_1 < int_0; int_1++) {
                val int_2 = buffer.readUnsignedByte();
                if (int_2 == 0) {
                    objects_0[int_1] = new Integer(buffer.readInt());
                } else if (int_2 == 1) {
                    objects_0[int_1] = buffer.readString();
                }
            }
            return objects_0;
        }
    }

    private int[] decodeTransmitList(final ByteBuffer buffer) {
        val int_0 = buffer.readUnsignedByte();
        if (int_0 == 0) {
            return null;
        } else {
            val ints_0 = new int[int_0];

            for (int int_1 = 0; int_1 < int_0; int_1++) {
                ints_0[int_1] = buffer.readInt();
            }

            return ints_0;
        }
    }

    @Override
    public ByteBuffer encode() {
        val buffer = new ByteBuffer(1024 * 10);
        if (isIf3) {
            buffer.writeByte(-1);
            buffer.writeByte(type);
            buffer.writeShort(contentType);
            buffer.writeShort(x);
            buffer.writeShort(y);
            buffer.writeShort(width);
            buffer.writeShort(height);
            buffer.writeByte(widthMode);
            buffer.writeByte(heightMode);
            buffer.writeByte(xMode);
            buffer.writeByte(yMode);
            buffer.writeShort(parentId == -1 ? 65535 : parentId);

            buffer.writeByte(hidden ? 1 : 0);

            if (type == 0) {
                buffer.writeShort(scrollWidth);
                buffer.writeShort(scrollHeight);
                buffer.writeByte(noClickThrough ? 1 : 0);
            }

            if (type == 5) {
                buffer.writeInt(spriteId);
                buffer.writeShort(textureId);
                buffer.writeByte(spriteTiling ? 1 : 0);
                buffer.writeByte(opacity);
                buffer.writeByte(borderType);
                buffer.writeInt(shadowColor);
                buffer.writeByte(flippedVertically ? 1 : 0);
                buffer.writeByte(flippedHorizontally ? 1 : 0);
            }

            if (type == 6) {
                buffer.writeShort(modelId == -1 ? 65535 : modelId);
                buffer.writeShort(offsetX2d);
                buffer.writeShort(offsetY2d);
                buffer.writeShort(rotationX);
                buffer.writeShort(rotationY);
                buffer.writeShort(rotationZ);
                buffer.writeShort(modelZoom);
                buffer.writeShort(animation == -1 ? 65535 : animation);
                buffer.writeByte(orthogonal ? 1 : 0);
                buffer.writeShort(5);

                if (widthMode != 0) {
                    buffer.writeShort(modelHeightOverride);
                }

                if (heightMode != 0) {
                    buffer.writeShort(5);
                }

            }

            if (type == 4) {
                buffer.writeShort(font == -1 ? 65535 : font);
                buffer.writeString(text);
                buffer.writeByte(lineHeight);
                buffer.writeByte(xAllignment);
                buffer.writeByte(yAllignment);
                buffer.writeByte((textShadowed ? 1 : 0));
                buffer.writeInt(color);
            }

            if (type == 3) {
                buffer.writeInt(color);
                buffer.writeByte(filled ? 1 : 0);
                buffer.writeByte(opacity);
            }

            if (type == 9) {
                buffer.writeByte(lineWidth);
                buffer.writeInt(color);
                buffer.writeByte(lineDirection ? 1 : 0);
            }

            buffer.writeMedium(accessMask);
            buffer.writeString(opBase);
            val len = actions == null ? 0 : actions.length;
            buffer.writeByte(len);
            for (int i = 0; i < len; i++) {
                buffer.writeString(actions[i]);
            }

            buffer.writeByte(dragDeadZone);
            buffer.writeByte(dragDeadTime);
            buffer.writeByte((dragRenderBehavior ? 1 : 0));

            buffer.writeString(targetVerb);

            encodeListener(buffer, onLoadListener);

            encodeListener(buffer, onMouseOverListener);
            encodeListener(buffer, onMouseLeaveListener);
            encodeListener(buffer, onTargetLeaveListener);
            encodeListener(buffer, onTargetEnterListener);
            encodeListener(buffer, onVarTransmitListener);
            encodeListener(buffer, onInvTransmitListener);
            encodeListener(buffer, onStatTransmitListener);
            encodeListener(buffer, onTimerListener);
            encodeListener(buffer, onOpListener);

            encodeListener(buffer, onMouseRepeatListener);
            encodeListener(buffer, onClickListener);
            encodeListener(buffer, onClickRepeatListener);
            encodeListener(buffer, onReleaseListener);
            encodeListener(buffer, onHoldListener);
            encodeListener(buffer, onDragListener);
            encodeListener(buffer, onDragCompleteListener);
            encodeListener(buffer, onScrollWheelListener);

            encodeTransmitList(buffer, varTransmitTriggers);
            encodeTransmitList(buffer, invTransmitTriggers);
            encodeTransmitList(buffer, statTransmitTriggers);
        } else { //if1
            buffer.writeByte(type);
            buffer.writeByte(menuType);

            buffer.writeShort(contentType);
            buffer.writeShort(x);
            buffer.writeShort(y);
            buffer.writeShort(width);
            buffer.writeShort(height);
            buffer.writeByte(opacity);
            buffer.writeShort((parentId == -1 ? 65535 : parentId));
            buffer.writeShort((hoveredSiblingId == -1 ? 65535 : hoveredSiblingId));

            final int len = valueCompareType == null ? 0 : valueCompareType.length;
            buffer.writeByte(len);
            for (int i = 0; i < len; i++) {
                buffer.writeByte(valueCompareType[i]);
                buffer.writeShort(requiredValues[i]);
            }

            buffer.writeByte(dynamicValues == null ? 0 : dynamicValues.length);
            if (dynamicValues != null) {
                for (int i = 0; i < dynamicValues.length; i++) {
                    buffer.writeShort(dynamicValues[i].length);
                    for (int i2 = 0; i2 < dynamicValues[i].length; i2++) {
                        buffer.writeShort(dynamicValues[i][i2]);
                    }
                }
            }

            if (type == 0) {
                buffer.writeShort(scrollHeight);
                buffer.writeByte((hidden ? 1 : 0));
            }

            if (type == 1) {
                buffer.writeShort(0);
                buffer.writeByte(0);
            }

            if (type == 2) {
                buffer.writeByte((((accessMask & (1 << 28)) != 1) ? 1 : 0));
                buffer.writeByte((((accessMask & (1 << 30)) != 1) ? 1 : 0));
                buffer.writeByte((((accessMask & (1 << 31)) != 1) ? 1 : 0));
                buffer.writeByte((((accessMask & (1 << 29)) != 1) ? 1 : 0));
                buffer.writeByte(xPitch);
                buffer.writeByte(yPitch);

                for (int i = 0; i < 20; i++) {
                    if (sprites[i] == -1) {
                        buffer.writeByte(0);
                    } else {
                        buffer.writeByte(1);
                        buffer.writeShort(xOffsets[i]);
                        buffer.writeShort(yOffsets[i]);
                        buffer.writeInt(sprites[i]);
                    }
                }

                for (int i = 0; i < 5; i++) {
                    if (configActions[i] != null) {
                        buffer.writeString(configActions[i]);
                    } else {
                        buffer.writeString("");
                    }
                }
            }

            if (type == 3) {
                buffer.writeByte((filled ? 1 : 0));
            }

            if (type == 4 || type == 1) {
                buffer.writeByte(xAllignment);
                buffer.writeByte(yAllignment);
                buffer.writeByte(lineHeight);
                buffer.writeShort(font);
                buffer.writeByte(textShadowed ? 1 : 0);
            }

            if (type == 4) {
                buffer.writeString(text);
                buffer.writeString(alternateText);
            }

            if (type == 1 || type == 3 || type == 4) {
                buffer.writeInt(color);
            }

            if (type == 3 || type == 4) {
                buffer.writeInt(alternateTextColor);
                buffer.writeInt(hoveredTextColor);
                buffer.writeInt(alternateHoveredTextColor);
            }

            if (type == 5) {
                buffer.writeInt(spriteId);
                buffer.writeInt(alternateSpriteId);
            }

            if (type == 6) {
                buffer.writeShort(modelId);
                buffer.writeShort(alternateModelId);
                buffer.writeShort(animation);
                buffer.writeShort(alternateAnimation);
                buffer.writeShort(modelZoom);
                buffer.writeShort(rotationX);
                buffer.writeShort(rotationY);
            }

            if (type == 7) {
                buffer.writeByte(xAllignment);
                buffer.writeShort(font);
                buffer.writeByte((textShadowed ? 1 : 0));
                buffer.writeInt(color);
                buffer.writeShort(xPitch);
                buffer.writeShort(yPitch);
                buffer.writeByte((((accessMask & (1 << 30)) != 1) ? 1 : 0));
                for (int i = 0; i < 5; i++) {
                    if (configActions[i] != null) {
                        buffer.writeString(configActions[i]);
                    } else {
                        buffer.writeString("");
                    }
                }
            }

            if (type == 8) {
                buffer.writeString(text);
            }

            if (menuType == 2 || type == 2) {
                buffer.writeString(targetVerb);
                buffer.writeString(spellName);
                buffer.writeShort(((accessMask >> 11) & 63));
            }

            if (menuType == 1 || menuType == 4 || menuType == 5 || menuType == 6) {
                buffer.writeString(tooltip);

            }
        }
        return buffer;
    }

    @Override
    public void pack()  {
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.INTERFACES);
        var group = archive.findGroupByID(interfaceId);

        if (group == null) { //write whole new interface
            group = new Group(interfaceId);
            group.addFile(new File(encode()));
            for (int i = 0; i < children.size(); i++) {
                val component = children.get(i);
                group.addFile(new File(component.encode()));
            }
            cache.getArchive(ArchiveType.INTERFACES).addGroup(group);
        } else { //update single component
            val file = group.findFileByID(componentId);
            if (file == null) {
                group.addFile(new File(componentId, encode()));
            } else {
                file.setData(encode());
            }
            for (int i = 0; i < children.size(); i++) {
                val component = children.get(i);
                val f = group.findFileByID(i + 1);
                if (f == null) {
                    group.addFile(new File(component.getComponentId(), component.encode()));
                } else {
                    f.setData(component.encode());
                }
            }
        }
    }

    public void encodeListener(final ByteBuffer buffer, final Object[] objectArray) {
        buffer.writeByte(objectArray == null ? 0 : objectArray.length);
        if (objectArray == null) {
            return;
        }
        for (int i = 0; i < objectArray.length; i++) {
            val object = objectArray[i];
            if (object instanceof Integer) {
                buffer.writeByte(0);
                buffer.writeInt((int) object);
            } else {
                buffer.writeByte(1);
                buffer.writeString((String) object);
            }
        }
    }

    public void encodeTransmitList(final ByteBuffer buffer, final int[] intArray) {
        buffer.writeByte(intArray == null ? 0 : intArray.length);
        if (intArray == null) {
            return;
        }
        for (int i = 0; i < intArray.length; i++) {
            buffer.writeInt(intArray[i]);
        }
    }

    public void setOption(final int index, final String option) {
        if (actions == null) {
            actions = new String[index + 1];
        }
        if (index >= actions.length) {
            val options = new String[index + 1];
            for (int i = 0; i < actions.length; i++) {
                options[i] = actions[i];
            }
            actions = options;
        }
        actions[index] = option.isEmpty() || option == null || option.equals("null") ? null : option;
    }

    public int setColor(final String hex) {
        color = hex.equals("") ? 0 : Integer.parseInt(hex.replaceFirst("#", ""), 16);
        return color;
    }

    public int setShadowColor(final String hex) {
        shadowColor = hex.equals("") ? 0 : Integer.parseInt(hex.replaceFirst("#", ""), 16);
        return shadowColor;
    }

    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public void setDynamicSize(final int width, final int height) {
        widthMode = width;
        heightMode = height;
    }

    public void setPosition(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public void setDynamicPosition(final int x, final int y) {
        xMode = x;
        yMode = y;
    }

    public void setClickMask(final AccessMask mask) {
        accessMask = mask.getValue();
    }

    public void add(final int componentId, final ComponentDefinitions component) {
        children.add(component);
        //children[componentId] = component;
    }

    public static boolean containsInterface(final int id) {
        return get(id, 0) != null;
    }


}
