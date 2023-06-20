package com.zenyte.game.content.minigame.tithefarm;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TitheFarmSackOverlay extends Interface {

    private static final int ITEM_VARBIT = 4901;
    private static final int SCORE_VARBIT = 4893;
    private static final int FRUIT_VARBIT = 4900;

    @Override
    protected final void attach() {

    }

    public static final void process(final Player player, final TithePlantType type) {
        val score = player.getNumericAttribute("tithe_farm_points").intValue();
        val fruit = player.getNumericAttribute("tithe_farm_fruit").intValue();

        // this allows us to change it based on what was deposited
        switch(type.toString()) {
            case "GOLOVANOVA":
                player.getVarManager().sendBit(ITEM_VARBIT, 1);
                break;
            case "BOLOGANO":
                player.getVarManager().sendBit(ITEM_VARBIT, 2);
                break;
            case "LOGAVANO":
                player.getVarManager().sendBit(ITEM_VARBIT, 3);
                break;
        }

        if(score > 0) {
            player.getVarManager().sendBit(SCORE_VARBIT, score);
        }

        if(fruit > 0) {
            player.getVarManager().sendBit(FRUIT_VARBIT, fruit);
        }

    }

    @Override
    public final void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
        process(player, TithePlantType.GOLOVANOVA);
    }

    @Override
    protected final void build() {

    }

    @Override
    public final GameInterface getInterface() {
        return GameInterface.TITHE_FARM_SACK;
    }
}
