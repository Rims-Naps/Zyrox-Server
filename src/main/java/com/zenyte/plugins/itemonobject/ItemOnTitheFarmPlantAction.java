package com.zenyte.plugins.itemonobject;

import com.zenyte.game.content.minigame.tithefarm.TithePlant;
import com.zenyte.game.content.minigame.tithefarm.TithePlantType;
import com.zenyte.game.content.minigame.tithefarm.TitheStatus;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

import static com.zenyte.game.content.skills.farming.actions.Planting.SPADE_ANIM;
import static com.zenyte.game.content.skills.farming.actions.Planting.SPADE_PLANTING_SOUND;
import static com.zenyte.game.content.skills.farming.actions.Watering.*;


/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class ItemOnTitheFarmPlantAction implements ItemOnObjectAction {

    private static final Animation FERTILISER_ANIM = new Animation(2283);

    @Override
    public final void handleItemOnObjectAction(final Player player, final Item item, final int slot, final WorldObject object) {
        final TithePlant plant = TithePlant.GLOBAL_PLANTS.get(object.getPositionHash());
        if(plant == null) {
            return;
        }

        if(plant.getOwner() != player) {
            player.sendMessage("This plant was planted by "+plant.getOwner().getUsername()+".");
            return;
        }

        // this means it is a watering action
        if(item.getId() >= ItemId.WATERING_CAN1 && item.getId() <= ItemId.WATERING_CAN8 || item.getId() == ItemId.GRICOLLERS_CAN) {
            if(!TitheStatus.getStatus(object.getId()).toLowerCase().contains("unwatered")) {
                player.sendMessage("It doesn't need watering now.");
                return;
            }

            WorldTasksManager.schedule(new WorldTask() {

                private int ticks = 0;

                @Override
                public void run() {
                    if(ticks == 0) {
                        player.lock();
                        if(item.getId() != ItemId.GRICOLLERS_CAN) {
                            // replace the original spot where the watering can is with the new one.
                            player.getInventory().replaceItem(item.getId() == ItemId.WATERING_CAN1 ? ItemId.WATERING_CAN : item.getId() - 1, 1, slot);
                        }

                        player.faceObject(object);
                        player.setAnimation(WATERING_ANIM);
                        player.setGraphics(WATERING_GRAPHICS);
                        player.getPacketDispatcher().sendSoundEffect(WATERING_SOUND);
                        plant.water();
                    } else if(ticks == 1) {
                        player.unlock();
                        stop();
                    }

                    ticks++;
                }
            }, 0, 1);
        }

        // this is a clear action
        if(item.getId() == ItemId.SPADE || item.getId() == ItemId.GILDED_SPADE) {

            if(!object.getName().toLowerCase().contains("blighted")) {
                player.sendMessage("Nothing interesting happens");
                return;
            }

            WorldTasksManager.schedule(new WorldTask() {

                private int ticks = 0;

                @Override
                public void run() {
                    if (ticks == 0) {
                        player.faceObject(object);
                        player.setAnimation(SPADE_ANIM);
                        player.getPacketDispatcher().sendSoundEffect(SPADE_PLANTING_SOUND);
                        plant.die();
                    } else if (ticks == 1) {
                        player.unlock();
                        stop();
                    }
                    ticks++;
                }

            }, 0, 1);
        }

        if(item.getId() == ItemId.GRICOLLERS_FERTILISER) {
            if(plant.isFertilised()) {
                player.sendMessage("It's already been fertilised!");
                return;
            }

            WorldTasksManager.schedule(new WorldTask() {

                private int ticks;

                @Override
                public void run() {
                    if(ticks == 0) {
                        player.lock();
                        player.faceObject(object);
                        player.setAnimation(FERTILISER_ANIM);
                        plant.setFertilised(true);
                    } else if(ticks == 1) {
                        player.unlock();
                        stop();
                    }
                    ticks++;
                }

            }, 0, 1);
        }
    }

    @Override
    public final Object[] getItems() {
        final List<Integer> list = new ArrayList<>();

        for(int i = ItemId.WATERING_CAN1; i <= ItemId.WATERING_CAN8; i++) {
            list.add(i);
        }

        list.add(ItemId.SPADE);
        list.add(ItemId.GILDED_SPADE);
        list.add(ItemId.GRICOLLERS_CAN);
        list.add(ItemId.GRICOLLERS_FERTILISER);

        return list.toArray(new Object[list.size()]);
    }

    @Override
    public final Object[] getObjects() {
        val keyset = TithePlantType.OBJECT_MAP.keySet();
        return keyset.toArray(new Object[keyset.size()]);
    }
}
