package com.zenyte.game.content.theatreofblood.boss.nylocas.npc;

import com.zenyte.game.content.theatreofblood.boss.nylocas.NylocasRoom;
import com.zenyte.game.content.theatreofblood.boss.nylocas.model.Spawn;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NpcId;

/**
 * @author Tommeh | 6/7/2020 | 12:58 AM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class NylocasIschyros extends Nylocas {

    public NylocasIschyros(final NylocasRoom room, final Location location, final Direction direction, final Spawn spawn) {
        super(room, spawn.isLarge() ? NpcId.NYLOCAS_ISCHYROS_8345 : NpcId.NYLOCAS_ISCHYROS_8342, location, direction, spawn);
    }

    @Override
    public void autoRetaliate(final Entity source) {
    }

    @Override
    public boolean checkAggressivity() {
        return true;
    }
}
