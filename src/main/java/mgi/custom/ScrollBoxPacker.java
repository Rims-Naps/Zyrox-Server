package mgi.custom;

import com.zenyte.game.content.treasuretrails.ClueItem;
import mgi.types.config.items.ItemDefinitions;

/**
 * @author Kris | 23/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ScrollBoxPacker {

    public final void pack() {
        ItemDefinitions.getOrThrow(ClueItem.BEGINNER.getScrollBox()).toBuilder()
                .name("Scroll box (beginner)").isStackable(1).grandExchange(false).isMembers(true).inventoryOptions(new String[]{"Open", null, null, null, "Drop"})
                .price(50).shiftClickIndex(-2).zoom(770).offsetX(1).offsetY(-6).modelPitch(236).modelRoll(1697).modelYaw(0).inventoryModelId(53002).build().pack();

        ItemDefinitions.getOrThrow(ClueItem.EASY.getScrollBox()).toBuilder()
                .name("Scroll box (easy)").isStackable(1).grandExchange(false).isMembers(true).inventoryOptions(new String[]{"Open", null, null, null, "Drop"})
                .price(50).shiftClickIndex(-2).zoom(770).offsetX(1).offsetY(-6).modelPitch(236).modelRoll(1697).modelYaw(0).inventoryModelId(53003).build().pack();

        ItemDefinitions.getOrThrow(ClueItem.MEDIUM.getScrollBox()).toBuilder()
                .name("Scroll box (medium)").isStackable(1).grandExchange(false).isMembers(true).inventoryOptions(new String[]{"Open", null, null, null, "Drop"})
                .price(50).shiftClickIndex(-2).zoom(770).offsetX(1).offsetY(-6).modelPitch(236).modelRoll(1697).modelYaw(0).inventoryModelId(53005).build().pack();

        ItemDefinitions.getOrThrow(ClueItem.HARD.getScrollBox()).toBuilder()
                .name("Scroll box (hard)").isStackable(1).grandExchange(false).isMembers(true).inventoryOptions(new String[]{"Open", null, null, null, "Drop"})
                .price(50).shiftClickIndex(-2).zoom(770).offsetX(1).offsetY(-6).modelPitch(236).modelRoll(1697).modelYaw(0).inventoryModelId(53000).build().pack();

        ItemDefinitions.getOrThrow(ClueItem.ELITE.getScrollBox()).toBuilder()
                .name("Scroll box (elite)").isStackable(1).grandExchange(false).isMembers(true).inventoryOptions(new String[]{"Open", null, null, null, "Drop"})
                .price(50).shiftClickIndex(-2).zoom(770).offsetX(1).offsetY(-6).modelPitch(236).modelRoll(1697).modelYaw(0).inventoryModelId(53004).build().pack();

        ItemDefinitions.getOrThrow(ClueItem.MASTER.getScrollBox()).toBuilder()
                .name("Scroll box (master)").isStackable(1).grandExchange(false).isMembers(true).inventoryOptions(new String[]{"Open", null, null, null, "Drop"})
                .price(50).shiftClickIndex(-2).zoom(770).offsetX(1).offsetY(-6).modelPitch(236).modelRoll(1697).modelYaw(0).inventoryModelId(53001).build().pack();
    }

}
