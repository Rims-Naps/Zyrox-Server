package com.zenyte.game.content.godwars.objects;

import com.zenyte.game.content.godwars.instance.GodwarsInstance;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.godwars.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.zenyte.game.content.rottenpotato.PotatoToggles.*;

/**
 * @author Tommeh | 24-3-2019 | 14:05
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GodwarsBossDoorObject implements ObjectAction {

    @Getter
    @RequiredArgsConstructor
    private enum BossDoor {
        BANDOS(ObjectId.BIG_DOOR_26503, BandosChamberArea.class),
        ARMADYL(ObjectId.BIG_DOOR_26502, ArmadylChamberArea.class),
        SARADOMIN(ObjectId.BIG_DOOR_26504, SaradominChamberArea.class),
        ZAMORAK(ObjectId.BIG_DOOR_26505, ZamorakChamberArea.class);

        private final int objectId;
        private final Class<? extends GodwarsDungeonArea> clazz;
        @Getter private static final List<BossDoor> values = Collections.unmodifiableList(Arrays.asList(values()));
        private final String formattedName = TextUtils.capitalizeFirstCharacter(name().toLowerCase());

        @Override
        public String toString() {
            return formattedName;
        }
    }

    private int calculateRequiredKillcount(@NotNull final Player player) {
        int requiredKillcount = 40;
        val rank = player.getMemberRank();
        if (rank.eligibleTo(MemberRank.ZENYTE_MEMBER)) {
            requiredKillcount -= 30;
        } else if (rank.eligibleTo(MemberRank.DIAMOND_MEMBER)) {
            requiredKillcount -= 25;
        } else if (rank.eligibleTo(MemberRank.RUBY_MEMBER)) {
            requiredKillcount -= 20;
        } else if (rank.eligibleTo(MemberRank.EMERALD_MEMBER)) {
            requiredKillcount -= 15;
        } else if (rank.eligibleTo(MemberRank.SAPPHIRE_MEMBER)) {
            requiredKillcount -= 10;
        }
        return requiredKillcount;
    }

    private final void notifyChamberSize(@NotNull final Player player, final int size) {
        player.sendMessage("There " + (size == 1 ? "is" : "are") + " " + size + " adventurer" + (size == 1 ? "" : "s") + " inside the chamber.");
    }

    public static final int getInstanceChamberCount(@NotNull final Area area) {
        int count = 0;
        if (area instanceof GodwarsInstance) {
            val instance = (GodwarsInstance) area;
            val polygon = instance.chamberPolygon();

            for (val p : instance.getPlayers()) {
                if (polygon.contains(p.getLocation())) {
                    count++;
                }
            }
        }
        return count;
    }

    private final boolean insideChamber(@NotNull final Player player, @NotNull final BossDoor door) {
        val area = player.getArea();
        if (area instanceof GodwarsInstance) {
            return (((GodwarsInstance) area).chamberPolygon().contains(player.getLocation()));
        }
        return GlobalAreaManager.getArea(door.clazz).getPlayers().contains(player);
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val door = Objects.requireNonNull(Utils.findMatching(BossDoor.getValues(), v -> v.getObjectId() == object.getId()));
        if (option.equals("Peek")) {
            val playerArea = player.getArea();
            val count = playerArea instanceof GodwarsInstance ? getInstanceChamberCount(playerArea) : GlobalAreaManager.getArea(door.clazz).getPlayers().size();
            notifyChamberSize(player, count);
            return;
        }
        if (insideChamber(player, door)) {
            player.sendMessage("You cannot leave the boss room through this side of the door!");
            return;
        }
        val horizontal = (object.getRotation() & 0x1) == 0;
        val requiredKillcount = calculateRequiredKillcount(player);
        val killcount = player.getNumericAttribute(door.formattedName + "Kills").intValue();

        if (killcount < requiredKillcount && !player.getInventory().containsItem(ItemId.ECUMENICAL_KEY, 1) && isKcEnabled(door)) {
            player.sendMessage("This door is locked by the power of " + door.formattedName + "! " +
                    "You will need to collect the essence of at least " + requiredKillcount + " of his followers before the door will open.");
            return;
        }

        if(isKcEnabled(door))
        {
            if (killcount >= requiredKillcount) {
                player.addAttribute(door.formattedName + "Kills", Math.max(0, killcount - requiredKillcount));
                GodwarsDungeonArea.refreshKillcount(player);
                player.sendMessage("The door devours the life-force of " + requiredKillcount + " followers of " + door.formattedName + " that you have slain.");
            } else {
                player.getInventory().deleteItem(ItemId.ECUMENICAL_KEY, 1);
                player.sendMessage("The door devours the ecumenical key.");
            }
        }

        object.setLocked(true);
        val obj = new WorldObject(object);
        obj.setRotation((obj.getRotation() - 1) & 0x3);
        World.spawnGraphicalDoor(obj);
        player.lock();
        player.setRunSilent(2);
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                switch (ticks++) {
                    case 0:
                        val destinationX = horizontal ? (player.getX() + (player.getX() < object.getX() ? 2 : -2)) : (player.getX());
                        val destinationY = !horizontal ? (player.getY() + (player.getY() < object.getY() ? 2 : -2)) : (player.getY());
                        player.addWalkSteps(destinationX, destinationY, 2, false);
                        break;
                    case 1:
                        player.unlock();
                        World.spawnGraphicalDoor(object);
                        break;
                    case 2:
                        object.setLocked(false);
                        stop();
                        break;
                }
            }
        }, 0, 1);
    }

    public boolean isKcEnabled(BossDoor door)
    {
        switch (door) {
            case ARMADYL:
                return ARMA_KC_REQUIRED;
            case BANDOS:
                return BANDOS_KC_REQUIRED;
            case SARADOMIN:
                return SARA_KC_REQUIRED;
            case ZAMORAK:
                return ZAMMY_KC_REQUIRED;
            default:
                return false;
        }
    }


    @Override
    public Object[] getObjects() {
        return new Object[] { ObjectId.BIG_DOOR_26502, ObjectId.BIG_DOOR_26503, ObjectId.BIG_DOOR_26504, ObjectId.BIG_DOOR_26505 };
    }
}
