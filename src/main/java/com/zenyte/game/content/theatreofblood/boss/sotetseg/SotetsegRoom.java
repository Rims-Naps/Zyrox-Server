package com.zenyte.game.content.theatreofblood.boss.sotetseg;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.npc.Sotetseg;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.content.theatreofblood.shared.HealthBarType;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.util.Optional;

/**
 * @author Corey
 * @since 10/06/2020
 */
public class SotetsegRoom extends TheatreArea {
    
    private static final Location sotetsegSpawnLocation = new Location(3277, 4326);
    private static final Location mazeTopLeft = new Location(3273, 4324);
    private static final Location mazeBottomRight = new Location(3286, 4310);
    public final RSPolygon combatZone = new RSPolygon(new int[][] {
            {getX(3272), getY(4305)},
            {getX(3272), getY(4329)},
            {getX(3277), getY(4334)},
            {getX(3282), getY(4334)},
            {getX(3287), getY(4328)},
            {getX(3287), getY(4305)},
            {getX(3282), getY(4305)},
            {getX(3282), getY(4307)},
            {getX(3277), getY(4307)},
            {getX(3277), getY(4305)}
    });
    private final Sotetseg sotetseg;
    
    public SotetsegRoom(TheatreOfBloodRaid raid, AllocatedArea area, TheatreRoom room) {
        super(raid, area, room);
        this.sotetseg = new Sotetseg(this);
    }
    
    public void setMazeTileIds(final Tile tile) {
        val topLeft = getMazeTopLeft();
        val bottomRight = getMazeBottomRight();
        
        for (int x = topLeft.getX(); x <= bottomRight.getX(); x++) {
            for (int y = bottomRight.getY(); y <= topLeft.getY(); y++) {
                World.spawnObject(new WorldObject(tile.getId(), 22, 0, x, y, topLeft.getPlane()));
            }
        }
    }

    @Override
    public TheatreRoom onAdvancement() {
        val nextRoom = TheatreRoom.XARPUS;
        raid.removeRoom(getRoom());
        raid.addRoom(TheatreRoom.XARPUS);
        return nextRoom;
    }
    
    public boolean isInMaze(final Player player) {
        return isInMaze(player.getLocation());
    }
    
    public boolean isInMaze(final Location location) {
        if (location.getX() < getMazeTopLeft().getX() || location.getX() > getMazeBottomRight().getX()) {
            return false;
        }
        return location.getY() >= getMazeBottomRight().getY() && location.getY() <= getMazeTopLeft().getY();
    }
    
    public Location getSotetsegSpawnLocation() {
        return getLocation(sotetsegSpawnLocation);
    }
    
    public Location getMazeTopLeft() {
        return getLocation(mazeTopLeft);
    }
    
    public Location getMazeBottomRight() {
        return getLocation(mazeBottomRight);
    }
    
    @Override
    public void onLoad() {
        setMazeTileIds(Tile.LIGHT_GREY);
        sotetseg.spawn();
    }
    
    @Override
    public Location getEntranceLocation() {
        return getLocation(3279, 4293, 0);
    }
    
    @Override
    public void enterBossRoom(WorldObject barrier, Player player) {
        super.enterBossRoom(barrier, player);
        sotetseg.setStarted(true);
        setDuration(0);
        if (!sotetseg.getTemporaryAttributes().containsKey("start")) {
            sotetseg.getTemporaryAttributes().put("start", System.currentTimeMillis());
        }
        player.getAttributes().put("tobpoints", player.getNumericAttribute("tobpoints").intValue() + 3);
    }

    @Override
    public void onCompletion() {
        setDuration((int) TimeUnit.MILLISECONDS.toTicks(System.currentTimeMillis() - ((long) sotetseg.getTemporaryAttributes().get("start"))));
        super.onCompletion();
    }

    @Override
    public WorldObject getRefillChest() {
        return new WorldObject(ObjectId.CHEST_32758, 10, 2, getLocation(3281, 4293, 0));
    }

    @Override
    public boolean inCombatZone(int x, int y) {
        return combatZone.contains(x, y); //|| (sotetseg.getShadowRealm() != null && sotetseg.getShadowRealm().inCombatZone(x, y)
    }

    @Override
    public WorldObject getVyreOrator() {
        return new WorldObject(ObjectId.VYRE_ORATOR, 11, 1, getLocation(3281, 4301, 0));
    }
    
    @Override
    public Location getSpectatingLocation() {
        return getLocation(3272, 4301, 0);
    }

    @Override
    public Location[] getJailLocations() {
        return new Location[] { getLocation(3270, 4314, 0), getLocation(3270, 4313, 0),
                                getLocation(3289, 4314, 0), getLocation(3289, 4313, 0) };
    }

    @Override
    public Direction[] getJailFacingDirections() {
        return new Direction[] { Direction.EAST, Direction.EAST, Direction.WEST, Direction.WEST };
    }

    @Override
    public Optional<TheatreNPC<? extends TheatreArea>> getBoss() {
        return Optional.of(sotetseg);
    }
    
    @Override
    public boolean isEnteringBossRoom(WorldObject barrier, Player player) {
        return player.getY() < barrier.getY();
    }
    
    @Override
    public HealthBarType getHealthBarType() {
        return sotetseg.isMazePhase() ? HealthBarType.DISABLED : HealthBarType.REGULAR;
    }
    
    @Override
    public String name() {
        return "Sotetseg";
    }
    
    @Getter
    @AllArgsConstructor
    public enum Tile {
        DARK_GREY(ObjectId.TILE_33034),
        LIGHT_GREY(ObjectId.TILE),
        RED(ObjectId.TILE_33035),
        RED_DAMAGE(ObjectId.TILE_33036);
    
        private final int id;
    }
    @Override
    public boolean hit(final Player source, final Entity target, final Hit hit, final float modifier) {
        if (source == null) {
            return true;
        }
        if (target instanceof Sotetseg) {
            val player = (Player) hit.getSource();
            if (player.getAttributes().containsKey("sotebossdamage")) {
                player.getAttributes().put("sotebossdamage", (player.getNumericAttribute("sotebossdamage").intValue() + hit.getDamage()));
            } else {
                player.getAttributes().put("sotebossdamage", hit.getDamage());
            }
        }
        return true;
    }

    @Override
    public Location getRespawnLocation() {
        return getLocation(3280, 4308, 0);
    }
}
