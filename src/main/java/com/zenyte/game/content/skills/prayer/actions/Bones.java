package com.zenyte.game.content.skills.prayer.actions;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.skills.prayer.ectofuntus.Bonecrusher;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.region.area.wilderness.LavaDragonIsle;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 9. nov 2017 : 17:38.00 || Kris | 10. veebr 2018 : 4:15.32
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum Bones {

	BONES(4.5, new Item(526), new Item(2530), new Item(3187)),
	BURNT_BONES(4.5, new Item(528)),
	BAT_BONES(5, new Item(530)),
	BIG_BONES(15, new Item(532)),
	BABYDRAGON_BONES(30, new Item(534)),
	DRAGON_BONES(72, new Item(536)),
	WOLF_BONES(4.5, new Item(2859)),
	SHAIKAHAN_BONES(25, new Item(3123)),
	JOGRE_BONES(15, new Item(3125)),
	BURNT_JOGRE_BONES(16, new Item(3127)),
	PASTY_JOGRE_BONES(17, new Item(3128), new Item(3129), new Item(3131), new Item(3132)),
	MARINATED_JOGRE_BONES(17, new Item(3130)),
	MONKEY_BONES(5, new Item(3179), new Item(3180), new Item(3181), new Item(3182), new Item(3183), new Item(3185), new Item(3186)),
	ZOGRE_BONES(22.5, new Item(4812)),
	FAYRG_BONES(84, new Item(4830)),
	RAURG_BONES(96, new Item(4832)),
	OURG_BONES(140, new Item(4834)),
	DAGANNOTH_BONES(125, new Item(6729)),
	WYVERN_BONES(72, new Item(6812)),
	LONG_BONE(15, new Item(10976)),
	CURVED_BONE(15, new Item(10977)),
	LAVA_DRAGON_BONES(85, new Item(11943)),
	SUPERIOR_DRAGON_BONES(150, new Item(22124)),
    WYRM_BONES(50, new Item(22780)),
    DRAKE_BONES(80, new Item(22783)),
    HYDRA_BONES(110, new Item(22786));

	@Getter
	private final Item[] bones;
	@Getter
	private final double xp;
	@Getter
	private final String name;

	Bones(final double xp, final Item... item) {
		this.xp = xp;
		this.bones = item;
		this.name = name().toLowerCase().replace("_", " ");
	}

	public static final Bones[] VALUES = values();
	private static final Map<Integer, Bones> BONES_MAP = new HashMap<Integer, Bones>(VALUES.length);
	private static final Animation BURY_ANIMATION = new Animation(827);

	static {
		for (Bones bones : VALUES) {
			for (Item bone : bones.bones)
				BONES_MAP.put(bone.getId(), bones);
		}
	}

	public static Bones getBone(int id) {
		return BONES_MAP.get(id);
	}

	public static final void bury(final Player player, final Bones bone, final Item item, final int slot) {
		if (player.isDead() || player.isFinished()) {
			return;
		}
		if (player.getNumericTemporaryAttribute("BoneBuryDelay").longValue() > Utils.currentTimeMillis()) {
			return;
		}
		if (bone.equals(Bones.BIG_BONES)) {
			player.getDailyChallengeManager().update(SkillingChallenge.BURY_BIG_BONES);
		} else if (bone.equals(Bones.LAVA_DRAGON_BONES)) {
			player.getAchievementDiaries().update(WildernessDiary.KILL_A_LAVA_DRAGON, 0x2);
		}
		player.lock(1);
		player.setAnimation(BURY_ANIMATION);
		double xp = bone.getXp();
		if (player.inArea(LavaDragonIsle.class) && bone == Bones.LAVA_DRAGON_BONES) {
		    xp *= 4;
        }
		player.getSkills().addXp(Skills.PRAYER, xp);
		player.getInventory().deleteItem(slot, item);
		player.sendSound(2738);
		player.sendMessage("You bury the " + bone.name + ".");
		player.getTemporaryAttributes().put("BoneBuryDelay", Utils.currentTimeMillis() + 1000);
		val crusherType = Bonecrusher.CrusherType.get(player);
		if (crusherType != null) {
			crusherType.getEffect().crush(player, bone, true);
		} else if (player.inArea("Catacombs of Kourend")) {
			Bonecrusher.restorePrayer(player, bone);
		}
	}
}