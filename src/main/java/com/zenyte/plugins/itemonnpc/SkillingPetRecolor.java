package com.zenyte.plugins.itemonnpc;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.impl.slayer.Rockslug;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Cresinkel
 */

public final class SkillingPetRecolor implements ItemOnNPCAction {

    @Override
    public void handleItemOnNPCAction(final Player player, final Item item, final int slot, final NPC npc) {
        if (npc.getId() == 7352 && item.getId() == ItemId.ACORN
                || npc.getId() == 14039 && item.getId() == ItemId.DRAGONFRUIT_TREE_SEED
                || npc.getId() == 14040 && item.getId() == ItemId.GUAM_SEED
                || npc.getId() == 14041 && item.getId() == ItemId.CRYSTAL_SHARD
                || npc.getId() == 14042 && item.getId() == ItemId.WHITE_LILY_SEED
                || npc.getId() == 14043 && item.getId() == ItemId.REDWOOD_TREE_SEED
                || npc.getId() == 14044 && item.getId() == ItemId.DARK_ACORN
                || npc.getId() == 7351 && item.getId() == ItemId.ACORN
                || npc.getId() == 7353 && item.getId() == ItemId.WHITE_BERRIES
                || npc.getId() == 14046 && item.getId() == ItemId.REDBERRIES
                || npc.getId() == 14045 && item.getId() == ItemId.POISON_IVY_BERRIES
                || npc.getId() == 7370 && item.getId() == ItemId.RED_FIRELIGHTER
                || npc.getId() == 14047 && item.getId() == ItemId.BLUE_FIRELIGHTER
                || npc.getId() == 14048 && item.getId() == ItemId.PURPLE_FIRELIGHTER
                || npc.getId() == 14049 && item.getId() == ItemId.GREEN_FIRELIGHTER
                || npc.getId() == 14050 && item.getId() == ItemId.WHITE_FIRELIGHTER
                || npc.getId() == 14051 && item.getId() == ItemId.BLUE_FEATHER
                || npc.getId() == 6722 && item.getId() == ItemId.FEATHER) {
            return;
        }
        if (npc.getDefinitions().getName().contains("root")) {
            if (item.getId() == ItemId.ACORN) {
                player.setPetId(7352);
                player.getFollower().setTransformation(7352);
                player.getInventory().deleteItem(ItemId.ACORN, 1);
            }
            if (item.getId() == ItemId.DRAGONFRUIT_TREE_SEED) {
                player.setPetId(14039);
                player.getFollower().setTransformation(14039);
                player.getInventory().deleteItem(ItemId.DRAGONFRUIT_TREE_SEED, 1);
            }
            if (item.getId() == ItemId.GUAM_SEED) {
                player.setPetId(14040);
                player.getFollower().setTransformation(14040);
                player.getInventory().deleteItem(ItemId.GUAM_SEED, 1);
            }
            if (item.getId() == ItemId.CRYSTAL_SHARD) {
                player.setPetId(14041);
                player.getFollower().setTransformation(14041);
                player.getInventory().deleteItem(ItemId.CRYSTAL_SHARD, 1);
            }
            if (item.getId() == ItemId.WHITE_LILY_SEED) {
                player.setPetId(14042);
                player.getFollower().setTransformation(14042);
                player.getInventory().deleteItem(ItemId.WHITE_LILY_SEED, 1);
            }
            if (item.getId() == ItemId.REDWOOD_TREE_SEED) {
                player.setPetId(14043);
                player.getFollower().setTransformation(14043);
                player.getInventory().deleteItem(ItemId.REDWOOD_TREE_SEED, 1);
            }
        }
        if (npc.getDefinitions().getName().contains("Squirrel")) {
            if (item.getId() == ItemId.DARK_ACORN) {
                player.setPetId(14044);
                player.getFollower().setTransformation(14044);
                player.getInventory().deleteItem(ItemId.DARK_ACORN, 1);
            }
            if (item.getId() == ItemId.ACORN) {
                player.setPetId(7351);
                player.getFollower().setTransformation(7351);
                player.getInventory().deleteItem(ItemId.ACORN, 1);
            }
        }
        if (npc.getDefinitions().getName().contains("Rocky") || npc.getDefinitions().getName().contains("Red") || npc.getDefinitions().getName().contains("Ziggy")) {
            if (item.getId() == ItemId.POISON_IVY_BERRIES) {
                player.setPetId(14045);
                player.getFollower().setTransformation(14045);
                player.getInventory().deleteItem(ItemId.POISON_IVY_BERRIES, 1);
            }
            if (item.getId() == ItemId.REDBERRIES) {
                player.setPetId(14046);
                player.getFollower().setTransformation(14046);
                player.getInventory().deleteItem(ItemId.REDBERRIES, 1);
            }
            if (item.getId() == ItemId.WHITE_BERRIES) {
                player.setPetId(7353);
                player.getFollower().setTransformation(7353);
                player.getInventory().deleteItem(ItemId.WHITE_BERRIES, 1);
            }
        }
        if (npc.getDefinitions().getName().contains("Phoenix")) {
            if (item.getId() == ItemId.RED_FIRELIGHTER && player.getInventory().containsItem(ItemId.RED_FIRELIGHTER, 250)) {
                player.setPetId(7370);
                player.getFollower().setTransformation(7370);
                player.getInventory().deleteItem(ItemId.RED_FIRELIGHTER, 250);

            }
            if (item.getId() == ItemId.BLUE_FIRELIGHTER && player.getInventory().containsItem(ItemId.BLUE_FIRELIGHTER, 250)) {
                player.setPetId(14047);
                player.getFollower().setTransformation(14047);
                player.getInventory().deleteItem(ItemId.BLUE_FIRELIGHTER, 250);
            }
            if (item.getId() == ItemId.PURPLE_FIRELIGHTER && player.getInventory().containsItem(ItemId.PURPLE_FIRELIGHTER, 250)) {
                player.setPetId(14048);
                player.getFollower().setTransformation(14048);
                player.getInventory().deleteItem(ItemId.PURPLE_FIRELIGHTER, 250);
            }
            if (item.getId() == ItemId.GREEN_FIRELIGHTER && player.getInventory().containsItem(ItemId.GREEN_FIRELIGHTER, 250)) {
                player.setPetId(14049);
                player.getFollower().setTransformation(14049);
                player.getInventory().deleteItem(ItemId.GREEN_FIRELIGHTER, 250);
            }
            if (item.getId() == ItemId.WHITE_FIRELIGHTER && player.getInventory().containsItem(ItemId.WHITE_FIRELIGHTER, 250)) {
                player.setPetId(14050);
                player.getFollower().setTransformation(14050);
                player.getInventory().deleteItem(ItemId.WHITE_FIRELIGHTER, 250);
            }
        }
        if (npc.getDefinitions().getName().toLowerCase().contains("heron")) {
            if (item.getId() == ItemId.BLUE_FEATHER) {
                player.setPetId(14051);
                player.getFollower().setTransformation(14051);
                player.getInventory().deleteItem(ItemId.BLUE_FEATHER, 1);
            }
            if (item.getId() == ItemId.FEATHER) {
                player.setPetId(6722);
                player.getFollower().setTransformation(6722);
                player.getInventory().deleteItem(ItemId.FEATHER, 1);
            }
        }
    }

    @Override
    public Object[] getItems() {
        return new Object[] {ItemId.ACORN, ItemId.DRAGONFRUIT_TREE_SEED, ItemId.GUAM_SEED, ItemId.CRYSTAL_SHARD,
                ItemId.WHITE_LILY_SEED, ItemId.REDWOOD_TREE_SEED, ItemId.DARK_ACORN, ItemId.REDBERRIES,
                ItemId.WHITE_BERRIES, ItemId.POISON_IVY_BERRIES, ItemId.RED_FIRELIGHTER, ItemId.BLUE_FIRELIGHTER,
                ItemId.WHITE_FIRELIGHTER, ItemId.GREEN_FIRELIGHTER, ItemId.PURPLE_FIRELIGHTER, ItemId.FEATHER, ItemId.BLUE_FEATHER};
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { "Tangleroot", "Dark Squirrel", "Giant Squirrel", "Rocky", "Ziggy", "Red", "Phoenix", "Heron", "Great blue heron" };
    }

}