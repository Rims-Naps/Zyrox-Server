package com.zenyte.game.content.skills.hunter.actions;

import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.content.skills.hunter.TrapState;
import com.zenyte.game.content.skills.hunter.node.TrapPrey;
import com.zenyte.game.content.skills.hunter.node.TrapType;
import com.zenyte.game.content.skills.hunter.object.HunterTrap;
import com.zenyte.game.content.skills.hunter.plugins.ItemTrapSetupAction;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author Kris | 29/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public class CheckPlacedTrap extends BuiltHunterTrapAction {

    private static final Animation snareAndNetTrapAnimation = new Animation(5207);
    private static final Animation boxAnimation = new Animation(5212);

    private final HunterTrap trap;
    private final boolean reset;

    @Override
    public boolean start() {
        if (!preconditions(player, false)) {
            return false;
        }
        val type = trap.getType();
        val listOfItemsToBeGiven = new ObjectArrayList<Item>();
        if (type.isNetTrap()) {
            listOfItemsToBeGiven.add(new Item(ItemId.ROPE, 1));
            listOfItemsToBeGiven.add(new Item(ItemId.SMALL_FISHING_NET, 1));
        }
        val trapId = type.getItemId();
        if (trapId != -1) {
            listOfItemsToBeGiven.add(new Item(trapId));
        }
        val prey = trap.getPrey();
        if (prey != null) {
            for (val immutableItem : prey.getItems()) {
                listOfItemsToBeGiven.add(new Item(immutableItem.getId(), Utils.random(immutableItem.getMinAmount(), immutableItem.getMaxAmount())));
            }
        }
        val inventory = player.getInventory();
        int spaceRequired = 0;
        for (val item : listOfItemsToBeGiven) {
            val stackable = item.isStackable();
            val amount = item.getAmount();
            if (!stackable) {
                spaceRequired += amount;
            } else {
                //If the user has none of the item, it should require one more inventory space.
                if (inventory.getAmountOf(item.getId()) == 0) {
                    spaceRequired++;
                }
            }
        }
        if (inventory.getFreeSlots() < spaceRequired) {
            player.sendMessage("You don't have enough inventory space. You need " + spaceRequired + " more inventory slot" + (spaceRequired == 1 ? "" : "s") + ".");
            return false;
        }
        player.setAnimation(type == TrapType.BIRD_SNARE || type.isNetTrap() ? snareAndNetTrapAnimation : boxAnimation);
        player.lock(3);
        player.setFaceLocation(trap.getLocation());
        delay(2);
        return true;
    }

    @Override
    public boolean process() {
        return true;
    }

    @Override
    public int processWithDelay() {
        if (!preconditions(player, false)) {
            return -1;
        }
        val type = trap.getType();
        val state = trap.getState();
        //If the trap's status has changed.
        if (state != TrapState.COLLAPSED && type != TrapType.DEADFALL) {
            return -1;
        }
        val prey = trap.getPrey();
        val inventory = player.getInventory();
        trap.remove();
        if (type.isNetTrap()) {
            inventory.addOrDrop(ItemId.ROPE, 1);
            inventory.addOrDrop(ItemId.SMALL_FISHING_NET, 1);
        }
        val trapId = type.getItemId();
        if (trapId != -1) {
            inventory.addOrDrop(new Item(trapId));
        }
        player.sendSound(type.getTakeSound());
        player.sendFilteredMessage("You dismantle the trap.");
        if (prey != null) {
            checkTasks();
            for (val immutableItem : prey.getItems()) {
                if (prey.getTrap().equals(TrapType.BOX_TRAP)) {
                    inventory.addOrDrop(new Item(immutableItem.getId(), Utils.random(immutableItem.getMinAmount(), immutableItem.getMaxAmount())));
                }
                inventory.addOrDrop(new Item(immutableItem.getId(), Utils.random(immutableItem.getMinAmount(), immutableItem.getMaxAmount())));
            }
            player.getSkills().addXp(Skills.HUNTER, prey.getExperience());
            player.sendFilteredMessage("You've caught " + Utils.getAOrAn(prey.toString()) + " " + prey + ".");
        }
        if (reset) {
            scheduleSetup();
        }
        return -1;
    }

    private void scheduleSetup() {
        //If the player moves, cancel the resetting action for a new trap.
        val location = player.getLocation().getPositionHash();
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (player.getLocation().getPositionHash() != location) {
                    stop();
                } else if (ticks++ == 2) {
                    player.getActionManager().setAction(new ItemTrapSetupAction(trap.getType(), null, -1, new Location(player.getLocation())));
                    stop();
                }
            }
        }, 0, 0);
    }

    private void checkTasks() {
        val prey = trap.getPrey();
        if (prey == null) {
            return;
        }
        if (prey.equals(TrapPrey.CARNIVOROUS_CHINCHOMPA)) {
            player.getDailyChallengeManager().update(SkillingChallenge.CATCH_RED_CHINCHOMPAS);
            SherlockTask.CATCH_RED_CHINCHOMPA.progress(player);
        } else if (prey.equals(TrapPrey.BLACK_CHINCHOMPA)) {
            player.getDailyChallengeManager().update(SkillingChallenge.CATCH_BLACK_CHINCHOMPAS);
        } else if (prey.equals(TrapPrey.CHINCHOMPA)) {
            player.getAchievementDiaries().update(KourendDiary.CATCH_A_CHINCHOMPA);
            player.getDailyChallengeManager().update(SkillingChallenge.CATCH_GREY_CHINCHOMPAS);
        } else if (prey == TrapPrey.RED_SALAMANDER) {
            player.getAchievementDiaries().update(ArdougneDiary.CATCH_RED_SALAMANDER);
        } else if (prey == TrapPrey.ORANGE_SALAMANDER) {
            player.getAchievementDiaries().update(DesertDiary.CATCH_ORANGE_SALAMANDER);
        } else if (prey == TrapPrey.CERULEAN_TWITCH) {
            player.getAchievementDiaries().update(FremennikDiary.CATCH_CERULEAN_TWITCH);
        } else if (prey == TrapPrey.SWAMP_LIZARD) {
            player.getAchievementDiaries().update(MorytaniaDiary.CATCH_SWAMP_LIZARD);
        } else if (prey == TrapPrey.COPPER_LONGTAIL) {
            player.getAchievementDiaries().update(WesternProvincesDiary.CATCH_COPPER_LONGTAIL);
        } else if (prey == TrapPrey.BLACK_SALAMANDER) {
            player.getAchievementDiaries().update(WildernessDiary.CATCH_A_BLACK_SALAMANDER);
        } else if (prey == TrapPrey.GOLDEN_WARBLER) {
            player.getAchievementDiaries().update(DesertDiary.CATCH_GOLDEN_WARBLER);
        }
    }

    @Override
    public void stop() {
    }

    @Override
    protected HunterTrap getTrap() {
        return trap;
    }
}
