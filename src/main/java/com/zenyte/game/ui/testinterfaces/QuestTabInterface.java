package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfaceHandler;
import com.zenyte.game.ui.PaneType;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;

import static com.zenyte.game.constants.GameInterface.*;

/**
 * @author Tommeh | 28-10-2018 | 19:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class QuestTabInterface extends Interface {

    private static final int ACHIEVEMENT = 3612;
    private static final int MINIGAMES = 3217;
    private static final int KOUREND = 618;

    @Override
    protected void attach() {
        put(3, "View Achievement Diaries");
        put(4, "View Minigames");
        put(5, "View Kourend Favour");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface().getId(), 2, PaneType.JOURNAL_TAB_HEADER, true);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), 7, 0, 19, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), 8, 0, 115, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), 9, 0, 11, AccessMask.CLICK_OP1);
    }

    @Override
    protected void build() {
        bind("View Achievement Diaries", ACHIEVEMENT_DIARY_TAB::open);
        bind("View Minigames", MINIGAME_TAB::open);
        bind("View Kourend Favour", KOUREND_FAVOUR_TAB::open);
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.QUEST_TAB;
    }
}
