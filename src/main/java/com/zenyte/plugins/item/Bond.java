package com.zenyte.plugins.item;

import com.google.common.primitives.Ints;
import com.zenyte.Constants;
import com.zenyte.api.client.query.BondClaimRequest;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Tommeh | 08/06/2019 | 22:41
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Bond extends ItemPlugin {
    
    @Override
    public void handle() {
        bind("Redeem", (player, item, slotId) -> {
            val bond = com.zenyte.api.model.Bond.Companion.getBond(item.getId());
            
            if (Constants.WORLD_PROFILE.isPrivate() || Constants.WORLD_PROFILE.isBeta()) {
                player.sendMessage("You cannot do that on this world.");
                return;
            }
            
            val credits = bond.getCredits();
            val donated = bond.getAmount();
    
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    item(item, "Would you like to redeem this bond for <col=000080>" + credits + " store credits</col> and $" + donated + " rank credit</col>?");
                    options("Redeem the bond for <col=000080>" + credits + " store credits</col> and $<col=000080>" + donated + " rank credit</col>?", "Yes.", "No.")
                            .onOptionOne(() -> {
                                val succeeded = player.getInventory().deleteItem(item).getSucceededAmount();
                                if (succeeded > 0) {
                                    CoresManager.getServiceProvider().submit(() -> {
                                        new BondClaimRequest(player, item.getId()).execute();
                                        player.syncTotalDonated();
                                    });
                                    setKey(5);
                                } else {
                                    setKey(10);
                                }
                            });
                    plain(5, "You have successfully redeemed your bond for <col=000080>" + credits + " store credits</col> and" +
                                     " $<col=000080>" + donated + " rank credit</col>.");
                    plain(10, "Failure to redeem bond.");
                }
            });
        });
    }
    
    @Override
    public int[] getItems() {
        return Ints.toArray(com.zenyte.api.model.Bond.getItemIds());
    }
}
