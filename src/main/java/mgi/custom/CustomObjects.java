package mgi.custom;

import com.zenyte.game.world.entity.pathfinding.events.RouteEvent;
import mgi.types.config.ObjectDefinitions;
import mgi.custom.christmas.ChristmasObjectPacker;
import mgi.custom.halloween.HalloweenObjectPacker;
import lombok.val;
import lombok.var;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Tommeh | 5-3-2019 | 18:48
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class CustomObjects {

    public static void pack() throws IOException {
        new HalloweenObjectPacker().pack();
        new ChristmasObjectPacker().pack();
        var def = ObjectDefinitions.get(12355); //tournament portal
        System.out.println(Arrays.toString(def.getOptions()));
        def.setName("Tournament Portal");
        def.setOptions(new String[] { "View Tournaments", null, null, null, null });
        def.pack();

        val geBooths = new int[] { 10060, 10061 };
        for (int id : geBooths) {
            def = ObjectDefinitions.get(id);
            System.out.println(Arrays.toString(def.getOptions()));
            def.setOptions(new String[] { null, id == 10061 ? "Exchange" : "Bank", "Collect", "Offers Viewer", null });
            def.pack();
        }

        def = ObjectDefinitions.get(18258); //home altar
        System.out.println(Arrays.toString(def.getOptions()));
        def.setName("Saradomin altar");//Default name is too small and makes the options look weird.
        def.setOptions(new String[] { "Pray", "Regular", "Ancient", "Lunar", "Arceuus" });
        def.pack();

        def = ObjectDefinitions.get(26756); //wilderness statistics
        System.out.println(Arrays.toString(def.getOptions()));
        def.setOptions(new String[] { "Toggle K/D", null, null, null, null });
        def.pack();

        def = ObjectDefinitions.get(12136); //Home lectern
        System.out.println(Arrays.toString(def.getOptions()));
        def.setId(35008);
        def.setOptionsInvisible(-1);
        def.setName("Lectern");
        def.setOptions(new String[] { "Study", null, null, null, null }); //TODO add options for making teleports for different spellbooks
        System.out.println(RouteEvent.NORTH_EXIT);
        def.setAccessBlockFlag(RouteEvent.NORTH_EXIT);
        def.pack();

        def = ObjectDefinitions.get(11784); //Door
        System.out.println(Arrays.toString(def.getOptions()));
        def.setId(35009);
        def.setOptionsInvisible(-1);
        def.setName("Door");
        def.setOptions(new String[] { "Open", null, null, null, null });
        def.pack();

        def = ObjectDefinitions.get(11785); //Door
        def.setId(35010);
        def.setOptionsInvisible(-1);
        def.setName("Door");
        def.setOptions(new String[] { "Open", null, null, null, null });
        def.pack();

        def = ObjectDefinitions.get(11784); //Door
        def.setId(35011);
        def.setOptionsInvisible(-1);
        def.setName("Door");
        def.setOptions(new String[] { "Close", null, null, null, null });
        def.pack();

        def = ObjectDefinitions.get(11785); //Door
        def.setId(35012);
        def.setOptionsInvisible(-1);
        def.setName("Door");
        def.setOptions(new String[] { "Close", null, null, null, null });
        def.pack();

        def = ObjectDefinitions.get(25016);
        def.setClipType(1);
        def.pack();


        def = ObjectDefinitions.get(15478); //zenyte portal
        def.setName("Zenyte Portal");
        def.setId(35000);
        def.setOptions(new String[] { "Teleport", "Teleport-previous", null, null, null });
        //def.getModels()[0] = 38000;
        def.setModelColours(new int[] { (short) 49705, (short) 49829, (short) 49953, (short) 49948, (short) 50072 });
        def.setReplacementColours(new int[] { 4008, 2980, 1952, 2971, 2967 });
        def.pack();

        def = ObjectDefinitions.get(23709); //box of restoration
        def.setId(35001);
        def.setName("Box of Restoration");
        System.err.println(Arrays.toString(def.getOptions()));
        def.setOptions(new String[] { "Restore", null, null, null, null });
        def.setModelSizeX(205);
        def.setModelSizeY(152);
        def.setModelSizeHeight(190);
        def.setOffsetY(20);
        def.setSizeX(2);
        def.pack();

        def = ObjectDefinitions.get(29170); //mounted max cape
        def.setId(35002);
        System.err.println(Arrays.toString(def.getOptions()));
        def.setOptions(new String[] { null, null, null, null, null });
        def.setSizeX(1);
        def.setSizeY(1);
        def.pack();

        def = ObjectDefinitions.get(29422);
        def.setId(35003);
        System.err.println(Arrays.toString(def.getOptions()));
        def.setOptions(new String[] {
                "Tree", "Ring-Zanaris", "Ring-configure", "Ring-last-destination", null, null
        });
        def.setModelSizeX((int) (def.getModelSizeX() * 1.5F));
        def.setModelSizeY((int) (def.getModelSizeY() * 1.5F));
        def.setModelSizeHeight((int) (def.getModelSizeHeight() * 1.5F));
        System.err.println(def.getModelSizeX() + " - " + def.getModelSizeY() + " - " + def.getModelSizeHeight());
        def.setSizeX(3);
        def.setSizeY(3);
        def.pack();

        def = ObjectDefinitions.get(31625); //zenyte portal
        def.setName("Fountain of Health");
        def.setId(35004);
        /*
        6107
        6092
         */
        //43045, 43036, 43025, 43029, 3009, 2838, 6945, 8993, 7485, 7481
        def.setModelColours(new int[] { (short) 43045, (short) 43036, (short) 43025, (short) 43029, 3009, 2838, 6945, 8993, 7485, 7481 });
        def.setReplacementColours(new int[] { (short) 43047, (short) 43038, (short) 43026, (short) 43030, 2000, 2000, 7104, 9152, 7766, 7767 });
        def.setOptions(new String[] { "Drink", null, null, null, null });
        def.getModels()[0] = 50001;

        /*def.setModelSizeX(128);
        def.setModelSizeY(128);
        def.setModelSizeHeight(128);*/

        /*val originals = new short[] {
                5799, 5807, 5814, 8115, 8123, 3348, 2316, 4917, 6073, 3345, 3342, 5441, 5313, 3335, 3336, 4374, 4368, 2317, 3343, 9149, 10173, 8114, 5945, 4014, 4916, 5806, 9142, 4006
        };

        val replacements = new short[] {
                2810, 3834, 4858, 5882, 6906, 3481, 2447, 4063, 3053, 3477, 3474, 5588, 5452, 3464, 4490, 4508, 4500, 2448, 3475, 7029, 7029, 4960, 4966, 4952, 5981, 5837, 7015, 4937
        };

        {2810, 3834, 4858, 5882, 6906, 3481, 2447, 4063, 3053, 3477, 3474, 5588, 5452, 4489, 4490, 4508, 4500, 2448, 3475, 7029, 4960, 4966, 4952, 5981, 5837, 7015, 4937}

        def.setModelColour(originals);
        def.setReplacementColour(replacements);*/

        /*def.setSizeX(1);
        def.setSizeY(1);
        def.setOffsetX(-30);
        def.setOffsetY(-30);
        def.setAnimationId(3174);*/
        def.pack();

        def = ObjectDefinitions.get(32457); //tournament barrier
        def.setId(35005);
        def.setName("Tournament Barrier");
        def.pack();

        def = ObjectDefinitions.get(31583); //tournament supplies ne
        def.setId(35006);
        def.setName("Tournament Supplies");
        System.out.println(Arrays.toString(def.getOptions()));
        def.setOptions(new String[] { "View", null, null, null, null });
        def.setModels(new int[] { 38001 });
        def.setOffsetY(-78);
        def.setOffsetX(-78);
        def.pack();

        def = ObjectDefinitions.get(31583); //tournament supplies nw
        def.setId(35007);
        def.setName("Tournament Supplies");
        def.setOptions(new String[] { "View", null, null, null, null });
        def.setModels(new int[] { 38001 });
        def.setOffsetY(-78);
        def.setOffsetX(-43);
        def.pack();
    }
}
