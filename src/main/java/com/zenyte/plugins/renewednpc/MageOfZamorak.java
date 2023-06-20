package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentUtils;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import lombok.val;

/**
 * @author Kris | 25/11/2018 20:06
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MageOfZamorak extends NPCPlugin {

    private static final Graphics TELEPORT_GFX = new Graphics(343);
    private static final Animation TELEPORT_ANIM = new Animation(1818);
    private static final ForceTalk TELEPORT_CHAT = new ForceTalk("Veniens! Sallakar! Rinnesset!");
    private static final Location ABYSS_LOCATION = new Location(3017, 4839, 0);

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new NPCChat(player, npc.getId(), "This is " +
                "no place to talk!<br><br>Meet me at the Varrock Chaos Temple!")));
        bind("Teleport", (player, npc) -> {
            npc.setAnimation(TELEPORT_ANIM);
            npc.setForceTalk(TELEPORT_CHAT);
            player.setGraphics(TELEPORT_GFX);
            npc.setGraphics(TELEPORT_GFX);
            player.lock(3);
            player.blockIncomingHits();
            WorldTasksManager.schedule(() ->  {
                player.blockIncomingHits();
                player.getAchievementDiaries().update(WildernessDiary.TELEPORT_TO_ABYSS);
                if (!EquipmentUtils.containsAbyssalBracelet(player)) {
                    player.getVariables().setSkull(true);
                    player.getPrayerManager().drainPrayerPoints(100.0, 0);
                } else {
                    val braclet = player.getEquipment().getItem(EquipmentSlot.HANDS);
                    val charges = Integer.valueOf(braclet.getName().substring(17, 18));
                    val nextCharge = new Item(braclet.getId() + 2);
                    if (charges == 1) {
                        player.sendMessage("<col=7F00FF>Your bracelet crumbles to dust.</col>");
                        player.getEquipment().deleteItem(braclet);
                    } else {
                        val message = charges == 2 ? "<col=7F00FF>Your bracelet has one charge left.</col>" : "<col=7F00FF>Your bracelet has " + Utils.format(charges - 1) + " charges left.</col>";
                        player.sendMessage(message);
                        player.getEquipment().set(EquipmentSlot.HANDS.getSlot(), nextCharge);
                    }
                }
                //player.getVarManager().sendBit(625, Utils.random(10)); used to randomize the obstacles, if you enable this make sure to fix up the object interactions
                player.setLocation(ABYSS_LOCATION);
            }, 2);
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {2581};
    }
}
