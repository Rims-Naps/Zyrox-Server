package com.zenyte.game.world.region.area.darkcaves;

import com.zenyte.game.HintArrow;
import com.zenyte.game.HintArrowPosition;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.LootBroadcastPlugin;
import com.zenyte.plugins.equipment.equip.EquipPlugin;
import com.zenyte.plugins.item.LightSourceItem;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 27. aug 2018 : 23:31:36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class FaladorMoleLairArea extends DarkArea implements EquipPlugin, LootBroadcastPlugin {

    private static final Location MIDDLE = new Location(1761, 5185, 0);
    public static final int[] shields = {
            DiaryReward.FALADOR_SHIELD3.getItem().getId(), DiaryReward.FALADOR_SHIELD4.getItem().getId()
    };

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{new RSPolygon(new int[][]{{1722, 5252}, {1722, 5115}, {1799, 5115}, {1799, 5252}}, 0)};
    }

    @Override
    public void enter(final Player player) {
        locateMole(player);
        refreshOverlay(player);
    }

    private void locateMole(final Player player) {
        val npcs = CharacterLoop.find(MIDDLE, 75, NPC.class, n -> n.getName(player).toLowerCase().equals("giant mole"));
        if (!npcs.isEmpty()) {
            val mole = npcs.get(0);
            val middle = mole.getMiddleLocation();
            if (player.getInventory().containsAnyOf(shields) || player.getEquipment().containsAnyOf(shields)) {
                player.getPacketDispatcher().sendHintArrow(new HintArrow(middle.getX(), middle.getY(), (byte) 100, HintArrowPosition.EAST));
            } else {
                player.getPacketDispatcher().resetHintArrow();
            }
        }
    }

    @Override
    public void leave(final Player player, boolean logout) {
        player.getInterfaceHandler().closeInterface(InterfacePosition.OVERLAY);
        player.getPacketDispatcher().resetHintArrow();
    }

    @Override
    public String name() {
        return "Falador Mole Lair";
    }

    @Override
    public void onContainerModification(final Player player, final Container container, final Item previousItem, final Item currentItem) {
        if (currentItem != null && ArrayUtils.contains(shields, currentItem.getId()) && container.getType().equals(ContainerType.INVENTORY)) {
            locateMole(player);
        } else if (previousItem != null && ArrayUtils.contains(shields, previousItem.getId()) && currentItem == null && container.getType().equals(ContainerType.INVENTORY)) {
            player.getPacketDispatcher().resetHintArrow();
        }
        val previousSource = LightSourceItem.LightSource.getSource(previousItem);
        val currentSource = LightSourceItem.LightSource.getSource(currentItem);
        if (previousSource != null || currentSource != null) {
            refreshOverlay(player);
        }
    }

    @Override
    public boolean handle(Player player, Item item, int slotId, int equipmentSlot) {
        return true;
    }

    @Override
    public int[] getItems() {
        return new int[0];
    }
}
