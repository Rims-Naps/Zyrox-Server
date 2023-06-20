package com.zenyte.game.content.skills.slayer;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Kris | 5. aug 2018 : 23:40:04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum BossTask implements SlayerTask {

	KREE_ARRA(357, p -> p.getSkills().getLevelForXp(Skills.RANGED) >= 70, "Kree'arra", false),
	KRIL_TSUTSAROTH(350.5F, p -> p.getSkills().getLevelForXp(Skills.HITPOINTS) >= 70, "K'ril Tsutsaroth", false),
	COMMANDER_ZILYANA(350, p -> p.getSkills().getLevelForXp(Skills.AGILITY) >= 70, "Commander Zilyana", false),
	GENERAL_GRAARDOR(338, p -> p.getSkills().getLevelForXp(Skills.STRENGTH) >= 70, "General Graardor", false),
	DAGANNOTH_KINGS(331.5F, p -> true, "Dagannoth Kings", false) {
		@Override
		public boolean validate(@NonNull final String name, final NPC npc) {
			return name.equalsIgnoreCase("Dagannoth Rex") || name.equalsIgnoreCase("Dagannoth Prime") || name.equalsIgnoreCase("Dagannoth Supreme");
		}

		@Override
		public float getExperience(final NPC npc) {
			return npc.getDefinitions().getName().equalsIgnoreCase("Dagannoth Supreme") ? 255 : 331.5F;
		}
	},
	/*DAGANNOTH_SUPREME(255, p -> true, "Dagannoth Supreme", false),
	DAGANNOTH_REX(331.5F, p -> true, "Dagannoth Rex", false),
	DAGANNOTH_PRIME(331.5F, p -> true, "Dagannoth Prime", false),*/
	KING_BLACK_DRAGON(258, p -> true, "King Black Dragon", false),
	VORKATH(750, p -> true, "Vorkath", false),
	VETION(312, p -> true, "Vet'ion reborn", true),
	CRAZY_ARCHEAOLOGIST(275, p -> true, "Crazy archaeologist", true),
	ZULRAH(500, p -> true, "Zulrah", false),
	CERBERUS(690, p -> p.getSkills().getLevelForXp(Skills.SLAYER) >= 91, "Cerberus", false),
	GIANT_MOLE(215, p -> true, "Giant Mole", false),
	CALLISTO(312, p -> true, "Callisto", true),
	CHAOS_ELEMENTAL(250, p -> true, "Chaos elemental", true),
	SCORPIA(260, p -> true, "Scorpia", true),
	KRAKEN(255, p -> p.getSkills().getLevelForXp(Skills.SLAYER) >= 87, "Kraken", false),
    ABYSSAL_SIRE(450, p -> false /* TODO: Implement sire; p.getSkills().getLevelForXp(Skills.SLAYER) >= 85 */, "Abyssal sire", false),
	KALPHITE_QUEEN(535.5F, p -> true, "Kalphite Queen", false),
	VENENATIS(388.8F, p -> true, "Venenatis", true),
	CHAOS_FANATIC(253, p -> true, "Chaos Fanatic", true),
	THERMONUCLEAR_SMOKE_DEVIL(240, p -> p.getSkills().getLevelForXp(Skills.SLAYER) >= 93, "Thermonuclear smoke devil", false),
	GROTESQUE_GUARDIANS(1350, p -> p.getSkills().getLevelForXp(Skills.SLAYER) >= 75 && p.getNumericAttribute("brittle-entrance_unlocked").intValue() == 1, "Grotesque Guardians", false) {
		@Override
		public boolean validate(@NonNull final String name, final NPC npc) {
			return name.equals("Dusk");
		}
	},
	BARROWS(255, p -> true, "Barrows Brothers", false) {
		@Override
		public boolean validate(@NonNull final String name, final NPC npc) {
			return name.equals("Ahrim the Blighted") || name.equals("Dharok the Wretched")
					|| name.equals("Guthan the Infested") || name.equals("Karil the Tainted")
					|| name.equals("Torag the Corrupted") || name.equals("Verac the Defiled");
		}
	},

	//The below monsters can never be acquired as a boss task, their only purpose is for experience amplification when slaying(e.g. Skotizo as a demons task giving more experience).
	SKOTIZO(618.5F, p -> false, "Skotizo", false),
	;
	
	BossTask(final float xp, final Predicate<Player> predicate, final String name, final boolean assignableByKrystilia) {
		this.xp = xp;
		this.predicate = predicate;
		monsters = new String[] { name.toLowerCase() };
		this.name = name;
		this.assignableByKrystilia = assignableByKrystilia;
	}
	
	@Getter private float xp;
	@Getter private final Predicate<Player> predicate;
	private final String name;
	@Getter private final String[] monsters;
	@Getter private final boolean assignableByKrystilia;
	
	public static final BossTask[] VALUES = values();
	public static final Map<String, BossTask> MAPPED_VALUES = new HashMap<String, BossTask>(VALUES.length);

	static {
		for (val value : VALUES) {
			MAPPED_VALUES.put(value.name.toLowerCase(), value);
		}
	}
	
	@Override
	public boolean validate(@NonNull final String name, final NPC npc) {
        if (ArrayUtils.contains(this.getMonsterIds(), npc.getId())) {
            return true;
        }
		return name.equalsIgnoreCase(this.name);
	}

	@Override
	public float getExperience(final NPC npc) {
		return xp;
	}

	@Override
	public int getTaskId() {
		return 98;
	}

	public int[] getMonsterIds() {
	    return new int[0];
    }

    @Override
    public int getSlayerRequirement() {
        return 0;
    }

    @Override
	public String getTip() {
		return "Not available.";
	}
	
	@Override
	public String toString() {
		return Utils.formatString(name);
	}

	@Override
	public String getTaskName() {
		return name;
	}

	@Override
	public String getEnumName() {
		return name();
	}
	
}
