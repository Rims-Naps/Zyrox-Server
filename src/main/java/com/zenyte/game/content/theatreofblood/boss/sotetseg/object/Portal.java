package com.zenyte.game.content.theatreofblood.boss.sotetseg.object;

import com.zenyte.game.content.theatreofblood.boss.sotetseg.ShadowRealmArea;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.npc.Sotetseg;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Corey
 * @since 16/06/2020
 */
public class Portal implements ObjectAction {
    
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!option.equalsIgnoreCase("Enter")) {
            return;
        }
        
        val area = player.getArea();
        
        if (!(area instanceof ShadowRealmArea)) {
            return;
        }
        
        val realm = (ShadowRealmArea) area;
        
        if (!realm.getBoss().isPresent() || realm.isCompleted()) {
            return;
        }
        
        val sotetseg = (Sotetseg) realm.getBoss().get();

        if(realm.getRaid().getParty().getAlivePlayers().size() < 2) {
            PartyOverlayInterface.fadeWhite(player, "");
            WorldTasksManager.schedule(() -> {
                PartyOverlayInterface.fade(player, 200, 0, "");
                player.cancelCombat();
                player.setLocation(realm.getRaid().getActiveRoom().getLocation(3275, 4308, 0));
            }, 2);
            WorldTasksManager.schedule(() -> {
                realm.setPlayer(null);
                realm.setCompleted(true);
                realm.refreshHealthBar(realm.getRaid());
            }, 3);
        } else {
            if(sotetseg.mazeComplete()) {
                PartyOverlayInterface.fadeWhite(player, "");
                WorldTasksManager.schedule(() -> {
                    PartyOverlayInterface.fade(player, 200, 0, "");
                    player.cancelCombat();
                    player.setLocation(realm.getRaid().getActiveRoom().getLocation(3275, 4327, 0));
                    realm.refreshHealthBar(realm.getRaid());
                    realm.setPlayer(null);
                    realm.setCompleted(true);
                }, 2);
                sotetseg.completeMaze(player);
            }
        }

    }
    
    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.PORTAL_33037};
    }
    
}
