package com.zenyte.plugins.object;

import com.zenyte.game.content.boss.kraken.KrakenInstance;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Tommeh | 23 mei 2018 | 01:06:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@Slf4j
public class KrakenBossEntrance implements ObjectAction {

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
		if (object.getId() == 537) {
			if (optionId == 1) {
				player.setLocation(KrakenInstance.INSIDE_TILE);
			} else if (optionId == 2) {
				val price = new Item(995,25_000);
				if (!player.getBank().containsItem(price) && !player.getInventory().containsItem(price)) {
					player.getDialogueManager().start(new PlainChat(player, "You don't have enough gold in both your inventory and bank to start this instance."));
					return;
				}
				player.getDialogueManager().start(new Dialogue(player) {

					@Override
					public void buildDialogue() {
						options("Enter a private Kraken boss cave?", "Pay " + Utils.format(price.getAmount()) + " coins.", "Cancel.").onOptionOne(() -> {

						    try {
						        val area = MapBuilder.findEmptyChunk(8, 8);
						        val instance = new KrakenInstance(player, area, 282, 1250);
						        instance.constructRegion();
						        if (player.getInventory().containsItem(price)) {
                                    player.getInventory().deleteItem(price);
                                } else if (player.getBank().containsItem(price)) {
									player.getBank().remove(price);
								}
                            } catch (Exception e) {
                                log.error(Strings.EMPTY, e);
                            }

						});
					}

				});
			} else if (optionId == 3) {
				player.sendMessage("You peek through the crevice...");
				WorldTasksManager.schedule(() -> {
                    val players = GlobalAreaManager.get("Kraken Boss Room").getPlayers();
                    val playerCount = players.size();
					player.sendMessage("Standard cave: " + (playerCount == 0 ? "No adventurers." : playerCount + (playerCount == 1 ? " adventurer." : " adventurers.")));
				}, 2);
			}
		} else {
			player.setLocation(KrakenInstance.OUTSIDE_TILE);
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 537, 538 };
	}

}
