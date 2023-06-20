package com.zenyte.plugins.renewednpc;

import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 16-12-2018 | 20:58
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class HomeShopNPC extends NPCPlugin {

    private static final Map<Integer, String> SHOPS = new HashMap<Integer, String>() {{
        put(10001, "Melee Armoury Shop");
        put(10002, "Melee Weaponry Shop");
        put(10003, "Ranged Armoury Shop");
        put(10004, "Ranged Weaponry Shop");
        put(10005, "Magic Armoury Shop");
        put(10006, "Magic Weaponry Shop");
        put(10007, "Food Shop");
        put(10008, "Tools Shop");
        put(10009, "Vote Shop");
        put(10011, "Event Shop");
    }};

    @Override
    public void handle() {
        bind("Trade", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.stopAll();
                player.faceEntity(npc);
                if (npc.getId() == 10002 && player.getNumericAttribute("demon_kills").intValue() == 100) {
                    player.openShop("Melee Weaponry Shop<Alternative>");
                } else {
                    player.openShop(SHOPS.get(npc.getId()));
                }
            }

            @Override
            public void execute(final Player player, final NPC npc) {
                player.stopAll();
                player.setFaceEntity(npc);
                handle(player, npc);
                if (npc.getId() != 10009) { //all but vote shop
                    npc.setInteractingWith(player);
                }
            }
        });

        bind("Vote shop", new OptionHandler() {
            @Override
            public void handle(final Player player, final NPC npc) {
                player.openShop("Vote Shop");
                player.sendMessage("You currently have " + Colour.RED.wrap(player.getNumericAttribute("vote_points").intValue()) + " vote points.");
            }
            @Override
            public void execute(final Player player, final NPC npc) {
                player.stopAll();
                player.setFaceEntity(npc);
                handle(player, npc);
                if (npc.getId() != 10009) { //all but vote shop
                    npc.setInteractingWith(player);
                }
            }
        });
        bind("Loyalty shop", new OptionHandler() {
            @Override
            public void handle(final Player player, final NPC npc) {
                player.openShop("Loyalty Shop");
                player.sendMessage("You currently have " + Colour.RED.wrap(player.getLoyaltyManager().getLoyaltyPoints()) + " loyalty points.");

            }
            @Override
            public void execute(final Player player, final NPC npc) {
                player.stopAll();
                player.setFaceEntity(npc);
                handle(player, npc);
                if (npc.getId() != 10009) { //all but vote shop
                    npc.setInteractingWith(player);
                }
            }
        });
        bind("Jewellery", (player, npc) -> player.openShop("Jackie's Jewellery Shop"));
        bind("Event shop", (player, npc) -> player.openShop("Event Token Exchange Shop"));
    }



    @Override
    public int[] getNPCs() {
        return new int[] { 10001, 10002, 10003, 10004, 10005, 10006, 10007, 10008, 10009 };
    }
}
