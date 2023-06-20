package com.zenyte.game.content.theatreofblood.plugin.item;

import com.zenyte.game.content.skills.magic.spells.teleports.TeleportCollection;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import lombok.val;

/**
 * @author Tommeh | 5/26/2020 | 6:21 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class VerzikCrystalShard extends ItemPlugin {

    public static final Item verzikCrystalShard = new Item(ItemId.VERZIKS_CRYSTAL_SHARD);

    @Override
    public void handle() {
        bind("Activate", (player, item, slotId) -> {
            if (!(player.getArea() instanceof TheatreArea)) {
                player.sendMessage("The crystal shard seems inert outside the Theatre of Blood.");
                return;
            }
            val party = VerSinhazaArea.getParty(player);
            if (party == null) {
                return;
            }
            player.getInventory().deleteItem(verzikCrystalShard);
            TeleportCollection.VERZIK_CRYSTAL_SHARD.teleport(player);
            WorldTasksManager.schedule(new TickTask() {
                @Override
                public void run() {
                    switch (ticks++) {
                        case 1:
                            PartyOverlayInterface.fadeRed(player, "The crystal teleports you out.");
                            break;
                        case 3:
                            if(party.getMembers().size() > 1) {
                                party.removeMember(player);
                            }
                            PartyOverlayInterface.fade(player, 200, 0, "The crystal teleports you out.");
                            PartyOverlayInterface.refresh(player, party);
                            stop();
                            break;
                    }
                }
            }, 0, 0);
        });
    }

    @Override
    public int[] getItems() {
        return new int[]{ItemId.VERZIKS_CRYSTAL_SHARD};
    }
}
