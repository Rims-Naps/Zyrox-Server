package com.zenyte.game.content.skills.cooking;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.SkillDialogue;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Kris | 21. aug 2018 : 19:00:35
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class MilkChurning implements ObjectAction {

	private static final Item BUCKET = new Item(1925);

	@RequiredArgsConstructor
	private static enum Churnable {

		BUCKET_OF_MILK(new Item(1927), 0, 0, null),

		CREAM(new Item(2130), 18, 8, new Animation(2793)),
		BUTTER(new Item(6697), 41, 16, new Animation(2794)),
		CHEESE(new Item(1985), 64, 25, new Animation(2795));

		private final Item product;
		private final int xp;
		private final int delay;
		private final Animation animation;

		private static final Churnable[] VALUES = values();

		@Override
		public String toString() {
			val name = name().toLowerCase().replaceAll("_", " ").replaceAll("bucket of ", "");
			return name;
		}
	}

	@AllArgsConstructor
	private static final class ChurnAction extends Action {

		private final Churnable churnable;
		private int amount;

		@Override
		public boolean start() {
			val ingredient = getQuickestIngredient();
			if (ingredient == null) {
				player.sendMessage("You need some milk to churn " + churnable.toString() + ".");
				return false;
			}
			val ordinal = churnable.ordinal() - ingredient.ordinal();
			val churn = Churnable.VALUES[ordinal];
			player.setAnimation(churn.animation);
			delay(churn.delay);
			return true;
		}

		private Churnable getQuickestIngredient() {
			val inventory = player.getInventory();
			if (churnable == Churnable.CHEESE) {
				if (inventory.containsItem(Churnable.BUTTER.product)) {
					return Churnable.BUTTER;
				} else if (inventory.containsItem(Churnable.CREAM.product)) {
					return Churnable.CREAM;
				}
			} else if (churnable == Churnable.BUTTER) {
				if (inventory.containsItem(Churnable.CREAM.product)) {
					return Churnable.CREAM;
				}
			}
			if (inventory.containsItem(Churnable.BUCKET_OF_MILK.product)) {
				return Churnable.BUCKET_OF_MILK;
			}
			return null;
		}

		@Override
		public boolean process() {
			return true;
		}

		@Override
		public int processWithDelay() {
			val quickestIngredient = getQuickestIngredient();
			if (quickestIngredient == null) {
				return -1;
			}
			val ingredient = quickestIngredient.product;
			val inventory = player.getInventory();
			inventory.deleteItem(ingredient);
			inventory.addItem(churnable.product);
			if (quickestIngredient == Churnable.BUCKET_OF_MILK) {
				inventory.addItem(BUCKET).onFailure(item -> World.spawnFloorItem(item, player));
			}
			val xp = churnable.xp - quickestIngredient.xp;
			player.getSkills().addXp(Skills.COOKING, xp);
			player.sendMessage("You churn your " + quickestIngredient.toString() + " to make " + churnable.toString() + ".");
			if (--amount <= 0) {
				return -1;
			}
			val nextQuickest = getQuickestIngredient();
			if (nextQuickest == null) {
				return -1;
			}
			val ordinal = churnable.ordinal() - nextQuickest.ordinal();
			val churn = Churnable.VALUES[ordinal];
			player.setAnimation(churn.animation);
			return churn.delay + 2;
		}

		@Override
		public void stop() {
			player.setAnimation(Animation.STOP);
		}
	}

	private static final class ChurnDialogue extends SkillDialogue {

		private static final Item[] ITEMS = new Item[] { Churnable.CREAM.product, Churnable.BUTTER.product, Churnable.CHEESE.product };

		public ChurnDialogue(final Player player) {
			super(player, ITEMS);
		}

		@Override
		public void run(final int slotId, final int amount) {
			if (slotId < 0 || slotId >= ITEMS.length) {
				return;
			}
			val churnable = Churnable.VALUES[slotId + 1];
			player.getActionManager().setAction(new ChurnAction(churnable, amount));
		}

	}

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId,
			final String option) {
		player.getDialogueManager().start(new ChurnDialogue(player));
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 11695 };
	}

}
