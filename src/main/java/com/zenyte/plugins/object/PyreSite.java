package com.zenyte.plugins.object;

import com.zenyte.game.content.skills.firemaking.FiremakingTool;
import com.zenyte.game.content.skills.woodcutting.actions.Woodcutting;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

public class PyreSite implements ObjectAction {

    @Override
    public void handle(Player player, WorldObject object, String name, int optionId, String option) {
        if (!player.getInventory().containsItem(ItemId.CHEWED_BONES)) {
            player.sendMessage("You need chewed bones to sacrifice.");
            return;
        }
        if (!player.getInventory().containsAnyOf(ItemId.LOGS, ItemId.ACHEY_TREE_LOGS, ItemId.OAK_LOGS,
                ItemId.WILLOW_LOGS, ItemId.TEAK_LOGS, ItemId.ARCTIC_PINE_LOGS, ItemId.MAPLE_LOGS, ItemId.MAHOGANY_LOGS,
                ItemId.YEW_LOGS, ItemId.MAGIC_LOGS, ItemId.REDWOOD_LOGS)) {
            player.sendMessage("You need any type of logs to burn.");
            return;
        }
        if (!Woodcutting.getAxe(player).isPresent()) {
            player.sendMessage("You need an axe to do this.");
            return;
        }
        if (!FiremakingTool.getAvailableTool(player).isPresent()) {
            player.sendMessage("You need a way to light the pyre ship to do this.");
            return;
        }
        if (object.getX() == 2503) {
            if (object.getY() == 3499) {
                object = World.getObjectWithId(object.getPosition().transform(0,-1,0), object.getId());
            }
        } else if (object.getY() == 3518) {
            if (object.getX() == 2507) {
                object = World.getObjectWithId(object.getPosition().transform(-1,0,0), object.getId());
            }
        } else if (object.getY() == 3519) {
            if (object.getX() == 2519) {
                object = World.getObjectWithId(object.getPosition().transform(-1,0,0), object.getId());
            }
        }
        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object), getRunnable(player, object, name,
                optionId, option), getDelay()));
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.lock();
        player.faceObject(object);
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                val axeId = Woodcutting.getAxe(player).get().getDefinitions().getItemId();
                val axeAnim = new Animation(
                        axeId == 1351 ? 6744 :
                        axeId == 1349 ? 6743 :
                        axeId == 1353 ? 6742 :
                        axeId == 1361 ? 6741 :
                        axeId == 1355 ? 6740 :
                        axeId == 1357 ? 6739 :
                        axeId == 1359 ? 6738 :
                        axeId == 6739 ? 6745 : 10006);

                val pyreship = new WorldObject(ObjectId.CARVED_LOG,10, object.getY() >= 3518 ? 1 : 4, object.getPosition().transform(0, object.getY() >= 3518 ? -2 : 0,0));
                if (ticks == 0) {
                    World.spawnObject(pyreship);
                    player.setAnimation(axeAnim);
                } else if (ticks == 4) {
                    pyreship.setId(ObjectId.CARVED_LOG_25289);
                    World.spawnObject(pyreship);
                } else if (ticks == 8) {
                    player.setAnimation(axeAnim);
                    pyreship.setId(ObjectId.PYRE_BOAT);
                    World.spawnObject(pyreship);
                } else if (ticks == 12) {
                    Item logs = null;
                    for (int logId : new int[] {ItemId.LOGS, ItemId.ACHEY_TREE_LOGS, ItemId.OAK_LOGS,
                            ItemId.WILLOW_LOGS, ItemId.TEAK_LOGS, ItemId.ARCTIC_PINE_LOGS, ItemId.MAPLE_LOGS,
                            ItemId.MAHOGANY_LOGS, ItemId.YEW_LOGS, ItemId.MAGIC_LOGS, ItemId.REDWOOD_LOGS}) {
                        if (player.getInventory().containsItem(logId)) {
                            logs = new Item(logId);
                            break;
                        }
                    }
                    if (logs == null) {
                        player.sendMessage("Couldn't find logs.");
                        return;
                    }
                    player.getInventory().deleteItem(ItemId.CHEWED_BONES, 1);
                    player.getInventory().deleteItem(logs);
                    pyreship.setId(ObjectId.PYRE_BOAT_25291);
                    World.spawnObject(pyreship);
                } else if (ticks == 16) {
                    pyreship.setId(ObjectId.PYRE_BOAT_25292);
                    World.spawnObject(pyreship);
                    if (Utils.random(255) == 0) {
                        player.getInventory().addOrDrop(ItemId.DRAGON_FULL_HELM, 1);
                        player.getCollectionLog().add(new Item(ItemId.DRAGON_FULL_HELM));
                    } else {
                        val reward = PyreSiteRewards.random();
                        player.getInventory().addOrDrop(reward.id, Utils.random(reward.getMinimumAmount(), reward.getMaximumAmount()));
                    }
                    player.getSkills().addXp(Skills.FIREMAKING,1000);
                    player.getSkills().addXp(Skills.CRAFTING,250);

                    player.addAttribute("pyreboats", player.getNumericAttribute("pyreboats").intValue() + 1);
                    val pyres = player.getNumericAttribute("pyreboats").intValue();
                    player.sendMessage("Total spirits laid to rest: "+ Colour.RED.wrap(pyres + "."));

                    //val peacefulSpirit = new NPC(NpcId.PEACEFUL_BARBARIAN_SPIRIT, pyreship.getPosition(), Direction.SOUTH, 0);
                    //World.spawnNPC(peacefulSpirit);
                    //peacefulSpirit.setAnimation(new Animation(1296)); find the right animation id

                } else if (ticks == 22) {
                    pyreship.setId(ObjectId.PYRE_BOAT_25293);
                    World.spawnObject(pyreship);
                } else if (ticks == 24) {
                    pyreship.setId(ObjectId.PYRE_BOAT_25294);
                    World.spawnObject(pyreship);
                } else if (ticks == 26) {
                    pyreship.setId(ObjectId.PYRE_BOAT_25295);
                    World.spawnObject(pyreship);
                } else if (ticks == 27) {
                    World.removeObject(pyreship);
                    player.unlock();
                    stop();
                    return;
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {ObjectId.PYRE_SITE};
    }

    @Getter
    @AllArgsConstructor
    public enum PyreSiteRewards {
        RANARR_POTION(100,2,2),
        DEATH_RUNE(ItemId.DEATH_RUNE,8,15),
        BLOOD_RUNE(ItemId.BLOOD_RUNE,4,7),
        SILVER_BOLTS(ItemId.SILVER_BOLTS,5,5),
        RUNE_ARROW(ItemId.RUNE_ARROW,10,10),
        RUNITE_BOLTS(ItemId.RUNITE_BOLTS,10,10),
        DIAMOND(1602,2,2),
        MITH_GRAPPLE(ItemId.MITH_GRAPPLE_9419,2,2),
        ADAMANT_DART_P(ItemId.ADAMANT_DARTP,20,20),
        ADAMANT_KNIFE(ItemId.ADAMANT_KNIFE,20,20),
        ANTI_POISON_SUPERMIX(ItemId.ANTIPOISON_SUPERMIX2,1,1),
        ANTIFIRE_MIX(ItemId.ANTIFIRE_MIX1,1,1),
        ANTIFIRE_MIX2(ItemId.ANTIFIRE_MIX2,1,1),
        FISHING_MIX2(ItemId.FISHING_MIX2,1,1),
        PRAYER_MIX(ItemId.PRAYER_MIX1,1,1),
        PRAYER_MIX2(ItemId.PRAYER_MIX2,1,1),
        SUPERATTACK_MIX(ItemId.SUPERATTACK_MIX1,1,1),
        SUPERATTACK_MIX2(ItemId.SUPERATTACK_MIX2,1,1),
        SUPER_STR_MIX(ItemId.SUPER_STR_MIX1,1,1),
        SUPER_STR_MIX2(ItemId.SUPER_STR_MIX2,1,1),
        SUPER_DEF_MIX(ItemId.SUPER_DEF_MIX1,1,1),
        SUPER_DEF_MIX2(ItemId.SUPER_DEF_MIX2,1,1);

        private static final PyreSiteRewards[] values = values();
        private final int id;
        private final int minimumAmount;
        private final int maximumAmount;

        /**
         * Selects a random reward out of the values of this enum.
         * @return a random raid reward out of the lot.
         */
        public static PyreSiteRewards random() {
            return values[Utils.random(values.length - 1)];
        }
    }
}
