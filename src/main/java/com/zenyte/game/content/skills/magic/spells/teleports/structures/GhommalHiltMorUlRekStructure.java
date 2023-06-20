package com.zenyte.game.content.skills.magic.spells.teleports.structures;

import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.val;

import static com.zenyte.game.util.TimeUnit.MILLISECONDS;

/**
 * @author Cresinkel
 */
public class GhommalHiltMorUlRekStructure implements TeleportStructure {

    private static final Animation animation = new Animation(2881);
    private static final Graphics graphics = new Graphics(715);

    @Override
    public Animation getStartAnimation() {
        return animation;
    }

    @Override
    public Graphics getStartGraphics() {
        return graphics;
    }

    @Override
    public void start(Player player, Teleport teleport) {
        val experience = teleport.getExperience();
        val startAnimation = Utils.getOrDefault(getStartAnimation(), Animation.STOP);
        val startGraphics = Utils.getOrDefault(getStartGraphics(), Graphics.RESET);
        player.lock();
        val sound = getStartSound();
        if (sound != null) {
            World.sendSoundEffect(player, sound);
        }
        if (experience != 0) {
            player.getSkills().addXp(Skills.MAGIC, experience);
        }
        teleport.onUsage(player);
        player.setAnimation(startAnimation);
        player.setGraphics(startGraphics);
        WorldTasksManager.scheduleOrExecute(() -> end(player, teleport),
                (int) MILLISECONDS.toTicks(startAnimation.getCeiledDuration()) + 1);
    }

    @Override
    public void end(Player player, Teleport teleport) {
        TeleportStructure.super.end(player, teleport);
        if (player.getTemporaryAttributes().remove("mor ul rek restricted teleport") != null) {
            final int teleports = player.getVariables().getMorulrekGhommalHiltTeleports();
            if (MasterTasks.allMasterCombatAchievementsDone(player)) {
                player.sendMessage(Colour.RED.wrap("You have used up " + teleports + "/5 of your Mor Ul Rek teleports for today."));
            } else {
                player.sendMessage(Colour.RED.wrap("You have used up " + teleports + "/3 of your Mor Ul Rek teleports for today."));
            }
        }
    }
}
