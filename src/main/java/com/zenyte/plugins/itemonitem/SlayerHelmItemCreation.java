package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

/**
 * @author Tommeh | 19 mei 2018 | 16:04:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class SlayerHelmItemCreation implements ItemOnItemAction {
	
	public static final Item[] REQUIRED_ITEMS = {
			new Item(4166), new Item(4164), new Item(4168),
			new Item(4155), new Item(4551), new Item(8921)
	};

    public static final Item SLAYER_HELM = new Item(11864);
	
    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		for (int i = 8901; i <= 8919; i+=2) {
			if (from.getId() == i || to.getId() == i) {
				player.sendMessage("This black mask must be uncharged first in order for it to be used to assemble a slayer helmet.");
				return;
			}
		}
		for (int i = 11774; i <= 11783; i++) {
			if (from.getId() == i || to.getId() == i) {
				player.sendMessage("This black mask must be uncharged first in order for it to be used to assemble a slayer helmet.");
				return;
			}
		}
		int imbuedId = -1;
        for (int i = 11774; i <= 11784; i++) {
            if (player.getInventory().containsItem(i, 1)) {
                imbuedId = i;
                break;
            }
        }
    	if (player.getSkills().getLevel(Skills.CRAFTING) < 55) {
    		player.sendMessage("You need a Crafting level of at least 55 to assemble a slayer helmet.");
    		return;
    	}
    	if (!player.getSlayer().isUnlocked("Malevolent masquerade")) {
    		player.sendMessage("You must unlock the slayer reward Malevolent masquerade in order to assemble a slayer helmet");
    		return;
    	}
    	final StringBuilder builder = new StringBuilder();
    	for (final Item item : REQUIRED_ITEMS) {
    		if (!player.getInventory().containsItem(item)) {
    		    if (item.getId() != 8921 || imbuedId == -1 || !player.getInventory().containsItem(imbuedId, 1)) {
                    builder.append(item.getName().toLowerCase()).append(", ");
                }
    		}
    	}
    	if (builder.length() > 0) {
    		val message = "You are still missing the following items in order to assemble a slayer helmet;<br>" + builder.toString().replaceAll(", $", ".");
    		val bldr = new StringBuilder(message);
    		val index = message.lastIndexOf(", ");
    		if (index >= 0) {
                bldr.replace(message.lastIndexOf(", "), message.lastIndexOf(", ") + 1, " and");
            }
    		player.sendMessage(bldr.toString());
    		return;
    	}
    	boolean imbued = false;
    	for (val item : REQUIRED_ITEMS) {
    	    if (item.getId() == 8921 && !player.getInventory().containsItem(8921, 1)) {
    	        imbued = true;
    	        player.getInventory().deleteItem(new Item(imbuedId, 1));
            } else {
                player.getInventory().deleteItem(item);
            }
    	}
    	player.getInventory().addItem(imbued ? new Item(11865, 1) : SLAYER_HELM);
    	player.sendMessage("You successfully assembled a slayer helmet.");
    }

    @Override
    public int[] getItems() {
		val list = new IntArrayList();
		for (val item : REQUIRED_ITEMS) {
			list.add(item.getId());
		}
		for (int i = 8901; i <= 8919; i+=2) {
			list.add(i);
		}
		for (int i = 11774; i <= 11784; i++) {
            list.add(i);
        }
		return list.toArray(new int[list.size()]);
    }
}
