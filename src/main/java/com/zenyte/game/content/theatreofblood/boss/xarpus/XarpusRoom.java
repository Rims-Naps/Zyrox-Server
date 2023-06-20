package com.zenyte.game.content.theatreofblood.boss.xarpus;

import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.xarpus.npc.Xarpus;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.content.theatreofblood.shared.HealthBarType;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import lombok.val;
import lombok.var;

import java.util.Optional;

/**
 * @author Tommeh | 7/13/2020 | 1:21 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class XarpusRoom extends TheatreArea {

    private final Xarpus xarpus;

    public XarpusRoom(final TheatreOfBloodRaid raid, final AllocatedArea area, final TheatreRoom room) {
        super(raid, area, room);
        xarpus = new Xarpus(this);
    }

    @Override
    public void enterBossRoom(WorldObject barrier, Player player) {
        if (!isStarted()) {
            xarpus.spawn();
            setDuration(0);
            xarpus.getTemporaryAttributes().put("start", System.currentTimeMillis());
        }
        if (!isCompleted()) {
            player.getAttributes().put("tobpoints", player.getNumericAttribute("tobpoints").intValue() + 3);
        }
        super.enterBossRoom(barrier, player);
    }

    @Override
    public void onCompletion() {
        setDuration((int) TimeUnit.MILLISECONDS.toTicks(System.currentTimeMillis() - ((long) xarpus.getTemporaryAttributes().get("start"))));
        super.onCompletion();
    }

    @Override
    public boolean inCombatZone(int x, int y) {
        return inZone(3163, 4380, 3177, 4394, x, y);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public TheatreRoom onAdvancement() {
        var MVP = (Player) raid.getParty().getLeader();
        int MVPDamage;
        for (val p : raid.getParty().getPlayers()) {
            MVPDamage = MVP.getNumericAttribute("xarpusbossdamage").intValue();
            int PlayerDamage = p.getNumericAttribute("xarpusbossdamage").intValue();
            if (PlayerDamage > MVPDamage) {
                MVP = p;
            }
        }
        System.out.println(MVP != null ? MVP.getName() : "Null mvp");
        if (MVP != null) {
            if (MVP.getAttributes().containsKey("tobpoints")) {
                MVP.getAttributes().put("tobpoints", MVP.getNumericAttribute("tobpoints").intValue() + 2);
            } else {
                MVP.sendMessage("You did not have the attribute for points.");
            }
        } else {
            raid.getParty().getLeader().sendMessage("MVP was null.");
        }
        System.out.println(MVP != null ? MVP.getNumericAttribute("tobpoints").intValue() + " points" : "Null mvp");

        for (val p : raid.getParty().getPlayers()) {
            if (p.getBooleanAttribute("CanYouDance")
                    && !p.getBooleanAttribute("master-combat-achievement65")) {
                p.putBooleanAttribute("master-combat-achievement65", true);
                MasterTasks.sendMasterCompletion(p, 65);
            }
        }

        val nextRoom = TheatreRoom.VERZIK;
        raid.removeRoom(getRoom());
        raid.addRoom(TheatreRoom.VERZIK);
        return nextRoom;
    }

    @Override
    public Location getEntranceLocation() {
        return getLocation(3170, 4375, 1);
    }

    @Override
    public WorldObject getVyreOrator() {
        return new WorldObject(ObjectId.VYRE_ORATOR, 11, 2, getLocation(3169, 4376, 1));
    }

    @Override
    public WorldObject getRefillChest() {
        return null;
    }

    @Override
    public Location getSpectatingLocation() {
        return getLocation(3157, 4383, 1);
    }

    @Override
    public Location[] getJailLocations() {
        return new Location[] { getLocation(3157, 4391, 1), getLocation(3157, 4383, 1),
                                getLocation(3183, 4391, 1), getLocation(3183, 4383, 1) };
    }

    @Override
    public Direction[] getJailFacingDirections() {
        return new Direction[] { Direction.EAST, Direction.EAST, Direction.WEST, Direction.WEST };
    }

    @Override
    public Optional<TheatreNPC<? extends TheatreArea>> getBoss() {
        return Optional.of(xarpus);
    }

    @Override
    public boolean isEnteringBossRoom(WorldObject barrier, Player player) {
        if (barrier.getRotation() == 2) {
            return player.getY() < barrier.getY();
        } else {
            return player.getY() > barrier.getY();
        }

    }

    @Override
    public HealthBarType getHealthBarType() {
        return HealthBarType.DISABLED;
    }

    @Override
    public String name() {
        return "Xarpus";
    }

    @Override
    public boolean hit(final Player source, final Entity target, final Hit hit, final float modifier) {
        if (source == null) {
            return true;
        }
        if (target instanceof Xarpus) {
            val player = (Player) hit.getSource();
            if (player.getAttributes().containsKey("xarpusbossdamage")) {
                player.getAttributes().put("xarpusbossdamage", (player.getNumericAttribute("xarpusbossdamage").intValue() + hit.getDamage()));
            } else {
                player.getAttributes().put("xarpusbossdamage", hit.getDamage());
            }
            if (hit.getHitType().equals(HitType.MAGIC) || hit.getHitType().equals(HitType.RANGED)) {
                for (val p : getRaid().getParty().getPlayers()) {
                    p.putBooleanAttribute("CanYouDance", false);
                }
            }
        }
        return true;
    }

    @Override
    public Location getRespawnLocation() {
        return getLocation(3170, 4387, 1);
    }
}
