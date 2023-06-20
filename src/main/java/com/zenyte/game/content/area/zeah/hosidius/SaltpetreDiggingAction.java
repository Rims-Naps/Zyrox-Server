package com.zenyte.game.content.area.zeah.hosidius;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;

public class SaltpetreDiggingAction extends Action
{

    private Animation DIG_ANIM = new Animation(830);

    @Override
    public boolean start()
    {
        final int freeSlots = player.getInventory().getFreeSlots();
        if(freeSlots == 0)
        {
            player.sendMessage("Your inventory is too full to hold any saltpetre.");
        }
        return freeSlots > 0;
    }

    @Override
    public boolean process()
    {
        return player.getInventory().getFreeSlots() > 0;
    }

    @Override
    public int processWithDelay()
    {
        player.setAnimation(DIG_ANIM);
        int miningLevel = player.getSkills().getLevel(Skills.MINING);
        int random = Utils.random(100);
        if(random <= miningLevel)
        {
            player.getInventory().addOrDrop(new Item(ItemId.SALTPETRE, 2));
        } else
        {
            player.getInventory().addOrDrop(new Item(ItemId.SALTPETRE));
        }
        return 4;
    }
}
