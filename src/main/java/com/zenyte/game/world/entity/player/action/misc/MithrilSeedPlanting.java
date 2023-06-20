package com.zenyte.game.world.entity.player.action.misc;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.area.plugins.LayableObjectPlugin;
import com.zenyte.plugins.dialogue.OptionDialogue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import static com.zenyte.game.world.entity.player.dialogue.Dialogue.TITLE;

/**
 * @author Tommeh | 28-4-2019 | 14:55
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class MithrilSeedPlanting extends Action {

    private static final Item MITHRIL_SEEDS = new Item(299);
    private static final Animation PICKUP_ANIM = new Animation(827);
    private final Flowers flowers;

    @Override
    public boolean start() {
        if (player.getNumericTemporaryAttribute("mithril seed delay").longValue() > System.currentTimeMillis()) {
            return false;
        }
        val location = player.getLocation();
        if (!World.isTileFree(location, 0) || World.getObjectWithType(location, 10) != null || World.getObjectWithType(location, 11) != null || (player.getDuel() != null && player.getDuel().inDuel())) {
            player.sendMessage("You can't plant mithril seeds here.");
            return false;
        }
        val area = player.getArea();
        if (area instanceof LayableObjectPlugin) {
            if (!(((LayableObjectPlugin) area).canLay(player, LayableObjectPlugin.LayableObjectType.MITHRIL_SEED))) {
                return false;
            }
        }
        player.lock(3);
        return true;
    }

    @Override
    public boolean process() {
        return true;
    }

    @Override
    public int processWithDelay() {
        val location = new Location(player.getLocation());
        if (!World.isTileFree(location, 0) || World.getObjectWithType(location, 10) != null || World.getObjectWithType(location, 11) != null || (player.getDuel() != null && player.getDuel().inDuel())) {
            player.sendMessage("You can't plant mithril seeds here.");
            return -1;
        }
        val object = new WorldObject(flowers.getObject(), 10, 0, location);
        player.getInventory().deleteItem(MITHRIL_SEEDS);
        player.sendFilteredMessage("You open the small mithril case.");
        player.sendFilteredMessage("You drop a seed by your feet.");
        player.log(LogLevel.INFO, "Planting mithril seed: " + flowers.toString() + " at " + location + ".");
        World.spawnTemporaryObject(object, 100);
        player.getTemporaryAttributes().put("mithril seed delay", System.currentTimeMillis() + TimeUnit.TICKS.toMillis(4));
        player.getTemporaryAttributes().put("ignoreWalkingRestrictions", true);
        player.setRouteEvent(new TileEvent(player, new TileStrategy(player.getLocation(), 1), () -> {
            player.faceObject(object);
            player.getTemporaryAttributes().remove("ignoreWalkingRestrictions");
            player.getDialogueManager().start(new OptionDialogue(player, TITLE, new String[] { "Pick the flowers.", "Leave the flowers." }, new Runnable[] { () -> {
                player.setAnimation(PICKUP_ANIM);
                player.getInventory().addItem(flowers.getItem(), 1);
                World.removeObject(object);
            }, null }));
        }));
        return -1;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Flowers {

        RED(2462, 2981, 148),
        BLUE(2464, 2982, 148),
        YELLOW(2466, 2983, 148),
        PURPLE(2468, 2984, 148),
        ORANGE(2470, 2985, 148),
        MIXED(2460, 2980, 148),
        ASSORTED(2472, 2986, 110),
        BLACK(2476, 2988, 2),
        WHITE(2474, 2987, 1);

        private final int item, object;
        private final int chance;

        private static final Flowers[] all = values();

        public static final Flowers random() {
            val random = Utils.secureRandom(1001);
            int weight = 0;
            for (val value : all) {
                if ((weight += value.chance) >= random) {
                    return value;
                }
            }
            throw new IllegalStateException();
        }

    }
}
