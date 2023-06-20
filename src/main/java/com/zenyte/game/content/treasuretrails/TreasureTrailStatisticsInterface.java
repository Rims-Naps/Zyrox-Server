package com.zenyte.game.content.treasuretrails;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 04/01/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TreasureTrailStatisticsInterface extends Interface {
    @Override
    protected void attach() {

    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        val dispatcher = player.getPacketDispatcher();
        dispatcher.sendComponentText(getInterface(), 2, "Treasure trails completed:");

        val array = ClueLevel.values();
        int total = 0;
        for (int i = 0; i < array.length; i++) {
            val tier = array[i].toString().toLowerCase();
            val count = player.getNumericAttribute("completed " + tier + " treasure trails").intValue();
            total += count;
            dispatcher.sendComponentText(getInterface(), 4 + i, Utils.formatString(tier) + ": " + count);
        }
        dispatcher.sendComponentText(getInterface(), 11, "Total: " + total);

        val rank = total <= 100 ? "Beginner" : total <= 200 ? "Novice" : total <= 400 ? "The Explorer" : total <= 600 ? "Treasure Hunter" :
                total <= 1000 ? "Expert" : total <= 1500 ? "Master" : "Legendary";
        dispatcher.sendComponentText(getInterface(), 13, "Your rank is: " + rank);
    }

    @Override
    protected void build() {

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TREASURE_TRAIL_STATISTICS;
    }
}
