package com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.npc.MaidenOfSugadinti;
import com.zenyte.game.content.theatreofblood.boss.pestilentbloat.npc.PestilentBloat;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.content.theatreofblood.shared.HealthBarType;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import lombok.val;
import lombok.var;

import java.util.Optional;

/**
 * @author Tommeh | 5/22/2020 | 4:36 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class MaidenOfSugadintiRoom extends TheatreArea {
    
    private final MaidenOfSugadinti maiden;
    private final WorldObject maidenObjectPlaceholder;
    private final WorldObject poolOfBlood;
    
    public MaidenOfSugadintiRoom(final TheatreOfBloodRaid raid, final AllocatedArea area, final TheatreRoom room) {
        super(raid, area, room);
        maiden = new MaidenOfSugadinti(this);
        maidenObjectPlaceholder = new WorldObject(ObjectId.THE_MAIDEN_OF_SUGADINTI, 10, 3, getLocation(MaidenOfSugadinti.spawnLocation));
        poolOfBlood = new WorldObject(ObjectId.POOL_OF_BLOOD, 10, 0, getLocation(MaidenOfSugadinti.spawnLocation));
    }
    
    @Override
    public void onLoad() {
        if (!isCompleted()) {
            World.spawnObject(maidenObjectPlaceholder);
        } else {
            World.spawnObject(poolOfBlood);
        }
    }
    
    @Override
    public void destroyRegion() {
        super.destroyRegion();
    }
    
    @Override
    public boolean isEnteringBossRoom(final WorldObject barrier, final Player player) {
        return player.getX() > barrier.getX();
    }
    
    @Override
    public void enterBossRoom(WorldObject barrier, Player player) {
        if (!isStarted()) {
            World.removeObject(maidenObjectPlaceholder);
            maiden.spawn();
            if (!isCompleted()) {
                setDuration(0);
                maiden.getTemporaryAttributes().put("start", System.currentTimeMillis());
            }
        }
        player.getAttributes().put("tobpoints", player.getNumericAttribute("tobpoints").intValue() + 3);
        super.enterBossRoom(barrier, player);
    }

    @Override
    public TheatreRoom onAdvancement() {
        var MVP = (Player) raid.getParty().getLeader();
        int MVPDamage;
        for (val p : raid.getParty().getPlayers()) {
            MVPDamage = MVP.getNumericAttribute("maidenbossdamage").intValue();
            int PlayerDamage = p.getNumericAttribute("maidenbossdamage").intValue();
            if (PlayerDamage > MVPDamage) {
                MVP = p;
            }
        }
        if (MVP != null) {
            if (MVP.getAttributes().containsKey("tobpoints")) {
                MVP.getAttributes().put("tobpoints", MVP.getNumericAttribute("tobpoints").intValue() + 2);
            } else {
                MVP.sendMessage("You did not have the attribute for points.");
            }
        } else {
            raid.getParty().getLeader().sendMessage("MVP was null.");
        }
        val nextRoom = TheatreRoom.THE_PESTILENT_BLOAT;
        raid.addRoom(TheatreRoom.THE_PESTILENT_BLOAT);
        return nextRoom;
    }

    @Override
    public Location getEntranceLocation() {
        return getLocation(3219, 4459, 0);
    }

    @Override
    public WorldObject getVyreOrator() {
        return new WorldObject(ObjectId.VYRE_ORATOR, 11, 0, getLocation(3192, 4447, 0));
    }

    @Override
    public WorldObject getRefillChest() {
        return null;
    }

    @Override
    public Location getSpectatingLocation() {
        return getLocation(3190, 4453, 0);
    }

    @Override
    public Location[] getJailLocations() {
        return new Location[] { getLocation(3166, 4460, 0), getLocation(3167, 4460, 0),
                getLocation(3166, 4433, 0), getLocation(3167, 4433, 0) };
    }

    @Override
    public Direction[] getJailFacingDirections() {
        return new Direction[] {Direction.SOUTH, Direction.SOUTH, Direction.NORTH, Direction.NORTH};
    }

    @Override
    public Optional<TheatreNPC<? extends TheatreArea>> getBoss() {
        return Optional.of(maiden);
    }
    
    @Override
    public HealthBarType getHealthBarType() {
        return HealthBarType.REGULAR;
    }
    
    @Override
    public String name() {
        return "The Maiden of Sugadinti";
    }
    
    @Override
    public void onCompletion() {
        setDuration((int) TimeUnit.MILLISECONDS.toTicks(System.currentTimeMillis() - ((long) maiden.getTemporaryAttributes().get("start"))));
        super.onCompletion();
        World.spawnObject(poolOfBlood);
    }

    @Override
    public boolean inCombatZone(int x, int y) {
        return inZone(3160, 4435, 3184, 4458, x, y)
                || inZone(3185, 4450, 3187, 4458, x, y)
                || inZone(3185, 4435, 3187, 4443, x, y);
        //zone 3160 4435 -> 3184 4458 / 3185 4450 -> 3187 4458 / 3185 4435 -> 3187 4443
    }

    @Override
    public boolean hit(final Player source, final Entity target, final Hit hit, final float modifier) {
        if (source == null) {
            return true;
        }
        if (target instanceof MaidenOfSugadinti) {
            val player = (Player) hit.getSource();
            if (player.getAttributes().containsKey("maidenbossdamage")) {
                player.getAttributes().put("maidenbossdamage", (player.getNumericAttribute("maidenbossdamage").intValue() + hit.getDamage()));
            } else {
                player.getAttributes().put("maidenbossdamage", hit.getDamage());
            }
        }
        return true;
    }

    @Override
    public Location getRespawnLocation() {
        return getLocation(3191, 4446, 0);
    }
}
