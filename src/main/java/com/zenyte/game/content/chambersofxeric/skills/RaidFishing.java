package com.zenyte.game.content.chambersofxeric.skills;

import com.zenyte.game.content.chambersofxeric.room.ResourcesRoom;
import com.zenyte.game.content.skills.fishing.FishingTool;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.WorldObject;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableBoolean;

/**
 * @author Kris | 19. nov 2017 : 17:07.54
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@RequiredArgsConstructor
public final class RaidFishing extends Action {

    /**
     * The fishing animation played when a player attempts to catch some fish.
     */
    private static final Animation animation = new Animation(622);

    /**
     * The sound effect played when the player's inventory is fully and can no longer fish anymore.
     */
    private static final SoundEffect inventoryFullSound = new SoundEffect(2277);

    /**
     * The type of the fish the player is catching, based on their fishing level.
     */
    private RaidFish fishType;

    /**
     * The fishing tool they're using, can only be the {@link FishingTool#FISHING_ROD}.
     */
    private FishingTool.Tool tool;

    /**
     * The fishing spot object.
     */
    private final WorldObject spot;

    @Override
    public boolean start() {
        val tool = FishingTool.FISHING_ROD.getTool(player);
        if (!tool.isPresent()) {
            player.sendMessage("You need a fishing rod to fish here.");
            return false;
        }
        this.tool = tool.get();
        if (!player.getInventory().containsItem(20853, 1)) {
            player.sendMessage("You need some suitable bait to catch these fish.");
            return false;
        } else if (!player.getInventory().hasFreeSlots()) {
            player.sendSound(inventoryFullSound);
            player.sendMessage("Your inventory is too full to do that.");
            return false;
        }
        for (val data : RaidFish.values) {
            if (player.getSkills().getLevel(Skills.FISHING) < data.getRequirement()) {
                break;
            }
            this.fishType = data;
        }
        val bool = new MutableBoolean();
        player.getRaid().ifPresent(raid -> raid.ifInRoom(player, ResourcesRoom.class, room -> {
            if (room.getFishingSpotPosition().matches(spot)) {
                bool.setTrue();
                room.riseSnake();
                player.sendMessage("This seems like a very bad moment to fish here.");
            }
        }));
        delay(this.tool.isIncreasedSpeed() ? 3 : 4);
        player.setAnimation(animation);
        if (bool.isTrue()) {
            return false;
        }
        player.sendMessage("You cast out your line...");
        return true;
    }

    /**
     * Checks whether or not the player has successfully caught a fish.
     * @return whether or not the player successfully caught a fish.
     */
    public boolean success() {
        val fishLevel = this.fishType.getRequirement();
        val level = player.getSkills().getLevel(Skills.FISHING);
        val advancedLevels = level - fishLevel;
        return Math.min(Math.round(advancedLevels * 0.6F) + 30, 70) > Utils.random(100);
    }

    @Override
    public boolean process() {
        player.setAnimation(animation);
        if (!player.getInventory().hasFreeSlots()) {
            player.setAnimation(Animation.STOP);
            player.sendSound(inventoryFullSound);
            player.sendMessage("Your inventory is too full to do that.");
            return false;
        } else if (!player.getInventory().containsItem(20853, 1)) {
            player.sendMessage("You need some suitable bait to catch these fish.");
            return false;
        }
        val bool = new MutableBoolean();
        player.getRaid().ifPresent(raid -> raid.ifInRoom(player, ResourcesRoom.class, room -> {
            if (room.getFishingSpotPosition().matches(spot)) {
                bool.setTrue();
                room.riseSnake();
                player.sendMessage("This seems like a very bad moment to fish here.");
            }
        }));
        return !bool.isTrue();
    }

    @Override
    public int processWithDelay() {
        if (!success()) {
            return this.tool.isIncreasedSpeed() ? 3 : 4;
        }
        player.getSkills().addXp(Skills.FISHING, fishType.getExperience());
        player.getInventory().addItem(fishType.getFish());
        player.getInventory().deleteItem(20853, 1);
        return this.tool.isIncreasedSpeed() ? 3 : 4;
    }

    @Override
    public void stop() {

    }

}
