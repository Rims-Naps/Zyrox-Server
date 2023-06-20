package mgi.custom;

import com.zenyte.game.util.Utils;
import lombok.var;
import mgi.types.config.GraphicsDefinitions;

/**
 * @author Tommeh | 22-3-2019 | 22:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class CustomGraphics {

    public static void pack() {
        var def = GraphicsDefinitions.get(1296);
        var recolorToFind = new short[] {
                (short) 45863,
                (short) 45619,
                (short) 45504,
                (short) 45972,
                (short) 45729,
                (short) 45485,
                (short) 47019,
                (short) 48051,
                (short) 46631,
                (short) 48066,
                (short) 48061,
                (short) 48057,
                (short) 48053,
                (short) 48049,
                (short) 47021,
                (short) 47952,
                (short) 46746

        };
        var recolorToReplace = new short[] {
                (short) Utils.rgbToHSL16(163,44,0),
                (short) Utils.rgbToHSL16(209,75,25),
                (short) Utils.rgbToHSL16(255, 106, 50),
                (short) Utils.rgbToHSL16(163,44,0),
                (short) Utils.rgbToHSL16(209,75,25),
                (short) Utils.rgbToHSL16(255, 106, 50),
                (short) Utils.rgbToHSL16(163,44,0),
                (short) Utils.rgbToHSL16(172,65,25),
                (short) Utils.rgbToHSL16(163,44,0),
                (short) Utils.rgbToHSL16(255, 106, 50),
                (short) Utils.rgbToHSL16(255,120,50),
                (short) Utils.rgbToHSL16(255,115,50),
                (short) Utils.rgbToHSL16(255,115,45),
                (short) Utils.rgbToHSL16(255,112,40),
                (short) Utils.rgbToHSL16(172,65,25),
                (short) Utils.rgbToHSL16(255, 120, 70),
                (short) Utils.rgbToHSL16(255,0,0),
        };
        def.setId(2000);
        def.setOriginalColours(recolorToFind);
        def.setReplacementColours(recolorToReplace);
        def.pack();
        System.err.println(def.toString());
        def = GraphicsDefinitions.get(678);
        recolorToFind = new short[] {
                (short) 39896,
                (short) 39498,
                (short) 39351,

        };
        recolorToReplace = new short[] {
                (short) Utils.rgbToHSL16(255, 106, 50),
                (short) Utils.rgbToHSL16(255, 106, 50),
                (short) Utils.rgbToHSL16(255, 106, 50),
        };
        def.setId(2001);
        def.setOriginalColours(recolorToFind);
        def.setReplacementColours(recolorToReplace);
        def.pack();
        System.err.println(def.toString());
    }
}
