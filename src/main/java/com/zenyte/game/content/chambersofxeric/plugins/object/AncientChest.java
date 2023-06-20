package com.zenyte.game.content.chambersofxeric.plugins.object;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 06/07/2019 04:34
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AncientChest implements ObjectAction {
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        player.getRaid().ifPresent(raid -> {
            val rewards = raid.getRewards();
            if (rewards != null) {
                rewards.open(player);
                if (player.getAttributes().containsKey("coxrarereward")) {
                    val rare = (Item) player.getAttributes().get("coxrarereward");
                    WorldBroadcasts.broadcast(player, BroadcastType.RARE_DROP, rare, raid.isChallengeMode() ? "Challenge Mode Chambers of Xeric" : "Chambers of Xeric");
                    player.getCollectionLog().add(rare);
                    for (val p : raid.getPlayers()) {
                        p.sendMessage(player.getName() + " - " + Colour.RED.wrap(rare.getName()));
                    }
                    player.getAttributes().remove("coxrarereward");
                }
            }
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                30028
        };
    }
}
