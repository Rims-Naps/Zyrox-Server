package com.zenyte.plugins.dialogue;

import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 29. nov 2017 : 22:55.15
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>}
 */
public abstract class OptionsMenuD extends Dialogue {

	public OptionsMenuD(final Player player, final String title, final String... options) {
		super(player);
		this.title = title;
		this.options = options;
	}

	private final String title;
	@Getter
	private final String[] options;

	public abstract void handleClick(final int slotId);

	public abstract boolean cancelOption();

	public final void handleInterface(final int slotId) {
		player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
		player.getPacketDispatcher().sendClientScript(2158);
		if (slotId != options.length) {
			handleClick(slotId);
		}
	}

	@Override
	public final void buildDialogue() {
		player.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 187);
		player.setCloseInterfacesEvent(() -> player.getPacketDispatcher().sendClientScript(2158));
		player.getPacketDispatcher().sendComponentSettings(187, 3, 0, 127, AccessMask.CONTINUE);
		val builder = new StringBuilder();
		for (val string : options) {
			builder.append(string).append("|");
		}
		if (cancelOption()) {
			builder.append("Cancel|");
		}
		builder.delete(builder.lastIndexOf("|"), builder.lastIndexOf("|") + 1);
		player.getPacketDispatcher().sendClientScript(217, title, builder.toString(), 1);
	}

}
