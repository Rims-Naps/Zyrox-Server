package mgi.custom;

import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.player.teleportsystem.TeleportScroll;
import com.zenyte.plugins.item.CosmeticBox;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;
import lombok.var;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * @author Tommeh | 17-2-2019 | 22:29
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class CustomItems {

    public static void pack() {
        var def = ItemDefinitions.get(2402); //silverlight
        System.out.println(";p;" + Arrays.toString(def.getInventoryOptions()));
        def.setParameters(new Int2ObjectOpenHashMap<>());
        def.getParameters().put(451, "Check");
        def.setInventoryOptions(new String[] { null, "Wield", "Check", null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2724); //infinity set
        System.out.println(Arrays.toString(def.getInventoryOptions()));
        def.setName("Infinity set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2726); //void knight set
        def.setName("Void knight set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2728); //elite knight set
        def.setName("Elite void knight set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2730); //3rd age range set
        def.setName("3rd age range set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2732); //3rd age melee set
        def.setName("3rd age melee set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2734); //3rd age mage set
        def.setName("3rd age mage set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2736); //3rd age druidic set
        def.setName("3rd age druidic set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2738); //corrupted set
        def.setName("Corrupted set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2740); //ranger set
        def.setName("Rangers' set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(2742); //ranger set
        def.setName("Santa set");
        def.setInventoryOptions(new String[] { "Open", null, null, null, "Drop" });
        def.pack();


        def = ItemDefinitions.get(13344); //ranger set
        def.setGrandExchange(true);
        System.out.println(Arrays.toString(def.getInventoryOptions()));
        def.setInventoryOptions(new String[] { "Wear", null, null, null, "Destroy" });
        def.pack();

        new ScrollBoxPacker().pack();

        //21347, 21352, 21348
        /*21347 - 3900
        21348 - 975
        ;p;[, Wear, Check, null, Drop]
[Open, , null, null, Drop]
[Wear, , null, null, Destroy]
[, null, null, null, Drop]
[Commune, Configure, null, null, Destroy]
        32770
        21350 - 245
        21352 - 1520*/
        val amethystItems = new int[] {
                21316, 21318, 21320, 21322, 21324, 21326, 21332, 21334, 21336, 21338, 21347, 21348, 21350, 21352
        };

        for (val amethystItem : amethystItems) {
            def = ItemDefinitions.get(amethystItem);
            //System.err.println("Changing the price of " + def.getName() + " from " + def.getPrice() + " to " + (def.getPrice() / 4));
            //def.setPrice(def.getPrice() / 4);
            def.pack();
        }
        val tradableItems = new int[] {
                11862, 6666, 13343, 11847, 1419, 1037, 6199, 7927, 21859, 13288,
                13655, 12746, 12747, 12748, 12749, 12750, 12751, 12752, 12753, 12754,
                12755, 12756, ItemId.JACK_LANTERN_MASK, ItemId.GRIM_REAPER_HOOD, ItemId.HUNTING_KNIFE
        };

        for (val item : tradableItems) {
            def = ItemDefinitions.get(item);
            def.setGrandExchange(true);
            val index = ArrayUtils.indexOf(def.getInventoryOptions(), "Destroy");
            if (index != -1) {
                def.getInventoryOptions()[index] = "Drop";
            }
            def.pack();
        }


        for (val item : ClueItem.values()) {
            def = ItemDefinitions.getOrThrow(item.getCasket());
            def.setOriginalColours(new short[] {13248});
            def.setReplacementColours(new short[] { (short) item.getReplacementColour() });
            def.pack();
        }

        for (int i = 12637; i <= 12639; i++) {
            def = ItemDefinitions.get(i);
            def.setPrice(1);
            def.pack();
        }

        for (int i = 20537; i <= 20541; i++) {
            def = ItemDefinitions.get(i);
            def.setPrice(1);
            def.pack();
        }

        def = ItemDefinitions.get(11349);
        def.setName("Emote scroll");
        //Replaces a clue scroll; Will be unused in the future.
        def.setId(2678);
        def.setInventoryOptions(ItemDefinitions.get(11681).getInventoryOptions());
        def.setPlaceholderId(30027);
        def.pack();



        def = ItemDefinitions.get(23101);
        def.setName("Zenyte helm");
        def.setId(2683);
        def.setPlaceholderId(30019);
        def.setPrimaryMaleModel(52512);
        def.setPrimaryFemaleModel(52512);
        def.setSecondaryMaleModel(-1);
        def.setSecondaryFemaleModel(-1);
        def.setTertiaryMaleModel(-1);
        def.setTertiaryFemaleModel(-1);
        def.setInventoryModelId(52511);
        def.setGrandExchange(true);
        def.pack();

        def = ItemDefinitions.get(23097);
        def.setName("Zenyte top");
        def.setId(2684);
        def.setPlaceholderId(30020);
        def.setPrimaryMaleModel(52514);
        def.setPrimaryFemaleModel(52514);
        def.setSecondaryMaleModel(-1);
        def.setSecondaryFemaleModel(-1);
        def.setTertiaryMaleModel(-1);
        def.setTertiaryFemaleModel(-1);
        def.setInventoryModelId(52513);
        def.setGrandExchange(true);
        def.pack();

        def = ItemDefinitions.get(23095);
        def.setName("Zenyte legs");
        def.setId(2685);
        def.setPlaceholderId(30021);
        def.setPrimaryMaleModel(52516);
        def.setPrimaryFemaleModel(52516);
        def.setSecondaryMaleModel(-1);
        def.setSecondaryFemaleModel(-1);
        def.setTertiaryMaleModel(-1);
        def.setTertiaryFemaleModel(-1);
        def.setInventoryModelId(52515);
        def.setGrandExchange(true);
        def.pack();

        def = ItemDefinitions.get(23099);
        def.setName("Zenyte cape");
        def.setId(2686);
        def.setPlaceholderId(30022);
        def.setPrimaryMaleModel(52508);
        def.setPrimaryFemaleModel(52508);
        def.setSecondaryMaleModel(-1);
        def.setSecondaryFemaleModel(-1);
        def.setTertiaryMaleModel(-1);
        def.setTertiaryFemaleModel(-1);
        def.setInventoryModelId(52507);
        def.setZoom(2800);
        def.setGrandExchange(true);
        def.pack();

        def = ItemDefinitions.get(23093);
        def.setName("Zenyte boots");
        def.setId(2687);
        def.setPlaceholderId(30023);
        def.setPrimaryMaleModel(52506);
        def.setPrimaryFemaleModel(52506);
        def.setSecondaryMaleModel(-1);
        def.setSecondaryFemaleModel(-1);
        def.setTertiaryMaleModel(-1);
        def.setTertiaryFemaleModel(-1);
        def.setInventoryModelId(52505);
        def.setGrandExchange(true);
        def.pack();

        def = ItemDefinitions.get(23091);
        def.setName("Zenyte gloves");
        def.setId(2688);
        def.setPlaceholderId(30024);
        def.setPrimaryMaleModel(52510);
        def.setPrimaryFemaleModel(52510);
        def.setSecondaryMaleModel(-1);
        def.setSecondaryFemaleModel(-1);
        def.setTertiaryMaleModel(-1);
        def.setTertiaryFemaleModel(-1);
        def.setInventoryModelId(52509);
        def.setGrandExchange(true);
        def.pack();

        def = ItemDefinitions.get(9712); //normal home tp
        def.getParameters().put(601, "Zenyte Home Teleport");
        def.pack();

        def = ItemDefinitions.get(1615); //RDT
        System.out.println(Arrays.toString(def.getInventoryOptions()));
        def.setName("Rare drop table");
        def.setNotedId(-1);
        def.setNotedTemplate(-1);
        def.setId(2689);
        def.setInventoryModelId(52523);

        def.setOriginalColours(new short[] {
                60, 9023
        });
        def.setReplacementColours(new short[] {
                -14425, -22467//-14784, -14425, -15462, -14784
        });
        def.setInventoryOptions(new String[] {null, null, null, null, "Destroy"});
        def.pack();
        //Continuing identical item as a GTD
        def.setName("Gem drop table");
        def.setId(2690);
        def.setNotedId(-1);
        def.setNotedTemplate(-1);
        def.pack();

        def = ItemDefinitions.get(22667);
        def.setId(2710);
        def.setName("Graceful dye");
        def.setInventoryOptions(new String[] { "Info", null, null, null, "Drop" });
        def.pack();

        def = ItemDefinitions.get(ItemId.ROYAL_SEED_POD);
        System.out.println(Arrays.toString(def.getInventoryOptions()));
        def.setInventoryOptions(new String[] { "Commune", "Configure", null, null, "Destroy" });
        def.pack();

        def = ItemDefinitions.get(11142); //ancient home tp
        def.getParameters().put(601, "Zenyte Home Teleport");
        def.pack();

        def = ItemDefinitions.get(11143); //ancient home tp
        def.getParameters().put(601, "Zenyte Home Teleport");
        def.pack();

        def = ItemDefinitions.get(11681);
        def.setName("Imbue scroll");
        def.pack();

        def = ItemDefinitions.get(10937);
        def.setPrice(235);
        def.pack();

        def = ItemDefinitions.get(10938);
        def.setPrice(235);
        def.pack();

        packTeleportScrolls();
        packTeletab();

        {
            def = ItemDefinitions.get(10485);
            def.setId(19782);
            def.setOriginalColours(new short[]{5563});
            def.setReplacementColours(new short[]{6433});
            System.out.println(Arrays.toString(def.getInventoryOptions()));
            def.setInventoryOptions(new String[]{"Read", null, null, null, "Drop"});
            def.setName("Xeric's Wisdom");
            def.setGrandExchange(false);
            def.pack();
        }



        def = ItemDefinitions.get(21835); //ancient home tp
        def.getParameters().put(601, "Zenyte Home Teleport");
        def.pack();

        def = ItemDefinitions.get(1561);
        def.setId(30000);
        def.setName("Cute creature");
        def.setInventoryModelId(38002);
        def.setPlaceholderId(-1);
        def.setGrandExchange(false);
        def.pack();

        def = ItemDefinitions.get(1561);
        def.setId(30001);
        def.setName("Stray dog");
        def.setInventoryModelId(38003);
        def.setPlaceholderId(-1);
        def.setGrandExchange(false);
        def.pack();

        def = ItemDefinitions.get(1561);
        def.setId(30002);
        def.setName("Evil Creature");
        def.setInventoryModelId(38004);
        def.setPlaceholderId(-1);
        def.setGrandExchange(false);
        def.pack();

        def = ItemDefinitions.get(1561);
        def.setId(30003);
        def.setName("Jal-ImRek");
        def.setInventoryModelId(33010);
        def.setPlaceholderId(-1);
        def.setZoom(6000);
        def.setGrandExchange(false);
        def.pack();

        def = ItemDefinitions.get(1561);
        def.setId(30004);
        def.setName("Bucket Pete");
        def.setInventoryModelId(38004);
        def.setPlaceholderId(-1);
        def.setGrandExchange(false);
        def.pack();

        def = ItemDefinitions.get(1561);
        def.setId(30005);
        def.setName("Wyrmy");
        def.setInventoryModelId(36166);
        def.setPlaceholderId(-1);
        def.setZoom(10000);
        def.setGrandExchange(false);
        def.pack();

        def = ItemDefinitions.get(12791);
        def.setName("Tournament Rune Pouch");
        def.setId(30006);
        def.pack();

        def = ItemDefinitions.get(13190);
        def.setName("$10 Bond");
        def.setPrice(500000);
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(13190);
        def.setId(30017);
        def.setInventoryModelId(38005);
        def.setName("$50 Bond");
        def.setPrice(500000);
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.setPlaceholderId(30025);
        def.pack();

        def = ItemDefinitions.get(13190);
        def.setId(30018);
        def.setInventoryModelId(38006);
        def.setName("$100 Bond");
        def.setPrice(500000);
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.setPlaceholderId(30026);
        def.pack();

        def = ItemDefinitions.get(13190);
        def.setId(30051);
        def.setInventoryModelId(29209);
        def.setName("$5 Bond");
        def.setGrandExchange(false);
        def.setPrice(500000);
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.setPlaceholderId(30052);
        def.pack();

        def = ItemDefinitions.get(15430);
        def.setId(30052);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(30051);
        def.setInventoryModelId(29209);
        def.pack();

        def = ItemDefinitions.get(1580);
        def.setInventoryOptions(new String[] { "Wear", "Check", null, null, null });
        def.pack();

        def = ItemDefinitions.get(1580);
        def.setId(30030);
        def.setGrandExchange(true);
        def.setReplacementColours(new short[] { (short) 32511, (short) 32511 });
        def.setInventoryOptions(new String[] { "Wear", null, null, null, null });
        def.setName("Enhanced ice gloves");
        def.pack();

        val skeletonOutfit = new int[] {
                ItemId.SKELETON_BOOTS, ItemId.SKELETON_GLOVES, ItemId.SKELETON_LEGGINGS, ItemId.SKELETON_MASK, ItemId.SKELETON_SHIRT
        };

        for (val item : skeletonOutfit) {
            def = ItemDefinitions.getOrThrow(item);
            def.setInventoryOptions(new String[] { "Wear", null, null, null, "Destroy" });
            def.pack();
        }

        packPlaceholders();

        for (val clueDef : ItemDefinitions.definitions) {
            if (clueDef == null || !clueDef.getName().startsWith("Clue scroll") || ClueItem.getMap().containsKey(clueDef.getId())
             || ItemDefinitions.getPackedIds().contains(clueDef.getId())) {
                continue;
            }
            clueDef.setName("Null CS");
            clueDef.pack();
        }
    }

    private static void packTeleportScrolls() {
        val d = ItemDefinitions.get(10485);
        val model = d.getInventoryModelId();
        System.out.println(model);
        for (val scroll : TeleportScroll.values()) {
            val def = ItemDefinitions.get(scroll.getId());
            def.setInventoryModelId(model);
            def.setOriginalColours(new short[]{ 5563 });
            def.setReplacementColours(new short[] { -26433 });
            def.setInventoryOptions(new String[] { "Read", null, null, null, "Drop" });
            def.setName(scroll.getName());
            def.setGrandExchange(true);
            def.pack();
        }
    }

    private static void packTeletab() {
        var def = ItemDefinitions.get(8007);
        def.setId(22721);//8796
        def.setInventoryModelId(50000);
        def.setInventoryOptions(new String[] { "Break", null, null, null, "Drop" });
        def.setName("Zenyte home teleport");
        def.setPlaceholderTemplate(-1);
        def.setPlaceholderId(30028);
        def.setGrandExchange(true);
        def.pack();
    }

    private static void packPlaceholders() {
        var def = ItemDefinitions.get(15430);
        def.setId(30019);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(2683);
        def.setInventoryModelId(52511);
        def.pack();

        def = ItemDefinitions.get(15430);
        def.setId(30020);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(2684);
        def.setInventoryModelId(52513);
        def.pack();

        def = ItemDefinitions.get(15430);
        def.setId(30021);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(2685);
        def.setInventoryModelId(52515);
        def.pack();

        def = ItemDefinitions.get(15430);
        def.setId(30022);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(2686);
        def.setInventoryModelId(52507);
        def.pack();

        def = ItemDefinitions.get(15430);
        def.setId(30023);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(2687);
        def.setInventoryModelId(52505);
        def.pack();

        def = ItemDefinitions.get(15430);
        def.setId(30024);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(2688);
        def.setInventoryModelId(52509);
        def.pack();
        /////
        def = ItemDefinitions.get(15430);
        def.setId(30025);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(30017);
        def.setInventoryModelId(38005);
        def.pack();

        def = ItemDefinitions.get(15430);
        def.setId(30026);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(30018);
        def.setInventoryModelId(38006);
        def.pack();

        def = ItemDefinitions.get(15430);
        def.setId(30027);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(2678);
        def.setInventoryModelId(26671);
        def.pack();

        def = ItemDefinitions.get(15430);
        def.setId(30028);
        def.setName("null");
        def.setPlaceholderTemplate(14401);
        def.setPlaceholderId(22721);
        def.setInventoryModelId(50000);
        def.pack();

        def = ItemDefinitions.get(6199);
        def = ItemDefinitions.get(def.getPlaceholderId());
        def.setId(30032);
        def.setPlaceholderId(30031);
        def.setName("null");
        def.setReplacementColours(new short[] {0});
        def.pack();

        def = ItemDefinitions.get(6199);
        def.setId(30031);
        def.setName("Pet mystery box");
        def.setGrandExchange(true);
        def.setPlaceholderId(30032);
        def.setReplacementColours(new short[] {0});
        def.pack();

        for (val cosmeticBoxEntry : CosmeticBox.CosmeticItem.values()) {
            def = ItemDefinitions.get(cosmeticBoxEntry.getId());
            if (def == null) {
                continue;
            }
            def.setGrandExchange(true);
            val index = ArrayUtils.indexOf(def.getInventoryOptions(), "Destroy");
            System.err.println(cosmeticBoxEntry.getId() + " - " + index);
            if (index != -1) {
                def.getInventoryOptions()[index] = "Drop";
            }
            def.pack();
        }

        def = ItemDefinitions.get(22333);
        def.setZoom(1900);
        def.setModelRoll(0);
        def.setInventoryModelId(52517);
        def.setPrimaryMaleModel(52518);
        def.setPrimaryFemaleModel(52518);
        def.pack();

        def = ItemDefinitions.get(22335);
        def.setZoom(2200);
        def.setModelRoll(0);
        def.setModelPitch(500);
        def.setInventoryModelId(52519);
        def.setPrimaryMaleModel(52520);
        def.setPrimaryFemaleModel(52520);
        def.pack();

        def = ItemDefinitions.get(22331);
        def.setZoom(1400);
        def.setModelRoll(0);
        def.setInventoryModelId(52521);
        def.setPrimaryMaleModel(52522);
        def.setPrimaryFemaleModel(52522);
        def.pack();
    }

           /* def = ItemDefinitions.get(2678);
        def.setId(30003);
        def.setName("Master Miner Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30004);
        def.setName("Athletic Runner Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30005);
        def.setName("Master Fisherman Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30006);
        def.setName("Master Slayer Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30007);
        def.setName("Sleight of Hand Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30008);
        def.setName("Fill the Bank Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30009);
        def.setName("Lumberjack Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30011);
        def.setName("Leprechaun's Friend Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30012);
        def.setName("Aubury's Friend Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30012);
        def.setName("Wrath of Zaros Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30013);
        def.setName("Riddle in the tunnels Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30014);
        def.setName("Fertilizer Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30015);
        def.setName("Backfire Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();

        def = ItemDefinitions.get(2678);
        def.setId(30016);
        def.setName("Pyromancer Perk");
        def.setInventoryOptions(new String[] { "Redeem", null, null, null, null });
        def.pack();*/

}
