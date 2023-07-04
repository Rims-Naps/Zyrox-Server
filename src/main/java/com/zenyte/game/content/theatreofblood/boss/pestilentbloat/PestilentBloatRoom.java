package com.zenyte.game.content.theatreofblood.boss.pestilentbloat;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.pestilentbloat.npc.PestilentBloat;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.content.theatreofblood.shared.HealthBarType;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import lombok.val;


import java.util.Optional;

/**
 * @author Tommeh | 5/22/2020 | 5:57 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PestilentBloatRoom extends TheatreArea {

    private final PestilentBloat bloat;
    private final RSPolygon walkableArea;

    public PestilentBloatRoom(final TheatreOfBloodRaid raid, final AllocatedArea area, final TheatreRoom room) {
        super(raid, area, room);
        bloat = new PestilentBloat(this);
        walkableArea = new RSPolygon(new int[][]{
                {getX(3293), getY(4451)},
                {getX(3299), getY(4451)},
                {getX(3299), getY(4445)},
                {getX(3293), getY(4445)},
                {getX(3288), getY(4440)},
                {getX(3288), getY(4456)},
                {getX(3304), getY(4456)},
                {getX(3304), getY(4440)},
                {getX(3288), getY(4440)},
                {getX(3293), getY(4445)}
        });
    }

    @Override
    public void onLoad() {
        bloat.spawn();
    }

    public Location getRandomMeatPoint() {
        val poly = walkableArea.getPolygon();
        val box = poly.getBounds2D();
        var count = 1000;
        Location location = new Location(0);
        do {
            if (--count <= 0) {
                throw new RuntimeException("Unable to find a valid point in polygon.");
            }
            location.setLocation((int) box.getMinX() + Utils.random((int) box.getWidth()), (int) box.getMinY() + Utils.random((int) box.getHeight()), bloat.getPlane());
        } while (!poly.contains(location.getX(), location.getY()));
        return location;
    }

    @Override
    public void onStart(final Player player) {
        bloat.inversePath((int) TimeUnit.SECONDS.toTicks(17));
        bloat.stompAttack((int) TimeUnit.SECONDS.toTicks(21));
    }

    @Override
    public boolean isEnteringBossRoom(final WorldObject barrier, final Player player) {
        if (barrier.getRotation() == 1) {
            return player.getX() > barrier.getX();
        }
        return player.getX() < barrier.getX();
    }

    @Override
    public boolean inCombatZone(int x, int y) {
        return inZone(3288, 4440, 3303, 4455, x, y);
    }

    @Override
    public TheatreRoom onAdvancement() {
        val nextRoom = TheatreRoom.THE_NYLOCAS;
        raid.removeRoom(getRoom());
        raid.addRoom(TheatreRoom.THE_NYLOCAS);
        return nextRoom;
    }

    @Override
    public Location getEntranceLocation() {
        return getLocation(3322, 4448, 0);
    }

    @Override
    public WorldObject getVyreOrator() {
        return new WorldObject(ObjectId.VYRE_ORATOR_32757, 11, 2, getLocation(3309, 4445, 0));
    }

    @Override
    public WorldObject getRefillChest() {
        return new WorldObject(ObjectId.CHEST_32758, 10, 3, getLocation(3269, 4449, 0));
    }

    @Override
    public Location getSpectatingLocation() {
        return getLocation(3304, 4434, 0);
    }

    @Override
    public Location[] getJailLocations() {
        return new Location[] { getLocation(3295, 4459, 0), getLocation(3296, 4459, 0),
                getLocation(3295, 4436, 0), getLocation(3296, 4436, 0),
        };
    }

    @Override
    public Direction[] getJailFacingDirections() {
        return new Direction[] {Direction.SOUTH, Direction.SOUTH, Direction.NORTH, Direction.NORTH};
    }

    @Override
    public Optional<TheatreNPC<? extends TheatreArea>> getBoss() {
        return Optional.of(bloat);
    }

    @Override
    public HealthBarType getHealthBarType() {
        return HealthBarType.REGULAR;
    }

    @Override
    public String name() {
        return "The Pestilent Bloat";
    }

    @Override
    public boolean hit(final Player source, final Entity target, final Hit hit, final float modifier) {
        if (source == null) {
            return true;
        }
        if (target instanceof PestilentBloat) {
            val player = (Player) hit.getSource();
            if (player.getAttributes().containsKey("bloatbossdamage")) {
                player.getAttributes().put("bloatbossdamage", (player.getNumericAttribute("bloatbossdamage").intValue() + hit.getDamage()));
            } else {
                player.getAttributes().put("bloatbossdamage", hit.getDamage());
            }
        }
        return true;
    }

    @Override
    public void enterBossRoom(WorldObject barrier, Player player) {
        player.getAttributes().put("tobpoints", player.getNumericAttribute("tobpoints").intValue() + 3);
        if (!isStarted()) {
            setDuration(0);
            bloat.getTemporaryAttributes().put("start", System.currentTimeMillis());
        }
        super.enterBossRoom(barrier, player);
    }

    @Override
    public Location getRespawnLocation() {
        return getLocation(3281, 4448, 0);
    }

    @Override
    public void onCompletion() {
        setDuration((int) TimeUnit.MILLISECONDS.toTicks(System.currentTimeMillis() - ((long) bloat.getTemporaryAttributes().get("start"))));
        super.onCompletion();
    }
}
