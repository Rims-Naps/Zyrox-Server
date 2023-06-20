package com.zenyte.game.content.chambersofxeric.plugins.item;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.testinterfaces.ExperienceLampInterface;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.IntConsumer;

/**
 * @author Kris | 06/09/2019 20:07
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DarkRelic extends ItemPlugin {

    /**
     * The dark relic item id.
     */
    private static final int DARK_RELIC = 21027;

    /**
     * An array of skills which the dark relic can grant experience in.
     */
    private static final int[] boostedSkills = new int[]{
            Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGED, Skills.HITPOINTS, Skills.PRAYER,
            Skills.MINING, Skills.WOODCUTTING, Skills.HERBLORE, Skills.FARMING, Skills.HUNTER, Skills.COOKING, Skills.FISHING, Skills.THIEVING,
            Skills.FIREMAKING, Skills.AGILITY
    };

    @Override
    public void handle() {
        bind("Commune", (player, item, container, slotId) -> player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                item(item, "A dark power emanates from the relic.<br>" +
                        "You sense that this power can be directed.").executeAction(() -> {
                    GameInterface.EXPERIENCE_LAMP.open(player);
                    player.getTemporaryAttributes().put("experience_lamp_custom_handler", (IntConsumer) i -> {
                        player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
                        if (player.getInventory().getItem(slotId) != item) {
                            return;
                        }
                        val modifier = ArrayUtils.contains(boostedSkills, i) ? 150 : 50;
                        player.getInventory().set(slotId, null);
                        val xp = player.getSkills().getLevelForXp(i) * modifier;
                        player.getSkills().addXp(i, xp);
                        player.getPacketDispatcher().sendSoundEffect(ExperienceLampInterface.SOUND_EFFECT);
                        player.getDialogueManager().start(new PlainChat(player,
                                "<col=000080>Your wish has been granted!</col><br><br>You have been awarded " + Utils.format((xp * player.getExperienceRate(i))) + " " + Skills.getSkillName(i) + " " + "experience!"));
                    });
                });
            }
        }));
    }

    @Override
    public int[] getItems() {
        return new int[]{
                DARK_RELIC
        };
    }
}
