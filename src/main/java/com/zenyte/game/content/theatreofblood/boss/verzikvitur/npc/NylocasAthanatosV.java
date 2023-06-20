package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.theatreofblood.boss.verzikvitur.VerzikRoom;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.model.NylocasTypeV;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NpcId;

public class NylocasAthanatosV extends NylocasV{

    public NylocasAthanatosV(final VerzikRoom room, final Location location, final Direction direction) {
        super(room, NpcId.NYLOCAS_ATHANATOS, location, direction, NylocasTypeV.PURPLY);
        setAttackDistance(0);
    }

    @Override
    public void autoRetaliate(final Entity source) {
    }

    @Override
    public boolean checkAggressivity() {
        return true;
    }
}
