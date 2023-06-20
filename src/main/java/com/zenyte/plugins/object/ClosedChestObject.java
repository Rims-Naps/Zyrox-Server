package com.zenyte.plugins.object;

import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.content.treasuretrails.challenges.KeyRequest;
import com.zenyte.game.content.treasuretrails.clues.CrypticClue;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;

/**
 * @author Kris | 12/04/2019 15:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ClosedChestObject extends SearchableClueObjectPlugin implements ObjectAction {

    private static final Animation animation = new Animation(832);

    public static final Int2IntOpenHashMap map = new Int2IntOpenHashMap();

    static {
        map.put(103, 104);
        map.put(170, 171);
        //map.put(377, 378);
        map.put(375, 378);
        map.put(376, 378);
        map.put(1994, 1995);
        map.put(2436, 2437);
        map.put(5108, 5109);
        map.put(7350, 7351);
        map.put(8797, 8798);
        map.put(9754, 9755);
        map.put(9756, 9757);
        map.put(9785, 9759);
        map.put(9760, 9761);
        map.put(12120, 12121);
        map.put(12735, 12736);
        map.put(12768, 12769);
        map.put(16116, 16117);
        map.put(16118, 16119);
        map.put(25592, 25593);
        map.put(25793, 25794);
        map.put(31987, 31988);
    }

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (option.equalsIgnoreCase("Open")) {
            val clue = CrypticClue.getClueFromKeyObject(object);
            if (clue.isPresent()) {
                val k = ((KeyRequest) clue.get().getChallenge()).getKeyId();
                if (!player.getInventory().containsItem(k, 1)) {
                    player.sendMessage("The chest is firmly shut.");
                    return;
                }
            }
            player.sendSound(52);
            player.setAnimation(animation);
            player.lock(1);
            player.sendMessage("You open the chest...");
            clue.ifPresent(k -> player.getInventory().deleteItem(((KeyRequest) k.getChallenge()).getKeyId(), 1));
            swapObject(object);
        } else if (option.equalsIgnoreCase("Close") || option.equalsIgnoreCase("Shut")) {
            player.setAnimation(animation);
            player.sendSound(51);
            player.lock(1);
            player.sendMessage("You shut the chest...");
            swapObject(object);
        } else if (option.equalsIgnoreCase("Search")) {
            if (TreasureTrail.searchKeyObject(player, object, option) || TreasureTrail.search(player, object, option)) {
                return;
            }
            player.sendMessage("You find nothing.");
        }
    }

    @Override
    public Object[] getObjects() {
        val set = new IntOpenHashSet();
        set.addAll(map.keySet());
        set.addAll(map.values());
        return set.toArray();
    }

    @Override
    protected Int2IntOpenHashMap map() {
        return map;
    }
}
