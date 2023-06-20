package com.zenyte.game.content.theatreofblood.boss.sotetseg;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.npc.Sotetseg;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.content.theatreofblood.shared.HealthBarType;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Corey
 * @since 12/06/2020
 */
@Slf4j
public class ShadowRealmArea extends TheatreArea {
    
    public static final int RED_MAZE_TILE_ID = ObjectId.TILE_33035;
    private static final int MAZE_WIDTH = 14;
    private static final int MAZE_HEIGHT = 15;
    private static final Location startLocation = new Location(3360, 4309, 3);
    private static final Location mazeTopLeft = new Location(3354, 4325, 3);
    private static final Location mazeBottomRight = new Location(3367, 4311, 3);
    private static final Graphics randomDamageGfx = new Graphics(1608);
    public static final RSPolygon combatZone = new RSPolygon(new int[][] {
            {3353, 4308},
            {3353, 4331},
            {3368, 4331},
            {3368, 4308}
    });
    @Getter
    @Setter
    private Player player;
    private final Sotetseg sotetseg;
    @Getter
    private final ObjectLinkedOpenHashSet<Location> mazePath;
    
    private int ticks;
    @Getter
    @Setter
    private boolean completed;
    
    public ShadowRealmArea(TheatreOfBloodRaid raid, AllocatedArea area, TheatreRoom room, final Player player, final Sotetseg sotetseg) {
        super(raid, area, room);
        this.player = player;
        this.sotetseg = sotetseg;
        
        val path = generatePath(getLocation(mazeTopLeft), getLocation(mazeBottomRight));
        if (path == null) {
            this.mazePath = generateEmergencyPath(getLocation(mazeTopLeft), getLocation(mazeBottomRight));
            for (val m : raid.getParty().getMembers()) {
                val member = RaidingParty.getPlayer(m);
                if (member == null) {
                    continue;
                }
                member.sendMessage(Colour.RED.wrap("Attention: AN ERROR OCCURRED WHEN GENERATING SOTETSEG MAZE, PLEASE CONTACT AN ADMIN"));
            }
            log.error("Error occurred when generating Sotetseg maze"); // TODO add some more data
        } else {
            this.mazePath = path;
        }
    }
    
    @Nullable
    public static ObjectLinkedOpenHashSet<Location> generatePath(final Location topLeft, final Location bottomRight) {
        val yIndexOffset = 2;
        val maxXChange = 5;
        val tiles = new ArrayList<Location>();
        
        int yIndex = 0;
        int xIndex = Utils.random(0, MAZE_WIDTH - 1);
        
        val startTile = topLeft.transform(xIndex, (-MAZE_HEIGHT) + 1, 0);
        
        tiles.add(startTile); // start
        tiles.add(startTile.transform(0, 1, 0)); // start
        
        yIndex = 1;
        
        int iterations = 0;
        
        while (yIndex < (MAZE_HEIGHT - 1)) {
            int newXIndex = Utils.random(Math.max(0, xIndex - maxXChange), Math.min(xIndex + maxXChange, MAZE_WIDTH - 1));

            if (xIndex > newXIndex) {
                for (int x = Math.max(newXIndex, xIndex); x >= Math.min(newXIndex, xIndex); x--) {
                    tiles.add(translateTile(topLeft, bottomRight, x, yIndex)); // fill in X changes
                }
            } else {
                for (int x = Math.min(newXIndex, xIndex); x <= Math.max(newXIndex, xIndex); x++) {
                    tiles.add(translateTile(topLeft, bottomRight, x, yIndex)); // fill in X changes
                }
            }

            int newYIndex = yIndex + (Utils.random(1) == 0 ? yIndexOffset : yIndexOffset * 2); // Y always increases
            newYIndex = Math.min(newYIndex, MAZE_HEIGHT - 1);
            
            for (int y = yIndex; y < newYIndex; y++) {
                tiles.add(translateTile(topLeft, bottomRight, newXIndex, y)); // fill in Y changes
            }
            
            // update current indices
            xIndex = newXIndex;
            yIndex = newYIndex;
            
            iterations++;
            
            if (iterations > 1000) {
                // if for some reason the while-loop continues far over the time it should take, return null so it can be handled
                return null;
            }
        }
        
        tiles.add(tiles.get(tiles.size() - 1).transform(0, 1, 0)); // last tile
        
        return new ObjectLinkedOpenHashSet<>(tiles);
    }
    
    private static ObjectLinkedOpenHashSet<Location> generateEmergencyPath(final Location topLeft, final Location bottomRight) {
        val tiles = new ArrayList<Location>();
        
        val endY = topLeft.getY();
        val startY = topLeft.getY() - (MAZE_HEIGHT - 1);
        
        for (int y = startY; y <= endY; y++) {
            tiles.add(new Location(topLeft.getX(), y, topLeft.getPlane()));
        }
        
        return new ObjectLinkedOpenHashSet<>(tiles);
    }
    
    private static Location translateTile(final Location topLeft, final Location bottomRight, final int x, final int y) {
        return new Location(topLeft.transform(x, 0, 0).getX(), bottomRight.transform(0, y, 0).getY(), topLeft.getPlane());
    }
    
    @Override
    public void process() {
        super.process();
        
        if (ticks++ == 6) {
            ticks = 0;
            randomDamage();
        }
    }

    @Override
    public boolean inCombatZone(int x, int y) {
        return combatZone.contains(x, y) || getRaid().getActiveRoom().inCombatZone(x, y);
    }

    public boolean shouldStartMazeStorm() {
        if(player == null) {
            return false;
        }
        return player.getY() > getMazeBottomRight().getY() + 2;
    }
    
    private void randomDamage() {
        if (completed) {
            return;
        }
        if(player == null) {
            return;
        }
        player.setGraphics(randomDamageGfx);
        WorldTasksManager.schedule(() -> {
            if (completed) {
                return;
            }
            player.applyHit(new Hit(Utils.random(1, 3), HitType.REGULAR));
        }, 2);
    }
    
    public Location getMazeTopLeft() {
        return getLocation(mazeTopLeft);
    }
    
    public Location getMazeBottomRight() {
        return getLocation(mazeBottomRight);
    }
    
    @Override
    public Location getEntranceLocation() {
        return null;
    }
    
    @Override
    public WorldObject getVyreOrator() {
        return null;
    }

    @Override
    public WorldObject getRefillChest() {
        return null;
    }

    @Override
    public Location getSpectatingLocation() {
        return null;
    }

    @Override
    public Location[] getJailLocations() {
        return new Location[0];
    }

    @Override
    public Direction[] getJailFacingDirections() {
        return new Direction[0];
    }

    @Override
    public Optional<TheatreNPC<? extends TheatreArea>> getBoss() {
        return Optional.of(sotetseg);
    }
    
    @Override
    public boolean isEnteringBossRoom(WorldObject barrier, Player player) {
        return false;
    }
    
    @Override
    public void constructed() {
        for (val mazeTile : mazePath) {
            World.spawnObject(new WorldObject(RED_MAZE_TILE_ID, 22, 0, mazeTile));
        }
        player.setLocation(getLocation(startLocation));
        player.cancelCombat();
        refreshHealthBar(player, getRaid());
    }
    
    @Override
    public void enter(Player player) {
    
    }
    
    @Override
    public void leave(Player player, boolean logout) {
    
    }
    
    @Override
    public String name() {
        return "Sotetseg: Shadow Realm";
    }
    
    @Override
    public HealthBarType getHealthBarType() {
        return HealthBarType.DISABLED;
    }
}
