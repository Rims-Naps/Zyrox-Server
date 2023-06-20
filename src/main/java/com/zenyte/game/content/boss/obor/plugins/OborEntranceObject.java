package com.zenyte.game.content.boss.obor.plugins;

import com.zenyte.game.content.boss.obor.OborInstance;
import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.actions.FadeScreenAction;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Tommeh | 13/05/2019 | 22:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Slf4j
public class OborEntranceObject implements ObjectAction {

    private static final Item GIANT_KEY = new Item(20754);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (player.getSlayer().getAssignment().getTask() != RegularTask.OBOR){
            if (!player.getInventory().containsItem(GIANT_KEY)) {
                player.sendMessage("The gate is locked shut.");
                return;
            }
        }
        player.getDialogueManager().start(new Dialogue(player) {

            @Override
            public void buildDialogue() {
                options("Enter Obor's Lair?", "Yes.", "No.")
                        .onOptionOne(() -> setKey(5));
                options(5, "<col=d80000>You will lose all of your items dropped if you die!</col>", "I know I'm risking everything I have.", "I need to prepare some more.")
                        .onOptionOne(() -> {
                            if (player.getSlayer().getAssignment().getTask() != RegularTask.OBOR) {
                                player.getInventory().deleteItem(GIANT_KEY);
                            }
                            player.lock();
                            new FadeScreenAction(player, 2, () -> {
                                try {
                                    val area = MapBuilder.findEmptyChunk(8, 8);
                                    val instance = new OborInstance(player, area);
                                    instance.constructRegion();
                                } catch (OutOfSpaceException e) {
                                    log.error(Strings.EMPTY, e);
                                }
                            }).run();
                        });
            }
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{29486, 29487};
    }
}
