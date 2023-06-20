package com.zenyte.game.packet.in.event;

import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.content.skills.magic.SpellDefinitions;
import com.zenyte.game.content.skills.magic.spells.ObjectSpell;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpLocTEvent implements ClientProtEvent {

    private final int interfaceId, componentId, slotId, objectId, x, y;
    private final boolean run;

    @Override
    public void log(@NotNull final Player player) {
        log(player,
                "Interface: " + interfaceId + ", component: " + componentId + ", slot: " + slotId + ", object id: " + objectId + ", x: " + x + ", y: " + y + ", z: " + player.getPlane() + ", run: " + run);
    }

    @Override
    public void handle(Player player) {
        val location = new Location(x, y, player.getPlane());
        val object = World.getObjectWithId(location, objectId);
        if (object == null) {
            return;
        }
        if (run && player.eligibleForShiftTeleportation()) {
            player.setLocation(new Location(object));
            return;
        } else if (run) {
            player.setRun(true);
        }
        player.stopAll();
        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object), () -> {
            player.stopAll();
            player.faceObject(object);
            if (interfaceId == 218) {
                val spell = Magic.getSpell(player.getCombatDefinitions().getSpellbook(), SpellDefinitions.getSpellName(componentId), ObjectSpell.class);
                if (spell == null) {
                    return;
                }
                spell.execute(player, object);
            }
        }));
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
