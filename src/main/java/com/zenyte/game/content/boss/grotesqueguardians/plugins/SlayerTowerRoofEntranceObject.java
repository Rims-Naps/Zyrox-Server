package com.zenyte.game.content.boss.grotesqueguardians.plugins;

import com.zenyte.Constants;
import com.zenyte.game.content.boss.grotesqueguardians.instance.GrotesqueGuardiansInstance;
import com.zenyte.game.content.skills.slayer.BossTask;
import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import com.zenyte.plugins.dialogue.ItemChat;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Tommeh | 21/07/2019 | 20:59
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Slf4j
public class SlayerTowerRoofEntranceObject implements ObjectAction {
    
    private static final Item BRITTLE_KEY = new Item(21724);
    
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!Constants.GROTESQUE_GUARDIANS) {
            player.sendMessage("The Grotesque Guardians have temporarily been disabled. Come back later.");
            return;
        }
        if (option.equals("Unlock")) {
            if (!player.getInventory().containsItem(BRITTLE_KEY)) {
                player.getDialogueManager().start(new PlainChat(player, "You need a brittle key to unlock the gateway."));
                return;
            }
            player.addAttribute("brittle-entrance_unlocked", 1);
            VarCollection.BRITTLE_ENTRANCE_UNLOCKED.update(player);
            player.getInventory().deleteItem(BRITTLE_KEY);
            player.getDialogueManager().start(new ItemChat(player, BRITTLE_KEY, "You use the key and it is absorbed by the gateway as<br><br> it activates..."));
        } else if (option.equals("Go-through")) {
            val assignment = player.getSlayer().getAssignment();
            if (assignment == null || assignment.getTask() == null || (!assignment.getTask().equals(RegularTask.GARGOYLES) && !assignment.getTask().equals(BossTask.GROTESQUE_GUARDIANS) && !assignment.getTask().equals(RegularTask.GROTESQUE_GUARDIANS))) {
                player.getDialogueManager().start(new PlainChat(player, "You need a Gargoyle slayer task or Grotesque Guardians boss task to enter the roof."));
                return;
            }
            player.getDialogueManager().start(new PlainChat(player, "You enter the passageway, and it takes you to the roof of the tower."));
            new FadeScreen(player, () -> {
                player.lock();
                try {
                    val area = MapBuilder.findEmptyChunk(8, 8);
                    val instance = new GrotesqueGuardiansInstance(player, area);
                    instance.constructRegion();
                } catch (OutOfSpaceException e) {
                    log.error(Strings.EMPTY, e);
                }
            }).fade(3);
        }
    }
    
    @Override
    public Object[] getObjects() {
        return new Object[]{31681};
    }
}
