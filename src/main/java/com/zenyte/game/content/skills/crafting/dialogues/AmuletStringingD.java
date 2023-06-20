package com.zenyte.game.content.skills.crafting.dialogues;

import com.zenyte.game.content.skills.crafting.CraftingDefinitions;
import com.zenyte.game.content.skills.crafting.actions.AmuletStringingCrafting;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.SkillDialogue;

public class AmuletStringingD extends SkillDialogue {

    private CraftingDefinitions.AmuletStringingData data;

    public AmuletStringingD(Player player, CraftingDefinitions.AmuletStringingData data) {
        super(player, data.getProduct());
        this.data = data;
    }

    @Override
    public void run(int slotId, int amount) {
        player.getActionManager().setAction(new AmuletStringingCrafting(data, amount));
    }
}
