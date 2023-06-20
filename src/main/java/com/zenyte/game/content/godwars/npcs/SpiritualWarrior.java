package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Christopher
 * @since 3/9/2020
 */
public class SpiritualWarrior extends KillcountNPC {

    protected SpiritualWarrior(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
    }

    @Override
    public void onDeath(Entity source) {
        super.onDeath(source);
        if (source instanceof Player) {
            val player = (Player) source;
            player.getAchievementDiaries().update(WildernessDiary.KILL_A_SPIRITUAL_WARRIOR);
        }
    }

    @Override
    public boolean validate(int id, String name) {
        return name.equalsIgnoreCase("Spiritual warrior");
    }
}
