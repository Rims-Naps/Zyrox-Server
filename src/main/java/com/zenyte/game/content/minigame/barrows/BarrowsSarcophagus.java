package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 30/11/2018 18:41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BarrowsSarcophagus implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Search")) {
            val wight = Utils.findMatching(BarrowsWight.values, npc -> npc.getSarcophagusId() == object.getId());
            if (wight != null && player.getBarrows().getCurrentWight() == null) {
                val barrows = player.getBarrows();
                if (wight.equals(barrows.getHiddenWight())) {
                    player.getDialogueManager().start(new Dialogue(player) {
                        @Override
                        public void buildDialogue() {
                            val options = new ObjectArrayList<DialogueOption>();
                            if (player.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER) && barrows.isSkipTunnels()) {
                                options.add(new DialogueOption("Take me to the chest!", () -> {
                                    val barrows = player.getBarrows();
                                    player.setLocation(new Location(3551 + Utils.random(1), 9691, 0));
                                    barrows.refreshDoors();
                                }));
                            }
                            options.add(new DialogueOption("Yes, I'm fearless.", () -> {
                                val barrows = player.getBarrows();
                                val corner = barrows.getCorner();
                                player.setLocation(corner.ladder.transform(0, -1, 0));
                                barrows.refreshDoors();
                            }));
                            options.add(new DialogueOption("No way, that looks scary!"));
                            options(options.size() == 3 ? "You've found two tunnels.<br>Would you like to enter either?"
                                    : "You've found a tunnel.<br>Would you like to enter it?", options.toArray(new DialogueOption[0]));
                        }
                    });
                    return;
                }
                if (!barrows.getSlainWights().contains(wight)) {
                    barrows.sendWight(wight, player.getLocation(), "You dare disturb my rest!");
                }
            }
            player.sendMessage("You find nothing.");
            if (player.getPrayerManager().getPrayerPoints() > 0) {
                player.putBooleanAttribute("faithless_barrows", false);

            }
        }
    }

    @Override
    public Object[] getObjects() {
        val list = new ArrayList<Integer>();
        for (val wight : BarrowsWight.values) {
            list.add(wight.getSarcophagusId());
        }
        return list.toArray();
    }
}
