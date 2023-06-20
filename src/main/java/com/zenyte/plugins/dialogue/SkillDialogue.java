package com.zenyte.plugins.dialogue;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.Getter;

/**
 * @author Kris | 22. okt 2017 : 15:52.26
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
//TODO: Refactor to use MakeType enum for baseops!
public abstract class SkillDialogue extends Dialogue {

	private static final String QUESTION = "What would you like to make?";
	
	public SkillDialogue(final Player player, final Item... items) {
		super(player);
		this.question = QUESTION;
		this.items = items;
	}
	
	public SkillDialogue(Player player, final String question, final Item... items) {
		super(player);
		this.question = question;
		this.items = items;
	}
	
	private final String question;
	@Getter
	protected final Item[] items;
	
	public int getMaximumAmount() {
		return 28;
	}

	public MakeType type() {
		return MakeType.MAKE;
	}
	
	public abstract void run(final int slotId, final int amount);
	
	@Override
	public final void communicateNext(final int slotId, final int componentId) {
		player.getDialogueManager().finish();
		run(slotId, componentId);
		player.getVarManager().sendBit(5983, 0);
		player.getTemporaryAttributes().put("lastSkillDialogueAmount", componentId);
	}
	
	@Override
	public void buildDialogue() {
		skill(getMaximumAmount(), type(), question, items);
	}

}
