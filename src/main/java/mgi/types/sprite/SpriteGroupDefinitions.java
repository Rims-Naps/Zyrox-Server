package mgi.types.sprite;

import com.zenyte.Game;
import mgi.types.Definitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.types.config.AnimationDefinitions;
import mgi.utilities.ByteBuffer;
import org.apache.logging.log4j.util.Strings;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Represents a {@link SpriteGroupDefinitions} which may contain one or more images.
 *
 * @author Graham
 * @author `Discardedx2
 */
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class SpriteGroupDefinitions implements Definitions, Cloneable {

    public static final Int2ObjectMap<SpriteGroupDefinitions> definitions = new Int2ObjectOpenHashMap<>();

    public static final int FLAG_VERTICAL = 0x01;

    private int id;

    /**
     * The width of this sprite.
     */
    private int width;

    /**
     * The height of this sprite.
     */
    private int height;

    /**
     * The map of animation images in this sprite.
     */
    private Int2ObjectMap<BufferedImage> images;

    public SpriteGroupDefinitions(final int id, final int width, final int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.images = new Int2ObjectAVLTreeMap<>();
    }

    private SpriteGroupDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        decode(buffer);
    }

    public SpriteGroupDefinitions clone() throws CloneNotSupportedException {
        return (SpriteGroupDefinitions) super.clone();
    }

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val sprites = cache.getArchive(ArchiveType.SPRITES);
        for (int id = 0; id < sprites.getHighestGroupId(); id++) {
            val spriteGroup = sprites.findGroupByID(id);
            if (spriteGroup == null) {
                continue;
            }
            for (int index = 0; index < spriteGroup.fileCount(); index++) {
                val file = spriteGroup.findFileByID(index);
                if (file == null) {
                    continue;
                }
                val buffer = file.getData();
                if (buffer == null) {
                    continue;
                }
                definitions.put(id, new SpriteGroupDefinitions(id, buffer));
            }
        }
    }

    public void decode(final ByteBuffer buffer) {
        /* find the size of this sprite set */
        buffer.setPosition(buffer.limit() - 2);
        int size = buffer.readUnsignedShort();

        /* allocate arrays to store info */
        int[] offsetsX = new int[size];
        int[] offsetsY = new int[size];
        int[] subWidths = new int[size];
        int[] subHeights = new int[size];

        /* read the width, height and palette size */
        buffer.setPosition(buffer.limit() - size * 8 - 7);
        width = buffer.readUnsignedShort();
        height = buffer.readUnsignedShort();
        images = new Int2ObjectAVLTreeMap<>();
        int[] palette = new int[buffer.readUnsignedByte() + 1];

        /* read the offsets and dimensions of the individual sprites */
        readToArray(offsetsX, buffer, size);
        readToArray(offsetsY, buffer, size);
        readToArray(subWidths, buffer, size);
        readToArray(subHeights, buffer, size);

        /* read the palette */
        buffer.setPosition(buffer.limit() - size * 8 - 7 - (palette.length - 1) * 3);
        palette[0] = 0; /* transparent colour (black) */
        for (int index = 1; index < palette.length; index++) {
            palette[index] = buffer.readMedium();
            if (palette[index] == 0)
                palette[index] = 1;
        }

        /* read the pixels themselves */
        buffer.setPosition(0);
        try {
            for (int id = 0; id < size; id++) {
                /* grab some frequently used values */
                int subWidth = subWidths[id], subHeight = subHeights[id];
                int offsetX = offsetsX[id], offsetY = offsetsY[id];

                /* create a BufferedImage to store the resulting image */
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                images.put(id, image);

                /* allocate an array for the palette indices */
                int[][] indices = new int[subWidth][subHeight];

                /*
                 * read the flags so we know whether to read horizontally or
                 * vertically
                 */
                int flags = buffer.readUnsignedByte();

                /* now read the image */
                /* read the palette indices */
                if ((flags & FLAG_VERTICAL) != 0) {
                    for (int x = 0; x < subWidth; x++) {
                        for (int y = 0; y < subHeight; y++) {
                            indices[x][y] = buffer.readUnsignedByte();
                        }
                    }
                } else {
                    for (int y = 0; y < subHeight; y++) {
                        for (int x = 0; x < subWidth; x++) {
                            indices[x][y] = buffer.readUnsignedByte();
                        }
                    }
                }

                for (int x = 0; x < subWidth; x++) {
                    for (int y = 0; y < subHeight; y++) {
                        int index = indices[x][y];
                        if (index == 0) {
                            image.setRGB(x + offsetX, y + offsetY, 0);
                        } else {
                            image.setRGB(x + offsetX, y + offsetY, 0xFF000000 | palette[index]);
                        }
                    }
                }

            }
        } catch (Exception e) {
            System.out.println(id);
        }
    }

    private void readToArray(final int[] array, final ByteBuffer buffer, final int length) {
        assert length >= 0 && length <= array.length;
        for (int i = 0; i < length; i++) {
            array[i] = buffer.readUnsignedShort();
        }
    }

    @Override
    public void decode(ByteBuffer buffer, int opcode) {

    }

    public ByteBuffer encode() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bout);
        try {
            /* set up some variables */
            IntArrayList palette = new IntArrayList(0xFF);
            palette.add(0); /* transparent colour */

            /* write the sprites */
            for (BufferedImage image : images.values()) {
                /* check if we can encode this */
                //if (image.getWidth() != width || image.getHeight() != height)
                //	throw new IOException("All images must be the same size.");

                /* loop through all the pixels constructing a palette */
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        /* grab the colour of this pixel */
                        int argb = image.getRGB(x, y);
                        //int alpha = (argb >> 24) & 0xFF;
                        int rgb = argb & 0xFFFFFF;
                        if (rgb == 0)
                            rgb = 1;

                        /*
                         * enqueue the colour to the palette if it isn't already in
                         * the palette
                         */
                        if (!palette.contains(rgb)) {
                            palette.add(rgb);
                        }
                    }
                }

                if (palette.size() > 256) {
                    throw new IOException("Too many colours in this sprite! - " + palette.size());
                }

                /* write this sprite */
                os.write(FLAG_VERTICAL);
                for (int x = 0; x < Math.min(image.getWidth(), width); x++) {
                    for (int y = 0; y < Math.min(image.getHeight(), height); y++) {
                        int argb = image.getRGB(x, y);
                        int alpha = (argb >> 24) & 0xFF;
                        int rgb = argb & 0xFFFFFF;
                        if (rgb == 0)
                            rgb = 1;

                        os.write(alpha == 0 ? 0 : palette.indexOf(rgb));
                    }
                }
            }

            /* write the palette */
            for (int i = 1; i < palette.size(); i++) {
                int rgb = palette.getInt(i);
                os.write((byte) (rgb >> 16));
                os.write((byte) (rgb >> 8));
                os.write((byte) rgb);
            }

            /* write the max width, height and palette size */
            os.writeShort(width);
            os.writeShort(height);
            os.write(palette.size() - 1);

            /* write the individual offsets and dimensions */
            for (int i = 0; i < images.size(); i++) {
                os.writeShort(0); // offset X
            }

            for (int i = 0; i < images.size(); i++) {
                os.writeShort(0); // offset Y
            }

            for (int i = 0; i < images.size(); i++) {
                val image = images.get(i);
                os.writeShort(image.getWidth());
            }

            for (int i = 0; i < images.size(); i++) {
                val image = images.get(i);
                os.writeShort(image.getHeight());
            }


            /* write the number of images */
            os.writeShort(images.size());

            /* convert the stream to a byte array and then wrap a buffer */
            byte[] bytes = bout.toByteArray();
            os.close();
            return new ByteBuffer(bytes);
        } catch (IOException e) {
            log.error(Strings.EMPTY, e);
        }
        return null;
    }

    @Override
    public void pack() {
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.SPRITES);
        if (archive.findGroupByID(id) == null) {
            Game.getCacheMgi().getArchive(ArchiveType.SPRITES).addGroup(new Group(id, new File(encode())));
        } else {
            Game.getCacheMgi().getArchive(ArchiveType.SPRITES).findGroupByID(id).findFileByID(0).setData(encode());
        }

    }

    /**
     * Gets the frame with the specified id.
     *
     * @param id The id.
     * @return The frame.
     */
    public BufferedImage getImage(int id) {
        return images.get(id);
    }

    /**
     * Sets the frame with the specified id.
     *
     * @param id    The id.
     * @param image The frame.
     */
    public void setImage(int id, BufferedImage image) {
        if (getWidth() != width || getHeight() != height) {
            throw new IllegalArgumentException("The frame's dimensions do not match with the sprite's dimensions.");
        }
        images.put(id, image);
    }


    /**
     * Gets the number of images in this set.
     *
     * @return The number of images.
     */
    public int size() {
        return images.size();
    }

    public static SpriteGroupDefinitions get(final int id) {
        return definitions.get(id);
    }

}
