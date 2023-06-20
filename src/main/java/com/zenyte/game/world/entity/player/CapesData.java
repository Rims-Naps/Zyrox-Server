package com.zenyte.game.world.entity.player;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 2 okt. 2018 | 21:07:27
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public enum CapesData {
	
	ATTACK(new Animation(4959), new Graphics(823), new Item(9747), new Item(9748), new Item(31050)),
	DEFENCE(new Animation(4961), new Graphics(824), new Item(9753), new Item(9754), new Item(32037)),
	STRENGTH(new Animation(4981), new Graphics(828), new Item(9750), new Item(9751), new Item(32039)),
	HITPOINTS(new Animation(4971), new Graphics(833), new Item(9768), new Item(9769), new Item(32060)),
	RANGED(new Animation(4973), new Graphics(832), new Item(9756), new Item(9757), new Item(32070)),
	PRAYER(new Animation(4979), new Graphics(829), new Item(9759), new Item(9760), new Item(32068)),
	MAGIC(new Animation(4939), new Graphics(813), new Item(9762), new Item(9763), new Item(32064)),
	COOKING(new Animation(4955), new Graphics(821), new Item(9801), new Item(9802), new Item(32045)),
	WOODCUTTING(new Animation(4957), new Graphics(822), new Item(9807), new Item(9808), new Item(32080)),
	FLETCHING(new Animation(4937), new Graphics(812), new Item(9783), new Item(9784), new Item(32053)),
	FISHING(new Animation(4951), new Graphics(819), new Item(9798), new Item(9799), new Item(32051)),
	FIREMAKING(new Animation(4975), new Graphics(831), new Item(9804), new Item(9805), new Item(32055)),
	CRAFTING(new Animation(4949), new Graphics(818), new Item(9780), new Item(9781), new Item(32047)),
	SMITHING(new Animation(4943), new Graphics(815), new Item(9795), new Item(9796), new Item(32076)),
	MINING(new Animation(4941), new Graphics(814), new Item(9792), new Item(9793), new Item(32066)),
	HERBLORE(new Animation(4969), new Graphics(835), new Item(9774), new Item(9775), new Item(32058)),
	AGILITY(new Animation(4977), new Graphics(830), new Item(9771), new Item(9772), new Item(32041)),
	THIEVING(new Animation(4965), new Graphics(826), new Item(9777), new Item(9778), new Item(32078)),
	SLAYER(new Animation(4967), new Graphics(827), new Item(9786), new Item(9787), new Item(32074)),
	FARMING(new Animation(4963), new Graphics(825), new Item(9810), new Item(9811), new Item(32049)),
	RUNECRAFTING(new Animation(4947), new Graphics(817), new Item(9765), new Item(9766), new Item(32072)),
	CONSTRUCTION(new Animation(4953), new Graphics(820), new Item(9789), new Item(9790), new Item(32043)),
	HUNTER(new Animation(5158), new Graphics(907), new Item(9948), new Item(9949), new Item(32062)),
	QUEST_POINT(new Animation(4945), new Graphics(816), new Item(9813), new Item(13068)),
	MAX(new Animation(7121), new Graphics(1286), new Item(13342), new Item(13280), new Item(13282), new Item(13329), new Item(13331), new Item(13333), new Item(13335),
            new Item(13337), new Item(13342), new Item(20760), new Item(21186), new Item(21284), new Item(21285), new Item(21776), new Item(21780), new Item(21784), new Item(21898), new Item(32184), new Item(32367));
	
	@Getter
	private final Animation animation;
	@Getter
	private final Graphics graphics;
	@Getter
	private final Item[] capes;
	
	public static final CapesData[] VALUES = values();
	public static final Map<Integer, CapesData> MAP = new HashMap<Integer, CapesData>(VALUES.length);
	
	private CapesData(final Animation animation, final Graphics graphics, Item... capes){
		this.animation = animation;
		this.graphics = graphics;
		this.capes = capes;
	}
	
	static {
		for (CapesData data : VALUES) {
			for (Item cape : data.capes) 
				MAP.put(cape.getId(), data);
		}
	}
	
}
