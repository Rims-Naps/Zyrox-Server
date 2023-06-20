package com.zenyte.plugins.renewednpc;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;

/**
 * @author Kris | 25/11/2018 09:49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum ShopNPCHandler {

    AARONS_ARCHERY_APPENDAGES("Aaron's Archery Appendages", 6059),
    AEMADS_ADVENTURING_SUPPLIES("Aemad's Adventuring Supplies", 1177),
    AGMUNDI_QUALITY_CLOTHES("Agmundi Quality Clothes", 2366),
    AK_HARANUS_EXOTIC_SHOP("Ak-Haranu's Exotic Shop", 2989),//TODO: Npc only visible after quest completion.
    AL_KHARID_GENERAL_STORE("Al Kharid General Store", 510),
    ALECKS_HUNTER_EMPORIUM("Aleck's Hunter Emporium", 1501),
    ALICES_FARMING_SHOP("Alice's Farming shop", 504),
    ALLANNAS_FARMING_SHOP("Allanna's Farming Shop", 8531),
    //TODO: Ali's Discount Wares,
    AMELIAS_SEED_SHOP("Amelia's Seed Shop", 8530),
    ARDOUGNE_BAKERS_STALL("Ardougne Baker's Stall", 1040),//Potentially TODO: Western side sells more items.
    ARDOUGNE_FUR_STALL("Ardougne Fur Stall", 1042),
    ARDOUGNE_GEM_STALL("Ardougne Gem Stall", 1039),
    ARDOUGNE_SILVER_STALL("Ardougne Silver Stall", 1038),
    ARDOUGNE_SPICE_STALL("Ardougne Spice Stall", 1041),
    NEDS_HANDMADE_ROPE("Ned's Handmade Rope", 4280),
    ARHEIN_STORE("Arhein Store", 1032),
    RAUM_URDA_STEIN_ARMOUR_SHOP("Armour Shop", 1943),
    JORZIK_ARMOUR_STORE("Armour store", 1561),//TODO: Verify that npc exists + add access to it.
    ARMOURY("Armoury", 1030),
    ARNOLDS_ECLECTIC_SUPPLIES("Arnold's Eclectic Supplies", 4293),//TODO: Add npc
    AUBURY_RUNES_SHOP("Aubury's Rune Shop", 637),
    AURELS_SUPPLIES("Aurel's Supplies", 4445),//TODO: Requires quest completion.
    AUTHENTIC_THROWING_WEAPONS("Authentic Throwing Weapons", 6069),
    AVAS_ODDS_AND_ENDS("Ava's Odds and Ends", 4407),//TODO: Requires quest completion.
    BABA_YAGAS_MAGIC_SHOP("Baba Yaga's Magic Shop", 3837),//TODO: Add access + spawns.
    BANDIT_BARGAINS("Bandit Bargains", 1833),
    BANDIT_DUTY_FREE("Bandit Duty Free", 1304),
    BARKER_HABERDASHERY("Barker's Haberdashery", 6524),
    BATTLE_RUNES("Battle Runes", 2581),
    BEDABIN_VILLAGE_BARTERING("Bedabin Village Bartering", 4638),
    BETTYS_MAGIC_EMPORIUM("Betty's Magic Emporium", 1052),
    BLADES_BY_URBI("Blades by Urbi", 3891),//TODO: Requires quest completion.
    BLURBERRY_BAR("Blurberry Bar", 6532),//TODO: Daero npc also allows opening shop.
    BOBS_BRILLIANT_AXES("Bob's Brilliant Axes", 505),
    BOLKOYS_VILLAGE_SHOP("Bolkoy's Village Shop", 4965),
    BRIANS_ARCHERY_SUPPLIES("Brian's Archery Supplies", 1309),
    BRIANS_BATTLEAXE_BAZAAR("Brian's Battleaxe Bazaar", 1028),
    BRIDGETS_ARMOUR("Briget's Armour", 7201),//TODO: On option "Armour"
    BRIDGETS_WEAPONS("Briget's Weapons", 7201),//TODO: On option "Weapons"
    BURTHORPE_SUPPLIES("Burthorpe Supplies", 4106),
    CANDLE_SHOP("Candle Shop", 1031),
    CAREFREE_CRAFTING_STALL("Carefree Crafting Stall", 2363),//TODO: Ensure npc exists
    CASSIES_SHIELD_SHOP("Cassie's Shield Shop", 1046),
    CONTRABAND_YAK_PRODUCE("Contraband yak produce", 1990),//TODO: No trade option.
    //TODO: Culinaromancer's chest.
    DAGAS_SCIMITAR_SMITHY("Daga's Scimitar Smithy", 5250),
    DALS_GENERAL_OGRE_SUPPLIES("Dal's General Ogre Supplies", 4404),
    DARGAUDS_BOW_AND_ARROWS("Dargaud's Bow and Arrows", 6060),
    DARRENS_WILDERNESS_CAPE_SHOP("Darren's Wilderness Cape Shop", 2198),
    DAVONS_AMULET_STORE("Davon's Amulet Store", 1175),
    DEAD_MANS_CHEST("Dead Man's Chest", 1314),//TODO: No trade option.
    DIANGOS_TOY_STORE("Diango's Toy Store", 1308),
    //DODGY_MIKES_SECOND_HAND_CLOTHING("Dodgy Mike's Second-hand Clothing", 4022),
    DOMMIKS_CRAFTING_STORE("Dommik's Crafting Store", 531),
    DORGESH_KAAN_GENERAL_SUPPLIES("Dorgesh-Kaan General Supplies", 2314),//TODO: Verify npc's existence.
    DRAYNOR_SEED_MARKET("Draynor Seed Market", 500),
    DROGOS_MINING_EMPORIUM("Drogo's Mining Emporium", 1048),
    DWARVEN_SHOPPING_STORE("Dwarven Shopping Store", 1051),
    EDGEVILLE_GENERAL_STORE_ASSISTANCE("Edgeville General Store", 515),
    EDGEVILLE_GENERAL_STORE_KEEPER("Edgeville General Store", 514),
    EDMONDS_WILDERNESS_CAPE_SHOP("Edmond's Wilderness Cape Shop", 2202),
    EDWARDS_WILDERNESS_CAPE_SHOP("Edward's Wilderness Cape Shop", 2199),
    ETCETERIA_FISH("Etceteria Fish", 775),
    FAIRY_FIXITS_FAIRY_ENCHANTMENT("Fairy Fixit's Fairy Enchantment", 7333),//TODO: Quest requirement
    FALADOR_GENERAL_STORE("Falador General Store", 512, 513),
    FANCY_CLOTHES_STORE("Fancy Clothes Store", 1023),
    FERNAHEIS_FISHING_HUT("Fernahei's Fishing Hut", 5363),
    FILAMINAS_WARES("Filamina's Wares", 7204),//TODO: Add npc spawn
    FINE_FASHIONS("Fine Fashions", 14),
    FISHING_GUILD_SHOP("Fishing Guild Shop", 1299),
    FLOSIS_FISHMONGERS("Flosi's Fishmongers", 1942),
    FLYNNS_MACE_MARKET("Flynn's Mace Market", 1049),
    FORTUNADOS_FINE_WINE("Fortunato's Fine Wine", 1260),
    FOSSIL_ISLAND_GENERAL_STORE("Fossil Island General Store", 7769),
    FRANKIES_FISHING_EMPORIUM("Frankie's Fishing Emporium", 6963),
    FREMENNIK_FISHMONGER("Fremennik Fishmonger", 3947),
    FREMENNIK_FUR_TRADER("Fremennik Fur Trader", 3948),
    FRENITAS_COOKERY_SHOP("Frenita's Cookery Shop", 1300),
    FRINCOS_FABULOUS_HERB_STORE("Frincos' Fabulous Herb Store", 1047),
    FUNCHS_FINE_GROCERIES("Funch's Fine Groceries", 16),
    GABOOTYS_TAI_BWO_WANNAI_COOPERATIVE("Gabooty's Tai Bwo Wannai Cooperative", 6424),//TODO: Co-op option + tradingsticks used.
    GABOOTYS_TAI_BWO_WANNAI_DRINKY_STORE("Gabooty's Tai Bwo Wannai Drinky Store", 6424),//TODO: Drinks option +trading sticks
    GAIUS_TWO_HANDED_STORE("Gaius' Two-Handed Shop", 1173),
    GARDEN_CENTRE("Garden Centre", 5423),
    GEM_TRADER("Gem Trader", 526),
    GENERAL_STORE_CANIFIS("General Store (Canifis)", 6525),
    GERRANTS_FISHY_BUSINESS("Gerrant's Fishy Business", 1027),
    GIANNES_RESTAURANT("Gianne's Restaurant", 2657),
    GRACES_GRACEFUL_CLOTHING("Grace's Graceful Clothing", 5919),//TODO: Verify existence + use marks of grace currency
    GRAND_TREE_GROCERIES("Grand Tree Groceries", 12),
    GREEN_GEMSTONE_GEMS("Green Gemstone Gems", 2362), //TODO: Verify existence
    GREENGROCER_OF_MISCELLANIA("Greengrocer of Miscellania", 3689),
    GRUDS_HERBLORE_STALL("Grud's Herblore Stall", 4402),
    GRUMS_GOLD_EXCHANGE("Grum's Gold Exchange", 1025),
    GULLUCK_AND_SONS("Gulluck and Sons", 15),
    GUNSLIK_ASSORTED_ITEMS("Gunslik's Assorted Items", 2359),
    HABABS_CRAFTING_EMPORIUM("Hamab's Crafting Emporium", 5253),
    HAPPY_HEROES_HEMPORIUM("Happy Heroes' H'Emporium", 4924),
    HARPOON_JOES_HOUSE_OF_RUM("Harpoon Joe's House of 'Rum'", 4019),
    HARRYS_FISHING_SHOP("Harry's Fishing Shop", 1045),
    HELMET_SHOP("Helmet Shop", 524),
    HENDORS_AWESOME_ORES("Hendor's Awesome Ores", 7717),
    HERQUINS_GEMS("Herquin's Gems", 1053),
    HICKTONS_ARCHERY_EMPORIUM("Hickton's Archery Emporium", 1044),
    HORVIKS_SMITHY("Horvik's Armour Shop", 535),
    IANS_WILDERNESS_CAPE_SHOP("Ian's Wilderness Cape Shop", 2196),
    IFABAS_GENERAL_STORE("Ifaba's General Store", 5252),
    ISLAND_FISHMONGER("Island Fishmonger", 3688),
    ISLAND_GREENGROCER("Island Greengrocer", 776),
    JAMILAS_CRAFT_STALL("Jamila's Craft Stall", 3892),//TODO: Quest requirement
    JATIXS_HERBLORE_SHOP("Jatix's Herblore Shop", 1174),
    JENNIFERS_GENERAL_FIELD_SUPPLIES("Jennifer's General Field Supplies", 305),
    JIMINUAS_JUNGLE_STORE("Jiminua's Jungle Store", 1029),
    KARAMJA_GENERAL_STORE("Karamja General Store", 518, 519),
    KARAMJA_WINES_SPIRITS_BEERS("Karamja Wines, Spirits, and Beers", 1037),
    KEEPA_KETTILONS_STORE("Keepa Kettilon's Store", 1945),
    KELDAGRIM_STONEMASON("Keldagrim Stonemason", 5420),
    KELDAGRIMS_BEST_BREAD("Keldagrim's Best Bread", 2361),//TODO: Verify existence.
    KENELMES_WARES("Kenelme's Wares", 7202),
    KHAZARD_GENERAL_STORE("Khazard General Store", 1024),
    //KING_NARNODES_ROYAL_SEED_PODS("King Narnode's Royal Seed Pods", 8020),
    //TODO: Kjut's kebabs through dialogue
    LARRYS_WILDERNESS_CAPE_SHOP("Larry's Wilderness Cape Shop", 2197),
    LEENZ_GENERAL_SUPPLIES("Leenz's General Supplies", 6986),
    LEGENDS_GUILD_GENERAL_STORE("Legends Guild General Store", 3960),//TODO: Add npc
    LEGENDS_GUILD_SHOP_OF_USEFUL_ITEMS("Legends Guild Shop of Useful Items", 3961),//TODO: Add npc
    LEPRECHAUN_LARRYS_FARMING_SUPPLIES("Leprechaun Larry's Farming Supplies", 757),//TODO: Add npc
    LITTLE_MUNTYS_LITTLE_SHOP("Little Munty's Little Shop", 7088),
    LITTLE_SHOP_OF_HORACE("Little Shop of Horace", 6943),//TODO: Add npc
    LLETYA_ARCHERY_SHOP("Lletya Archery Shop", 1481),
    LLETYA_FOOD_STORE("Lletya Food Store", 1482),
    LLETYA_GENERAL_STORE("Lletya General Store", 1477),
    LLETYA_SEAMSTRESS("Lletya Seamstress", 1478),
    LOGAVA_GRICOLLERS_COOKING_SUPPLIES("Logava Gricoller's Cooking Supplies", 6945),
    LOUIES_ARMOURED_LEGS_BAZAAR("Louie's Armoured Legs Bazaar", 528),
    LOVECRAFTS_TACKLE("Lovecraft's Tackle", 4789),
    LOWES_ARCHERY_EMPORIUM("Lowe's Archery Emporium", 536),
    LUMBRIDGE_GENERAL_STORE("Lumbridge General Store", 506, 507),
    LUNDAILS_ARENASIDE_RUNE_SHOP("Lundail's Arena-side Rune Shop", 1601),
    MAGE_ARENA_STAFFS("Mage Arena Staffs", 1602),
    MAGIC_GUILD_STORE("Magic Guild Store (Mystic Robes)", 3249),
    MAGIC_GUID_RUNES("Magic Guild Store (Runes and Staves)", 3247),
    IRKSOL("Irksol", 1035),
    JUKAT("Jukat", 1033),
    MARTIN_THWAITS_LOST_AND_FOUND("Martin Thwait's Lost and Found", 3193),//TODO: Verify existence
    MILTOGS_LAMPS("Miltog's Lamps", 2297),//TODO: Verify existence
    MISCELLANIAN_CLOTHES_SHOP("Miscellanian Clothes Shop", 1079),//TODO: Add npc
    MISCELLANIAN_FOOD_SHOP("Miscellanian Food Shop", 1081),
    MISCELLANIAN_GENERAL_STORE("Miscellanian General Store", 1080),
    MOON_CLAN_FINE_CLOTHES("Moon Clan Fine Clothes", 3842),
    MOON_CLAN_GENERAL_STORE("Moon Clan General Store", 3840),
    MULTICANNON_PARTS("Multicannon Parts", 1400),
    MYTHICAL_CAPE_STORE("Mythical Cape Store", 8037),
    MYTHS_GUILD_ARMOURY("Myths' Guild Armoury", 7952),
    MYTHS_GUILD_HERBALIST("Myths' Guild Herbalist", 7953),
    MYTHS_GUILD_WEAPONRY("Myths' Guild Weaponry", 8036),
    NARDAH_GENERAL_STORE("Nardah General Store", 4755),
    NARDAH_HUNTER_SHOP("Nardah Hunter Shop", 1350),
    NARDOKS_BONE_WEAPONS("Nardok's Bone Weapons", 996),//TODO: Verify existence
    NATHIFAS_BAKE_STALL("Nathifa's Bake Stall", 3890),//TODO: Requires quest completion.
    NEILS_WILDERNESS_CAPE_SHOP("Neil's Wilderness Cape Shop", 2201),
    NEITIZNOT_SUPPLIES("Neitiznot supplies", 1884),
    NURMOFS_PICKAXE_SHOP("Nurmof's Pickaxe Shop", 1301),
    OBLIS_GENERAL_STORE("Obli's General Store", 5362),
    OOBAPOHKS_JAVELIN_STORE("Oobapohk's Javelin Store", 7120),
    ORE_SELLER("Ore Seller", 1560),//TODO: Verify exitence
    ORE_STORE("Ore store", 1941),
    OZIACHS_ARMOUR("Oziach's Armour", 822),
    PERRYS_CHOP_CHOP_SHOP("Perry's Chop-chop Shop", 7240),
    PICKAXE_IS_MINE("Pickaxe-Is-Mine", 2365),
    PIE_SHOP("Pie Shop", 6533),
    POLLNIVNEACH_GENERAL_STORE("Pollnivneach general store", 3537),
    PORT_PHASMATYS_GENERAL_STORE("Port Phasmatys General Store", 3000),
    QUALITY_ARMOUYR_SHOP("Quality Armour Shop", 2358),
    QUALITY_WEAPONS_SHOP("Quality Weapons Shop", 2357),
    QUARTERMASTERS_STORES("Quartermaster's Stores", 3438),//TODO: Open through dialogue, no trade option.
    RAETUL_AND_COS_CLOTHS_STORE("Raetul and Co's Cloth Store", 4204),//TODO: Open through dialogue, no trade op.
    RANAELS_SUPER_SKIRT_STORE("Ranael's Super Skirt Store", 530),
    RASOLO_THE_WANDERING_MERCHANT("Rasolo the Wandering Merchant", 679),
    RAZMIRE_BUILDERS_MERCHANTS("Razmire Builders Merchants", 1290),//TODO two options, no trade. Additionally quest
    RAZMIRE_GENERAL_STORE("Razmire General Store", 1290),//TODO: Two options, no trade. Additionally quest
    REGATHS_WARES("Regath's Wares", 7056),
    RELDAKS_LEATHER_ARMOUR("Reldak's Leather Armour", 2296),//TODO: Verify exitence
    RICHARDS_FARMING_SHOP("Richard's Farming shop", 503),
    RICHARDS_WILDERNESS_CAPE_SHOP("Richard's Wilderness Cape Shop", 2200),
    RIMMINGTON_GENERAL_STORE("Rimmington General Store", 516, 517),
    ROKS_CHOCS_BOX("Rok's Chocs Box", 4761),
    ROMMIKS_CRAFTY_SUPPLIES("Rommik's Crafty Supplies", 1172),
    RUFUS_MEAT_EMPORIUM("Rufus's Meat Emporium", 6478),
    SAMS_WILDERNESS_CAPE_SHOP("Sam's Wilderness Cape Shop", 2204),
    SARAHS_FARMING_SHOP("Sarah's Farming Shop", 501),
    SCAVVOS_RUNE_STORE("Scavvo's Rune Store", 523),
    SEDDUS_ADVENTURER_STORE("Seddu's Adventurers' Store", 4754),
    SHANTAY_PASS_SHOP("Shantay Pass Shop", 4642),
    SHOP_OF_DISTASTE("Shop of Distaste", 3340),//TODO: Uses Buy option instead.
    SILVER_COG_SILVER_STALL("Silver Cog Silver Stall", 2364),//TODO: Verify existence
    SIMONS_WILDERNESS_CAPE_SHOP("Simon's Wilderness Cape Shop", 2203),
    SKULGRIMENS_BATTLE_GEAR("Skulgrimen's Battle Gear", 3935),
    SMITHING_SMITHS_SHOP("Smithing Smith's Shop", 4018),
    SOLIHIBS_FOOD_STAFF("Solihib's Food Stall", 5249),
    TAMAYUS_SPEAR_STALL("Tamayu's Spear Stall", 4703),//TODO: Add npc, trading done through dialogue.
    THE_ASP_AND_SNAKE_BAR("The Asp & Snake Bar", 3535),//TODO: Multiple npcs open it.
    THE_BIG_HEIST_LODGE("The Big Heist Lodge", 687),
    THE_DEEPER_LODE("The Deeper Lode", 7090),
    THE_GOLDEN_FIELD("The Golden Field", 6954),
    THE_HAYMAKERS_ARMS("The Haymaker's Arms", 6953),
    THE_LIGHTHOUSE_STORE("The Lighthouse Store", 4423),
    THE_OTHER_INN("The Other Inn", 4020),//TODO: Another npc opens through dialogue.
    THE_SHRIMP_AND_PARROT("The Shrimp and Parrot", 4920),//TODO Another npc opens it too.
    THE_SPICE_IS_RIGHT("The Spice is Right", 4202),
    THESSELIAS_FINE_CLOTHES("Thessalia's Fine Clothes", 534),
    THIRUS_URKAR_FINE_DYNAMITE_STORE("Thirus Urkar's Fine Dynamite Store", 7208),
    THYRIAS_WARES("Thyria's Wares", 7203),
    TIADECHES_KARAMBWAN_STALL("Tiadeche's Karambwan Stall", 4700),//TODO: Add npc + Quest req
    TOAD_AND_CHICKEN("Toad and Chicken", 4102),
    TONYS_PIZZA_BASES("Tony's Pizza Bases", 1303),
    TOOTHYS_PICKAXES("Toothy's Pickaxes", 7071),
    TRADER_STANS_TRADING_POST("Trader Stan's Trading Post", 1328, 1329, 1330, 1331, 1332, 1333, 1334),
    TRADER_SVENS_BLACK_MARKET_GOODS("Trader Sven's Black-market Goods", 3779),//TODO: Opened through dialogue.
    TUTABS_MAGICAL_MARKET("Tutab's Magical Market", 5251),
    TWO_FEET_CHARLEYS_FISH_SHOP("Two Feet Charley's Fish Shop", 4017),
    TYNANS_FISHING_SUPPLIES("Tynan's Fishing Supplies", 6964),
    TZHAAR_HUR_LEKS_ORE_AND_GEM_STORE("TzHaar-Hur-Lek's Ore and Gem Store", 2184),
    TZHAAR_HUR_RINS_ORE_AND_GEM_STORE("TzHaar-Hur-Rin's Ore and Gem Store", 7689),
    TZHAAR_HUR_TELS_EQUIPMENT_STORE("TzHaar-Hur-Tel's Equipment Store", 2183),
    TZHAAR_MEJ_ROHS_RUNE_STORE("TzHaar-Mej-Roh's Rune Store", 2185),
    TZHAAR_HUR_ZALS_EQUIPMENT_STORE("TzHaar-Hur-Zal's Equipment Store", 7688),
    UGLUGS_STUFFIES("Uglug's Stuffsies", 861),
    VALAINES_SHOP_OF_CHAMPIONS("Valaine's Shop of Champions", 522),
    VANESSAS_FARMING_SHOP("Vanessa's Farming shop", 502),
    VANNAHS_FARM_STORE("Vannah's Farm Store", 6944),
    VARROCK_GENERAL_STORE("Varrock General Store", 508, 509),
    VARROCK_SWORDSHOP("Varrock Swordshop", 537),
    VERMUNDIS_CLOTHES_STALL("Vermundi's Clothes Stall", 2367),
    VIGRS_WARHAMMERS("Vigr's Warhammers", 2356),
    VOID_KNIGHT_ARCHERY_STORE("Void Knight Archery Store", 1765),
    VOID_KNIGHT_GENERAL_STORE("Void Knight General Store", 1768),
    VOID_KNIGHT_MAGIC_STORE("Void Knight Magic Store", 1767),
    WARRENS_FISH_MONGER("Warrens Fish Monger", 7912),//TODO: Add npc
    WARRENS_GENERAL_STORE("Warrens General Store", 7913),//TODO: Add npc
    WARRIOR_GUILD_ARMOURY("Warrior Guild Armoury", 2471),
    WARRIOR_GUILD_FOOD_SHOP("Warrior Guild Food Shop", 2469),
    WARRIOR_GUILD_POTION_SHOP("Warrior Guild Potion Shop", 2470),
    WAYNES_CHAINS_CHAINMAIL_SPECIALIST("Wayne's Chains - Chainmail Specialist", 1050),
    WEAPONS_GALORE("Weapons galore", 1944),
    WEST_ARDOUGNE_GENERAL_STORE("West Ardougne General Store", 4527),
    WILLIAMS_WILDERNESS_CAPE_SHOP("William's Wilderness Cape Shop", 2195),
    WYDINS_FOOD_STORE("Wydin's Food Store", 1026),
    YARSUL_PRODIGIUOUS_PICKAXES("Yarsul's Prodigious Pickaxes", 7718),
    YE_OLDE_TEA_SHOPPE("Ye Olde Tea Shoppe", 1302),
    YRSAS_ACCPUNTREMENTS("Yrsa's Accoutrements", 3933),
    ZAFFS_SUPERIOR_STAFFS("Zaff's Superior Staffs!", 532),
    ZANARIS_GENERAL_STORE("Zanaris General Store", 520),
    ZEKES_SUPERIOR_SCIMITARS("Zeke's Superior Scimitars", 527),
    ZENESHAS_PLATE_MAIL_BODY_SHOP("Zenesha's Plate Mail Body Shop", 1176),
    LEKE_QUO_KERAN("Mount Karuulm Weapon Shop", 8532),
    SLAYER_EQUIPMENT("Slayer Equipment", 401, 402, 403, 404, 405, 490, 6797, 7663, 8623);

    ShopNPCHandler(final String shop, final int... npcIds) {
        this.npcIds = npcIds;
        this.shop = shop;
    }

    final int[] npcIds;
    final String shop;

    static final ShopNPCHandler[] values = values();
    static final Int2ObjectOpenHashMap<ShopNPCHandler> map = new Int2ObjectOpenHashMap<>();
    static {
        for (val shop : values) {
            for (val id : shop.npcIds) {
                map.put(id, shop);
            }
        }
    }


}
