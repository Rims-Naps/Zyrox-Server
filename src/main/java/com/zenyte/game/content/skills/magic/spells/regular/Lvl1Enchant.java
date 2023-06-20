package com.zenyte.game.content.skills.magic.spells.regular;

import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.magic.actions.JewelleryEnchantment;
import com.zenyte.game.content.skills.magic.actions.JewelleryEnchantmentType;
import com.zenyte.game.content.skills.magic.actions.JewelleryEnchantment.JewelleryEnchantmentItem;
import com.zenyte.game.content.skills.magic.spells.ItemSpell;
import com.zenyte.game.content.skills.magic.spells.regular.dialogue.EnchantJewelleryD;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 21 mei 2018 | 17:20:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class Lvl1Enchant implements ItemSpell {

    @Override
    public int getDelay() {
        return 2000;
    }

    @Override
    public boolean spellEffect(final Player player, final Item item, final int slot) {
        val data = JewelleryEnchantmentItem.getDataByMaterial(item, JewelleryEnchantmentType.LVL1_ENCHANTMENT);
        if (data != null && JewelleryEnchantment.check(player, data)) {
            player.getDialogueManager().start(new EnchantJewelleryD(player, this, data));
            return false;
        } else {
            player.sendMessage("This spell can only be cast on sapphire or opal amulets, necklaces, rings and " + "bracelets, and on shapes in the Mage Training Arena.");
            return false;
        }
    }

    @Override
    public Spellbook getSpellbook() {
        return Spellbook.NORMAL;
    }

}
