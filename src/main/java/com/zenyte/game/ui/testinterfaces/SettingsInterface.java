package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import mgi.types.config.enums.EnumDefinitions;
import com.zenyte.plugins.interfaces.KeybindingInterface;
import lombok.val;

import static com.zenyte.game.constants.GameInterface.HOUSE_OPTIONS_TAB;

/**
 * @author Kris | 24/10/2018 12:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SettingsInterface extends Interface {

    private static final int[] volumes = new int[]{4, 3, 2, 1, 0};

    @Override
    protected void attach() {
        put(18, "Brightness 1");
        put(19, "Brightness 2");
        put(20, "Brightness 3");
        put(21, "Brightness 4");
        put(24, "FPS 15");
        put(26, "FPS 20");
        put(28, "FPS 30");
        put(30, "FPS Max");
        put(35, "Advanced options");
        put(39, "Toggle tap through chatbox");
        put(41, "Toggle store orb");
        put(45, "Music volume 0");
        put(46, "Music volume 1");
        put(47, "Music volume 2");
        put(48, "Music volume 3");
        put(49, "Music volume 4");
        put(51, "Sound effect volume 0");
        put(52, "Sound effect volume 1");
        put(53, "Sound effect volume 2");
        put(54, "Sound effect volume 3");
        put(55, "Sound effect volume 4");
        put(57, "Area sound volume 0");
        put(58, "Area sound volume 1");
        put(59, "Area sound volume 2");
        put(60, "Area sound volume 3");
        put(61, "Area sound volume 4");
        put(63, "Chat effects");
        put(65, "Split private chat");
        put(67, "Hide private chat");
        put(69, "Profanity filter");
        put(71, "Notifications");
        put(73, "Login/Logout notification timeout");
        put(75, "Display name");
        put(77, "Number of mouse buttons");
        put(79, "Middle mouse camera");
        put(81, "Follower options priority");
        put(83, "Keybinding");
        put(85, "Shift click drop");
        put(87, "Toggle function button");
        put(92, "Accept aid");
        put(95, "Toggle run");
        put(100, "Open bond pouch");
        put(98, "House options");
        put(106, "Player attack options");
        put(107, "NPC attack options");
    }

    @Override
    public void open(Player player) {
        val attackOptionsEnum = EnumDefinitions.get(1246);
        val dispatcher = player.getPacketDispatcher();
        player.getInterfaceHandler().sendInterface(getInterface());
        dispatcher.sendComponentSettings(getInterface(), getComponent("Player attack options"), 1, attackOptionsEnum.getSize(), AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(getInterface(), getComponent("NPC attack options"), 1, attackOptionsEnum.getSize(), AccessMask.CLICK_OP1);
    }

    @Override
    protected void build() {
        bind("Advanced options", GameInterface.ADVANCED_SETTINGS::open);
        bind("Notifications", player -> {
            if (player.isLocked()) {
                return;
            }
            if (player.isUnderCombat()) {
                player.sendMessage("You can't do this while in combat.");
                return;
            }
            GameInterface.NOTIFICATION_SETTINGS.open(player);
        });
        bind("Player attack options", (player, slotId, itemId, option) -> player.getSettings().setSetting(Setting.PLAYER_ATTACK_OPTION, slotId - 1));
        bind("NPC attack options", (player, slotId, itemId, option) -> player.getSettings().setSetting(Setting.NPC_ATTACK_OPTION, slotId - 1));
        bind("Music volume 0", player -> setMusicVolume(player, 0));
        bind("Music volume 1", player -> setMusicVolume(player, 1));
        bind("Music volume 2", player -> setMusicVolume(player, 2));
        bind("Music volume 3", player -> setMusicVolume(player, 3));
        bind("Music volume 4", player -> setMusicVolume(player, 4));
        bind("Sound effect volume 0", player -> setSoundEffectVolume(player, 0));
        bind("Sound effect volume 1", player -> setSoundEffectVolume(player, 1));
        bind("Sound effect volume 2", player -> setSoundEffectVolume(player, 2));
        bind("Sound effect volume 3", player -> setSoundEffectVolume(player, 3));
        bind("Sound effect volume 4", player -> setSoundEffectVolume(player, 4));
        bind("Area sound volume 0", player -> setAreaSoundVolume(player, 0));
        bind("Area sound volume 1", player -> setAreaSoundVolume(player, 1));
        bind("Area sound volume 2", player -> setAreaSoundVolume(player, 2));
        bind("Area sound volume 3", player -> setAreaSoundVolume(player, 3));
        bind("Area sound volume 4", player -> setAreaSoundVolume(player, 4));
        bind("Brightness 1", player -> player.getSettings().setSetting(Setting.SCREEN_BRIGHTNESS, 1));
        bind("Brightness 2", player -> player.getSettings().setSetting(Setting.SCREEN_BRIGHTNESS, 2));
        bind("Brightness 3", player -> player.getSettings().setSetting(Setting.SCREEN_BRIGHTNESS, 3));
        bind("Brightness 4", player -> player.getSettings().setSetting(Setting.SCREEN_BRIGHTNESS, 4));
        bind("FPS 15", player -> player.getSettings().setSetting(Setting.FPS_CONTROL, 1));
        bind("FPS 20", player -> player.getSettings().setSetting(Setting.FPS_CONTROL, 2));
        bind("FPS 30", player -> player.getSettings().setSetting(Setting.FPS_CONTROL, 3));
        bind("FPS Max", player -> player.getSettings().setSetting(Setting.FPS_CONTROL, 4));
        bind("Toggle tap through chatbox", player -> player.getSettings().toggleSetting(Setting.CLICK_THROUGH_CHATBOX));
        bind("Chat effects", player -> player.getSettings().toggleSetting(Setting.CHAT_EFFECTS));
        bind("Split private chat", player -> player.getSettings().toggleSetting(Setting.SPLIT_PRIVATE_CHAT));
        bind("Hide private chat", player -> player.getSettings().toggleSetting(Setting.HIDE_PRIVATE_CHAT));
        bind("Profanity filter", player -> player.getSettings().toggleSetting(Setting.PROFANITY_FILTER));
        bind("Login/Logout notification timeout", player -> player.getSettings().toggleSetting(Setting.LOGIN_NOTIFICATIONS));
        bind("Number of mouse buttons", player -> {
            player.getSettings().toggleSetting(Setting.MOUSE_BUTTONS);
            val funcButtonSetting = player.getAttributes().get("function button setting");
            if (funcButtonSetting != null) {
                if (funcButtonSetting.toString().equals(MobilePaneInterface.FunctionButtonOption.TOGGLE_SINGLE_TAP_MODE.name())) {
                    player.getSettings().toggleSetting(Setting.MOBILE_FUNCTION_BUTTON_ENABLED);
                }
            }
        });
        bind("Middle mouse camera", player -> player.getSettings().toggleSetting(Setting.MIDDLE_MOUSE_BUTTON));
        bind("Follower options priority", player -> player.getSettings().toggleSetting(Setting.PET_OPTIONS));
        bind("Shift click drop", player -> {
            if (player.isOnMobile()) {
                player.getSettings().toggleSetting(Setting.MOBILE_SHIFT_CLICK_DROPPING);
                val funcButtonSetting = player.getAttributes().get("function button setting");
                if (funcButtonSetting != null) {
                    if (funcButtonSetting.toString().equals(MobilePaneInterface.FunctionButtonOption.TOGGLE_TAP_TO_DROP_MODE.name())) {
                        player.getSettings().toggleSetting(Setting.MOBILE_FUNCTION_BUTTON_ENABLED);
                    }
                }
            } else {
                player.getSettings().toggleSetting(Setting.SHIFT_CLICK_DROPPING);
            }
        });
        bind("Toggle function button", player -> player.getSettings().toggleSetting(Setting.TOGGLE_FUNCTION_BUTTON));
        bind("Toggle store orb", player -> {
//            player.getSettings().toggleSetting(Setting.TOGGLE_STORE_ORB);
            player.getSettings().setSetting(Setting.TOGGLE_STORE_ORB, 0);
            player.sendMessage("Mobile store not yet supported");
        });
        bind("Accept aid", player -> {
            if (player.isIronman()) {
                player.getSettings().setSetting(Setting.ACCEPT_AID, 0);
                player.sendMessage("Ironmen cannot accept aid from other players.");
                return;
            }
            player.getSettings().toggleSetting(Setting.ACCEPT_AID);
        });
        bind("Toggle run", player -> {
            player.getInterfaceHandler().closeInterfaces();
            player.setRun(!player.isRun());
        });
        bind("Keybinding", player -> {
            if (player.isLocked()) {
                return;
            }
            //TODO Rewrite as GameInterface
            player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, KeybindingInterface.INTERFACE);
        });
        bind("House options", HOUSE_OPTIONS_TAB::open);
    }

    private void setMusicVolume(final Player player, final int volume) {
        if (volume != 0) {
            if (player.getIntSetting(Setting.MUSIC_VOLUME) == volumes[0]) {
                player.getMusic().restartCurrent();
            }
        }
        player.getSettings().setSetting(Setting.MUSIC_VOLUME, volumes[volume]);
    }

    private void setSoundEffectVolume(final Player player, final int volume) {
        player.getSettings().setSetting(Setting.SOUND_EFFECT_VOLUME, volumes[volume]);
    }

    private void setAreaSoundVolume(final Player player, final int volume) {
        player.getSettings().setSetting(Setting.AREA_SOUND_VOLUME, volumes[volume]);
    }

    @Override
    public boolean isInterruptedOnLock() {
        return false;
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.SETTINGS;
    }
}
