package com.zenyte.plugins.renewednpc;

import com.google.common.collect.ImmutableMap;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.varrock.LarxusD;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.plaf.IconUIResource;
import java.util.Map;

/**
 * @author Cresinkel
 */


public class Larxus extends NPCPlugin {

    public static final Map<Integer, String> SCROLLS = ImmutableMap.<Integer, String>builder().put(6798, "Earth warrior champion scroll").put(6799, "Ghoul champion scroll")
            .put(6800, "Giant champion scroll").put(6801, "Goblin champion scroll").put(6802, "Hobgoblin champion scroll").put(6803, "Imp champion scroll")
            .put(6804, "Jogre champion scroll").put(6805, "Lesser demon champion scroll").put(6806, "Skeleton champion scroll")
            .put(6807, "Zombie champion scroll").build();

    private static final Integer[] IDS = SCROLLS.keySet().toArray(new Integer[SCROLLS.size()]);

    public static final Item CAPE = new Item(21439);

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new LarxusD(player, npc));
            for (int slot = 27; slot > -1; --slot) {
                if (player.getInventory().getItem(slot) == null) {
                    continue;
                }
                if (ArrayUtils.contains(IDS, player.getInventory().getItem(slot).getId())) {
                    val payload = ("has_gotten_ " + SCROLLS.get(player.getInventory().getItem(slot).getId()).toLowerCase());
                    player.getAttributes().put(payload, 1);
                    player.getInventory().deleteItem(slot, player.getInventory().getItem(slot));
                }
            }
            int count = 0;
            for (int itemId : IDS) {
                val payload = ("has_gotten_ " + SCROLLS.get(itemId).toLowerCase());
                if (player.getAttributes().containsKey(payload)) {
                    count++;
                }
            }
            if (count == 10 && !player.getAttributes().containsKey("has_gotten_champ_cape")) {
                player.getInventory().addOrDrop(CAPE.getId());
                player.getCollectionLog().add(CAPE);
                player.getAttributes().put("has_gotten_champ_cape", 1);
                player.sendMessage("You've turned in all 10 champion scrolls to Larxus, he hands you a Champions Cape.");
                WorldBroadcasts.broadcast(player,BroadcastType.ACHIEVEMENT, CAPE.getId());
            } else if (!player.getAttributes().containsKey("has_gotten_champ_cape")){
                val payload = ("You need " + (10 - count) + " more champion scrolls.");
                player.sendMessage(payload);
            }
        });
        bind("Reclaim", (player, npc) -> {
            if (player.getAttributes().containsKey("has_gotten_champ_cape")) {
                player.getInventory().addOrDrop(CAPE.getId());
                player.getCollectionLog().add(CAPE);
                player.sendMessage("Larxus gives you another Champions Cape.");
            } else {
                player.sendMessage("You have not yet achieved this cape.");
            }
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                3321
        };
    }
}
