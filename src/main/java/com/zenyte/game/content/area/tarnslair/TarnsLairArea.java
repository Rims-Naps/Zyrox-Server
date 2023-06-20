package com.zenyte.game.content.area.tarnslair;

import com.zenyte.game.content.area.abandonedmine.object.SpikeTrap;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.CannonRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;

public class TarnsLairArea extends Area implements CycleProcessPlugin, CannonRestrictionPlugin {
    @Override
    public void process() {
        for(Player p : players) {
            for(SpikeTrap trap : SpikeTrap.values()) {
                if(p.getLocation().equals(trap.getLocation())) {
                    if(p.getNextWalkStepPeek() > 0) {
                        return;
                    }
                    World.sendObjectAnimation(trap.getTrapObject(), new Animation(459));
                    p.setForceMovement(new ForceMovement(trap.getLocation(), 25, trap.getPushTo(), 45, trap.getDirection()));
                    p.setLocation(trap.getPushTo());
                    p.setAnimation(new Animation(1441, 25));
                    CombatUtilities.processHit(p, new Hit(Utils.random(0, 2), HitType.REGULAR));
                    p.sendSound(new SoundEffect(3347));
                }
            }
        }
    }

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 3121, 4673 },
                        { 3121, 4537 },
                        { 3204, 4537 },
                        { 3204, 4673 }
                })
        };
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player, boolean logout) {

    }

    @Override
    public String restrictionMessage() {
        return "This temple is ancient and would probably collapse if you started firing a cannon.";
    }

    @Override
    public String name() {
        return "Tarn's Lair";
    }
}
