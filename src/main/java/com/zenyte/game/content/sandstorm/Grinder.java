package com.zenyte.game.content.sandstorm;

import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlayerChat;
import lombok.val;
import lombok.var;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris
 * @since August 20 2020
 */
public class Grinder implements ObjectAction {
    public static final String BUCKET_OF_SAND_AMOUNT_KEY = "sandstorm bucket of sand amount";
    private static final int MAXIMUM_BUCKET_OF_SAND_AMOUNT = 25_000;

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        deposit(player);
    }

    public static void deposit(@NotNull final Player player) {
        val currentBucketsOfSand = player.getNumericAttribute(BUCKET_OF_SAND_AMOUNT_KEY).intValue();
        if (!canDeposit(player, currentBucketsOfSand)) {
            return;
        }
        var bucketsOfSandToAdd = 0;
        val inventory = player.getInventory();
        for (val sandstone : Sandstone.SANDSTONES) {
            val amount = Math.min((MAXIMUM_BUCKET_OF_SAND_AMOUNT - currentBucketsOfSand) / sandstone.getBucketsOfSand(), inventory.getAmountOf(sandstone.getItemId()));
            if (currentBucketsOfSand + (amount * sandstone.getBucketsOfSand()) >= MAXIMUM_BUCKET_OF_SAND_AMOUNT) {
                continue;
            }
            bucketsOfSandToAdd += amount * sandstone.getBucketsOfSand();
            inventory.deleteItem(sandstone.getItemId(), amount);
        }
        val newBucketsOfSand = player.incrementNumericAttribute(BUCKET_OF_SAND_AMOUNT_KEY, bucketsOfSandToAdd);
        player.getDialogueManager().start(new NPCChat(player, NpcId.DREW, "The grinder is now holding enough sandstone equivalent to " + newBucketsOfSand + " buckets of sand."));
    }

    private static boolean canDeposit(@NotNull final Player player, @NonNegative final int currentBucketsOfSand) {
        if (currentBucketsOfSand >= MAXIMUM_BUCKET_OF_SAND_AMOUNT) {
            player.getDialogueManager().start(new PlayerChat(player, "Looks like it's already full."));
            return false;
        }
        if (!player.carryingAny(Sandstone.SANDSTONE_IDS)) {
            player.sendMessage("You do not have any sandstone to grind.");
            return false;
        }
        return true;
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {ObjectId.GRINDER};
    }
}
