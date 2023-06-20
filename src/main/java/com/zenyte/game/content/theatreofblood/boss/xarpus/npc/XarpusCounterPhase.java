package com.zenyte.game.content.theatreofblood.boss.xarpus.npc;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris
 * @since August 25 2020
 */
public class XarpusCounterPhase extends XarpusPhase {
    private static final int TURN_INCREMENT = (int) TimeUnit.SECONDS.toTicks(5);
    private static final int BASE_DAMAGE = 50;
    private static final ForceTalk screech = new ForceTalk("Screeeeech!");
    private XarpusQuadrant currentQuadrant = XarpusQuadrant.random();
    private long currentTurnStart;

    public XarpusCounterPhase(Xarpus xarpus) {
        super(xarpus);
    }

    @Override
    void onPhaseStart() {
        xarpus.setForceTalk(screech);
        WorldTasksManager.schedule(() -> {
            xarpus.getTemporaryAttributes().put("screech", false);
        },2);
    }

    @Override
    void onTick() {
        xarpus.setFaceEntity(null);
        if (ticks.getValue() % TURN_INCREMENT == 0) {
            turn();
        }
    }

    private void turn() {
        currentQuadrant = currentQuadrant.randomOther();
        xarpus.setFaceLocation(xarpus.getRoom().getLocation(currentQuadrant.getCornerTile()));
        currentTurnStart = Utils.currentTimeMillis();
    }

    public void counter(@NotNull final Hit receivedHit) {
        if (receivedHit.getSource() instanceof Player) {
            if(!xarpus.getRaid().getParty().getTargetablePlayers().contains(receivedHit.getSource())) {
               return;
            }
            if (xarpus.getTemporaryAttributes().get("screech").equals(true)) {
                return;
            }
            val hitTime = receivedHit.getScheduleTime();
            val player = (Player) receivedHit.getSource();
            if (hitTime >= currentTurnStart && currentQuadrant.isInside(new ImmutableLocation(player.getLocation()), xarpus)) {
                player.applyHit(new Hit(Math.min(player.getHitpoints(), BASE_DAMAGE + receivedHit.getDamage() / 2), HitType.POISON));
                for (val p : xarpus.getRaid().getParty().getPlayers()) {
                    p.putBooleanAttribute("PerfectXarpus", false);
                }
            }
        }
    }

    @Override
    boolean isPhaseComplete() {
        return xarpus.isDying() || xarpus.isDead();
    }

    @Override
    XarpusPhase advance() {
        for (val poisonSplat : xarpus.getPoisonSplats()) {
            World.removeObject(poisonSplat);
        }
        xarpus.getPoisonSplats().clear();
        return null;
    }
}
