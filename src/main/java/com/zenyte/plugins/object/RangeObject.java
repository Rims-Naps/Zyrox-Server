package com.zenyte.plugins.object;

import com.zenyte.game.content.skills.cooking.CookingDefinitions;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.skills.CookingD;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 28/11/2018 12:08
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RangeObject implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Cook")) {
            val inventory = player.getInventory();
            val list = new IntArrayList();
            val fire = object.getName().toLowerCase().contains("fire");
            val cookableSet = new ObjectLinkedOpenHashSet<CookingDefinitions.CookingData>();
            /*
            		val list = new ArrayList<Item>(2);
		val cookableList = new ArrayList<CookingData>();
		for (val it : food) {
			if (CookingData.hasRequirements(player, it)) {
				list.add(new Item(it.getCooked()));
				cookableList.add(it);
			}
		}
             */
            loop : for (int i = 0; i < 28; i++) {
                val item = inventory.getItem(i);
                if (item == null)
                    continue;
                val food = CookingDefinitions.CookingData.isCooking(player, item, fire);
                if (food.length > 0) {
                    for (val f : food) {
                        if (!list.contains(f.getCooked())) {
                            list.add(f.getCooked());
                            cookableSet.add(f);
                        }
                        if (list.size() >= 10)
                            break loop;
                    }
                }
            }
            if (!list.isEmpty()) {
                val itemList = new ArrayList<Item>(list.size());
                for (int i : list) {
                    itemList.add(new Item(i));
                }
                val cookableList = new ArrayList<>(cookableSet);
                player.getDialogueManager().start(new CookingD(player, object, true, cookableList, itemList.toArray(new Item[0])));
            } else {
                player.sendMessage("You have nothing to cook at the moment.");
            }
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { "Range", "Cooking range", 21302 };
    }
}
