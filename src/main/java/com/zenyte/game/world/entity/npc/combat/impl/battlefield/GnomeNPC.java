package com.zenyte.game.world.entity.npc.combat.impl.battlefield;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combatdefs.AggressionType;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import static com.zenyte.game.world.entity.npc.NpcId.*;

/**
 * @author Kris | 25/11/2018 13:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class GnomeNPC extends NPC implements Spawnable {

    private static final IntArrayList list = new IntArrayList(new int[]
            {TRACKER_GNOME_3, MOUNTED_TERRORBIRD_GNOME_5971, TRACKER_GNOME_1, GNOME_5968, GNOME_TROOP_5967,
             GNOME_5970, GNOME_5969, MOUNTED_TERRORBIRD_GNOME_5972, TRACKER_GNOME_2, GNOME_TROOP_5966,
             MOUNTED_TERRORBIRD_GNOME_5973, GNOME_6095, GNOME_6096, LOCAL_GNOME_4979, TORTOISE_6076});

    public GnomeNPC(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
        setTargetType(EntityType.NPC);
    }

    @Override
    public NPC spawn() {
        super.spawn();
        this.combatDefinitions.setAggressionType(AggressionType.ALWAYS_AGGRESSIVE);
        return this;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity target) {
        return target instanceof KhazardTrooperNPC;
    }

    @Override
    public boolean validate(int id, String name) {
        return list.contains(id);
    }
}
