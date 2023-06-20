package com.zenyte.game.content.Thanksgiving2021;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.area.plugins.LogoutPlugin;
import com.zenyte.game.world.region.area.plugins.LootBroadcastPlugin;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;

/**
 * @author Matt (11/11/2021)
 *
 */
public class ThanksgivingInstance extends DynamicArea implements LogoutPlugin, LootBroadcastPlugin {

    private transient Player player;

    private static final Location INSIDE_INSTANCE = new Location(3087, 3489, 0);
    private static final Location TURKEY_SPAWN_LOCATION = new Location(3093, 3499, 0);
    private static final Location OUTSIDE_INSTANCE = new Location(3095, 9832, 0);

    public ThanksgivingInstance(final Player player, final AllocatedArea allocatedArea) {
        super(allocatedArea, INSIDE_INSTANCE.getChunkX(), INSIDE_INSTANCE.getChunkY());
        this.player = player;
    }

    @Override
    public void constructed() {
        player.unlock();
        player.setLocation(getLocation(INSIDE_INSTANCE));
        player.sendMessage("You use your key to unlock the gate.");

        World.spawnNPC(new NPC(10022, getLocation(TURKEY_SPAWN_LOCATION), true));
        //3093, 3499
    }

    @Override
    public void enter(Player player) {
        player.getPacketDispatcher().resetCamera();
    }

    @Override
    public void leave(Player player, boolean logout) {
        if (logout) {
            player.forceLocation(OUTSIDE_INSTANCE);
        }
    }

    @Override
    public void onLogout(final Player player) {
        player.setLocation(getLocation(OUTSIDE_INSTANCE));
    }

    @Override
    public Location onLoginLocation() {
        return OUTSIDE_INSTANCE;
    }

    @Override
    public void cleared() {
        if (players.isEmpty()) {
            destroyRegion();
        }
    }

    @Override
    public String name() {
        return player.getName() + "'Thanksgiving Instance";
    }
}
