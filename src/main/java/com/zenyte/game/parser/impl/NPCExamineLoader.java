package com.zenyte.game.parser.impl;

import com.zenyte.game.parser.Parse;
import com.zenyte.game.util.Examine;
import com.zenyte.game.util.LabelledExamine;
import com.zenyte.game.world.World;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.FileReader;

@Slf4j
public class NPCExamineLoader implements Parse {

	public static final Int2ObjectOpenHashMap<Examine> DEFINITIONS = new Int2ObjectOpenHashMap<Examine>();

	@Override
	public void parse() throws Throwable {
		final BufferedReader br = new BufferedReader(new FileReader("data/examines/NPC examines.json"));
		final Examine[] examines = World.getGson().fromJson(br, Examine[].class);
		for (final Examine def : examines) {
			if (def != null) {
				DEFINITIONS.put(def.getId(), def);
			}
		}
		parseOverrides();
	}

	private void parseOverrides() throws Throwable {
		final BufferedReader br = new BufferedReader(new FileReader("data/examines/Forced npc examines.json"));
		final LabelledExamine[] examines = World.getGson().fromJson(br, LabelledExamine[].class);
		for (final LabelledExamine def : examines) {
			DEFINITIONS.put(def.getId(), def);
		}
	}
	
	public static final void loadExamines() {
		try {
			new NPCExamineLoader().parse();
		} catch (final Throwable e) {
            log.error(Strings.EMPTY, e);
		}
	}

	public static Examine get(final int npcId) {
		return DEFINITIONS.get(npcId);
	}
}
