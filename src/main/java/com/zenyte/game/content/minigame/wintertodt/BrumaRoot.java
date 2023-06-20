package com.zenyte.game.content.minigame.wintertodt;

import com.zenyte.game.content.skills.fletching.FletchingDefinitions;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Corey
 * @since 12:28 - 22/07/2019
 */
public class BrumaRoot extends ItemPlugin implements ItemOnItemAction {
    
    static final int ROOT = 20695;
    static final int KINDLING = 20696;
    
    @Override
    public void handle() {
        setDefault("Drop", (player, item, slotId) -> {
            val itemName = item.getId() == ROOT ? "root" : "kindling";
            player.sendMessage("The " + itemName + " shatters as it hits the floor.");
            player.getInventory().deleteItem(slotId, item);
        });
    }
    
    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val firstSlot = ROOT == from.getId() ? fromSlot : toSlot;
        player.getActionManager().setAction(new FletchKindlingAction(firstSlot));
    }
    
    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[]{new ItemPair(FletchingDefinitions.KNIFE.getId(), ROOT)};
    }
    
    @Override
    public int[] getItems() {
        return new int[]{ROOT, KINDLING};
    }
    
    @RequiredArgsConstructor
    private static final class FletchKindlingAction extends Action {
        
        private final int firstSlot;
        private boolean firstSlotProcessed = false;
        
        @Override
        public boolean start() {
            if (inSafeZone()) {
                return false;
            }
            player.setAnimation(FletchingDefinitions.ANIMATION);
            delay(FletchingDefinitions.ANIMATION.getCeiledDuration() / 600);
            return true;
        }
        
        @Override
        public boolean process() {
            if (inSafeZone()) {
                player.setAnimation(Animation.STOP);
                return false;
            }
            return player.getInventory().containsItems(new Item(ROOT), FletchingDefinitions.KNIFE);
        }
        
        @Override
        public int processWithDelay() {
            val slot = firstSlotProcessed ? getNextSlot() : firstSlot;
    
            if (player.getInventory().getItem(slot).getId() == ROOT) {
                player.getInventory().set(slot, new Item(KINDLING));
            } else {
                player.getInventory().set(getNextSlot(), new Item(KINDLING));
            }
    
            player.sendFilteredMessage("You carefully fletch the root into a bundle of kindling.");
            player.getSkills().addXp(Skills.FLETCHING, player.getSkills().getLevelForXp(Skills.FLETCHING) * 0.6);
            player.setAnimation(FletchingDefinitions.ANIMATION);
            firstSlotProcessed = true;
            
            return FletchingDefinitions.ANIMATION.getCeiledDuration() / 600;
        }
        
        @Override
        public void stop() {
        }
        
        private boolean inSafeZone() {
            if (Wintertodt.insideSafeArea(player)) {
                player.sendMessage("Your hands are too cold to fletch here - move closer to the braziers.");
                return true;
            }
            return false;
        }
        
        private int getNextSlot() {
            return player.getInventory().getContainer().getFirst(ROOT).getIntKey();
        }
        
    }
    
}
