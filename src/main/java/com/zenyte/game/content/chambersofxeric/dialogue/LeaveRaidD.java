package com.zenyte.game.content.chambersofxeric.dialogue;

import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.greatolm.OlmRoom;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableBoolean;

/**
 * @author Kris | 16. nov 2017 : 3:28.30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class LeaveRaidD extends Dialogue {

	public LeaveRaidD(Player player, final Raid raid) {
		super(player);
		this.raid = raid;
	}
	
	private final Raid raid;

	@Override
	public void buildDialogue() {
	    val bool = new MutableBoolean();
	    val fullChest = new MutableBoolean();
        raid.ifInRoom(player.getLocation(), OlmRoom.class, room -> {
        	bool.setTrue();
        	val rewards = raid.getRewards();
        	if (rewards != null) {
        		val map = rewards.getRewardMap();
        		if (map != null) {
        			val container = map.get(player);
        			if (container != null) {
        				if (!container.isEmpty()) {
        					fullChest.setTrue();
						}
					}
				}
			}
		});
		options(fullChest.isTrue() ? "You have stuff in the chest.<br>Are you sure you want to abandon the raid?" : "You will not be able to rejoin this raid again.",
				new DialogueOption("Leave the raid.", () -> raid.leaveRaid(player, false, bool.isTrue())),
				new DialogueOption("Stay."));
	}

}
