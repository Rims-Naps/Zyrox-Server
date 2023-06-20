package com.zenyte.plugins.object;

import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;

/**
 * @author Tommeh | 19-3-2019 | 18:07
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class HomeBoxOfRestorationObject implements ObjectAction {

    private static final Graphics HEAL_GFX = new Graphics(1177);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Restore")) {
            val time = player.getNumericAttribute("box of restoration delay").longValue();
            if (time <= System.currentTimeMillis()) {
                player.getToxins().reset();
                player.heal(player.getMaxHitpoints());
                player.getCombatDefinitions().setSpecialEnergy(100);
                player.getVariables().setRunEnergy(100);
                for (int i = 0; i < Skills.SKILLS.length; i++) {
                    if (player.getSkills().getLevel(i) < player.getSkills().getLevelForXp(i)) {
                        player.getSkills().setLevel(i, player.getSkills().getLevelForXp(i));
                    }
                }
                player.setGraphics(HEAL_GFX);
                player.sendFilteredMessage("The box of restoration completely restores your health.");
                player.addAttribute("box of restoration delay",
                        System.currentTimeMillis() + (TimeUnit.MINUTES.toMillis(getDelay(player))));
            } else {
                val totalSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(time - System.currentTimeMillis());
                val seconds = totalSeconds % 60;
                val minutes = totalSeconds / 60;
                player.sendMessage("You need to wait another " + (minutes == 0 ? (seconds + " seconds") : (minutes + " minutes") + " until you can use this again."));
            }
        } else if (option.equalsIgnoreCase("Remove-skull")) {
            if (player.getVariables().getTime(TickVariable.SKULL) > TimeUnit.MINUTES.toTicks(20)) {
                player.getDialogueManager().start(new PlainChat(player, "You are currently under the effects of a permanent skull and cannot be unskulled."));
                return;
            }
            player.getReceivedDamage().clear();
            player.getReceivedHits().clear();
            player.getVariables().removeSkull();
            player.sendFilteredMessage("Your skull has been removed.");
        }
    }

    private int getDelay(final Player player) {
        val memberRank = player.getMemberRank();
        if (memberRank.eligibleTo(MemberRank.ZENYTE_MEMBER)) {
            return 0;
        } else if (memberRank.eligibleTo(MemberRank.ONYX_MEMBER)) {
            return 0;
        } else if (memberRank.eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
            return 0;
        } else if (memberRank.eligibleTo(MemberRank.DIAMOND_MEMBER)) {
            return 0;
        } else if (memberRank.eligibleTo(MemberRank.RUBY_MEMBER)) {
            return 0;
        } else if (memberRank.eligibleTo(MemberRank.EMERALD_MEMBER)) {
            return 0;
        } else if (memberRank.eligibleTo(MemberRank.SAPPHIRE_MEMBER)) {
            return 1;
        }
        return 2;
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 35001 };
    }
}
