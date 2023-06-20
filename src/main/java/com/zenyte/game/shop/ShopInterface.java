package com.zenyte.game.shop;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Examine;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Device;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import mgi.types.config.items.ItemDefinitions;
import lombok.val;

import java.util.Optional;

import static com.zenyte.game.util.AccessMask.*;

/**
 * @author Kris | 23/11/2018 17:21
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ShopInterface extends Interface {
    
    private static final Object[] params = new Object[]{
            GameInterface.SHOP_INVENTORY.getId() << 16,
            ContainerType.INVENTORY.getId(),
            4, 7, 0, -1,
            "Value<col=ff9040>",
            "Sell 1<col=ff9040>",
            "Sell 5<col=ff9040>",
            "Sell 10<col=ff9040>",
            "Sell 50<col=ff9040>"
    };
    
    private static Shop getShopAttr(final Player player) {
        val shopAttr = player.getTemporaryAttributes().get("Shop");
        if (!(shopAttr instanceof Shop)) {
            throw new RuntimeException("Unable to open the shop directly.");
        }
        return (Shop) shopAttr;
    }
    
    private static void refreshContainer(final Player player) {
        val container = getShopAttr(player).getContainer();
        container.refresh(player);
    }
    
    private static void resetMobileOptions(final Player player) {
        player.getSettings().setSetting(Setting.MOBILE_SHOP_QUANTITY, 0);
        player.getTemporaryAttributes().remove("mobile buy amount");
    }
    
    private static void changeMobileBuyOption(final Player player, int varBitValue, int optionId) {
        player.getSettings().setSetting(Setting.MOBILE_SHOP_QUANTITY, varBitValue);
        player.addTemporaryAttribute("mobile buy amount", optionId);
        refreshContainer(player);
    }
    
    @Override
    protected void attach() {
        put(5, "Mobile value check");
        put(8, "Mobile buy-1");
        put(10, "Mobile buy-5");
        put(12, "Mobile buy-10");
        put(14, "Mobile buy-50");
        
        put(16, "Interact with item");
    }
    
    @Override
    public void open(Player player) {
        val shop = getShopAttr(player);
        val dispatcher = player.getPacketDispatcher();
        val container = shop.getContainer();
        player.getInterfaceHandler().sendInterface(getInterface());
        GameInterface.SHOP_INVENTORY.open(player);
        resetMobileOptions(player);
        dispatcher.sendClientScript(1074, shop.getName(), container.getType().getId());
        dispatcher.sendClientScript(149, params);
        dispatcher.sendComponentSettings(getInterface(), getComponent("Interact with item"), 0,
                container.getContainerSize(), CLICK_OP1, CLICK_OP2, CLICK_OP3, CLICK_OP4, CLICK_OP5, CLICK_OP6, CLICK_OP10);
        shop.getPlayers().add(player);
        container.setFullUpdate(true);
        container.refresh(player);
    }
    
    public void close(final Player player, final Optional<GameInterface> replacement) {
        if (replacement.isPresent()) {
            //Don't remove shop attribute if opening another shop because the attribute will be the new shop.
            if (replacement.get() == GameInterface.SHOP) {
                return;
            }
        }
        val shop = getShopAttr(player);
        shop.getPlayers().remove(player);
        player.getTemporaryAttributes().remove("Shop");
        if (player.getPlayerInformation().getDevice().equals(Device.MOBILE)) {
            // reset mobile button to price check setting
            resetMobileOptions(player);
        }
    }
    
    @Override
    protected void build() {
        bind("Mobile value check", player -> {
            resetMobileOptions(player);
            refreshContainer(player);
        });
        bind("Mobile buy-1", player -> {
            changeMobileBuyOption(player, 1, ItemOption.BUY_1.optionId);
        });
        bind("Mobile buy-5", player -> {
            changeMobileBuyOption(player, 2, ItemOption.BUY_5.optionId);
        });
        bind("Mobile buy-10", player -> {
            changeMobileBuyOption(player, 3, ItemOption.BUY_10.optionId);
        });
        bind("Mobile buy-50", player -> {
            changeMobileBuyOption(player, 4, ItemOption.BUY_50.optionId);
        });
        
        bind("Interact with item", ((player, interfaceSlotId, itemId, option) -> {
            val slotId = interfaceSlotId - 1;
            val shop = getShopAttr(player);
            val item = shop.getContainer().get(slotId);
            if (ItemDefinitions.isInvalid(itemId) || item == null || item.getId() != itemId)
                return;
    
            val mobileBuyAmount = player.getNumericTemporaryAttribute("mobile buy amount").intValue();
            if (mobileBuyAmount != 0 && option == ItemOption.VALUE.optionId) { // if the first option (value) was replaced with buy-x shortcut on mobile
                option = mobileBuyAmount;
            }
            
            val op = ItemOption.of(option);
            if (op.is(ItemOption.EXAMINE)) {
                Examine.sendItemExamine(player, item);
                return;
            }
            if (op.is(ItemOption.VALUE) || op.is(ItemOption.SHIFTED_MOBILE_VALUE)) {
                val price = shop.getBuyPrice(player, item.getId());
                if (price <= -1) {
                    player.sendMessage(item.getName() + ": currently unavailable.");
                } else if (price == 0) {
                    player.sendMessage(item.getName() + ": currently costs nothing.");
                } else {
                    player.sendMessage(item.getName() + ": currently costs " + Utils.format(price) + " " + shop.getCurrency() + ".");
                }
                return;
            }
            shop.purchase(player, op, slotId);
    
        }));
    }
    
    @Override
    public GameInterface getInterface() {
        return GameInterface.SHOP;
    }
    
    enum ItemOption {
        
        VALUE(1, -1),
        BUY_1(2, 1),
        BUY_5(3, 5),
        BUY_10(4, 10),
        BUY_50(5, 50),
        SHIFTED_MOBILE_VALUE(6, -1),
        EXAMINE(10, -1);
        
        private static final ItemOption[] values = values();
        final int optionId;
        final int amount;
        
        ItemOption(final int optionId, final int amount) {
            this.optionId = optionId;
            this.amount = amount;
        }
        
        /**
         * Gets the ItemOption constant for the input integer option id.
         *
         * @param option the option id.
         * @return the ItemOption constant.
         */
        private static ItemOption of(final int option) {
            val constant = Utils.findMatching(values, value -> value.optionId == option);
            if (constant == null) {
                throw new IllegalArgumentException("Option cannot be " + option + ".");
            }
            return constant;
        }
        
        /**
         * Whether the input enum constant is identical to this option-wise, necessary because
         * {@code SkeletonEnum#equals(final Object other)} is final, thus preventing us from overriding it,
         * and because the options {@code CLEAR_ALL} and {@code WITHDRAW_ALL_BUT_1} are identical
         * option id wise.
         *
         * @param other the other constant to compare against.
         * @return whether the constants are identical option id wise.
         */
        public boolean is(final ItemOption other) {
            return other.optionId == optionId;
        }
    }
}
