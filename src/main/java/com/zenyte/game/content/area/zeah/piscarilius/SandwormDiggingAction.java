package com.zenyte.game.content.area.zeah.piscarilius;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;

public class SandwormDiggingAction extends Action
{
    private final Animation DIG_ANIM = new Animation(830);

    private int cycles = 0;

    @Override
    public boolean start()
    {
        if(!player.getInventory().containsItem(ItemId.BUCKET))
        {
            player.sendMessage("You need an empty bucket to dig these up!");
            return false;
        }
        return player.getInventory().containsItem(ItemId.BUCKET);
    }

    @Override
    public boolean process()
    {
        return cycles < 1;
    }

    @Override
    public int processWithDelay()
    {
        player.setAnimation(DIG_ANIM);
        player.getInventory().deleteItem(ItemId.BUCKET, 1);
        if(Utils.random(100) > 49)
        {
            player.getInventory().addItem(ItemId.BUCKET_OF_SAND, 1);
        } else
        {
            player.getInventory().addItem(ItemId.BUCKET_OF_SANDWORMS, 1);
        }
        cycles++;
        return 2;
    }
}
