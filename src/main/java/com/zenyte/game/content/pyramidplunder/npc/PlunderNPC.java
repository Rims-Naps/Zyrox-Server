package com.zenyte.game.content.pyramidplunder.npc;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableInt;

import java.lang.ref.WeakReference;

/**
 * @author Christopher
 * @since 4/5/2020
 */
public class PlunderNPC extends NPC {
    private static final int removalTicks = (int) TimeUnit.MINUTES.toTicks(1);
    private final MutableInt ticksLeft = new MutableInt(removalTicks);
    private final WeakReference<Player> player;

    public PlunderNPC(int id, Player player) {
        super(id, player.getLocation(), Direction.NORTH, 0);
        super.spawned = true;
        super.setTarget(player);
        this.player = new WeakReference<>(player);
    }

    @Override
    public void processNPC() {
        val target = player.get();
        if (ticksLeft.decrementAndGet() <= 0
                || target == null
                || target.isLoggedOut()
                || target.getLocation().getTileDistance(getLocation()) >= 20) {
            finish();
            return;
        }
        if (isUnderCombat()) {
            ticksLeft.setValue(removalTicks);
        }
        super.processNPC();
    }

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        return entity == player.get();
    }

    @Override
    public boolean canAttack(final Player target) {
        return player.get() == target;
    }
}
