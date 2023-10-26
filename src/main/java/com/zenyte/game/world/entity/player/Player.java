package com.zenyte.game.world.entity.player;

import com.google.gson.annotations.Expose;
import com.zenyte.Constants;
import com.zenyte.api.client.query.SubmitPlayerInformation;
import com.zenyte.api.client.query.adventurerslog.AdventurersLogIcon;
import com.zenyte.api.client.query.adventurerslog.ApiAdventurersLogRequest;
import com.zenyte.api.client.query.hiscores.SendPlayerHiscores;
import com.zenyte.api.model.ExpMode;
import com.zenyte.api.model.Skill;
import com.zenyte.api.model.SkillHiscore;
import com.zenyte.cores.CoresManager;
import com.zenyte.api.client.query.TotalDonatedRequest;
import com.zenyte.game.BonusCoxManager;
import com.zenyte.game.BonusXpManager;
import com.zenyte.game.constants.GameConstants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.AvasDevice;
import com.zenyte.game.content.GodBooks;
import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.RespawnPoint;
import com.zenyte.game.content.achievementdiary.AchievementDiaries;
import com.zenyte.game.content.boss.cerberus.Cerberus;
import com.zenyte.game.content.boss.grotesqueguardians.instance.GrotesqueGuardiansInstance;
import com.zenyte.game.content.boss.skotizo.npc.DarkAnkou;
import com.zenyte.game.content.boss.skotizo.npc.ReanimatedDemon;
import com.zenyte.game.content.boss.skotizo.npc.Skotizo;
import com.zenyte.game.content.boss.zulrah.SnakelingNPC;
import com.zenyte.game.content.boss.zulrah.ZulrahNPC;
import com.zenyte.game.content.bountyhunter.BountyHunter;
import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.npc.IceDemon;
import com.zenyte.game.content.chambersofxeric.npc.JewelledCrab;
import com.zenyte.game.content.chambersofxeric.npc.LizardmanShaman;
import com.zenyte.game.content.chambersofxeric.npc.Tekton;
import com.zenyte.game.content.chambersofxeric.storageunit.PrivateStorage;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.content.event.christmas2019.AChristmasWarble;
import com.zenyte.game.content.event.christmas2019.ChristmasConstants;
import com.zenyte.game.content.event.easter2020.EasterConstants;
import com.zenyte.game.content.event.easter2020.SplittingHeirs;
import com.zenyte.game.content.event.easter2020.Stage;
import com.zenyte.game.content.event.halloween2019.HalloweenUtils;
import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.PetInsurance;
import com.zenyte.game.content.follower.PetWrapper;
import com.zenyte.utils.FieldModifiersHelper;
import com.zenyte.game.content.godwars.npcs.GeneralGraardor;
import com.zenyte.game.content.grandexchange.GrandExchange;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.AlchemicalHydra;
import com.zenyte.game.content.magicstorageunit.MagicStorageUnit;
import com.zenyte.game.content.minigame.barrows.Barrows;
import com.zenyte.game.content.minigame.barrows.BarrowsWightNPC;
import com.zenyte.game.content.minigame.blastfurnace.BlastFurnace;
import com.zenyte.game.content.minigame.castlewars.CastleWars;
import com.zenyte.game.content.minigame.duelarena.Duel;
import com.zenyte.game.content.minigame.inferno.instance.Inferno;
import com.zenyte.game.content.minigame.inferno.npc.impl.JalTokJad;
import com.zenyte.game.content.minigame.inferno.npc.impl.zuk.TzKalZuk;
import com.zenyte.game.content.multicannon.DwarfMulticannon;
import com.zenyte.game.content.preset.PresetManager;
import com.zenyte.game.content.sailing.CharterLocation;
import com.zenyte.game.content.skills.construction.Construction;
import com.zenyte.game.content.skills.construction.RoomReference;
import com.zenyte.game.content.skills.farming.Farming;
import com.zenyte.game.content.skills.farming.seedvault.SeedVault;
import com.zenyte.game.content.skills.hunter.Hunter;
import com.zenyte.game.content.skills.magic.spells.lunar.SpellbookSwap;
import com.zenyte.game.content.skills.magic.spells.teleports.ForceTeleport;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.skills.prayer.PrayerManager;
import com.zenyte.game.content.skills.slayer.Slayer;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.treasuretrails.clues.LightBox;
import com.zenyte.game.content.treasuretrails.clues.PuzzleBox;
import com.zenyte.game.content.treasuretrails.stash.Stash;
import com.zenyte.game.content.wheeloffortune.WheelOfFortune;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.item.containers.GemBag;
import com.zenyte.game.item.containers.GnomishFirelighter;
import com.zenyte.game.item.containers.HerbSack;
import com.zenyte.game.item.degradableitems.ChargesManager;
import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.PacketDispatcher;
import com.zenyte.game.packet.Session;
import com.zenyte.game.packet.out.*;
import com.zenyte.game.polls.PollManager;
import com.zenyte.game.shop.Shop;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.InterfaceHandler;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.SceneSynchronization;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.TriviaBroadcasts;
import com.zenyte.game.world.entity.*;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.impl.GiantMole;
import com.zenyte.game.world.entity.npc.impl.wilderness.ChaosElemental;
import com.zenyte.game.world.entity.npc.impl.wilderness.Scorpia;
import com.zenyte.game.world.entity.pathfinding.Flags;
import com.zenyte.game.world.entity.pathfinding.events.RouteEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.collectionlog.CollectionLog;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerWrapper;
import com.zenyte.game.world.entity.player.container.impl.*;
import com.zenyte.game.world.entity.player.container.impl.bank.Bank;
import com.zenyte.game.world.entity.player.container.impl.death.DeathMechanics;
import com.zenyte.game.world.entity.player.container.impl.equipment.Equipment;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentUtils;
import com.zenyte.game.world.entity.player.controller.ControllerManager;
import com.zenyte.game.world.entity.player.cutscene.CutsceneManager;
import com.zenyte.game.world.entity.player.dailychallenge.DailyChallengeManager;
import com.zenyte.game.world.entity.player.dialogue.DialogueManager;
import com.zenyte.game.world.entity.player.login.Authenticator;
import com.zenyte.game.world.entity.player.loyalty.LoyaltyManager;
import com.zenyte.game.world.entity.player.perk.PerkManager;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import com.zenyte.game.world.entity.player.teleportsystem.TeleportManager;
import com.zenyte.game.world.entity.player.update.NPCInfo;
import com.zenyte.game.world.entity.player.update.PlayerInfo;
import com.zenyte.game.world.entity.player.var.EventType;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.game.world.entity.player.variables.PlayerVariables;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.game.world.region.*;
import com.zenyte.game.world.region.area.TutorialIslandArea;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import com.zenyte.plugins.MethodicPluginHandler;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.dialogue.CountDialogue;
import com.zenyte.plugins.dialogue.ItemDialogue;
import com.zenyte.plugins.dialogue.NameDialogue;
import com.zenyte.plugins.dialogue.StringDialogue;
import com.zenyte.plugins.events.LoginEvent;
import com.zenyte.plugins.events.LogoutEvent;
import com.zenyte.plugins.events.PlayerResetEvent;
import com.zenyte.plugins.interfaces.KeybindingInterface;
import com.zenyte.plugins.object.WellOfGoodwill;
import com.zenyte.processor.Listener.ListenerType;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mgi.types.config.AnimationDefinitions;
import mgi.types.config.TransmogrifiableType;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.zenyte.game.world.entity.player.Emote.GIVE_THANKS_VARP;

