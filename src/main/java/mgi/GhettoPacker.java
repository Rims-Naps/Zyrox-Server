package mgi;

import com.zenyte.Constants;
import com.zenyte.Game;
import com.zenyte.game.constants.GameConstants;
import com.zenyte.game.content.achievementdiary.AchievementDiaries;
import com.zenyte.game.content.achievementdiary.DiaryArea;
import com.zenyte.game.content.treasuretrails.stash.StashUnit;
import com.zenyte.game.ui.testinterfaces.BountyHunterStoreInterface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.MapUtils;
import com.zenyte.game.world.region.XTEALoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.custom.*;
import mgi.custom.christmas.ChristmasMapPacker;
import mgi.custom.christmas.ChristmasObject;
import mgi.custom.halloween.HalloweenMapPacker;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.Group;
import mgi.types.Definitions;
import mgi.types.component.ComponentDefinitions;
import mgi.types.component.custom.*;
import mgi.types.component.type.GraphicComponent;
import mgi.types.component.type.LayerComponent;
import mgi.types.component.type.TextComponent;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.enums.SkeletonEnum;
import mgi.types.sprite.SpriteGroupDefinitions;
import mgi.types.worldmap.WorldMapDefinitions;
import mgi.utilities.ByteBuffer;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static mgi.types.component.ComponentDefinitions.CENTER;

