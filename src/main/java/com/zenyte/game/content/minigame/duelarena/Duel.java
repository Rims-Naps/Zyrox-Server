package com.zenyte.game.content.minigame.duelarena;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.zenyte.game.HintArrow;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.event.christmas2019.ChristmasConstants;
import com.zenyte.game.content.follower.PetWrapper;
import com.zenyte.game.content.minigame.duelarena.area.*;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.RSPolygon;
import mgi.types.config.items.ItemDefinitions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

import static com.zenyte.game.constants.GameInterface.DUEL_CONFIRMATION;
import static com.zenyte.game.content.minigame.duelarena.DuelSetting.*;

/**
 * @author Tommeh | 28-11-2018 | 20:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@Setter
@Slf4j
public class Duel {

    public static final ArenaArea NORMAL_ARENA = new NormalArenaArea();
    public static final ArenaArea NO_MOVEMENT_ARENA = new NoMovementArena();
    public static final ArenaArea OBSTACLES_ARENA = new ObstaclesArenaArea();
    public static final Area LOBBY = new DuelArenaLobbyArea();
    public static final int SCOREBOARD_INTERFACE = 108, WINNINGS_INTERFACE = 372, CONFIRMATION_INTERFACE = 476, STAKING_INTERFACE = 481, SETTINGS_INTERFACE = 482;
    public static final ImmutableList<Integer> FUN_WEAPONS = ImmutableList.of(8650, 8652, 8654, 8656, 8658, 8660, 8662, 8664, 8666, 8668, 8670, 8672, 8274, 8676, 8678, 8680, 6082, 2460, 2462, 2464,
            2466, 2468, 2470, 2472, 2474, 2476, 751, 6541, 10150, 3695, 6773, 6774, 6775, 6776, 6777, 6778, 6779, 4566, 1419, 10501, 4086, 10487, ChristmasConstants.CHRISTMAS_SCYTHE);
    private static final ImmutableMap<Integer, InterfacePosition> CLOSED_TABS = ImmutableMap.<Integer, InterfacePosition>builder().
            put(593, InterfacePosition.COMBAT_TAB).put(320, InterfacePosition.SKILLS_TAB).
            put(399, InterfacePosition.JOURNAL_TAB_HEADER).put(149, InterfacePosition.INVENTORY_TAB).
            put(387, InterfacePosition.EQUIPMENT_TAB).put(541, InterfacePosition.PRAYER_TAB).
            put(218, InterfacePosition.SPELLBOOK_TAB).put(261, InterfacePosition.SETTINGS_TAB).
            put(216, InterfacePosition.EMOTE_TAB).build();
    private static final ForceTalk FIGHT = new ForceTalk("FIGHT!");

    private Player player, opponent;
    private Map<Player, Container> containers;
    private Map<Player, DuelStage> stages;
    private Map<Player, List<Item>> ammunitions;
    private Map<Player, Long> waitTimers;
    private Map<Player, Location> startLocations;
    private ArenaArea arena;
    private int settings;
    private boolean countdown;
    @Getter
    private boolean completed;

    public Duel(final Player player, final Player opponent) {
        this.player = player;
        this.opponent = opponent;
        containers = new HashMap<>(2);
        stages = new HashMap<>(2);
        ammunitions = new HashMap<>(2);
        waitTimers = new HashMap<>(2);
        startLocations = new HashMap<>(2);
        stages.put(player, DuelStage.NONE);
        stages.put(opponent, DuelStage.NONE);
        ammunitions.put(player, new ArrayList<>());
        ammunitions.put(opponent, new ArrayList<>());
        player.setDuel(this);
        opponent.setDuel(this);
    }

    public static final void beforeShutdown() {
        try {
            for (val player : World.getPlayers()) {
                if (player.getAreaManager() == null) {
                    continue;
                }
                if (player.getArea() instanceof ArenaArea) {
                    val duel = player.getDuel();
                    val opponent = duel.getOpponent();
                    duel.containers.get(player).getItems().values().forEach(item -> player.getInventory().addItem(item).onFailure(remaining -> player.getBank().add(remaining)));
                    duel.containers.get(opponent).getItems().values().forEach(item -> opponent.getInventory().addItem(item).onFailure(remaining -> opponent.getBank().add(remaining)));
                    duel.getAmmunitions().get(player).stream().filter(Objects::nonNull).forEach(ammo -> player.getInventory().addItem(ammo).onFailure(i -> player.getBank().add(i)));
                    duel.getAmmunitions().get(opponent).stream().filter(Objects::nonNull).forEach(ammo -> opponent.getInventory().addItem(ammo).onFailure(i -> opponent.getBank().add(i)));
                    duel.containers.get(player).clear();
                    duel.containers.get(opponent).clear();
                    player.forceLocation(new Location(getRandomPoint(LOBBY.polygons()[0], 0, location -> World.isFloorFree(location, 1))));
                    opponent.forceLocation(new Location(getRandomPoint(LOBBY.polygons()[0], 0, location -> World.isFloorFree(location, 1))));
                    GlobalAreaManager.update(player, false, false);
                    GlobalAreaManager.update(opponent, false, false);
                    duel.reset(player);
                    duel.reset(opponent);
                    player.setDuel(null);
                    opponent.setDuel(null);
                }
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public static Location getRandomPoint(final RSPolygon polygon, final int plane, final Predicate<Location> predicate) {
        if (!polygon.getPlanes().contains(plane)) throw new RuntimeException("Polygon does not cover plane " + plane);
        val poly = polygon.getPolygon();
        val box = poly.getBounds2D();
        int count = 1000;
        Location location = new Location(0);
        do {
            if (--count <= 0) {
                throw new RuntimeException("Unable to find a valid point in polygon.");
            }
            location.setLocation((int) box.getMinX() + Utils.random((int) box.getWidth()), (int) box.getMinY() + Utils.random((int) box.getHeight()), plane);
        } while (!poly.contains(location.getX(), location.getY()) || !predicate.test(location));
        return location;
    }

    public void setRules(final int settings) {
        for (val setting : DuelSetting.SETTINGS.values()) {
            val currentValue = Utils.getShiftedBoolean(this.settings, setting.getBit());
            val value = Utils.getShiftedBoolean(settings, setting.getBit());
            if (currentValue != value) {
                toggleRule(setting);
            }
        }
    }

    public void toggleRule(final DuelSetting setting) {
        if (player == null || opponent == null || !player.inArea("Duel Arena") || !opponent.inArea("Duel Arena")) {
            return;
        }
        switch (setting) {
            case NO_MELEE:
                if (hasRule(NO_WEAPON_SWITCH)) {
                    player.sendMessage("You can't restrict attack types and have no weapon switching.");
                    player.getVarManager().sendVar(286, player.getVarManager().getValue(286));
                    return;
                }
                if (hasRule(NO_MAGIC) && hasRule(NO_RANGED)) {
                    player.sendMessage("You can't have no melee, no magic, no ranged, how would you fight?");
                    return;
                }
                break;
            case NO_MAGIC:
                if (hasRule(NO_WEAPON_SWITCH)) {
                    player.sendMessage("You can't restrict attack types and have no weapon switching.");
                    player.getVarManager().sendVar(286, player.getVarManager().getValue(286));
                    return;
                }
                if (hasRule(NO_MELEE) && hasRule(NO_RANGED)) {
                    player.sendMessage("You can't have no melee, no magic, no ranged, how would you fight?");
                    return;
                }
                break;
            case NO_RANGED:
                if (hasRule(NO_WEAPON_SWITCH)) {
                    player.sendMessage("You can't restrict attack types and have no weapon switching.");
                    player.getVarManager().sendVar(286, player.getVarManager().getValue(286));
                    return;
                }
                if (hasRule(NO_MELEE) && hasRule(NO_MAGIC)) {
                    player.sendMessage("You can't have no melee, no magic, no ranged, how would you fight?");
                    return;
                }
                break;
            case NO_WEAPON_SWITCH:
                if (hasRule(NO_MELEE) || hasRule(NO_MAGIC) || hasRule(NO_RANGED)) {
                    player.sendMessage("You can't restrict attack types and have no weapon switching.");
                    player.getVarManager().sendVar(286, player.getVarManager().getValue(286));
                    return;
                }
                break;
            case NO_MOVEMENT:
                if (hasRule(OBSTACLES)) {
                    player.sendMessage("You can't have obstacles if you want No Movement.");
                    return;
                }
                break;
            case OBSTACLES:
                if (hasRule(NO_MOVEMENT)) {
                    player.sendMessage("You can't have No Movement in an arena with obstacles.");
                    return;
                }
                break;
            default:
                break;
        }
        settings = Utils.getShiftedValue(settings, setting.getBit());
        player.getVarManager().sendVar(286, settings);
        opponent.getVarManager().sendVar(286, settings);
        opponent.getPacketDispatcher().sendClientScript(968, 31588458, setting.getBit());
        opponent.sendMessage("Duel Option change - " + setting.getName() + (hasRule(setting) ? " ON!" : " OFF!"));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 118, "<col=ff0000>An option has changed - check before accepting!");
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 118, "");
        player.getPacketDispatcher().sendComponentVisibility(482, 114, !(settings == player.getNumericAttribute("duelPresetSettings").intValue()));
        player.getPacketDispatcher().sendComponentVisibility(482, 115, !(settings == player.getNumericAttribute("lastDuelSettings").intValue()));
        opponent.getPacketDispatcher().sendComponentVisibility(482, 114, !(settings == opponent.getNumericAttribute("duelPresetSettings").intValue()));
        opponent.getPacketDispatcher().sendComponentVisibility(482, 115, !(settings == opponent.getNumericAttribute("lastDuelSettings").intValue()));
        stages.put(player, DuelStage.NONE);
        stages.put(opponent, DuelStage.NONE);
        waitTimers.put(opponent, Utils.currentTimeMillis() + 3000);
    }

    public void openChallenge() {
        if (player == null || opponent == null) {
            return;
        }
        player.stopAll();
        player.getVarManager().sendVar(286, settings = 0);
        player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, SETTINGS_INTERFACE);
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 35, "Dueling with: " + opponent.getPlayerInformation().getDisplayname());
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 34, Utils.getLevelColour(player.getSkills().getCombatLevel(), opponent.getSkills().getCombatLevel()) + "Combat level: " + opponent.getSkills().getCombatLevel());
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 8, Utils.getLevelColour(player.getSkills().getLevel(Skills.ATTACK), opponent.getSkills().getLevel(Skills.ATTACK)) + opponent.getSkills().getLevel(Skills.ATTACK));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 9, Utils.getLevelColour(player.getSkills().getLevelForXp(Skills.ATTACK), opponent.getSkills().getLevelForXp(Skills.ATTACK)) + opponent.getSkills().getLevelForXp(Skills.ATTACK));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 12, Utils.getLevelColour(player.getSkills().getLevel(Skills.STRENGTH), opponent.getSkills().getLevel(Skills.STRENGTH)) + opponent.getSkills().getLevel(Skills.STRENGTH));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 13, Utils.getLevelColour(player.getSkills().getLevelForXp(Skills.STRENGTH), opponent.getSkills().getLevelForXp(Skills.STRENGTH)) + opponent.getSkills().getLevelForXp(Skills.STRENGTH));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 16, Utils.getLevelColour(player.getSkills().getLevel(Skills.DEFENCE), opponent.getSkills().getLevel(Skills.DEFENCE)) + opponent.getSkills().getLevel(Skills.DEFENCE));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 17, Utils.getLevelColour(player.getSkills().getLevelForXp(Skills.DEFENCE), opponent.getSkills().getLevelForXp(Skills.DEFENCE)) + opponent.getSkills().getLevelForXp(Skills.DEFENCE));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 20, Utils.getLevelColour(player.getSkills().getLevel(Skills.HITPOINTS), opponent.getSkills().getLevel(Skills.HITPOINTS)) + opponent.getSkills().getLevel(Skills.HITPOINTS));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 21, Utils.getLevelColour(player.getSkills().getLevelForXp(Skills.HITPOINTS), opponent.getSkills().getLevelForXp(Skills.HITPOINTS)) + opponent.getSkills().getLevelForXp(Skills.HITPOINTS));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 24, Utils.getLevelColour(player.getPrayerManager().getPrayerPoints(), opponent.getPrayerManager().getPrayerPoints()) + opponent.getPrayerManager().getPrayerPoints());
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 25, Utils.getLevelColour(player.getSkills().getLevelForXp(Skills.PRAYER), opponent.getSkills().getLevelForXp(Skills.PRAYER)) + opponent.getSkills().getLevelForXp(Skills.PRAYER));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 28, Utils.getLevelColour(player.getSkills().getLevel(Skills.RANGED), opponent.getSkills().getLevel(Skills.RANGED)) + opponent.getSkills().getLevel(Skills.RANGED));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 29, Utils.getLevelColour(player.getSkills().getLevelForXp(Skills.RANGED), opponent.getSkills().getLevelForXp(Skills.RANGED)) + opponent.getSkills().getLevelForXp(Skills.RANGED));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 32, Utils.getLevelColour(player.getSkills().getLevel(Skills.MAGIC), opponent.getSkills().getLevel(Skills.MAGIC)) + opponent.getSkills().getLevel(Skills.MAGIC));
        player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 33, Utils.getLevelColour(player.getSkills().getLevelForXp(Skills.MAGIC), opponent.getSkills().getLevelForXp(Skills.MAGIC)) + opponent.getSkills().getLevelForXp(Skills.MAGIC));
        // player.setCloseInterfacesEvent(() -> close(true));


        opponent.stopAll();
        opponent.getVarManager().sendVar(286, settings = 0);
        opponent.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, SETTINGS_INTERFACE);
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 35, "Dueling with: " + player.getPlayerInformation().getDisplayname());
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 34, Utils.getLevelColour(opponent.getSkills().getCombatLevel(), player.getSkills().getCombatLevel()) + "Combat level: " + player.getSkills().getCombatLevel());
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 8, Utils.getLevelColour(opponent.getSkills().getLevel(Skills.ATTACK), player.getSkills().getLevel(Skills.ATTACK)) + player.getSkills().getLevel(Skills.ATTACK));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 9, Utils.getLevelColour(opponent.getSkills().getLevelForXp(Skills.ATTACK), player.getSkills().getLevelForXp(Skills.ATTACK)) + player.getSkills().getLevelForXp(Skills.ATTACK));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 12, Utils.getLevelColour(opponent.getSkills().getLevel(Skills.STRENGTH), player.getSkills().getLevel(Skills.STRENGTH)) + player.getSkills().getLevel(Skills.STRENGTH));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 13, Utils.getLevelColour(opponent.getSkills().getLevelForXp(Skills.STRENGTH), player.getSkills().getLevelForXp(Skills.STRENGTH)) + player.getSkills().getLevelForXp(Skills.STRENGTH));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 16, Utils.getLevelColour(opponent.getSkills().getLevel(Skills.DEFENCE), player.getSkills().getLevel(Skills.DEFENCE)) + player.getSkills().getLevel(Skills.DEFENCE));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 17, Utils.getLevelColour(opponent.getSkills().getLevelForXp(Skills.DEFENCE), player.getSkills().getLevelForXp(Skills.DEFENCE)) + player.getSkills().getLevelForXp(Skills.DEFENCE));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 20, Utils.getLevelColour(opponent.getSkills().getLevel(Skills.HITPOINTS), player.getSkills().getLevel(Skills.HITPOINTS)) + player.getSkills().getLevel(Skills.HITPOINTS));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 21, Utils.getLevelColour(opponent.getSkills().getLevelForXp(Skills.HITPOINTS), player.getSkills().getLevelForXp(Skills.HITPOINTS)) + player.getSkills().getLevelForXp(Skills.HITPOINTS));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 24, Utils.getLevelColour(opponent.getPrayerManager().getPrayerPoints(), player.getPrayerManager().getPrayerPoints()) + player.getPrayerManager().getPrayerPoints());
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 25, Utils.getLevelColour(opponent.getSkills().getLevelForXp(Skills.PRAYER), player.getSkills().getLevelForXp(Skills.PRAYER)) + player.getSkills().getLevelForXp(Skills.PRAYER));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 28, Utils.getLevelColour(opponent.getSkills().getLevel(Skills.RANGED), player.getSkills().getLevel(Skills.RANGED)) + player.getSkills().getLevel(Skills.RANGED));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 29, Utils.getLevelColour(opponent.getSkills().getLevelForXp(Skills.RANGED), player.getSkills().getLevelForXp(Skills.RANGED)) + player.getSkills().getLevelForXp(Skills.RANGED));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 32, Utils.getLevelColour(opponent.getSkills().getLevel(Skills.MAGIC), player.getSkills().getLevel(Skills.MAGIC)) + player.getSkills().getLevel(Skills.MAGIC));
        opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 33, Utils.getLevelColour(opponent.getSkills().getLevelForXp(Skills.MAGIC), player.getSkills().getLevelForXp(Skills.MAGIC)) + player.getSkills().getLevelForXp(Skills.MAGIC));

        startLocations.put(player, player.getLocation());
        startLocations.put(opponent, opponent.getLocation());
        //opponent.setCloseInterfacesEvent(() -> close(true));
    }

    public final void confirm(final DuelStage stage) {
        if (!player.inArea("Duel Arena") || !opponent.inArea("Duel Arena")) {
            return;
        }
        val timer = waitTimers.get(player);
        if (timer != null && timer > Utils.currentTimeMillis()) {
            return;
        }
        switch (stage) {
            case SETTINGS:
                player.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 118, "Waiting for other player...");
                opponent.getPacketDispatcher().sendComponentText(SETTINGS_INTERFACE, 118, "Other player has accepted.");
                break;
            case STAKE:
                player.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 81, "<col=ff0000>Waiting for other player...</col>");
                opponent.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 81, "<col=ff0000>Other player has accepted.</col>");
                player.getInterfaceHandler().closeInput();
                opponent.getInterfaceHandler().closeInput();
                break;
            case CONFIRMATION:
                player.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 51, "<col=ff0000>Waiting for other player...</col>");
                opponent.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 51, "Other player has accepted.");
                break;
        }
        stages.put(player, stage);
        val opponentStage = stages.get(opponent);
        if (opponentStage == null) {
            return;
        }
        if (stage.equals(DuelStage.SETTINGS) && opponentStage.equals(DuelStage.SETTINGS)) {
            containers.put(player, new Container(ContainerPolicy.NORMAL, ContainerType.DUEL_STAKE, Optional.of(player)));
            containers.put(opponent, new Container(ContainerPolicy.NORMAL, ContainerType.DUEL_STAKE, Optional.of(opponent)));
            if (hasRule(LEFT_HAND) || hasRule(RIGHT_HAND)) {
                player.sendMessage("Beware: You won't be able to use two-handed weapons such as bows.");
                opponent.sendMessage("Beware: You won't be able to use two-handed weapons such as bows.");
            }
            Arrays.asList(player, opponent).forEach(GameInterface.DUEL_STAKING::open);
            /*Arrays.asList(player, opponent).forEach(p -> {
                p.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, STAKING_INTERFACE);
                p.getInterfaceHandler().sendInterface(InterfacePosition.SINGLE_TAB, 421);
                p.getPacketDispatcher().sendClientScript(149, 27590657, 93, 4, 7, 0, -1, "Use", "", "", "", "");
                p.getPacketDispatcher().sendComponentSettings(421, 1, 0, 27, AccessMask.CLICK_OP1, AccessMask.CLICK_OP10);
                p.getPacketDispatcher().sendComponentSettings(STAKING_INTERFACE, 19, 0, 5, AccessMask.CLICK_OP1);
                p.getPacketDispatcher().sendComponentSettings(STAKING_INTERFACE, 20, 0, 5, AccessMask.CLICK_OP1);
                p.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 24, opponent.getName() + "'s stake:");
                p.getPacketDispatcher().sendUpdateItemContainer(containers.get(player));
                p.getPacketDispatcher().sendUpdateItemContainer(containers.get(opponent));
            });
            player.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 24, opponent.getName() + "'s stake:");
            opponent.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 24, player.getName() + "'s stake:");*/
            //updateInventories();
        } else if (stage.equals(DuelStage.STAKE) && opponentStage.equals(DuelStage.STAKE)) {
            val container = containers.get(player);
            val opponentContainer = containers.get(opponent);
            int size = 0, sizePlayer = 0, sizeOpponent = 0;
            for (val item : Utils.concatenate(container.getItems().values(), opponentContainer.getItems().values())) {
                if (item.getDefinitions().isStackable() && player.getInventory().containsItem(item)) {
                    continue;
                }
                size++;
            }
            for (int i = HEAD.ordinal(); i <= AMMUNITION.ordinal(); i++) {
                if (Utils.getShiftedBoolean(settings, VALUES[i].getBit())) {
                    val slot = i == LEG.ordinal() ? 7 : i == HAND.ordinal() ? 9 : i == FEET.ordinal() ? 10 : i == RING.ordinal() ? 12 : i == AMMUNITION.ordinal() ? 13 : i - 13;
                    if (player.getEquipment().getId(slot) != -1) {
                        sizePlayer++;
                    }
                    if (opponent.getEquipment().getId(slot) != -1) {
                        sizeOpponent++;
                    }
                }
            }
            for (val item : container.getItems().values()) {
                if (opponent.getInventory().getAmountOf(item.getId()) + item.getAmount() < 0) {
                    opponent.sendMessage("You are holding too many of the same item to continue this stake.");
                    player.sendMessage("Other player has declined the duel.");
                    close(false);
                    return;
                }
            }
            for (val item : opponentContainer.getItems().values()) {
                if (player.getInventory().getAmountOf(item.getId()) + item.getAmount() < 0) {
                    player.sendMessage("You are holding too many of the same item to continue this stake.");
                    opponent.sendMessage("Other player has declined the duel.");
                    close(false);
                    return;
                }
            }
            if (player.getInventory().getFreeSlots() < size + sizePlayer) {
                player.sendMessage("You don't have enough inventory space to accept this duel.");
                opponent.sendMessage("Other player has declined the duel.");
                close(false);
                return;
            }
            if (opponent.getInventory().getFreeSlots() < size + sizeOpponent) {
                opponent.sendMessage("You don't have enough inventory space to accept this duel.");
                player.sendMessage("Other player has declined the duel.");
                close(false);
                return;
            }
            val details = new StringBuilder();
            val detailsOpponent = new StringBuilder();
            DUEL_CONFIRMATION.open(player);
            DUEL_CONFIRMATION.open(opponent);
            player.getInterfaceHandler().closeInterface(InterfacePosition.SINGLE_TAB);
            opponent.getInterfaceHandler().closeInterface(InterfacePosition.SINGLE_TAB);
            CLOSED_TABS.forEach((id, type) -> {
                player.getInterfaceHandler().closeInterface(type);
                opponent.getInterfaceHandler().closeInterface(type);
            });
            details.append(player.getName()).append("<br>Combat level: ").append(player.getSkills().getCombatLevel()).append("<br>");
            detailsOpponent.append(opponent.getName()).append("<br>Combat level: ").append(opponent.getSkills().getCombatLevel()).append("<br>");
            for (int i = 0; i < 7; i++) {
                details.append(Skills.getSkillName(i)).append(": ").append(player.getSkills().getLevel(i)).append("/").append(player.getSkills().getLevelForXp(i)).append("<br>");
                detailsOpponent.append(Skills.getSkillName(i)).append(": ").append(opponent.getSkills().getLevel(i)).append("/").append(opponent.getSkills().getLevelForXp(i)).append("<br>");
            }
            player.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 66, detailsOpponent.toString());
            opponent.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 66, details.toString());
            player.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 81, opponentContainer.getAmountOf(13204));
            player.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 86, opponentContainer.getAmountOf(995));
            opponent.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 81, container.getAmountOf(13204));
            opponent.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 86, container.getAmountOf(995));
            player.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 90, container.getAmountOf(13204));
            player.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 95, container.getAmountOf(995));
            opponent.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 90, opponentContainer.getAmountOf(13204));
            opponent.getPacketDispatcher().sendComponentText(CONFIRMATION_INTERFACE, 95, opponentContainer.getAmountOf(995));
            updateValues(CONFIRMATION_INTERFACE, 55, 60);
        } else if (stage.equals(DuelStage.CONFIRMATION) && opponentStage.equals(DuelStage.CONFIRMATION)) {
            if (player.getFollower() != null) {
                if (!player.getInventory().hasFreeSlots()) {
                    player.sendMessage("You currently have a follower, either pick it up or get some inventory space first.");
                    close(false);
                    return;
                }
                val pet = PetWrapper.getByPet(player.getFollower().getId());
                player.getInventory().addItem(pet.itemId(), 1);
                player.getFollower().finish();
                player.setFollower(null);
            }
            if (opponent.getFollower() != null) {
                if (!opponent.getInventory().hasFreeSlots()) {
                    opponent.sendMessage("You currently have a follower, either pick it up or get some inventory space first.");
                    close(false);
                    return;
                }
                val pet = PetWrapper.getByPet(opponent.getFollower().getId());
                opponent.getInventory().addItem(pet.itemId(), 1);
                opponent.getFollower().finish();
                opponent.setFollower(null);
            }
            initiateDuel();
        }
    }

    private boolean prepareDuel(final Player player) {
        if (Utils.getShiftedBoolean(settings, NO_FUN_WEAPONS.getBit())) {
            for (val id : FUN_WEAPONS) {
                if (player.getEquipment().getId(EquipmentSlot.WEAPON) == id && !player.getEquipment().unequipItem(EquipmentSlot.WEAPON.getSlot())) {
                    player.sendMessage("Not enough space in your inventory.");
                    return false;
                }
            }
        }
        for (int i = HEAD.ordinal(); i <= AMMUNITION.ordinal(); i++) {
            if (Utils.getShiftedBoolean(settings, VALUES[i].getBit())) {
                val slot = i == LEG.ordinal() ? 7 : i == HAND.ordinal() ? 9 : i == FEET.ordinal() ? 10 : i == RING.ordinal() ? 12 : i == AMMUNITION.ordinal() ? 13 : i - 13;

                if (slot == EquipmentSlot.SHIELD.getSlot()) {
                    if (hasRule(RIGHT_HAND)) {
                        val weapon = player.getWeapon();
                        if (weapon != null) {
                            if (weapon.getDefinitions().isTwoHanded()) {
                                if (!player.getEquipment().unequipItem(EquipmentSlot.WEAPON.getSlot())) {
                                    player.sendMessage("Not enough space in your inventory.");
                                    return false;
                                }
                            }
                        }
                    }
                }

                if (player.getEquipment().getId(slot) != -1 && !player.getEquipment().unequipItem(slot)) {
                    player.sendMessage("Not enough space in your inventory.");
                    return false;
                }
            }
        }
        player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
        player.getInterfaceHandler().closeInterface(InterfacePosition.SINGLE_TAB);
        player.sendMessage("Accepted stake and duel options.");
        return true;
    }

    public void resetAttributes(final Player player) {
        if (player.getAttributes().remove("vengeance") != null) {
            player.sendMessage("Your Vengeance has been cleared!");
        }
        reset(player);
    }

    private void initiateDuel() {
        if (!prepareDuel(player) || !prepareDuel(opponent)) {
            stages.put(player, DuelStage.NONE);
            stages.put(opponent, DuelStage.NONE);
            close(true);
            return;
        }
        arena = hasRule(OBSTACLES) ?
                OBSTACLES_ARENA :
                hasRule(NO_MOVEMENT) ?
                NO_MOVEMENT_ARENA :
                NORMAL_ARENA;

        try {
            Location positionOpponent = getRandomPoint(arena.polygons()[0], 0, location -> hasRule(NO_MOVEMENT) || World.isFloorFree(location, 1));
            val position = getRandomPoint(arena.polygons()[0], 0, location -> hasRule(NO_MOVEMENT) || World.isFloorFree(location, 1));
            int counter = 0;
            while (position.equals(positionOpponent) && counter++ < 1000) {
                positionOpponent = getRandomPoint(arena.polygons()[0], 0, location -> hasRule(NO_MOVEMENT) || World.isFloorFree(location, 1));
            }
            player.setLocation(position);
            if(player.getInterfaceHandler().isPresent(GameInterface.BANK)) {
                player.getInterfaceHandler().closeInterface(GameInterface.BANK);
            }
            if (hasRule(NO_MOVEMENT)) {
                try {
                    val tile = getLocationNorthOrSouth(arena.polygons()[0], position);
                    opponent.setLocation(tile);
                } catch (final Exception e) {
                    log.error(Strings.EMPTY, e);
                    opponent.setLocation(positionOpponent);
                }
            } else {
                opponent.setLocation(positionOpponent);
            }
            if(opponent.getInterfaceHandler().isPresent(GameInterface.BANK)) {
                opponent.getInterfaceHandler().closeInterface(GameInterface.BANK);
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
            player.sendMessage("Failed to initiate the duel.");
            opponent.sendMessage("Failed to initiate the duel.");
            close(false);
            return;
        }
        player.getVarManager().sendVar(1075, opponent.getIndex());
        opponent.getVarManager().sendVar(1075, player.getIndex());
        player.getPacketDispatcher().sendHintArrow(new HintArrow(opponent));
        opponent.getPacketDispatcher().sendHintArrow(new HintArrow(player));
        player.getInterfaceHandler().closeInterface(InterfacePosition.SINGLE_TAB);
        opponent.getInterfaceHandler().closeInterface(InterfacePosition.SINGLE_TAB);
        player.getInterfaceHandler().closeInput();
        opponent.getInterfaceHandler().closeInput();
        player.addAttribute("lastDuelSettings", settings);
        opponent.addAttribute("lastDuelSettings", settings);

        player.addAttribute("lastDuelStake", new Item[] { new Item(995, containers.get(player).getAmountOf(995)), new Item(13204, containers.get(player).getAmountOf(13204)) });
        opponent.addAttribute("lastDuelStake", new Item[] { new Item(995, containers.get(opponent).getAmountOf(995)), new Item(13204, containers.get(opponent).getAmountOf(13204)) });

        restoreTabs(player);
        restoreTabs(opponent);

        player.getInterfaceHandler().sendInterface(InterfacePosition.MINIGAME_OVERLAY, 105);
        opponent.getInterfaceHandler().sendInterface(InterfacePosition.MINIGAME_OVERLAY, 105);
        countdown = true;
        WorldTasksManager.schedule(new WorldTask() {
            int ticks = 6;

            @Override
            public void run() {
                if (ticks % 2 == 0) {
                    player.setForceTalk(new ForceTalk("" + (ticks / 2)));
                    opponent.setForceTalk(new ForceTalk("" + (ticks / 2)));
                } else if (ticks == 1) {
                    countdown = false;
                    player.setForceTalk(FIGHT);
                    opponent.setForceTalk(FIGHT);
                    stop();
                }
                ticks--;
            }
        }, 0, 1);
    }

    private void restoreTabs(@NotNull final Player player) {
        player.getInterfaceHandler().openJournal();
        GameInterface.COMBAT_TAB.open(player);
        GameInterface.SKILLS_TAB.open(player);
        GameInterface.INVENTORY_TAB.open(player);
        GameInterface.EQUIPMENT_TAB.open(player);
        GameInterface.PRAYER_TAB_INTERFACE.open(player);
        GameInterface.SPELLBOOK.open(player);
        GameInterface.SETTINGS.open(player);
        GameInterface.EMOTE_TAB.open(player);
    }

    public void finishDuel(final Player winner, final Player loser) {
        completed = true;
        val players = Arrays.asList(winner, loser);
        for (val player : players) {
            try {
                val win = player == winner;
                val container = containers.get(player);
                val opponentContainer = containers.get(player == winner ? loser : winner);

                player.unlock();
                player.setAnimation(Animation.STOP);
                player.getPacketDispatcher().resetHintArrow();
                player.setLocation(startLocations.get(player));
                if (win) {
                    player.getMusic().playJingle(98);
                }
                reset(player);
                player.getInterfaceHandler().closeInterface(InterfacePosition.DIALOGUE);
                player.getInterfaceHandler().sendInterface(InterfacePosition.MINIGAME_OVERLAY, 389);
                player.getPacketDispatcher().sendPlayerOption(1, "Challenge", false);
                player.addAttribute(win ?
                                    "DuelsWon" :
                                    "DuelsLost", player.getNumericAttribute(win ?
                                                                            "DuelsWon" :
                                                                            "DuelsLost").intValue() + 1);
                val wins = player.getNumericAttribute("DuelsWon").intValue();
                val losses = player.getNumericAttribute("DuelsLost").intValue();
                player.sendMessage((win ?
                                    "You won! " :
                                    "You were defeated! ") + "You have won " + wins + " duel" + (wins != 1 ?
                                                                                                 "s." :
                                                                                                 "."));
                player.sendMessage("You have now lost " + losses + " duel" + (losses != 1 ?
                                                                              "s." :
                                                                              "."));
                player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, WINNINGS_INTERFACE);
                val amount = container.getAmountOf(995) + opponentContainer.getAmountOf(995) + (container.getAmountOf(13204) * 1000) + (opponentContainer.getAmountOf(13204) * 1000);
                val tax = Tax.getTax(amount);
                val rate = tax.getRate() / 100F;
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 3, win && player.getName().equals(this.player.getName()) ?
                                                                                      opponent.getName() :
                                                                                      this.player.getName());
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 2, win && player.getName().equals(this.player.getName()) ?
                                                                                      opponent.getSkills().getCombatLevel() :
                                                                                      this.player.getSkills().getCombatLevel());
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 7, win && player.getName().equals(this.player.getName()) ?
                                                                                      this.player.getName() :
                                                                                      opponent.getName());
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 6, win && player.getName().equals(this.player.getName()) ?
                                                                                      this.player.getSkills().getCombatLevel() :
                                                                                      opponent.getSkills().getCombatLevel());
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 16, win ?
                                                                                       "You have won!" :
                                                                                       "You lost!");
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 35, opponentContainer.getAmountOf(13204) + container.getAmountOf(13204));
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 37, opponentContainer.getAmountOf(995) + container.getAmountOf(995));
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 38, tax);
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 39, Utils.format(amount * rate) + " gp");
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 40, Utils.format((amount - (amount * rate))) + " gp");
                long value = getValue(container) + getValue(opponentContainer);
                player.getPacketDispatcher().sendComponentText(WINNINGS_INTERFACE, 32, Utils.format(value) + " gp");
                player.setCloseInterfacesEvent(() -> {
                    if (win) {
                        player.log(LogLevel.INFO, "Won stake of items: \nPlayer items: " + container.getItems() + "\nPartner items: " + opponentContainer.getItems());
                        for (val item : Utils.concatenate(container.getItems().values(), opponentContainer.getItems().values())) {
                            if (item == null) {
                                continue;
                            }
                            val taxatedItem = new Item(item.getId(), (int) Math.round(item.getAmount() - (item.getAmount() * rate)));
                            player.getInventory().addItem(taxatedItem).onFailure(i -> {
                                World.spawnFloorItem(i, player);
                                player.sendMessage(Colour.RED + "Some of the " + taxatedItem.getName() + " have been placed on the ground.");
                            });
                        }
                        container.clear();
                        opponentContainer.clear();
                    }
                    for (val ammo : ammunitions.get(player)) {
                        if (ammo == null) {
                            continue;
                        }
                        player.getInventory().addItem(ammo).onFailure(i -> {
                            World.spawnFloorItem(i, player);
                            player.sendMessage(Colour.RED + "Some of the ammunition has been placed on the ground.");
                        });
                    }
                });
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        }
        arena = null;
    }

    public void sendSpoils(final Player player) {
        val winner = player.getName().equals(this.player.getName()) ? this.player : opponent;
        val loser = player.getName().equals(this.player.getName()) ? opponent : this.player;
        val container = containers.get(player);
        val opponentContainer = containers.get(opponent);
        winner.getPacketDispatcher().sendUpdateItemContainer(winner.getName().equals(this.player.getName()) ? opponentContainer : container, ContainerType.SPOILS_STAKE);
        loser.getPacketDispatcher().sendUpdateItemContainer(loser.getName().equals(this.player.getName()) ? container : opponentContainer, ContainerType.SPOILS_STAKE);
        player.getReceivedDamage().clear();
        player.getReceivedHits().clear();
        player.setProtectionDelay(Utils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));
    }

    public void close(final boolean message) {
        if (player == null || opponent == null) {
            return;
        }
        if (stages.get(player).equals(DuelStage.CONFIRMATION) && stages.get(opponent).equals(DuelStage.CONFIRMATION)) {
            return;
        }

        if (message) {
            player.sendMessage("You declined the duel.");
            opponent.sendMessage("The other player declined the duel.");
        }
        val container = containers.get(player);
        val opponentContainer = containers.get(opponent);
        if (container != null) {
            for (val item : container.getItems().values()) {
                if (item == null) {
                    continue;
                }
                player.getInventory().addOrDrop(item);
            }
            container.clear();
        }
        if (opponentContainer != null) {
            for (val item : opponentContainer.getItems().values()) {
                if (item == null) {
                    continue;
                }
                opponent.getInventory().addOrDrop(item);
            }
            opponentContainer.clear();
        }
        opponent.setDuel(null);
        player.setDuel(null);
        player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL, true, false);
        opponent.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL, true, false);
        player.getInterfaceHandler().closeInterface(InterfacePosition.SINGLE_TAB);
        opponent.getInterfaceHandler().closeInterface(InterfacePosition.SINGLE_TAB);
        player.getInterfaceHandler().closeInput();
        opponent.getInterfaceHandler().closeInput();
        restoreTabs(player);
        restoreTabs(opponent);
        //Refresh the inventories a tick after because of process order; Do not change this. Covers an edge case!
        WorldTasksManager.schedule(() -> {
            player.getInventory().refreshAll();
            opponent.getInventory().refreshAll();
        });
    }

    public void setItem(final int itemId, final int requestedAmount) {
        if (player == null || opponent == null || !player.inArea("Duel Arena") || !opponent.inArea("Duel Arena") || !player.getInterfaceHandler().isPresent(GameInterface.DUEL_STAKING) || !opponent.getInterfaceHandler().isPresent(GameInterface.DUEL_STAKING)) {
            return;
        }
        if (opponent.isIronman()) {
            player.sendMessage("You're dueling an Iron Man, so you can't stake items in a duel.");
            return;
        }
        if (player.isIronman()) {
            player.sendMessage("You're an Iron Man, so you can't stake items in a duel.");
            return;
        }
        val container = containers.get(player);
        if (container == null) {
            return;
        }
        val inventory = player.getInventory();
        val previousAmount = container.getAmountOf(itemId);
        if (requestedAmount > previousAmount) {
            container.deposit(player, inventory.getContainer(), inventory.getContainer().getSlotOf(itemId), requestedAmount - previousAmount);
            inventory.refresh();
        } else {
            inventory.getContainer().deposit(player, container, container.getSlotOf(itemId), previousAmount - requestedAmount);
            inventory.refresh();
        }
        val currentAmount = container.getAmountOf(itemId);
        if (currentAmount - previousAmount == 0) {
            return;
        }
        opponent.getPacketDispatcher().sendClientScript(1450, 31522846, itemId, container.getAmountOf(itemId), 31522896, 31522906, 31522910);
        if (previousAmount < currentAmount) {
            opponent.sendMessage("Duel Stake addition: " + Utils.format(currentAmount - previousAmount) + " x " + ItemDefinitions.get(itemId).getName() + " added!");
        } else {
            opponent.sendMessage("Duel Stake removal: " + Utils.format(previousAmount - currentAmount) + " x " + ItemDefinitions.get(itemId).getName() + " removed!");
        }
        player.getPacketDispatcher().sendUpdateItemContainer(container, ContainerType.DUEL_STAKE);
        opponent.getPacketDispatcher().sendUpdateItemContainer(containers.get(opponent), ContainerType.DUEL_STAKE);
        player.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 81, "");
        opponent.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 81, "<col=ff0000>Stake has changed - check before accepting!");
        stages.put(player, DuelStage.NONE);
        stages.put(opponent, DuelStage.NONE);
        waitTimers.put(opponent, Utils.currentTimeMillis() + 3000);
        updateValues(STAKING_INTERFACE, 17, 27);
    }

    public void addItem(final int itemId, final int requestedAmount) {
        if (player == null || opponent == null || !player.inArea("Duel Arena") || !opponent.inArea("Duel Arena")) {
            return;
        }
        val container = containers.get(player);
        if (container == null) {
            return;
        }
        val previousAmount = container.getAmountOf(itemId);
        val inventory = player.getInventory();
        container.deposit(player, inventory.getContainer(), inventory.getContainer().getSlotOf(itemId), requestedAmount);
        inventory.refresh();
        opponent.getPacketDispatcher().sendClientScript(1450, 31522846, itemId, container.getAmountOf(itemId), 31522896, 31522906, 31522910);
        opponent.sendMessage("Duel Stake addition: " + Utils.format(container.getAmountOf(itemId) - previousAmount) + " x " + ItemDefinitions.get(itemId).getName() + " added!");
        player.getPacketDispatcher().sendUpdateItemContainer(container, ContainerType.DUEL_STAKE);
        opponent.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 81, "<col=ff0000>Stake has changed - check before accepting!");
        player.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 81, "");
        stages.put(player, DuelStage.NONE);
        stages.put(opponent, DuelStage.NONE);
        waitTimers.put(opponent, Utils.currentTimeMillis() + 3000);
        updateValues(STAKING_INTERFACE, 17, 27);
    }

    public void removeItem(final int itemId, final int requestedAmount) {
        if (player == null || opponent == null || !player.inArea("Duel Arena") || !opponent.inArea("Duel Arena")) {
            return;
        }
        val container = containers.get(player);
        if (container == null) {
            return;
        }
        val previousAmount = container.getAmountOf(itemId);
        player.getInventory().getContainer().deposit(player, container, container.getSlotOf(itemId), requestedAmount);
        player.getInventory().refresh();
        opponent.getPacketDispatcher().sendClientScript(1450, 31522846, itemId, container.getAmountOf(itemId), 31522896, 31522906, 31522910);
        opponent.sendMessage("Duel Stake removal: " + Utils.format(previousAmount - container.getAmountOf(itemId)) + " x " + ItemDefinitions.get(itemId).getName() + " removed!");
        player.getPacketDispatcher().sendUpdateItemContainer(container, ContainerType.DUEL_STAKE);
        opponent.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 81, "<col=ff0000>Stake has changed - check before accepting!");
        player.getPacketDispatcher().sendComponentText(STAKING_INTERFACE, 81, "");
        stages.put(player, DuelStage.NONE);
        stages.put(opponent, DuelStage.NONE);
        waitTimers.put(opponent, Utils.currentTimeMillis() + 3000);
        updateValues(STAKING_INTERFACE, 17, 27);
    }

    private Object[] toArray(final Container container) {
        val size = container.getType().equals(ContainerType.INVENTORY) ? 28 : 14;
        val array = new Integer[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = container.getItems().get(i) == null ? -1 : container.getItems().get(i).getId();
        }
        return array;
    }

    private long getValue(final Container container) {
        long value = 0;
        value += container.getAmountOf(995);
        value += container.getAmountOf(13204) * 1000;
        return value;
    }

    private void reset(final Player player) {
        player.reset();
        player.getCombatDefinitions().setSpecialEnergy(100);
        player.getCombatDefinitions().setAutocastSpell(null);
        player.getToxins().reset();
        player.setAttackedByDelay(0);
        player.getNextHits().clear();
        player.getPacketDispatcher().resetHintArrow();
        //player.setCanDuel(false);
    }

    private Location getLocationNorthOrSouth(final RSPolygon polygon, final Location tile) throws IllegalStateException {
        val north = tile.transform(0, 1, 0);
        val south = tile.transform(0, -1, 0);
        if (!polygon.contains(north)) {
            if (!polygon.contains(south)) {
                throw new IllegalStateException();
            }
            return south;
        }
        return north;
    }

    public void updateInventory() {
        if (!Utils.getShiftedBoolean(settings, DuelSetting.SHOW_INVENTORIES.getBit())) {
            return;
        }
        player.getPacketDispatcher().sendClientScript(1452, toArray(opponent.getInventory().getContainer()));
        player.getPacketDispatcher().sendClientScript(1447, toArray(opponent.getEquipment().getContainer()));
    }

    private void updateValues(final int interfaceId, final int first, final int second) {
        long value = getValue(containers.get(opponent));
        String suffix = value >= 1_000 && value < 1_000_000 ? "k" : value >= 1_000_000 && value < 1_000_000_000 ? "m" : value >= 1_000_000_000 ? "b" : " gp";
        value = suffix.equals("k") ? (int) Math.floor(value / 1_000) : suffix.equals("m") ? (int) Math.floor(value / 1_000_000) : suffix.equals("b") ? (int) Math.floor(value / 1_000_000_000) : value;
        player.getPacketDispatcher().sendComponentText(interfaceId, second, value + suffix + (suffix.equals(" gp") ? "" : " gp"));
        opponent.getPacketDispatcher().sendComponentText(interfaceId, first, value + suffix + (suffix.equals(" gp") ? "" : " gp"));
        value = getValue(containers.get(player));
        suffix = value >= 1_000 && value < 1_000_000 ? "k" : value >= 1_000_000 && value < 1_000_000_000 ? "m" : value >= 1_000_000_000 ? "b" : " gp";
        value = suffix.equals("k") ? (int) Math.floor(value / 1_000) : suffix.equals("m") ? (int) Math.floor(value / 1_000_000) : suffix.equals("b") ? (int) Math.floor(value / 1_000_000_000) : value;
        player.getPacketDispatcher().sendComponentText(interfaceId, first, value + suffix + (suffix.equals(" gp") ? "" : " gp"));
        opponent.getPacketDispatcher().sendComponentText(interfaceId, second, value + suffix + (suffix.equals(" gp") ? "" : " gp"));
    }

    public boolean hasRule(final DuelSetting setting) {
        return Utils.getShiftedBoolean(settings, setting.getBit());
    }

    public void registerDuelHistory(final Player winner, final Player loser) {
        World.LATEST_DUELS.offer(winner.getName() + " (" + winner.getSkills().getCombatLevel() + ") beat " + loser.getName() + " (" + loser.getSkills().getCombatLevel() + ")");
        while (World.LATEST_DUELS.size() > 50) {
            World.LATEST_DUELS.poll();
        }
    }

    public Container getContainer(final Player player) {
        return containers.get(player);
    }

    public boolean inDuel() {
        return arena != null;
    }
}
