package com.zenyte.game.content.achievementdiary;

import com.zenyte.game.world.entity.player.Player;

import lombok.Getter;

/**
 * @author Kris | 15. apr 2018 : 0:44.33
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum AchievementDiaryTab {

	KARAMJA(-1, 3576, new DiaryChunk(2423, 10, 3577), new DiaryChunk(6288, 19, 3598), new DiaryChunk(6289, 10, 3610), new DiaryChunk(6290, 5, 4567)),
	ARDOUGNE(4458, 4448, new DiaryChunk(6291, 10, 4499), new DiaryChunk(6292, 12, 4500), new DiaryChunk(6293, 12, 4501), new DiaryChunk(6294, 8, 4502)),
	FALADOR(4462, 4449, new DiaryChunk(6299, 11, 4503), new DiaryChunk(6300, 14, 4504), new DiaryChunk(6301, 11, 4505), new DiaryChunk(6302, 6, 4506)),
	FREMENNIK(4491, 4450, new DiaryChunk(6303, 10, 4531), new DiaryChunk(6304, 9, 4532), new DiaryChunk(6305, 9, 4533), new DiaryChunk(6306, 6, 4534)),
	KANDARIN(4475, 4451, new DiaryChunk(6307, 11, 4515), new DiaryChunk(6308, 14, 4516), new DiaryChunk(6309, 11, 4517), new DiaryChunk(6310, 7, 4518)),
	DESERT(4483, 4452, new DiaryChunk(6295, 11, 4523), new DiaryChunk(6296, 12, 4524), new DiaryChunk(6297, 10, 4525), new DiaryChunk(6298, 6, 4526)),
	LUMBRIDGE_AND_DRAYNOR(4495, 4453, new DiaryChunk(6311, 12, 4535), new DiaryChunk(6312, 12, 4536), new DiaryChunk(6313, 11, 4537), new DiaryChunk(6314, 6, 4538)),
	MORYTANIA(4487, 4454, new DiaryChunk(6315, 11, 4527), new DiaryChunk(6316, 11, 4528), new DiaryChunk(6317, 10, 4529), new DiaryChunk(6318, 6, 4530)),
	VARROCK(4479, 4455, new DiaryChunk(6319, 14, 4519), new DiaryChunk(6320, 13, 4520), new DiaryChunk(6321, 10, 4521), new DiaryChunk(6322, 5, 4522)),
	WILDERNESS(4466, 4457, new DiaryChunk(6323, 12, 4507), new DiaryChunk(6324, 11, 4508), new DiaryChunk(6325, 10, 4509), new DiaryChunk(6326, 7, 4510)),
	WESTERN_PROVINCES(4471, 4456, new DiaryChunk(6327, 11, 4511), new DiaryChunk(6328, 13, 4512), new DiaryChunk(6329, 13, 4513), new DiaryChunk(6330, 7, 4514));
	
	@Getter private final int tabGreenVarbit, tabYellowVarbit;
	@Getter private DiaryChunk[] diaries;
	
	public static final AchievementDiaryTab[] VALUES = values();
	
	private AchievementDiaryTab(final int tabGreenVarbit, final int tabYellowVarbit, final DiaryChunk... diaries) {
		this.tabGreenVarbit = tabGreenVarbit;
		this.tabYellowVarbit = tabYellowVarbit;
		this.diaries = diaries;
	}
	
	public static final void unlockAll(final Player p) {
		for (final AchievementDiaryTab v : AchievementDiaryTab.VALUES) {
			
			for (final DiaryChunk chunk : v.getDiaries()) {
				p.getVarManager().sendBit(chunk.getGreenVarbit(), 1);//if this difficulty tab is 100% complete, set it green.
				p.getVarManager().sendBit(chunk.getVarbit(), chunk.getSize());//number of tasks complete in this difficulty tab.
			}
			//if all tasks are complete, we mark the whole text green:
			if (true) {//if green
			if (v == AchievementDiaryTab.KARAMJA) {
				p.getVarManager().sendBit(3578, 2);
				p.getVarManager().sendBit(3599, 2);
				p.getVarManager().sendBit(3611, 2);
				p.getVarManager().sendBit(4566, 1);
			} else {
				for (int i = 0; i < 4; i++)
					p.getVarManager().sendBit(v.tabGreenVarbit + i, 1);
			}
			} else if (false) {//if yellow.
				p.getVarManager().sendBit(v.tabYellowVarbit, 1);
			}
			
		}
	}
	
}
