package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;

/**
 * @author Kris | 24/10/2018 13:47
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AdvancedSettingsInterface extends Interface {

    @Override
    protected void attach() {
        put(4, "Chatbox scrollbar");
        put(6, "Transparent sidepanel");
        put(8, "Remaining xp tooltips");
        put(10, "Prayer tooltips");
        put(12, "Special attack tooltips");
        put(16, "Data orbs");
        put(18, "Transparent chatbox");
        put(20, "Chatbox can be clicked through");
        put(21, "Side-stones arrangement");
        put(23, "Side panels can be closed by hotkeys");
    }

    @Override
    protected void build() {
        bind("Chatbox scrollbar", player -> player.getSettings().toggleSetting(Setting.CHATBOX_SCROLLBAR));
        bind("Transparent sidepanel", player -> player.getSettings().toggleSetting(Setting.TABS_TRANSPARENT));
        bind("Remaining xp tooltips", player -> player.getSettings().toggleSetting(Setting.DISPLAY_REMAINING_XP));
        bind("Prayer tooltips", player -> player.getSettings().toggleSetting(Setting.DISPLAY_PRAYER_TOOLTIPS));
        bind("Special attack tooltips", player -> player.getSettings().toggleSetting(Setting.DISPLAY_SPECIAL_BAR_TOOLTIPS));
        bind("Data orbs", player -> player.getSettings().toggleSetting(Setting.DATA_ORBS));
        bind("Side-stones arrangement", player -> player.getSettings().toggleSetting(Setting.SIDE_PANELS));
        bind("Side panels can be closed by hotkeys", player -> {
            if (!player.getBooleanSetting(Setting.SIDE_PANELS)) {
                player.sendMessage("You need to enable side panels first.");
                return;
            }
            player.getSettings().toggleSetting(Setting.CLOSE_TABS_WITH_HOTKEYS);
        });
        bind("Transparent chatbox", player -> player.getSettings().toggleSetting(Setting.CHATBOX_TRANSPARENT));
        bind("Chatbox can be clicked through", player -> {
            if (!player.getBooleanSetting(Setting.CHATBOX_TRANSPARENT)) {
                player.sendMessage("You need to enable chatbox transparency first.");
                return;
            }
            player.getSettings().toggleSetting(Setting.CLICK_THROUGH_CHATBOX);
        });
    }

    @Override
    public void open(Player player) {
        if (player.isLocked()) {
            return;
        }
        if (player.isUnderCombat()) {
            player.sendMessage("You can't do this while in combat.");
            return;
        }
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.ADVANCED_SETTINGS;
    }
}
