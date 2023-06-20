package com.zenyte.game.world.entity.player.container.impl;

import lombok.Getter;
import mgi.types.config.InventoryDefinitions;

/**
 * @author Kris | 4. mai 2018 : 02:16:14
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum ContainerType {

    /** If1 containers */
    INVENTORY(93, 149, 0),
    FALADOR_PARTY_CHEST_DEPOSIT(92, 265, 116),
    FALADOR_PARTY_CHEST_PUBLIC_DEPOSIT(91, 265, 115),
    FALADOR_PARTY_CHEST_PRIVATE_DEPOSIT(92, 265, 116),
	FALADOR_PARTY_CHEST_INVENTORY_DEPOSIT(93, 266, 0),
	CUSTOM_FUR_CLOTHING(66, 477, 75),
	
	/** If3 containers */
	TOURNAMENT(207),
	HERB_SACK(10), //custom
	GEM_BAG(12), //custom
	GNOMISH_FIRELIGHTER(14),
	MAGIC_STORAGE(100),
	COLLECTION_LOG(620),
	STASH_UNIT_BUILD_STAGES(576),
	PRIVATE_STORAGE(583),
	SHARED_STORAGE(582, -2),
    MAXIMUM_SIZE_CONTAINER(582),
	RAID_REWARDS(581),
	EQUIPMENT(94),
	SPOILS_STAKE(541),
    TRADE(90),
    BANK(95),
    RUNE_POUCH(169),
    GE_COLLECTABLES_1(518),
    GE_COLLECTABLES_2(519),
    GE_COLLECTABLES_3(520),
    GE_COLLECTABLES_4(521),
    GE_COLLECTABLES_5(522),
    GE_COLLECTABLES_6(523),
    GE_COLLECTABLES_7(539),
    GE_COLLECTABLES_8(540),
    PRICE_CHECKER(90),
    SHOP(510),
	BARROWS_CHEST(141),
	ITEMS_KEPT_ON_DEATH(584),
	ITEMS_LOST_ON_DEATH(468),
	PUZZLE_BOX(140),
	THEATRE_OF_BLOOD(612),
	FALADOR_PARTY_CHEST(91),
	ITEM_RETRIEVAL_SERVICE(525),
	DUEL_STAKE(134, -1),
	OPPONENT_STAKE(134, -2),
	SEED_BOX(150),
	SEED_VAULT(626),
	LOOTING_BAG(516);

	public static final ContainerType[] VALUES = values();

	public static final ContainerType[] GE_COLLECTABLES_CONTAINERS = new ContainerType[8];

	static {
		for (int i = 0; i < 8; i++) {
			GE_COLLECTABLES_CONTAINERS[i] = ContainerType.valueOf("GE_COLLECTABLES_" + (i + 1));
		}
	}
	
	@Getter private final int id, interfaceId, componentId;
	@Getter private final String name;
	
	ContainerType(final int id) {
		this(id, -1, 0);
	}
	
	ContainerType(final int id, final int interfaceId) {
		this(id, interfaceId, 0);
	}
	
	ContainerType(final int id, final int interfaceId, final int componentId) {
		this.id = id;
		this.interfaceId = interfaceId;
		this.componentId = componentId;
		name = toString().toLowerCase().replaceAll("_", " ");
	}
	
	public int getSize() {
		final InventoryDefinitions defs = InventoryDefinitions.get(getId());
		return defs == null ? 0 : defs.getSize();
	}
	
}
