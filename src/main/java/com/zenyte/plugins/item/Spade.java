package com.zenyte.plugins.item;

import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 25. aug 2018 : 22:48:45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class Spade extends ItemPlugin {

	private static final int BRINE_RATS_DIG_SPOT_HASH = new Location(2748, 3733, 0).getPositionHash();
	private static final Animation DIG_ANIM = new Animation(830);
	private static final SoundEffect soundEffect = new SoundEffect(1470);

	private static final int[] GIANT_MOLE_HOLE_HASHES = {
			new Location(2984, 3387, 0).getPositionHash(),
			new Location(2987, 3387, 0).getPositionHash(),
			new Location(2989, 3378, 0).getPositionHash(),
			new Location(2996, 3377, 0).getPositionHash(),
			new Location(2999, 3375, 0).getPositionHash(),
			new Location(3005, 3376, 0).getPositionHash(),
	};

	private static final int[] CLUE_HUNTER_ITEM_HASHES = {
			new Location(2579, 3378, 0).getPositionHash(), //gloves and boots
			new Location(1595, 3627, 0).getPositionHash(),
			new Location(2819, 3126, 0).getPositionHash(),
			new Location(2614, 3065, 0).getPositionHash(),
			new Location(2590, 3231, 0).getPositionHash()
	};

	@Override
	public void handle() {
		bind("Dig", (player, item, slotId) -> {
			player.resetWalkSteps();
			player.setAnimation(DIG_ANIM);
			player.sendSound(soundEffect);
			WorldTasksManager.schedule(() -> {
				/*
				for(int i = 0; i < EasterBunny2022.DIG_SPOTS.length; i++) {
					if(player.getLocation().equals(Santa2022.DIG_SPOTS[i])) {
						switch(i) {
							case 0:
								if(!player.containsItem(6542)) {
									if(player.getInventory().addItem(new Item(6542, 1)).getResult() == RequestResult.SUCCESS) {
										player.sendMessage("you manage to find one of the four presents!");
										return;
									} else {
										player.sendMessage("You don't find anything...");
										return;
									}
								}
								break;
							case 1:
								if(!player.containsItem(13346)) {
									if(player.getInventory().addItem(new Item(13346, 1)).getResult() == RequestResult.SUCCESS) {
										player.sendMessage("you manage to find one of the four presents!");
										return;
									} else {
										player.sendMessage("You don't find anything...");
										return;
									}
								}
								break;
							case 2:
								if(!player.containsItem(13656)) {
									if(player.getInventory().addItem(new Item(13656, 1)).getResult() == RequestResult.SUCCESS) {
										player.sendMessage("you manage to find one of the four presents!");
										return;
									} else {
										player.sendMessage("You don't find anything...");
										return;
									}
								}
								break;
							case 3:
								if(!player.containsItem(29025)) {
									if(player.getInventory().addItem(new Item(29025, 1)).getResult() == RequestResult.SUCCESS) {
										player.sendMessage("you manage to find one of the four presents!");
										return;
									} else {
										player.sendMessage("You don't find anything...");
										return;
									}
								}
								break;
							case 4:
								if(!player.containsItem(29027)) {
									if(player.getInventory().addItem(new Item(29027, 1)).getResult() == RequestResult.SUCCESS) {
										player.sendMessage("you manage to find one of the four presents!");
										return;
									} else {
										player.sendMessage("You don't find anything...");
										return;
									}
								}
								break;
						}
					}
				}
				 */
				if (TreasureTrail.dig(player)) {
					return;
				}
				val mound = player.getBarrows().getMound();
				if (mound.isPresent()) {
					player.getBarrows().enter(mound.get());
					return;
				}
				if (player.getLocation().getPositionHash() == BRINE_RATS_DIG_SPOT_HASH) {
					player.setLocation(new Location(2696, 10118, 0));
					player.sendMessage("You fall down into a cavern.");
					return;
				}
				if (ArrayUtils.contains(GIANT_MOLE_HOLE_HASHES, player.getLocation().getPositionHash())) {
					player.setLocation(new Location(1752, 5237, 0));
					return;
				}
				if(ArrayUtils.contains(CLUE_HUNTER_ITEM_HASHES, player.getLocation().getPositionHash())) {
					int spaces = 1;
					int[] sprites = {};
					if(player.getLocation().getPositionHash() == CLUE_HUNTER_ITEM_HASHES[0]) { // Gloves/boots
						if(player.getInventory().hasFreeSlots()) {
							int freeSpots = player.getInventory().getFreeSlots();
							spaces--;
							if(!player.containsItem(ItemId.CLUE_HUNTER_BOOTS)) {
								freeSpots--;
								player.getInventory().addItem(new Item(ItemId.CLUE_HUNTER_BOOTS));
								sprites = ArrayUtils.add(sprites, ItemId.CLUE_HUNTER_BOOTS);
							}
							if(!player.containsItem(ItemId.CLUE_HUNTER_GLOVES)) {
								spaces--;
								if(freeSpots > 0) {
									player.getInventory().addItem(new Item(ItemId.CLUE_HUNTER_GLOVES));
								} else {
									World.spawnFloorItem(new Item(ItemId.CLUE_HUNTER_GLOVES), player, 500, 0);
								}
								sprites = ArrayUtils.add(sprites, ItemId.CLUE_HUNTER_GLOVES);
							}
						} else {
							spaces = 2;
						}
					} else if(player.getLocation().getPositionHash() == CLUE_HUNTER_ITEM_HASHES[1]) {// Garb
						spaces--;
						if(player.getInventory().hasFreeSlots()) {
							if(!player.containsItem(ItemId.CLUE_HUNTER_GARB)) {
								player.getInventory().addItem(new Item(ItemId.CLUE_HUNTER_GARB));
								sprites = ArrayUtils.add(sprites, ItemId.CLUE_HUNTER_GARB);
							}
						}
					} else if(player.getLocation().getPositionHash() == CLUE_HUNTER_ITEM_HASHES[2]) {// Trousers
						spaces--;
						if(player.getInventory().hasFreeSlots()) {
							if(!player.containsItem(ItemId.CLUE_HUNTER_TROUSERS)) {
								player.getInventory().addItem(new Item(ItemId.CLUE_HUNTER_TROUSERS));
								sprites = ArrayUtils.add(sprites, ItemId.CLUE_HUNTER_TROUSERS);
							}
						}
					} else if(player.getLocation().getPositionHash() == CLUE_HUNTER_ITEM_HASHES[3]) {// Cloak
						spaces--;
						if(player.getInventory().hasFreeSlots()) {
							if(!player.containsItem(ItemId.CLUE_HUNTER_CLOAK)) {
								player.getInventory().addItem(new Item(ItemId.CLUE_HUNTER_CLOAK));
								sprites = ArrayUtils.add(sprites, ItemId.CLUE_HUNTER_CLOAK);
							}
						}
					} else if(player.getLocation().getPositionHash() == CLUE_HUNTER_ITEM_HASHES[4]) {// Helm
						spaces--;
						if(player.getInventory().hasFreeSlots()) {
							if(!player.containsItem(ItemId.HELM_OF_RAEDWALD)) {
								player.getInventory().addItem(new Item(ItemId.HELM_OF_RAEDWALD));
								sprites = ArrayUtils.add(sprites, ItemId.HELM_OF_RAEDWALD);
							}
						}
					}

					if (spaces > 0) {
						player.sendMessage("You find " + spaces + " piece" + (spaces > 1 ? "s " : " ") + "of gear in the ground but don't have enough space to take them.");
						return;
					} else if (sprites.length != 0){
						int[] finalSprites = sprites;
						player.getDialogueManager().start(new Dialogue(player) {
							@Override
							public void buildDialogue() {
								if(finalSprites.length == 2) {
									doubleItem(finalSprites[0], finalSprites[1], "You dig with your spade and find some gear hidden in the ground.");
								} else {
									item(finalSprites[0], "You dig with your spade and find some gear hidden in the ground.");
								}
							}
						});
						return;
					}
				}
				player.sendMessage("Nothing interesting happens.");
			});
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 952 };
	}

}
