package com.zenyte.game.content.boss.smokedevil;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Tommeh | 21 aug. 2018 | 16:59:45
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Slf4j
public class ThermonuclearSmokeDevilEntrance implements ObjectAction {

	private static final Location outsideTile = new Location(2379, 9452, 0);
	private static final Location insideTile = new Location(2376, 9452, 0);

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
		if (object.getId() == 535) {
			if (option.equals("Peek")) {
				player.sendMessage("You peek through the crevice...");
				WorldTasksManager.schedule(() -> {
					val players = GlobalAreaManager.get("Thermonuclear Boss Room").getPlayers();
					val playerCount = players.size();
					player.sendMessage("Standard cave: " + (playerCount == 0 ? "No adventurers." : playerCount + (playerCount == 1 ? " adventurer." : " adventurers.")));
				}, 2);
			} else if (option.equals("Private")) {
				player.getDialogueManager().start(new Dialogue(player) {
					@Override
					public void buildDialogue() {
						val price = 100_000;
						options("Enter a private Thermonuclear boss cave?", "Pay " + Utils.format(price) + " coins.", "Cancel.").onOptionOne(() -> {
							try {
								val amountInInventory = player.getInventory().getAmountOf(ItemId.COINS_995);
								val amountInBank = player.getBank().getAmountOf(ItemId.COINS_995);
								if ((long) amountInBank + amountInInventory < price) {
									setKey(100);
									return;
								}
								player.lock(10);
								player.getInventory().deleteItem(new Item(ItemId.COINS_995, price)).onFailure(remainder -> player.getBank().remove(remainder));
								val area = MapBuilder.findEmptyChunk(7, 5);
								val instance = new SmokeDevilInstance(player, area, 293, 1179);
								instance.constructRegion();
							} catch (Exception e) {
								log.error(Strings.EMPTY, e);
							}

						});
						plain(100, "You need at least " + Utils.format(price) + " coins to start a private Thermonuclear boss instance.");
					}
				});
			} else {
				player.teleport(insideTile);
			}
		} else {
			player.teleport(outsideTile);
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 535, 536 };
	}

}
