package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ForcedGate;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import java.util.Optional;

/**
 * @author Kris | 13/04/2019 13:21
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CombatTrainingAreaGate implements ObjectAction {

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        val gate = new ForcedGate<>(player, object);
        gate.handle(Optional.empty());
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                2039, 2041
        };
    }
}
