package com.zenyte.plugins.drop.wilderness;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.combatachievements.combattasktiers.*;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.npc.spawns.NPCSpawnLoader;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import com.zenyte.game.world.region.area.wilderness.WildernessGodwarsDungeon;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;

/**
 * @author Kris | 28/04/2019 17:55
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class EcumenicalKeyProcessor extends DropProcessor {

    @Override
    public void attach() {
        appendDrop(new DisplayedDrop(11942, 1, 1, 0) {
            @Override
            public double getRate(final Player player, final int id) {
                return rate(player);
            }
        });
        for (val id : allIds) {
            put(id, 11942, new PredicatedDrop("Only dropped by those in Wilderness Godwars Dungeon. " +
                    "Only up to 3(4 & 5 after medium & hard Wilderness Diaries respectively) keys at a time."));
        }
    }

    @Override
    public void onDeath(final NPC npc, final Player killer) {
        if (WildernessArea.isWithinWilderness(npc.getX(), npc.getY())) {
            if (random(rate(killer)) == 0) {
                npc.dropItem(killer, new Item(11942));
            }
        }
    }

    private int rate(final Player player) {
        boolean easy = EasyTasks.allEasyCombatAchievementsDone(player);
        boolean medium = MediumTasks.allMediumCombatAchievementsDone(player) && easy;
        boolean hard = HardTasks.allHardCombatAchievementsDone(player) && medium;
        boolean elite = EliteTasks.allEliteCombatAchievementsDone(player) && hard;
        boolean master = MasterTasks.allMasterCombatAchievementsDone(player) && elite;
        boolean grandmaster = GrandmasterTasks.allGrandmasterCombatAchievementsDone(player) && master;
        return grandmaster ? 40 : master ? 45 : elite ? 50 : hard ? 55 : 60;
    }

    @Override
    public int[] ids() {
        val set = new IntOpenHashSet();
        val polygon = WildernessGodwarsDungeon.polygon;
        for (val spawn : NPCSpawnLoader.DEFINITIONS) {
            if (polygon.contains(spawn.getX(), spawn.getY())) {
                set.add(spawn.getId());
            }
        }
        return set.toIntArray();
    }
}
