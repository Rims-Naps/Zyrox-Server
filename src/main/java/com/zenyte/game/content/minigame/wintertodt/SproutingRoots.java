package com.zenyte.game.content.minigame.wintertodt;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.RequiredArgsConstructor;

/**
 * @author Corey
 * @since 22:49 - 25/07/2019
 */
public class SproutingRoots implements ObjectAction {
    
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equalsIgnoreCase("pick")) {
            player.getActionManager().setAction(new PickBrumaHerbAction());
        }
    }
    
    @Override
    public Object[] getObjects() {
        return new Object[]{29315};
    }
    
    @RequiredArgsConstructor
    private static final class PickBrumaHerbAction extends Action {
        
        static final Item HERB = new Item(RejuvenationPotion.BRUMA_HERB);
        static final Animation PICK_ANIM = new Animation(2282);
        
        @Override
        public boolean start() {
            if (Wintertodt.betweenRounds()) {
                player.sendFilteredMessage("There's no need to do that at this time.");
                return false;
            }
            player.setAnimation(PICK_ANIM);
            delay((PICK_ANIM.getCeiledDuration() / 600) + 1);
            return true;
        }
        
        @Override
        public boolean process() {
            return player.getInventory().hasFreeSlots();
        }
        
        @Override
        public int processWithDelay() {
            player.getInventory().addItem(HERB);
            player.getSkills().addXp(Skills.FARMING, 2);
            player.setAnimation(PICK_ANIM);
            player.sendSound(new SoundEffect(2581));
            return PICK_ANIM.getCeiledDuration() / 600;
        }
        
        @Override
        public void stop() {
            player.setAnimation(Animation.STOP);
        }
        
    }
    
    public static final class DropBrumaHerb extends ItemPlugin {
        
        @Override
        public void handle() {
            setDefault("Drop", (player, item, slotId) -> {
                player.sendMessage("The herb shatters as it hits the floor.");
                player.getInventory().deleteItem(slotId, item);
            });
        }
        
        @Override
        public int[] getItems() {
            return new int[]{RejuvenationPotion.BRUMA_HERB};
        }
        
    }
    
}
