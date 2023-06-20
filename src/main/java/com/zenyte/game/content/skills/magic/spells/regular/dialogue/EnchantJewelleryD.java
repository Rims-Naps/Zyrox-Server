package com.zenyte.game.content.skills.magic.spells.regular.dialogue;

import com.zenyte.game.content.skills.magic.actions.JewelleryEnchantment;
import com.zenyte.game.content.skills.magic.spells.MagicSpell;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.SkillDialogue;

public class EnchantJewelleryD extends SkillDialogue {

    private final MagicSpell spell;
    private final JewelleryEnchantment.JewelleryEnchantmentItem data;

    public EnchantJewelleryD(Player player, MagicSpell spell, JewelleryEnchantment.JewelleryEnchantmentItem data) {
        super(player, data.getProduct());
        this.spell = spell;
        this.data = data;
    }

    @Override
    public void run(int slotId, int amount) {
        player.getActionManager().setAction(new JewelleryEnchantment(spell, data, amount));
    }
}
