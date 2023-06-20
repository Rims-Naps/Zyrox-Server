package com.zenyte.game.content.boss.bryophyta.plugins;

import com.zenyte.game.content.boss.bryophyta.BryophytaInstance;
import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.content.skills.slayer.SlayerMaster;
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
 * @author Tommeh | 17/05/2019 | 15:03
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Slf4j
public class BryophytaEntranceObject implements ObjectAction {

    private static final Item MOSSY_KEY = new Item(22374);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (player.getSlayer().getAssignment().getTask() != RegularTask.BRYOPHYTA) {
            if (!player.getInventory().containsItem(MOSSY_KEY)) {
                player.sendMessage("The gate is locked shut.");
                return;
            }
        }
        player.getDialogueManager().start(new Dialogue(player) {

            @Override
            public void buildDialogue() {
                plain("Warning! You're about to enter an instanced area, anything left on<br><br>the ground when you leave will be lost. Would you like to continue?");
                options("Are you sure you wish to open it?", "Yes, let's go!", "I don't think I'm quite ready yet.")
                        .onOptionOne(() -> {
                            player.getAttributes().put("amount_of_growthlings_killed", "0");
                            if (player.getSlayer().getAssignment().getTask() != RegularTask.BRYOPHYTA) {
                                player.getInventory().deleteItem(MOSSY_KEY);
                            }
                            player.lock();
                            new FadeScreenAction(player, 2, () -> {
                                try {
                                    val area = MapBuilder.findEmptyChunk(8, 8);
                                    val instance = new BryophytaInstance(player, area);
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
        return new Object[] { 32534 };
    }
}
