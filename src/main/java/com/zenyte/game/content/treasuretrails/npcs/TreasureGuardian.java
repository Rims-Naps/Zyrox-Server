package com.zenyte.game.content.treasuretrails.npcs;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.events.LogoutEvent;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Kris | 12/04/2019 18:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TreasureGuardian extends NPC {

    @NotNull
    final Player owner;
    int ticks;

    TreasureGuardian(@NotNull final Player owner, @NotNull final Location tile, final int id) {
        super(id, tile, Direction.SOUTH, 3);
        this.owner = owner;
        this.spawned = true;
        this.randomWalkDelay = 5;
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        return entity == owner;
    }

    @Override
    public boolean isAttackable(final Entity e) {
        if (e != owner) {
            if (e instanceof Player) {
                ((Player) e).sendMessage("It's not after you...");
            }
            return false;
        }
        return true;
    }

    @Subscribe
    public static final void onLogout(@NotNull final LogoutEvent event) {
        val npc = event.getPlayer().getTemporaryAttributes().remove("TT Guardian NPC");
        if (npc instanceof Collection) {
            for (val element : ((Collection<?>) npc)) {
                if (element instanceof NPC) {
                    ((NPC) element).finish();
                }
            }
        }
    }

    boolean isOutOfReach() {
        if (++ticks > 50 || !owner.getLocation().withinDistance(this, 15)) {
            finish();
            owner.getTemporaryAttributes().remove("TT Guardian NPC");
            return true;
        }
        return false;
    }

    @Override
    public void processNPC() {
        if (isOutOfReach()) {
            return;
        }
        super.processNPC();
        if (isUnderCombat()) {
            ticks = 0;
        }
    }


    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        val list = owner.getTemporaryAttributes().get("TT Guardian NPC");
        if (list instanceof Collection) {
            ((Collection<?>) list).remove(this);
            if (((Collection<?>) list).isEmpty()) {
                owner.getTemporaryAttributes().put("TT Guardian NPC", Boolean.TRUE);
            }
        }
    }


}
