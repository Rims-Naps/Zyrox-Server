package com.zenyte.game.content.chambersofxeric.dialogue;

import com.zenyte.game.content.chambersofxeric.map.RaidRoom;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import lombok.val;

import java.util.Optional;

/**
 * @author Kris | 11. jaan 2018 : 4:27.11
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class RaidFloorOverviewD extends OptionsMenuD {

	private RaidFloorOverviewD(final Player player, final String title, final String[] options) {
		super(player, title, options);
	}

	@Override
	public void handleClick(final int slotId) {
	    val optionalRaid = player.getRaid();
		if (!optionalRaid.isPresent()) {
			player.sendMessage("You need to be in a raid to view the floors.");
			return;
		}
		val raid = optionalRaid.get();
		val map = raid.getMap();
		if (slotId < map.getRaidChunks().size()) {
			val chunk = map.getRaidChunks().get(slotId);
			val tile = new Location((chunk.getChunkX() * 8) + 16, (chunk.getChunkY() * 8) + 16, chunk.getToPlane());
			val square = Utils.findEmptySquare(tile, 15, 1, Optional.empty());
			player.setLocation(square.orElse(tile));
		} else {
			player.setLocation(map.getBoss().getEntrance());
		}
		player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
	}
	
	public static final void open(final Player player) {
        val optionalRaid = player.getRaid();
        if (!optionalRaid.isPresent()) {
            player.sendMessage("You need to be in a raid to view the floors.");
            return;
        }
        val raid = optionalRaid.get();
        val map = raid.getMap();
		val options = new String[map.getRaidChunks().size() + 1];
		for (int i = 0; i < map.getRaidChunks().size(); i++) {
			val chunk =  map.getRaidChunks().get(i);
			val data = getRoomData(chunk.getStaticChunkY(), chunk.getFromPlane());
            assert data != null;
            val string = Utils.formatString(data.toString().toLowerCase().replace("_", " "));
			val prefix = chunk.getToPlane() == 3 ? "Top floor" : chunk.getToPlane() == 2 ? "Middle floor" : "Bottom floor";
			options[i] = prefix + " - " + string + (string.endsWith("room") ? "" : " room");
		}
		options[map.getRaidChunks().size()] = "The Great Olm";
		player.getDialogueManager().start(new RaidFloorOverviewD(player, "Raid floors", options));
	}
	
	private static final RaidRoom getRoomData(final int ry, final int plane) {
	    return Utils.findMatching(RaidRoom.values, room -> room.getStaticChunkY() == ry && room.getHeight() == plane);
	}
	
	@Override
	public boolean cancelOption() {
		return true;
	}

}
