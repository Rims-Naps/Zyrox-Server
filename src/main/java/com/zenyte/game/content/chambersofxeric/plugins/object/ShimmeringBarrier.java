package com.zenyte.game.content.chambersofxeric.plugins.object;

import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.room.DeathlyRoom;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import static com.zenyte.game.content.chambersofxeric.room.DeathlyRoom.placingKeystoneAnimation;
import static com.zenyte.game.content.chambersofxeric.room.DeathlyRoom.keystoneCrystal;

/**
 * @author Kris | 06/07/2019 04:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ShimmeringBarrier implements ObjectAction, ItemOnObjectAction {

    private static final SoundEffect sound = new SoundEffect(1657, 10, 0);

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        dispel(player, object);
    }

    private final void dispel(final Player player, final WorldObject object) {
        player.getRaid().ifPresent(raid -> raid.ifInRoom(player, DeathlyRoom.class, room -> {
            if (!player.getInventory().containsItem(keystoneCrystal)) {
                player.sendMessage("You're going to need a magical keystone to dispel this barrier.");
                return;
            }
            val amount = raid.getOriginalPlayers().size();
            val rangersAmount = amount >= 7 ? 4 : amount >= 4 ? 3 : 2;
            val magersAmount = amount > 9 ? 4 : amount >= 4 ? 3 : 2;
            val total = magersAmount + rangersAmount;
            boolean allAlive = true;
            for (val npc : room.getNpcs()) {
                if (npc.isDead() || npc.isFinished()) {
                    allAlive = false;
                }
            }
            if (allAlive) {
                for (val p : raid.getPlayers()) {
                    if (!p.getBooleanAttribute("master-combat-achievement19")) {
                        p.putBooleanAttribute("master-combat-achievement19", true);
                        MasterTasks.sendMasterCompletion(p, 19);
                    }
                }
            }
            room.getNpcs().forEach(NPC::sendDeath);
            player.setAnimation(placingKeystoneAnimation);
            World.sendSoundEffect(player.getLocation(), sound);
            player.sendMessage("Your keystone glows as it is absorbed into the barrier, which disperses.");
            player.getInventory().deleteItem(keystoneCrystal);
            World.removeObject(object);
            Raid.incrementPoints(player, 2000);
        }));
    }

    @Override
    public void handleItemOnObjectAction(final Player player, final Item item, final int slot, final WorldObject object) {
        dispel(player, object);
    }

    @Override
    public Object[] getItems() {
        return new Object[]{
                keystoneCrystal.getId()
        };
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{
                29749
        };
    }
}
