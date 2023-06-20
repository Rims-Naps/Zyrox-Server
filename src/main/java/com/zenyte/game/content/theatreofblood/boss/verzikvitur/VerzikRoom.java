package com.zenyte.game.content.theatreofblood.boss.verzikvitur;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc.VerzikDawnbringerPhase;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc.VerzikPillar;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc.VerzikVitur;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.object.VerzikPillarLocation;
import com.zenyte.game.content.theatreofblood.plugin.entity.TheatreNPC;
import com.zenyte.game.content.theatreofblood.shared.HealthBarType;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import lombok.Getter;
import lombok.val;
import lombok.var;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VerzikRoom extends TheatreArea {
    
    private final Location VERZIK_SPAWN_LOCATION = new Location(3166, 4323, 0);
    private final Location VERZIK_OBJECT_SPAWN_LOCATION = new Location(3167, 4324, 0);
    @Getter
    private Location[] nyloSpawns;
    @Getter
    private final VerzikVitur verzik;
    private Map<VerzikPillarLocation, VerzikPillar> pillars;

    public static WorldObject verzikObjectPlaceholder;

    public VerzikRoom(TheatreOfBloodRaid raid, AllocatedArea area, TheatreRoom room) {
        super(raid, area, room);
        verzik = new VerzikVitur(this);
        verzikObjectPlaceholder = new WorldObject(ObjectId.VERZIK_VITUR, 10, 0, getLocation(VERZIK_OBJECT_SPAWN_LOCATION));
        pillars = new HashMap<>(6);
        for (val location : VerzikPillarLocation.values()) {
            pillars.put(location, new VerzikPillar(this, location));
        }
        nyloSpawns = new Location[]{
                getLocation(new Location(3167, 4320, 0)),
                getLocation(new Location(3156, 4318, 0)),
                getLocation(new Location(3179, 4318, 0)),
                getLocation(new Location(3158, 4313, 0)),
                getLocation(new Location(3177, 4313, 0)),
                getLocation(new Location(3156, 4308, 0)),
                getLocation(new Location(3179, 4308, 0))};
    }

    @Override
    public void enter(Player player) {
        super.enter(player);

        WorldTasksManager.schedule(() -> {
            player.lock(4);
            player.setRunSilent(4);
            player.addWalkSteps(player.getX(), player.getY() + 4, -1, false);
            if(allInRoom()) {
                for(Player p : getVerzik().getRoom().getRaid().getParty().getPlayers()) {
                    enterBossRoom(null, p);
                }
            }
        });

    }

    private boolean allInRoom() {
        boolean allIn = true;
        for(Player p : getVerzik().getRoom().getRaid().getParty().getPlayers()) {
            if(!(p.getArea() instanceof VerzikRoom)) {
                allIn = false;
            }
        }
        return allIn;
    }

    @Override
    public HealthBarType getHealthBarType() {
        if(verzik.getPhase() == null) {
            return HealthBarType.DISABLED;
        } else {
            switch(verzik.getPhase().getOrdinal()) {
                case 2:
                    return HealthBarType.CYAN;
                case 3:
                case 4:
                    return HealthBarType.REGULAR;
                case 1:
                default:
                    return HealthBarType.DISABLED;
            }
        }
    }

    public Map<VerzikPillarLocation, VerzikPillar> getPillars() {
        return this.pillars;
    }

    @Override
    public Location getEntranceLocation() {
        return getLocation(3168, 4299, 0);
    }

    @Override
    public WorldObject getVyreOrator() {
        return null;
    }

    @Override
    public WorldObject getRefillChest() {
        return null;
    }

    @Override
    public Location getSpectatingLocation() {
        return getLocation(3179, 4325, 0);
    }

    @Override
    public Location[] getJailLocations() {
        return new Location[] { getLocation(3159, 4325, 0), getLocation(3161, 4325, 0),
                                getLocation(3175, 4325, 0), getLocation(3177, 4325, 0) };
    }

    @Override
    public Direction[] getJailFacingDirections() {
        return new Direction[] { Direction.SOUTH, Direction.SOUTH, Direction.SOUTH, Direction.SOUTH};
    }

    @Override
    public Optional<TheatreNPC<? extends TheatreArea>> getBoss() {
        return Optional.of(verzik);
    }

    @Override
    public boolean isEnteringBossRoom(WorldObject barrier, Player player) {
        return player.getLocation().equals(getEntranceLocation());
    }

    @Override
    public void enterBossRoom(WorldObject barrier, Player player) {
        if (!isStarted()) {
            setStarted(true);
            setDuration(0);
            verzik.getTemporaryAttributes().put("start", System.currentTimeMillis());

        }
        player.getAttributes().put("tobpoints", player.getNumericAttribute("tobpoints").intValue() + 3);
    }

    @Override
    public boolean inCombatZone(int x, int y) {
        return inZone(3154, 4303, 3182, 4322, x, y);
    }

    @Override
    public String name() {
        return "The Final Challenge";
    }
    
    public Location getVerzikSpawnLocation() {
        return getLocation(VERZIK_SPAWN_LOCATION);
    }

    @Override
    public void onLoad() {
        if(!isStarted()) {
            World.spawnObject(verzikObjectPlaceholder);
        }
        WorldTasksManager.schedule(() -> verzik.spawn(), 2);
    }

    public void moveToDawnbringerPhase() {
        if(!(verzik.getPhase() instanceof VerzikDawnbringerPhase)) {
            verzik.setPhase(new VerzikDawnbringerPhase(verzik));
            verzik.getPhase().onPhaseStart();
        }
    }

    @Override
    public TheatreRoom onAdvancement() {
        var MVP = (Player) raid.getParty().getLeader();
        int MVPDamage;
        for (val p : raid.getParty().getPlayers()) {
            MVPDamage = MVP.getNumericAttribute("verzikp1bossdamage").intValue();
            int PlayerDamage = p.getNumericAttribute("verzikp1bossdamage").intValue();
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
        var MVP2 = (Player) raid.getParty().getLeader();
        int MVPDamage2;
        for (val p : raid.getParty().getPlayers()) {
            MVPDamage2 = MVP2.getNumericAttribute("verzikp2bossdamage").intValue();
            int PlayerDamage = p.getNumericAttribute("verzikp2bossdamage").intValue();
            if (PlayerDamage > MVPDamage2) {
                MVP2 = p;
            }
        }
        if (MVP2 != null) {
            if (MVP2.getAttributes().containsKey("tobpoints")) {
                MVP2.getAttributes().put("tobpoints", MVP2.getNumericAttribute("tobpoints").intValue() + 2);
            } else {
                MVP2.sendMessage("You did not have the attribute for points.");
            }
        } else {
            raid.getParty().getLeader().sendMessage("MVP was null.");
        }
        var MVP3 = (Player) raid.getParty().getLeader();
        int MVPDamage3;
        for (val p : raid.getParty().getPlayers()) {
            MVPDamage3 = MVP3.getNumericAttribute("verzikp3bossdamage").intValue();
            int PlayerDamage = p.getNumericAttribute("verzikp3bossdamage").intValue();
            if (PlayerDamage > MVPDamage3) {
                MVP3 = p;
            }
        }
        if (MVP3 != null) {
            if (MVP3.getAttributes().containsKey("tobpoints")) {
                MVP3.getAttributes().put("tobpoints", MVP3.getNumericAttribute("tobpoints").intValue() + 2);
            } else {
                MVP3.sendMessage("You did not have the attribute for points.");
            }
        } else {
            raid.getParty().getLeader().sendMessage("MVP was null.");
        }
        val nextRoom = TheatreRoom.REWARD;
        raid.removeRoom(getRoom());
        raid.addRoom(TheatreRoom.REWARD);
        return nextRoom;
    }

    @Override
    public boolean hit(final Player source, final Entity target, final Hit hit, final float modifier) {
        if (source == null) {
            return true;
        }
        if (target instanceof VerzikVitur) {
            if (verzik.getPhase().getOrdinal() == 2) {
                if (hit.getHitType() == HitType.RANGED || hit.getHitType() == HitType.MAGIC) {
                    val player = (Player) hit.getSource();
                    if (player.getEquipment().getId(EquipmentSlot.WEAPON) != ItemId.DAWNBRINGER) {
                        hit.setDamage(Math.min(Math.min(hit.getDamage(), 3), verzik.getHitpoints()));
                    } else {
                        hit.setDamage(Math.min(hit.getDamage(), verzik.getHitpoints()));
                    }
                }
                if (hit.getHitType() == HitType.MELEE) {
                    hit.setDamage(Math.min(Math.min(hit.getDamage(), 10), verzik.getHitpoints()));
                }
            }
            if (verzik.getPhase().getOrdinal() == 2 && hit.getHitType() != HitType.HEALED) {
                val player = (Player) hit.getSource();
                if (player.getAttributes().containsKey("verzikp1bossdamage")) {
                    player.getAttributes().put("verzikp1bossdamage", (player.getNumericAttribute("verzikp1bossdamage").intValue() + hit.getDamage()));
                } else {
                    player.getAttributes().put("verzikp1bossdamage", hit.getDamage());
                }
            }
            if (verzik.getPhase().getOrdinal() == 3 && hit.getHitType() != HitType.HEALED) {
                val player = (Player) hit.getSource();
                if (player.getAttributes().containsKey("verzikp2bossdamage")) {
                    player.getAttributes().put("verzikp2bossdamage", (player.getNumericAttribute("verzikp2bossdamage").intValue() + hit.getDamage()));
                } else {
                    player.getAttributes().put("verzikp2bossdamage", hit.getDamage());
                }
            }
            if (verzik.getPhase().getOrdinal() == 4 && hit.getHitType() != HitType.HEALED) {
                val player = (Player) hit.getSource();
                if (player.getAttributes().containsKey("verzikp3bossdamage")) {
                    player.getAttributes().put("verzikp3bossdamage", (player.getNumericAttribute("verzikp3bossdamage").intValue() + hit.getDamage()));
                } else {
                    player.getAttributes().put("verzikp3bossdamage", hit.getDamage());
                }
            }
        }
        return true;
    }

    @Override
    public Location getRespawnLocation() {
        return getLocation(3168, 4312, 0);
    }

    @Override
    public void onCompletion() {
        setDuration((int) TimeUnit.MILLISECONDS.toTicks(System.currentTimeMillis() - ((long) verzik.getTemporaryAttributes().get("start"))));
        super.onCompletion();
    }
}
