package com.zenyte.game.content.skills.magic.resources;

import com.zenyte.game.content.skills.magic.RuneContainer;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 22/07/2019 | 22:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class SecondaryRunePouchResource implements RuneResource {

    private static final Item SECONDARY_RUNE_POUCH = new Item(30006);

    @Override
    public RuneContainer getContainer() {
        return RuneContainer.SECONDARY_RUNE_POUCH;
    }

    @Override
    public int getAmountOf(final Player player, final int runeId, final int amountRequired) {
        if (!player.getInventory().containsItem(SECONDARY_RUNE_POUCH)) {
            return 0;
        }
        return Math.min(player.getSecondaryRunePouch().getAmountOf(runeId), amountRequired);
    }

    @Override
    public void consume(final Player player, final int runeId, final int amount) {
        val pouch = player.getSecondaryRunePouch();
        pouch.getContainer().remove(new Item(runeId, amount));
        pouch.getContainer().refresh(player);
    }

    @Override
    public int[] getAffectedRunes() {
        return null;
    }

}
