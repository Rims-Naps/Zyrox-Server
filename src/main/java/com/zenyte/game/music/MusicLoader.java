package com.zenyte.game.music;

import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.MusicHandler;
import mgi.types.config.enums.EnumDefinitions;
import lombok.val;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 27. juuli 2018 : 23:42:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MusicLoader implements ScheduledExternalizable {

	public static final List<Integer> UNLOCKED_BY_DEFAULT = new ArrayList<Integer>(500);
	public static final int[] DEFAULT_VARP_VALUES = new int[MusicHandler.VARP_IDS.length];

	//Excluded varp values cus I accidentally scuffed it by placing default-unlocked tracks within unlocked map.
	private static final int[] excludedVarpValues = new int[] {
            51200, 536871936, 1210056705, 805306384, 1075840064, 201871616, -1073741543, -2035273693, 186794284, -703027092, 923736283, -1173808644, -1107383168, -193028019, 2130705371, -423099905, -678336849, 265464435, 3592
    };

	public static final int getExcludedVarpValue(final int index) {
	    if (index < 0 || index >= excludedVarpValues.length) {
	        return 0;
        }
	    return excludedVarpValues[index];
    }

	@Override
	public int writeInterval() {
		return 0;
	}

	@Override
	public void read(final BufferedReader reader) {
		val music = World.getGson().fromJson(reader, Music[].class);
		for (val track : music) {
			Music.map.put(track.getName(), track);
			Music.lowercaseMap.put(track.getName().toLowerCase(), track);
			val regions = track.getRegionIds();
			if (regions == null) {
				continue;
			}
			for (final int region : regions) {
				World.getRegion(region).getMusicTracks().add(track);
			}
		}

		val map = EnumDefinitions.get(819);
		val nameMap = EnumDefinitions.get(812);
		map.getValues().forEach((slot, grid) -> {
			if (!(grid instanceof Integer)) {
				return;
			}
			val name = nameMap.getStringValue(slot);
			if (name == null) {
				System.err.println("Failure to load song: " + slot);
				return;
			}
			val track = Music.map.get(name);
			if (track == null) {
				System.err.println("Failure to locate track for name: " + name);
				return;
			}

			if (track.getRegionIds() != null || track.isDefaultLocked()) {
				return;
			}
			if ((int) grid == -1) {
				return;
			}
			final int index = ((int) grid >> 14 & 0x3FFF) - 1;
			if (index >= DEFAULT_VARP_VALUES.length) {
				System.err.println("Failure to set music track: " + name + " as a default track!");
				return;
			}
			DEFAULT_VARP_VALUES[index] |= 1 << ((int) grid & 0x3FFF);
		});
	}

	@Override
	public void write() {

	}

	@Override
	public String path() {
		return "data/music.json";
	}

}
