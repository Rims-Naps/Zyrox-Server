package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfaceHandler;
import com.zenyte.game.ui.PaneType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.var.VarCollection;

import static com.zenyte.game.constants.GameInterface.*;

/**
 * @author Tommeh | 28-10-2018 | 19:51
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class KourendFavourTabInterface extends Interface {

    private static final int ACHIEVEMENT = 3612;
    private static final int MINIGAMES = 3217;
    private static final int KOUREND = 618;

    @Override
    protected void attach() {
        put(4, "View Quests");
        put(5, "View Achievement Diaries");
        put(6, "View Minigames");
    }

    @Override
    public void open(Player player) {
        //Hide quest tab from view.
        //player.getPacketDispatcher().sendComponentVisibility(getInterface(), getComponent("View Quests"), true);
        player.getInterfaceHandler().sendInterface(getInterface().getId(), 2, PaneType.JOURNAL_TAB_HEADER, true);
    }

    @Override
    protected void build() {
        bind("View Quests", QUEST_TAB::open);
        bind("View Achievement Diaries", ACHIEVEMENT_DIARY_TAB::open);
        bind("View Minigames", MINIGAME_TAB::open);
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.KOUREND_FAVOUR_TAB;
    }
}
