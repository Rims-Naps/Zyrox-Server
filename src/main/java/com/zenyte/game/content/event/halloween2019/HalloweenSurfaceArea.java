package com.zenyte.game.content.event.halloween2019;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.FullMovementPlugin;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 03/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class HalloweenSurfaceArea extends Area implements FullMovementPlugin {
    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 1728, 4735 },
                        { 1728, 4672 },
                        { 1791, 4672 },
                        { 1791, 4735 }
                })
        };
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player, boolean logout) {

    }

    @Override
    public String name() {
        return "Halloween mansion";
    }

    @Override
    public boolean processMovement(Player player, int x, int y) {
        val dist = 3;
        if (player.getPlane() != 0) {
            return true;
        }
        if (x == 1781 && y == 4713 && HalloweenUtils.getStage(player) < HalloweenUtils.FREED_GHOST) {
            player.getDialogueManager().start(new Dialogue(player, 922) {
                @Override
                public void buildDialogue() {
                    npc("No entry past here!");
                }
            });
            return false;
        }
        try {
            for (val crow : HalloweenCrow.crows) {
                val crowX = crow.getX();
                val crowY = crow.getY();
                crow.setRun(true);
                if (crowX >= x - dist && crowX <= x + dist && crowY >= y - dist && crowY <= y + dist) {
                    int offsetX = (crowX - x) * 4;
                    int offsetY = (crowY - y) * 4;
                    if (offsetX != 0) {
                        offsetX += Utils.random(1) == 0 ? -Utils.random(2) : Utils.random(2);
                    }
                    if (offsetY != 0) {
                        offsetY += Utils.random(1) == 0 ? -Utils.random(2) : Utils.random(2);
                    }
                    crow.resetWalkSteps();
                    crow.setRandomWalkDelay(4);
                    crow.addWalkSteps(crowX + offsetX, crowY + offsetY, 8, true);
                    for (int i = 0; i < 5; i++) {
                        if (!crow.hasWalkSteps()) {
                            crow.addWalkSteps(crowX + (Utils.random(1) == 0 ? -Utils.random(4) : Utils.random(4)),
                                    crowY + (Utils.random(1) == 0 ? -Utils.random(4) : Utils.random(4)), 4, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        return true;
    }
}
