package com.zenyte.game.content.achievementdiary;

import lombok.Getter;

public final class DiaryChunk {
	
	public DiaryChunk(final int varbit, final int size, final int greenVarbit) {
		this.varbit = varbit;
		this.size = size;
		this.greenVarbit = greenVarbit;
	}

	@Getter
	private final int varbit;
	@Getter
	private final int size;
	@Getter
	private final int greenVarbit;

}