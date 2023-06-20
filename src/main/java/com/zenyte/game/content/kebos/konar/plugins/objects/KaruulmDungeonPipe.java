package com.zenyte.game.content.kebos.konar.plugins.objects;

import com.zenyte.game.MinimapState;
import com.zenyte.game.content.kebos.konar.map.KaruulmSlayerDungeon;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 10/05/2019 20:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class KaruulmDungeonPipe implements ObjectAction {
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (player.getSkills().getLevel(Skills.AGILITY) < 88) {
            player.sendMessage("You need an Agility level of at least 88 to use this shortcut.");
            return;
        }
        val southern = object.getX() == 1316;
        if (southern && !KaruulmSlayerDungeon.isProtectedAgainstHeat(player)) {
            player.getDialogueManager().start(new Dialogue(player) {

                @Override
                public void buildDialogue() {
                    plain("Warning: The heat of the ground beyond this point can burn you as<br><br>you walk upon it. It is recommended you wear appropriate boots for<br><br>this.");
                    options("Proceed regardless?", "Yes.", "No.")
                            .onOptionOne(() -> enterPipe(player, southern));
                }
            });
        } else {
            enterPipe(player, southern);
        }
    }

    private static void enterPipe(final Player player, final boolean southern) {
        if (!southern) {
            player.addTemporaryAttribute("karuulm_slayer_dungeon_heat_protection", 1);
        }
        player.sendMessage("You enter the pipe..");
        player.getPacketDispatcher().sendMinimapState(MinimapState.MAP_DISABLED);
        new FadeScreen(player, () -> {
            player.sendMessage(".. and find yourself at the other end of the pipe.");
            player.getPacketDispatcher().sendMinimapState(MinimapState.ENABLED);
            player.setLocation(southern ? new Location(1346, 10232, 0) : new Location(1316, 10213, 0));
            if (!southern) {
                player.addTemporaryAttribute("karuulm_slayer_dungeon_heat_protection", 0);
            }
        }).fade(5);
        if (southern) {
            WorldTasksManager.schedule(() -> player.sendMessage("You take a turn right."), 2);
            WorldTasksManager.schedule(() -> player.sendMessage("You take a turn north."), 4);
        } else {
            WorldTasksManager.schedule(() -> player.sendMessage("You take a turn right."), 2);
            WorldTasksManager.schedule(() -> player.sendMessage("You take a turn south."), 4);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{
                34655
        };
    }
}
