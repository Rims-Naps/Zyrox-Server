package com.zenyte.plugins.dialogue.skills;

import com.zenyte.game.content.skills.smithing.SmeltableBar;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.BlastFurnaceBarFinishD;
import com.zenyte.plugins.dialogue.SkillDialogue;
import lombok.val;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class BlastFurnaceBarD extends SkillDialogue {
    
    private final Item[] bars;
    
    public BlastFurnaceBarD(final Player player, final Item[] bars) {
        super(player, bars);
        
        this.bars = bars;
    }
    
    @Override
    public void run(int slotId, int amount) {
        val data = bars[slotId];
        if (data == null) {
            return;
        }

        val inventorySpace = player.getInventory().getFreeSlots();
        val bar = SmeltableBar.getDataByBar(data.getId());

        if (bar == null)
            return;

        int amt = player.getBlastFurnace().getBar(bar);
        amt = amount > amt ? (amt > inventorySpace ? inventorySpace : amt) : (amount > inventorySpace ? inventorySpace : amount);
        player.getBlastFurnace().subBars(bar, amt);
        player.getInventory().addItem(data.getId(), amt);

        if (!player.getBlastFurnace().hasBars()) {
            player.getBlastFurnace().setDispenser(0);
        }

        val payload = amt > 1 ? data.getName().toLowerCase() + "s" : data.getName().toLowerCase();
        player.getBlastFurnace().processVarbits();
        player.getDialogueManager().start(new BlastFurnaceBarFinishD(player, amt, payload));
    }
}
