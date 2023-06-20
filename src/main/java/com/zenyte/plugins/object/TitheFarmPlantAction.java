package com.zenyte.plugins.object;

import com.zenyte.game.content.minigame.tithefarm.TithePlant;
import com.zenyte.game.content.minigame.tithefarm.TithePlantType;
import com.zenyte.game.content.minigame.tithefarm.TitheStatus;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import static com.zenyte.game.content.skills.farming.actions.Planting.SPADE_ANIM;
import static com.zenyte.game.content.skills.farming.actions.Planting.SPADE_PLANTING_SOUND;
import static com.zenyte.game.content.skills.farming.actions.Watering.*;


/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TitheFarmPlantAction implements ObjectAction {

    private final Item getWateringCan(final Player player) {
        // i believe this is the best way to iterate the users inventory.
        for(final Item item : player.getInventory().getContainer().getItems().values()) {
            if(item.getId() >= ItemId.WATERING_CAN1 && item.getId() <= ItemId.WATERING_CAN8) {
                return player.getInventory().getItem(player.getInventory().getContainer().getSlotOf(item.getId()));
            }
        }

        // return null if not found.
        return null;
    }

    @Override
    public final void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        final TithePlant plant = TithePlant.GLOBAL_PLANTS.get(object.getPositionHash());
        if(plant == null) {
            return;
        }

        if(plant.getOwner() != player) {
            player.sendMessage("This plant was planted by "+plant.getOwner().getUsername()+".");
            return;
        }

        if(option.equals("Water")) {
            if(!plant.isUnwatered()) {
                player.sendMessage("It doesn't need watering now.");
                return;
            }

            final Item wateringCan = getWateringCan(player);

            if(!player.getInventory().containsItem(ItemId.GRICOLLERS_CAN) && wateringCan == null) {
                player.sendMessage("You need a watering can with water in it to water the plant!");
                return;
            }

            WorldTasksManager.schedule(new WorldTask() {

                private int ticks = 0;

                @Override
                public void run() {
                    if(ticks == 0) {
                        player.lock();
                        if(!player.getInventory().containsItem(ItemId.GRICOLLERS_CAN)) {
                            // replace the original spot where the watering can is with the new one.
                            player.getInventory().replaceItem(wateringCan.getId() == ItemId.WATERING_CAN1 ? ItemId.WATERING_CAN : wateringCan.getId() - 1, 1,
                                    player.getInventory().getContainer().getSlot(wateringCan));
                        }

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

        if(option.equals("Clear")) {
            if(!plant.isBlighted()) {
                player.sendMessage("Nothing interesting happens");
                return;
            }

            WorldTasksManager.schedule(new WorldTask() {

                private int ticks = 0;

                @Override
                public void run() {
                    if (ticks == 0) {
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

        if(option.equals("Harvest")) {
            if(!plant.isReady()) {
                return;
            }

            WorldTasksManager.schedule(new WorldTask() {

                private int ticks;

                @Override
                public void run() {
                    if(ticks == 0) {
                        player.setAnimation(SPADE_ANIM);
                        player.getPacketDispatcher().sendSoundEffect(SPADE_PLANTING_SOUND);
                        player.getInventory().addItem(plant.getType().getFruit());
                        player.getSkills().addXp(Skills.FARMING, plant.getType().getBaseXp());
                        plant.die();
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
    public final Object[] getObjects() {
        val keyset = TithePlantType.OBJECT_MAP.keySet();
        return keyset.toArray(new Object[keyset.size()]);
    }
}
