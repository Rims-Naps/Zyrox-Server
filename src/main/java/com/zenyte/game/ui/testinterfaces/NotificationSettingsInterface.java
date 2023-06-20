package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.NotificationSettings;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import lombok.val;

/**
 * @author Kris | 24/04/2019 14:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class NotificationSettingsInterface extends Interface {
    @Override
    protected void attach() {
        put(7, "Loot drop notifications");
        put(11, "Untradable loot notifications");
        put(12, "Boss kill-count notifications");
        put(14, "Drop item warnings");
    }

    @Override
    public void open(final Player player) {
        val dispatcher = player.getPacketDispatcher();
        player.getInterfaceHandler().sendInterface(this);
        dispatcher.sendComponentSettings(NotificationSettings.INTERFACE, getComponent("Untradable loot notifications"), 0, 1, AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(NotificationSettings.INTERFACE, getComponent("Boss kill-count notifications"), 0, 1, AccessMask.CLICK_OP1);
    }

    @Override
    protected void build() {
        bind("Loot drop notifications", (player, slotId, itemId, option) -> player.getNotificationSettings().setThresholdValue(option));
        bind("Untradable loot notifications", (player, slotId, itemId, option) -> player.getSettings().setSetting(Setting.UNTRADEABLE_DROP_NOTIFICATIONS, slotId));
        bind("Boss kill-count notifications", (player, slotId, itemId, option) -> player.getSettings().setSetting(Setting.FILTERED_BOSS_KILLCOUNT_UPDATES, slotId));
        bind("Drop item warnings", (player, slotId, itemId, option) -> player.getNotificationSettings().setDropWarningTresholdValue(option));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.NOTIFICATION_SETTINGS;
    }
}
