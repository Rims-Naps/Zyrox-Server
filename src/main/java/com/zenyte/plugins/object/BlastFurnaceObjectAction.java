package com.zenyte.plugins.object;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.minigame.blastfurnace.BlastFurnaceArea;
import com.zenyte.game.content.minigame.blastfurnace.BlastFurnaceOre;
import com.zenyte.game.content.skills.smithing.SmeltableBar;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.impl.blastfurnace.ConveyerBeltOreNPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.*;
import com.zenyte.plugins.dialogue.skills.BlastFurnaceBarD;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 *
 * 9089 - temperature
 * 9090 - pump
 * 9085 - stove
 * 9088 - coke
 * 9097 - pedals
 * 9098 - melting pot
 * 9100 - conveyor belt
 * 9092:9096 - bar dispenser
 * 29330 - coffer
 *
 */
public class BlastFurnaceObjectAction implements ObjectAction {

    private static final Item ICE_GLOVES = new Item(1580, 1);
    private static final Item ENHANCED_ICE_GLOVES = new Item(30030, 1);
    private static final Item COINS_PLACEHOLDER = new Item(1000, 1);

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {

        // temperature
        if (object.getId() == 9089) {
            GameInterface.BLAST_FURNACE_TEMPERATURE.open(player);
            return;
        }

        // placeholder code for melting pot
        if (object.getId() == 9098) {
            player.getDialogueManager().start(new BlastFurnaceOreD(player));
            return;
        }

        // conveyer belt
        if (object.getId() == 9100) {
    
            val primaryOres = player.getBlastFurnace().checkPrimaryOres();

            if(player.getBlastFurnace().getCoffer() == 0) {
                player.getDialogueManager().start(new ItemChat(player, COINS_PLACEHOLDER, "You must put money in the coffer to pay the workers."));
                return;
            }
    
            if (primaryOres >= 28 && !player.getBlastFurnace().hasSecondaryOres()) {
                player.getDialogueManager().start(new PlainChat(player, "You should make sure all your ore smelts before adding any more."));
                return;
            }

            if(!player.getInventory().containsAnyOf(BlastFurnaceOre.ORE_IDS_ARRAY)) {
                player.getDialogueManager().start(new PlainChat(player, "You don't have any suitable ores to place onto the conveyor belt."));
                return;
            }
    
            if (player.getBlastFurnace().getTotalBars() >= 28 && !player.getBlastFurnace().hasSecondaryOres()) {
                player.getDialogueManager().start(new PlainChat(player, "You should collect your bars before making any more."));
                return;
            }
    
            boolean secondaryOnly = primaryOres >= 28;
            boolean hasPrimaryOverflow = false;
            boolean hasSecondaryOverflow = false;
            boolean npcSpawned = false;

            for(final BlastFurnaceOre ore : BlastFurnaceOre.VALUES) {
                if (secondaryOnly && ore.isPrimaryOre()) {
                    continue;
                }

                if(player.getInventory().containsItem(ore.getItemId(), 1)) {
                    val bar = SmeltableBar.getDataByBar(ore.getBarId());
    
                    if (player.getSkills().getLevel(Skills.SMITHING) < bar.getLevel()) {
                        player.sendMessage("You need to have level " + bar.getLevel() + " smithing to put " + ore.toString().toLowerCase().replace("_", " ") + " on the conveyor belt.");
                        continue;
                    }
    
                    if (ore.isPrimaryOre() && primaryOres >= 28 && !player.getInventory().containsAnyOf(BlastFurnaceOre.TIN_ORE.getItemId(), BlastFurnaceOre.COAL.getItemId())) {
                        hasPrimaryOverflow = true;
                        continue;
                    }
    
                    if (!ore.isPrimaryOre()) {
                        if (player.getBlastFurnace().getOre(BlastFurnaceOre.COAL) == 254 && !player.getInventory().containsItem(BlastFurnaceOre.TIN_ORE.getItemId(), 1)) {
                            hasSecondaryOverflow = true;
                            continue;
                        }
        
                        if (player.getBlastFurnace().getOre(BlastFurnaceOre.TIN_ORE) == 254 && !player.getInventory().containsItem(BlastFurnaceOre.COAL.getItemId(), 1)) {
                            hasSecondaryOverflow = true;
                            continue;
                        }
                    }

                    int amount = player.getInventory().getAmountOf(ore.getItemId());
                    //amount = ore.equals(BlastFurnaceOre.COAL) ? (coal + amount > 254 ? (254-coal) : amount) : (primaryOres + amount > 28 ? (28-primaryOres) : amount);
                    amount = !ore.isPrimaryOre() ? (player.getBlastFurnace().getOre(ore) + amount > 254 ? (254 - player.getBlastFurnace().getOre(ore)) : amount) :
                                     (primaryOres + amount > 28 ? (28 - primaryOres) : amount);
                    val ores = new Item(ore.getItemId(), amount);
                    player.getInventory().deleteItem(ores);
                    player.getBlastFurnace().setOresOnBelt(player.getBlastFurnace().getOresOnBelt()+1);
                    World.spawnNPC(new ConveyerBeltOreNPC(ore.getNpcId(), BlastFurnaceArea.ORE_CONVEYER_START, Direction.SOUTH, 0, player, ores));
                    npcSpawned = true;
                }
            }
    
            if (hasPrimaryOverflow || hasSecondaryOverflow) {
                player.sendMessage("Please allow your ore to smelt before adding more to the melting pot.");
            } else if (npcSpawned) {
                player.sendMessage("All your ore goes onto the conveyor belt.");
            }
        }

        // coffer functionality
        if (object.getId() == 29330) {
            if(player.getBlastFurnace().getCoffer() == 0 && !player.getInventory().containsItem(995, 1)) {
                player.getDialogueManager().start(new ItemChat(player, COINS_PLACEHOLDER, "There are no coins in the coffer or your inventory."));
                return;
            }

            player.getDialogueManager().start(new GenericCofferD(player, "blast_furnace_coffer", 5356));
            return;
        }

        // bar dispenser
        if (object.getId() == 9092) {
    
            if (option.toLowerCase().equals("check")) {
                player.getDialogueManager().start(new BlastFurnaceDispenserD(player));
                return;
            }

            if (object.getId(player) == 9093) {
                player.getDialogueManager().start(new PlainChat(player, "The dispenser doesn't contain any bars."));
                return;
            }
        
            if (object.getId(player) == 9095) {
                val gloves = player.getEquipment().getItem(EquipmentSlot.HANDS);

                if(gloves == null || (gloves.getId() != ICE_GLOVES.getId() && gloves.getId() != ENHANCED_ICE_GLOVES.getId())) {
                    player.getDialogueManager().start(new PlainChat(player, "The bars are still molten! You need to cool them down."));
                    return;
                }

                if(gloves != null) {
                    if (gloves.getId() == ICE_GLOVES.getId() || gloves.getId() == ENHANCED_ICE_GLOVES.getId()) {
                        Item[] barsArray = player.getBlastFurnace().constructBarArray();
                        ArrayUtils.reverse(barsArray);

                        if (barsArray.length == 0) {
                            player.getBlastFurnace().setDispenser(0);
                            player.getDialogueManager().start(new PlainChat(player, "You have no bars. Please attempt to recreate this and report it to an administrator."));
                            return;
                        }

                        player.getBlastFurnace().setDispenser(3);
                        player.getBlastFurnace().setEarlyCool(true);

                        if(gloves.getId() == ICE_GLOVES.getId()) {
                            gloves.setCharges(gloves.getCharges()-1);

                            if(gloves.getCharges() <= 0) {
                                player.getEquipment().set(EquipmentSlot.HANDS, null);
                                player.getEquipment().refresh();
                            }
                        }

                        if (player.getInventory().hasFreeSlots()) {
                            player.getDialogueManager().start(new BlastFurnaceBarD(player, barsArray));
                        } else {
                            player.sendMessage("You don't have any inventory space to grab bars!");
                        }

                        return;
                    }
                }
            }
        
            if (object.getId(player) == 9096) {
                Item[] barsArray = player.getBlastFurnace().constructBarArray();
                ArrayUtils.reverse(barsArray);
    
                if (barsArray.length != 0) {
                    if (player.getInventory().hasFreeSlots())
                        player.getDialogueManager().start(new BlastFurnaceBarD(player, barsArray));
                    else
                        player.sendMessage("You don't have any inventory space to grab bars!");
                } else {
                    player.getBlastFurnace().setDispenser(0);
                    player.getDialogueManager().start(new PlainChat(player, "You have no bars. Please attempt to recreate this and report it to an administrator."));
                }

                return;
            }
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 9089, 9090, 9085, 9088, 9097, 9098, 9092, 9093, 9094, 9095, 9096, 9100, 29330 };
    }
}
