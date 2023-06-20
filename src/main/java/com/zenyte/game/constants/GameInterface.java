package com.zenyte.game.constants;

import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.NewInterfaceHandler;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Optional;

import static com.zenyte.game.ui.InterfacePosition.*;

/**
 * @author Kris | 11. juuli 2018 : 23:53:40
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public enum GameInterface {
    BRIMHAVEN_AGILITY(5, MINIGAME_OVERLAY),
    TOB_PARTY_INFORMATION(50),
    TOB_PARTIES_OVERVIEW(364),
    TOB_PARTY(28, OVERLAY),
    TOB_SUPPLIES(405),
    TOB_PERFORMANCE_STATS(459),
    PYRAMID_PLUNDER(428, MINIGAME_OVERLAY),
    WHEEL_OF_FORTUNE(710),
    SKOTIZO_OVERLAY(308, OVERLAY),
    MASTER_SCROLL_BOOK(597),
    CUSTOM_FUR_CLOTHING(477),
    TREASURE_TRAIL_STATISTICS(220),
    ITEM_SETS(451),
    ITEM_SETS_INVENTORY_INTERFACE(430, SINGLE_TAB),
    SEXTANT(365),
    NIEVE_GRAVESTONE(221),
    GRAND_EXCHANGE_OFFERS_VIEWER(709),
    MYCELIUM_TELEPORTATION(608),
    TOURNAMENT_VIEWER(708),
    DIANGO_ITEM_RETRIEVAL(78),
    STASH_UNIT(493),
    PRESET_MANAGER(711),
    TOURNAMENT_PRESETS(707),
    TOURNAMENT_SPECTATING(154),
    BATTLESTAFF_ENCHANTMENT(332),
    JOURNAL_HEADER_TAB(629, JOURNAL_TAB_HEADER),
    DAILY_CHALLENGES_OVERVIEW(705),
    JOSSIKS_SALVAGED_GODBOOKS(302),
    PUZZLE_BOX(306),
    LIGHT_BOX(322),
    RUNE_POUCH(190),
    RAID_OVERLAY(513, OVERLAY),
    RAID_PARTY_TAB(500, JOURNAL_TAB_HEADER),
    RAIDING_PARTIES(499),
    FURNITURE_CREATION(458),
    RAIDING_PARTY(507),
    RAID_REWARDS(539),
    RAIDS_SCOREBOARD(21),
    EQUIPMENT_INVENTORY(85, InterfacePosition.SINGLE_TAB),
    SILVER_JEWELLERY_INTERFACE(6),
    GOLD_JEWELLERY_INTERFACE(446),
    DROP_VIEWER(704),
    MOTHERLODE_MINE(382, OVERLAY),
    TITHE_FARM_SACK(241, MINIGAME_OVERLAY),
    TITHE_FARM_REWARDS(236, CENTRAL),
    BLAST_FURNACE_TEMPERATURE(30, CENTRAL),
    BLAST_FURNACE_COFFER(474, MINIGAME_OVERLAY),
    WINTERTODT(396, MINIGAME_OVERLAY),
    TUTORIAL_MAKE_OVER(269),
    AVAS_DEVICES(67),
    DECANTING(582, InterfacePosition.DIALOGUE),
    BOUNTY_HUNTER_STORE(178),
    PARTY_DROP_CHEST(265),
    PARTY_DROP_CHEST_INVENTORY(266, SINGLE_TAB),
    GNOME_GLIDER(138),
    TRADE_STAGE1(335),
    TRADE_STAGE2(334),
    SLAYER_REWARDS(426),
    CLUE_SCROLL(203),
    PRICE_CHECKER(464),
    PRICE_CHECKER_INVENTORY(238, SINGLE_TAB),
    NOTIFICATION_SETTINGS(492, InterfacePosition.SINGLE_TAB),
    SKILLS_TAB(320, InterfacePosition.SKILLS_TAB),
    INVENTORY_TAB(149, InterfacePosition.INVENTORY_TAB),
    EQUIPMENT_TAB(387, InterfacePosition.EQUIPMENT_TAB),
    PRAYER_TAB_INTERFACE(541, InterfacePosition.PRAYER_TAB),
    WILDERNESS_OVERLAY(90, InterfacePosition.WILDERNESS_OVERLAY),
    TRADE_INVENTORY(336, InterfacePosition.SINGLE_TAB),
    LOOTING_BAG(81, InterfacePosition.SINGLE_TAB),
    LOGOUT(182, InterfacePosition.LOGOUT_TAB),
    SEED_BOX(128, InterfacePosition.SINGLE_TAB),
    PET_INSURANCE(148),
    TELEPORT_MENU(700),
    GAME_NOTICEBOARD(701, InterfacePosition.ACCOUNT_MANAGEMENT),
    GAME_SETTINGS(702, InterfacePosition.ACCOUNT_MANAGEMENT),
    EXPERIENCE_MODE_SELECTION(703),
    EXPERIENCE_TRACKER(137),
    EXPERIENCE_DROPS_WINDOW(122, XP_TRACKER),
    GAME_MODE_SETUP(215),
    EXPERIENCE_LAMP(134),
    HOUSE_OPTIONS_TAB(370, InterfacePosition.SINGLE_TAB),
    UNMORPH_TAB(375, InterfacePosition.SINGLE_TAB),
    FARMING_STORAGE(125),
    REGULAR_LECTERN(79),
    ARCEUUS_LECTERN(264),
    SAWMILL(403),
    FARMING_STORAGE_INVENTORY(126, SINGLE_TAB),
    LOBBY(378),
    MAKEOVER(205),
    MONSTER_EXAMINE(522, SPELLBOOK_TAB),
    HAIRDRESSER(82),
    COLLECTION_LOG(621),
    SPELLBOOK(218, SPELLBOOK_TAB),
    PEST_CONTROL_LANDER_OVERLAY(407, MINIGAME_OVERLAY),
    PEST_CONTROL_GAME_OVERLAY(408, MINIGAME_OVERLAY),
    VOID_KNIGHT_REWARDS(243),
    GRAND_EXCHANGE_HISTORY(383),
    GRAND_EXCHANGE_COLLECTION_BOX(402),
    ORBS(160, InterfacePosition.ORBS),
    MINIMIZED_ORBS(311, InterfacePosition.ORBS),
    WORLD_MAP(595, InterfacePosition.WORLD_MAP),
    DUEL_WINNINGS(108),
    DUEL_STAKING(481),
    DUEL_SETTINGS(482),
    DUEL_CONFIRMATION(476),
    DUEL_SCOREBOARD(108),
    CHAT(162, CHATBOX),
    ITEMS_KEPT_ON_DEATH(4),
    CLAN_CHAT_TAB(7, CLAN_TAB),
    BANK(12),
    BANK_PIN_SETTINGS(14),
    SHOP(300),
    BARROWS_OVERLAY(24, InterfacePosition.OVERLAY),
    BUG_REPORT_FORM(156),
    SHOP_INVENTORY(301, SINGLE_TAB),
    THESSALIA_MAKEOVER(591),
    BANK_INVENTORY(15, SINGLE_TAB),
    BANK_DEPOSIT_INTERFACE(192),
    AUTOCAST_TAB(201, InterfacePosition.COMBAT_TAB),
    COMBAT_TAB(593, InterfacePosition.COMBAT_TAB),
    SETTINGS(261, InterfacePosition.SETTINGS_TAB),
    KOUREND_FAVOUR_TAB(245, InterfacePosition.JOURNAL_TAB_HEADER),
    ACHIEVEMENT_DIARY_TAB(259, InterfacePosition.JOURNAL_TAB_HEADER),
    QUEST_TAB(399, InterfacePosition.JOURNAL_TAB_HEADER),
    MINIGAME_TAB(76, InterfacePosition.JOURNAL_TAB_HEADER),
    EMOTE_TAB(216, InterfacePosition.EMOTE_TAB),
    FRIEND_LIST_TAB(429, FRIENDS_TAB),
    IGNORE_LIST_TAB(432, FRIENDS_TAB),
    GNOME_COCKTAIL(436),
    ITEM_RETRIEVAL_SERVICE(602),
    ADVANCED_SETTINGS(60),
    BARROWS_PUZZLE(25),
    BARROWS_REWARDS(155),
    RAIDS_PRIVATE_STORAGE(271),
    QUEST_COMPLETED(277),
    RAIDS_SHARED_STORAGE(550),
    RAIDS_STORAGE_INVENTORY_INTERFACE(551, SINGLE_TAB),
    MUSIC_TAB(239, InterfacePosition.MUSIC_TAB),
    BOOK(26),
    CANOE_SHAPE(52),
    CANOE_SELECTION(57),
    WORLD_SWITCHER(69, LOGOUT_TAB),
    SHIP_DESTINATION_CHART(72),
    CLUE_SCROLL_REWARD(73),
    QUICK_PRAYERS(77, PRAYER_TAB),
    TELETAB_CREATION(79),
    EQUIPMENT_STATS(84),
    CLAN_CHAT_SETUP(94),
    FIXED_PANE(548),
    RESIZABLE_PANE(161),
    MOBILE_PANE(601),
    SIDE_PANELS_RESIZABLE_PANE(164),
    CASTLE_WARS_LOBBY_OVERLAY(131, InterfacePosition.OVERLAY),
    CASTLE_WARS_OVERLAY(58, InterfacePosition.OVERLAY),
    CASTLE_WARS_CATAPULT(54, InterfacePosition.CENTRAL),
    PURO_PURO_IMPLING_OVERLAY(157, MINIGAME_OVERLAY),
    IMPLING_TRACKER(180),
    ELNOCKS_EXCHANGE(540),
    EXPLORER_RING_ALCH(483, SPELLBOOK_TAB),
    EASTER_NOTICEBOARD(713),
    SEED_VAULT(631),
    SEED_VAULT_INVENTORY(630, SINGLE_TAB),
    MAGIC_STORAGE(592),
    POPUP(660, CENTRAL);

    public static final GameInterface[] VALUES = values();
    @Getter
    private final int id;
    @Getter
    private final InterfacePosition position;

    GameInterface(final int id) {
        this(id, CENTRAL);
    }

    GameInterface(final int id, final InterfacePosition position) {
        this.id = id;
        this.position = position;
    }

    public static final Optional<GameInterface> get(final int id) {
        val field = Utils.findMatching(VALUES, value -> value.id == id);
        if (field == null)
            return Optional.empty();
        return Optional.of(field);
    }

    public final void open(final Player player) {
        player.log(LogLevel.INFO, "Opening interface: " + toString());
        val plugin = NewInterfaceHandler.INTERFACES.get(id);
        if (plugin != null) {
            plugin.open(player);
        } else {
            player.getInterfaceHandler().sendInterface(this);
        }
    }

    public final Optional<Interface> getPlugin() {
        val plugin = NewInterfaceHandler.INTERFACES.get(id);
        if (plugin == null)
            return Optional.empty();
        return Optional.of(plugin);
    }

}
