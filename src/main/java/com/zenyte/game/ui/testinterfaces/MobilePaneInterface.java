package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;


/**
 * @author Corey
 * @since 17:26 - 18/07/2019
 */
public class MobilePaneInterface extends Interface {
    
    @Override
    protected void attach() {
        put(30, "Function button");
    }
    
    @Override
    public void open(Player player) {
        throw new IllegalStateException("Panes cannot be opened as interfaces.");
    }
    
    @Override
    protected void build() {
        bind("Function button", (player, slotId, itemId, option) -> {
            if (option == 2) {
                player.getDialogueManager().start(new Dialogue(player) {
                    @Override
                    public void buildDialogue() {
                        val currentSettingName = player.getAttributes().getOrDefault("function button setting", FunctionButtonOption.DISABLE.name()).toString();
                        val currentSetting = FunctionButtonOption.valueOf(currentSettingName);
                        
                        val keyboard = new DialogueOption(FunctionButtonOption.SHOW_KEYBOARD.optionName, () -> {
                            player.getSettings().setSetting(Setting.MOBILE_FUNCTION_BUTTON_SETTING, FunctionButtonOption.SHOW_KEYBOARD.varbitValue);
                            player.getSettings().setSetting(Setting.MOBILE_FUNCTION_BUTTON_ENABLED, 1);
                            player.getSettings().setSetting(Setting.MOUSE_BUTTONS, 0);
                            player.getSettings().setSetting(Setting.MOBILE_SHIFT_CLICK_DROPPING, 0);
                            player.getAttributes().put("function button setting", FunctionButtonOption.SHOW_KEYBOARD.name());
                        });
                        val singleTap = new DialogueOption(FunctionButtonOption.TOGGLE_SINGLE_TAP_MODE.optionName, () -> {
                            player.getSettings().setSetting(Setting.MOBILE_FUNCTION_BUTTON_SETTING, FunctionButtonOption.TOGGLE_SINGLE_TAP_MODE.varbitValue);
                            player.getSettings().setSetting(Setting.MOBILE_FUNCTION_BUTTON_ENABLED, 0);
                            player.getSettings().setSetting(Setting.MOUSE_BUTTONS, 0);
                            player.getSettings().setSetting(Setting.MOBILE_SHIFT_CLICK_DROPPING, 0);
                            player.getAttributes().put("function button setting", FunctionButtonOption.TOGGLE_SINGLE_TAP_MODE.name());
                        });
                        val tapToDrop = new DialogueOption(FunctionButtonOption.TOGGLE_TAP_TO_DROP_MODE.optionName, () -> {
                            player.getSettings().setSetting(Setting.MOBILE_FUNCTION_BUTTON_SETTING, FunctionButtonOption.TOGGLE_TAP_TO_DROP_MODE.varbitValue);
                            player.getSettings().setSetting(Setting.MOBILE_FUNCTION_BUTTON_ENABLED, 0);
                            player.getSettings().setSetting(Setting.MOUSE_BUTTONS, 0);
                            player.getSettings().setSetting(Setting.MOBILE_SHIFT_CLICK_DROPPING, 0);
                            player.getAttributes().put("function button setting", FunctionButtonOption.TOGGLE_TAP_TO_DROP_MODE.name());
                        });
                        val disable = new DialogueOption(FunctionButtonOption.DISABLE.optionName, () -> {
                            player.getSettings().setSetting(Setting.MOBILE_FUNCTION_BUTTON_SETTING, FunctionButtonOption.DISABLE.varbitValue);
                            player.getSettings().setSetting(Setting.MOBILE_FUNCTION_BUTTON_ENABLED, 0);
                            player.getSettings().setSetting(Setting.MOUSE_BUTTONS, 0);
                            player.getSettings().setSetting(Setting.MOBILE_SHIFT_CLICK_DROPPING, 0);
                            player.getAttributes().put("function button setting", FunctionButtonOption.DISABLE.name());
                        });
                        val cancel = new DialogueOption("Cancel");
                        
                        var options = new DialogueOption[]{};
                        switch (currentSetting) {
                            case TOGGLE_SINGLE_TAP_MODE:
                                options = new DialogueOption[]{keyboard, tapToDrop, disable, cancel};
                                break;
                            case TOGGLE_TAP_TO_DROP_MODE:
                                options = new DialogueOption[]{keyboard, singleTap, disable, cancel};
                                break;
                            case SHOW_KEYBOARD:
                                options = new DialogueOption[]{tapToDrop, singleTap, disable, cancel};
                                break;
                            case DISABLE:
                                options = new DialogueOption[]{keyboard, tapToDrop, singleTap, cancel};
                                break;
                        }
                        
                        val optionName = currentSetting == FunctionButtonOption.DISABLE ? "disabled" : currentSetting.optionName.toLowerCase();
                        options("Select function-mode (currently " + optionName + ")", options);
                    }
                });
                return;
            }
            FunctionButtonOption.valueOf(player.getAttributes().get("function button setting").toString()).executeFunction(player);
        });
    }
    
    @Override
    public GameInterface getInterface() {
        return GameInterface.MOBILE_PANE;
    }
    
    @Getter
    @AllArgsConstructor
    public enum FunctionButtonOption {
        TOGGLE_SINGLE_TAP_MODE("Single-tap", 2) {
            @Override
            public void executeFunction(final Player player) {
                player.getSettings().toggleSetting(Setting.MOBILE_FUNCTION_BUTTON_ENABLED);
                player.getSettings().toggleSetting(Setting.MOUSE_BUTTONS);
            }
        },
        TOGGLE_TAP_TO_DROP_MODE("Tap-to-drop", 1) {
            @Override
            public void executeFunction(final Player player) {
                player.getSettings().toggleSetting(Setting.MOBILE_FUNCTION_BUTTON_ENABLED);
                player.getSettings().toggleSetting(Setting.MOBILE_SHIFT_CLICK_DROPPING);
            }
        },
        SHOW_KEYBOARD("Keyboard", 3) {
            @Override
            public void executeFunction(final Player player) {
                player.getPacketDispatcher().sendClientScript(1980, 0, 80);
            }
        },
        DISABLE("Disable", 0) {
            @Override
            public void executeFunction(final Player player) {
            }
        };
        
        private String optionName;
        private int varbitValue;
        
        public abstract void executeFunction(final Player player);
        
    }
}
