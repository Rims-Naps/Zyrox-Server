package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.plugins.renewednpc.ZenyteGuide;
import lombok.val;

import java.util.Optional;

/**
 * @author Tommeh | 28-10-2018 | 20:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GameModeSetupInterface extends Interface {

    @Override
    protected void attach() {
        put(10, "Regular");
        put(11, "Standard Iron Man");
        put(12, "Hardcore Iron Man");
        put(13, "Ultimate Iron Man");
        put(30, "Confirm");
    }

    @Override
    public void open(Player player) {
        val attr = player.getTemporaryAttributes().get("ironman_setup");
        if (!(attr instanceof String)) {
            return;
        }
        val type = (String) attr;
        if (type.equals("register")) {
            VarCollection.IRONMAN_MODE.update(player, 0);
            VarCollection.UNKNOWN_IRONMAN.update(player, 0);
            VarCollection.PIN_IRONMAN_MODE.update(player, 1);
        } else if (type.equals("review")) {
            VarCollection.PIN_IRONMAN_MODE.update(player, 0);
            VarCollection.IRONMAN_MODE.update(player);
            VarCollection.UNKNOWN_IRONMAN.update(player);
        }
        player.getInterfaceHandler().sendInterface(getInterface());
        for (int i = 17; i < 18; i++) {
            player.getPacketDispatcher().sendComponentVisibility(215, i, true);
        }
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        VarCollection.IRONMAN_MODE.update(player);
    }

    @Override
    protected void build() {
        bind("Regular", player -> handle(player, GameMode.REGULAR));
        bind("Standard Iron Man", player -> handle(player, GameMode.STANDARD_IRON_MAN));
        bind("Hardcore Iron Man", player -> handle(player, GameMode.HARDCORE_IRON_MAN));
        bind("Ultimate Iron Man", player -> handle(player, GameMode.ULTIMATE_IRON_MAN));
        bind("Confirm", this::confirm);
    }

    private void confirm(final Player player) {
        val attr = player.getTemporaryAttributes().get("selected_game_mode");
        if (!(attr instanceof GameMode)) {
            return;
        }
        val mode = (GameMode) attr;
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                npc(3308, "Are you sure you would like to choose the<br><col=00080>" + mode + "</col> mode?", 1);
                options("Are you sure you would like to choose the <col=00080>" + mode + "</col> mode?", "Yes!", "No, not yet.").onOptionOne(() -> {
                    player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
                    setKey(5);
                }).onOptionTwo(() -> {
                    GameInterface.GAME_MODE_SETUP.open(player);
                });
                npc(3308, "Very well then. Could you now select the experience mode you want to play with?", 5).executeAction(() -> GameInterface.EXPERIENCE_MODE_SELECTION.open(player));
            }
        });
    }

    private void handle(final Player player, final GameMode mode) {
        val attr = player.getTemporaryAttributes().get("ironman_setup");
        if (!(attr instanceof String)) {
            return;
        }
        val type = (String) attr;
        if (type.equals("register")) {
            if (player.getVarManager().getBitValue(VarCollection.PIN_IRONMAN_MODE.getId()) == 1) {
                VarCollection.PIN_IRONMAN_MODE.update(player, 0);
            }
            VarCollection.IRONMAN_MODE.update(player, mode.ordinal());
            player.getTemporaryAttributes().put("selected_game_mode", mode);
            player.getDialogueManager().finish();
        } else if (type.equals("review")) {
            val currentMode = player.getGameMode();
            if (mode.equals(currentMode)) {
                return;
            }
            if (currentMode.equals(GameMode.REGULAR)) {
                player.sendMessage("You cannot become a " + mode + " as a regular player.");
                return;
            }
            if (currentMode.equals(GameMode.ULTIMATE_IRON_MAN) && mode.equals(GameMode.HARDCORE_IRON_MAN) || currentMode.equals(GameMode.HARDCORE_IRON_MAN) && mode.equals(GameMode.ULTIMATE_IRON_MAN)) {
                player.sendMessage("You cannot become a " + mode + " after leaving Tutorial Island.");
                return;
            }
            if (currentMode.equals(GameMode.STANDARD_IRON_MAN) && mode.equals(GameMode.HARDCORE_IRON_MAN) || mode.equals(GameMode.ULTIMATE_IRON_MAN)) {
                player.sendMessage("You cannot become a " + mode + " after leaving Tutorial Island.");
                return;
            }
            player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
            player.getDialogueManager().start(new Dialogue(player) {

                @Override
                public void buildDialogue() {
                    plain("Are you sure you want to revoke your current <col=00080>" + currentMode + "</col> mode and switch to the <col=00080>" + mode + "</col> mode instead?");
                    options(TITLE, "Yes, I'm sure.", "No.").onOptionOne(() -> {
                        val oldArmour = ZenyteGuide.STARTER_ITEMS[currentMode.ordinal()];
                        val newArmour = ZenyteGuide.STARTER_ITEMS[mode.ordinal()];
                        player.setGameMode(mode);
                        player.getInventory().deleteItems(oldArmour).onFailure(item -> {
                            player.getEquipment().deleteItem(item).onFailure(i -> {
                                player.getBank().remove(i);
                            });
                        });
                        if (player.isIronman()) {
                            player.getInventory().addItems(newArmour).onFailure(item -> player.getBank().add(item).onFailure(i -> World.spawnFloorItem(i, player)));
                        }
                        setKey(5);
                    }).onOptionTwo(() -> {
                        player.getTemporaryAttributes().put("ironman_setup", "review");
                        open(player);
                    });
                    plain(5, "Congratulations, you have successfully changed your game mode to <col=00080>" + mode + "</col>.");
                }
            });
        }
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.GAME_MODE_SETUP;
    }
}
