package com.zenyte.game.content.boss.grotesqueguardians;

import com.zenyte.game.content.boss.grotesqueguardians.boss.Dawn;
import com.zenyte.game.content.boss.grotesqueguardians.instance.GrotesqueGuardiansInstance;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 01/08/2019 | 21:35
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class EnergySphere extends WorldObject {

    private final transient Player player;
    private final transient Dawn dawn;
    private final GrotesqueGuardiansInstance instance;
    private int ticks;
    @Getter
    private SphereState state;

    public EnergySphere(final int index, final Location tile, final Player player, final Dawn dawn, final GrotesqueGuardiansInstance instance) {
        super(31686 + index, 10, 0, tile);
        this.player = player;
        this.dawn = dawn;
        this.instance = instance;
        state = SphereState.LOW;
        refresh();
    }

    public void process() {
        if (ticks > 0 && ticks % 12 == 0) { //every 7 seconds
            if (state.equals(SphereState.HIGH)) {
                val projectile = new Projectile(state.getProjectile(), 0, 90, 20, 0);
                instance.getEnergySpheres().remove(getId());
                World.removeObject(this);
                World.sendProjectile(this, dawn, projectile);
                WorldTasksManager.schedule(() -> dawn.heal(90), projectile.getTime(this, dawn));
                if (dawn.isFinished() || dawn.isDead() || dawn.isDying()) {
                    return;
                }
                player.putBooleanAttribute("dawn_healed", true);
                return;
            }
            state = state.next();
            refresh();
        }
        ticks++;
    }

    public void absorb() {
        player.applyHit(new Hit(dawn, state.getDamage(), HitType.REGULAR));
        instance.getEnergySpheres().remove(getId());
    }

    private void refresh() {
        player.getVarManager().sendBit(getDefinitions().getVarbit(), state.getIdentifier() + 1);
    }
}
