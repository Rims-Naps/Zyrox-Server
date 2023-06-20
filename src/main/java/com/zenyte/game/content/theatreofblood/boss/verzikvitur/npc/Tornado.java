package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Cresinkel
 */
public class Tornado extends NPC {

    public static final int TORNADO_ID = 8386;

    private final VerzikVitur verzik;
    private final Player player;
    private int ticksSinceLastDamage = 0;

    public Tornado(final VerzikVitur verzik, final Player player, final Location spawnLocation) {
        super(TORNADO_ID, spawnLocation, true);
        this.size = 1;
        this.verzik = verzik;
        this.player = player;
    }

    @Override
    public void processNPC() {
        if (verzik.isDead() || verzik.isFinished()) {
            finish();
            return;
        }
        if (player.isDead() || player.isFinished() || !verzik.getRaid().getParty().getAlivePlayers().contains(player)) {
            finish();
            return;
        }
        super.processNPC();

        getWalkSteps().clear();
        val nextLocation = getNextFollowLocation(getLocation(), player.getLocation());

        addWalkSteps(nextLocation.getX(), nextLocation.getY(), 1);
        if (player.getLocation().matches(getLocation())){
            if (ticksSinceLastDamage > 3) {
                player.sendMessage("Verzik saps your health and powers up!");
                int health = player.getHitpoints();
                player.applyHit(new Hit(verzik, health / 2, HitType.REGULAR));
                verzik.heal((health / 2) * 3);
                ticksSinceLastDamage = 0;
                player.setGraphics(new Graphics(1602));
            }
        } else {
            player.setGraphics(Graphics.RESET);
        }
        ticksSinceLastDamage++;
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    private Location getNextFollowLocation(final Location tornado, final Location target) {
        if (tornado.matches(target)) {
            return target;
        }
        val direction = Utils.getFaceDirection(target.getX() - tornado.getX(), target.getY() - tornado.getY());
        val entityDirection = Entity.getRoundedDirection(direction, 0);
        val directionConstant = Utils.findMatching(Direction.values, dir -> dir.getNPCDirection() == entityDirection);
        assert directionConstant != null;
        return tornado.transform(directionConstant, 1);
    }
    
}
