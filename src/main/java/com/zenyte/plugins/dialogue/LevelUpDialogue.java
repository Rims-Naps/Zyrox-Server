package com.zenyte.plugins.dialogue;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.LevelUpMessage;
import lombok.val;

public class LevelUpDialogue extends Dialogue {

	public LevelUpDialogue(final Player player, final int skill) {
		super(player);
		this.skill = skill;
		player.getTemporaryAttributes().put("skillDialogue", this);
	}
	
	private final int skill;

	@Override
	public void buildDialogue() {
		skill(skill);
	}
	
    private LevelUpMessage skill(final int skill) {
    	final LevelUpMessage msg = new LevelUpMessage(skill);
    	dialogue.put(dialogue.size() + 1, msg);
    	return msg;
    }
    
    @Override
    public void finish() {
    	super.finish();
    	player.getTemporaryAttributes().remove("skillDialogue");
    }
    
    public static final void sendSkillDialogue(final Player player, final int skill) {
    	val dialogue = player.getTemporaryAttributes().get("skillDialogue");
    	val skillDialogue = dialogue == null ? null : (LevelUpDialogue) dialogue;
    	if (skillDialogue == null) {
    		final LevelUpDialogue d = new LevelUpDialogue(player, skill);
    		player.getTemporaryAttributes().put("skillDialogue", d);
    		player.getDialogueManager().start(d);
    	} else {
			skillDialogue.skill(skill);
		}
    	player.getActionManager().interrupt(true);
    }

}
