package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.crafting.CraftingDefinitions.JewelleryData;
import com.zenyte.game.content.skills.crafting.actions.JewelleryCrafting;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 26/05/2019 22:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SilverJewelleryInterface extends Interface {

    @Override
    protected void attach() {
        put(56, "Make one");
        put(57, "Make five");
        put(58, "Make ten");
        put(59, "Make x");
        put(60, "Make all");
        for (JewelleryData data : JewelleryData.VALUES) {
            if (isExcluded(data)) {
                continue;
            }
            put(data.getComponentId(), data.getSlotId(), data.name());
        }
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
        val dispatcher = player.getPacketDispatcher();

        dispatcher.sendComponentSettings(getInterface(), 4, 0, 11, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2,
                AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP5);
        dispatcher.sendComponentSettings(getInterface(), 6, 0, 7, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2,
                AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP5);
    }

    @Override
    protected void build() {
        for (JewelleryData data : JewelleryData.VALUES) {
            if (isExcluded(data)) {
                continue;
            }
            bind(data.name(), (player, slotId, itemId, option) -> {
                val amount = option == 1 ? 1 : option == 2 ? 5 : option == 3 ? 10 : 28;
                if (option == 4) {
                    player.sendInputInt("Enter amount:", value -> player.getActionManager().setAction(new JewelleryCrafting(data, value)));
                } else {
                    player.getActionManager().setAction(new JewelleryCrafting(data, amount));
                }
            });
        }
    }

    private boolean isExcluded(final JewelleryData data) {
        return data.getSlotId() == -1;
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.SILVER_JEWELLERY_INTERFACE;
    }
}
