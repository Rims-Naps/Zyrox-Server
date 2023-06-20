package com.zenyte.game.content.Thanksgiving2021;

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
 * @author Matt (11/11/2021)
 *
 */
@Slf4j
public class ThanksgivingEntranceObject implements ObjectAction {

    private static final Item THANKSGIVING_DINNER = new Item(12862);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!player.getInventory().containsItem(THANKSGIVING_DINNER)) {
            player.sendMessage("The gate is locked shut.");
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {

            @Override
            public void buildDialogue() {
                options("Would you like to enter the Thanksgiving wonderland?", "Yes.", "No.")
                        .onOptionOne(() -> setKey(5));
                options(5, "<col=d80000>You understand this is a instance and you will lose everything you drop or die with?!</col>", "Yes I am willing to risk it..", "No I better bank my items!.")
                        .onOptionOne(() -> {
                            player.getInventory().deleteItem(THANKSGIVING_DINNER);
                            player.lock();
                            new FadeScreenAction(player, 2, () -> {
                                try {
                                    val area = MapBuilder.findEmptyChunk(8, 8);
                                    val instance = new ThanksgivingInstance(player, area);
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
        return new Object[]{};
    }
}
