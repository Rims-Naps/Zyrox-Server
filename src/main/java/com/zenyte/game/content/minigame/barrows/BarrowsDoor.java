package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.TemporaryDoubleDoor;
import com.zenyte.game.world.object.WorldObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 5. dets 2017 : 0:22.29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class BarrowsDoor implements ObjectAction {

    private static final int[] puzzleDoorTiles = new int[] {
            Location.hash(3551, 9683, 0), Location.hash(3552, 9683, 0),
            Location.hash(3540, 9695, 0), Location.hash(3540, 9694, 0),
            Location.hash(3552, 9706, 0), Location.hash(3551, 9706, 0),
            Location.hash(3563, 9694, 0), Location.hash(3563, 9695, 0),
    };

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (ArrayUtils.contains(puzzleDoorTiles, player.getLocation().getPositionHash())) {
            if (!player.getBarrows().isPuzzleSolved()) {
                player.sendMessage("The door is locked with a strange puzzle.");
                player.getBarrows().getPuzzle().reset();
                GameInterface.BARROWS_PUZZLE.open(player);
                return;
            }
        }
        TemporaryDoubleDoor.executeBarrowsDoors(player, object, location -> player.getBarrows().sendRandomTarget(location));
    }

    @Override
	public Object[] getObjects() {
		return new Object[] { 20679, 20680, 20681, 20682, 20683, 20684, 20685, 20686, 20687, 20688, 20689, 20690, 20691, 20692, 20693, 20694, 20695, 20696, 20698, 20699, 20700, 20701, 20702, 20703, 20704, 20705, 20706, 20707, 20708, 20709, 20710, 20711, 20712, 20713, 20714, 20715, 20717 };
	}

}
