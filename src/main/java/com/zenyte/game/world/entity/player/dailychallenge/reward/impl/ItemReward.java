package com.zenyte.game.world.entity.player.dailychallenge.reward.impl;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dailychallenge.reward.ChallengeReward;
import com.zenyte.game.world.entity.player.dailychallenge.reward.RewardType;
import lombok.Getter;

/**
 * @author Tommeh | 04/05/2019 | 13:57
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ItemReward implements ChallengeReward {

    @Getter private final Item item;

    public ItemReward(final int id, final int amount) {
        this.item = new Item(id, amount);
    }

    @Override
    public void apply(Player player) {
        player.getInventory().addItem(item);
        player.sendMessage("<col=ce8500><shad=000000>You have been awarded with " + item.getAmount() + " " + item.getName() + "!");
    }

    @Override
    public RewardType getType() {
        return RewardType.ITEM;
    }
}
