package com.zenyte.game.content.skills.magic.spells.teleports.structures;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.val;

import static com.zenyte.game.util.TimeUnit.MILLISECONDS;

/**
 * @author Tommeh | 23-3-2019 | 00:41
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ZenyteTabletStructure implements TeleportStructure {

    private static final int DELAY = Animation.getSynchronizedAnimationDelay(4069);
    private static final Animation BREAK_ANIM = new Animation(4069, DELAY);
    private static final SoundEffect SOUND = new SoundEffect(965, 5, DELAY);

    private static final Graphics SHRINK_GFX = new Graphics(2001);
    private static final Animation SHRINK_ANIM = new Animation(4071);

    @Override
    public Animation getStartAnimation() {
        return BREAK_ANIM;
    }

    @Override
    public void start(final Player player, final Teleport teleport) {
        val experience = teleport.getExperience();
        val startAnimation = Utils.getOrDefault(getStartAnimation(), Animation.STOP);
        val startGraphics = Utils.getOrDefault(getStartGraphics(), Graphics.RESET);

        World.sendSoundEffect(player, SOUND);

        player.lock();
        teleport.onUsage(player);
        if (experience != 0) {
            player.getSkills().addXp(Skills.MAGIC, experience);
        }
        player.setAnimation(startAnimation);
        player.setGraphics(startGraphics);

        val breakDuration = (int) MILLISECONDS.toTicks(BREAK_ANIM.getCeiledDuration()) - 1;
        val shrinkDuration = (int) MILLISECONDS.toTicks(SHRINK_ANIM.getCeiledDuration());

        WorldTasksManager.scheduleOrExecute(() -> {
            player.setAnimation(SHRINK_ANIM);
            player.setGraphics(SHRINK_GFX);
        }, breakDuration);

        WorldTasksManager.scheduleOrExecute(() -> end(player, teleport), breakDuration + shrinkDuration);
    }

    @Override
    public void end(final Player player, final Teleport teleport) {
        if (isTeleportPrevented(player, teleport) || isAreaPrevented(player, teleport) || isRestricted(player, teleport)) {
            stop(player, teleport);
            return;
        }

        val endAnimation = Utils.getOrDefault(getEndAnimation(), Animation.STOP);
        val endGraphics = Utils.getOrDefault(getEndGraphics(), Graphics.RESET);
        val location = getRandomizedLocation(player, teleport);
        verifyLocation(player, location);
        teleport.onArrival(player);
        player.setLocation(location);
        player.setAnimation(endAnimation);
        player.setGraphics(endGraphics);
        player.getInterfaceHandler().closeInterfaces();
        WorldTasksManager.scheduleOrExecute(() -> stop(player, teleport),
                (int) MILLISECONDS.toTicks(endAnimation.getCeiledDuration()) + EXTRA_DELAY - 1);
        updateDiaries(player, teleport);
    }

}
