package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.packet.out.IfSetPosition;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfaceHandler;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 12/05/2019 | 18:24
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class JournalHeaderTabInterface extends Interface {

    @Override
    protected void attach() {
        put(3, "Quests");
        put(4, "Achievement Diary");
        put(5, "Minigames");
        put(6, "Kourend Favour");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        player.getPacketDispatcher().sendComponentVisibility(id, 3, true);
        player.getPacketDispatcher().sendComponentVisibility(id, 6, true);
        player.send(new IfSetPosition(id, 4, 0, 0));
        player.send(new IfSetPosition(id, 5, 44, 0));
    }

    @Override
    protected void build() {
        bind("Quests", player -> player.getInterfaceHandler().setJournal(InterfaceHandler.Journal.QUEST_TAB));
        bind("Achievement Diary", player -> player.getInterfaceHandler().setJournal(InterfaceHandler.Journal.ACHIEVEMENT_DIARIES));
        bind("Minigames", player -> player.getInterfaceHandler().setJournal(InterfaceHandler.Journal.MINIGAMES));
        bind("Kourend Favour", player -> player.getInterfaceHandler().setJournal(InterfaceHandler.Journal.KOUREND));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.JOURNAL_HEADER_TAB;
    }
}
