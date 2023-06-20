package com.zenyte.game.content.skills.hunter.object.plugin;

import com.zenyte.game.content.skills.hunter.HunterUtils;
import com.zenyte.game.content.skills.hunter.actions.CheckPlacedTrap;
import com.zenyte.game.content.skills.hunter.actions.DismantlePlacedTrap;
import com.zenyte.game.content.skills.hunter.node.TrapType;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 30/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BoxTrapObject implements HunterObjectPlugin {

    @Override
    public void handle(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (option.equalsIgnoreCase("Reset") || player.getLocation().matches(object)) {
            player.setRouteEvent(new TileEvent(player, new TileStrategy(object), getRunnable(player, object, name, optionId, option), getDelay()));
        } else {
            player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object), getRunnable(player, object, name, optionId, option), getDelay()));
        }
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val trap = HunterUtils.findTrap(TrapType.BOX_TRAP, object, null).orElseThrow(RuntimeException::new);
        switch(option) {
            case "Check":
                player.getActionManager().setAction(new CheckPlacedTrap(trap, false));
                break;
            case "Investigate":
                //TODO: More of this.
                player.sendMessage("This trap has been set without any bait.");
                player.sendMessage("This trap hasn't been smoked.");
                break;
            case "Dismantle":
                player.getActionManager().setAction(new DismantlePlacedTrap(trap));
                break;
            case "Reset":
                player.getActionManager().setAction(new CheckPlacedTrap(trap, true));
                break;
            default:
                throw new IllegalStateException(option);
        }
    }

    @Override
    public TrapType type() {
        return TrapType.BOX_TRAP;
    }
}