package com.zenyte.game.world.entity.npc.impl.wilderness;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.skills.woodcutting.AxeDefinitions;
import com.zenyte.game.content.skills.woodcutting.actions.Woodcutting;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.EnumSet;

/**
 * @author Kris | 25/01/2019 16:07
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Trunk extends NPC {

    Trunk(final Ent ent, final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
        this.ent = ent;
        this.setSpawned(true);
        this.ticks = 100;
    }

    private int ticks;
    private final Ent ent;

    protected void onFinish(final Entity source) {
        val finished = isFinished();
        super.onFinish(source);
        if (!finished) {
            ent.setRespawnTask();
        }
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (--ticks <= 0) {
            sendDeath();
        }
    }

    public static final class TrunkPlugin extends NPCPlugin {

        //TODO: Different rates for specific combinations; better axe improves better loot chance etc.
        //TODO: Wilderness ones drop in quantities of 2; the ones in woodcutting guild only drop 1 each.
        @AllArgsConstructor
        private enum TrunkLogs {
            LOGS(1, new Item(1512)),
            OAK_LOGS(15, new Item(1522)),
            WILLOW_LOGS(30, new Item(1520, 2)),
            MAPLE_LOGS(45, new Item(1518, 2)),
            YEW_LOGS(60, new Item(1516, 2)),
            MAGIC_LOGS(75, new Item(1514, 2));
            private static final TrunkLogs[] values = values();
            private final int level;
            private final Item logs;
        }

        @Override
        public void handle() {
            bind("Chop", (player, npc) -> player.getActionManager().setAction(new Action() {
                private EnumSet<TrunkLogs> logs;
                private Woodcutting.AxeResult axe;
                private int level;
                @Override
                public boolean start() {
                    if (!player.getInventory().hasFreeSlots()) {
                        player.sendMessage("You need some more free space to cut the trunk.");
                        return false;
                    }
                    val result = Woodcutting.getAxe(player);
                    if (!result.isPresent()) {
                        player.sendMessage("You do not have an axe which you have the woodcutting level to use.");
                        return false;
                    }
                    this.level = player.getSkills().getLevelForXp(Skills.WOODCUTTING);
                    axe = result.get();
                    logs = EnumSet.noneOf(TrunkLogs.class);
                    for (val log : TrunkLogs.values) {
                        if (level >= log.level) {
                            logs.add(log);
                        }
                    }
                    player.sendMessage("You swing your axe at the trunk.");
                    delay(5);
                    animate();
                    return true;
                }

                @Override
                public boolean process() {
                    if (!player.getInventory().hasFreeSlots()) {
                        player.sendMessage("You need some more free space to cut the trunk.");
                        return false;
                    }
                    return !npc.isFinished();
                }

                @Override
                public int processWithDelay() {
                    player.getSkills().addXp(Skills.WOODCUTTING, 25);
                    val log = Utils.getRandomCollectionElement(logs);
                    if (log == TrunkLogs.YEW_LOGS) {
                        player.getAchievementDiaries().update(WildernessDiary.CHOP_YEW_LOGS);
                    }
                    player.getInventory().addItem(log.logs);
                    animate();
                    return 5;
                }

                private void animate() {
                    val animation = new Animation(axe.getDefinitions().equals(AxeDefinitions.DRAGON) ? 3292 :
                            3291 - axe.getDefinitions().ordinal() + 1);
                    player.setAnimation(animation);
                }
            }));
        }

        @Override
        public int[] getNPCs() {
            return new int[] {6595};
        }
    }

}
