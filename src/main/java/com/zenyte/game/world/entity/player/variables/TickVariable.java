package com.zenyte.game.world.entity.player.variables;

import com.zenyte.game.content.consumables.Drinkable;
import com.zenyte.game.content.consumables.drinks.Drink;
import com.zenyte.game.content.kebos.alchemicalhydra.instance.AlchemicalHydraInstance;
import com.zenyte.game.content.skills.farming.Seedling;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 5. juuni 2018 : 02:32:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum TickVariable {

	ANTIFIRE(new VariableMessage(15, Colour.RS_PURPLE + "Your antifire potion is about to expire."), new VariableMessage(0, "<col=7f007f>Your antifire potion has expired.</col>")),
	SUPER_ANTIFIRE(new VariableMessage(15, Colour.RS_PURPLE + "Your super antifire potion is about to expire."), new VariableMessage(0,
			"<col=7f007f>Your super antifire potion has expired.</col>")),
	TELEBLOCK(),
    TELEBLOCK_IMMUNITY(),
	POISON_IMMUNITY(), 
	VENOM_IMMUNITY(),
    BOUNTY_HUNTER_TARGET_LOSS((player, ticks) -> {
       if (ticks == 100) {
           player.getBountyHunter().halfTimeNotification();
       } else if (ticks == 0) {
           player.getBountyHunter().abandonTarget();
       }
    }),
    WINE_FERMENTATION((player, ticks) -> {
        if (ticks == 0) {
            val cooking = player.getSkills().getLevel(Skills.COOKING);
            val inventory = player.getInventory();
            int successfulCount = 0;
            int wineCount = 0;
            int zamorakWineCount = 0;
            for (val entry : inventory.getContainer().getItems().int2ObjectEntrySet()) {
                val item = entry.getValue();
                if (item == null || (item.getId() != 1995 && item.getId() != 20752)) continue;
                val percentage = 0.5F + ((cooking - 35F) * 1.52) / 100F;
                if (percentage < Utils.randomDouble()) {
                    item.setId(1991);
                    continue;
                }
                if (item.getId() == 1995) {
                    wineCount++;
                }
                else {
                    zamorakWineCount++;
                }
                item.setId(item.getId() == 1995 ? 1993 : 245);
                successfulCount++;
            }
            inventory.refreshAll();


            int badWineCount = 0;
            val bank = player.getBank();
            for (val entry : bank.getContainer().getItems().int2ObjectEntrySet()) {
                val item = entry.getValue();
                if (item == null || (item.getId() != 1995 && item.getId() != 20752)) continue;
                for (int i = 0; i < item.getAmount(); i++) {
                    val percentage = 0.5F + ((cooking - 35F) * 1.52) / 100F;
                    if (percentage < Utils.randomDouble()) {
                        badWineCount++;
                        continue;
                    }
                    if (item.getId() == 1995) {
                        wineCount++;
                    } else {
                        zamorakWineCount++;
                    }
                    successfulCount++;
                }
                bank.set(entry.getIntKey(), null);
            }
            if (zamorakWineCount > 0) {
                bank.add(new Item(245, zamorakWineCount)).onFailure(remaining -> {
                    World.spawnFloorItem(new Item(246, remaining.getAmount()), player);
                    player.sendMessage(Colour.RS_RED.wrap("Some of your wines of zamorak were placed on the ground as your bank is full."));
                });
            }
            if (wineCount > 0) {
                bank.add(new Item(1993, wineCount)).onFailure(remaining -> {
                    World.spawnFloorItem(new Item(1994, remaining.getAmount()), player);
                    player.sendMessage(Colour.RS_RED.wrap("Some of your wines were placed on the ground as your bank is full."));
                });
            }

            if (badWineCount > 0) {
                //The wines vanish if there's not enough space.
                bank.add(new Item(1991, badWineCount));
            }
            if (successfulCount > 0) {
                player.getSkills().addXp(Skills.COOKING, wineCount * 200 + zamorakWineCount * 240);
                bank.getContainer().setFullUpdate(true);
                bank.getContainer().refresh(player);
                player.sendMessage("Your wines have finished fermenting.");
            }
        }
    }),
    SEEDLING_SPROUT((player, ticks) -> {
       if (ticks == 0) {
           val inventory = player.getInventory();
           val list = new ArrayList<Item>();
           val wiped = new ObjectArrayList<Item>();
           for (val entry : inventory.getContainer().getItems().int2ObjectEntrySet()) {
               val item = entry.getValue();
               if (item == null) continue;
               val seedling = Seedling.getWateredSeedling(item.getId());
               if (seedling == null) continue;
               wiped.add(entry.getValue());
               list.add(new Item(seedling.getSapling(), item.getAmount(), item.getCharges()));
           }
           wiped.forEach(inventory::deleteItem);
           list.forEach(inventory::addOrDrop);
           wiped.clear();
           list.clear();
           inventory.refreshAll();

           val bank = player.getBank();
           for (val entry : bank.getContainer().getItems().int2ObjectEntrySet()) {
               val item = entry.getValue();
               if (item == null) continue;
               val seedling = Seedling.getWateredSeedling(item.getId());
               if (seedling == null) continue;
               wiped.add(entry.getValue());
               list.add(new Item(seedling.getSapling(), item.getAmount(), item.getCharges()));
           }
           wiped.forEach(bank::remove);
           list.forEach(item -> bank.add(item).onFailure(it -> World.spawnFloorItem(it, player)));
           bank.getContainer().setFullUpdate(true);
           bank.getContainer().refresh(player);
           player.sendFilteredMessage("Your watered seedlings have sprouted to saplings.");
       }
    }),
	STAMINA_ENHANCEMENT((player, ticks) -> {
		if (ticks == 0) {
            player.getVarManager().sendBit(25, 0);
		}
	}, new VariableMessage(0, "<col=8f4808>Your stamina potion has expired.</col>")),
	OVERLOAD((player, ticks) -> {
		if (ticks == 0) {
			Drinkable.resetOverload(player);
		} else if (ticks % 25 == 0) {
            Drinkable.applyOverload(player);
		}
	}, new VariableMessage(25, Colour.RED + "Your divine effect has almost ran out!")),
    DIVINECOMBAT((player, ticks) -> {
        if (ticks == 0) {
            Drinkable.resetdivinecombat(player);
        } else if (ticks % 25 == 0) {
            Drinkable.applydivinecombat(player);
        }
    }, new VariableMessage(25, Colour.RED + "Your divine effect has almost ran out!")),
    DIVINEMAGIC((player, ticks) -> {
        if (ticks == 0) {
            Drinkable.resetdivinemagic(player);
        } else if (ticks % 25 == 0) {
            Drinkable.applydivinemagic(player);
        }
    }, new VariableMessage(25, Colour.RED + "Your divine effect has almost ran out!")),
    DIVINERANGE((player, ticks) -> {
        if (ticks == 0) {
            Drinkable.resetdivineranging(player);
        } else if (ticks % 25 == 0) {
            Drinkable.applydivineranging(player);
        }
    }, new VariableMessage(25, Colour.RED + "Your divine effect has almost ran out!")),
    DIVINEBATTLEMAGE((player, ticks) -> {
        if (ticks == 0) {
            Drinkable.resetdivinebattlemage(player);
        } else if (ticks % 25 == 0) {
            Drinkable.applydivinebattlemage(player);
        }
    }, new VariableMessage(25, Colour.RED + "Your divine effect has almost ran out!")),
    DIVINEDEFENCE((player, ticks) -> {
        if (ticks == 0) {
            Drinkable.resetdivinedefence(player);
        } else if (ticks % 25 == 0) {
            Drinkable.applydivinedefence(player);
        }
    }, new VariableMessage(25, Colour.RED + "Your divine effect has almost ran out!")),
    DIVINESTRENGTH((player, ticks) -> {
        if (ticks == 0) {
            Drinkable.resetdivinestrength(player);
        } else if (ticks % 25 == 0) {
            Drinkable.applydivinestrength(player);
        }
    }, new VariableMessage(25, Colour.RED + "Your divine effect has almost ran out!")),
    DIVINEBASTION((player, ticks) -> {
        if (ticks == 0) {
            Drinkable.resetdivinebastion(player);
        } else if (ticks % 25 == 0) {
            Drinkable.applydivinebastion(player);
        }
    }, new VariableMessage(25, Colour.RED + "Your divine effect has almost ran out!")),
    DIVINEATTACK((player, ticks) -> {
        if (ticks == 0) {
            Drinkable.resetdivineattack(player);
        } else if (ticks % 25 == 0) {
            Drinkable.applydivineattack(player);
        }
    }, new VariableMessage(25, Colour.RED + "Your divine effect has almost ran out!")),
	PRAYER_ENHANCE((player, ticks) -> {
		if (ticks % 6 == 0) {
			player.getPrayerManager().restorePrayerPoints(1);
		}
	}, new VariableMessage(0, Colour.RED + "Your prayer enhance effect has worn off.")),
	IMBUED_HEART_COOLDOWN(new VariableMessage(0, "<col=ef1020>Your imbued heart has regained its magical power.")),
	SKULL((player, ticks) -> {
	    if (ticks > 2000 && player.getEquipment().getId(EquipmentSlot.AMULET) != 22557) {
	        //Cannot do a direct reference to itself so we invoke it through reflection.
	        player.getVariables().cancel(TickVariable.valueOf("SKULL"));
	        player.getVariables().setSkull(true);
        }
		if (ticks == 0) {
			player.getVariables().setSkull(false);
		}
	}),
    POWER_OF_DEATH((player, ticks) -> {
        if (ticks == 0) {
            player.sendSound(new SoundEffect(1598));
            player.sendMessage(Colour.RS_GREEN.wrap("Your protection fades away."));
        }
    }),
    HAMSTRUNG(),
	CHARGE(new VariableMessage(0, "<col=ef1020>Your magical charge fades away.</col>")),
	MAGIC_IMBUE((player, ticks) -> {
		if (ticks == 0) {
			player.getVarManager().sendBit(5438, 0);
			player.sendMessage("Your Magic Imbue charge has ended.");
		}
	}),
	VENGEANCE((player, ticks) -> {
		if (ticks == 0) {
			player.getVarManager().sendBit(2451, 0);
		}
	}),
	BOX_OF_RESTORATION,
    YELL,
    HYDRA_BLEED((player, ticks) -> {
        val area = player.getArea();
        if (!(area instanceof AlchemicalHydraInstance) || player.isDead() || player.isFinished()) {
            return;
        }
        val instance = (AlchemicalHydraInstance) area;
        val nextLocation = player.getNextLocation();
        if (nextLocation != null) {
            if (!instance.inside(nextLocation)) {
                return;
            }
        }
        if (ticks < 25 && ticks % 5 == 0) {
            CombatUtilities.delayHit(null, -1, player, new Hit(5, HitType.REGULAR));
        }
    });

	final VariableTask task;
	final VariableMessage[] messages;

	TickVariable(final VariableMessage... messages) {
		this(null, messages);
	}

	TickVariable(final VariableTask task, final VariableMessage... messages) {
		this.messages = messages;
		this.task = task;
	}

}
