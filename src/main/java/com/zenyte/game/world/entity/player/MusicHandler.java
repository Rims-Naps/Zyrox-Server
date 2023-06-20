package com.zenyte.game.world.entity.player;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.music.Music;
import com.zenyte.game.music.MusicLoader;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import mgi.types.config.enums.Enums;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

/**
 * @author Kris | 21. veebr 2018 : 4:27.50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class MusicHandler {

	public static final int[] VARP_IDS = new int[] { 20, 21, 22, 23, 24, 25, 298, 311, 346, 414, 464, 598, 662, 721, 906, 1009, 1338,
			1681, 2065 };

	MusicHandler(final Player player) {
		this.player = player;
		unlockedTracks = new Int2IntOpenHashMap(VARP_IDS.length);
	}

	/** The given player who owns this handler. */
	private final transient Player player;
	
	/** An int2int map containing the varps and their values. */
	@Getter private final Int2IntOpenHashMap unlockedTracks;
	
	/** The music track that's currently playing. */
	private transient Music currentlyPlaying;
	
	/** The amount of ticks the current song has played for, and the amount of ticks at which this song ends. */
	private transient int ticks, nextSongAtTicks;
	
	/** Whether the music player is currently stopped or not. */
	@Getter @Setter private transient boolean stopped;

	/**
	 * Refreshes the varps to portray on the music tab interface.
	 */
    void refreshListConfigs() {
		val manager = player.getVarManager();
		val reset = isDefaultsReset();
		if (!reset) {
		    setDefaultsReset();
        }
		for (int i = VARP_IDS.length - 1; i >= 0; i--) {
			val varp = VARP_IDS[i];
			if (!reset) {
			    unlockedTracks.put(varp, unlockedTracks.get(varp) - (unlockedTracks.get(varp) & MusicLoader.getExcludedVarpValue(i)));
            }
			val value = unlockedTracks.get(varp) | MusicLoader.DEFAULT_VARP_VALUES[i];
			manager.sendVar(varp, value);
		}
		playRandomTrack();
	}

	public void stop() {
        setStopped(true);
        player.getPacketDispatcher().sendMusic(-1);
    }

	private boolean isDefaultsReset() {
        return player.getBooleanAttribute("reset default music tracks");
    }

    private void setDefaultsReset() {
        player.addAttribute("reset default music tracks", 1);
    }

	/** Restarts the currently playing track again(after setting music volume from 0 to > 0) */
	public void restartCurrent() {
		val music = currentlyPlaying;
		if (music == null) {
			return;
		}
		if (stopped)
		    stopped = false;
		nextSongAtTicks = music.getDuration();
		currentlyPlaying = music;
		ticks = 0;
	}

	/** Plays a random track. */
	private void playRandomTrack() {
        val list = World.getRegion(player.getLocation().getRegionId()).getMusicTracks();
        if (list == null || list.isEmpty()) {
            return;
        }
        val randomTrack = Utils.getRandomCollectionElement(list);
        if (randomTrack == null || randomTrack == currentlyPlaying) {
            return;
        }
        val slot = Enums.MUSIC_SLOT_NAME_ENUM.getKey(randomTrack.getName());
        play(slot.orElseThrow(RuntimeException::new));
	}

	/** Unlocks all the music tracks associated with the given region id. */
	public void unlock(final int regionId) {
		val list = World.getRegion(regionId).getMusicTracks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (val music : list) {
			unlock(music, false);
		}
		if (!player.getBooleanSetting(Setting.AUTO_MUSIC)) {
			return;
		}
		playRandomTrack();
	}

	public void unlock(final Music music) {
	    unlock(music, true);
    }

	public void unlock(final Music music, final boolean play) {
        val optionalSlot = Enums.MUSIC_SLOT_NAME_ENUM.getKey(music.getName());
        if (!optionalSlot.isPresent())
            return;
        val slot = optionalSlot.getAsInt();
        val optionalMusicIndex = Enums.MUSIC_SLOT_INTERFACE_ENUM.getValue(slot);
        if (!optionalMusicIndex.isPresent()) {
            return;
        }
        val musicIndex = optionalMusicIndex.getAsInt();
        val index = (musicIndex >> 14 & 0x3FFF) - 1;
        if (index >= VARP_IDS.length) {
            return;
        }

        val varp = VARP_IDS[index];
        val value = player.getVarManager().getValue(varp) | (1 << (musicIndex & 0x3FFF));
        if (!isUnlocked(slot)) {
            unlockedTracks.put(varp, value - (value & MusicLoader.DEFAULT_VARP_VALUES[index]));
            player.getVarManager().sendVar(varp, value);
            val unlocked = player.getEmotesHandler().isUnlocked(Emote.AIR_GUITAR);
            if (!unlocked && unlockedMusicCount() >= 500) {
                player.getEmotesHandler().unlock(Emote.AIR_GUITAR);
                player.sendMessage(Colour.RS_GREEN.wrap("Congratulations, you've unlocked the Air Guitar emote!"));
            }
        }
        if (play) {
            if (currentlyPlaying == music) {
                return;
            }
            play(slot);
        }
    }

	public final int unlockedMusicCount() {
	    int count = 0;
	    val varManager = player.getVarManager();
	    for (val varp : VARP_IDS) {
	        count += Integer.bitCount(varManager.getValue(varp));
        }
	    return count;
    }

	/** Processes the music player. */
    void processMusicPlayer() {
		if (stopped) {
			return;
		}
		if (++ticks >= nextSongAtTicks) {
			resetCurrent();
			ticks = 0;
			if (!player.getBooleanSetting(Setting.LOOP_MUSIC)) {
				if (player.getBooleanSetting(Setting.AUTO_MUSIC)) {
					playRandomTrack();
				} else {
					stopped = true;
				}
			} else {
				val current = currentlyPlaying;
				currentlyPlaying = null;
				if (current != null) {
					val slot = Enums.MUSIC_SLOT_NAME_ENUM.getKey(current.getName());
					play(slot.orElseThrow(RuntimeException::new));
				}
			}
		}
	}

	public void playJingle(final int jingle) {
        this.ticks = 0;
        player.getPacketDispatcher().playJingle(jingle);
    }

	/** Sends the hint for the music track at the given slot. */
	public void sendUnlockHint(final int slotId) {
		val musicName = Enums.MUSIC_SLOT_NAME_ENUM.getValue(slotId);
		val music = Music.map.get(musicName.orElseThrow(RuntimeException::new));
		if (music == null) {
			return;
		}
		player.sendMessage(
				(!isUnlocked(slotId) ? "This track unlocks " : "This track was unlocked ") + music.getHint().replace("unlocked ", ""));
	}

	/** Resets the current music by stopping it client-sided. */
	private void resetCurrent() {
		player.getPacketDispatcher().sendMusic(-1);
	}

	/** Whether the track at the given slot is unlocked or not. */
	private boolean isUnlocked(final int slot) {
		val optionalRandomSong = Enums.MUSIC_SLOT_INTERFACE_ENUM.getValue(slot);
		if (!optionalRandomSong.isPresent()) {
			return false;
		}
		val randomSong = optionalRandomSong.getAsInt();
		if (randomSong == -1) {
			return true;
		}
		val index = (randomSong >> 14 & 0x3FFF) - 1;
		if (index >= VARP_IDS.length) {
			return false;
		}

		val value = unlockedTracks.get(VARP_IDS[index]) | MusicLoader.DEFAULT_VARP_VALUES[index];
		val bitIndex = randomSong & 0x3FFF;
		return (value >> bitIndex & 0x1) == 1;
	}

	/** Attempts to play the track at the requested slot. If it's not unlocked, or an error is thrown, returns as false and stops. */
	public boolean play(final int slot) {
		if (!isUnlocked(slot)) {
			return false;
		}
		val musicName = Enums.MUSIC_SLOT_NAME_ENUM.getValue(slot);
		val music = Music.map.get(musicName.orElseThrow(RuntimeException::new));
		if (music == null) {
			return false;
		}
		if (currentlyPlaying == music) {
			resetCurrent();
		}
		nextSongAtTicks = music.getDuration();
		ticks = 0;
		currentlyPlaying = music;
		stopped = false;
		player.getPacketDispatcher().sendMusic(music.getMusicId());
        if (GameInterface.MUSIC_TAB.getPlugin().isPresent())
		player.getPacketDispatcher().sendComponentText(GameInterface.MUSIC_TAB,
                GameInterface.MUSIC_TAB.getPlugin().get().getComponent("Song name"), music.getName());
		TreasureTrail.playSong(player, music.getName());
		return true;
	}

}
