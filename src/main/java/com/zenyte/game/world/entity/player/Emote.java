package com.zenyte.game.world.entity.player;

import com.zenyte.game.content.achievementdiary.diaries.FaladorDiary;
import com.zenyte.game.content.achievementdiary.diaries.LumbridgeDiary;
import com.zenyte.game.content.achievementdiary.diaries.VarrockDiary;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom || Kris | 22. veebr 2018 : 21:41.43
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum Emote {

	ACHIEVEMENT_DIARY_CAPE(-1, player -> {
		val delay = EmoteSequence.ACHIEVEMENT_DIARY_ANIM.getDuration();
        addDelay(player, delay);
        player.lock(delay / 600);
		player.setAnimation(EmoteSequence.ACHIEVEMENT_DIARY_ANIM);
		player.setGraphics(EmoteSequence.ACHIEVEMENT_DIARY_GFX);
	}),
	YES(0, new Animation(855)),
	NO(1, new Animation(856)),
	BOW(2, new Animation(858)),
	ANGRY(3, new Animation(859)),
	THINK(4, new Animation(857)),
	WAVE(5, new Animation(863)),
	SHRUG(6, new Animation(2113)),
	CHEER(7, new Animation(862)),
	BECKON(8, new Animation(864)),
	LAUGH(9, new Animation(861)),
	JUMP_FOR_JOY(10, new Animation(2109)),
	YAWN(11, new Animation(2111)),
	DANCE(12, new Animation(866)),
	JIG(13, new Animation(2106)),
	SPIN(14, new Animation(2107)),
	HEADBANG(15, new Animation(2108)),
	CRY(16, new Animation(860)),
	BLOW_KISS(17, new Animation(1368), new Graphics(574)),
	PANIC(18, new Animation(2105)),
	RASPBERRY(19, new Animation(2110)),
	CLAP(20, new Animation(865)),
	SALUTE(21, new Animation(2112)),
	GOBLIN_BOW(22, 532, new Animation(2127)),
	GOBLIN_SALUTE(23, 532, new Animation(2128)),
	GLASS_BOX(24, 1368, new Animation(1131)),
	CLIMB_ROPE(25, 1369, new Animation(1130)),
	LEAN(26, 1370, new Animation(1129)),
	GLASS_WALL(27, 1367, new Animation(1128)),
	IDEA(28, 2311, new Animation(4276), new Graphics(712)),
	STAMP(29, 2312, new Animation(4278)),
	FLAP(30, 2309, new Animation(4280)),
	SLAP_HEAD(31, 2310, new Animation(4275)),
	ZOMBIE_WALK(32, 1921, new Animation(3544)),
	ZOMBIE_DANCE(33, 1920, new Animation(3543)),
	SCARED(34, 1371, new Animation(2836)),
	RABBIT_HOP(35, 2055, new Animation(6111)),
	SIT_UP(36, 4732, new Animation(2763)),
	PUSH_UP(37, 4732, new Animation(2762)),
	STAR_JUMP(38, 4732, new Animation(2761)),
	JOG(39, 4732, new Animation(2764)),
	ZOMBIE_HAND(40, 1000, new Animation(4513), new Graphics(320)),
	HYPERMOBILE_DRINKER(41, 4802, new Animation(7131)),
	SKILL_CAPE(42, player -> {
		val capeId = player.getEquipment().getId(EquipmentSlot.CAPE.getSlot());
		if (capeId == 13221 || capeId == 13222) {
			play(player, "air_guitar");
			return;
		} else if (capeId == 19476 || capeId == 13069) {
			play(player, "achievement_diary_cape");
			return;
		}
		val cape = CapesData.MAP.get(capeId);
		if (cape == null) {
			player.sendMessage("You need to be wearing a skillcape in order to perform that emote.");
			return;
		}
		if (cape.equals(CapesData.QUEST_POINT)) {
			player.getAchievementDiaries().update(LumbridgeDiary.PERFORM_QUEST_CAPE_EMOTE);
		}
		player.getAchievementDiaries().update(FaladorDiary.PERFORM_SKILLCAPE_EMOTE);
		val delay = cape.getAnimation() == null ? 7200 : cape.getAnimation().getDuration();
        addDelay(player, delay);
        player.lock(delay / 600);
		if (cape.getAnimation() != null) {
			player.setAnimation(cape.getAnimation());
		}
		if (cape.getGraphics() != null) {
			player.setGraphics(cape.getGraphics());
		}
	}),
	AIR_GUITAR(43, 4673, new Animation(4751), new Graphics(1239)),
	URI_TRANSFORM(44, 5104, player -> {
        addDelay(player, 10200);
        player.lock(16);
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks = 0;

			@Override
			public void run() {
				switch (ticks) {
				case 0:
					player.setGraphics(EmoteSequence.URI_POOF);
					player.getAppearance().transform(7311);
					break;
				case 1:
					player.setGraphics(EmoteSequence.URI_START_GFX);
					player.setAnimation(EmoteSequence.URI_START_ANIM);
					player.getAppearance().transform(7313);
					break;
				case 6:
					player.setAnimation(EmoteSequence.URI_MIDDLE_ANIM);
					break;
				case 7:
					player.setGraphics(EmoteSequence.URI_END_GFX);
					player.setAnimation(EmoteSequence.URI_END_ANIM);
					break;
				case 8:
					player.setGraphics(EmoteSequence.URI_POOF);
					player.setAnimation(Animation.STOP);
					player.getAppearance().transform(-1);
					stop();
					break;
				}
				ticks++;
			}
		}, 0, 1);
	}),
	SMOOTH_DANCE(45, 5597, new Animation(7533)),
	CRAZY_DANCE(46, 5598, player -> {
		val cycle = player.getNumericTemporaryAttribute("CrazyDanceCycle").intValue() & 0x1;
		player.getTemporaryAttributes().put("CrazyDanceCycle", cycle + 1);
		val anim = cycle == 0 ? EmoteSequence.CRAZY_DANCE_A : EmoteSequence.CRAZY_DANCE_B;
		addDelay(player, anim.getDuration());
		player.setAnimation(anim);
	}),
	PREMIER_SHIELD(47, 6041, player -> {
		final int cycle = player.getNumericTemporaryAttribute("PremierShieldCycle").intValue() % 3;
		player.getTemporaryAttributes().put("PremierShieldCycle", cycle + 1);
        addDelay(player, EmoteSequence.PREMIER_SHIELD_ANIM.getDuration());
		player.setAnimation(EmoteSequence.PREMIER_SHIELD_ANIM);
		player.setGraphics(EmoteSequence.PREMIER_GRAPHICS[cycle]);
	}),
	TRICK(48, -2, new Animation(15247), new Graphics(2517)),
	GIVE_THANKS(49, -2, null, null),
	SNOWMAN_DANCE(50, 15024, new Animation(15094), null),
	FREEZE(51, 15025, new Animation(15093), new Graphics(2505)),
	DRAMATIC_POINT(52, 15026, new Animation(15084), new Graphics(2502)),
	AROUND_THE_WORLD_IN_EGGTY_DAYS(53, 15051, new Animation(15231), new Graphics(2514));
	
	@Getter
	private final Animation animation;
	@Getter
	private final Graphics graphics;
	@Getter
	private final int slot, config;
	
	@Getter private final EmoteSequence sequence;
	
	public static final Emote[] VALUES = values();
	private static final Emote[] SOS_EMOTES = { FLAP, SLAP_HEAD, IDEA, STAMP };

	public static final int GIVE_THANKS_VARP = 3622;
	
	public static void play(final Player player, final String emoteName) {
		val emote = MAP.get(emoteName);
		if (emote == null) {
			return;
		}
		if (player.getTemporaryAttributes().get("greegree") != null) {
			player.sendMessage("You can't perform that emote now.");
			return;
		}
		if (emote == SKILL_CAPE || emote == URI_TRANSFORM) {
		    if (player.isLocked()) {
		        return;
            }
		    if (player.getRaid().isPresent()) {
                player.sendMessage("You can't do that here.");
		        return;
            }
        }
		TreasureTrail.emoteChallenge(player, emote);
		if (emote == TRICK) {
			player.forceAnimation(emote.animation);
			player.setGraphics(emote.graphics);
			val delay = (int) Math.ceil(emote.animation.getDuration() / 600F);
			player.addMovementLock(new MovementLock(System.currentTimeMillis() + (delay * 600)));
			addDelay(player, delay * 600);
			return;
		} else if (emote == GIVE_THANKS) {
			addDelay(player, 18 * 600);
			player.lock(17);
			player.setInvalidAnimation(EmoteSequence.GIVE_THANKS_START);
			WorldTasksManager.schedule(new TickTask() {
				@Override
				public void run() {
					switch(ticks++) {
						case 0:
							player.setGraphics(EmoteSequence.POOF_GRAPHICS);
							player.getAppearance().transform(10022);
							player.setInvalidAnimation(EmoteSequence.TURKEY_SPIN);
							break;
						case 13:
							player.getAppearance().transform(-1);
							player.setInvalidAnimation(EmoteSequence.GIVE_THANKS_END);
							player.setGraphics(EmoteSequence.POOF_GRAPHICS);
							stop();
							break;
					}
				}
			}, 1, 0);
			return;
		}
		if (ArrayUtils.contains(SOS_EMOTES, emote)) {
			val index = ArrayUtils.indexOf(SOS_EMOTES, emote);
			player.getAchievementDiaries().update(VarrockDiary.PERFORM_SOS_EMOTES, (int) Math.pow(2, index));
		}
 		if (emote.sequence != null) {
			emote.sequence.play(player);
			return;
		}
 		if (emote == AIR_GUITAR) {
 			player.getMusic().playJingle(249);
		}
		val delay = (int) Math.ceil(emote.animation.getDuration() / 600F);

 		if (emote == FREEZE || emote == AROUND_THE_WORLD_IN_EGGTY_DAYS || emote == ACHIEVEMENT_DIARY_CAPE) {
 			player.addMovementLock(new MovementLock(System.currentTimeMillis() + (delay * 600)));
 			if (emote == AROUND_THE_WORLD_IN_EGGTY_DAYS) {
				addDelay(player, (delay + 1) * 600);
			}
		}
		player.setInvalidAnimation(emote.animation);
		if (emote.graphics != null) {
			player.setGraphics(emote.graphics);
			if (emote != AROUND_THE_WORLD_IN_EGGTY_DAYS) {
				addDelay(player, delay * 600);
			}
		}
	}

	private static void addDelay(final Player player, final long milliseconds) {
        player.getTemporaryAttributes().put("emote_delay", Utils.currentTimeMillis() + milliseconds);
    }
	
	public static final Map<String, Emote> MAP = new HashMap<>(VALUES.length);

	Emote(final int slotId, final EmoteSequence sequence) {
		this(slotId, -1, null, null, sequence);
	}
	
	Emote(final int slotId, final int varbitId, final EmoteSequence sequence) {
		this(slotId, varbitId, null, null, sequence);
	}
	
	Emote(final int slotId, final Animation animation) {
		this(slotId, -1, animation, null, null);
	}
	
	Emote(final int slotId, final int varbitId, final Animation animation) {
		this(slotId, varbitId, animation, null, null);
	}
	
	Emote(final int slotId, final Animation animation, final Graphics graphics) {
		this(slotId, -1, animation, graphics, null);
	}
	
	Emote(final int slotId, final int varbitId, final Animation animation, final Graphics graphics) {
		this(slotId, varbitId, animation, graphics, null);
	}
	
	Emote(final int slotId, final int config, final Animation animation, final Graphics graphics, final EmoteSequence sequence) {
		slot = slotId;
		this.config = config;
		this.animation = animation;
		this.graphics = graphics;
		this.sequence = sequence;
		if (config != -1) {
		    VarManager.appendPersistentVarbit(config);
        }
	}
	
	static {
		for (val emote : VALUES) {
			MAP.put(emote.toString().toLowerCase(), emote);
		}
	}
	
	interface EmoteSequence {
		
		Graphics URI_POOF = new Graphics(86, 0, 100);
		Graphics URI_START_GFX = new Graphics(1306);
		Graphics URI_END_GFX = new Graphics(678);
		Animation URI_START_ANIM = new Animation(7278);
		Animation URI_MIDDLE_ANIM = new Animation(4069);
		Animation URI_END_ANIM = new Animation(4071);
		
		Animation ACHIEVEMENT_DIARY_ANIM = new Animation(2709);
		Graphics ACHIEVEMENT_DIARY_GFX = new Graphics(323);
		
		Animation PREMIER_SHIELD_ANIM = new Animation(7751);
		Animation CRAZY_DANCE_A = new Animation(7536);
		Animation CRAZY_DANCE_B = new Animation(7537);

		Animation GIVE_THANKS_START = new Animation(11008);
		Animation GIVE_THANKS_END = new Animation(11009);
		Animation TURKEY_SPIN = new Animation(11010);
		Graphics POOF_GRAPHICS = new Graphics(5005);

		Graphics[] PREMIER_GRAPHICS = new Graphics[] {
				new Graphics(1412), new Graphics(1413), new Graphics(1414)
		};
		
		void play(final Player player);
		
	}
}
