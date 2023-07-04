package mgi.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import mgi.custom.halloween.HalloweenNPCPacker;
import mgi.types.config.npcs.NPCDefinitions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Tommeh | 16-12-2018 | 18:27
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class CustomNPCs {

    private static final String[] SHOP_OPTIONS = {null, null, "Trade", null, null};

    public static void pack() throws IOException {
        new HalloweenNPCPacker().pack();
        var def = NPCDefinitions.get(379); //challenge headmaster
        def.setName("Challenge Headmaster");
        def.setOptions(new String[]{"Talk-to", null, "View Challenges", null, null});
        def.pack();

        for (int id = 2148; id <= 2151; id++) {

            def = NPCDefinitions.get(id); //GE clerks
            def.setOptions(new String[]{"Talk-to", "Exchange", "History", "Offers Viewer", "Sets"});
            def.pack();
        }

        def = NPCDefinitions.get(3308); //Zenyte guide @tutorial island
        def.setName("Zenyte Guide");
        def.setOptions(new String[]{"Talk-to", "Trade", null, null, null});
        def.pack();

        def = NPCDefinitions.get(6088);
        def.setOptions(new String[]{"Glider", null, null, null, null});

        def = NPCDefinitions.get(7456); //perdu
        def.setOptions(new String[]{"Repair", null, null, null, null});
        def.pack();

        def = NPCDefinitions.get(7690);
        def.setOptions(new String[]{"Talk-to", "Practice Mode", null, null, null});
        def.pack();

        //Zenyte teleport wizard
        def = NPCDefinitions.get(4398);
        def.setName("Zenyte Teleporter");
        def.setId(10000);
        def.setModels(new int[]{28220, 214, 250, 28991, 28226, 28224, 177, 534, 10698, 28223, 29249});
        def.setOptions(new String[]{"Teleport", null, null, "Teleport-previous", null});
        def.setStandAnimation(813);
        def.setWalkAnimation(1205);
        def.setRotate90Animation(1207);
        def.setRotate180Animation(1206);
        def.setRotate270Animation(1208);
        def.pack();

        //Melee armour shop
        def = NPCDefinitions.get(1176);
        def.setName("Cynthia");
        def.setId(10001);
        def.setOptions(new String[] { null, null, "Trade", null, null });
        def.pack();




        //Melee weapons shop
        def = NPCDefinitions.get(4105);
        def.setName("Arnas");
        def.setId(10002);
        def.setOptions(SHOP_OPTIONS);
        def.setModels(new int[]{230, 246, 302, 168, 179, 263, 185, 491});
        def.pack();

        //Zahur
        def = NPCDefinitions.get(4753);
        val options = new ObjectArrayList<String>(Arrays.asList(def.getOptions()));
        options.removeIf(Objects::isNull);
        options.add(options.size() - 1, "Crush secondaries");
        def.setOptions(options.toArray(new String[0]));
        def.pack();

        //Ranged armour shop
        def = NPCDefinitions.get(6067);
        def.setName("Robin");
        def.setId(10003);
        def.setOptions(SHOP_OPTIONS);
        def.setCombatLevel(0);
        def.setModels(new int[]{220, 253, 303, 169, 176, 277, 185, 20423});
        def.pack();

        //Ranged weapons shop
        def = NPCDefinitions.get(1157);
        def.setName("Fae");
        def.setId(10004);
        def.setOptions(SHOP_OPTIONS);
        def.setModels(new int[]{390, 414, 477, 20429, 332, 356, 512, 422});
        def.pack();

        //Magic weapons shop
        def = NPCDefinitions.get(881);
        def.setName("Dhalius");
        def.setId(10005);
        def.setOptions(SHOP_OPTIONS);
        def.pack();

        //Magic armour shop
        def = NPCDefinitions.get(3232);
        def.setName("Edalf");
        def.setId(10006);
        def.setOptions(SHOP_OPTIONS);
        def.pack();

        def = NPCDefinitions.get(1027); //food shop
        def.setName("John");
        def.setId(10007);
        def.setOptions(SHOP_OPTIONS);
        def.setModels(new int[]{18191, 217, 302, 3190, 10980, 176, 562, 254, 185, 246});
        def.pack();

        def = NPCDefinitions.get(5036); //skilling shop
        def.setName("Jackie");
        def.setId(10008);
        def.setOptions(new String[]{null, null, "Trade", "Jewellery", null});
        def.pack();

        def = NPCDefinitions.get(4225); //vote shop
        def.setName("Frank");
        def.setId(10009);
        def.setOptions(new String[]{null, null, "Vote shop", "Loyalty shop", null});
        def.pack();

        def = NPCDefinitions.get(6481); //mac
        def.setName("Mac");
        def.setCombatLevel(0);
        def.setId(10010);
        def.setOptions(new String[]{"Talk-to", null, "Trade", null, null});
        def.pack();

        def = NPCDefinitions.get(3434); //tournament guard
        def.setId(10011);
        def.setName("Tournament Guard");
        def.setCombatLevel(0);
        def.setOptions(new String[]{"Talk-to", null, "Spectate", null, null});
        def.pack();

        def = NPCDefinitions.get(3434); //tournament guard
        def.setId(10012);
        def.setName("Tournament Guard");
        def.setCombatLevel(0);
        def.setOptions(new String[]{"Talk-to", null, "View Tournaments", null, null});
        def.pack();

        def = NPCDefinitions.get(1257); //cute creature
        System.err.println(Arrays.toString(def.getOptions()));
        def.setName("Cute creature");
        def.setId(10013);
        def.setCombatLevel(0);
        def.setOptions(new String[]{null, null, "Pick-up", null, null});
        def.setFilteredOptions(new String[]{null, null, "Pick-up", null, null});
        def.setMinimapVisible(true);
        def.setFamiliar(true);
        def.pack();

        def = NPCDefinitions.get(2922); //stray dog
        def.setId(10014);
        def.setCombatLevel(0);
        def.setOptions(new String[]{null, null, "Pick-up", null, null});
        def.setFilteredOptions(new String[]{null, null, "Pick-up", null, null});
        def.setMinimapVisible(true);
        def.setFamiliar(true);
        def.pack();

        def = NPCDefinitions.get(1258); //evil creature
        def.setName("Evil creature");
        def.setId(10015);
        def.setCombatLevel(0);
        def.setOptions(new String[]{null, null, "Pick-up", null, null});
        def.setFilteredOptions(new String[]{null, null, "Pick-up", null, null});
        def.setMinimapVisible(true);
        def.setFamiliar(true);
        def.pack();

        def = NPCDefinitions.get(7697); //Jal-ImRek
        def.setName("Jal-ImRek");
        def.setId(10016);
        def.setCombatLevel(0);
        def.setOptions(new String[]{null, null, "Pick-up", null, null});
        def.setFilteredOptions(new String[]{null, null, "Pick-up", null, null});
        def.setMinimapVisible(true);
        def.setFamiliar(true);
        def.setSize(1);
        def.setResizeX(30);
        def.setResizeY(30);
        def.setRotate90Animation(7595);
        def.setRotate180Animation(7595);
        def.setRotate270Animation(7595);
        def.pack();

        def = NPCDefinitions.get(5792); //bucket pete
        def.setName("Bucket Pete");
        def.setId(10017);
        def.setCombatLevel(0);
        def.setOptions(new String[]{null, null, "Pick-up", null, null});
        def.setFilteredOptions(new String[]{null, null, "Pick-up", null, null});
        def.setMinimapVisible(true);
        def.setFamiliar(true);
        def.setModels(new int[]{230, 246, 31759, 167, 176, 297, 267, 181});
        def.setResizeX(80);
        def.setResizeY(80);
        def.pack();

        def = NPCDefinitions.get(8611); //Wyrm pet
        def.setName("Wyrmy");
        def.setId(10018);
        def.setCombatLevel(0);
        def.setOptions(new String[]{null, null, "Pick-up", null, null});
        def.setFilteredOptions(new String[]{null, null, "Pick-up", null, null});
        def.setMinimapVisible(true);
        def.setFamiliar(true);
        def.setSize(1);
        def.setResizeX(30);
        def.setResizeY(30);
        def.setRotate90Animation(8267);
        def.setRotate180Animation(8267);
        def.setRotate270Animation(8267);
        def.pack();

//2668 : 7413
        def = NPCDefinitions.get(2668); //Combat dummy
        def.setId(10019);
        def.setOptions(new String[]{null, "Attack", null, null, null});
        def.setFilteredOptions(new String[]{null, "Attack", null, null, null});
        def.pack();

        def = NPCDefinitions.get(7413); //Undead combat dummy
        def.setId(10020);
        def.setOptions(new String[]{null, "Attack", null, null, null});
        def.setFilteredOptions(new String[]{null, "Attack", null, null, null});
        def.pack();

        def = NPCDefinitions.get(1307); //make over mage
        def.setId(10021);
        def.setOptions(new String[]{"Talk-to", null, null, "Makeover", "Skin Colour"});
        def.pack();

        def = NPCDefinitions.get(1870); //Thanksgiving turkey
        def.setId(10022);
        def.setOptions(new String[]{null, null, null, null, null});
        def.setCombatLevel(1);
        def.setName("Thanksgiving Turkey");
        def.setModels(new int[]{52531});
        def.setStandAnimation(-1);
        def.setWalkAnimation(-1);
        def.setRotate90Animation(-1);
        def.setRotate180Animation(-1);
        def.setRotate270Animation(-1);
        def.setChatModels(null);
        def.setSize(1);
        def.pack();
    }
}
