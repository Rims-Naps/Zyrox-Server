package com.zenyte.game.content;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;


@AllArgsConstructor
public enum PVPEquipment {
    MORRIGANS_THROWING_AXE(ItemId.MORRIGANS_THROWING_AXE, 50,100),
    MORRIGANS_JAVELIN(ItemId.MORRIGANS_JAVELIN, 50,100),

    VESTAS_CHAINBODY(ItemId.VESTAS_CHAINBODY),
    VESTAS_PLATESKIRT(ItemId.VESTAS_PLATESKIRT),
    STATIUSS_FULL_HELM(ItemId.STATIUSS_FULL_HELM),
    STATIUSS_PLATEBODY(ItemId.STATIUSS_PLATEBODY),
    STATIUSS_PLATELEGS(ItemId.STATIUSS_PLATELEGS),
    MORRIGANS_COIF(ItemId.MORRIGANS_COIF),
    MORRIGANS_LEATHER_BODY(ItemId.MORRIGANS_LEATHER_BODY),
    MORRIGANS_LEATHER_CHAPS(ItemId.MORRIGANS_LEATHER_CHAPS),
    ZURIELS_HOOD(ItemId.ZURIELS_HOOD),
    ZURIELS_ROBE_BOTTOM(ItemId.ZURIELS_ROBE_BOTTOM),
    ZURIELS_ROBE_TOP(ItemId.ZURIELS_ROBE_TOP),

    STATIUSS_WARHAMMER(ItemId.STATIUSS_WARHAMMER, 5, 1),
    VESTAS_SPEAR(ItemId.VESTAS_SPEAR, 5, 1),
    VESTAS_LONGSWORD(ItemId.VESTAS_LONGSWORD, 5, 1),
    ZURIELS_STAFF(ItemId.ZURIELS_STAFF,5,1);

    PVPEquipment(int itemId) {
        this.itemId = itemId;
        this.weight = 10;
        this.quantity = 1;
    }

    public static final PVPEquipment[] values = values();

    @Getter private final int itemId;
    @Getter private final int weight;
    @Getter private final int quantity;

    public static int getTotalWeighting() {
        int sum = 0;
        for(val i : values) {
            sum += i.weight;
        }
        return sum;
    }

    public static Item roll() {
        val roll = Utils.random(getTotalWeighting() - 1);
        var currWeight = -1;
        for(val i : values) {
            if(currWeight + i.weight >= roll) {
                return new Item(i.itemId, i.quantity);
            } else {
                currWeight += i.weight;
            }
        }
        return null;
    }
}