/**
 * @author Kris | 29. dets 2017 : 3:52.50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@SuppressWarnings("FieldMayBeFinal")
@Slf4j
public class Player extends Entity {

    public static final int SCENE_DIAMETER = 104;
    public static final int SCENE_RADIUS = SCENE_DIAMETER >> 1;
    public static final int SMALL_VIEWPORT_RADIUS = 15;
    public static final int LARGE_VIEWPORT_RADIUS = 127;

    public static final Animation DEATH_ANIMATION = new Animation(836);
    private static final ForceTalk VENGEANCE = new ForceTalk("Taste vengeance!");
    private static final HitType[] PROCESSED_HIT_TYPES = new HitType[]{HitType.MELEE, HitType.RANGED, HitType.MAGIC,
            HitType.DEFAULT};
    private static final Graphics ELYSIAN_EFFECT_GFX = new Graphics(321);
    private static final Graphics BULWARK_GFX = new Graphics(1336);
    private static final Animation BULWARK_ANIM = new Animation(7512);
    private static final Animation PLAIN_DEFENCE_ANIM = new Animation(424);
    private static String[] deathMessages = new String[]{
            "You have defeated %s.",
            "You were clearly a better fighter than %s.",
            "%s was no match for you.",
            "With a crushing blow you finish %s.",
            "%s didn't stand a chance against you.",
            "Can anyone defeat you? Certainly not %s.",
            "%s falls before your might.",
            "A humiliating defeat for %s.",
            "What was %s thinking challenging you...",
            "What an embarrassing performance by %s.",
            "RIP %s.",
            "%s probably logged out after that beating.",
            "Such a shame that %s can't play this game.",
            "How not to do it right: Written by %s.",
            "A certain, crouching-over-face animation would be suitable for %s right now.",
            "%s got rekt.",
            "%s was made to sit down.",
            "The struggle for %s is real.",
            "MUM! GET THE CAMERA, I JUST KILLED %s!",
            "%s will probably tell you %gender% wanted a free teleport after that performance.",//he/she
            "%s should take lessons from you. You're clearly too good for %gender%.",//him/her
    };
    @Getter
    private AchievementDiaries achievementDiaries = new AchievementDiaries(this);
    @Getter
    private transient CutsceneManager cutsceneManager = new CutsceneManager(this);
    @Getter
    private transient PuzzleBox puzzleBox = new PuzzleBox(this);
    @Getter
    private transient LightBox lightBox = new LightBox(this);
    @Getter
    private transient ChargesManager chargesManager = new ChargesManager(this);
    @Getter
    private transient PollManager pollManager = new PollManager(this);
    @Getter
    private transient AreaManager areaManager = new AreaManager(this);
    @Getter
    private GodBooks godBooks = new GodBooks();
    @Expose
    @Getter
    private BossTimer bossTimer = new BossTimer(this);
    @Getter
    private CollectionLog collectionLog = new CollectionLog(this);
    @Getter
    private transient DialogueManager dialogueManager = new DialogueManager(this);
    @Expose
    @Getter
    private Map<String, Object> attributes = new ConcurrentHashMap<>();
    @Expose
    @Getter
    private ControllerManager controllerManager = new ControllerManager(this);
    @Expose
    @Getter
    private MusicHandler music = new MusicHandler(this);
    @Getter
    private PresetManager presetManager = new PresetManager(this);
    @Expose
    @Getter
    private EmotesHandler emotesHandler = new EmotesHandler(this);
    @Expose
    @Getter
    private InterfaceHandler interfaceHandler = new InterfaceHandler(this);
    @Getter
    private BountyHunter bountyHunter = new BountyHunter(this);
    @Getter
    private List<Integer> trackedHolidayItems = new IntArrayList();
    @Expose
    @Getter
    private Appearance appearance = new Appearance(this);
    @Getter
    private transient Set<Container> pendingContainers = new LinkedHashSet<Container>();
    @Expose
    @Getter
    private SocialManager socialManager = new SocialManager(this);
    @Expose
    @Getter
    private CombatDefinitions combatDefinitions = new CombatDefinitions(this);
    @Getter private MagicStorageUnit magicStorageUnit = new MagicStorageUnit();
    @Expose
    @Getter
    private DwarfMulticannon dwarfMulticannon = new DwarfMulticannon(this);
    @Expose
    @Getter
    private Equipment equipment = new Equipment(this);
    @Expose
    @Getter
    private Inventory inventory = new Inventory(this);
    @Getter
    private transient DeathMechanics deathMechanics = new DeathMechanics(this);
    @Expose
    @Getter
    private NotificationSettings notificationSettings = new NotificationSettings(this);
    @Expose
    @Getter
    private PriceChecker priceChecker = new PriceChecker(this);
    @Expose
    @Getter
    private transient Trade trade = new Trade(this);
    @Getter
    private SeedVault seedVault = new SeedVault(this);
    @Expose
    @Getter
    private RunePouch runePouch = new RunePouch(this);
    @Getter
    private RunePouch secondaryRunePouch = new RunePouch(this);
    @Getter
    private SeedBox seedBox = new SeedBox(this);
    @Getter
    private LootingBag lootingBag = new LootingBag(this);
    @Getter
    private HerbSack herbSack = new HerbSack(this);
    @Getter
    private GemBag gemBag = new GemBag(this);
    @Getter
    private GnomishFirelighter gnomishFirelighter = new GnomishFirelighter(this);
    @Expose
    @Getter
    private Skills skills = new Skills(this);
    @Expose
    @Getter
    private Settings settings = new Settings(this);
    @Expose
    @Getter
    private Construction construction = new Construction(this);
    @Expose
    @Getter
    private PrayerManager prayerManager = new PrayerManager(this);
    @Expose
    @Getter
    private TeleportManager teleportManager = new TeleportManager(this);
    @Getter
    private VarManager varManager = new VarManager(this);
    @Getter
    private transient PlayerInfo playerViewport = new PlayerInfo(this);
    @Getter
    private transient NPCInfo npcViewport = new NPCInfo(this);
    @Expose
    @Getter
    private PlayerVariables variables = new PlayerVariables(this);
    @Getter
    private transient Set<Player> botObservers = new ObjectOpenHashSet<>();
    @Getter
    private transient WorldMap worldMap = new WorldMap(this);
    @Expose
    @Getter
    private GrandExchange grandExchange = new GrandExchange(this);
    @Getter
    private transient Bonuses bonuses = new Bonuses(this);
    @Getter
    private transient String[] options = new String[9];
    @Getter
    private transient Object2LongOpenHashMap<String> attackedByPlayers = new Object2LongOpenHashMap<>();
    @Getter
    private PerkManager perkManager = new PerkManager(this);
    @Getter
    private transient ChatMessage chatMessage = new ChatMessage();
    @Getter
    private transient ChatMessage clanMessage = new ChatMessage();
    @Getter
    private Barrows barrows = new Barrows(this);
    @Getter
    private ItemRetrievalService retrievalService = new ItemRetrievalService(this);
    @Getter
    @Setter
    public transient Runnable closeInterfacesEvent;
    @Getter
    @Setter
    private transient boolean needRegionUpdate;
    @Getter
    @Setter
    private transient boolean running;
    private transient List<ProjPacket> tempList = new ArrayList<>();
    @Getter
    private transient ActionManager actionManager = new ActionManager(this);
    @Expose
    @Getter
    @Setter
    private PrivateStorage privateStorage = new PrivateStorage(this);
    @Expose
    @Getter
    @Setter
    private PlayerInformation playerInformation;
    @Getter
    @Setter
    private transient Entity lastTarget;
    @Getter
    private transient DelayedActionManager delayedActionManager = new DelayedActionManager(this);
    @Expose
    @Getter
    private Farming farming = new Farming(this);
    @Getter
    private transient PacketDispatcher packetDispatcher = new PacketDispatcher(this);
    @Getter
    @Setter
    private PetInsurance petInsurance = new PetInsurance(this);
    @Expose
    @Getter
    private transient Follower follower;
    @Getter
    @Setter
    private int petId;
    @Getter
    private transient boolean canPvp;
    @Expose
    @Getter
    @Setter
    private Stash stash = new Stash(this);
    @Getter
    @Setter
    private transient boolean maximumTolerance;
    @Setter
    private transient Duel duel;
    @Expose
    @Getter
    @Setter
    private Bank bank = new Bank(this);
    @Getter
    @Setter
    private transient boolean forceReloadMap;
    @Getter
    private transient int viewDistance = 15;
    @Getter
    @Setter
    private Slayer slayer = new Slayer(this);

    @Getter
    @Setter
    private Hunter hunter = new Hunter(this);

    @Getter
    private BlastFurnace blastFurnace = new BlastFurnace(this);
    @Expose
    @Getter
    private RespawnPoint respawnPoint = RespawnPoint.EDGEVILLE;
    @Getter
    private DailyChallengeManager dailyChallengeManager = new DailyChallengeManager(this);

    @Getter @Setter private transient Optional<GrotesqueGuardiansInstance> grotesqueGuardiansInstance;

    public void setRespawnPoint(final RespawnPoint point) {
        this.respawnPoint = point;
    }

    @Getter
    @Setter
    private transient int pid;
    @Getter
    @Setter
    private transient boolean loadingRegion;
    @Getter
    @Setter
    private transient long movementLock, diceDelay;
    @Getter
    private transient String[] nametags;
    @Expose
    @Getter
    private GameMode gameMode = GameMode.REGULAR;
    @Getter
    private MemberRank memberRank = MemberRank.NONE;
    @Getter
    private ExperienceMode experienceMode = ExperienceMode.TIMES_100;
    @Expose
    @Getter
    private Privilege privilege = Privilege.PLAYER;
    @Getter
    @Setter
    private transient long lastDisconnectionTime;
    @Getter
    @Setter
    private transient boolean loggedOut;
    @Getter
    private WheelOfFortune wheelOfFortune = new WheelOfFortune(this);
    @Getter
    @Setter
    private transient int logoutCount;
    @Getter
    @Setter
    private transient boolean updatingNPCOptions = true;
    @Getter
    @Setter
    private transient boolean updateNPCOptions;
    @Getter
    private transient IntLinkedOpenHashSet pendingVars = new IntLinkedOpenHashSet(100);
    @Getter
    @Setter
    private transient Runnable pathfindingEvent;
    @Getter
    @Setter
    private transient RouteEvent<Player, EntityStrategy> combatEvent;
    private transient int hashcode;
    private transient Rectangle sceneRectangle;
    @Getter
    private transient Int2ObjectOpenHashMap<List<GamePacketEncoder>> zoneFollowPackets = new Int2ObjectOpenHashMap<>();
    @Getter
    @Setter
    private transient boolean heatmap;
    @Getter
    private transient IntOpenHashSet chunksInScope = new IntOpenHashSet(SceneSynchronization.CHUNK_SYNCHRONIZATION_MAX_COUNT);
    @Getter
    @Setter
    private transient int heatmapRenderDistance = SMALL_VIEWPORT_RADIUS;
    @Getter
    @Setter
    private transient boolean hidden;
    @Getter
    @Setter
    private transient int damageSound = -1;
    @Getter
    private IntArrayList paydirt = new IntArrayList();
    @Getter
    private LoyaltyManager loyaltyManager = new LoyaltyManager(this);
    @Getter
    @Setter
    private transient long lastReceivedPacket = System.currentTimeMillis();
    @Getter
    private Authenticator authenticator;

    @Getter
    private transient ArrayDeque<Notification> notifications = new ArrayDeque<>();
    @Getter
    private transient Notification currentNotification;

    private transient List<Runnable> postPacketProcessingRunnables = new LinkedList<>();

    public void addPostProcessRunnable(@NotNull final Runnable runnable) {
        postPacketProcessingRunnables.add(runnable);
    }
    
    public void addMovementLock(final MovementLock lock) {
        movementLocks.add(lock);
    }

    public void removeAllMovementLocks() {
        movementLocks.clear();
    }

    public boolean isFullMovementLocked() {
        if (movementLocks.isEmpty()) {
            return false;
        }
        for (MovementLock next : movementLocks) {
            if (!next.isFullLock()) {
                continue;
            }
            if (!next.canWalk(this, false)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isMovementLocked(final boolean executeAttachments) {
        if (movementLocks.isEmpty()) {
            return false;
        }
        val iterator = movementLocks.iterator();
        while(iterator.hasNext()) {
            val next = iterator.next();
            if (!next.canWalk(this, executeAttachments)) {
                return true;
            }
            iterator.remove();
        }
        return false;
    }

    @Getter
    private String lastIP;
    @Getter
    private String lastMAC;

    public Player(final PlayerInformation information, final Authenticator authenticator) {
        this.authenticator = authenticator == null ? new Authenticator() : authenticator;
        forceLocation(new Location(GameConstants.REGISTRATION_LOCATION));
        setLastLocation(new Location(getLocation()));
        playerInformation = information;
        getUpdateFlags().flag(UpdateFlag.APPEARANCE);
        getUpdateFlags().flag(UpdateFlag.TEMPORARY_MOVEMENT_TYPE);
        setTeleported(true);
        this.hashcode = information != null ? information.getUsername().hashCode() : -1;
    }

    public boolean canHit(final Player other) {
        if (!other.isCanPvp() || !isCanPvp()) {
            return false;
        }
        if(CastleWars.isUserPlaying(this) && CastleWars.isUserPlaying(other)) {
            if(CastleWars.getTeam(this).equals(CastleWars.getTeam(other))) {
                return false;
            }
        }
        val level = WildernessArea.getWildernessLevel(getLocation());
        if (level.isPresent()) {
            return Math.abs(getSkills().getCombatLevel() - other.getSkills().getCombatLevel()) <= level.getAsInt();
        }
        return true;
    }

    public void resetViewDistance() {
        this.viewDistance = SMALL_VIEWPORT_RADIUS;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance < 1 ? 1 : viewDistance > 104 ? 104 : viewDistance;
    }

    public final Session getSession() {
        return getPlayerInformation().getSession();
    }

    public Area getArea() {
        return areaManager.getArea();
    }

    public boolean isOnMobile() {
        return playerInformation.isOnMobile();
    }

    public boolean updateNPCOptions(final NPC npc) {
        val definitions = NPCDefinitions.get(getTransmogrifiedId(npc.getDefinitions(), npc.getId()));
        if (definitions == null) return false;
        return definitions.getFilterFlag() > 0;
    }

    public void teleport(@NotNull final Location location) {
        new ForceTeleport(location).teleport(this);
    }

    /**
     * Checks whether the player is tolerant to the entities around.
     *
     * @return whether it's tolerant or not.
     */
    public final boolean isTolerant(@NotNull final Location tile) {
        return this.variables.getToleranceTimer() > TimeUnit.MINUTES.toTicks(10) && inTolerantPosition(tile);
    }

    /**
     * Sets the force movement for the player for the teleportation-type. This means that the player will be teleported to the end destination right as the force movement starts, and the
     * force-movement rewinds the player to the start location client-sided and rolls from the start. To the user, this is seamless and not visible.
     *
     * @param tile                  the tile to which the player is force-moved.
     * @param direction             the direction value which the player will be facing throughout the force movement; If absent, default face direction of where the player moves to is calculated.
     * @param delayInTicks          the delay in ticks until the player starts the force movement action - a value of 0 would mean instant start.
     * @param speedInTicks          the delay in ticks for how long the player will be moved through the force movement. Minimum value is 1!
     * @param startConsumer         the optional consumer that is executed instantly as the method is executed, this will not wait for the force movement to begin.
     * @param movementStartConsumer the optional consumer that is executed as soon as the force movement itself begins.
     * @param endConsumer           the consumer that is executed when the player finishes the force movement.
     */
    public void setTeleportForceMovement(@NotNull final Location tile, @NotNull final OptionalInt direction, final int delayInTicks, final int speedInTicks,
                                         @NotNull final Optional<Consumer<Location>> startConsumer, @NotNull final Optional<Consumer<Location>> movementStartConsumer,
                                         @NotNull final Optional<Consumer<Location>> endConsumer) {
        if (speedInTicks < 1) {
            throw new IllegalStateException("Speed must always be positive.");
        }
        startConsumer.ifPresent(consumer -> consumer.accept(tile));
        WorldTasksManager.scheduleOrExecute(() -> {
            movementStartConsumer.ifPresent(consumer -> consumer.accept(tile));
            val currentTile = new Location(getLocation());
            setForceMovement(new ForceMovement(new Location(getLocation()), 0, tile, speedInTicks * 30,
                    direction.orElse(Utils.getFaceDirection(tile.getX() - currentTile.getX(), tile.getY() - currentTile.getY()))));
            setLocation(tile);
            endConsumer.ifPresent(consumer -> WorldTasksManager.scheduleOrExecute(() -> consumer.accept(tile), speedInTicks - 1));
        }, delayInTicks - 1);
    }

    public void autoForceMovement(final Location tile, final int speed) {
        val currentTile = new Location(getLocation());
        setLocation(tile);
        val fm = new ForceMovement(currentTile, 1, tile, speed, Utils.getFaceDirection(tile.getX() - currentTile.getX(), tile.getY() - currentTile.getY()));
        setForceMovement(fm);
    }

    public void autoForceMovement(final Location tile, final int delay, final int totalDuration, final int direction) {
        /*if ((totalDuration) % 30 != 0) {
            throw new RuntimeException("Unable to synchronize players location with forcemovement due to" + " delay and duration not being in synchronization with game ticks.");
        }
        if (delay == totalDuration) {
            throw new RuntimeException("Delay cannot be equal to speed.");
        }*/
        val currentTile = new Location(getLocation());
        val fm = new ForceMovement(currentTile, delay, tile, totalDuration, direction);
        setForceMovement(fm);
        WorldTasksManager.schedule(() -> setLocation(tile), (int) Math.ceil(totalDuration / 30F) - 1);
    }

    public void autoForceMovement(final Location tile, final int delay, final int totalDuration) {
        val currentTile = new Location(getLocation());
        val direction = Utils.getFaceDirection(tile.getX() - currentTile.getX(), tile.getY() - currentTile.getY());
        autoForceMovement(tile, delay, totalDuration, direction);
    }

    public boolean eligibleForShiftTeleportation() {
        return privilege.eligibleTo(Privilege.SPAWN_ADMINISTRATOR) || (privilege.eligibleTo(Privilege.ADMINISTRATOR) && !(getArea() instanceof Inferno));
    }


    public void setNametag(final int index, final String string) {
        if (index < 0 || index >= 3) {
            return;
        }

        if (nametags == null) {
            nametags = new String[3];
        }
        nametags[index] = string;
        updateFlags.flag(UpdateFlag.NAMETAG);
    }

    public void resetNametags() {
        nametags = null;
        updateFlags.flag(UpdateFlag.NAMETAG);
    }

    /**
     * Gets the player's current display name.
     *
     * @return current display name.
     */
    public String getName() {
        return playerInformation.getDisplayname();
    }

    public boolean inArea(@NotNull final Class<? extends Area> clazz) {
        return inArea(GlobalAreaManager.getArea(clazz).name());
    }

    public boolean inArea(final String areaName) {
        val area = areaManager.getArea();
        if (area == null) {
            return false;
        }
        Area superArea = area;
        val name = areaName.toLowerCase();
        val location = getLocation();
        while (true) {
            if (superArea.inside(location) && name.equals(superArea.name().toLowerCase())) {
                return true;
            }
            superArea = superArea.getSuperArea();
            if (superArea == null) {
                return false;
            }
        }
    }

    @Override
    public boolean isFrozen() {
        return super.isFrozen() && getTemporaryAttributes().get("ignoreWalkingRestrictions") == null;
    }

    public boolean isUnderCombat() {
        return (getAttackedByDelay() + 4200) > Utils.currentTimeMillis();
    }

    public boolean isUnderCombat(final int ticksAfterLastAttack) {
        return (getAttackedByDelay() + (ticksAfterLastAttack * 600)) > Utils.currentTimeMillis();
    }

    public void setDefaultSettings() {
        settings.setSettingNoRefresh(Setting.DAREEYAK_TELEPORT_WARNING, 1);
        settings.setSettingNoRefresh(Setting.ANNAKARL_TELEPORT_WARNING, 1);
        settings.setSettingNoRefresh(Setting.CARRALLANGAR_TELEPORT_WARNING, 1);
        settings.setSettingNoRefresh(Setting.GHORROCK_TELEPORT_WARNING, 1);
        settings.setSettingNoRefresh(Setting.BOUNTY_TARGET_TELEPORT_WARNING, 1);
        settings.setSettingNoRefresh(Setting.SCREEN_BRIGHTNESS, 3);
        settings.setSettingNoRefresh(Setting.AUTO_MUSIC, 1);
        settings.setSettingNoRefresh(Setting.SPELL_FILTERING, 1);
        setQuestPoints(250);
        attributes.put("LEVEL_99_DIALOGUES", 75);
        attributes.put("ALCHEMY_WARNING_VALUE", 30000);
        attributes.put("RING_OF_RECOIL", 40);
        attributes.put("RING_OF_FORGING", 140);
        attributes.put("checking combat in slayer", 1);
        attributes.put("recoil effect", 1);
        attributes.put("looting_bag_amount_prompt", 1);
        attributes.put("first_99_skill", -1);
        attributes.put("quest_points", 250);//To unlock slayer rewards.
        for (val setting : GameSetting.ALL) {
            if (setting == GameSetting.YELL_FILTER || setting == GameSetting.ALWAYS_SHOW_LATEST_UPDATE || setting == GameSetting.EXAMINE_NPCS_DROP_VIEWER) {
                continue;
            }
            attributes.put(setting.toString(), 1);
        }
        KeybindingInterface.setDefaultKeybinds(this);
    }

    @Override
    protected void processHit(final Hit hit) {
        if (hit.getScheduleTime() < protectionDelay) {
            return;
        }
        if (VerSinhazaArea.getParty(this) != null) {
            if (VerSinhazaArea.getParty(this).getRaid() != null) {
                if (!VerSinhazaArea.getParty(this).getAlivePlayers().contains(this)
                        && areaManager.getArea() instanceof TheatreArea
                        && VerSinhazaArea.getParty(this).getRaid().getActiveRoom().isCompleted()) {
                    return;
                }
            }
        }
        if (isImmune(hit.getHitType())) {
            hit.setDamage(0);
        }
        val action = actionManager.getAction();
        if (action != null && action.interruptedByCombat()) {
            actionManager.forceStop();
        }
        if (hit.getDamage() > Short.MAX_VALUE) {
            hit.setDamage(Short.MAX_VALUE);
        }
        getUpdateFlags().flag(UpdateFlag.HIT);
        nextHits.add(hit);
        if (hitBars.isEmpty()) {
            hitBars.add(hitBar);
        }
        val type = hit.getHitType();
        if (type == HitType.DISEASED || type == HitType.DISEASED_LARGE) {
            return;
        }
        if (type == HitType.HEALED) {
            heal(hit.getDamage());
        } else {
            removeHitpoints(hit);
        }
        val source = hit.getSource();
        if (source != null) {
            if (source instanceof LizardmanShaman) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_shaman", true);
                }
            }
            if (source instanceof BarrowsWightNPC) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_wight", true);
                }
            }
            if (source instanceof GiantMole) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_giant_mole", true);
                }
            }
            if (source instanceof ChaosElemental) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_chaos_elemental", true);
                }
            }
            if (source instanceof Scorpia) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_scorpia", true);
                }
            }
            if (source instanceof GeneralGraardor) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_bandos", true);
                }
            }
            if (source instanceof IceDemon) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_ice_demon", true);
                }
            }
            if (source instanceof LizardmanShaman && this.getRaid().isPresent()) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_shaman_in_raid", true);
                }
            }
            if (source instanceof Skotizo || source instanceof DarkAnkou || source instanceof ReanimatedDemon) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_skotizo", true);
                }
            }
            if (source instanceof AlchemicalHydra ) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_hydra", true);
                }
            }
            if (source instanceof Tekton) {
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_from_tekton", true);
                }
            }
            if (source instanceof NPC) {
                val npc = (NPC) source;
                if ((npc.getId() == NpcId.KREEARRA
                        || npc.getId() == NpcId.KREEARRA_6492
                        || npc.getId() == NpcId.FLIGHT_KILISA)
                        && hit.getHitType() == HitType.MELEE) {
                    if (hit.getDamage() > 0) {
                        this.putBooleanAttribute("has_taken_melee_damage_in_kree", true);
                    }
                }
            }
            if (source instanceof Cerberus) {
                val npc = (NPC) source;
                if (hit.getHitType() == HitType.MELEE) {
                    if (hit.getDamage() > 0) {
                        this.putBooleanAttribute("has_taken_melee_damage_in_cerb", true);
                    }
                }
            }
            if (source instanceof ZulrahNPC || source instanceof SnakelingNPC) {
                if (source instanceof ZulrahNPC) {
                    val zulrah = (ZulrahNPC) source;
                    val phaseId = zulrah.getId();
                    if (phaseId == NpcId.ZULRAH_2044) {
                        return;
                    }
                }
                if (hit.getDamage() > 0) {
                    this.putBooleanAttribute("has_taken_damage_during_zulrah", true);
                }
            }
            if (source instanceof NPC) {
                val npc = (NPC) source;
                if (npc.getId() == NpcId.COMMANDER_ZILYANA
                        || npc.getId() == NpcId.COMMANDER_ZILYANA_6493
                        || npc.getId() == NpcId.STARLIGHT
                        || npc.getId() == NpcId.BREE
                        || npc.getId() == NpcId.GROWLER) {
                    if (hit.getDamage() > 0) {
                        this.putBooleanAttribute("has_taken_damage_during_zilyana", true);
                    }
                }
            }
            if (source instanceof NPC) {
                val npc = (NPC) source;
                if (npc.getId() == NpcId.SERGEANT_GRIMSPIKE
                        || npc.getId() == NpcId.SERGEANT_STEELWILL
                        || npc.getId() == NpcId.SERGEANT_STRONGSTACK) {
                    if (hit.getDamage() > 0) {
                        this.putBooleanAttribute("has_taken_damage_during_bandos_from_minions", true);
                    }
                }
            }
            if (getArea() instanceof Inferno) {
                if (source instanceof JalTokJad) {
                    if (hit.getDamage() > 0) {
                        this.putBooleanAttribute("has_taken_damage_from_jad_during_inferno", true);
                    }
                }
                if (source instanceof TzKalZuk) {
                    this.putBooleanAttribute("has_taken_hit_from_zuk_during_inferno", true);
                }
            }
            if (source instanceof NPC) {
                val npc = (NPC) source;
                if (npc.getId() == NpcId.KRIL_TSUTSAROTH
                        || npc.getId() == NpcId.KRIL_TSUTSAROTH_6495
                        || npc.getId() == NpcId.BALFRUG_KREEYATH
                        || npc.getId() == NpcId.TSTANON_KARLAK
                        || npc.getId() == NpcId.ZAKLN_GRITCH) {
                    if (hit.getDamage() > 0) {
                        this.putBooleanAttribute("has_taken_damage_during_kril", true);
                    }
                }
            }
        }
    }

    public void sendAdventurersEntry(final AdventurersLogIcon icon, final String message) {
        sendAdventurersEntry(icon.getLink(), message, false);
    }

    public void sendAdventurersEntry(final String icon, final String message, final boolean pvp) {
        if (!Constants.WORLD_PROFILE.getApi().isEnabled()
                || Constants.WORLD_PROFILE.isBeta()
                || Constants.WORLD_PROFILE.isPrivate()
                || Constants.WORLD_PROFILE.isDevelopment()) {
            return;
        }
        CoresManager.getServiceProvider().submit(() -> new ApiAdventurersLogRequest(Player.this, icon, message).execute());
    }

    public void refreshDirection() {
        if (faceEntity >= 0) {
            final Entity target = faceEntity >= 32768 ? World.getPlayers().get(faceEntity - 32768) : World.getNPCs().get(faceEntity);
            if (target != null) {
                direction = Utils.getFaceDirection(target.getLocation().getCoordFaceX(target.getSize()) - getX(), target.getLocation().getCoordFaceY(target.getSize()) - getY());
            }
        }
    }

    @Getter private transient int lastWalkX, lastWalkY;

    @Override
    public void processMovement() {
        if (!inArea("Wilderness")) {
            if (getAttributes().containsKey("joined_wildy_at_chaosfanatic_kc")) {
                getAttributes().remove("joined_wildy_at_chaosfanatic_kc");
            }
        } else {
            if (!getAttributes().containsKey("joined_wildy_at_chaosfanatic_kc")) {
                getAttributes().put("joined_wildy_at_chaosfanatic_kc", getNotificationSettings().getKillcount("chaos fanatic"));
            }
        }
        refreshDirection();
        walkDirection = runDirection = -1;
        val area = getArea();
        if (nextLocation != null) {
            val nextArea = GlobalAreaManager.getArea(nextLocation);
            if (nextArea instanceof TeleportMovementPlugin) {
                val plugin = (TeleportMovementPlugin) nextArea;
                if (!plugin.canTeleport(this, nextLocation)) {
                    teleported = false;
                    nextLocation = null;
                    return;
                }
            }
            if (lastLocation == null) {
                lastLocation = new Location(location);
            } else {
                lastLocation.setLocation(location);
            }
            unclip();
            LocationMap.remove(this);
            if (area instanceof TeleportMovementPlugin) {
                ((TeleportMovementPlugin) area).processMovement(this, nextLocation);
            }
            forceLocation(nextLocation);
            clip();
            LocationMap.add(this);
            nextLocation = null;
            updateFlags.flag(UpdateFlag.TEMPORARY_MOVEMENT_TYPE);
            teleported = true;
            refreshToleranceRectangle();
            World.updateEntityChunk(this, false);
            controllerManager.teleport(location);
            farming.refresh();
            if (interfaceHandler.isVisible(GameInterface.WORLD_MAP.getId())) {
                worldMap.updateLocation();
            }
            if (needMapUpdate()) {
                setNeedRegionUpdate(true);
                setLoadingRegion(true);
            }
            resetWalkSteps();
            return;
        }
        teleported = false;
        if (walkSteps.isEmpty()) {
            return;
        }
        if (isDead()) {
            return;
        }
        if (lastLocation == null) {
            lastLocation = new Location(location);
        } else {
            lastLocation.setLocation(location);
        }
        lastWalkX = 0;
        lastWalkY = 0;

        if (isRun() && !isSilentRun()) {
            int runStep = walkSteps.size() > 2 ? walkSteps.nthPeek(2) : 0;
            if (runStep != 0 && WalkStep.getDirection(runStep) != -1) {

                double energyLost = ((Math.min(inventory.getWeight() + equipment.getWeight(), 64) / 100) + 0.64);
                if (Constants.PRIVATE_BETA) {
                    //Run energy depletion lowered by 5x during private beta.
                    energyLost /= 5F;
                }
                if (variables.getTime(TickVariable.STAMINA_ENHANCEMENT) > 0) {
                    energyLost *= 0.3;
                }
                if (variables.getTime(TickVariable.HAMSTRUNG) > 0) {
                    energyLost *= 6;
                }
                if (!inArea("Wilderness")) {
                    if (memberRank.eligibleTo(MemberRank.ONYX_MEMBER)) {
                        energyLost *= 0.9F;
                    } else if (memberRank.eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
                        energyLost *= 0.925F;
                    } else if (memberRank.eligibleTo(MemberRank.RUBY_MEMBER)) {
                        energyLost *= 0.950F;
                    } else if (memberRank.eligibleTo(MemberRank.SAPPHIRE_MEMBER)) {
                        energyLost *= 0.975F;
                    }
                }
                if (this.getEquipment().getId(EquipmentSlot.RING) == 32236) {
                    val charges = this.getEquipment().getItem(EquipmentSlot.RING).getCharges();
                    if (charges > 500) {
                        energyLost *= 0.85F;
                    }
                }
                if (variables.getRunEnergy() >= 0) {
                    variables.forceRunEnergy(Math.max(0, variables.getRunEnergy() - energyLost));
                    if (variables.getRunEnergy() == 0) {
                        setRun(false);
                        varManager.sendVar(173, 0);
                    }
                }
            }
        }

        int steps = Math.min(silentRun ? 1 : run ? 2 : 1, walkSteps.size());
        int stepCount;
        for (stepCount = 0; stepCount < steps; stepCount++) {
            final int nextStep = getNextWalkStep();
            if (nextStep == 0) {
                break;
            }
            final int dir = WalkStep.getDirection(nextStep);
            if ((WalkStep.check(nextStep) && !World.checkWalkStep(getPlane(), getX(), getY(), dir, getSize(), false, true))) {
                resetWalkSteps();
                break;
            }
            final int x = Utils.DIRECTION_DELTA_X[dir];
            final int y = Utils.DIRECTION_DELTA_Y[dir];

            if (area instanceof FullMovementPlugin) {
                if (!((FullMovementPlugin) area).processMovement(this, getX() + x, getY() + y)) {
                    break;
                }
            }
            if (stepCount == 0) {
                walkDirection = dir;
                lastWalkX = -x;
                lastWalkY = -y;
            } else {
                runDirection = dir;
            }
            controllerManager.move((getWalkSteps().size() > 0 && steps == 2) ? stepCount == 1 : stepCount == 0, getX() + x, getY() + y);
            unclip();
            LocationMap.remove(this);
            location.moveLocation(x, y, 0);
            clip();
            LocationMap.add(this);
            if (interfaceHandler.isVisible(GameInterface.WORLD_MAP.getId())) {
                worldMap.updateLocation();
            }
        }
        if (area instanceof PartialMovementPlugin) {
            if (!(area instanceof FullMovementPlugin)) {
                ((PartialMovementPlugin) area).processMovement(this, getX(), getY());
            }
        }
        final int type = runDirection == -1 ? 1 : 2;
        if (type != lastMovementType) {
            if (stepCount == 1 && run) {
                updateFlags.flag(UpdateFlag.TEMPORARY_MOVEMENT_TYPE);
            } else {
                lastMovementType = type;
                updateFlags.flag(UpdateFlag.MOVEMENT_TYPE);
            }
        }

        if (faceEntity < 0) {
            direction = Utils.getFaceDirection(location.getX() - lastLocation.getX(), location.getY() - lastLocation.getY());
        }
        refreshToleranceRectangle();
        World.updateEntityChunk(this, false);//TODO check why double.
        farming.refresh();
        if (needMapUpdate()) {
            setNeedRegionUpdate(true);
            setLoadingRegion(true);
        }
    }
    
    @Getter
    private transient List<MovementLock> movementLocks = new LinkedList<>();

    private boolean inTolerantPosition(final Location t) {
        for (val tile : tolerancePositionQueue) {
            if (tile.withinDistance(t, 10)) {
                return true;
            }
        }
        return false;
    }

    private void refreshToleranceRectangle() {
        if (inTolerantPosition(getLocation())) {
            return;
        }
        if (tolerancePositionQueue.size() >= 2) {
            //Remove the earliest tolerance position.
            tolerancePositionQueue.poll();
        }
        //Add a new position to the tolerance queue.
        tolerancePositionQueue.add(new Location(getLocation()));
        //And every time the player's tolerance position(s) change, we reset the timer again.
        variables.setToleranceTimer(0);
    }

    public void logout(final boolean force) {
        if (!force) {
            if (!isRunning()) {
                return;
            }
            if (isLocked()) {
                this.sendMessage("You can't log out while performing an action.");
                return;
            } else if (isUnderCombat()) {
                this.sendMessage("You can't log out until 10 seconds after the end of combat.");
                return;
            }
        }

        packetDispatcher.sendLogout();
    }

    public void sendInputString(final String question, final StringDialogue dialogue) {
        packetDispatcher.sendClientScript(110, question);
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public void sendInputName(final String question, final NameDialogue dialogue) {
        packetDispatcher.sendClientScript(109, question);
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public void sendInputInt(final String question, final CountDialogue dialogue) {
        packetDispatcher.sendClientScript(108, question);
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public void sendInputItem(final String question, final ItemDialogue dialogue) {
        packetDispatcher.sendClientScript(750, question, 1, -1);
        temporaryAttributes.put("interfaceInput", dialogue);
    }

    public Construction getCurrentHouse() {
        final Object object = getTemporaryAttributes().get("VisitingHouse");
        if (!(object instanceof Construction)) {
            return null;
        }
        return (Construction) object;
    }

    @Override
    public void reset() {
        try {
            try {
                super.reset();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                for (int i = 0; i < 23; i++) {
                    skills.setLevel(i, skills.getLevelForXp(i));
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                attackedByPlayers.clear();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                PluginManager.post(new PlayerResetEvent(this));
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                toxins.reset();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                attributes.remove("vengeance");
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                variables.resetScheduled();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                prayerManager.deactivateActivePrayers();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                variables.setRunEnergy(100);
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                combatDefinitions.setSpecial(false, true);
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                setAttackedByDelay(0);
                setAttackingDelay(0);
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                resetFreeze();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public void setLunarDelay(final long delay) {
        getTemporaryAttributes().put("spellDelay", Utils.currentTimeMillis() + delay);
    }

    @Override
    public List<Entity> getPossibleTargets(final EntityType type) {
        if (!possibleTargets.isEmpty()) {
            possibleTargets.clear();
        }
        CharacterLoop.populateEntityList(possibleTargets, this.getLocation(), 15, type.getClazz(), this::isPotentialTarget);
        return possibleTargets;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        return true;
    }

    @Override
    protected boolean isPotentialTarget(final Entity entity) {
        val entityX = entity.getX();
        val entityY = entity.getY();
        val entitySize = entity.getSize();

        val x = getX();
        val y = getY();
        val size = getSize();


        return entity != this && !entity.isDead() && !entity.isMaximumTolerance() && (entity.isMultiArea() || entity.getAttackedBy() == this) && (!isProjectileClipped(entity, false) || Utils.collides(x, y, size, entityX, entityY, entitySize)) && (!(entity instanceof NPC) || ((NPC) entity).isAttackableNPC()) && (!(entity instanceof Player) || ((Player) entity).isCanPvp());
    }

    public long generateSnowflake() {
        return Utils.generateSnowflake(playerInformation.getUserIdentifier());
    }

    public final Number getNumericTemporaryAttribute(final String key) {
        final Object object = getTemporaryAttributes().get(key);
        if (!(object instanceof Number)) {
            return 0;
        }
        return (Number) object;
    }

    public final Number getNumericTemporaryAttributeOrDefault(final String key, final int defaultValue) {
        final Object object = getTemporaryAttributes().get(key);
        if (!(object instanceof Number)) {
            return defaultValue;
        }
        return (Number) object;
    }

    public final Number getNumericAttribute(final String key) {
        final Object object = attributes.get(key);
        if (object == null || !(object instanceof Number)) {
            return 0;
        }
        return (Number) object;
    }


    /**
     * Adds or subtracts a numeric attribute by specified amount.
     * @param key the key of the attribute to apply the arithmetic operation to.
     * @param amount the amount to add or subtract from current value of attribute. If no value is found then operation is applied to 0.
     * @return the new value for the numeric attribute.
     */
    public final Number incrementNumericAttribute(@NotNull final String key, final int amount) {
        final Object object = attributes.get(key);
        if (object != null && !(object instanceof Number)) {
            throw new IllegalArgumentException("Attribute with key [" + key + "] is not numeric.");
        }
        val newAmount = object == null ? amount : ((Number) object).intValue() + amount;
        attributes.put(key, newAmount);
        return newAmount;
    }


    @SuppressWarnings("unchecked")
    public final <T> T getAttributeOrDefault(final String key, final T defaultValue) {
        final Object object = attributes.get(key);
        if (object == null) {
            return defaultValue;
        }
        try {
            return (T) object;
        } catch (final Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public int getIntSetting(final Setting setting) {
        return getNumericAttribute(setting.toString()).intValue();
    }

    public void addAttribute(final String key, final Object value) {
        if (value == null || value instanceof Number && ((Number) value).longValue() == 0) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    public void addTemporaryAttribute(final String key, final Object value) {
        if (value instanceof Number && ((Number) value).longValue() == 0) {
            temporaryAttributes.remove(key);
        } else {
            temporaryAttributes.put(key, value);
        }
    }

    public int getTransmogrifiedId(@NonNull final TransmogrifiableType type, final int defaultValue) {
        val array = type.getTransmogrifiedIds();
        if (array == null) return defaultValue;
        val varbit = type.getVarbitId();
        val varp = type.getVarpId();
        val index = varbit == -1 ? varManager.getValue(varp) : varManager.getBitValue(varbit);
        if (index < 0) return defaultValue;
        if (index >= array.length) {
            return type.defaultId();
        }
        return array[index];
    }

    public int getKillcount(final NPC npc) {
        return this.notificationSettings.getKillcount(npc.getName(this));
    }

    public void toggleBooleanAttribute(final String key) {
        if (key == null) {
            return;
        }
        final int value = getNumericAttribute(key).intValue();
        if (value == 0) {
            addAttribute(key, 1);
            return;
        }
        addAttribute(key, 0);
    }

    public boolean getBooleanAttribute(final String key) {
        if (key == null) {
            return false;
        }
        final int value = getNumericAttribute(key).intValue();
        return value == 1;
    }

    public boolean getBooleanTemporaryAttribute(final String key) {
        if (key == null) {
            return false;
        }
        final int value = getNumericTemporaryAttribute(key).intValue();
        return value == 1;
    }

    public void putBooleanTemporaryAttribute(final String key, final boolean bool) {
        if (key == null) {
            return;
        }
        addTemporaryAttribute(key, bool ? 1 : 0);
    }

    public void putBooleanAttribute(final String key, final boolean bool) {
        if (key == null) {
            return;
        }
        addAttribute(key, bool ? 1 : 0);
    }

    public boolean getBooleanSetting(final Setting key) {
        if (key == null) {
            return false;
        }
        final int value = getNumericAttribute(key.toString()).intValue();
        return value == 1;
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        final int dir = Utils.getMoveDirection(nextX - lastX, nextY - lastY);
        if (dir == -1) {
            return false;
        }
        if (check && !World.checkWalkStep(getPlane(), lastX, lastY, dir, getSize(), false, true)) {
            return false;
        }
        if (!controllerManager.canMove(dir, nextX, nextY)) {
            return false;
        }
        getWalkSteps().enqueue(WalkStep.getHash(dir, nextX, nextY, check));
        return true;
    }

    public void openShop(final String name) {
        //Different shop across the world, same npc.
        if (name.equals("Trader Stan's Trading Post")) {
            val charterLocation = Utils.getOrDefault(CharterLocation.getLocation(getLocation()), CharterLocation.BRIMHAVEN);
            Shop.get(name + "<" + charterLocation.getShopPrefix() + ">", isIronman(), this).open(this);
            return;
        }
        Shop.get(name, isIronman(), this).open(this);
    }

    public void setFarming(final Farming farming) {
        this.farming = new Farming(this, farming);
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0.6;
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.6;
    }

    @Override
    public double getMeleePrayerMultiplier() {
        return 0.6;
    }

    @Override
    public void heal(final int amount) {
        val hitpoints = getHitpoints();
        if (hitpoints >= getMaxHitpoints()) {
            return;
        }
        setHitpoints((hitpoints + amount) >= (getMaxHitpoints()) ? (getMaxHitpoints()) : (hitpoints + amount));
    }

    @Override
    public void unclip() {
        val size = getSize();
        val x = getX();
        val y = getY();
        val z = getPlane();
        int hash, lastHash = -1;
        Chunk chunk = null;
        for (int x1 = x; x1 < (x + size); x1++) {
            for (int y1 = y; y1 < (y + size); y1++) {
                if ((hash = Chunk.getChunkHash(x1 >> 3, y1 >> 3, z)) != lastHash) {
                    chunk = World.getChunk(lastHash = hash);
                }
                assert chunk != null;
                //if (collides(chunk.getPlayers(), x1, y1) || collides(chunk.getNPCs(), x1, y1)) continue;
                World.getRegion(Location.getRegionId(x1, y1), true).removeFlag(z, x1 & 0x3F, y1 & 0x3F, Flags.OCCUPIED_BLOCK_NPC);
            }
        }
    }

    @Override
    public void clip() {
        if (isFinished()) {
            return;
        }
        val size = getSize();
        val x = getX();
        val y = getY();
        val z = getPlane();
        for (int x1 = x; x1 < (x + size); x1++) {
            for (int y1 = y; y1 < (y + size); y1++) {
                World.getRegion(Location.getRegionId(x1, y1), true).addFlag(z, x1 & 0x3F, y1 & 0x3F, Flags.OCCUPIED_BLOCK_NPC);
            }
        }
    }

    @Override
    public void processEntity() {
        getSession().processEvents();
        if (currentNotification == null) {
            if(!getInterfaceHandler().containsInterface(InterfacePosition.CENTRAL)) {
                if (notifications.peek() != null) {
                    val notif = notifications.pollFirst();
                    currentNotification = notif;
                    WorldTasksManager.schedule(new WorldTask() {
                        int ticks;

                        @Override
                        public void run() {
                            if (ticks == 1) {
                                getInterfaceHandler().sendInterface(InterfacePosition.OVERLAY, 660);
                                getPacketDispatcher().sendClientScript(3343, notif.getTitle(), notif.getMessage(), notif.getColour());
                            }
                            if (ticks == 13) {
                                getInterfaceHandler().closeInterface(InterfacePosition.OVERLAY);
                                currentNotification = null;
                                stop();
                            }
                            ticks++;
                        }
                    }, 0, 0);
                }
            }
        }
        try {
            val event = routeEvent;
            if (event != null) {
                if (event.process()) {
                    if (routeEvent == event) {
                        routeEvent = null;
                    }
                }
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        try {
            if (!postPacketProcessingRunnables.isEmpty()) {
                postPacketProcessingRunnables.forEach(runnable -> {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                });
                postPacketProcessingRunnables.clear();
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        try {
            try {
                actionManager.process();
            } catch (final Exception e) {
                log.error(Strings.EMPTY, e);
            }

            variables.process();
            if (getCape() != null) {
                AvasDevice.collectMetal(this);
            }
            try {
                controllerManager.process();
            } catch (final Exception e) {
                log.error(Strings.EMPTY, e);
            }
            cutsceneManager.process();
            music.processMusicPlayer();
            if (getAttackedByDelay() > Utils.currentTimeMillis() && getAttackedBy() != null || actionManager.getAction() instanceof PlayerCombat && getAttacking() != null) {
                chargesManager.removeCharges(DegradeType.TICKS);
            }
            farming.processAll();
            hunter.process();
            prayerManager.process();
            val energy = variables.getRunEnergy();
            if (energy < 100 && getRunDirection() == -1) {
                var restore = ((8f + (skills.getLevel(Skills.AGILITY) / 6f)) / 0.6f / 100f) * 0.6f;
                double boost = 1;
                if (EquipmentUtils.containsFullGraceful(this)) {
                    boost += 0.3F;
                }
                if (perkManager.ifValidConsume(PerkWrapper.ATHLETIC_RUNNER) && !inArea("Wilderness")) {
                    boost += 0.25F;
                }
                if (getSkillingXPRate() == 10) {
                    boost += 0.02F;
                } else if (getSkillingXPRate() == 5) {
                    boost += 0.03F;
                }
                variables.forceRunEnergy(energy + (restore * boost));
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        super.processEntity();
        appendNearbyNPCs();
        if (damageSound != -1) {
            sendSound(new SoundEffect(damageSound));
            damageSound = -1;
        }
    }

    public void postProcess() {
        try {
            delayedActionManager.process();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @Override
    public void appendHitEntry(final HitEntry entry) {
        if (!entry.isFreshEntry()) {
            return;
        }
        entry.setFreshEntry(false);
        val hit = entry.getHit();
        val type = hit.getHitType();
        val shieldId = equipment.getId(EquipmentSlot.SHIELD);
        val source = entry.getSource();
        if (source != null) {
            if (type == HitType.MELEE) {
                if (prayerManager.isActive(Prayer.PROTECT_FROM_MELEE)) {
                    hit.setDamage((int) Math.ceil(hit.getDamage() * source.getMeleePrayerMultiplier()));
                }
            } else if (type == HitType.RANGED) {
                if (prayerManager.isActive(Prayer.PROTECT_FROM_MISSILES)) {
                    hit.setDamage((int) Math.ceil(hit.getDamage() * source.getRangedPrayerMultiplier()));
                }
            } else if (type == HitType.MAGIC) {
                if (prayerManager.isActive(Prayer.PROTECT_FROM_MAGIC)) {
                    hit.setDamage((int) Math.ceil(hit.getDamage() * source.getMagicPrayerMultiplier()));
                }
            }
        }
        if (type != HitType.DEFAULT && shieldId == 12817) {
            if (Utils.randomDouble() < 0.7F) {
                val reduced = (int) (hit.getDamage() * 0.25F);
                setGraphics(ELYSIAN_EFFECT_GFX);
                hit.setDamage(hit.getDamage() - reduced);
            }
        }
    }

    private void appendNearbyNPCs() {
        CharacterLoop.forEach(getLocation(), 25, NPC.class, npc -> {
            if (npc.getTargetType() != EntityType.PLAYER || npc.isDead()) return;
            NPC.pendingAggressionCheckNPCs.add(npc.getIndex());
        });
    }

    public void finish() {
        if (isFinished()) {
            return;
        }
        try {
            log.info("'" + getName() + "' has logged out.");
            SpellbookSwap.checkSpellbook(this);
            final Object loc = getTemporaryAttributes().get("oculusStart");
            if (loc instanceof Location) {
                setLocation((Location) loc);
            }
            controllerManager.logout();
            val area = getArea();
            if (area instanceof LogoutPlugin) {
                ((LogoutPlugin) area).onLogout(this);
            }
            construction.getTipJar().onLogout();
            if(TriviaBroadcasts.getTriviaWinners().contains(getUsername()))
            {
                TriviaBroadcasts.getTriviaWinners().remove(getUsername());
            }
            setFinished(true);
            World.updateEntityChunk(this, true);
            LocationMap.remove(this);
            getInterfaceHandler().closeInterface(GameInterface.TOURNAMENT_SPECTATING);
            GlobalAreaManager.update(this, false, true);
            if (getTemporaryAttributes().get("cameraShake") != null) {
                packetDispatcher.resetCamera();
            }
            if (follower != null) {
                follower.finish();
            }
            ClanManager.leave(this, false);
            socialManager.updateStatus();
            interfaceHandler.closeInterfaces();

            final String address = getSession().getChannel().remoteAddress().toString();
            playerInformation.setIp(address.substring(1, address.indexOf(":")));
            MethodicPluginHandler.invokePlugins(ListenerType.LOGOUT, this);
            PluginManager.post(new LogoutEvent(this));
            CoresManager.getServiceProvider().submit(logger::shutdown);
            /*appender.getManager().flush();
            appender.stop();
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            loggerConfig.removeAppender(appender.getName());
            ctx.updateLoggers();*/
            this.getSession().getChannel().flush().closeFuture();
            sendPlayerInformationToApi();
            postFinish();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    private void sendPlayerInformationToApi() {
        if (Constants.WORLD_PROFILE.isPrivate()
                || Constants.WORLD_PROFILE.isBeta()
                || Constants.WORLD_PROFILE.isDevelopment()
                || !Constants.WORLD_PROFILE.getApi().isEnabled()) {
            return;
        }
        //Avoids a dumb exception.
        if (getCombatXPRate() == 1) {
            return;
        }
        val info = new com.zenyte.api.model.PlayerInformation(
                playerInformation.getUserIdentifier(),
                getUsername().replaceAll("_", " "),
                skills.getTotalLevel(),
                memberRank.getApiRole(),
                gameMode.getApiRole(),
                getApiExperienceMode()
        );
        val skipHighscores = this.playerInformation.getUserIdentifier() == -1 || this.privilege.eligibleTo(Privilege.SPAWN_ADMINISTRATOR) || !isApiExperienceModePresent();
        val tile = getLocation();
        val hiscores = skipHighscores ? null : getHiscores();
        val username = getUsername();
        CoresManager.getServiceProvider().submit(() -> {
            if (!skipHighscores) {
                refreshHighscores(username, tile, hiscores);
            }
            sendPlayerInfo(info);
        });
    }

    private void sendPlayerInfo(final com.zenyte.api.model.PlayerInformation info) {
        new SubmitPlayerInformation(info).execute();
    }

    private void refreshHighscores(final String username, final Location location, final List<SkillHiscore> hiscores) {
        if (isNulled() || this.playerInformation.getUserIdentifier() == -1 || this.privilege.eligibleTo(Privilege.SPAWN_ADMINISTRATOR) || !isApiExperienceModePresent()) {
            return;
        }
        if (TutorialIslandArea.polygon.contains(location)) {
            log.info("User '" + getName() + "' in tutorial island, holding off sending hiscores data");
            return;
        }
        new SendPlayerHiscores(username, hiscores).execute();
    }

    private List<SkillHiscore> getHiscores() {
        val hiscores = new ArrayList<SkillHiscore>(Skills.SKILLS.length);

        for (Skill skill : Skill.VALUES_NO_TOTAL) {
            hiscores.add(new SkillHiscore(
                    getPlayerInformation().getUserIdentifier(),
                    getUsername(),
                    getGameMode().getApiRole(),
                    getApiExperienceMode(),
                    skill.getId(),
                    skill.getFormattedName(),
                    getSkills().getLevelForXp(skill.getId()),
                    (long) getSkills().getExperience(skill.getId())
            ));
        }

        return hiscores;
    }

    private void postFinish() {
        temporaryAttributes.clear();
        pendingContainers.clear();
        attackedByPlayers.clear();
        pendingVars.clear();
        zoneFollowPackets.clear();
        tempList.clear();
        chunksInScope.clear();
        receivedHits.clear();
        nextHits.clear();
        hitBars.clear();
        npcViewport.reset();
        playerViewport.reset();
        postSaveFunction = this::postSave;
    }

    @Getter @Setter private transient Runnable postSaveFunction;
    @Getter @Setter private transient boolean nulled;

    private void postSave() {
        try {
            if (isNulled()) {
                return;
            }
            setNulled(true);
            unlink();
            final Field[] fields = getClass().getDeclaredFields();
            for (final Field field : fields) {
                final int modifier = field.getModifiers();
                if (Modifier.isStatic(modifier) || field.getType().isPrimitive()) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    FieldModifiersHelper.definalize(field);
                } catch (Exception e) {
                    log.error("", e);
                }
                try {
                    field.set(this, null);
                } catch (IllegalAccessException e) {
                    log.error("", e);
                }
            }
            this.postSaveFunction = null;
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void processEntityUpdate() {
        if (!pendingContainers.isEmpty()) {
            if (pendingContainers.contains(inventory.getContainer()) || pendingContainers.contains(equipment.getContainer())) {
                packetDispatcher.sendWeight();
            }
            for (final Container container : pendingContainers) {
                if (container.isFullUpdate() || container.getModifiedSlots().size() >= (container.getContainerSize() * 0.67f)) {
                    packetDispatcher.sendUpdateItemContainer(container);
                } else {
                    packetDispatcher.sendUpdateItemsPartial(container);
                }
            }
            pendingContainers.clear();
        }
        skills.sendQueuedFakeExperienceDrops();
        val regionUpdate = isNeedRegionUpdate();
        if (regionUpdate) {
            loadMapRegions();
        }
        send(playerViewport.cache());
        //from here.
        if (!pendingVars.isEmpty()) {
            for (val var : pendingVars) {
                packetDispatcher.sendConfig(var, varManager.getValue(var));
            }
            pendingVars.clear();
        }
        if (teleported) {
            if (lastLocation != null && getPlane() != lastLocation.getPlane()) {
                zoneFollowPackets.clear();
            }
            updateScopeInScene();
        }

        if (!tempList.isEmpty()) {
            for (val proj : tempList) {
                sendZoneUpdate(proj.sender.getX(), proj.sender.getY(), proj.packet);
            }
            tempList.clear();
        }

        if (!zoneFollowPackets.isEmpty()) {
            for (Int2ObjectMap.Entry<List<GamePacketEncoder>> entry : zoneFollowPackets.int2ObjectEntrySet()) {
                val key = entry.getIntKey();
                val packets = entry.getValue();
                if (packets.size() == 1) {
                    send(new UpdateZonePartialFollows((key & 0x7FF) << 3, (key >> 11 & 0x7FF) << 3, this));
                    send(packets.get(0));
                } else {
                    val zonePacket = new UpdateZonePartialEnclosed((key & 0x7FF) << 3, (key >> 11 & 0x7FF) << 3, this);
                    for (int i = packets.size() - 1; i >= 0; i--) {
                        zonePacket.append(packets.get(i));
                    }
                    send(zonePacket);
                }
            }
            zoneFollowPackets.clear();
        }
        send(npcViewport.cache());//to here
        if (regionUpdate) {
            setNeedRegionUpdate(false);
        }
        flush();
    }

    public void sendZoneUpdate(final int tileX, final int tileY, final GamePacketEncoder packet) {
        val chunkX = tileX >> 3;
        val chunkY = tileY >> 3;
        val hash = chunkX | chunkY << 11;
        var list = zoneFollowPackets.get(hash);
        if (list == null) {
            list = new ArrayList<>();
            zoneFollowPackets.put(hash, list);
        }
        list.add(packet);
    }

    /**
     * TODO: Temporary; testing to see if this fixes the issue!
     */
    public void addProj(final Location sender, final GamePacketEncoder packet) {
        tempList.add(new ProjPacket(sender, packet));
    }

    public void sendSound(final int id) {
        if (id <= -1) {
            return;
        }
        this.packetDispatcher.sendSoundEffect(SoundEffect.get(id));
    }

    public void sendSound(final SoundEffect sound) {
        this.packetDispatcher.sendSoundEffect(sound);
    }

    @Override
    public void setAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            if (!AnimationMap.isValidAnimation(appearance.getNpcId(), animation.getId())) {
                return;
            }
            updateFlags.flag(UpdateFlag.ANIMATION);
            final AnimationDefinitions defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    @Override
    public void setInvalidAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            updateFlags.flag(UpdateFlag.ANIMATION);
            final AnimationDefinitions defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    public ExpMode getApiExperienceMode() {
        return getApiExperienceMode(getCombatXPRate());
    }

    public ExpMode getApiExperienceMode(int rate) {
        if (rate >= 50) {
            return ExpMode.FIFTY;
        } else if (rate == 10) {
            return ExpMode.TEN;
        } else if (rate == 5) {
            return ExpMode.FIVE;
        }
        throw new RuntimeException("Invalid combat xp rate: " + rate);
    }

    public boolean isApiExperienceModePresent() {
        val rate = getCombatXPRate();
        return rate == 50 || rate == 10 || rate == 5;
    }

    public int getExperienceRate(final int skill) {
        return Skills.isCombatSkill(skill) ? getCombatXPRate() : getSkillingXPRate();
    }

    public int getSkillingXPRate() {
        return Math.max(1, getNumericAttribute("skilling_xp_rate").intValue());
    }

    public int getCombatXPRate() {
        return Math.max(1, getNumericAttribute("combat_xp_rate").intValue());
    }

    public void setExperienceMultiplier(final int combat, final int skilling) {
        addAttribute("skilling_xp_rate", Math.max(1, skilling));
        addAttribute("combat_xp_rate", Math.max(1, combat));
        if (getNumericAttribute("Xp Drops Multiplied").intValue() == 1) {
            if (getNumericAttribute("Xp Drops Wildy Only").intValue() == 0 || WildernessArea.isWithinWilderness(getX(), getY())) {
                getVarManager().sendVar(3504, 1);
            }
        }
        val optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            val plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("XP rate"), "XP: " + "<col=ffffff>" + getCombatXPRate() + "x Combat & " + getSkillingXPRate() + "x Skilling</col>");
        }
    }

    public boolean isFloorItemDisplayed(final FloorItem item) {
        if (getNumericAttribute(GameSetting.HIDE_ITEMS_YOU_CANT_PICK.toString()).intValue() == 0) {
            return true;
        }
        return !isIronman() || !item.hasOwner() || item.isOwner(this);
    }

    public boolean isXPDropsMultiplied() {
        return getNumericAttribute("Xp Drops Multiplied").intValue() == 1;
    }

    public boolean isXPDropsWildyOnly() {
        return getNumericAttribute("Xp Drops Wildy Only").intValue() == 1;
    }

    public void setXpDropsMultiplied(final boolean value) {
        addAttribute("Xp Drops Multiplied", value ? 1 : 0);
    }

    public void setXPDropsWildyOnly(final boolean value) {
        addAttribute("Xp Drops Wildy Only", value ? 1 : 0);
    }

    @Override
    public void setUnprioritizedAnimation(final Animation animation) {
        if (lastAnimation > Utils.currentTimeMillis() || updateFlags.get(UpdateFlag.ANIMATION)) {
            return;
        }
        if (animation != null && !AnimationMap.isValidAnimation(appearance.getNpcId(), animation.getId())) {
            return;
        }
        this.animation = animation;
        updateFlags.set(UpdateFlag.ANIMATION, animation != null);
    }

    public void forceAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            updateFlags.flag(UpdateFlag.ANIMATION);
            final AnimationDefinitions defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    private synchronized void flush() {
        val session = getSession();
        val prioritizedQueue = session.getGamePacketOutPrioritizedQueue();
        while (!prioritizedQueue.isEmpty()) {
            if (!session.write(prioritizedQueue.poll())) break;
        }
        val queue = session.getGamePacketOutQueue();
        while (!queue.isEmpty()) {
            if (!session.write(queue.poll())) break;
        }
        session.flush();
    }

    public synchronized void send(final GamePacketEncoder encoder) {
        try {
            val session = getSession();
            if (session == null) {
                return;
            }
            val packet = encoder.encode();
            if (encoder.prioritized()) {
                session.getGamePacketOutPrioritizedQueue().add(packet);
            } else {
                session.getGamePacketOutQueue().add(packet);
            }
            if (encoder.level().getPriority() >= PlayerLogger.WRITE_LEVEL.getPriority()) {
                encoder.log(this);
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public void init(final Player player) {
        run = player.run;
        gameMode = player.gameMode;
        memberRank = Utils.getOrDefault(player.memberRank, MemberRank.NONE);
        privilege = player.privilege;
        experienceMode = player.experienceMode;
        respawnPoint = player.respawnPoint;
        if (player.paydirt != null) {
            paydirt.addAll(player.paydirt);
        }
        if (player.trackedHolidayItems != null) {
            trackedHolidayItems.addAll(player.trackedHolidayItems);
        }
    }

    @Override
    public void loadMapRegions() {
        super.loadMapRegions();
        this.setNeedRegionUpdate(false);
        if (isRunning() && isAtDynamicRegion()) {
            packetDispatcher.sendDynamicMapRegion();
        } else {
            packetDispatcher.sendStaticMapRegion();
        }
        forceReloadMap = false;
        val tile = getLastLoadedMapRegionTile();
        val swx = ((tile.getChunkX() - 6) << 3) + 1;
        val swy = ((tile.getChunkY() - 6) << 3) + 1;
        this.sceneRectangle = World.getRectangle(swx, swx + 102, swy, swy + 102);
    }

    @Override
    public int getSize() {
        try {
            val npcId = appearance.getNpcId();
            if (npcId != -1) {
                return NPCDefinitions.get(npcId).getSize();
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        return 1;
    }

    @Override
    public int getHitpoints() {
        return isNulled() ? 0 : skills.getLevel(Skills.HITPOINTS);
    }

    @Override
    public boolean   setHitpoints(final int hitpoints) {
        val dead = isDead();
        skills.setLevel(Skills.HITPOINTS, hitpoints);
        this.hitpoints = hitpoints;
        if (!dead && this.hitpoints <= 0) {
            sendDeath();
            return true;
        }
        return false;
    }

    @Override
    public void unlink() {

    }

    @Override
    public int getMaxHitpoints() {
        return skills.getLevelForXp(Skills.HITPOINTS);
    }

    @Override
    public int getClientIndex() {
        return getIndex() + 32768;
    }

    @Override
    public boolean isDead() {
        return getHitpoints() == 0;
    }

    @Override
    public void cancelCombat() {
        if (actionManager.getAction() instanceof PlayerCombat) {
            actionManager.forceStop();
        }
    }

    public void setFollower(final Follower follower) {
        if (this.follower != null && follower == null) {
            this.follower.finish();
            petId = -1;
            this.follower = null;
            return;
        }
        this.follower = follower;
        petId = follower == null ? -1 : follower.getId();
        if (follower != null) {
            follower.spawn();
        }
        varManager.sendVar(447, follower == null ? -1 : follower.getIndex());
    }

    public void stopAll() {
        this.stopAll(true);
    }

    public void stopAll(final boolean stopWalk) {
        this.stopAll(stopWalk, true);
    }

    public void stopAll(final boolean stopWalk, final boolean stopInterface) {
        this.stopAll(stopWalk, stopInterface, true);
    }

    public void useStairs(final int emoteId, final Location dest, final int useDelay, final int totalDelay) {
        this.useStairs(emoteId, dest, useDelay, totalDelay, null);
    }

    public void useStairs(final int emoteId, final Location dest, final int useDelay, final int totalDelay, final String message) {
        this.useStairs(emoteId, dest, useDelay, totalDelay, message, false);
    }

    public void useStairs(final int emoteId, final Location dest, final int useDelay, final int totalDelay, final String message, final boolean resetAnimation) {
        this.stopAll();
        this.lock(totalDelay);
        if (emoteId != -1) {
            setAnimation(new Animation(emoteId));
        }
        if (useDelay == 0) {
            teleport(dest);
        } else {
            WorldTasksManager.schedule(() -> {
                if (Player.this.isDead()) {
                    return;
                }
                if (resetAnimation) {
                    Player.this.setAnimation(Animation.STOP);
                }
                teleport(dest);
                if (message != null) {
                    Player.this.sendMessage(message);
                }
            }, useDelay - 1);
        }
    }

    public void stopAll(final boolean stopWalk, final boolean stopInterfaces, final boolean stopActions) {
        setRouteEvent(null);
        if (getFaceEntity() >= 0) {
            setFaceEntity(null);
        }
        varManager.sendBit(5983, 0);
        if (getTemporaryAttributes().get("CreatingRoom") != null) {
            construction.roomPreview((RoomReference) getTemporaryAttributes().get("CreatingRoom"), true);
            getTemporaryAttributes().remove("CreatingRoom");
        }

        interfaceHandler.closeInput();

        if (stopInterfaces) {
            getTemporaryAttributes().remove("skillDialogue");
            interfaceHandler.closeInterfaces();
            if (worldMap.isVisible() && worldMap.isFullScreen()) {
                worldMap.close();
            }
        }
        if (stopWalk) {
            getPacketDispatcher().resetMapFlag();
            resetWalkSteps();
        }
        if (stopActions) {
            actionManager.forceStop();
            delayedActionManager.forceStop();
        }
    }

    @Override
    public void resetMasks() {
        if (updateFlags.isUpdateRequired()) {
            updateFlags.reset();
        }
        if (!hitBars.isEmpty()) {
            hitBars.clear();
        }
        if (!nextHits.isEmpty()) {
            nextHits.clear();
        }
        this.updateNPCOptions = false;
        if (appearance.getBuffer().isReadable()) {
            appearance.getBuffer().clear();
        }
    }

    @Override
    public void resetWalkSteps() {
        super.resetWalkSteps();
        pathfindingEvent = null;
    }

    public void stop(final StopType... types) {
        for (val type : types) {
            type.consumer.accept(this);
        }
    }

    public void stopAllExclWorldMap() {
        setRouteEvent(null);
        if (varManager.getBitValue(5983) != 0) {
            varManager.sendBit(5983, 0);
        }
        if (getTemporaryAttributes().get("CreatingRoom") != null) {
            construction.roomPreview((RoomReference) getTemporaryAttributes().get("CreatingRoom"), true);
            getTemporaryAttributes().remove("CreatingRoom");
        }
        getTemporaryAttributes().remove("skillDialogue");
        interfaceHandler.closeInterfaces();
        resetWalkSteps();
        actionManager.forceStop();
        delayedActionManager.forceStop();
        setAnimation(Animation.STOP);
    }

    @Override
    public void processReceivedHits() {
        super.processReceivedHits();
    }

    @Override
    public void applyHit(final Hit hit) {
        if (isNulled()) {
            return;
        }
        super.applyHit(hit);
        interfaceHandler.closeInterfaces();
        if (worldMap.isVisible() && worldMap.isFullScreen()) {
            worldMap.close();
        }
    }

    private final void reflectDamage(final Hit hit) {
        val source = hit.getSource();
        if (source == null ||  hit.getHitType() == HitType.REGULAR) {
            return;
        }
        val amuletId = equipment.getId(EquipmentSlot.AMULET);
        val damage = hit.getDamage();
        if (damage <= 0) {
            return;
        }
        if ((amuletId == 12851 || amuletId == 12853) && Utils.random(3) == 0 && CombatUtilities.hasFullBarrowsSet(this, "Dharok's")) {
            WorldTasksManager.schedule(() -> source.applyHit(new Hit(this, (int) (damage * 0.15f), HitType.REGULAR)));
        }
        if (hit.getDamage() > 3) {
            final boolean hasVengeance = getAttributes().remove("vengeance") != null;
            if (hasVengeance) {
                setForceTalk(VENGEANCE);
                if (!(source instanceof JewelledCrab)) {
                    source.applyHit(new Hit(this, (int) (damage * 0.75f), HitType.REGULAR), true);
                }
            }
        }
        val ring = equipment.getId(EquipmentSlot.RING);
        if (ring == 2550 || ((ring == 19710 || ring == 20655 || ring == 20657))) {
            if (ring == 2550 || getBooleanAttribute("recoil effect")) {
                val ringItem = getRing();
                int charges = ring == 2550 ? getNumericAttribute("RING_OF_RECOIL").intValue() : ringItem.getCharges();
                if (ring == 2550 && charges == 0) {
                    charges = 40;
                }
                final int reflected = Math.min((int) Math.floor(damage / 10F) + 1, charges);
                chargesManager.removeCharges(ringItem, reflected, equipment.getContainer(), EquipmentSlot.RING.getSlot());
                WorldTasksManager.schedule(() -> source.applyHit(new Hit(this, reflected, HitType.REGULAR)));
            }
        }
    }

    private void applySmite(final Hit hit) {
        val source = hit.getSource();
        if (!(source instanceof Player)) {
            return;
        }
        val damage = Math.min(hit.getDamage(), getHitpoints());
        if (((Player) source).getPrayerManager().isActive(Prayer.SMITE)) {
            val drain = damage / 4;
            if (drain > 0) {
                prayerManager.drainPrayerPoints(drain);
            }
        }
    }

    private void applyDamageReducers(final Hit hit, final Entity source) {
        int damage = hit.getDamage();

        val weaponId = equipment.getId(EquipmentSlot.WEAPON);
        val type = hit.getHitType();
        if (source.getEntityType() == EntityType.NPC) {
            if (weaponId == 21015) {
                val delay = getNumericTemporaryAttribute("dinhsbulwarkdelay").longValue();
                if (Utils.currentTimeMillis() > delay) {
                    damage = (int) (damage * 0.8F);
                }
            }

            if (CombatUtilities.hasFullJusticiarSet(this) && type != HitType.DEFAULT) {
                val bonus = bonuses.getBonus(type == HitType.MELEE ? 7 : type == HitType.MAGIC ? 8 : 9);
                if (bonus > 0) {
                    val percentage = bonus / 3000D;
                    val reduced = (int) (damage * percentage);
                    damage -= reduced;
                }
            }
        }

        if (hit.getDamage() != damage) {
            hit.setDamage(Math.max(0, damage));
        }

    }

    private static final int[] maleDamageSounds = new int[] {
            518, 519, 521
    };

    private static final int[] femaleDamageSounds = new int[] {
            509, 510
    };

    @Override
    public void postProcessHit(final Hit hit) {
        applySmite(hit);
        reflectDamage(hit);
        if (damageSound == -1 || damageSound == 511) {
            if (hit.getDamage() > 0) {
                val array = appearance.isMale() ? maleDamageSounds : femaleDamageSounds;
                damageSound = array[Utils.random(array.length - 1)];
            } else {
                damageSound = 511;
            }
        }
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        val source = hit.getSource();
        if (source == null) {
            return;
        }

        if (hit.getDamage() > 0) {
            chargesManager.removeCharges(DegradeType.INCOMING_HIT);
        }

        val type = hit.getHitType();
        if (!ArrayUtils.contains(PROCESSED_HIT_TYPES, type)) {
            return;
        }
        applyDamageReducers(hit, source);
    }

    public final void sendMessage(final String message) {
        packetDispatcher.sendGameMessage(message, false);
    }

    public final void sendFilteredMessage(final String message) {
        packetDispatcher.sendGameMessage(message, true);
    }

    public final void sendMessage(final String message, final MessageType type) {
        packetDispatcher.sendGameMessage(message, type);
    }

    public final void sendMessage(final String message, final MessageType type, final String extension) {
        packetDispatcher.sendMessage(message, type, extension);
    }
    
    @Getter
    private Queue<Location> tolerancePositionQueue = new LinkedList<>();
    /*private transient LoggerConfig loggerConfig;
    private transient FileAppender appender;
    @Getter
    private transient Logger logger;*/

    public final void createLogger() {
        /*val name = getUsername();
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        Layout<? extends Serializable> layout = PatternLayout.newBuilder().withAlwaysWriteExceptions(true).withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} " + "[%thread] %-5level - %msg%n").withConfiguration(config).build();

        appender =
                FileAppender.newBuilder().withFileName("data/logs/player/" + name + ".log").withName("File").withImmediateFlush(false).withBufferedIo(true).withBufferSize(4096).withLayout(layout).setConfiguration(config).build();

        appender.start();
        config.addAppender(appender);
        AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
        AppenderRef[] refs = new AppenderRef[] { ref };
        loggerConfig = LoggerConfig.createLogger(false, Level.INFO, "org.apache.logging.log4j", "true", refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);
        config.addLogger("Player logger: " + name, loggerConfig);
        ctx.updateLoggers();
        this.logger = ctx.getLogger("Player logger: " + name);*/
        logger.build();
    }

    public void log(final LogLevel level, final String info) {
        logger.log(level, info);
    }

    public void errorlog(final String info) {
        log(LogLevel.ERROR, info);
    }

    @Override
    public void checkMultiArea() {
        final boolean isAtMultiArea = isForceMultiArea() || World.isMultiArea(getLocation());
        /** Multi icon is updated in synchronization with the client. */
        if (isAtMultiArea && !Player.this.isMultiArea()) {
            Player.this.setMultiArea(isAtMultiArea);
            WorldTasksManager.schedule(() -> varManager.sendBit(4605, 1));
        } else if (!isAtMultiArea && Player.this.isMultiArea()) {
            Player.this.setMultiArea(isAtMultiArea);
            WorldTasksManager.schedule(() -> varManager.sendBit(4605, 0));
        }
    }

    @Override
    public void removeHitpoints(final Hit hit) {
        if (isDead()) {
            return;
        }

        val hitpoints = getHitpoints();

        int damage = hit.getDamage();
        if (damage > hitpoints) {
            damage = hitpoints;
        }
        addReceivedDamage(hit.getSource(), damage);
        val dead = setHitpoints(hitpoints - damage);
        if (dead) {
            temporaryAttributes.put("killing blow hit", hit.getSource());
        }
        if (!isDead() && (getHitpoints() < getMaxHitpoints() * 0.1F) && prayerManager.isActive(Prayer.REDEMPTION)) {
            prayerManager.applyRedemptionEffect();
        }
        if (!isDead() && (getHitpoints() < getMaxHitpoints() * 0.2F)) {
            if (equipment.getId(EquipmentSlot.AMULET) == 21157) {
                equipment.set(EquipmentSlot.AMULET, null);
                prayerManager.restorePrayerPoints((int) (skills.getLevelForXp(Skills.PRAYER) * 0.1F));
                sendFilteredMessage("Your necklace of faith degrades to dust.");
            }
        }
        if (!isDead()) {
            val necklace = this.getAmulet();
            if (necklace != null && necklace.getId() == 11090 && getHitpoints() < (getMaxHitpoints() * 0.2F) && getDuel() == null) {
                this.heal((int) (this.getMaxHitpoints() * 0.3F));
                sendMessage("Your phoenix necklace heals you, but is destroyed in the process.");
                equipment.set(EquipmentSlot.AMULET, null);
                equipment.refresh();
            }
            if (getHitpoints() <= (getMaxHitpoints() * 0.1F)) {
                val ring = equipment.getId(EquipmentSlot.RING);
                val area = getArea();
                if (area instanceof DeathPlugin && !((DeathPlugin) area).isRingOfLifeEffective()) {
                    return;
                }
                if (!(ring == 2570 || (SkillcapePerk.DEFENCE.isEffective(this) && getBooleanAttribute("Skillcape ring of life teleport")))) {
                    return;
                }
                if (variables.getTime(TickVariable.TELEBLOCK) > 0) {
                    return;
                }
                val level = WildernessArea.getWildernessLevel(getLocation());
                if (level.isPresent() && level.getAsInt() > 30) {
                    return;
                }
                stopAll();
                if (SkillcapePerk.DEFENCE.isEffective(this) && getBooleanAttribute("Skillcape ring of life teleport")) {
                    sendMessage("Your cape saves you.");
                } else {
                    equipment.set(EquipmentSlot.RING, null);
                    sendMessage("Your Ring of Life saves you and is destroyed in the process.");
                    updateFlags.flag(UpdateFlag.APPEARANCE);
                }
                val teleport = new Teleport() {
                    @Override
                    public TeleportType getType() {
                        return TeleportType.REGULAR_TELEPORT;
                    }

                    @Override
                    public Location getDestination() {
                        return respawnPoint.getLocation();
                    }

                    @Override
                    public int getLevel() {
                        return 0;
                    }

                    @Override
                    public double getExperience() {
                        return 0;
                    }

                    @Override
                    public int getRandomizationDistance() {
                        return 0;
                    }

                    @Override
                    public Item[] getRunes() {
                        return null;
                    }

                    @Override
                    public int getWildernessLevel() {
                        return 30;
                    }

                    @Override
                    public boolean isCombatRestricted() {
                        return false;
                    }
                };
                teleport.teleport(this);
            }
        }
    }
    
    private transient PlayerLogger logger = new PlayerLogger(this);

    @Override
    public void sendDeath() {
        val source = getMostDamagePlayer();
        if (!controllerManager.sendDeath(source) || areaManager.sendDeath(this, source)) {
            return;
        }
        if (animation != null) {
            animation = null;
            updateFlags.set(UpdateFlag.ANIMATION, false);
        }
        lock();
        stopAll();
        if (prayerManager.isActive(Prayer.RETRIBUTION)) {
            prayerManager.applyRetributionEffect(source);
        }
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                if (isFinished() || isNulled()) {
                    stop();
                    return;
                }
                if (ticks == 1) {
                    setAnimation(DEATH_ANIMATION);
                } else if (ticks == 4) {
                    deathMechanics.death(source, null);
                    if (source != null) {
                        val index = Utils.random(deathMessages.length - 1);
                        String message = deathMessages[index];
                        if (index >= deathMessages.length - 2) {
                            if (index == deathMessages.length - 1) {
                                message = message.replace("%gender%", getAppearance().isMale() ? "him" : "her");
                            } else {
                                message = message.replace("%gender%", getAppearance().isMale() ? "he" : "she");
                            }
                        }
                        source.sendMessage(String.format(message, getName()));
                    }
                    sendMessage("Oh dear, you have died.");
                    getMusic().playJingle(90);
                    reset();
                    setAnimation(Animation.STOP);
                    variables.setSkull(false);
                    val area = getArea();
                    val plugin = area instanceof DeathPlugin ? (DeathPlugin) area : null;
                    val respawnLocation = plugin == null ? null : plugin.getRespawnLocation();
                    Player.this.setLocation(respawnLocation != null ? respawnLocation : respawnPoint.getLocation());
                } else if (ticks == 5) {
                    unlock();
                    setAnimation(Animation.STOP);
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    public int getQuestPoints() {
        return getNumericAttribute("quest_points").intValue();
    }

    public void setQuestPoints(final int amount) {
        addAttribute("quest_points", amount);
    }

    public void refreshQuestPoints() {
        getVarManager().sendVar(101, getQuestPoints());
    }

    public boolean isVisibleInViewport(final Position position) {
        return isVisibleInScene(position) && location.withinDistance(position, getViewDistance());
    }

    public boolean isVisibleInScene(final Position position) {
        val pos = position.getPosition();
        return sceneRectangle.contains(pos.getX(), pos.getY());
    }

    void refreshScopedGroundItems(final boolean add) {
        SceneSynchronization.refreshScopedGroundItems(this, add);
    }

    public void syncTotalDonated() {
        if (Constants.WORLD_PROFILE.getApi().isEnabled()) {
            CoresManager.getServiceProvider().submit(() -> {
                val amount = new TotalDonatedRequest(getUsername()).execute();
                val actualAmount = Math.max(0, amount);
                WorldTasksManager.schedule(() -> {
                    addAttribute("total donated online", actualAmount);
                    refreshTotalDonated();
                });
            });
        }
    }
    public void refreshTotalDonated() {
        val totalDonated = getNumericAttribute("total donated online").intValue();
        if (totalDonated >= 5000) {
            setMemberRank(MemberRank.ZENYTE_MEMBER);
        } else if (totalDonated >= 2500) {
            setMemberRank(MemberRank.ONYX_MEMBER);
        } else if (totalDonated >= 1000) {
            setMemberRank(MemberRank.DRAGONSTONE_MEMBER);
        } else if (totalDonated >= 500) {
            setMemberRank(MemberRank.DIAMOND_MEMBER);
        } else if (totalDonated >= 200) {
            setMemberRank(MemberRank.RUBY_MEMBER);
        } else if (totalDonated >= 50) {
            setMemberRank(MemberRank.EMERALD_MEMBER);
        } else if (totalDonated >= 10) {
            setMemberRank(MemberRank.SAPPHIRE_MEMBER);
        } else {
            setMemberRank(MemberRank.NONE);
        }


        GameInterface.GAME_NOTICEBOARD.getPlugin().ifPresent(plugin -> {
            getPacketDispatcher().sendComponentText(GameInterface.GAME_NOTICEBOARD, plugin.getComponent("Member Rank"), "Member: <col=ffffff>" + getMemberRank().getCrown() + getMemberRank().toString().replace(" Member", "") + "</col>");
            getPacketDispatcher().sendComponentText(GameInterface.GAME_NOTICEBOARD,
                    plugin.getComponent("Total donated"),
                    "Total donated: <col=ffffff>$" + (totalDonated) + "</col>");
        });
    }

    public void updateScopeInScene() {
        SceneSynchronization.updateScopeInScene(this);
    }

    @Override
    public void setLocation(final Location tile) {
        if (tile == null) {
            return;
        }
        nextLocation = new Location(tile);
    }

    /**
     * Equipment getters - a better form of this should be made (kotlins extension / proxy functions would be nice)
     */

    public Item getHelmet() {
        return equipment.getItem(0);
    }

    public Item getCape() {
        return equipment.getItem(1);
    }

    public Item getAmulet() {
        return equipment.getItem(2);
    }

    public Item getWeapon() {
        return equipment.getItem(3);
    }

    public Item getChest() {
        return equipment.getItem(4);
    }

    public Item getShield() {
        return equipment.getItem(5);
    }

    public Item getLegs() {
        return equipment.getItem(7);
    }

    public Item getGloves() {
        return equipment.getItem(9);
    }

    public Item getBoots() {
        return equipment.getItem(10);
    }

    public Item getRing() {
        return equipment.getItem(12);
    }

    public Item getAmmo() {
        return equipment.getItem(13);
    }

    @Override
    public Location getMiddleLocation() {
        if (middleTile == null) {
            middleTile = new Location(getLocation());
        } else {
            middleTile.setLocation(getLocation());
        }
        return middleTile;
    }

    private static final Animation candyCaneBlockAnimation = new Animation(15086);
    private static final Animation easterCarrotBlockAnimation = new Animation(15162);

    private Animation getDefenceAnimation() {
        val weaponId = equipment.getId(EquipmentSlot.WEAPON);
        if (weaponId == 21015) {
            return BULWARK_ANIM;
        }
        if (weaponId == ChristmasConstants.CANDY_CANE) {
            return candyCaneBlockAnimation;
        }
        if (weaponId == EasterConstants.EasterItem.EASTER_CARROT.getItemId()) {
            return easterCarrotBlockAnimation;
        }
        val weaponDefinitions = ItemDefinitions.get(weaponId);
        if (weaponDefinitions == null) {
            return PLAIN_DEFENCE_ANIM;
        }
        val shieldId = equipment.getId(EquipmentSlot.SHIELD);
        val shieldDefinitions = ItemDefinitions.get(shieldId);
        if (weaponId == 4084) {
            return new Animation(1466);
        }
        if (shieldId != -1) {
            if ((shieldId >= 8844 && shieldId <= 8850) || shieldId == 12954 || shieldId == 19722 || shieldId == 22322 || shieldId == ItemId.RUNE_DEFENDER_T) {
                return new Animation(4177);
            }
            if (shieldDefinitions != null && shieldDefinitions.getName().toLowerCase().contains("book")) {
                return new Animation(420);
            }
            return new Animation(1156);
        }
        val blockAnimation = weaponDefinitions.getBlockAnimation();
        if (!AnimationMap.isValidAnimation(appearance.getNpcId(), blockAnimation) || blockAnimation == 0) {
            return PLAIN_DEFENCE_ANIM;
        }
        return new Animation(blockAnimation);
    }

    public void setCanPvp(final boolean canPvp) {
        setCanPvp(canPvp, false);
    }

    public void setCanPvp(final boolean canPvp, final boolean duel) {
        if (this.canPvp == canPvp) {
            return;
        }
        this.canPvp = canPvp;
        this.setPlayerOption(1, canPvp ? duel ? "Fight" : "Attack" : "null", true);
    }

    @Override
    public void performDefenceAnimation(Entity attacker) {
        if (getWeapon() != null && getWeapon().getId() == 21015) {
            setGraphics(BULWARK_GFX);
        }
        setUnprioritizedAnimation(getDefenceAnimation());
    }

    @Override
    public int drainSkill(final int skill, final double percentage) {
        if (skill == Skills.PRAYER) {
            return prayerManager.drainPrayerPoints(percentage, 0);
        }
        return skills.drainSkill(skill, percentage, 0);
    }

    @Override
    public int drainSkill(final int skill, final double percentage, final int minimumDrain) {
        if (skill == Skills.PRAYER) {
            return prayerManager.drainPrayerPoints(percentage, minimumDrain);
        }
        return skills.drainSkill(skill, percentage, minimumDrain);
    }

    @Override
    public int drainSkill(final int skill, final int amount) {
        if (skill == Skills.PRAYER) {
            return prayerManager.drainPrayerPoints(amount);
        }
        return skills.drainSkill(skill, amount);
    }

    @Override
    public boolean startAttacking(final Player source, final CombatType type) {
        return true;
    }

    @Override
    public boolean canAttack(final Player source) {
        return true;
    }

    @Override
    public void autoRetaliate(final Entity source) {
        if (!combatDefinitions.isAutoRetaliate() || !source.triggersAutoRetaliate() || actionManager.hasSkillWorking()
                || hasWalkSteps() || isLocked()) {
            return;
        }
        PlayerCombat.attackEntity(this, source, null);
    }

    /**
     * Gets the players current username.
     *
     * @return current username.
     */
    public String getUsername() {
        return playerInformation.getUsername();
    }

    /**
     * Returns the player's current username.
     */
    @Override
    public String toString() {
        return playerInformation.getUsername();
    }

    @Override
    public void handleOutgoingHit(final Entity target, final Hit hit) {
        if (target == null || hit == null) {
            return;
        }
        if (target.getHitpoints() - hit.getDamage() <= 0) {
            if (target instanceof NPC) {
                handleNpcKill((NPC) target, hit);
            } else {
                handlePlayerKill((Player) target, hit);
            }
        }
        //controllerManager.processOutgoingHit(target, hit); UNUSED
    }

    private void handleNpcKill(final NPC target, final Hit hit) {
        if (getNumericAttribute("demon_kills").intValue() < 100 && CombatUtilities.isDemon(target)) {
            val weapon = getEquipment().getId(EquipmentSlot.WEAPON);
            if (weapon != 2402) { //silverlight
                return;
            }
            if (!hit.getHitType().equals(HitType.MELEE)) {
                return;
            }
            addAttribute("demon_kills", getNumericAttribute("demon_kills").intValue() + 1);
            val kills = getNumericAttribute("demon_kills").intValue();
            if (kills % 25 == 0 && kills < 100) {
                val remaining = 100 - kills;
                sendMessage("You've reached a demon kill checkpoint! You need to kill " + remaining + " more demon" + (kills == 1 ?
                        "" :
                        "s") + " to upgrade your Silverlight.");
            } else if (kills == 100) {
                getEquipment().set(EquipmentSlot.WEAPON, new Item(6746));
                getUpdateFlags().flag(UpdateFlag.APPEARANCE);
                sendMessage("You've reached 100 demon kills, your Silverlight has been upgraded into a Darklight!");
            }
        }
    }

    private void handlePlayerKill(final Player target, final Hit hit) {

    }

    public void refreshTitles() {
        //setNametag(0,
        //       (!privilege.equals(Privilege.PLAYER) ? privilege.getCrown() + " " : "") + (!gameMode.equals
        //       (GameMode.REGULAR) ? gameMode.getCrown() + GameMode.getTitle(this) + " " : ""));
    }

    public void setPrivilege(final Privilege privilege) {
        this.privilege = privilege;
        val optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            val plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("Privilege"), "Privilege: " + "<col=ffffff>" + privilege.getCrown() + privilege.toString() + "</col>");
        }
    }

    public void setExperienceMode(final ExperienceMode mode) {
        experienceMode = mode;
        val optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            val plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("XP rate"), "XP rate: " + "<col=ffffff>" + experienceMode.getRate() + "x</col>");
        }
    }

    public Optional<Raid> getRaid() {
        if (isNulled()) {
            return Optional.empty();
        }
        val channel = settings.getChannel();
        if (channel == null) {
            return Optional.empty();
        }
        val party = channel.getRaidParty();
        if (party == null) {
            return Optional.empty();
        }
        val raid = party.getRaid();
        if (raid == null) {
            return Optional.empty();
        }
        if (!raid.getPlayers().contains(this)) {
            return Optional.empty();
        }
        return Optional.of(raid);
    }

    public void setGameMode(final GameMode mode) {
        if (gameMode != GameMode.REGULAR && mode == GameMode.REGULAR) {
            if (getNumericAttribute(GameSetting.HIDE_ITEMS_YOU_CANT_PICK.toString()).intValue() == 1) {
                GameSetting.HIDE_ITEMS_YOU_CANT_PICK.handleSetting(this);
            }
        }
        gameMode = mode;

        varManager.sendBit(1777, gameMode.ordinal());
        val optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            val plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("Game Mode"), "Mode: " + "<col=ffffff>" + gameMode.getCrown() + gameMode.toString() + "</col>");
        }
    }

    public void setMemberRank(final MemberRank rank) {
        memberRank = rank;
        val optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            val plugin = optionalPlugin.get();
            packetDispatcher.sendComponentText(plugin.getInterface(), plugin.getComponent("Member Rank"), "Member: " + "<col=ffffff>" + memberRank.getCrown() + memberRank.toString().replace(" Member", "") + "</col>");
        }
        varManager.sendBit(16000, memberRank.eligibleTo(MemberRank.SAPPHIRE_MEMBER) ? 1 : 0);
    }

    public boolean isIronman() {
        return !gameMode.equals(GameMode.REGULAR);
    }

    public boolean containsItem(final int id) {
        return containsItem(new Item(id));
    }

    public boolean containsAnyItem(final int... ids) {
        for (val id : ids) {
            return containsItem(id);
        }
        return false;
    }

    public boolean containsAny(final int... ids) {
        boolean contains = false;
        for (val id : ids) {
            if (containsItem(id)) {
                contains = true;
            }
        }
        return contains;
    }

    public boolean containsItem(final Item item) {
        for (val i : inventory.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (val i : equipment.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (val i : bank.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (val i : retrievalService.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (val i : runePouch.getContainer().getItems().values()) {
            if (i != null && i.getId() == item.getId()) {
                return true;
            }
        }
        for (val i : privateStorage.getContainer().getItems().values()) {
            if (i != null && i.getId() == item.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean carryingAny(final Collection<Integer> ids) {
        for (val id : ids) {
            if (carryingItem(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean carryingAny(final int... ids) {
        for (val id : ids) {
            if (carryingItem(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean carryingItem(final int id) {
        return carryingItem(new Item(id));
    }

    public boolean carryingItem(final Item item) {
        for (val i : inventory.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        for (val i : equipment.getContainer().getItems().values()) {
            if (i.getId() == item.getId()) {
                return true;
            }
        }
        return false;
    }

    public int getAmountOf(final int id) {
        int count = 0;
        for (val i : inventory.getContainer().getItems().values()) {
            if (i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (val i : equipment.getContainer().getItems().values()) {
            if (i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (val i : bank.getContainer().getItems().values()) {
            if (i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (val i : retrievalService.getContainer().getItems().values()) {
            if (i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (val i : runePouch.getContainer().getItems().values()) {
            if (i != null && i.getId() == id) {
                count += i.getAmount();
            }
        }
        for (val i : privateStorage.getContainer().getItems().values()) {
            if (i != null && i.getId() == id) {
                count += i.getAmount();
            }
        }
        return count;
    }

    public void removeItem(final Item item) {
        val wrappers = new ContainerWrapper[] { inventory, equipment };
        for (val wrapper : wrappers) {
            for (int slot = 0; slot < wrapper.getContainer().getSize(); slot++) {
                val i = wrapper.getItem(slot);
                if (i == null || i.getId() != item.getId()) {
                    continue;
                }
                wrapper.deleteItem(i);
                if (wrapper instanceof Equipment) {
                    getUpdateFlags().flag(UpdateFlag.APPEARANCE);
                }
            }
        }
        for (int slot = 0; slot < bank.getContainer().getSize(); slot++) {
            val i = bank.get(slot);
            if (i == null || i.getId() != item.getId()) {
                continue;
            }
            bank.remove(i);
        }
    }

    public boolean addWalkSteps(final Direction direction, final int distance, final int maxStepsCount, final boolean check) {
        val dest = getLocation().transform(direction, distance);
        return addWalkSteps(dest.getX(), dest.getY(), maxStepsCount, check);
    }

    @Override
    public boolean addWalkSteps(final int destX, final int destY, final int maxStepsCount, final boolean check) {
        final int[] lastTile = getLastWalkTile();
        int myX = lastTile[0];
        int myY = lastTile[1];
        int stepCount = 0;
        while (true) {
            stepCount++;
            if (myX < destX) {
                myX++;
            } else if (myX > destX) {
                myX--;
            }
            if (myY < destY) {
                myY++;
            } else if (myY > destY) {
                myY--;
            }
            if (!addWalkStep(myX, myY, lastTile[0], lastTile[1], check)) {
                return false;
            }
            if (stepCount == maxStepsCount) {
                return true;
            }
            lastTile[0] = myX;
            lastTile[1] = myY;
            if (lastTile[0] == destX && lastTile[1] == destY) {
                return true;
            }
        }

    }

    @Override
    public int getCombatLevel() {
        return skills.getCombatLevel();
    }

    public void sendPlayerOptions() {
        setPlayerOption(3, "Follow", false);
        setPlayerOption(4, "Trade with", false);
    }

    public void setPlayerOption(final int index, final String option, final boolean top) {
        options[index] = option;
        packetDispatcher.sendPlayerOption(index, option, top);
        if (options[index] != null) {
            if (options[index].equals("Attack") && (option == null || !option.equals("Attack"))) {
                setCanPvp(false);
            } else if (options[index].equals("Fight") && (option == null || !option.equals("Fight"))) {
                setCanPvp(false);
            }
        }
        if (Objects.equals(option, "Attack") || Objects.equals(option, "Fight")) {
            setCanPvp(true, option.equals("Fight"));
        }
    }

    public final OptionalInt findPlayerOption(@NotNull final String query) {
        for (int i = 0; i < options.length; i++) {
            if (Objects.equals(options[i], query)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    public String getIP() {
        return lastIP;
    }

    public String getMACAddress() {
        return lastMAC;
    }

    private static final Calendar thanksgivingStart, thanksgivingEnd;

    static {
        thanksgivingStart = Calendar.getInstance();
        thanksgivingEnd = Calendar.getInstance();
        thanksgivingStart.set(2019, Calendar.NOVEMBER, 28);
        thanksgivingEnd.set(2019, Calendar.DECEMBER, 8);
    }

    public void onLogin() {
        log.info("'" + getName() + "' has logged in.");
        log(LogLevel.INFO, "Logged in with IP: " + getSession().getChannel().remoteAddress());
        sendMessage("Welcome to " + GameConstants.SERVER_NAME + ".");
        varManager.refreshSerializedVars();
        val updateMessage = "Latest Update: " + Constants.UPDATE_LOG_BROADCAST + "|" + Constants.UPDATE_LOG_URL;
        if (getNumericAttribute(GameSetting.ALWAYS_SHOW_LATEST_UPDATE.toString()).intValue() == 1 || !Objects.equals(attributes.get("latest update message"), updateMessage)) {
            sendMessage(updateMessage, MessageType.GLOBAL_BROADCAST);
            addAttribute("latest update message", updateMessage);
        }

        if (Constants.WORLD_PROFILE.isBeta()) {
            sendMessage("This is a " + Colour.TURQOISE.wrap("Beta World") + "; your progress will not affect the main game.");
        }
        if (Constants.BOOSTED_XP) {
            sendMessage("<col=00FF00><shad=000000>Experience is boosted by 50% until " + new Date(BonusXpManager.expirationDate).toString() + "!</col></shad>");
        }
        if (Constants.BOOSTED_COX) {
            sendMessage("<col=00FF00><shad=000000>Chambers of Xeric is boosted until " + new Date(BonusCoxManager.expirationDateCox).toString() + "!</col></shad>");
        }
        if (WellOfGoodwill.BONUSSLAYER) {
            sendMessage("<col=00FF00><shad=000000>Slayer Points are boosted by 50% until " + new Date(WellOfGoodwill.expirationDateBonusSlayer).toString() + "!</col></shad>");
        }
        if (WellOfGoodwill.DEPOBOX) {
            sendMessage("<col=00FF00><shad=000000>Special deposit boxes around Zenyte are open until " + new Date(WellOfGoodwill.expirationDateDepo).toString() + "!</col></shad>");
        }
        if (WellOfGoodwill.BONUSPURPLES) {
            sendMessage("<col=00FF00><shad=000000>COX Purples are dropped at a higher rate until " + new Date(WellOfGoodwill.expirationDateBonusPurples).toString() + "!</col></shad>");
        }

        if (!attributes.containsKey("death timers info")) {
            attributes.put("death timers info", true);
            sendMessage(Colour.RS_GREEN.wrap("Info: Items lost on death will remain invisible on the ground for 3 minutes(boosted to 60 for UIM), " +
                    "followed by 5 minutes of visibility to everyone.")
            );
        }

        if (!attributes.containsKey("Thanksgiving 2019 event")) {
            val date = Calendar.getInstance();
            if (thanksgivingStart.before(date) && thanksgivingEnd.after(date)) {
                attributes.put("Thanksgiving 2019 event", true);
                sendMessage(Colour.RS_PURPLE.wrap("Congratulations! You have unlocked the 'Give Thanks' emote."));
            }
        }

        if (!attributes.containsKey("treasure trails broadcasting")) {
            attributes.put("treasure trails broadcasting", 1);
            if (getNumericAttribute(GameSetting.TREASURE_TRAILS_BROADCASTS.toString()).intValue() == 0) {
                GameSetting.TREASURE_TRAILS_BROADCASTS.handleSetting(this);
            }
        }

        varManager.sendVar(HalloweenUtils.COMPLETED_VARP, HalloweenUtils.isCompleted(this) ? 1 : 0);
        varManager.sendVar(GIVE_THANKS_VARP, attributes.containsKey("Thanksgiving 2019 event") ? 1 : 0);
        if (SplittingHeirs.progressedAtLeast(this, Stage.EVENT_COMPLETE)) {
            emotesHandler.unlock(Emote.AROUND_THE_WORLD_IN_EGGTY_DAYS);
            emotesHandler.unlock(Emote.RABBIT_HOP);
        }

        val christmasEventCompleted = AChristmasWarble.progressedAtLeast(this, AChristmasWarble.ChristmasWarbleProgress.EVENT_COMPLETE);
        varManager.sendBit(15024, christmasEventCompleted ? 1 : 0);
        varManager.sendBit(15025, christmasEventCompleted ? 1 : 0);
        varManager.sendBit(15026, christmasEventCompleted ? 1 : 0);
        varManager.sendBit(16000, memberRank.eligibleTo(MemberRank.SAPPHIRE_MEMBER) ? 1 : 0);
        varManager.sendBit(5597, christmasEventCompleted ? 1 : 0);
        varManager.sendBit(5598, christmasEventCompleted ? 1 : 0);
        if (!getAuthenticator().isEnabled()) {
            sendMessage(Colour.RED.wrap("You do not have 2FA enabled. Please enable it for extra account security and extra vote rewards!"));
        }
        PunishmentManager.isPunishmentActive(getUsername(), getIP(), getMACAddress(), PunishmentType.MUTE)
                .ifPresent(value -> sendMessage("You are currently " + value.toLoginString() + "."));
        syncTotalDonated();
        updateScopeInScene();
        setRun(isRun());
        //Invokes the xp multiplier refresh.
        setExperienceMultiplier(getCombatXPRate(), getSkillingXPRate());
        lastIP = this.playerInformation.getIpFromChannel();
        lastMAC = this.playerInformation.getMACFromChannel();
        refreshQuestPoints();
        inventory.refreshAll();
        equipment.refreshAll();
        skills.refresh();
        toxins.refresh();
        settings.refresh();
        bonuses.update();
        appearance.resetRenderAnimation();
        packetDispatcher.sendRunEnergy();
        sendPlayerOptions();
        MethodicPluginHandler.invokePlugins(ListenerType.LOGIN, this);
        PluginManager.post(new LoginEvent(this));
        controllerManager.login();
        GlobalAreaManager.update(this, true, false);
        World.updateEntityChunk(this, false);
        clip();
        LocationMap.add(this);
        val calendar = Calendar.getInstance();
        refreshGameClock();
        val ticksUntilNextMinute = TimeUnit.MILLISECONDS.toTicks(60000 - (((calendar.get(Calendar.SECOND) * 1000) + calendar.get(Calendar.MILLISECOND)) % 60000)) + 1;
        if (ticksUntilNextMinute > 1) {
            WorldTasksManager.schedule(this::refreshGameClock, (int) ticksUntilNextMinute);
        }
        if (isOnMobile()) {
            packetDispatcher.sendClientScript(2644);
            varManager.sendBit(Setting.CHATBOX_SCROLLBAR.getId(), 1);
            varManager.sendBit(Setting.TABS_TRANSPARENT.getId(), 0);
        }
        if (attributes.get("fixed respawn point teleport") == null) {
            attributes.put("fixed respawn point teleport", true);
            respawnPoint = RespawnPoint.EDGEVILLE;
        }

        if (isXPDropsWildyOnly()) {
            varManager.sendVar(3504, WildernessArea.isWithinWilderness(getX(), getY()) ? getSkillingXPRate() : 1);
        } else if (isXPDropsMultiplied()) {
            varManager.sendVar(3504, getSkillingXPRate());
        } else {
            varManager.sendVar(3504, 1);
        }

        packetDispatcher.resetCamera();

        //Blades by Urbi shop in Sophanem; Quest.
        varManager.sendBit(3275, 1);
        variables.onLogin();
        music.refreshListConfigs();
        if (World.isUpdating()) {
            send(new UpdateRebootTimer(World.getUpdateTimer()));
        }
        if (!getBooleanAttribute("registered")) {
            setLocation(GameConstants.REGISTRATION_LOCATION);
            if (getSettings().getChannelOwner() == null) {
                ClanManager.join(this, "Zenyte");
            }
        }
        int unreadMessageCount = getNumericAttribute("unread message count").intValue();
        if (unreadMessageCount > 0) {
            sendMessage("You currently have <col=ff0000>"
                    + unreadMessageCount
                    + "</col> unread message"
                    + (unreadMessageCount == 1 ? "" : "s") + "; visit the forums to check your inbox.");
        }
    }

    public void refreshGameClock() {
        if (this.isLoggedOut()) {
            return;
        }
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        val hours = calendar.get(Calendar.HOUR_OF_DAY);
        val minutes = calendar.get(Calendar.MINUTE);
        varManager.sendBit(8354, (hours * 60) + minutes);
    }

    public void onLobbyClose() {
        pollManager.loadAnsweredPolls();
        varManager.sendVar(1050, 90);// chivalry/piety
        varManager.sendBit(598, 2);
        prayerManager.refreshQuickPrayers();

        if (petId != -1 && PetWrapper.getByPet(petId) != null) {
            if (follower == null) {
                setFollower(new Follower(petId, this));
            }
        }
        /*
         * if (player.getHelmet() != null && player.getHelmet().getId() >= 5525 && player.getHelmet().getId() <= 5547) { final int bitId =
         * 599 + (player.getHelmet().getId() - 5525); player.getVarManager().sendBit(bitId, 1); }
         */
        combatDefinitions.refresh();
        socialManager.loadFriends();
        socialManager.loadIgnores();
        /*
         * final ClanChannel channel = player.getSettings().getChannel(); if (channel != null) { ClanManager.join(player,
         * channel.getOwner()); } else { ClanManager.join(player, "kris"); }
         */
        socialManager.updateStatus();
        farming.refresh();
        runePouch.getContainer().refresh(this);
        grandExchange.updateOffers();
        VarCollection.updateType(this, EventType.POST_LOGIN);

        send(new ChatFilterSettings(this));
        send(new SetPrivateChatFilter(this));
        dailyChallengeManager.notifyUnclaimedChallenges();
        MethodicPluginHandler.invokePlugins(ListenerType.LOBBY_CLOSE, this);
        if (isDead()) {
            sendDeath();
        }
    }

    public Duel getDuel() {
        if (duel != null && duel.getPlayer() != this) {
            val opponent = duel.getPlayer();
            duel.setPlayer(this);
            duel.setOpponent(opponent);
        }
        return duel;
    }

    public int getPrimaryIcon() {
        return privilege.getIcon();
    }

    public int getSecondaryIcon() {
        return gameMode.getIcon();
    }

    public int getTertiaryIcon() {
        return memberRank.getIcon();
    }

    public boolean isMember() {
        return !memberRank.equals(MemberRank.NONE);
    }

    public boolean isStaff() {
        return privilege.ordinal() >= Privilege.JUNIOR_MODERATOR.ordinal();
    }

    @RequiredArgsConstructor
    public enum StopType {
        ROUTE_EVENT(p -> {
            p.setRouteEvent(null);
            if (p.getFaceEntity() >= 0) {
                p.setFaceEntity(null);
            }
        }),
        INTERFACES(p -> {
            p.getTemporaryAttributes().remove("skillDialogue");
            p.getInterfaceHandler().closeInterfaces();
        }),
        WORLD_MAP(p -> {
            if (p.getWorldMap().isVisible() && p.getWorldMap().isFullScreen()) {
                p.getWorldMap().close();
            }
        }),
        WALK(p -> {
            p.getPacketDispatcher().resetMapFlag();
            p.resetWalkSteps();
        }),
        ACTIONS(p -> {
            p.getActionManager().forceStop();
            p.getDelayedActionManager().forceStop();
        }),
        ANIMATIONS(p -> p.setAnimation(Animation.STOP));

        private final Consumer<Player> consumer;
    }

    @AllArgsConstructor
    private static final class ProjPacket {
        private final Location sender;
        private final GamePacketEncoder packet;
    }

}
