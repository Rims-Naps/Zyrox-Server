package com.zenyte.game.world.entity.npc.impl;

import com.zenyte.game.HintArrow;
import com.zenyte.game.HintArrowPosition;
import com.zenyte.game.RuneDate;
import com.zenyte.game.content.achievementdiary.diaries.FaladorDiary;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.darkcaves.FaladorMoleLairArea;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Tommeh | 02/05/2019 | 18:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GiantMole extends NPC implements Spawnable {

    private long timeWhenGiantMoleResurfacedLast;
    private int timesGiantMoleHasResurfaced;
    private int timesGiantMoleHasBeenHit;

    private static final Animation DIG_START = new Animation(3314);
    private static final Animation DIG_END = new Animation(3315);

    private static final ArrayList<Location> RESURFACE_LOCATIONS = new ArrayList<Location>() {
        {
            add(new Location(1736, 5227, 0));
            add(new Location(1776, 5237, 0));
            add(new Location(1779, 5208, 0));
            add(new Location(1769, 5199, 0));
            add(new Location(1748, 5206, 0));
            add(new Location(1741, 5187, 0));
            add(new Location(1746, 5170, 0));
            add(new Location(1773, 5173, 0));
            add(new Location(1760, 5163, 0));
            add(new Location(1755, 5151, 0));
            add(new Location(1739, 5151, 0));

        }
    };

    public GiantMole(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
        this.maxDistance = 100;
        timeWhenGiantMoleResurfacedLast = 0;
        timesGiantMoleHasResurfaced = 0;
        timesGiantMoleHasBeenHit = 0;
       // this.radius = 100;
    }

    @Override
    public int getRespawnDelay() {
        return 15;
    }

    @Override
    public NPC spawn() {
        if (!isFinished()) {
            throw new RuntimeException("The NPC has already been spawned.");
        }
        World.addNPC(this);
        location.setLocation(getRespawnTile());
        setFinished(false);
        updateLocation();
        if (!combatDefinitionsMap.isEmpty()) {
            combatDefinitionsMap.clear();
        }
        updateCombatDefinitions();
        updateHintArrow();
        val players = GlobalAreaManager.get("Falador Mole Lair").getPlayers();
        for (val player : players) {
            if (!player.getBooleanAttribute("medium-combat-achievement27")) {
                player.putBooleanAttribute("has_taken_damage_from_giant_mole", false);
            }
        }
        timeWhenGiantMoleResurfacedLast = 0;
        timesGiantMoleHasResurfaced = 0;
        timesGiantMoleHasBeenHit = 0;
        return this;
    }

    @Override
    public void onMovement() {
        updateHintArrow();
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        val players = GlobalAreaManager.get("Falador Mole Lair").getPlayers();
        for (val player : players) {
            player.getPacketDispatcher().resetHintArrow();
        }
        if (source instanceof Player){
            val player = (Player) source;
            player.getAchievementDiaries().update(FaladorDiary.KILL_GIANT_MOLE);
            if (!player.getBooleanAttribute("has_taken_damage_from_giant_mole") && !player.getBooleanAttribute("medium-combat-achievement27")) {
                player.putBooleanAttribute("medium-combat-achievement27", true);
                MediumTasks.sendMediumCompletion(player, 27);
            }
            if (timeWhenGiantMoleResurfacedLast + 10000L >= RuneDate.currentTimeMillis() && !player.getBooleanAttribute("hard-combat-achievement27")) {
                player.putBooleanAttribute("hard-combat-achievement27", true);
                HardTasks.sendHardCompletion(player, 27);
            }
            if (timesGiantMoleHasResurfaced < 3 && !player.getBooleanAttribute("hard-combat-achievement28")) {
                player.putBooleanAttribute("hard-combat-achievement28", true);
                HardTasks.sendHardCompletion(player, 28);
            }
            if (timesGiantMoleHasBeenHit < 5 && !player.getBooleanAttribute("elite-combat-achievement36")) {
                player.putBooleanAttribute("elite-combat-achievement36", true);
                EliteTasks.sendEliteCompletion(player, 36);
            }
        }
    }

    @Override
    public void handleIngoingHit(final Hit hit) { //TODO dirt overlay
        super.handleIngoingHit(hit);
        timesGiantMoleHasBeenHit++;
        if (!isDead() && getHitpoints() <= getMaxHitpoints() / 2 && Utils.random(4) == 0) {
            val target = hit.getSource();
            if (target == null) {
                super.handleIngoingHit(hit);
                return;
            }
            if (target instanceof Player) {
                val player = (Player) target;
                player.cancelCombat();
                WorldTasksManager.schedule(() -> player.getPacketDispatcher().sendClientScript(896, 60));
            }
            setAnimation(DIG_START);
            timesGiantMoleHasResurfaced++;
            lock();
            WorldTasksManager.schedule(() -> {
                Location location = null;
                Collections.shuffle(RESURFACE_LOCATIONS);
                for (val l : RESURFACE_LOCATIONS) {
                    if (!getLocation().withinDistance(l, 10)) {
                        location = l;
                        break;
                    }
                }
                setAnimation(DIG_END);
                setLocation(location);
                timeWhenGiantMoleResurfacedLast = RuneDate.currentTimeMillis();
                unlock();
            }, 2);
        }
    }

    private void updateHintArrow() {
        val middle = getMiddleLocation();
        val players = GlobalAreaManager.get("Falador Mole Lair").getPlayers();
        for (val player : players) {
            if (player.getInventory().containsAnyOf(FaladorMoleLairArea.shields) || player.getEquipment().containsAnyOf(FaladorMoleLairArea.shields)) {
                player.getPacketDispatcher().sendHintArrow(new HintArrow(middle.getX(), middle.getY(), (byte) 100, HintArrowPosition.EAST));
            } else {
                player.getPacketDispatcher().resetHintArrow();
            }
        }
    }

    @Override
    public boolean validate(int id, String name) {
        return name.equals("giant mole");
    }
}
