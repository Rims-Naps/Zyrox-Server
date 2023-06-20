package com.zenyte.game.item.degradableitems;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ChargeExtension;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerWrapper;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import mgi.types.config.items.ItemDefinitions;
import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;

import java.text.DecimalFormat;

/**
 * @author Kris | 28. dets 2017 : 1:44.05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Log
public final class ChargesManager {

	private static final int[] SLOTS = new int[] { EquipmentSlot.HELMET.getSlot(), EquipmentSlot.PLATE.getSlot(),
			EquipmentSlot.LEGS.getSlot(), EquipmentSlot.WEAPON.getSlot(), EquipmentSlot.SHIELD.getSlot(), EquipmentSlot.AMULET.getSlot() };

	public static final DecimalFormat FORMATTER = new DecimalFormat("#0.0");

	public ChargesManager(final Player player) {
		this.player = player;
	}

	private final Player player;

	/**
	 * Loops over the player's worn equipment and removes charges if the item is a degradable item and the type matches.
	 * 
	 * @param type
	 */
	public void removeCharges(final DegradeType type) {
	    val container = player.getEquipment().getContainer();
		for (int slot = SLOTS.length - 1; slot >= 0; slot--) {
			final Item item = container.get(SLOTS[slot]);
			if (item == null) {
				continue;
			}
			final DegradableItem deg = DegradableItem.ITEMS.get(item.getId());
            if (deg == null) {
                continue;
            }
            if (deg.getType() != type) {
                continue;
            }
			final int charges = item.getCharges();

			if (charges != deg.getMaximumCharges() && charges <= 0) {
				continue;
			}
			if ((item.getId() == ItemId.CRYSTAL_HELM_50 || item.getId() == ItemId.CRYSTAL_HELM_100 || item.getId() == ItemId.CRYSTAL_HELM_FULL ||
					item.getId() == ItemId.CRYSTAL_BODY_50 || item.getId() == ItemId.CRYSTAL_BODY_100 || item.getId() == ItemId.CRYSTAL_BODY_FULL ||
					item.getId() == ItemId.CRYSTAL_LEGS_50 || item.getId() == ItemId.CRYSTAL_LEGS_100 || item.getId() == ItemId.CRYSTAL_LEGS_FULL)
				&& (player.getEquipment().getId(EquipmentSlot.RING.getSlot()) == 32309
					|| player.getEquipment().getId(EquipmentSlot.RING.getSlot()) == 32312)) {
				if (Utils.random(9) == 0) {
					continue;
				}
			}
            val chargesPlugin = ItemPlugin.getPlugin(item.getId());
            val containerType = container.getType();
            if (chargesPlugin instanceof ChargeExtension
                    && (containerType == ContainerType.INVENTORY || containerType == ContainerType.EQUIPMENT)) {
                ContainerWrapper wrapper = containerType == ContainerType.INVENTORY ? player.getInventory() :
                        player.getEquipment();
                ((ChargeExtension) chargesPlugin).removeCharges(player, item, wrapper, SLOTS[slot], 1);
                continue;
            }
            val currentCharges = item.getCharges();
			item.setCharges(charges - 1);
			if (item.getCharges() <= deg.getMinimumCharges()) {
			    val next = deg.getNextId();
			    if (deg.equals(DegradableItem.ABYSSAL_TENTACLE)) {
					player.getEquipment().set(SLOTS[slot], null);
					player.getInventory().addOrDrop(new Item(next));
				} else if (next == -1) {
                    player.getEquipment().set(SLOTS[slot], null);
                } else if (next != item.getId()) {
                    item.setId(deg.getNextId());
                    val nextDeg = DegradableItem.ITEMS.get(deg.getNextId());
                    if (nextDeg != null) {
                        item.setCharges(nextDeg.getMaximumCharges() - (currentCharges - item.getCharges()));
                    }
                }
				player.getEquipment().refresh(SLOTS[slot]);
				player.getAppearance().setRenderAnimation(player.getAppearance().generateRenderAnimation());
				final String name = ItemDefinitions.getOrThrow(deg.getItemId()).getName();
				player.sendMessage("Your " + name + " " + (name.contains("legs") ? "have" : "has")
						+ (item.getCharges() == 0 ? " fully" : "") + " degraded" + (deg.getNextId() == -1 ? " and turned to dust." : "."));
			}
		}
	}

	/**
	 * Removes the defined amount of charges from the item in arguments. The item nor its container can be null.
     * @param item
     *            the item to remove charges from, can't be null, nor can its container.
     * @param amount
     * @param container
     * @param slotId
     */
	public void removeCharges(@NonNull final Item item, final int amount, Container container, int slotId) {
		val chargesPlugin = ItemPlugin.getPlugin(item.getId());
		val containerType = container.getType();
		if (chargesPlugin instanceof ChargeExtension
				&& (containerType == ContainerType.INVENTORY || containerType == ContainerType.EQUIPMENT)) {
			ContainerWrapper wrapper = containerType == ContainerType.INVENTORY ? player.getInventory() :
                    player.getEquipment();
			((ChargeExtension) chargesPlugin).removeCharges(player, item, wrapper, slotId, amount);
			return;
		}
		val charges = item.getCharges();
		val deg = DegradableItem.ITEMS.get(item.getId());
		if (deg == null) {
			log.info("Unable to remove charges from item: " + item);
			return;
		}
        val currentCharges = item.getCharges();
		item.setCharges(charges - amount);
		val equipment = containerType == ContainerType.EQUIPMENT;
		if (item.getCharges() <= deg.getMinimumCharges()) {
			val nextId = deg.getNextId();
            val nextDeg = DegradableItem.ITEMS.get(deg.getNextId());
            if (nextDeg != null) {
                item.setCharges(nextDeg.getMaximumCharges() - (currentCharges - item.getCharges()));
            }
			container.set(slotId, nextId == -1 ? null : new Item(nextId, item.getAmount(), item.getAttributesCopy()));
			container.refresh(player);
			if (equipment) {
				player.getEquipment().refresh();
			}
			val name = ItemDefinitions.getOrThrow(deg.getItemId()).getName();
			player.sendMessage("Your " + name + " " + (name.contains("legs") ? "have" : "has") + (item.getCharges() == 0 ? " fully" : "")
					+ " degraded" + (deg.getNextId() == -1 ? " and turned to dust." : "."));
		}
	}

	public void checkCharges(@NonNull final Item item) {
		checkCharges(item, true);
	}

	/**
	 * Checks the charges of the item and informs the player of it.
	 * 
	 * @param item
	 *            the item to check the charges of.
	 */
	public void checkCharges(@NonNull final Item item, final boolean filterable) {
		val chargesPlugin = ItemPlugin.getPlugin(item.getId());
		if (chargesPlugin instanceof ChargeExtension) {
			((ChargeExtension) chargesPlugin).checkCharges(player, item);
			return;
		}
		val name = item.getName();
		val fullCharges = DegradableItem.getDefaultCharges(item.getId(), -1);
		if (item.getCharges() == fullCharges) {

			val payload = item.getName().toLowerCase().endsWith("s") ? " are fully charged." : " is fully charged.";
			player.sendMessage("Your " + item.getName() + payload);
		    return;
        }
		if (item.getCharges() <= 0) {
			if(item.getId() == ItemId.BOW_OF_FAERDHINEN_CHARGED)
			{
				player.sendMessage("Your bow is fully charged, but will revert to a weapon seed, blueprints, and 10m coins upon a PvP death. The weapon seed will be lost to your killer in that event.");
			} else {
				val payload = item.getName().toLowerCase().endsWith("s") ? " are completely degraded" : " is completely degraded";
				player.sendMessage("Your " + item.getName() + payload);
			}
			return;
		}

		val deg = DegradableItem.ITEMS.get(item.getId());
		if (deg == null) {
			log.info("Unable to check charges of item: " + item);
			return;
		}
		if (deg.getType() == DegradeType.RECOIL || deg.getType() == DegradeType.USE) {
			player.sendMessage(
					"Your " + name + " has " + item.getCharges() + " charge" + (item.getCharges() == 1 ? "" : "s") + " remaining.");
			return;
		}
		val percentage = FORMATTER.format(
				item.getCharges() / (float) DegradableItem.getFullCharges(item.getId()) * 100);
		player.sendMessage("Your " + name + " " + (name.contains("legs") ? "have " : "has ") + percentage.replace(".0", "") + "% charges remaining.");
	}

}
