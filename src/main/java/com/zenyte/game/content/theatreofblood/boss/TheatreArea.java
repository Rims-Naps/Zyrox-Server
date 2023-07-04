package com.zenyte.game.content.theatreofblood.boss;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.theatreofblood.TheatreAreaController;
import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.dialogue.TheatreBarrierDialogue;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.content.theatreofblood.plugin.object.scoreboard.ScoreBoardInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zenyte.game.world.entity.player.Player.DEATH_ANIMATION;

/**
 * @author Tommeh | 5/22/2020 | 4:59 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Setter
@Getter
public abstract class TheatreArea extends DynamicArea implements DropPlugin, TheatreAreaController, HealthBarOverlay, CycleProcessPlugin, DeathPlugin, CannonRestrictionPlugin, LogoutPlugin, HitProcessPlugin, TeleportPlugin {

    public final TheatreOfBloodRaid raid;
    public final TheatreRoom room;
    private final List<Player> entered;
    public int jailSpotsTaken = 0;

    private boolean started;
    protected int duration = 0;
    private boolean completed;

    public TheatreArea(final TheatreOfBloodRaid raid, final AllocatedArea area, final TheatreRoom room) {
        super(area, room.getChunkX(), room.getChunkY());
        this.raid = raid;
        this.room = room;
        entered = new ArrayList<>(5);
    }

    public abstract Location getEntranceLocation();

    public abstract WorldObject getVyreOrator();

    public abstract WorldObject getRefillChest();

    public abstract Location getSpectatingLocation();

    public abstract Location[] getJailLocations();

    public abstract Direction[] getJailFacingDirections();

    public abstract Optional<TheatreNPC<? extends TheatreArea>> getBoss();

    public abstract boolean isEnteringBossRoom(final WorldObject barrier, final Player player);

    @Override
    public boolean canTeleport(Player player, Teleport teleport) {
        if (teleport.getDestination().equals(3677, 3217, 0)) {
            return true;
        } else {
            player.sendMessage("You can only teleport out here using a Verzik's Crystal Shard.");
            return false;
        }
    }

    @Override
    public void constructed() {
        if (getVyreOrator() != null) {
            World.spawnObject(getVyreOrator());
        }
        if (getRefillChest() != null) {
            World.spawnObject(getRefillChest());
        }
    }

    @Override
    public void cleared() {
        val party = raid.getParty();
        if (!party.getPlayers().isEmpty()) {
            return;
        } else {
            for (val room : raid.getRooms().values()) {
                room.destroyRegion();
            }
        }
    }

    @Override
    public void enter(final Player player) {
        player.setViewDistance(Player.SCENE_DIAMETER);
    }

    @Override
    public void leave(final Player player, final boolean logout) {
        val party = raid.getParty();
        if (party == null) {
            return;
        }
        if (logout) {
            player.resetViewDistance();
            player.getAttributes().put("last_tob_party_id", party.getId());
            player.getAttributes().put("last_tob_safe", party.isPractice() ? 1 : 0);
            party.removeMember(player);
            if (player.getInventory().containsItem(ItemId.DAWNBRINGER)) {
                player.getInventory().deleteItem(ItemId.DAWNBRINGER, 1);
                World.spawnFloorItem(new Item(ItemId.DAWNBRINGER), player.getLastLocation(), 1, null, null, 0, 200, false);
                player.getInventory().refresh();
            }
            if (player.getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.DAWNBRINGER) {
                player.getEquipment().set(EquipmentSlot.WEAPON, null);
                World.spawnFloorItem(new Item(ItemId.DAWNBRINGER), player.getLastLocation(), 1, null, null, 0, 200, false);
                player.getEquipment().refresh();
            }
            return;
        }

        val nextArea = GlobalAreaManager.getArea(player.getLocation());
        if (!(nextArea instanceof TheatreArea)) {
            player.getInterfaceHandler().closeInterface(GameInterface.TOB_PARTY);
            removeHealthBar(player);
            party.removeMember(player);
            player.getVarManager().sendVar(3806, 0);

            if (player.getInventory().containsItem(ItemId.DAWNBRINGER)) {
                player.getInventory().deleteItem(ItemId.DAWNBRINGER, 1);
                World.spawnFloorItem(new Item(ItemId.DAWNBRINGER), player.getLastLocation(), 1, null, null, 0, 200, false);
                player.getInventory().refresh();
            }
            if (player.getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.DAWNBRINGER) {
                player.getEquipment().set(EquipmentSlot.WEAPON, null);
                World.spawnFloorItem(new Item(ItemId.DAWNBRINGER), player.getLastLocation(), 1, null, null, 0, 200, false);
                player.getEquipment().refresh();
            }
        }
    }

    @Override
    public boolean manualLogout(final Player player) {
        if (!raid.getParty().getAlivePlayers().contains(player)) {
            player.sendMessage("You can not log out whilst your team has not wiped yet.");
            return false;
        }
        return true;
    }

    @Override
    public Location onLoginLocation() {
        return TheatreOfBloodRaid.outsideLocation;
    }

    public void enterBossRoom(final WorldObject barrier, final Player player) {
        if (!started) {
            started = true;
            duration = 0;
            onStart(player);
        }
        passBarrier(barrier, player);
        WorldTasksManager.schedule(() -> {
            refreshHealthBar(player, raid);
            entered.add(player);
        }, 2);
    }

    private void passBarrier(final WorldObject barrier, final Player player) {
        player.lock(2);
        player.setRunSilent(2);

        if (barrier.getRotation() == 0 || barrier.getRotation() == 2) {
            player.addWalkSteps(player.getX(), barrier.getY() + (player.getY() > barrier.getY() ? -1 : 1), -1, false);
        } else {
            player.addWalkSteps(barrier.getX() + (player.getX() > barrier.getX() ? -1 : 1), player.getY(), -1, false);
        }
    }

    private boolean checkBarrier(final Player player, final boolean check) {
        if (!check) {
            player.sendMessage("You must stay and fight!");
            player.getDialogueManager().start(new NPCChat(player, NpcId.VYRE_ORATOR, "You must stay and fight!"));
        }
        return check;
    }

    public void handleBarrier(final WorldObject barrier, final Player player) {
        player.lock(2);
        player.setRunSilent(2);
        if (isEnteringBossRoom(barrier, player)) {
            if (!started) {
                player.getDialogueManager().start(new TheatreBarrierDialogue(player, this, barrier));
            } else {
                enterBossRoom(barrier, player);
            }
        } else {
            if (getBoss().isPresent()) {
                val boss = getBoss().get();
                if (!checkBarrier(player, boss.isFinished())) {
                    return;
                }
            }
            passBarrier(barrier, player);
        }
    }

    public void handlePassage(final Player player) {
        val currentRoom = (TheatreArea) player.getArea();

        if (!currentRoom.isCompleted()) {
            player.sendMessage("You can't proceed until the challenge is complete.");
            return;
        }

        val party = raid.getParty();
        if (raid.getActiveRoom() == currentRoom) {
            val nextRoom = onAdvancement();
            raid.advance(player, nextRoom);
            raid.constructMap();
        }

        PartyOverlayInterface.fadeRed(player, "");
        WorldTasksManager.schedule(() -> {
            PartyOverlayInterface.fade(player, 255, 0, raid.getActiveRoom().getRoom().getName());
            player.setLocation(raid.getActiveRoom().getEntranceLocation());
            player.addTemporaryAttribute("tob_advancing_room", 0);
        });
        if(raid.getSpectators().size() > 0) {
            World.getPlayers().forEach( p -> {
                raid.getSpectators().forEach(s -> {
                    if(p == null || p.isLoggedOut()) {
                        return;
                    }
                    if(p.getUsername().equals(s)) {
                        if (!raid.getActiveRoom().inside(p.getLocation())){
                            PartyOverlayInterface.fadeRed(p, "");
                            WorldTasksManager.schedule(() -> {
                                PartyOverlayInterface.fade(p, 255, 0, raid.getActiveRoom().getRoom().getName());
                                p.setLocation(raid.getActiveRoom().getSpectatingLocation());
                            });
                        }
                    }
                });
            });
        }

    }

    public void onCompletion() {
        completed = true;

        raid.getRoomDurations().put(raid.getActiveRoom().getRoom(), duration);
        int total = 0;
        for(int i : raid.getRoomDurations().values()) {
            total += i;
        }

        for (val m : raid.getParty().getMembers()) {
            val member = RaidingParty.getPlayer(m);
            if (member == null) {
                continue;
            }

            removeHealthBar(member);
            member.getPacketDispatcher().playJingle(250);

            for(int i = 0; i < Skills.SKILLS.length; i++) {
                if(member.getSkills().getLevel(i) < member.getSkills().getLevelForXp(i)) {
                    member.getSkills().setLevel(i, member.getSkills().getLevelForXp(i));
                }
            }
            member.getVariables().setRunEnergy(100);
            member.getCombatDefinitions().setSpecialEnergy(100);
            raid.getParty().getPlayers().forEach(p -> {
                if(p != null && getRaid().getParty().getLifeStates().get(p.getUsername()).equals("dead")) {
                    p.setLocation(raid.getActiveRoom().getRespawnLocation());
                    getRaid().getParty().getLifeStates().put(p.getUsername(), "alive");
                }
            });
            val minutes = TimeUnit.TICKS.toMinutes(duration);
            val seconds = TimeUnit.TICKS.toSeconds(duration - TimeUnit.MINUTES.toTicks(minutes));

            String GMTASK = "grandmaster-combat-achievement";
            boolean perfectTheatre = true;
            for (int i = 58; i < 64; i++) {
                if (!member.getBooleanAttribute(GMTASK+i)){
                    perfectTheatre = false;
                    break;
                }
            }
            if (perfectTheatre && !member.getBooleanAttribute("grandmaster-combat-achievement37")) {
                member.putBooleanAttribute("grandmaster-combat-achievement37", true);
                GrandmasterTasks.sendGrandmasterCompletion(member, 37);
            }

            val totalMinutes = TimeUnit.TICKS.toMinutes(total);
            val totalSeconds = TimeUnit.TICKS.toSeconds(total - TimeUnit.MINUTES.toTicks(totalMinutes));
            if (raid.getActiveRoom().getRoom().getWave() != 6) {
                member.sendMessage(String.format("Wave '%s' complete! Duration:" + Colour.RED.wrap(" %s") + " Total: " + Colour.RED.wrap("%s"), raid.getActiveRoom().getRoom().getName(), minutes + ":" + (seconds > 9 ? seconds : "0" + seconds), totalMinutes + ":" + (totalSeconds > 9 ? totalSeconds : "0" + totalSeconds)));
            } else {
                member.sendMessage(String.format("Wave '%s' complete! Duration:" + Colour.RED.wrap(" %s"), raid.getActiveRoom().getRoom().getName(), minutes + ":" + (seconds > 9 ? seconds : "0" + seconds)));
                member.addAttribute("theatreofblood", member.getNumericAttribute("theatreofblood").intValue() + 1);
                var message = "Theatre of Blood completion time: " + Colour.RED + Utils.formatTime(totalMinutes, totalSeconds) + Colour.END;

                val teamSize = raid.getParty().getPlayers().size();
                if (raid.getActiveRoom().getRoom().getWave() == 6) {
                    if (teamSize == 2
                            && total < 2600
                            && !member.getBooleanAttribute("grandmaster-combat-achievement39")) {
                        member.putBooleanAttribute("grandmaster-combat-achievement39", true);
                        GrandmasterTasks.sendGrandmasterCompletion(member, 39);
                    }
                    if (teamSize == 3
                            && total < 2000
                            && !member.getBooleanAttribute("master-combat-achievement67")) {
                        member.putBooleanAttribute("master-combat-achievement67", true);
                        MasterTasks.sendMasterCompletion(member, 67);
                    }
                    if (teamSize == 3
                            && total < 1750
                            && !member.getBooleanAttribute("grandmaster-combat-achievement40")) {
                        member.putBooleanAttribute("grandmaster-combat-achievement40", true);
                        GrandmasterTasks.sendGrandmasterCompletion(member, 40);
                    }
                    if (teamSize == 4
                            && total < 1700
                            && !member.getBooleanAttribute("master-combat-achievement68")) {
                        member.putBooleanAttribute("master-combat-achievement68", true);
                        MasterTasks.sendMasterCompletion(member, 68);
                    }
                    if (teamSize == 4
                            && total < 1500
                            && !member.getBooleanAttribute("grandmaster-combat-achievement41")) {
                        member.putBooleanAttribute("grandmaster-combat-achievement41", true);
                        GrandmasterTasks.sendGrandmasterCompletion(member, 41);
                    }
                    if (teamSize == 5
                            && total < 1600
                            && !member.getBooleanAttribute("master-combat-achievement69")) {
                        member.putBooleanAttribute("master-combat-achievement69", true);
                        MasterTasks.sendMasterCompletion(member, 69);
                    }
                    if (teamSize == 5
                            && total < 1425
                            && !member.getBooleanAttribute("grandmaster-combat-achievement42")) {
                        member.putBooleanAttribute("grandmaster-combat-achievement42", true);
                        GrandmasterTasks.sendGrandmasterCompletion(member, 42);
                    }
                    if (member.getBooleanAttribute("BackInMyDay")
                            && !member.getBooleanAttribute("master-combat-achievement66")) {
                        member.putBooleanAttribute("master-combat-achievement66", true);
                        MasterTasks.sendMasterCompletion(member, 66);
                    }
                    if (member.getBooleanAttribute("MorytaniaOnly")
                            && !member.getBooleanAttribute("grandmaster-combat-achievement38")) {
                        member.putBooleanAttribute("grandmaster-combat-achievement38", true);
                        GrandmasterTasks.sendGrandmasterCompletion(member, 38);
                    }
                }

                val pbKey = "tobpb" + teamSize;
                if (!member.getAttributes().containsKey(pbKey)) {
                    member.getAttributes().put(pbKey, total);
                    member.sendMessage(message + " (Personal Best!)");
                } else {
                    int pbTicks = member.getNumericAttribute(pbKey).intValue();
                    val pbTotalMinutes = TimeUnit.TICKS.toMinutes(pbTicks);
                    val pbTotalSeconds = TimeUnit.TICKS.toSeconds(pbTicks - TimeUnit.MINUTES.toTicks(pbTotalMinutes));
                    if (pbTicks > total) {
                        member.getAttributes().put(pbKey, total);
                        member.sendMessage(message + " (Personal Best!)");
                    } else {
                        message += ". Personal Best: " + Colour.RED + Utils.formatTime(pbTotalMinutes, pbTotalSeconds) + Colour.END;
                        member.sendMessage(message);
                    }
                }
                member.sendMessage("Your completed Theatre of Blood count is: " + Colour.RED + member.getNumericAttribute("theatreofblood").intValue() + Colour.END + ".");

                ScoreBoardInterface.refreshBoard(teamSize);
            }
        }
    }

    @Override
    public boolean hit(final Player source, final Entity target, final Hit hit, final float modifier) {
        if (!entered.contains(source)) {
            return false;
        }
        return true;
    }

    @Override
    public void process() {
        for (val p : getRaid().getParty().getPlayers()){
            val weapon = p.getEquipment().getItem(EquipmentSlot.WEAPON);
            if (weapon != null) {
                val weaponName = weapon.getName();
                if (!p.getBooleanAttribute("master-combat-achievement66") && p.getBooleanAttribute("BackInMyDay")) {
                    if (weaponName.startsWith("Scythe")) {
                        failBackinMyTime(getRaid(), p.getUsername());
                    }
                }
                if (!p.getBooleanAttribute("grandmaster-combat-achievement38") && p.getBooleanAttribute("MorytaniaOnly")) {
                    if (!(weaponName.startsWith("Dharok")
                            || weaponName.startsWith("Guthan")
                            || weaponName.startsWith("Ahrim")
                            || weaponName.startsWith("Verac")
                            || weaponName.startsWith("Torag")
                            || weaponName.startsWith("Karil")
                            || weaponName.startsWith("Dawnbringer"))) {
                        failMorytaniaOnly(getRaid(), p.getUsername(), weaponName);
                    }
                }
            }
        }
        if(teamWipe()) {
            wipeTeam();
        }
    }

    private void failBackinMyTime(TheatreOfBloodRaid raid, String name) {
        for (val p : raid.getParty().getPlayers()) {
            p.putBooleanAttribute("BackInMyDay", false);
            p.sendMessage("Your party failed the " + Colour.RS_GREEN.wrap("Back in My Day...") + " combat achievement task as " + name + " equipped a Scythe of Vitur.");
        }
    }

    private void failMorytaniaOnly(TheatreOfBloodRaid raid, String name, String weaponName) {
        for (val p : raid.getParty().getPlayers()) {
            p.putBooleanAttribute("MorytaniaOnly", false);
            p.sendMessage("Your party failed the " + Colour.RS_GREEN.wrap("Morytania Only") + " combat achievement task as " + name + " equipped " + Utils.getAOrAn(weaponName)  + " " + weaponName + ".");
        }
    }

    public abstract boolean inCombatZone(int x, int y);

    public boolean inZone(int tileX, int tileY, int x, int y) {
        return x == getX(tileX) && y == getY(tileY);
    }

    public boolean inZone(RSPolygon polygon, int x, int y) {
        return polygon.contains(x, y);
    }

    public boolean inZone(int zX, int zY, int zX2, int zY2, int x, int y) {
        return x >= getX(zX) && x <= getX(zX2) && y >= getY(zY) && y <= getY(zY2);
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public String getDeathInformation() {
        return null;
    }

    @Override
    public Location getRespawnLocation() {
        return getSpectatingLocation();
    }

    private final Logger hcimDeathLogger = LoggerFactory.getLogger("HCIM Death Logger");

    @Override
    public boolean sendDeath(Player player, Entity ent) {
        player.setAnimation(Animation.STOP);

        if (player.getInventory().containsItem(ItemId.DAWNBRINGER)) {
            player.getInventory().deleteItem(ItemId.DAWNBRINGER, 1);
            World.spawnFloorItem(new Item(ItemId.DAWNBRINGER), player.getLastLocation(), 1, null, null, 0, 200, false);
            player.getInventory().refresh();
        }
        if (player.getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.DAWNBRINGER) {
            player.getEquipment().set(EquipmentSlot.WEAPON, null);
            World.spawnFloorItem(new Item(ItemId.DAWNBRINGER), player.getLastLocation(), 1, null, null, 0, 200, false);
            player.getEquipment().refresh();
        }

        player.lock(3);

        WorldTasksManager.schedule(new WorldTask() {
            int ticks;
            final int jailSpot = getRaid().getActiveRoom().getJailSpotsTaken();

            @Override
            public void run() {

                if (player.isFinished() || player.isNulled()) {
                    stop();
                    return;
                }

                if(ticks == 0) {
                    player.setAnimation(DEATH_ANIMATION);
                    getRaid().getParty().getLifeStates().put(player.getUsername(), "dead");
                    getRaid().getActiveRoom().jailSpotsTaken++;
                    player.getAttributes().put("tobpoints", Math.max(0, player.getNumericAttribute("tobpoints").intValue() - 4));
                    player.getAttributes().put("tobdeaths", player.getNumericAttribute("tobdeaths").intValue() + 1);
                    if (getRaid().getActiveRoom().getRoom().getWave() < 5) {
                        player.getAttributes().put("tobrefillpoints", Math.max(0, player.getNumericAttribute("tobrefillpoints").intValue() - 4));
                    }
                    if (getRaid().getActiveRoom().getRoom().getWave() == 4) {
                        for (val p : getRaid().getParty().getPlayers()) {
                            p.putBooleanAttribute("ATimelySnack", false);
                        }
                    }
                }if (ticks == 2) {
                    if(!getRaid().getParty().isPractice()) {
                        if (player.getGameMode().equals(GameMode.HARDCORE_IRON_MAN)) {
                            WorldBroadcasts.broadcast(player, BroadcastType.HCIM_DEATH, "The Theatre of Blood");
                            player.sendMessage("You have fallen as a Hardcore Iron Man, your Hardcore status has been revoked.");
                            player.setGameMode(GameMode.STANDARD_IRON_MAN);
                        }
                    }
                    PartyOverlayInterface.fadeRed(player, "If your party survives the wave, you will respawn.");
                    WorldTasksManager.schedule(() -> {
                        player.setLocation(raid.getActiveRoom().getJailLocations()[jailSpot % 5]);
                        PartyOverlayInterface.fade(player, 255, 0, "If your party survives the wave, you will respawn.");
                        player.setAnimation(Animation.STOP);
                        player.stopAll();
                        player.reset();
                        player.faceDirection(raid.getActiveRoom().getJailFacingDirections()[jailSpot % 5]);
                    });
                    getRaid().getParty().updateStatusHUD(true);
                    stop();
                }
                ticks++;
            }
        }, 0, 1);
        return false;
    }

    public void wipeTeam() {
        for(Player p : getRaid().getParty().getPlayers()) {
            if(!getRaid().getParty().getLifeStates().get(p.getUsername()).equals("wiped")) {
                p.getNextHits().clear();
                wipePlayer(p);
            }
        }
        World.getPlayers().forEach( p -> {
            raid.getSpectators().forEach(s -> {
                if(p == null || p.isLoggedOut()) {
                    return;
                }
                if(p.getUsername().equals(s)) {
                    raid.getSpectators().remove(p.getUsername());
                    PartyOverlayInterface.fadeRed(p, "");
                    WorldTasksManager.schedule(() -> {
                        PartyOverlayInterface.fade(p, 200, 0, "");
                        p.setLocation(TheatreOfBloodRaid.outsideLocation);
                        p.getPacketDispatcher().resetCamera();
                    });
                }
            });
        });
        getRaid().getActiveRoom().setStarted(false);
    }

    private void wipePlayer(Player player) {
        getRaid().getParty().getLifeStates().put(player.getUsername(), "wiped");
        player.lock(4);
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                if (ticks == 4) {
                    PartyOverlayInterface.fadeRed(player, "Your party has failed.");
                    WorldTasksManager.schedule(() -> {
                        for(int i = 0; i < Skills.SKILLS.length; i++) {
                            if(player.getSkills().getLevel(i) < player.getSkills().getLevelForXp(i)) {
                                player.getSkills().setLevel(i, player.getSkills().getLevelForXp(i));
                            }
                        }
                        player.getCombatDefinitions().setSpecialEnergy(100);
                        player.getPacketDispatcher().resetCamera();
                        player.setLocation(TheatreOfBloodRaid.outsideLocation);
                        PartyOverlayInterface.fade(player, 255, 0, "Your party has failed.");
                        if(!getRaid().getParty().isPractice()) {
                            player.getDeathMechanics().service(ItemRetrievalService.RetrievalServiceType.THEATRE_OF_BLOOD, getRaid().getActiveRoom().getBoss().get());
                            player.sendMessage("A magical chest has retrieved some of your items. You can collect them from it outside the Theatre of Blood.");
                        }
                    });
                }
                if (ticks == 5) {
                    PartyOverlayInterface.refresh(player, getRaid().getParty());
                    stop();
                }
                ticks++;
            }
        }, 0, 1);
    }

    private boolean teamWipe() {
        return getRaid().getParty().getAlivePlayers().size() == 0 && getRaid().getActiveRoom().isStarted();
    }

    @Override
    public void postProcess() {
        val party = raid.getParty();
        if (party == null) {
            return;
        }
        party.updateStatusHUD(false);
    }

    @Override
    public int getCurrentHitpoints() {
        if (getBoss().isPresent()) {
            val boss = getBoss().get();
            return boss.isFinished() ? 0 : boss.getHitpoints();
        }
        return 0;
    }

    @Override
    public int getMaximumHitpoints() {
        if (getBoss().isPresent()) {
            val boss = getBoss().get();
            return boss.getMaxHitpoints();
        }
        return 0;
    }

    @Override
    public boolean dropOnGround(Player player, Item item) {
        if(item.getId() == ItemId.DAWNBRINGER) {
            World.spawnFloorItem(new Item(ItemId.DAWNBRINGER), player.getLocation(), 1, null, null, 0, 200, false);
            return false;
        } else {
            if(item.isTradable()) {
                World.spawnFloorItem(item, player.getLocation(), Integer.MAX_VALUE, player, null, 10, 200000, false);
                return false;
            } else {
                return true;
            }
        }
    }
}
