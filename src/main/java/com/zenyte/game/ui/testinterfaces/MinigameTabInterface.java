package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.magic.spells.teleports.MinigameGroupFinder;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.PaneType;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.enums.EnumDefinitions;
import lombok.val;

/**
 * @author Kris | 25/10/2018 17:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * The "Join" button is handled automatically by CS2s.
 */
public class MinigameTabInterface extends Interface {

    public static final int MINIGAME_LIST_ENUM = 848;

    @Override
    protected void attach() {
        put(26, "Teleport");
        put(18, "Select minigame");
    }

    @Override
    public void open(Player player) {
        val lastMinigameSlot = EnumDefinitions.get(MINIGAME_LIST_ENUM).getSize() + 1;
        player.getInterfaceHandler().sendInterface(getInterface().getId(), 2, PaneType.JOURNAL_TAB_HEADER, true);

        //Hide quest tab from view.
      /*  player.getPacketDispatcher().sendComponentVisibility(getInterface(), getComponent("View Quests"), true);
        player.getPacketDispatcher().sendComponentVisibility(getInterface(), getComponent("View Kourend Favour"), true);
        val component = getComponent("View Achievement Diaries");
        val componentDefinitions = ComponentDefinitions.get(id, component);
        player.send(new IfSetPosition(id, component, componentDefinitions.getOriginalX() + componentDefinitions.getOriginalWidth() - 4, componentDefinitions.getOriginalY()));
*/
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Teleport"), 1, lastMinigameSlot, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Select minigame"), 1, lastMinigameSlot, AccessMask.CLICK_OP1);
    }

    @Override
    protected void build() {
        bind("Teleport", (player, slotId, itemId, option) -> {
            val minigame = MinigameGroupFinder.getMinigame(slotId);
            minigame.teleport(player);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.MINIGAME_TAB;
    }
}
