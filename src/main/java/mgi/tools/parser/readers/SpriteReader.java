package mgi.tools.parser.readers;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import lombok.val;

import mgi.tools.parser.TypeProperty;
import mgi.tools.parser.TypeReader;
import mgi.types.Definitions;
import mgi.types.sprite.SpriteGroupDefinitions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tommeh | 01/02/2020 | 14:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class SpriteReader implements TypeReader {

    @Override
    public ArrayList<Definitions> read(final Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException {
        val defs = new ArrayList<Definitions>();
        if (properties.containsKey("inherit")) {
            val inherit = properties.get("inherit");
            defs.add(SpriteGroupDefinitions.get(((Long) inherit).intValue()));
        } else {
            defs.add(new SpriteGroupDefinitions());
        }
        for (val definition : defs) {
            val sprite = (SpriteGroupDefinitions) definition;
            TypeReader.setFields(sprite, properties);
            if (properties.containsKey(TypeProperty.IMAGES.getIdentifier())) {
                val map = (Map<String, Object>) properties.get(TypeProperty.IMAGES.getIdentifier());
                var clear = false;
                if (map.containsKey("clear")) {
                    clear = (Boolean) map.get("clear");
                }
                map.remove("clear");
                val images = new Int2ObjectAVLTreeMap<BufferedImage>();
                for (val entry : map.entrySet()) {
                    val id = Integer.parseInt(entry.getKey());
                    val path = entry.getValue().toString();
                    try {
                        val image = ImageIO.read(new File("./assets/sprites/" + path));

                        //Exception for emote tab emotes transparency; easier to do this programmatically. Higher revision emotes have a darker "locked" sprite.
                        if (path.startsWith("emotes/")) {
                            val colourToSearch = 42 << 16 | 37 << 8 | 27 | 0xFF000000;
                            val colourToReplace = 64 << 16 | 57 << 8 | 40 | 0xFF000000;
                            val borderColour = 51 << 16 | 46 << 8 | 32 | 0xFF000000;
                            val width = image.getWidth();
                            val height = image.getHeight();
                            for (int x = 0; x < width; x++) {
                                for (int y = 0; y < height; y++) {
                                    val colour = image.getRGB(x, y);
                                    if (colour == colourToSearch) {
                                        //Lets identify if this pixel is a border pixel and create a thin 1px inner border out of the image to match the consistency.
                                        val isBorderPixel = x == 0 || y == 0 || x == width - 1 || y == height - 1
                                                || image.getRGB(x - 1, y) == 0 || image.getRGB(x + 1, y) == 0
                                                || image.getRGB(x, y - 1) == 0 || image.getRGB(x, y + 1) == 0;
                                        image.setRGB(x, y, isBorderPixel ? borderColour : colourToReplace);
                                    }
                                }
                            }
                        }

                        images.put(id, image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (clear || sprite.getImages() == null) {
                    sprite.setImages(images);
                } else {
                    for (val entry : images.int2ObjectEntrySet()) {
                        sprite.getImages().put(entry.getIntKey(), entry.getValue());
                        sprite.setImage(entry.getIntKey(), entry.getValue());
                    }
                }
            }
        }
        return defs;
    }

    @Override
    public String getType() {
        return "sprite";
    }
}
