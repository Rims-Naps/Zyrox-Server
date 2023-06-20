package com.zenyte.game.music;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kris | 27. juuli 2018 : 21:28:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Music {

	public static final Map<String, Music> map = new HashMap<String, Music>(600);
	public static final Map<String, Music> lowercaseMap = new HashMap<>(600);

	public static final Music get(@NotNull final String name) {
	    val music = map.get(name);
	    if (music == null) {
	        throw new IllegalStateException("Music track '" + name + "' does not exist.");
        }
	    return music;
    }
	
	@Getter @Setter private String name, hint;
	@Getter @Setter private int musicId, duration;
	@Getter @Setter private List<Integer> regionIds;
	@Getter @Setter private boolean defaultLocked;
	
}