/**
 * @author Tommeh | 13 aug. 2018 | 16:54:08
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
@Slf4j
public class GhettoPacker {

    private static final Logger logger = LogManager.getLogger(GhettoPacker.class);


    public static void main(final String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: dedi/local");
            return;
        }
        Game.load();
        Definitions.loadDefinitions(Definitions.LOW_PRIORITY_DEFINITIONS);
        /*if (args[0].equals("local")) {
            packLocal();
        } else {
            try {
                XTEALoader.load();
                packOpenrs();
                packDedi();
            } catch (Throwable e) {
                log.error(Strings.EMPTY, e);
            }
        }*/
    }

    public static void packLocal() throws IOException {
        /*CustomNPCs.pack();
        //CustomObjects.pack();
        ComponentDefinitions component = new LayerComponent(); //3
        component.setParentId(1);
        component.setPosition(57, 7);
        component.setSize(80, 20);
        component.setClickMask(AccessMask.CLICK_OP1);
        component.setOption(0, "Exchange");
        component.setOnLoadListener(new Object[]{540, -2147483645, "Offer Viewer"});
        packComponent(465, 30, component);*/
        //packInterface(704, new DropViewer().assemble(704));
        //packInterface(709, new GrandExchangeOffersViewer().assemble(709));
        //CustomItems.pack();
        //packSprites();
        /*for (int id = 10102; id <= 10121; id++) {
            packClientScript(id, Files.readAllBytes(Paths.get("assets/cs2/drop_viewer/" + id + ".cs2")));
        }*/
        /*for (int i = 10800; i <= 10810; i++) {
            packClientScript(i, Files.readAllBytes(Paths.get("assets/cs2/ge_offers/" + i + ".cs2")));
        }*/
        //Game.getLibrary().getIndex(2).update();
        //Game.getLibrary().getIndex(4).update();
        /*Game.getLibrary().getIndex(8).update();
        Game.getLibrary().getIndex(12).update();*/
        //new ChristmasMapPacker().pack();
        /*for (int index = 0; index < 20; index++) {
            Game.getLibrary().getIndex(index).update();
        }*/

        //packClientBackground(Files.readAllBytes(Paths.get("assets/sprites/background/background_desktop.png")),
        //        Files.readAllBytes(Paths.get("assets/sprites/background/background_mobile.png")),
        //        Files.readAllBytes(Paths.get("assets/sprites/background/background_logo_inferno.png")));
        /*ComponentDefinitions comp = ComponentDefinitions.get(205, 0);
        comp.setSize(288, 305);
        packComponent(205, 0, comp);

        comp = ComponentDefinitions.get(205, 1);
        comp.setOnLoadListener(new Object[] { 227, -2147483645, "Select skin colour" });
        packComponent(205, 1, comp);

        comp = ComponentDefinitions.get(205, 2);
        comp.setHidden(true);
        packComponent(205, 2, comp);

        comp = ComponentDefinitions.get(205, 6);
        comp.setHidden(true);
        packComponent(205, 6, comp);

        comp = ComponentDefinitions.get(205, 9);
        comp.setDynamicSize(0, 0);
        comp.setSize(66, 235);
        packComponent(205, 9, comp);
        //Game.getLibrary().getIndex(2).update();
        //Game.getLibrary().getIndex(4).update();


        Game.getLibrary().getIndex(8).update();
        Game.getLibrary().getIndex(12).update();*/
        packSprites();
        //Game.getLibrary().getIndex(8).update();
    }


    private static void packMaps() throws IOException {
        new HalloweenMapPacker().pack();
        packMap(9261, Files.readAllBytes(Paths.get("assets/map/island_l_regular.dat")), MapUtils.inject(Files.readAllBytes(Paths.get("assets/map/island_m_regular.dat")), o -> {
            if (o.getId() == 46087) {
                o.setId(46089);
            }
            return false;
        }));
        packMap(11567, null, MapUtils.inject(11567, null,
                new WorldObject(187, 10, 1, new Location(2919, 3054, 0))));
        packMap(11595, null, MapUtils.inject(11595, null,
                new WorldObject(26254, 10, 0, new Location(2931, 4822, 0)),
                new WorldObject(26254, 10, 0, new Location(2896, 4821, 0)),
                new WorldObject(26254, 10, 1, new Location(2900, 4845, 0)),
                new WorldObject(26254, 10, 3, new Location(2920, 4848, 0))));
        packMap(12342, Files.readAllBytes(Paths.get("assets/map/home28_l.dat")), MapUtils.inject(Files.readAllBytes(Paths.get("assets/map/home28_m.dat")),
                o -> {
                    o.setId(ChristmasObject.redirectedIds.getOrDefault(o.getId(), o.getId()));
                    if (o.getId() == 46087) {
                        o.setId(46089);
                    } else if (o.getId() == 11784) {
                        o.setId(35009);
                    } else if (o.getId() == 11785) {
                        o.setId(35010);
                    } else if (o.getId() == 15617) {
                        o.setId(46030);
                    }
                    return o.hashInRegion() == new Location(3092, 3487, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3094, 3489, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3095, 3488, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3097, 3488, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3100, 3486, 0).hashInRegion()
                            || o.hashInRegion() == new Location(3127, 3496, 0).hashInRegion();
                },
                new WorldObject(0, 10, 0, new Location(3117, 3474, 0)),//Blocking tile behind combat dummy
                new WorldObject(StashUnit.EDGEVILLE_GENERAL_STORE.getObjectId(), 10, 0, new Location(3079, 3484, 0)),
                new WorldObject(0, 10, 0, new Location(3117, 3481, 0)),//Blocking tile behind combat dummy
                new WorldObject(35008, 10, 3, new Location(3092, 3508, 1)),//Lectern w/ study option at home.
                new WorldObject(14108, 22, 0, new Location(3101, 3487, 0)),//Map hyperlink for edgeville dungeon.
                new WorldObject(172, 10, 2, new Location(3091, 3511, 0)),//Crystal chest
                new WorldObject(7389, 22, 0, new Location(3115, 3505, 0)),//Map icon for wilderness lever.
                new WorldObject(7389, 22, 0, new Location(3097, 3503, 0)),//Map icon for portal.
                new WorldObject(7389, 22, 0, new Location(3097, 3488, 0)),//Map icon for spiritual tree.
                new WorldObject(673, 22, 0, new Location(3119, 3511, 0)),//Map icon for emblem trader.
                new WorldObject(35003, 10, 1, new Location(3096, 3487, 0)),//Spiritual fairy ring.
                new WorldObject(35000, 10, 2, new Location(3095, 3503, 0)),//Portal
                new WorldObject(35001, 10, 0, new Location(3090, 3486, 0)),//Box of Restoration
                new WorldObject(26756, 10, 0, new Location(3085, 3509, 0)),//Wilderness statistics
        
              /*  new WorldObject(ChristmasConstants.CHRISTMAS_CUPBOARD_ID, 10, 0, ChristmasConstants.homeChristmasCupboardLocation),//Christmas cupboard
                new WorldObject(1579, 22, 2, 3100, 3487, 0),//Trapdoor
                new WorldObject(2734, 22, 0, 3095, 3483, 0),//Missing mapicon
                new WorldObject(2747, 22, 0, 3092, 3485, 0),//Missing mapicon
                new WorldObject(2771, 22, 1, 3100, 3489, 0),//Missing mapicon
                new WorldObject(2772, 22, 2, 3102, 3494, 0),//Missing mapicon
                new WorldObject(2774, 22, 0, 3108, 3497, 0),//Missing mapicon
                new WorldObject(2742, 22, 0, 3112, 3501, 0),//Missing mapicon
                new WorldObject(5118, 22, 1, 3113, 3509, 0),//Missing mapicon
                new WorldObject(23590, 22, 0, 3117, 3516, 0),//Missing mapicon
                new WorldObject(26301, 22, 3, 3102, 3506, 0),//Missing mapicon
                new WorldObject(33163, 22, 0, 3098, 3511, 0),//Missing mapicon
                new WorldObject(2752, 22, 2, 3091, 3509, 0),//Missing mapicon
                new WorldObject(16458, 22, 0, 3090, 3498, 0),//Missing mapicon
                new WorldObject(2738, 22, 0, 3086, 3493, 0),//Missing mapicon
                new WorldObject(2756, 22, 1, 3084, 3507, 0),//Missing mapicon
                new WorldObject(2758, 22, 1, 3079, 3510, 0),//Missing mapicon
                new WorldObject(2753, 22, 1, 3078, 3507, 0),//Missing mapicon
                new WorldObject(2750, 22, 0, 3075, 3502, 0),//Missing mapicon
                new WorldObject(2766, 22, 0, 3078, 3499, 0),//Missing mapicon
                new WorldObject(2760, 22, 0, 3077, 3492, 0),//Missing mapicon
                new WorldObject(2768, 22, 2, 3073, 3491, 0),//Missing mapicon
                new WorldObject(2735, 22, 0, 3077, 3488, 0),//Missing mapicon
                new WorldObject(2733, 22, 0, 3082, 3485, 0),//Missing mapicon


                new WorldObject(20132, 22, 0, new Location(3092, 3503, 0)),//Event map icon*/
        
                new WorldObject(35002, 10, 0, new Location(3096, 3511, 0))));//Mounted max cape
        packMap(13109, null, MapUtils.inject(13109, null,
                new WorldObject(187, 10, 1, new Location(3322, 3428, 0))));
        packMap(14477, Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m56_141.dat")), Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l56_141.dat")));
        packMap(14478, Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m56_142.dat")), Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l56_142.dat")));
        packMap(14733, Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m57_141.dat")), Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l57_141.dat")));
        packMap(14734, Files.readAllBytes(Paths.get("assets/map/dmm_tourny/m57_142.dat")), Files.readAllBytes(Paths.get("assets/map/dmm_tourny/l57_142.dat")));
        packMap(15245, Files.readAllBytes(Paths.get("assets/map/tournament/2.dat")), Files.readAllBytes(Paths.get("assets/map/tournament/3.dat")));
        packMap(15248, Files.readAllBytes(Paths.get("assets/map/tournament/0.dat")), MapUtils.inject(Files.readAllBytes(Paths.get("assets/map/tournament/1.dat")), null,
                new WorldObject(35005, 10, 3, new Location(3806, 9245, 0)),
                new WorldObject(35006, 10, 1, new Location(3813, 9256, 0)),
                new WorldObject(35007, 10, 0, new Location(3799, 9256, 0))));
    }

    private static void packOpenrs() throws Exception {
        packComponents();
        packInterfaces();
        packMaps();
        packClientBackground(Files.readAllBytes(Paths.get("assets/sprites/background/background_desktop.png")),
                Files.readAllBytes(Paths.get("assets/sprites/background/background_mobile.png")),
                Files.readAllBytes(Paths.get("assets/sprites/background/background_logo.png")));
    }

    private static void packDedi() throws Exception {
        reload();
        CustomObjects.pack();
        packEnums();
        CustomNPCs.pack();
        CustomItems.pack();
        CustomAnimations.pack();
        CustomGraphics.pack();
        //StructHandler.pack();

        packModels();
        packSprites();
        packClientScripts();

        //new DiceBagPacker().pack();
        //new TrickPacker().pack();
        new ThanksgivingPacker().pack();
        new ChristmasMapPacker().packFull();
        new HighDefinitionPets().packFull();
        FramePacker.write();

        AnimationBase.pack();

        for (int index = 0; index < 20; index++) {
            //Game.getLibrary().getIndex(index).update();
        }

        reload();

        val defs = WorldMapDefinitions.decode("main");
        defs.update(9261, 0);
        defs.update(12342, 0);
        defs.encode("main");

        logger.info("Finished packing assets for dedi.");
    }

    private static void reload() {
            //Game.getLibrary().close();
            //Game.getCache().close();
            Game.load();
            Definitions.loadDefinitions(Definitions.LOW_PRIORITY_DEFINITIONS);
    }

    private static void packComponents() {
        ComponentDefinitions comp;

        //Keybinds EOC option.
        val lastComponent = Utils.getIndiceSize(Indice.INTERFACE_DEFINITIONS, 121);
        int last = lastComponent;
        for (int i = 104; i <= 106; i++) {
            comp = ComponentDefinitions.get(121, i);
            if (i == 104) {
                comp.getActions()[0] = "Pre-EOC Layout";
                comp.setY(180);
            } else if (i == 106) {
                comp.setText("Pre-EOC Layout");
            }
            if (i != 104) {
                comp.setParentId(lastComponent);
            }
            comp.setComponentId(last++);
            packComponent(121, comp.getComponentId(), comp);
        }

        comp = ComponentDefinitions.get(205, 0);
        comp.setSize(278, 305);
        packComponent(205, 0, comp);

        comp = ComponentDefinitions.get(205, 1);
        comp.setOnLoadListener(new Object[]{227, -2147483645, "Select skin colour"});
        packComponent(205, 1, comp);

        comp = ComponentDefinitions.get(205, 2);
        comp.setHidden(true);
        packComponent(205, 2, comp);

        comp = ComponentDefinitions.get(205, 6);
        comp.setHidden(true);
        packComponent(205, 6, comp);

        comp = ComponentDefinitions.get(205, 9);
        comp.setDynamicSize(0, 0);
        comp.setSize(66, 235);
        packComponent(205, 9, comp);

        comp = new GraphicComponent(541);
        comp.setParentId(0);
        comp.setDynamicPosition(2, 0);
        comp.setPosition(10, 20);
        comp.setSize(26, 23);
        comp.setClickMask(AccessMask.CLICK_OP1);
        comp.setOption(0, "Close");
        comp.setOnMouseOverListener(new Object[]{44, -2147483645, 542});
        comp.setOnMouseLeaveListener(new Object[]{44, -2147483645, 541});
        comp.setOnClickListener(new Object[]{29});
        packComponent(154, 59, comp);

        comp = new LayerComponent();
        comp.setParentId(0);
        comp.setDynamicPosition(2, 1);
        comp.setPosition(49, -75);
        comp.setSize(202, 180);
        comp.setOnLoadListener(new Object[]{712, -2147483645, 1});
        packComponent(154, 60, comp);

        comp = new GraphicComponent(897);
        comp.setParentId(60);
        comp.setSpriteTiling(true);
        comp.setDynamicSize(1, 1);
        comp.setDynamicPosition(1, 1);
        comp.setSize(2, 2);
        packComponent(154, 61, comp);

        comp = new TextComponent("Spectator Manual", 496, CENTER, CENTER);
        comp.setParentId(60);
        comp.setDynamicSize(1, 0);
        comp.setPosition(0, 9);
        comp.setSize(0, 20);
        packComponent(154, 62, comp);

        comp = new LayerComponent();
        comp.setParentId(60);
        comp.setPosition(3, 29);
        comp.setSize(196, 168);
        packComponent(154, 63, comp);

        comp = new LayerComponent();
        comp.setParentId(63);
        comp.setPosition(13, 40);
        comp.setSize(32, 32);
        comp.setOnLoadListener(new Object[]{1661, -2147483645, "A"});
        packComponent(154, 64, comp);

        comp = new LayerComponent();
        comp.setParentId(63);
        comp.setPosition(44, 40);
        comp.setSize(32, 32);
        comp.setOnLoadListener(new Object[]{1661, -2147483645, "S"});
        packComponent(154, 65, comp);

        comp = new LayerComponent();
        comp.setParentId(63);
        comp.setPosition(75, 40);
        comp.setSize(32, 32);
        comp.setOnLoadListener(new Object[]{1661, -2147483645, "D"});
        packComponent(154, 66, comp);

        comp = new LayerComponent();
        comp.setParentId(63);
        comp.setPosition(44, 10);
        comp.setSize(32, 32);
        comp.setOnLoadListener(new Object[]{1661, -2147483645, "W"});
        packComponent(154, 67, comp);

        comp = new TextComponent("WASD<br>Move Camera", "#ffffff", 494, CENTER, CENTER);
        comp.setParentId(63);
        comp.setPosition(109, 25);
        comp.setSize(75, 28);
        packComponent(154, 68, comp);

        comp = new LayerComponent();
        comp.setParentId(63);
        comp.setPosition(44, 75);
        comp.setSize(32, 32);
        comp.setOnLoadListener(new Object[]{1661, -2147483645, "R"});
        packComponent(154, 69, comp);

        comp = new LayerComponent();
        comp.setParentId(63);
        comp.setPosition(44, 105);
        comp.setSize(32, 32);
        comp.setOnLoadListener(new Object[]{1661, -2147483645, "F"});
        packComponent(154, 70, comp);

        comp = new TextComponent("R/F<br>Adjust Camera Height", "#ffffff", 494, CENTER, CENTER);
        comp.setParentId(63);
        comp.setPosition(109, 90);
        comp.setSize(75, 28);
        packComponent(154, 71, comp);

        comp = ComponentDefinitions.get(160, 1);
        val actions = new String[3];
        actions[0] = comp.getActions()[0];
        actions[1] = comp.getActions()[1];
        actions[2] = "XP Multiplier";
        comp.setAccessMask(14);
        comp.setActions(actions);
        packComponent(160, 1, comp);

        comp = ComponentDefinitions.get(192, 1);
        comp.setOnLoadListener(new Object[]{227, -2147483645, "The Bank of " + GameConstants.SERVER_NAME + " - Deposit Box"});
        packComponent(192, 1, comp);

        comp = new LayerComponent();
        comp.setParentId(1);
        comp.setPosition(195, 235);
        comp.setSize(90, 35);
        comp.setClickMask(AccessMask.CLICK_OP1);
        comp.setOption(0, "Confirm");
        comp.setHidden(true);
        comp.setOnLoadListener(new Object[]{92, -2147483645});
        comp.setOnMouseOverListener(new Object[]{94, -2147483645});
        comp.setOnMouseLeaveListener(new Object[]{92, -2147483645});
        packComponent(215, 30, comp);

        comp = new TextComponent("Confirm", ComponentDefinitions.FONT_BOLD, CENTER, CENTER);
        comp.setParentId(30);
        comp.setPosition(0, 0);
        comp.setSize(90, 35);
        packComponent(215, 31, comp);

        comp = ComponentDefinitions.get(218, 4);
        comp.getActions()[1] = "Lumbridge";
        comp.accessMask |= AccessMask.CLICK_OP2.getValue();
        packComponent(218, 4, comp);

        comp = ComponentDefinitions.get(218, 99);
        comp.getActions()[1] = "Lunar Isle";
        comp.accessMask |= AccessMask.CLICK_OP2.getValue();
        packComponent(218, 99, comp);

        comp = ComponentDefinitions.get(218, 143);
        comp.getActions()[1] = "Arceuus";
        comp.accessMask |= AccessMask.CLICK_OP2.getValue();
        packComponent(218, 143, comp);

        comp = new LayerComponent();
        comp.setParentId(1);
        comp.setPosition(57, 7);
        comp.setSize(80, 20);
        comp.setClickMask(AccessMask.CLICK_OP1);
        comp.setOption(0, "Exchange");
        comp.setOnLoadListener(new Object[]{540, -2147483645, "Offer Viewer"});
        packComponent(465, 30, comp);
    }

    private static void packInterfaces() throws IOException {
        /*val archive = new Archive(38);
        for (int i = 0; i < 38; i++) {
            val buffer = ByteBuffer.wrap(Files.readAllBytes(Paths.get("assets/interfaces/700/" + i)));
            archive.putEntry(i, buffer);
        }
        Game.getCache().writeArchive(ArchiveType.INTERFACES.getId(), 700, archive);*/
        packInterface(701, new GameNoticeboard().assemble(701));
        packInterface(702, new GameSettings().assemble(702));
        packInterface(703, new ExperienceModeSelection().assemble());
        packInterface(704, new DropViewer().assemble(704));
        packInterface(705, new DailyChallengesOverview().assemble(705));
        packInterface(706, new TournamentInformation().assemble(706));
        packInterface(707, new TournamentPresetsManager().assemble(707));
        packInterface(708, new TournamentViewer().assemble(708));
        packInterface(709, new GrandExchangeOffersViewer().assemble(709));
    }

    private static void packSprites() throws IOException {
        var sprite = new SpriteGroupDefinitions(423, 13, 13);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_0.png")));
        sprite.setImage(1, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_1.png")));
        sprite.setImage(2, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_2.png")));
        sprite.setImage(3, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_3.png")));
        sprite.setImage(4, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/support.png")));
        sprite.setImage(5, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/global_mod.png")));
        sprite.setImage(6, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/forum_mod.png")));
        sprite.setImage(7, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/youtuber.png")));
        sprite.setImage(8, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_8.png")));
        sprite.setImage(9, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_9.png")));
        sprite.setImage(10, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_10.png")));
        sprite.setImage(11, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_11.png")));
        sprite.setImage(12, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_12.png")));
        sprite.setImage(13, ImageIO.read(new File("./assets/sprites/chat_icons/announcement.png")));
        sprite.setImage(14, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/sapphire.png")));
        sprite.setImage(15, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/emerald.png")));
        sprite.setImage(16, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/ruby.png")));
        sprite.setImage(17, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/diamond.png")));
        sprite.setImage(18, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/dragonstone.png")));
        sprite.setImage(19, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/onyx.png")));
        sprite.setImage(20, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/zenyte.png")));
        sprite.setImage(21, ImageIO.read(new File("./assets/sprites/chat_icons/ranks/in-game/booster.png")));
        //sprite.setImage(21, ImageIO.read(new File("./assets/sprites/chat_icons/sprite_423_21.png")));
        sprite.pack();

        /*sprite = SpriteGroupDefinitions.get(499);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/inferno_loginscreen/sprite_499_0.png")));
        sprite.pack();

        sprite = SpriteGroupDefinitions.get(500);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/inferno_loginscreen/sprite_500_0.png")));
        sprite.pack();

        sprite = SpriteGroupDefinitions.get(811);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/inferno_loginscreen/sprite_811_0.png")));
        sprite.setImage(1, ImageIO.read(new File("./assets/sprites/inferno_loginscreen/sprite_811_1.png")));
        sprite.pack();

        sprite = SpriteGroupDefinitions.get(818);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/inferno_loginscreen/sprite_818_0.png")));
        sprite.pack();*/
/*
        sprite = SpriteGroupDefinitions.get(2134);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/mobile_button_logo.png")));
        sprite.pack();*/

       /* sprite = SpriteGroupDefinitions.get(2135);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/inferno_loginscreen/sprite_2135_0.png")));
        sprite.pack();

        sprite = SpriteGroupDefinitions.get(2137);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/inferno_loginscreen/sprite_2137_0.png")));
        sprite.pack();*/
/*
        sprite = new SpriteGroupDefinitions(2500, 16, 20);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/teleport/candle lantern.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2501, 13, 13);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/gamenoticeboard/website_icon.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2502, 13, 13);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/gamenoticeboard/forums_icon.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2503, 13, 13);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/gamenoticeboard/discord_icon.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2504, 13, 13);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/gamenoticeboard/store_icon.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2505, 13, 13);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/gamenoticeboard/game_settings_icon.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2506, 13, 13);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/gamenoticeboard/daily_challenges_icon.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2507, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/emotes/trick emote lit.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2508, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/emotes/trick emote unlit.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2509, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/emotes/give thanks lit.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2510, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/sprites/emotes/give thanks unlit.png")));
        sprite.pack();

        sprite = new SpriteGroupDefinitions(2511, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/christmas/sprites/Snowman dance lit.png")));
        sprite.pack();
        sprite = new SpriteGroupDefinitions(2512, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/christmas/sprites/Snowman dance unlit.png")));
        sprite.pack();
        sprite = new SpriteGroupDefinitions(2513, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/christmas/sprites/Freeze lit.png")));
        sprite.pack();
        sprite = new SpriteGroupDefinitions(2514, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/christmas/sprites/Freeze unlit.png")));
        sprite.pack();
        sprite = new SpriteGroupDefinitions(2515, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/christmas/sprites/Dramatic point lit.png")));
        sprite.pack();
        sprite = new SpriteGroupDefinitions(2516, 48, 48);
        sprite.setImage(0, ImageIO.read(new File("./assets/christmas/sprites/Dramatic point unlit.png")));
        sprite.pack();*/
    }

    private static void packEnums() {
        //EnumHandler.load();
        new MusicEnumPacker().pack();
        val e = EnumDefinitions.getStringEnum(1141);

        val skeleton = new SkeletonEnum();
        skeleton.setId(e.getId());
        skeleton.setKeyType(EnumDefinitions.TYPE_MAP.get(e.getKeyType()));
        skeleton.setValType(EnumDefinitions.TYPE_MAP.get(e.getValType()));
        skeleton.setDefaultString(e.getDefaultValue());
        val map = new HashMap<Integer, Object>();
        e.getValues().forEach(map::put);
        map.put(8, "Game Noticeboard");
        skeleton.setValues(map);
        //EnumHandler.ENUMS.add(skeleton);

        var it = new SkeletonEnum();
        it.setKeyType("int");
        it.setValType("namedobj");
        it.setDefaultInt(-1);
        it.setId(1974);
        var values = new HashMap<Integer, Object>();
        int id = 0;
        for (val reward : BountyHunterStoreInterface.Reward.values()) {
            values.put(id++, reward.getId());
        }
        it.setValues(values);
        //EnumHandler.ENUMS.add(it);

        val diaries = AchievementDiaries.ALL_DIARIES;
        for (val diaryEnum : diaries) {
            values = new HashMap<>();
            DiaryArea area = null;
            for (val diary : diaryEnum) {
                if (diary.autoCompleted())
                    continue;
                val complexity = diary.type();
                area = diary.area();
                values.put(complexity.ordinal(), (int) (values.get(complexity.ordinal()) == null ? 0 : values.get(complexity.ordinal())) + 1);
            }
            it = new SkeletonEnum();
            it.setKeyType("int");
            it.setValType("int");
            it.setDefaultInt(-1);
            it.setId(2501 + area.getIndex());
            it.setValues(values);
            //EnumHandler.ENUMS.add(it);
        }

        //EnumHandler.packLoaded();
    }

    private static void packClientScripts() throws IOException {
        packClientScript(73, Files.readAllBytes(Paths.get("assets/cs2/bank_command/73.cs2")));
        packClientScript(386, Files.readAllBytes(Paths.get("assets/cs2/tourny_fog/386.cs2")));
        packClientScript(393, Files.readAllBytes(Paths.get("assets/cs2/skill_tab_construction/393.cs2")));
        packClientScript(395, Files.readAllBytes(Paths.get("assets/cs2/skill_tab_construction/395.cs2")));
        packClientScript(687, Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/687.cs2")));
        packClientScript(1004, Files.readAllBytes(Paths.get("assets/cs2/experience_drops_multiplier.cs2")));
        packClientScript(1261, Files.readAllBytes(Paths.get("assets/cs2/tourny_fog/1261.cs2")));
        packClientScript(1705, Files.readAllBytes(Paths.get("assets/cs2/edgeville_map_link/1705.cs2")));
        packClientScript(2066, Files.readAllBytes(Paths.get("assets/cs2/broadcast_custom_links/2066.cs2")));
        packClientScript(2094, Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/2094.cs2")));
        packClientScript(2096, Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/2096.cs2")));
        packClientScript(2186, Files.readAllBytes(Paths.get("assets/cs2/tourny_viewer/2186.cs2")));
        packClientScript(2200, Files.readAllBytes(Paths.get("assets/cs2/achievement_diary_sizes/2200.cs2")));

        packClientScript(699, Files.readAllBytes(Paths.get("assets/cs2/emote_tab/699.cs2")));
        packClientScript(701, Files.readAllBytes(Paths.get("assets/cs2/emote_tab/701.cs2")));
        packClientScript(702, Files.readAllBytes(Paths.get("assets/cs2/emote_tab/702.cs2")));


        for (int id = 3500; id <= 3504; id++) {
            packClientScript(id, Files.readAllBytes(Paths.get("assets/cs2/game_noticeboard/" + id + ".cs2")));
        }
        for (int id = 0; id < 16; id++) {
            packClientScript(10000 + id, Files.readAllBytes(Paths.get("assets/cs2/teleport_menu/new/" + (10000 + id))));
        }
        packClientScript(10100, Files.readAllBytes(Paths.get("assets/cs2/ironman_setup/10100.cs2")));

        for (int id = 10102; id <= 10121; id++) {
            packClientScript(id, Files.readAllBytes(Paths.get("assets/cs2/drop_viewer/" + id + ".cs2")));
        }

        for (int id = 10200; id <= 10202; id++) {
            packClientScript(id, Files.readAllBytes(Paths.get("assets/cs2/game_settings/" + id + ".cs2")));
        }

        for (int id = 10300; id <= 10306; id++) {
            packClientScript(id, Files.readAllBytes(Paths.get("assets/cs2/daily_challenges/" + id + ".cs2")));
        }

        for (int id = 10400; id <= 10405; id++) {
            packClientScript(id, Files.readAllBytes(Paths.get("assets/cs2/tourny_info/" + id + ".cs2")));
        }

        for (int id = 10500; id <= 10518; id++) {
            packClientScript(id, Files.readAllBytes(Paths.get("assets/cs2/tourny_presets/" + id + ".cs2")));
        }

        packClientScript(10600, Files.readAllBytes(Paths.get("assets/cs2/tourny_viewer/10600.cs2")));
        packClientScript(10700, Files.readAllBytes(Paths.get("assets/cs2/hide_roofs/10700.cs2")));

        for (int i = 10800; i <= 10810; i++) {
            packClientScript(i, Files.readAllBytes(Paths.get("assets/cs2/ge_offers/" + i + ".cs2")));
        }
    }


    private static void packModels() {
        try {
            packModel(38000, Files.readAllBytes(Paths.get("assets/models/zenyte_portal_model.dat")));
            packModel(38001, Files.readAllBytes(Paths.get("assets/models/tournament_supplies.dat")));
            packModel(38002, Files.readAllBytes(Paths.get("assets/models/pets/cute_creature.dat")));
            packModel(38003, Files.readAllBytes(Paths.get("assets/models/pets/stray_dog.dat")));
            packModel(38004, Files.readAllBytes(Paths.get("assets/models/pets/evil_creature.dat")));
            packModel(38005, Files.readAllBytes(Paths.get("assets/models/bonds/cyan_bond.dat")));
            packModel(38006, Files.readAllBytes(Paths.get("assets/models/bonds/red_bond.dat")));
            packModel(50000, Files.readAllBytes(Paths.get("assets/models/zenyte_teletab_50000.dat")));
            packModel(50001, Files.readAllBytes(Paths.get("assets/models/healing fountain.dat")));
            packModel(52505, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Boots(drop)b.dat")));
            packModel(52506, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Bootsb.dat")));
            packModel(52507, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Cape(drop)b.dat")));
            packModel(52508, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Capeb.dat")));
            packModel(52509, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Gloves(drop)b.dat")));
            packModel(52510, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Glovesb.dat")));
            packModel(52511, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Helmet(drop)b.dat")));
            packModel(52512, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Helmetb.dat")));
            packModel(52513, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platebody(drop)b.dat")));
            packModel(52514, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platebodyb.dat")));
            packModel(52515, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platelegs(drop)b.dat")));
            packModel(52516, Files.readAllBytes(Paths.get("assets/models/zenyte armour/Zenyte Platelegsb.dat")));

            packModel(52517, Files.readAllBytes(Paths.get("assets/models/starter/Starter bow ground.dat")));
            packModel(52518, Files.readAllBytes(Paths.get("assets/models/starter/Starter bow.dat")));

            packModel(52519, Files.readAllBytes(Paths.get("assets/models/starter/Starter staff ground.dat")));
            packModel(52520, Files.readAllBytes(Paths.get("assets/models/starter/Starter staff.dat")));

            packModel(52521, Files.readAllBytes(Paths.get("assets/models/starter/Starter sword ground.dat")));
            packModel(52522, Files.readAllBytes(Paths.get("assets/models/starter/Starter sword.dat")));
            packModel(52523, Files.readAllBytes(Paths.get("assets/models/Rare drop table.dat")));

            //Jonas
            packModel(52524, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34041.dat")));
            packModel(52525, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34044.dat")));
            packModel(52526, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34046.dat")));
            packModel(52527, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/jonas/34047.dat")));
            //Grim reaper
            packModel(52528, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/grim reaper/28985.dat")));
            packModel(52529, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/grim reaper/34166.dat")));
            packModel(52530, java.nio.file.Files.readAllBytes(Paths.get("assets/halloween/staticmodels/grim reaper/34167.dat")));
            //Thanksgiving
            packModel(52531, Files.readAllBytes(Paths.get("assets/models/thanksgiving/thanksgiving turkey model.dat")));
            packModel(52532, Files.readAllBytes(Paths.get("assets/models/thanksgiving/thanksgiving poof model.dat")));
            //Christmas scythe
            packModel(52533, Files.readAllBytes(Paths.get("assets/models/christmas scythe inv.dat")));
            packModel(52534, Files.readAllBytes(Paths.get("assets/models/christmas scythe wield.dat")));
            //TT reward casket
            packModel(2450, Files.readAllBytes(Paths.get("assets/models/Treasure trails reward casket.DAT")));

            if (Constants.CHRISTMAS) {
                Iterator<File> it = FileUtils.iterateFiles(new File("assets/christmas/christmas-y entities models/"), null, false);
                val sortedMap = new Int2ObjectAVLTreeMap<File>();
                while (it.hasNext()) {
                    val file = it.next();
                    val originalId = Integer.parseInt(file.getName().replace(".dat", ""));
                    sortedMap.put(originalId, file);
                }
                for (val entry : sortedMap.int2ObjectEntrySet()) {
                    val file = entry.getValue();
                    val bytes = IOUtils.toByteArray(new FileInputStream(file));
                    //Game.getLibrary().getIndex(7).addArchive(Integer.parseInt(file.getName().replace(".dat", ""))).addFile(0, bytes);
                }
            }
            //Scroll boxes
            packModel(53000, Files.readAllBytes(Paths.get("assets/models/scroll boxes/39028.dat")));
            packModel(53001, Files.readAllBytes(Paths.get("assets/models/scroll boxes/39029.dat")));
            packModel(53002, Files.readAllBytes(Paths.get("assets/models/scroll boxes/39030.dat")));
            packModel(53003, Files.readAllBytes(Paths.get("assets/models/scroll boxes/39031.dat")));
            packModel(53004, Files.readAllBytes(Paths.get("assets/models/scroll boxes/39032.dat")));
            packModel(53005, Files.readAllBytes(Paths.get("assets/models/scroll boxes/39033.dat")));

        } catch (IOException e) {
            log.error(Strings.EMPTY, e);
        }
    }

    private static void packClientScript(final int id, final byte[] bytes) {
        //Game.getLibrary().getIndex(12).addArchive(id).addFile(0, bytes);
        logger.info("Packed clientscript: " + id + ".");
    }

    private static void packInterface(final int id, final ArrayList<ComponentDefinitions> components) {
        /*try {
            val cache = Game.getCache();
            val archive = new Archive(components.size());
            for (int i = 0; i < components.size(); i++) {
                archive.putEntry(i, ByteBufferUtils.wrap(components.get(i).encode()));
            }
            cache.writeArchive(ArchiveType.INTERFACES.getId(), id, archive);
            logger.info("Packed interface: " + id + ".");
        } catch (final IOException e) {
            logger.info("Something went wrong with packing interface: " + id + ".");
        }*/
    }

    private static void packComponent(final int interfaceId, final int componentId, final ComponentDefinitions component) {
        /*try {
            val cache = Game.getCache();
            cache.write(ArchiveType.INTERFACES.getId(), interfaceId, componentId, component.encode());
            logger.info("Packed component: " + interfaceId + ":" + componentId + ".");
        } catch (final IOException e) {
            logger.info("Something went wrong with packing component: " + interfaceId + ":" + componentId + ".");
        }*/
    }

    public static void packMap(final int id, final byte[] landscape, final byte[] map) {
        val cache = Game.getCacheMgi();
        val xteas = XTEALoader.getXTEAs(id);
        val regionX = id >> 8;
        val regionY = id & 0xFF;

        val mapId = cache.getArchive(ArchiveType.MAPS.getId()).findGroupByName("m" + regionX + "_" + regionY).getID();
        val landscapeId = cache.getArchive(ArchiveType.MAPS.getId()).findGroupByName("l" + regionX + "_" + regionY).getID();
        if (map != null) {
            val group = new Group(landscapeId, new mgi.tools.jagcached.cache.File(new ByteBuffer(map)));
            group.setName("l" + regionX + "_" + regionY);
            group.setXTEA(xteas);
            cache.getArchive(ArchiveType.MAPS).addGroup(group);
        }
        if (landscape != null) {
            val group = new Group(mapId, new mgi.tools.jagcached.cache.File(new ByteBuffer(landscape)));
            group.setName("m" + regionX + "_" + regionY);
            cache.getArchive(ArchiveType.MAPS).addGroup(group);
        }
        logger.info("Packed region: " + id + ".");
    }

    public static void packModel(final int id, final byte[] bytes) {
        Game.getCacheMgi().getArchive(ArchiveType.MODELS).addGroup(new Group(id, new mgi.tools.jagcached.cache.File(new ByteBuffer(bytes))));
        logger.info("Packed model: " + id + ".");
    }

    private static void packClientBackground(final byte[] desktop, final byte[] mobile, final byte[] logo) {
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.BINARY);
        var group = new Group(1);
        group.addFile(new mgi.tools.jagcached.cache.File(new ByteBuffer(desktop)));
        archive.addGroup(group);
        group = new Group(2);
        group.addFile(new mgi.tools.jagcached.cache.File(new ByteBuffer(mobile)));
        group.addFile(new mgi.tools.jagcached.cache.File(new ByteBuffer(logo)));
        archive.addGroup(group);
        logger.info("Packed client background.");
    }

}
