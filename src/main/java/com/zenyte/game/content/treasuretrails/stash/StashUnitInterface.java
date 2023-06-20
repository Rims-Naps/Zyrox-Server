package com.zenyte.game.content.treasuretrails.stash;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import lombok.val;
import mgi.types.config.enums.Enums;
import mgi.types.config.enums.IntEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.IntConsumer;

/**
 * @author Kris | 28/01/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class StashUnitInterface extends Interface {

    private static final int HIDEY_BUILD_SCRIPT = 1475;

    @Override
    protected void attach() {

    }

    private final void iterateEnums(@NotNull final IntEnum intEnum, @NotNull final Stash stash, @NotNull final IntConsumer consumer) {
        for (val entry : intEnum.getValues().int2IntEntrySet()) {
            val objectId = entry.getIntKey();
            val coordGrid = entry.getIntValue();
            val unit = StashUnit.getMap().get(objectId);
            if (unit == null) {
                throw new IllegalStateException("Stash unit not located for object " + objectId + "!");
            }
            if (stash.isFilled(unit)) {
                consumer.accept(coordGrid);
            }
        }
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        val stash = player.getStash();
        val containerEnum = Enums.STASH_UNIT_BUILD_STAGES_CONTAINER;
        val container = new Container(ContainerPolicy.NEVER_STACK, ContainerType.STASH_UNIT_BUILD_STAGES, Optional.empty());
        container.setFullUpdate(true);
        iterateEnums(containerEnum, stash, coordGrid -> container.set(((coordGrid >> 14) & 0x3FFF), new Item(0)));
        player.getPacketDispatcher().sendUpdateItemContainer(container);
        val arguments = new int[3];
        val varsEnum = Enums.STASH_UNIT_BUILD_STAGES_VARS;
        iterateEnums(varsEnum, stash, coordGrid -> arguments[((coordGrid >> 14) & 0x3FFF) - 1] |= 1 << (coordGrid & 0x3FFF));
        //Exception in CS2.
        if (stash.isFilled(StashUnit.TOP_WATCHTOWER)) {
            arguments[2] |= 1 << 10;
        }
        player.getPacketDispatcher().sendClientScript(HIDEY_BUILD_SCRIPT, arguments[0], arguments[1], arguments[2]);
    }

    @Override
    protected void build() {

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.STASH_UNIT;
    }
}
