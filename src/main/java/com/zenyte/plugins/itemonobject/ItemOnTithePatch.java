package com.zenyte.plugins.itemonobject;

import com.zenyte.game.content.minigame.tithefarm.TithePlant;
import com.zenyte.game.content.minigame.tithefarm.TithePlantType;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

import static com.zenyte.game.content.skills.farming.actions.Planting.SEED_DIPPING_ANIM;
import static com.zenyte.game.content.skills.farming.actions.Planting.SEED_PLANTING_SOUND;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class ItemOnTithePatch implements ItemOnObjectAction {

    @Override
    public final void handleItemOnObjectAction(final Player player, final Item item, final int slot, final WorldObject object) {
        final TithePlantType type = TithePlantType.SEEDS_MAP.get(item.getId());

        if(type == null) {
            player.sendMessage("Nothing interesting happens.");
            return;
        }

        if(!player.getInventory().containsItem(ItemId.SEED_DIBBER)) {
            player.sendMessage("You need a seed dibber to plant seeds!", MessageType.UNFILTERABLE);
            return;
        }

        if(player.getSkills().getLevel(Skills.FARMING) < type.getFarmingLevel()) {
            player.sendMessage("You need at least level "+type.getFarmingLevel()+" Farming to plant "+type.toString().toLowerCase()+" seeds.");
            return;
        }

        WorldTasksManager.schedule(new WorldTask() {

            private int ticks = 0;

            @Override
            public final void run() {
                if(ticks == 0) {
                    // yes, we lock the player, this was matched to osrs.
                    player.lock();

                    player.faceObject(object);
                    player.setAnimation(SEED_DIPPING_ANIM);
                    player.getPacketDispatcher().sendSoundEffect(SEED_PLANTING_SOUND);

                    player.getInventory().deleteItem(item.getId(), 1);
                    final WorldObject plantObject = new WorldObject(type.getSeedling(), object.getType(), object.getRotation(), object);
                    World.spawnObject(plantObject);

                    // create a new titheplant object which will automatically be stored on this players list.
                    final TithePlant plant = new TithePlant(player, plantObject, type);
                    TithePlant.GLOBAL_PLANTS.put(plantObject.getPositionHash(), plant);
                } else if(ticks == 1) {
                    stop();
                    player.unlock();
                }

                ticks++;
            }
        }, 0, 1);
    }

    @Override
    public final Object[] getItems() {
        final List<Object> list = new ArrayList<Object>();
        for(final TithePlantType plant : TithePlantType.VALUES) {
            list.add(plant.getSeed());
        }
        return list.toArray(new Object[list.size()]);
    }

    @Override
    public final Object[] getObjects() {
        return new Object[] { ObjectId.TITHE_PATCH };
    }
}
