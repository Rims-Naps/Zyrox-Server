package com.zenyte.game.content.event.easter2020.plugin.object;

import com.zenyte.game.content.event.easter2020.EasterConstants.EasterItem;
import com.zenyte.game.content.event.easter2020.SplittingHeirs;
import com.zenyte.game.content.event.easter2020.Stage;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * @author Corey
 * @since 08/04/2020
 */
public class CrateObject implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!option.equalsIgnoreCase("Search")) {
            return;
        }
        
        if (!player.getInventory().hasFreeSlots()) {
            player.sendMessage("I should probably clear some space before searching these.");
            return;
        }
        
        val crate = Contents.byPositionHash.get(object.getPositionHash());
        
        player.sendMessage("You search around in the " + (object.getName().equalsIgnoreCase("Crates") ? "crate" : "box") + "...");
        player.lock(1);
        
        WorldTasksManager.schedule(() -> {
            if (crate == null || SplittingHeirs.progressedAtLeast(player, Stage.INCUBATOR_FIXED_SPEAK_TO_BUNNY_JR)) {
                player.sendMessage("You find nothing of interest.");
            } else {
                if (player.getInventory().containsItem(crate.getItemContents())) {
                    player.sendMessage("You find nothing of interest.");
                } else {
                    player.sendMessage(crate.getMessage());
                    player.getInventory().addItem(new Item(crate.getItemContents()));
                }
            }
        }, 1);
    }
    
    @Override
    public Object[] getObjects() {
        val ids = new IntArraySet();
        
        ids.addAll(Contents.objectIds);
        ids.add(46281); // crate exists but does not contain items
        
        return ids.toArray();
    }
    
    @AllArgsConstructor
    @Getter
    enum Contents {
        
        COG(46278, new Location(2192, 4381), EasterItem.COG.getItemId(), "You find a large cog."),
        CHIMNEY(46280, new Location(2188, 4369), EasterItem.CHIMNEY.getItemId(), "You find a large chimney - how odd!"),
        CLEAN_PIPE(46278, new Location(2198, 4374), EasterItem.CLEAN_PIPE.getItemId(), "You find a clean pipe."),
        PISTONS(46279, new Location(2211, 4359), EasterItem.PISTONS.getItemId(), "You find a large set of pistons."),
        SOOTY_PIPE(46282, new Location(2212, 4364), EasterItem.SOOTY_PIPE.getItemId(), "You find a sooty pipe."),
        WET_PIPE(46280, new Location(2213, 4368), EasterItem.WET_PIPE.getItemId(), "You find a wet pipe.");
        
        private static final Int2ObjectOpenHashMap<Contents> byPositionHash;
        private static final Contents[] values = values();
        private static final IntSet objectIds = new IntArraySet(values.length);
        
        static {
            Utils.populateMap(values, byPositionHash = new Int2ObjectOpenHashMap<>(values.length), c -> c.getLocation().getPositionHash());
            for (Contents value : values) {
                objectIds.add(value.getObjectId());
            }
        }
        
        private final int objectId;
        private final Location location;
        private final int itemContents;
        private final String message;
        
    }
    
}
