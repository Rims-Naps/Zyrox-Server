package com.zenyte.game.content.theatreofblood.reward;

import com.zenyte.Constants;
import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.content.theatreofblood.shared.HealthBarType;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.val;

import java.util.Optional;

/**
 * @author Cresinkel
 */

public class RewardRoom extends TheatreArea {

    public static final Object2IntOpenHashMap<Location> chests = new Object2IntOpenHashMap<Location>() {{
        put(new Location(3233, 4330, 0), 4);
        put(new Location(3226, 4323, 0), 3);
        put(new Location(3240, 4327, 0), 1);
        put(new Location(3226, 4327, 0), 3);
        put(new Location(3240, 4323, 0), 1);
    }};
    private int maxPoints;
    private final int BASE_CHANCE = 91;

    public RewardRoom(TheatreOfBloodRaid raid, AllocatedArea area, TheatreRoom room) {
        super(raid, area, room);
    }

    @Override
    public void enter(Player player) {
        super.enter(player);
    }

    @Override
    public Location getEntranceLocation() {
        return getLocation(3237, 4307, 0);
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
        return getLocation(3234, 4324, 0);
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
        return null;
    }

    @Override
    public boolean isEnteringBossRoom(WorldObject barrier, Player player) {
        return player.getLocation().equals(getEntranceLocation());
    }

    @Override
    public void enterBossRoom(WorldObject barrier, Player player) {

    }

    @Override
    public boolean inCombatZone(int x, int y) {return true;}

    @Override
    public String name() {
        return "Verzik Vitur's Vault";
    }

    @Override
    public void onLoad() {
        int teamSize = getRaid().getParty().getSize();
        float totalPoints = 0;
        maxPoints = getMaxPoints(getRaid().getParty());
        for (Player p : getRaid().getParty().getPlayers()) {
            p.getAttributes().put("rareTobReward", false);
            p.getAttributes().put("tobIndex", 0);
            p.getVarManager().sendBit(6450, 0);
            p.getVarManager().sendBit(6451, 0);
            p.getVarManager().sendBit(6452, 0);
            p.getVarManager().sendBit(6453, 0);
            p.getVarManager().sendBit(6454, 0);
            int playerPoints = p.getNumericAttribute("tobpoints").intValue();
            totalPoints += playerPoints;
        }
        float fraction = (totalPoints/maxPoints);
        int modifiedBaseChance = Math.round(BASE_CHANCE + (BASE_CHANCE*(1-fraction)));
        float bonusPoints = 0;
        for (Player p : getRaid().getParty().getPlayers()) {
            if (p.getVariables().getTobBoost() > 0 || Constants.BOOSTED_TOB) {
                float playerPoints = (float) p.getNumericAttribute("tobpoints").intValue();
                playerPoints *= (p.getMemberRank().eligibleTo(MemberRank.ZENYTE_MEMBER) ? 0.1 :
                        p.getMemberRank().eligibleTo(MemberRank.ONYX_MEMBER) ? 0.09 :
                        p.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER) ? 0.08 :
                        p.getMemberRank().eligibleTo(MemberRank.DIAMOND_MEMBER) ? 0.07 :
                        p.getMemberRank().eligibleTo(MemberRank.RUBY_MEMBER) ? 0.07 :
                        p.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER) ? 0.06 :
                        p.getMemberRank().eligibleTo(MemberRank.SAPPHIRE_MEMBER) ? 0.06 : 0.05);
                bonusPoints += playerPoints;
            }
        }
        bonusPoints /= teamSize;
        double rand = Math.random()*(modifiedBaseChance-1);
        for (Player p : getRaid().getParty().getPlayers()) {
            p.sendMessage("Your team's chance to get a purple this raid was: " + (10+bonusPoints) + "/" + modifiedBaseChance + ".");
        }
        boolean rareReward = rand < 10 + bonusPoints; // 10 / (91 + (91 * (1 - total/max))) or 1/9.1 on perfect raids (no deaths and all enter every room)
        if (rareReward) {
            int random = Utils.random(Math.round(totalPoints));
            int total = 0;
            for (Player p : getRaid().getParty().getPlayers()) {
                int playerPoints = p.getNumericAttribute("tobpoints").intValue();
                total += playerPoints;
                if (random < total) { //decides who gets the purple
                    p.putBooleanAttribute("rareTobReward", true);
                    break;
                }
            }
        }

        int playerIndex = 0;
        int rareIndex = -1;
        while (playerIndex < teamSize) {
            for (Player p : getRaid().getParty().getPlayers()) {
                if (playerIndex < teamSize) {
                    int varbitId = 6450 + playerIndex;
                    int varbitValue = p.getBooleanAttribute("rareTobReward") ? 3 : 2;
                    if (p.getBooleanAttribute("rareTobReward")) {
                        rareIndex = playerIndex;
                    }
                    p.getVarManager().sendBit(varbitId, varbitValue);
                    p.getAttributes().put("tobChestLoc", getLocation((Location) chests.keySet().toArray()[playerIndex]));
                    playerIndex++;
                }
            }
        }
        if (rareReward) {
            int rareBitId = 6450 + rareIndex;
            for (Player p : getRaid().getParty().getPlayers()) {
                if (!p.getBooleanAttribute("rareTobReward")) {
                    p.getVarManager().sendBit(rareBitId, 1);
                }
            }
        }
        int chestIndex = 0;
        for (val chestLocation : chests.keySet()) {
                World.spawnObject(new WorldObject(33086 + chestIndex, 10, chests.getInt(chestLocation), getLocation(chestLocation)));
                chestIndex++;
                if (chestIndex == teamSize) {
                    break;
                }
        }
        super.onLoad();
    }


    @Override
    public TheatreRoom onAdvancement() {
        return null;
    }

    @Override
    public HealthBarType getHealthBarType() {
        return null;
    }

    @Override
    public boolean drop(Player player, Item item) {
        return super.drop(player, item);
    }

    private int getMaxPoints(RaidingParty party) {
            int teamSize = party.getSize();
            int total = 0;
            total += teamSize*3*6; //6 bosses, 3 pts for entering the bossroom per player //
            total += 14; //total MVP points 2maiden 2bloat 1nylo 1sote 2xarpus 3*2verzik (2 per phase)
        return total;
    }

    public static int getPetRate(Player player) {
        int BASE_RATE = 450;
        int perPointFraction = BASE_RATE/30;
        int points = player.getNumericAttribute("tobpoints").intValue();
        int missingPoints = 30 - points;
        if (points == 0) {
            return -1;
        }
        return BASE_RATE + missingPoints * perPointFraction;
        //at BASE = 300:  if 30 points: 1/300, if 18 points: 1/420, if 6 points: 1/540, if 0 points no chance
        //at BASE = 450:  if 30 points: 1/450, if 18 points: 1/630, if 6 points: 1/810, if 0 points no chance
        //at BASE = 600:  if 30 points: 1/600, if 18 points: 1/840, if 6 points: 1/1080, if 0 points no chance
    }
}
