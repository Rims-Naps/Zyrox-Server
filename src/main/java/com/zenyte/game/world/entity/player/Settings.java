package com.zenyte.game.world.entity.player;

import com.google.gson.annotations.Expose;
import com.zenyte.game.content.clans.ClanChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh 12 nov. 2017 : 21:16:17 | @author Kris | 17. veebr 2018 : 0:49.08
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class Settings {

    private final transient Player player;
    @Expose
    @Getter
    @Setter
    private String channelOwner;
    @Expose
    @Getter
    @Setter
    private transient ClanChannel channel;
    @Expose
    @Getter
    @Setter
    private Map<Integer, Integer> keybinds = new HashMap<Integer, Integer>();
    @Expose
    @Getter
    @Setter
    private Map<String, Integer> killsLog = new HashMap<String, Integer>();

    public Settings(final Player player) {
        this.player = player;
    }

    public final void initialize(final Settings settings) {
        channel = settings.channel;
        channelOwner = settings.channelOwner;
        keybinds = settings.keybinds;
        killsLog = settings.killsLog;
    }

    public void toggleSetting(final Setting setting) {
        player.toggleBooleanAttribute(setting.toString());
        refreshSetting(setting);
    }

    public void setSetting(final Setting setting, final int value) {
        player.addAttribute(setting.toString(), value);
        refreshSetting(setting);
    }

    public void setSettingNoRefresh(final Setting setting, final int value) {
        player.addAttribute(setting.toString(), value);
    }

    public void refreshSetting(final Setting setting) {
        final int state = player.getNumericAttribute(setting.toString()).intValue();
        if (setting.getId() == -1) {
            if (setting.getRunnable() == null) {
                return;
            }
            setting.getRunnable().run(player, state == 1);
            return;
        }

        if (setting.isVarp()) {
            player.getVarManager().sendVar(setting.getId(), state);
        } else {
            player.getVarManager().sendBit(setting.getId(), state);
        }
        if (setting.getRunnable() == null) {
            return;
        }
        setting.getRunnable().run(player, state == 1);
    }

    public void refresh() {
        player.refreshQuestPoints();
        final int size = Setting.SETTINGS.length;
        for (int i = 0; i < size; i++) {
            final Setting setting = Setting.SETTINGS[i];
            if (setting.getRunnable() == null) {
                refreshSetting(setting);
            }
        }
        refreshSetting(Setting.SPLIT_PRIVATE_CHAT);
        refreshSetting(Setting.UNTRADEABLE_DROP_NOTIFICATIONS);
        refreshSetting(Setting.EXPERIENCE_TRACKER);
        if (player.isOnMobile()) {
            refreshSetting(Setting.MOBILE_SHIFT_CLICK_DROPPING);
        } else {
            refreshSetting(Setting.SHIFT_CLICK_DROPPING);
        }
    }

    public int valueOf(final Setting setting) {
        return player.getNumericAttribute(setting.toString()).intValue();
    }

    public boolean isSidePanels() {
        return player.getNumericAttribute(Setting.SIDE_PANELS.toString()).intValue() == 1;
    }

    public boolean isRigour() {
        return player.getNumericAttribute(Setting.RIGOUR.toString()).intValue() == 1;
    }

    public boolean isAugury() {
        return player.getNumericAttribute(Setting.AUGURY.toString()).intValue() == 1;
    }

    public boolean isPreserve() {
        return player.getNumericAttribute(Setting.PRESERVE.toString()).intValue() == 1;
    }

    public boolean isFriendList() {
        return player.getNumericAttribute(Setting.FRIEND_LIST_TOGGLE.toString()).intValue() == 0;
    }


}
