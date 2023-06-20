package com.zenyte.game.item.enums;

import com.zenyte.game.item.ItemId;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

/**
 * @author Kris | 4. apr 2018 : 16:23.45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Getter
public enum DismantleableItem {
	//TODO This whole thing needs to be checked out for consistency.
	ARMADYL_GODSWORD(11802, 11810, 11798, false),
	BANDOS_GODSWORD(11804, 11812, 11798, false),
	SARADOMIN_GODSWORD(11806, 11814, 11798, false),
	ZAMORAK_GODSWORD(11808, 11816, 11798, false),
	ARMADYL_GODSWORD_OR(20368, 20068, 11802, true),
	BANDOS_GODSWORD_OR(20370, 20071, 11804, true),
	SARADOMIN_GODSWORD_OR(20372, 20074, 11806, true),
	ZAMORAK_GODSWORD_OR(20374, 20077, 11808, true),
	LIGHT_INFINITY_HAT(12419, 12530, 6918, true),
	LIGHT_INFINITY_TOP(12420, 12530, 6916, true),
	LIGHT_INFINITY_BOTTOMS(12421, 12530, 6924, true),
	DARK_INFINITY_HAT(12457, 12528, 6918, true),
	DARK_INFINITY_TOP(12458, 12528, 6916, true),
	DARK_INFINITY_BOTTOMS(12459, 12528, 6924, true),
	DRAGON_CHAINBODY_G(12414, 12534, 3140, true),
	DRAGON_PLATELEGS_G(12415, 12536, 4087, true),
	DRAGON_PLATESKIRT_G(12416, 12536, 4585, true),
	DRAGON_FULL_HELM_G(12417, 12538, 11335, true),
	DRAGON_SQ_SHIELD_G(12418, 12532, 1187, true),
	DRAGON_DEFENDER_G(19722, 20143, 12954, true),
	DRAGON_SCIMITAR_G(20000, 20002, 4587, true),
	DRAGON_BOOTS_G(22234, 22231, 11840, true),
	DRAGON_PLATEBODY_G(22242, 22236, 21892, true),
	DRAGON_KITESHIELD_G(22244, 22239, 21895, true),
	AMULET_OF_FURY_G(12436, 12526, 6585, true),
	OCCULT_NECKLACE_G(19720, 20065, 12002, true),
	AMULET_OF_TORTURE_G(20366, 20062, 19553, true),
	NECKLACE_OF_ANGUISH_G(22249, 22246, 19547, true),
    TORMENTED_BRACELET_G(23444, 23348, 19544, true),
	GUTHIX_SCIMITAR(ItemId.RUNE_SCIMITAR_23330, ItemId.RUNE_SCIMITAR_ORNAMENT_KIT_GUTHIX, ItemId.RUNE_SCIMITAR, true),
	SARADOMIN_SCIMITAR(ItemId.RUNE_SCIMITAR_23332, ItemId.RUNE_SCIMITAR_ORNAMENT_KIT_SARADOMIN, ItemId.RUNE_SCIMITAR, true),
	ZAMORAK_SCIMITAR(ItemId.RUNE_SCIMITAR_23334, ItemId.RUNE_SCIMITAR_ORNAMENT_KIT_ZAMORAK, ItemId.RUNE_SCIMITAR, true),
	RUNE_DEFENDER_ORNAMENT_KIT(ItemId.RUNE_DEFENDER_T, ItemId.RUNE_DEFENDER_ORNAMENT_KIT, ItemId.RUNE_DEFENDER, true),
	BERSERKER_NECKLACE_ORNAMENT_KIT(ItemId.BERSERKER_NECKLACE_OR, ItemId.BERSERKER_NECKLACE_ORNAMENT_KIT, ItemId.BERSERKER_NECKLACE, true),
	TZHAAR_KET_OM_ORNAMENT_KIT(ItemId.TZHAARKETOM_T, ItemId.TZHAARKETOM_ORNAMENT_KIT, ItemId.TZHAARKETOM, true),
	TWISTED_ANCESTRAL_HAT(30520, 30526, 21018, true),
	TWISTED_ANCESTRAL_ROBE_TOP(30522, 30526, 21021, true),
	TWISTED_ANCESTRAL_ROBE_BOTTOM(30524, 30526, 21024, true),
	NEITIZNOT_FACEGUARD(30536, 30538, 10828, false),
	;
	
	private final int completeItem, kit, baseItem;
	private final boolean splitOnDeath;
	
	public static final DismantleableItem[] VALUES = values();
	public static final Int2ObjectOpenHashMap<DismantleableItem> MAPPED_VALUES =
            new Int2ObjectOpenHashMap<>(VALUES.length);
	
	static {
		for (final DismantleableItem val : VALUES) {
			MAPPED_VALUES.put(val.completeItem, val);
		}
	}
	
	DismantleableItem(final int completeItem, final int kit, final int baseItem, boolean splitOnDeath) {
		this.completeItem = completeItem;
		this.kit = kit;
		this.baseItem = baseItem;
		this.splitOnDeath = splitOnDeath;
	}
	
}
