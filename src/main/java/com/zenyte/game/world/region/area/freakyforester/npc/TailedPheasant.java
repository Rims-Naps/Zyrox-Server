package com.zenyte.game.world.region.area.freakyforester.npc;


import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;

public class TailedPheasant extends NPCPlugin
{


    @Override
    public void handle()
    {
        bind("Kill", (player, npc) -> {
            WorldTasksManager.schedule(() -> {
                player.setAnimation(new Animation(422));
                npc.applyHit(new Hit(player, 5, HitType.DEFAULT));
                npc.setTarget(null);
            });
        });
    }

    @Override
    public int[] getNPCs()
    {
        return new int[] { NpcId.PHEASANT, NpcId.PHEASANT_374, NpcId.PHEASANT_5500, NpcId.PHEASANT_5502 };
    }
}
