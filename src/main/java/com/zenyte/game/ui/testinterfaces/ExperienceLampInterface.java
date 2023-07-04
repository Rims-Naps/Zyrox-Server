package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;

import java.util.function.IntConsumer;

/**
 * @author Tommeh | 8-11-2018 | 18:38
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ExperienceLampInterface extends Interface {

    public static final SoundEffect SOUND_EFFECT = new SoundEffect(2655, 0, 0);

    @Override
    protected void attach() {
        put(3, "Attack");
        put(4, "Strength");
        put(5, "Ranged");
        put(6, "Magic");
        put(7, "Defence");
        put(8, "Hitpoints");
        put(9, "Prayer");
        put(10, "Agility");
        put(11, "Herblore");
        put(12, "Thieving");
        put(13, "Crafting");
        put(14, "Runecraft");
        put(15, "Mining");
        put(16, "Smithing");
        put(17, "Fishing");
        put(18, "Cooking");
        put(19, "Firemaking");
        put(20, "Woodcutting");
        put(21, "Fletching");
        put(22, "Slayer");
        put(23, "Farming");
        put(24, "Construction");
        put(25, "Hunter");
        put(26, "Confirm");
    }

    @Override
    public void open(Player player) {
        player.getVarManager().sendVar(261, 0);
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    protected void build() {
        for (val name : Skills.SKILLS) {
            bind(name, player -> {
                val id = Skills.getSkill(name);
                if (id == 22) {
                    player.sendMessage("You cannot gain experience in Construction right now. Please choose another skill.");
                    return;
                }
                player.getVarManager().sendVar(261, Skills.getInternalId(id));
            });
        }
        bind("Confirm", player -> {
            val id =  Skills.getReverseInternalId(player.getVarManager().getValue(261));
            val consumer = player.getTemporaryAttributes().get("experience_lamp_custom_handler");
            if (consumer instanceof IntConsumer) {
                ((IntConsumer) consumer).accept(id);
                return;
            }
            val object = player.getTemporaryAttributes().get("experience_lamp_info");
            if (!(object instanceof Object[])) {
                return;
            }
            var args = (Object[]) object;
            val experience = (int) args[0];
            val minimumLevel = (int) args[1];
            val slotId = (int) args[2];
            val item = (Item) args[3];
            if (player.getInventory().getItem(slotId) != item) {
                return;
            }
            if (id < 0) {
                player.sendMessage("You haven't selected a skill.");
                return;
            }
            if (player.getSkills().getLevelForXp(id) < minimumLevel) {
                player.sendMessage("You need to select a skill that is at least level " + minimumLevel + ".");
                return;
            }
            player.getInventory().set(slotId, null);
            player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
            player.getSkills().addXp(id, experience);
            player.getPacketDispatcher().sendSoundEffect(SOUND_EFFECT);
            player.getDialogueManager().start(new PlainChat(player,
                    "<col=000080>Your wish has been granted!</col><br><br>You have been awarded " + (experience * player.getExperienceRate(id)) + " " + Skills.getSkillName(id) + " " + "experience!"));
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.EXPERIENCE_LAMP;
    }
}
