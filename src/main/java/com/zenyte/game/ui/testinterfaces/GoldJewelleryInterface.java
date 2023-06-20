package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.crafting.CraftingDefinitions.JewelleryData;
import com.zenyte.game.content.skills.crafting.actions.JewelleryCrafting;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.VarManager;
import lombok.val;

import java.util.Objects;

/**
 * @author Kris | 26/05/2019 22:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class GoldJewelleryInterface extends Interface {

    private static final int QUANTITY_VARP = 2224;

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
            put(data.getComponentId(), data.name());
        }
    }

    static {
        VarManager.appendPersistentVarp(QUANTITY_VARP);
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
        val dispatcher = player.getPacketDispatcher();
        if (player.getVarManager().getValue(QUANTITY_VARP) < 1) {
            player.getVarManager().sendVar(QUANTITY_VARP, 1);
        }
        dispatcher.sendComponentSettings(getInterface(), getComponent("Make one"), 9, 9, AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(getInterface(), getComponent("Make five"), 9, 9, AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(getInterface(), getComponent("Make ten"), 9, 9, AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(getInterface(), getComponent("Make x"), 9, 9, AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(getInterface(), getComponent("Make all"), 9, 9, AccessMask.CLICK_OP1);
    }

    @Override
    protected void build() {
        bind("Make one", player -> player.getVarManager().sendVar(QUANTITY_VARP, 1));
        bind("Make five", player -> player.getVarManager().sendVar(QUANTITY_VARP, 5));
        bind("Make ten", player -> player.getVarManager().sendVar(QUANTITY_VARP, 10));
        bind("Make x", player ->
                player.sendInputInt("How many would you like to make?", value ->
                        player.getVarManager().sendVar(QUANTITY_VARP, Math.max(0, Math.min(28, value)))));
        bind("Make all", player -> player.getVarManager().sendVar(QUANTITY_VARP, 28));

        for (JewelleryData data : JewelleryData.VALUES) {
            if (isExcluded(data)) {
                continue;
            }
            bind(data.name(), player -> {
                if (JewelleryData.SLAYER_RING.equals(data) && !player.getSlayer().isUnlocked("Ring bling")) {
                    player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
                    player.sendMessage("You have not unlocked the ability to make slayer rings.");
                    return;
                }
                val amount = Math.max(0, Math.min(28, player.getVarManager().getValue(QUANTITY_VARP)));
                player.getActionManager().setAction(new JewelleryCrafting(data, amount));
            });
        }
    }

    private boolean isExcluded(final JewelleryData data) {
        return data == JewelleryData.ETERNAL_SLAYER_RING || data.getSlotId() != -1;
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.GOLD_JEWELLERY_INTERFACE;
    }
}
