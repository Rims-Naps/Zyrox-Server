package com.zenyte.game.content.theatreofblood.boss.sotetseg.npc;

import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.CharacterLoop;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Corey
 * @since 21/06/2020
 */
public class RedStorm extends NPC {
    
    public static final int STORM_ID = 8389; // doesn't have a name so no NpcId constant :[
    
    private final Sotetseg sotetseg;
    private final ArrayList<Location> mazePath;
    private boolean shouldFinish = false;
    private ObjectListIterator<Location> iterator;
    private int aliveTicks = 0;
    
    public RedStorm(final Sotetseg sotetseg, final ArrayList<Location> mazePath) {
        super(STORM_ID, mazePath.get(0), true);
        this.size = 1;
        this.sotetseg = sotetseg;
        this.mazePath = mazePath;
        //this.iterator = mazePath.iterator();
    }
    
    @Override
    public void processNPC() {
        if(shouldFinish) {
            finish();
            return;
        }

        super.processNPC();
        checkForPlayers();

        Location next = null;
        if (aliveTicks == mazePath.size()) {
            // storm has reached the end of the path
            shouldFinish = true;
            return;
        } else {
          next = mazePath.get(aliveTicks);
        }
        getWalkSteps().clear();
        if(next != null) {
            addWalkSteps(next.getX() - 1, next.getY() - 1);
        }

        aliveTicks++;
    }
    
    private void checkForPlayers() {
        val nearbyPlayers = CharacterLoop.find(getMiddleLocation(), 1, Player.class, p -> sotetseg.getRaid().getParty().getAliveMembers().contains(p));
        
        if (!nearbyPlayers.isEmpty()) {
            for (val m : sotetseg.getRaid().getParty().getAliveMembers()) {
                val member = RaidingParty.getPlayer(m);
                if (member == null || sotetseg.getShadowRealm() != null && member.equals(sotetseg.getShadowRealm().getPlayer())) {
                    continue;
                }
                member.applyHit(new Hit(Utils.random(35, 45), HitType.REGULAR));
            }
        }
    }

    /*else {
        resetWalkSteps();
        iterator = mazePath.iterator(); // restart iterator
        setLocation(mazePath.first()); // reset location to start
    }*/
    
}
