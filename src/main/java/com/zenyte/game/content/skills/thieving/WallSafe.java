package com.zenyte.game.content.skills.thieving;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.content.achievementdiary.diaries.FaladorDiary;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.events.ServerLaunchEvent;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author Kris | 24/03/2019 20:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public class WallSafe extends Action {

    private static final Animation start = new Animation(2247);
    private static final SoundEffect attemptSound = new SoundEffect(1243);
    private static final SoundEffect successSound = new SoundEffect(1238);
    private static final Animation endAnim = new Animation(2248);
    private static final Animation failAnimation = new Animation(1113);
    private static final Animation locAnim = new Animation(1111);
    private static final SoundEffect failSound = new SoundEffect(1383);
    private static final int floorTrap = 7227;

    private static final Int2LongOpenHashMap safesMap = new Int2LongOpenHashMap();

    private final WorldObject object;

    @Subscribe
    public static final void onServerLaunch(final ServerLaunchEvent event) {
        WorldTasksManager.schedule(() -> {
            for (val map : safesMap.int2LongEntrySet()) {
                val hash = map.getIntKey();
                val time = map.getLongValue();
                if (time < System.currentTimeMillis()) {
                    val object = World.getObjectWithType(hash, 10);
                    if (object != null) {
                        World.removeObject(object);
                    }
                }
            }
        }, 100, 100);
    }

    @Override
    public boolean start() {
        if (player.getSkills().getLevelForXp(Skills.THIEVING) < 50) {
            player.sendMessage("You need a Thieving level of at least 50 to crack the wall safes.");
            return false;
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.sendMessage("You need some free space to crack the safe.");
            return false;
        }
        player.sendMessage("You start cracking the safe.");
        player.setAnimation(start);
        player.sendSound(attemptSound);
        delay(2);
        return true;
    }

    @Override
    public boolean process() {
        return object.exists();
    }

    @AllArgsConstructor
    private enum SafeLoot {
        LOW_COINS(995, 100, 250),
        MEDIUM_COINS(995, 200, 250),
        HIGH_COINS(995, 400, 250),
        SAPPHIRE(1623, 1, 50),
        EMERALD(1621, 1, 30),
        RUBY(1619, 1, 15),
        DIAMOND(1617, 1, 5);

        private final int id, amount, weight;
        private static int totalWeight;

        private static final SafeLoot[] values = values();
        static {
            for (val value : values) {
                totalWeight += value.weight;
            }
        }
    }

    @Override
    public int processWithDelay() {
        if (Utils.random(1) == 0) {
            player.setAnimation(start);
            player.sendSound(attemptSound);
            return 1;
        }
        player.lock(1);
        val hasDisarmed = player.getTemporaryAttributes().remove("Wall safe disarmed") != null;
        if (Thieving.success(player, hasDisarmed ? 30 : 50)) {
            player.setAnimation(endAnim);
            player.sendSound(attemptSound);
            WorldTasksManager.schedule(() -> {
                player.getAchievementDiaries().update(FaladorDiary.CRACK_WALL_SAFE);
                player.sendSound(successSound);
                player.setAnimation(Animation.STOP);
                player.getSkills().addXp(Skills.THIEVING, 70);
                player.sendMessage("You get some loot.");
                val cracked = new WorldObject(object);
                cracked.setId(7238);
                World.spawnObject(cracked);
                WorldTasksManager.schedule(() -> World.spawnObject(object), 1);
                val roll = Utils.random(SafeLoot.totalWeight);
                int current = 0;
                for (val loot : SafeLoot.values) {
                    if ((current += loot.weight) >= roll) {
                        player.getInventory().addItem(new Item(loot.id, loot.amount));
                        break;
                    }
                }
            });
        } else {
            player.setAnimation(endAnim);
            player.sendSound(attemptSound);
            WorldTasksManager.schedule(() -> {
                player.sendSound(failSound);
                player.setAnimation(failAnimation);
                player.setAnimation(Animation.STOP);
                if (!safesMap.containsKey(player.getLocation().getPositionHash())) {
                    World.spawnObject(new WorldObject(floorTrap, 10, 0, player.getLocation()));
                }
                val object = World.getObjectWithType(player.getLocation(), 10);
                if (object != null) {
                    World.sendObjectAnimation(object, locAnim);
                }
                safesMap.put(player.getLocation().getPositionHash(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
                player.sendMessage("You slip and trigger a trap!");
                player.applyHit(new Hit(Utils.random(2, 6), HitType.REGULAR));
                WorldTasksManager.schedule(() -> player.setAnimation(Animation.STOP));
            });
        }
        return -1;
    }
}
