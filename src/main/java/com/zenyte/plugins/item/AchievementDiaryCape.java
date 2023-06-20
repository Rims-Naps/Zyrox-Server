package com.zenyte.plugins.item;

import com.zenyte.game.content.skills.magic.spells.teleports.ItemTeleport;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import lombok.val;

import static com.zenyte.game.content.skills.magic.spells.teleports.ItemTeleport.*;


/**
 * @author Cresinkel
 */

public class AchievementDiaryCape extends ItemPlugin {
    private static final ItemTeleport[] TELEPORTS = {DIARY_CAPE_TWO_PINTS, DIARY_CAPE_JARR, DIARY_CAPE_SIR_REBRAL, DIARY_CAPE_THORODIN, DIARY_CAPE_FLAX_KEEPER, DIARY_CAPE_PIRATE_JACKIE_THE_FRUIT, DIARY_CAPE_KALEB_PARAMAYA, DIARY_CAPE_JUNGLE_FORESTER, DIARY_CAPE_TZHAAR_MEJ, DIARY_CAPE_ELISE, DIARY_CAPE_HATIUS_CONSAINTUS, DIARY_CAPE_LE_SABRE, DIARY_CAPE_TOBY, DIARY_CAPE_LESSER_FANATIC, DIARY_CAPE_ELDER_GNOME_CHILD, DIARY_CAPE_TWIGGY_O_KORN };


    @Override
    public void handle() {
        bind("Teleport", (player, item, slotId) -> {
            player.getDialogueManager().start(new OptionsMenuD(player, "Select a destination", "Two-Pints",
                    "Jarr",
                    "Sir Rebral",
                    "Thorodin",
                    "Flax Keeper",
                    "Pirate Jackie the Fruit",
                    "Kaleb Paramaya",
                    "Jungle forester",
                    "TzHaar-Mej",
                    "Elise",
                    "Hatius Consaintus",
                    "Le-sabré",
                    "Toby",
                    "Lesser Fanatic",
                    "Elder Gnome Child",
                    "Twiggy O'Korn") {


                /**
                 * slotid = teleportoption
                 * TELEPORTS = array of all teleports for this cape, coming from ItemTeleport
                 */
                @Override
                public void handleClick(int slotId) {
                    val teleport = TELEPORTS[slotId];
                    teleport(player, teleport);
                }

                @Override
                public boolean cancelOption() {
                    return false;
                }
            });
        });
        bind("Trim", (player, item, slotId) -> {
            if (player.containsItem(13221) || player.containsItem(13222) ){                 //checks for music cape in bank and inven
                if (player.getInventory().containsItem(19476)){                             //checks if the untrimmed version is in inventory
                    player.getInventory().deleteItem(19476,1);                          //deletes the untrimmed version
                    player.getInventory().addOrDrop(13069);                                 //adds the trimmed version
                }
                if (player.getEquipment().containsItem(19476)){                             //checks if the untrimmed version is in equipment
                    player.sendMessage(Colour.RED.wrap("Unequip your cape to trim it."));    //sends warning
                }
            }
            else{
                player.sendMessage(Colour.RED.wrap("You do not own a Music cape, and can therefore not trim this cape."));
            }
        });
        bind("Untrim", (player, item, slotId) -> {
            if (player.getInventory().containsItem(13069)){
                player.getInventory().deleteItem(13069,1);
                player.getInventory().addOrDrop(19476);
            }
            if (player.getEquipment().containsItem(13069)){
                player.sendMessage(Colour.RED.wrap("Unequip your cape to trim it."));
            }
        });
    }

    @Override
    public int[] getItems() {                           //gets capes id's
        return SkillcapePerk.DIARY.getSkillCapes();
    }

    private void teleport(final Player player, final Teleport teleport) {
        teleport.teleport(player);
    }
}