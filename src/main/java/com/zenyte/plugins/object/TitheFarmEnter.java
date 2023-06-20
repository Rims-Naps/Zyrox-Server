package com.zenyte.plugins.object;

import com.zenyte.game.content.minigame.tithefarm.TitheFarmManager;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.FarmerGricollerD;
import com.zenyte.plugins.dialogue.TitheFruitSpoilD;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TitheFarmEnter implements ObjectAction {

    @Override
    public final void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (player.getArea().name().contains("Tithe Farm")) {

            if(player.getInventory().containsAnyOf(ItemId.GOLOVANOVA_FRUIT, ItemId.BOLOGANO_FRUIT, ItemId.LOGAVANO_FRUIT)) {
                player.getDialogueManager().start(new TitheFruitSpoilD(player, NpcId.FARMER_GRICOLLER));
                return;
            }

            TitheFarmManager.getSingleton().selectRandomInstance().leaveInstance(player);
        } else {
            if(!player.getInventory().containsAnyOf(13423, 13424, 13425)) {
                player.getDialogueManager().start(new FarmerGricollerD(player, NpcId.FARMER_GRICOLLER, false, true));
                return;
            }

            if(player.getBooleanAttribute("gricoller_reminder_disabled")) {
                TitheFarmManager.getSingleton().selectRandomInstance().enterInstance(player);
            } else {
                player.getDialogueManager().start(new FarmerGricollerD(player, NpcId.FARMER_GRICOLLER, true, false));
            }
        }
    }

    @Override
    public final Object[] getObjects() {
        return new Object[] { ObjectId.FARM_DOOR };
    }
}
