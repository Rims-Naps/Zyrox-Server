package com.zenyte.game.content.quest;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.processor.Listener;
import com.zenyte.processor.Listener.ListenerType;

import lombok.val;

/**
 * Currently static class, just to unlock all the quests. Will be turned
 * into player-based quest manager once we start adding quests.
 * @author Kris | 23. veebr 2018 : 2:38.11
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class QuestManager {

	@Listener(type = ListenerType.LOBBY_CLOSE)
	protected final void unlock(final Player player) {
		val manager = player.getVarManager();
		manager.sendVar(Miniquest.ENTER_THE_ABYSS.getVarbit(), Miniquest.ENTER_THE_ABYSS.getFinishedValue());
		manager.sendVar(MembersQuest.THE_TOURIST_TRAP.getVarbit(), MembersQuest.THE_TOURIST_TRAP.getFinishedValue());
		manager.sendVar(MembersQuest.DEATH_PLATEAU.getVarbit(), MembersQuest.DEATH_PLATEAU.getFinishedValue());
		manager.sendBit(MembersQuest.SPIRITS_OF_THE_ELID.getVarbit(), MembersQuest.SPIRITS_OF_THE_ELID.getFinishedValue());
		manager.sendBit(MembersQuest.SWAN_SONG.getVarbit(), MembersQuest.SWAN_SONG.getFinishedValue());
		manager.sendBit(MembersQuest.BONE_VOYAGE.getVarbit(), MembersQuest.BONE_VOYAGE.getFinishedValue());
		manager.sendVar(MembersQuest.THE_GRAND_TREE.getVarbit(), MembersQuest.THE_GRAND_TREE.getFinishedValue());
		manager.sendVar(MembersQuest.TREE_GNOME_VILLAGE.getVarbit(), MembersQuest.TREE_GNOME_VILLAGE.getFinishedValue());
		val vars = player.getVarManager();
		
		for (final FreeQuest quest : FreeQuest.VALUES) {
			if (quest.isVarp()) {
				player.getVarManager().sendVar(quest.getVarbit(), quest.getFinishedValue());
			} else {
				player.getVarManager().sendBit(quest.getVarbit(), quest.getFinishedValue());
			}
		}
		for (final MembersQuest quest : MembersQuest.VALUES) {
			if (quest.isVarp()) {
				player.getVarManager().sendVar(quest.getVarbit(), quest.getFinishedValue());
			} else {
				val bitValue = vars.getBitValue(quest.getVarbit());
				if (bitValue < quest.getFinishedValue()) {
					vars.sendBit(quest.getVarbit(), quest.getFinishedValue());
				}
			}
		}
		for (final Miniquest quest : Miniquest.VALUES) {
			if (quest.isVarp()) {
				player.getVarManager().sendVar(quest.getVarbit(), quest.getFinishedValue());
			} else {
				player.getVarManager().sendBit(quest.getVarbit(), quest.getFinishedValue());
			}
		}
	}
	
}
