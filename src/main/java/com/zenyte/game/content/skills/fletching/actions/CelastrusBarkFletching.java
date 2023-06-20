package com.zenyte.game.content.skills.fletching.actions;

import com.zenyte.game.content.achievementdiary.diaries.KourendDiary;
import com.zenyte.game.content.skills.fletching.FletchingDefinitions;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 28-4-2019 | 16:33
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class CelastrusBarkFletching extends Action {

    private static final Item celastrusBark = new Item(22935);
    public static final Item battlestaff = new Item(1391);
    private static final Item knife = new Item(946);

    private final int amount;
    private int cycle, ticks;

    private boolean check() {
        if (player.getSkills().getLevel(Skills.FLETCHING) < 40) {
            player.sendMessage("You need a Fletching level of at least 40 to do that.");
            return false;
        }
        if (!player.getInventory().containsItem(knife)) {
            player.sendMessage("You need a knife to fletch this.");
            return false;
        }
        if (!player.getInventory().containsItem(celastrusBark)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean start() {
        return check();
    }

    @Override
    public boolean process() {
        if (cycle >= amount) {
            return false;
        }
        return check();
    }

    @Override
    public int processWithDelay() {
        if (ticks == 0) {
            player.setAnimation(FletchingDefinitions.ANIMATION);
        } else if (ticks == 3) {
            player.getInventory().ifDeleteItem(celastrusBark, () -> {
                player.getInventory().addItem(battlestaff);
                player.getSkills().addXp(Skills.FLETCHING, 80);
                player.getAchievementDiaries().update(KourendDiary.CREATE_BATTLESTAFF, 0x2);
            });
            cycle++;
            return ticks = 0;
        }
        ticks++;
        return 0;
    }
}
