package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import lombok.var;

/**
 * @author Tommeh | 26-1-2019 | 16:29
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ExperienceModeSelectionInterface extends Interface {

    @Override
    protected void attach() {
        put(7, "50x Combat/25x Skilling");
        put(9, "10x Combat/10x Skilling");
        put(11, "5x Combat/5x Skilling");
    }

    @Override
    public void open(Player player) {
       player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    public boolean click(final Player player, final int componentId, final int slotId, final int itemId, final int option) {
        Handler handler = handlers.get((slotId & 0xFFFF) | ((componentId & 0xFFFF) << 16));
        if (handler == null && (slotId & 0xFFFF) != 0xFFFF) {
            handler = handlers.get((0xFFFF) | ((componentId & 0xFFFF) << 16));
        }
        if (handler == null) {
            if (defaultClickHandler != null) {
                defaultClickHandler.run(player, componentId, slotId, itemId, option);
                return true;
            }
            return false;
        }
        handler.handle(player, slotId, itemId, option);
        return true;
    }

    @Override
    protected void build() {
        bind("50x Combat/25x Skilling", player -> handle(player, "50x Combat/25x Skilling"));
        bind("10x Combat/10x Skilling", player -> handle(player, "10x Combat/10x Skilling"));
        bind("5x Combat/5x Skilling", player -> handle(player, "5x Combat/5x Skilling"));
    }

    private void handle(final Player player, String component) {
        var combat = 1;
        var skilling = 1;
        val componentId = getComponent(component);
        if (componentId == 7) {
            combat = 50;
            skilling = 25;
        } else if (componentId == 9) {
            combat = 10;
            skilling = 10;
        } else {
            combat = 5;
            skilling = 5;
        }
        player.getTemporaryAttributes().put("selected_xp_mode", combat + "-" + skilling);
        val text = component.replace("/", " & ");
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                npc(3308, "Are you sure you would like to choose the<br><col=00080>" + text + "</col> experience mode?", 1);
                options("Are you sure you would like to choose this experience mode?", "Yes!", "No, not yet.")
                        .onOptionOne(() -> {
                            player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
                            setKey(5);
                }).onOptionTwo(() -> GameInterface.EXPERIENCE_MODE_SELECTION.open(player));
                npc(3308, "Very well. Now before you can continue your journey on the main land please choose your appearance. Don't worry, you will still be able to change your appearance during your adventure.", 5).executeAction(() -> {
                    GameInterface.TUTORIAL_MAKE_OVER.open(player);
                });
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.EXPERIENCE_MODE_SELECTION;
    }
}
