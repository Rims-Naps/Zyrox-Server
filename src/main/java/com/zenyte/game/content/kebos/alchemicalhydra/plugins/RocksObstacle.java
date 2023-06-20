package com.zenyte.game.content.kebos.alchemicalhydra.plugins;

import com.zenyte.Constants;
import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.kebos.alchemicalhydra.instance.AlchemicalHydraInstance;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 09/11/2019 | 16:51
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Slf4j
public class RocksObstacle implements ObjectAction {

    private static final Animation climb = new Animation(839);

    @Override
    public int getDelay() {
        return 1;
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (player.getArea() instanceof AlchemicalHydraInstance) {
            val currentTile = new Location(((AlchemicalHydraInstance) player.getArea()).getStaticLocation(player.getLocation()));
            player.setLocation(currentTile);
            val destination = currentTile.transform(0, -2, 0);
            val direction = Utils.getFaceDirection(destination.getX() - currentTile.getX(), destination.getY() - currentTile.getY());
            player.lock();
            WorldTasksManager.schedule(new TickTask() {
                @Override
                public void run() {
                    if (ticks == 0) {
                        player.setAnimation(climb);
                        player.autoForceMovement(destination, 0, 60, direction);
                    } else if (ticks == 1) {
                        player.lock(2);
                        player.sendFilteredMessage("You climb over the rocks.");
                        stop();
                    }
                    ticks++;
                }
            }, 0, 0);
            return;
        }
        if (!Constants.ALCHEMICAL_HYDRA) {
            player.sendMessage("The Alchemical Hydra has been temporarily disabled. Come back later.");
            return;
        }
        val retrievalService = player.getRetrievalService();
        if (retrievalService.getType() == ItemRetrievalService.RetrievalServiceType.ORRVOR_QUO_MATEN && !retrievalService.getContainer().isEmpty()) {
            player.getDialogueManager().start(new Dialogue(player, NpcId.ORRVOR_QUO_MATEN) {
                @Override
                public void buildDialogue() {
                    npc("I have some items of yours here human. I'd recommend you collect them before returning to the lair.", Expression.ORRVOR_QUO_MATEN);
                    options("Do you wish to proceed?",
                            new DialogueOption("Enter the lair and risk losing those items.", () -> enterInstance(player)),
                            new DialogueOption("No - stay out."));
                }
            });
            return;
        }
        enterInstance(player);
    }

    private final void enterInstance(@NotNull final Player player) {
        player.lock();
        try {
            val area = MapBuilder.findEmptyChunk(11, 10);
            val instance = new AlchemicalHydraInstance(player, area);
            instance.constructRegion();
        } catch (OutOfSpaceException e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{34548};
    }
}
