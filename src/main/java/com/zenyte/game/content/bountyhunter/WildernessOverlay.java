package com.zenyte.game.content.bountyhunter;

import com.zenyte.Constants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 26/03/2019 17:09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class WildernessOverlay extends Interface {

    @Override
    protected void attach() {
        put(6, "Bounty Hunter overlay");//Component updated.
        put(20, "Deadman mode final overlay");//Component updated.
        put(57, "Skip Bounty Hunter target");//Component updated.
        put(54, "Target name");//Component updated.
        put(55, "Wilderness level and target combat");//Component updated.
        put(43, "Current rogue streak");//Component updated.
        put(44, "Current hunter streak");//Component updated.
        put(46, "Rogue streak record");//Component updated.
        put(45, "Hunter streak record");//Component updated.
        put(64, "Red circle");//Component updated.
        put(58, "Minimise");//Component updated.
        put(3, "Wilderness level");//Component updated.
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        val dispatcher = player.getPacketDispatcher();
        dispatcher.sendComponentVisibility(getInterface(), getComponent("Red circle"), true);
        if (Constants.BOUNTY_HUNTER) {
            val bountyhunter = player.getBountyHunter();
            dispatcher.sendComponentText(getInterface(), getComponent("Current rogue streak"), bountyhunter.getValue(BountyHunterVar.CURRENT_ROGUE_KILLS));
            dispatcher.sendComponentText(getInterface(), getComponent("Current hunter streak"), bountyhunter.getValue(BountyHunterVar.CURRENT_HUNTER_KILLS));
            dispatcher.sendComponentText(getInterface(), getComponent("Rogue streak record"), bountyhunter.getValue(BountyHunterVar.ROGUE_KILLS_RECORD));
            dispatcher.sendComponentText(getInterface(), getComponent("Hunter streak record"), bountyhunter.getValue(BountyHunterVar.HUNTER_KILLS_RECORD));
            dispatcher.sendComponentText(getInterface(), getComponent("Target name"), "None");
            dispatcher.sendComponentText(getInterface(), getComponent("Wilderness level and target combat"), "Level: -----");
            updateTargetInformation(player);
        } else {
            dispatcher.sendComponentVisibility(getInterface(), getComponent("Bounty Hunter overlay"), true);
        }
    }

    void updateTargetInformation(@NotNull final Player player) {
        if (!Constants.BOUNTY_HUNTER) {
            return;
        }
        val bounty = player.getBountyHunter();
        val target = bounty.getTarget();
        if (target == null) {
            return;
        }
        val dispatcher = player.getPacketDispatcher();
        dispatcher.sendComponentText(getInterface(), getComponent("Target name"), target.getName());
        val targetWildernessLevel = WildernessArea.getWildernessLevel(target.getLocation());
        val level = targetWildernessLevel.orElse(0);
        val minLevel = Math.max(1, level - 2);
        val maxLevel = Math.min(64, level + 2);
        dispatcher.sendComponentText(getInterface(), getComponent("Wilderness level and target combat"),
                "<col=990000>Level: " + minLevel + "-" + maxLevel + ", Cmb " + target.getSkills().getCombatLevel() + "</col>");
    }

    @Override
    protected void build() {

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.WILDERNESS_OVERLAY;
    }
}
