package com.zenyte.game.content.treasuretrails.stash;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.zenyte.Constants;
import com.zenyte.game.content.treasuretrails.clues.emote.SlotLimitationRequirement;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.events.InitializationEvent;
import com.zenyte.plugins.events.LoginEvent;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import mgi.types.config.ObjectDefinitions;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author Kris | 27. nov 2017 : 0:47.00
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@RequiredArgsConstructor
public final class Stash {

	private static final Animation hammeringAnimation = new Animation(3676);
	private static final Animation searchAnimation = new Animation(832);
	private static final SoundEffect fillSound = new SoundEffect(2739);
	private static final SoundEffect emptyingSound = new SoundEffect(2582);

	@Subscribe
	public static final void onInitialization(@NotNull final InitializationEvent event) {
		val player = event.getPlayer();
		val savedPlayer = event.getSavedPlayer();
		val stash = savedPlayer.getStash();
		player.getStash().stashes = (stash == null || stash.stashes == null) ? new LinkedHashMap<>() : stash.stashes;
	}

	@Subscribe
	public static final void onLogin(@NotNull final LoginEvent event) {
		val player = event.getPlayer();
		val stash = player.getStash();
		if (stash == null) {
			return;
		}
		val stashes = stash.stashes;
		if (stashes == null || stashes.isEmpty()) {
			return;
		}
		val varManager = player.getVarManager();
		for (val entrySet : stashes.entrySet()) {
			varManager.sendBit(ObjectDefinitions.getOrThrow(entrySet.getKey().getObjectId()).getVarbitId(), 1);
		}
	}

	private final transient Player player;
	private LinkedHashMap<StashUnit, ObjectArrayList<Item>> stashes;

	private final boolean isBuilt(@NotNull final StashUnit unit) {
		return stashes.containsKey(unit);
	}

	public final boolean isFilled(@NotNull final StashUnit unit) {
		val list = stashes.get(unit);
		return list != null && !list.isEmpty();
	}

	public final void search(@NotNull final WorldObject object) {
		val unit = Objects.requireNonNull(StashUnit.getMap().get(object.getId()));
		if (unit.isDisabled()) {
			player.sendMessage("This STASH unit cannot be searched at the time.");
			return;
		}
		if (!isBuilt(unit)) {
			player.sendMessage("You have not yet built this STASH unit.");
			return;
		}
		if (isFilled(unit)) {
			empty(unit);
		} else {
			fill(unit);
		}
	}

	private final void fill(@NotNull final StashUnit unit) {
		val clue = unit.getClue();
		val requirements = clue.getRequirements();
		val slotsSet = new IntOpenHashSet();
		val inventory = player.getInventory();
		val items = new ObjectArrayList<Item>();
		//Populate per-index basis, including nulls.
		for (int i = 0; i < 28; i++) {
			items.add(inventory.getItem(i));
		}
		val itemsArray = items.toArray(new Item[0]);
		for (val requirement : requirements) {
			if (requirement instanceof SlotLimitationRequirement) {
				continue;
			}
			val slots = requirement.getFulfillingItemsIndexes(itemsArray);
			if (slots != null) {
				slotsSet.addAll(slots);
				continue;
			}
			player.sendMessage("You need all of the required items in order to store them here.");
			player.sendMessage("Keep an eye out for messages you may receive around the world, they may indicate what you need.");
			return;
		}
		val collection = stashes.get(unit);
		for (val slot : slotsSet) {
			val item = inventory.getItem(slot);
			collection.add(item);
			inventory.deleteItem(slot, item);
		}
		player.sendSound(fillSound);
		player.setAnimation(searchAnimation);
		player.sendMessage("You deposit your items into the STASH unit.");
	}

	private final void empty(@NotNull final StashUnit unit) {
		val items = Objects.requireNonNull(stashes.get(unit));
		Preconditions.checkArgument(!items.isEmpty());
		val count = items.size();
		val inventory = player.getInventory();
		if (inventory.getFreeSlots() < count) {
			player.sendMessage("You need at least " + count + " free inventory space to take the items from the STASH unit.");
			return;
		}
		for (val item : items) {
			inventory.addOrDrop(item);
		}
		items.clear();
		player.sendSound(emptyingSound);
		player.setAnimation(searchAnimation);
		player.sendMessage("You withdraw your items from the STASH unit.");
	}

    public void build(@NotNull final WorldObject object) {
		val unit = Objects.requireNonNull(StashUnit.getMap().get(object.getId()));
		if (unit.isDisabled()) {
			player.sendMessage("This STASH unit cannot be built at the time.");
			return;
		}
		if (isBuilt(unit)) {
			player.sendMessage("You have already built this STASH unit.");
			return;
		}
		val skill = Constants.CONSTRUCTION ? Skills.CONSTRUCTION : Skills.CRAFTING;
		val type = unit.getType();
		if (!player.getSkills().checkLevel(skill, type.getLevel(), "build this stash")) {
			return;
		}
		val inventory = player.getInventory();
		val hasHammer = inventory.containsItem(ItemId.HAMMER, 1);
		val hasSaw = inventory.containsItem(ItemId.SAW, 1) || inventory.containsItem(ItemId.CRYSTAL_SAW, 1);
		val hasNails = containsNails(player, 10);
		val hasMaterials = inventory.containsItems(type.getMaterials());

		if (!hasHammer || !hasSaw || !hasNails || !hasMaterials) {
			player.sendMessage("To build this stash unit, you need: ");
			if (!hasHammer) {
				player.sendMessage(Colour.RED.wrap("Hammer"));
			}
			if (!hasSaw) {
				player.sendMessage(Colour.RED.wrap("Saw"));
			}
			if (!hasNails) {
				player.sendMessage(Colour.RED.wrap("10 x Nails"));
			}
			if (!hasMaterials) {
				for (val item : type.getMaterials()) {
					if (!inventory.containsItem(item)) {
						player.sendMessage(Colour.RED.wrap(item.getAmount() + " x " + item.getName()));
					}
				}
			}
			return;
		}
		player.setAnimation(hammeringAnimation);
		player.getSkills().addXp(skill, type.getExperience());
		removeNails(player, 10);
		inventory.deleteItems(type.getMaterials());
		stashes.put(unit, new ObjectArrayList<>());
		player.getVarManager().sendBit(object.getDefinitions().getVarbitId(), 1);
		player.sendFilteredMessage("You build a STASH unit.");
    }

    private static final IntList usableNailsList = IntLists.unmodifiable(new IntArrayList(new int[] {
    		ItemId.BRONZE_NAILS, ItemId.IRON_NAILS, ItemId.STEEL_NAILS, ItemId.BLACK_NAILS, ItemId.MITHRIL_NAILS, ItemId.ADAMANTITE_NAILS, ItemId.RUNE_NAILS
    }));

    @SuppressWarnings("SameParameterValue")
	private final boolean containsNails(@NotNull final Player player, final int amount) {
		val inventory = player.getInventory();
		var requiredNails = amount;
		for (val nail : usableNailsList) {
			if ((requiredNails -= inventory.getAmountOf(nail)) <= 0) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("SameParameterValue")
	private final void removeNails(@NotNull final Player player, final int amount) {
		val inventory = player.getInventory();
		var requiredNails = amount;
		for (val nail : usableNailsList) {
			if ((requiredNails -= inventory.deleteItem(nail, requiredNails).getSucceededAmount()) <= 0) {
				return;
			}
		}
	}

	
}
