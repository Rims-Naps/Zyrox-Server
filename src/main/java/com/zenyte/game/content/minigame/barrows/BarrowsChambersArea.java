package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.MinimapState;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.EntityAttackPlugin;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import com.zenyte.game.world.region.area.plugins.PartialMovementPlugin;
import com.zenyte.game.world.region.area.plugins.TeleportMovementPlugin;
import lombok.val;

/**
 * @author Kris | 31. mai 2018 : 01:57:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class BarrowsChambersArea extends Area implements CycleProcessPlugin, PartialMovementPlugin,
        TeleportMovementPlugin, EntityAttackPlugin {

    private static final Graphics ROCKS_FALL_GFX = new Graphics(60);
    private static final int WIGHT_SCRIPT = 894;
    private static final int BASE_PRAYER_DRAIN = 8;

	@Override
	public RSPolygon[] polygons() {
		return new RSPolygon[] { new RSPolygon(new int[][] { { 3524, 9723 }, { 3524, 9667 }, { 3580, 9667 }, { 3580, 9724 } }, 0, 3) };
	}

	@Override
	public void enter(final Player player) {
	    val barrows = player.getBarrows();
	    barrows.refreshLadder(player.getLocation());
	    barrows.refreshDoors();
	    barrows.refreshShaking();
        player.getPacketDispatcher().sendMinimapState(MinimapState.MAP_DISABLED);
        GameInterface.BARROWS_OVERLAY.open(player);
	}

	@Override
	public void leave(final Player player, boolean logout) {
        player.getInterfaceHandler().closeInterface(GameInterface.BARROWS_OVERLAY);
        val dispatcher = player.getPacketDispatcher();
        dispatcher.sendMinimapState(MinimapState.ENABLED);
        val barrows = player.getBarrows();
        barrows.removeTarget();
        barrows.resetTimer();
        if (barrows.isLooted()){
            dispatcher.resetCamera();
        }
	}

	private int getRandomComponent() {
	    return GameInterface.BARROWS_OVERLAY.getId() << 16 | Utils.random(2, 7);
    }

	@Override
	public String name() {
		return "Barrows chambers";
	}

	@Override
	public void process() {
	    if (players.isEmpty())
	        return;
	    for (val player : players) {
	        val barrows = player.getBarrows();
	        val timer = barrows.getAndDecrementTimer();
	        if (timer > 0)
	            continue;

	        if (barrows.isLooted()) {
	            if (player.getInterfaceHandler().containsInterface(InterfacePosition.CENTRAL))
	                continue;
	            player.setGraphics(ROCKS_FALL_GFX);
                player.applyHit(new Hit(Utils.random(6), HitType.REGULAR));
                continue;
            }

            val optionalWight = barrows.getRandomSlainWight();
            player.getPrayerManager().drainPrayerPoints(BASE_PRAYER_DRAIN + Utils.random(barrows.getSlainWights().size()));
            optionalWight.ifPresent(wight -> player.getPacketDispatcher().sendClientScript(WIGHT_SCRIPT,
                    getRandomComponent(), wight.getModel(player)));
        }
	}

    @Override
    public boolean processMovement(Player player, int x, int y) {
        player.getBarrows().refreshLadder(player.getLocation());
        return true;
    }

    @Override
    public void processMovement(Player player, Location destination) {
        player.getBarrows().refreshLadder(destination);
    }

    @Override
    public boolean attack(Player player, Entity entity) {
	    if (entity instanceof BarrowsWightNPC) {
	        if (((BarrowsWightNPC) entity).owner.get() != player) {
	            player.sendMessage("This is not your target.");
	            return false;
            }
        }
        return true;
    }
}
