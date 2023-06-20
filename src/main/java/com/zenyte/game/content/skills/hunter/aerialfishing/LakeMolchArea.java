package com.zenyte.game.content.skills.hunter.aerialfishing;

import com.zenyte.game.content.skills.hunter.aerialfishing.npc.FishingSpotNpc;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.kourend.GreatKourend;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;

/**
 * @author Cresinkel
 */

public class LakeMolchArea extends GreatKourend implements CycleProcessPlugin {

    public static int fishingSpots = 0;

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 1354, 3620 },
                        { 1354, 3646 },
                        { 1381, 3646 },
                        { 1381, 3620 }
                })
        };
    }
    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player, boolean logout) {
        if (player.getEquipment().containsAnyOf(ItemId.CORMORANTS_GLOVE_22817, ItemId.CORMORANTS_GLOVE)) {
            player.sendMessage("Alry's cormorant flies back to him.");
            player.getEquipment().deleteItem(EquipmentSlot.WEAPON.getSlot(), new Item(ItemId.CORMORANTS_GLOVE_22817));
            player.getEquipment().deleteItem(EquipmentSlot.WEAPON.getSlot(), new Item(ItemId.CORMORANTS_GLOVE));
        }
    }

    @Override
    public String name() {
        return "Lake Molch";
    }

    @Override
    public void process() {
        if (fishingSpots == 0) {
            initialSpawnSpot();
        }
    }

    public static void handleCaughtSpot(NPC npc) {
        Location npcPos = npc.getLocation();
        fishingSpots--;
        npc.remove();
        spawnCaughtSpot(npcPos);
    }

    private static void spawnCaughtSpot(Location spotPos) {
        FishingSpotNpc spot = new FishingSpotNpc(8523, generateRandomCaughtSpotLocation(spotPos), Direction.NORTH, 0);
        spot.spawn();
        fishingSpots++;
    }

    private static void initialSpawnSpot() {
        int MAX_SPOT_COUNT = 30;
        for(int i = 0; i < MAX_SPOT_COUNT; i++) {
            spawnSpot();
        }
    }

    public static void spawnSpot() {
        FishingSpotNpc spot = new FishingSpotNpc(8523, generateRandomSpotLocation(), Direction.NORTH, 0);
        spot.spawn();
        fishingSpots++;
    }

    private static Location generateRandomCaughtSpotLocation(Location spot) {
        int randomX = Utils.random(spot.getX() - 3, spot.getX() + 3);
        int randomY = Utils.random(spot.getY() - 3, spot.getY() + 3);
        while (!isValidLocation(randomX, randomY)) {
            randomX = Utils.random(spot.getX() - 3, spot.getX() + 3);
            randomY = Utils.random(spot.getY() - 3, spot.getY() + 3);
        }
        return new Location(randomX, randomY, 0);
    }

    private static Location generateRandomSpotLocation() {
        int randomX = Utils.random(1353, 1382);
        int randomY = Utils.random(3619, 3640);
        while (!isValidLocation(randomX, randomY)) {
            randomX = Utils.random(1353, 1382);
            randomY = Utils.random(3619, 3640);
        }
        return new Location(randomX, randomY, 0);
    }

    private static boolean isValidLocation(int x, int y) {
        //Check if there is already a fishing spot in this location
        if (World.findNPC(8523,new Location(x,y,0),0).isPresent()){
            return false;
        }

        if (y < 3625) {
            return true;
        } else if (x < 1359) {
            return true;
        } else if (x > 1376) {
            return true;
        } else if (y == 3625 && (x >= 1368 && x <= 1371)) {
            return false;
        } else if (y == 3626 && (x >= 1365 && x <= 1374)) {
            return false;
        } else if (y == 3627 && (x >= 1362 && x <= 1375)) {
            return false;
        } else if (y == 3628 && x >= 1361) {
            return false;
        } else if (y == 3629 && x >= 1360) {
            return false;
        } else if (y == 3630 && x >= 1360) {
            return false;
        } else if (y == 3631 && x >= 1360) {
            return false;
        } else if (y == 3632 && (x >= 1360 && x <= 1375)) {
            return false;
        } else if (y == 3633 && x <= 1375) {
            return false;
        } else if (y == 3634) {
            return false;
        } else if (y == 3635) {
            return false;
        } else if (y == 3636 && x <= 1375) {
            return false;
        } else if (y >= 3637) {
            return false;
        } else {
            return true;
        }
    }
}
