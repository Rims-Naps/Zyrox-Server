package com.zenyte.plugins.item;

import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Kris | 16/03/2019 02:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MagicCape extends ItemPlugin {
    @Override
    public void handle() {
        bind("Spellbook", (player, item, slotId) -> MaxCape.sendSpellbookDialogue(player));
    }

    @Override
    public int[] getItems() {
        return SkillcapePerk.MAGIC.getSkillCapes();
    }
}
