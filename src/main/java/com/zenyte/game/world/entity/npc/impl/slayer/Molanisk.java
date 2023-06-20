package com.zenyte.game.world.entity.npc.impl.slayer;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.player.Player;

public class Molanisk extends NPC implements Spawnable {

    private Player target;
    private int ticksSinceSpawn;

    public Molanisk(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
        ticksSinceSpawn = 0;
    }

    @Override
    public void processNPC() {
        if(!isUnderCombat() && ticksSinceSpawn > 50)
        {
            finish();
            return;
        }
        ticksSinceSpawn++;
        super.processNPC();
    }

    @Override
    public boolean validate(int id, String name) {
        return id == NpcId.MOLANISK;
    }
}
