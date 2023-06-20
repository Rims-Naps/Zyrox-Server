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

public class MusicCape extends ItemPlugin {

    @Override
    public void handle() {
        bind("Teleport", (player, item, slotId) -> MUSIC_CAPE_FALO.teleport(player));
        bind("Trim", (player, item, slotId) -> {
                if (player.containsItem(19476) || player.containsItem(13069) ){
                    if (player.getInventory().containsItem(13221)){
                        player.getInventory().deleteItem(13221,1);
                        player.getInventory().addOrDrop(13222);
                    }
                    if (player.getEquipment().containsItem(13221)){
                        player.sendMessage(Colour.RED.wrap("Unequip your cape to trim it."));
                    }
                }
                else{
                    player.sendMessage(Colour.RED.wrap("You do not own a Achievement diary cape, and can therefore not trim this cape."));
                }
            });
            bind("Untrim", (player, item, slotId) -> {
                if (player.getInventory().containsItem(13222)){
                    player.getInventory().deleteItem(13222,1);
                    player.getInventory().addOrDrop(13221);
                }
                if (player.getEquipment().containsItem(13222)){
                    player.sendMessage(Colour.RED.wrap("Unequip your cape to trim it."));
                }
            });
    }

    @Override
    public int[] getItems() {
        return SkillcapePerk.MUSIC.getSkillCapes();
    }

    private void teleport(final Player player, final Teleport teleport) {
        teleport.teleport(player);
    }
}