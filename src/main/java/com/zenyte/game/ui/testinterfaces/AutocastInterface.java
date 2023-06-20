package com.zenyte.game.ui.testinterfaces;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.content.skills.magic.SpellState;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.items.ItemDefinitions;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

import static com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell.*;

/**
 * @author Kris | 20/10/2018 23:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AutocastInterface extends Interface {

    public static final EnumDefinitions AUTOCASTABLE_SPELLS_ENUM = EnumDefinitions.get(1986);

    private static final int VAR_AUTOCAST_PAGE = 664;
    private static final CombatSpell[] NORMAL_AUTOCASTABLE_SPELLS = new CombatSpell[]{
            WIND_STRIKE, WATER_STRIKE, EARTH_STRIKE, FIRE_STRIKE,
            WIND_BOLT, WATER_BOLT, EARTH_BOLT, FIRE_BOLT,
            WIND_BLAST, WATER_BLAST, EARTH_BLAST, FIRE_BLAST,
            WIND_WAVE, WATER_WAVE, EARTH_WAVE, FIRE_WAVE,
            WIND_SURGE, WATER_SURGE, EARTH_SURGE, FIRE_SURGE
    };
    private static final CombatSpell[] ANCIENT_AUTOCASTABLE_SPELLS = new CombatSpell[]{
            SMOKE_RUSH, SHADOW_RUSH, BLOOD_RUSH, ICE_RUSH,
            SMOKE_BURST, SHADOW_BURST, BLOOD_BURST, ICE_BURST,
            SMOKE_BLITZ, SHADOW_BLITZ, BLOOD_BLITZ, ICE_BLITZ,
            SMOKE_BARRAGE, SHADOW_BARRAGE, BLOOD_BARRAGE, ICE_BARRAGE
    };

    @Override
    protected void attach() {
        put(1, 0, "Close");
        put(1, "Set autocast spell");
    }

    @Override
    public void open(Player player) {
        val page = AutocastPage.getPage(player);
        if (!page.isPresent()) {
            player.sendMessage("You can't choose a spell to autocast with that combination of weapon and spellbook.");
            return;
        }
        player.getInterfaceHandler().sendInterface(getInterface());
        player.getVarManager().sendVar(VAR_AUTOCAST_PAGE, page.get().baseItemId);
        player.getPacketDispatcher().sendComponentSettings(id, getComponent("Set autocast spell"), 0,
                AUTOCASTABLE_SPELLS_ENUM.getLargestIntValue(),
                AccessMask.CLICK_OP1);
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        if (replacement.isPresent())
            return;
        GameInterface.COMBAT_TAB.open(player);
    }

    @Override
    protected void build() {
        bind("Close", player -> close(player));
        bind("Set autocast spell", ((player, slotId, itemId, option) -> {
            val spellItemId = AUTOCASTABLE_SPELLS_ENUM.getIntValue(slotId);
            val definitions = ItemDefinitions.get(spellItemId);
            val params = definitions.getParameters();
            if (params == null)
                throw new RuntimeException("Spell item parameters are null!");

            val name = (String) params.get(601);
            val playerSpellbook = player.getCombatDefinitions().getSpellbook();
            val optionalCombatSpell = Magic.getCombatSpell(playerSpellbook, name.replaceAll("-", "").toLowerCase());
            if (!optionalCombatSpell.isPresent()) {
                return;
            }
            val spell = optionalCombatSpell.get();
            val spellbook = spell.getSpellbook();
            val optionalAutocastPage = AutocastPage.getPage(player);
            if (player.getCombatDefinitions().getSpellbook() != spellbook || !optionalAutocastPage.isPresent()) {
                player.sendMessage("You cannot autocast that spell on this spellbook.");
                return;
            }

            val page = optionalAutocastPage.get();
            if (!page.spells.contains(spell)) {
                switch (spell) {
                    case IBAN_BLAST:
                        player.sendMessage("You cannot autocast this spell without an Iban's staff.");
                        return;
                    case MAGIC_DART:
                        player.sendMessage("You cannot autocast this spell without a Slayer's staff.");
                        return;
                    case FLAMES_OF_ZAMORAK:
                        player.sendMessage("You cannot autocast this spell without a Zamorak staff.");
                        return;
                    case CLAWS_OF_GUTHIX:
                        player.sendMessage("You cannot autocast this spell without a Guthix staff.");
                        return;
                    case SARADOMIN_STRIKE:
                        player.sendMessage("You cannot autocast this spell without a Saradomin staff.");
                        return;
                    default:
                        throw new IllegalArgumentException("Unhandled exception for spell " + spell + " on spellbook " + spellbook + ".");
                }
            }

            val state = new SpellState(player, spell.getLevel(), spell.getRunes());
            player.getInterfaceHandler().sendInterface(GameInterface.COMBAT_TAB);
            if (!state.check(true)) {
                player.getCombatDefinitions().setAutocastSpell(null);
                return;
            }
            player.getCombatDefinitions().setAutocastSpell(spell);
            if (player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
                player.sendMessage("Autocast spell set to: " + Utils.formatString(spell.toString().toLowerCase()) + ".");
            }
            player.getActionManager().forceStop();
        }));
    }

    public static final boolean canCast(@NotNull final Player player, @NotNull final CombatSpell spell) {
        val spellbook = spell.getSpellbook();
        val optionalAutocastPage = AutocastPage.getPage(player);
        if (player.getCombatDefinitions().getSpellbook() != spellbook || !optionalAutocastPage.isPresent()) {
            return false;
        }

        val page = optionalAutocastPage.get();
        if (!page.spells.contains(spell)) {
            return false;
        }

        return new SpellState(player, spell.getLevel(), spell.getRunes()).check(true);
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.AUTOCAST_TAB;
    }

    @AllArgsConstructor
    private enum AutocastPage {

        ANCIENT(4675, new EnumBuilder<CombatSpell>().add(ANCIENT_AUTOCASTABLE_SPELLS).build(), (player, name) -> onSpellbook(player, Spellbook.ANCIENT) && (name.equals("Master wand") || name.equals("Zuriel's staff") || name.equals("Kodai wand") || name.equals("Ancient staff") || player.getCombatDefinitions().hasFullAhrimsAndDamned())),
        IBANS_STAFF(1409, new EnumBuilder<CombatSpell>().add(NORMAL_AUTOCASTABLE_SPELLS).add(IBAN_BLAST).build(), (player, name) -> onSpellbook(player, Spellbook.NORMAL) && (name.equals("Iban's staff") || name.equals("Iban's staff (u)"))),
        SLAYERS_STAFF(4170, new EnumBuilder<CombatSpell>().add(NORMAL_AUTOCASTABLE_SPELLS).add(CRUMBLE_UNDEAD, MAGIC_DART).remove(WIND_STRIKE, WATER_STRIKE, EARTH_STRIKE, FIRE_STRIKE, WIND_BOLT, WATER_BOLT, EARTH_BOLT, FIRE_BOLT, WIND_BLAST, WATER_BLAST, EARTH_BLAST, FIRE_BLAST).build(), (player, name) -> onSpellbook(player, Spellbook.NORMAL) && (name.equals("Slayer's staff") || name.equals("Slayer's staff (e)"))),
        ZAMORAK_STAFF(11791, new EnumBuilder<CombatSpell>().add(NORMAL_AUTOCASTABLE_SPELLS).add(CRUMBLE_UNDEAD, MAGIC_DART, FLAMES_OF_ZAMORAK).build(), (player, name) -> onSpellbook(player, Spellbook.NORMAL) && (name.equals("Staff of the dead") || name.equals("Toxic staff of the dead") || name.equals("Zamorak staff"))),
        GUTHIX_STAFF(8841, new EnumBuilder<CombatSpell>().add(NORMAL_AUTOCASTABLE_SPELLS).add(CRUMBLE_UNDEAD, CLAWS_OF_GUTHIX).remove(WIND_STRIKE, WATER_STRIKE, EARTH_STRIKE, FIRE_STRIKE, WIND_BOLT, WATER_BOLT, EARTH_BOLT, FIRE_BOLT, WIND_BLAST, WATER_BLAST, EARTH_BLAST, FIRE_BLAST).build(), (player, name) -> onSpellbook(player, Spellbook.NORMAL) && (name.equals("Void knight mace") || name.equals("Guthix staff"))),
        SARADOMIN_STAFF(22296, new EnumBuilder<CombatSpell>().add(NORMAL_AUTOCASTABLE_SPELLS).add(CRUMBLE_UNDEAD, MAGIC_DART, SARADOMIN_STRIKE).build(), (player, name) -> onSpellbook(player, Spellbook.NORMAL) && (name.equals("Saradomin staff") || name.equals("Staff of light"))),
        REGULAR(-1, new EnumBuilder<CombatSpell>().add(NORMAL_AUTOCASTABLE_SPELLS).build(), (player, name) -> onSpellbook(player, Spellbook.NORMAL) && !(name.equals("Ancient staff") || name.equals("Zuriel's staff")));

        private static final AutocastPage[] values = values();
        private final int baseItemId;
        private final ImmutableSet<CombatSpell> spells;
        private final BiPredicate<Player, String> predicate;

        /**
         * Checks whether the player is on the spellbook requested.
         *
         * @param player    the player whose spellbook to check.
         * @param spellbook the spellbook to test.
         * @return whether the player is on this spellbook or not.
         */
        private static boolean onSpellbook(final Player player, final Spellbook spellbook) {
            return player.getCombatDefinitions().getSpellbook().equals(spellbook);
        }

        /**
         * Gets the autocast page that should be opened based on player's spellbook and held weapon.
         *
         * @param player the player who's opening the interface.
         * @return an optional autocast page, or empty is absent.
         */
        private static final Optional<AutocastPage> getPage(final Player player) {
            val weapon = player.getEquipment().getItem(EquipmentSlot.WEAPON.getSlot());
            if (weapon == null) {
                return Optional.empty();
            }
            val name = weapon.getName();
            for (val page : values) {
                if (page.predicate.test(player, name))
                    return Optional.of(page);
            }
            return Optional.empty();
        }

        /**
         * An immutable enumset builder of a generic type.
         *
         * @param <E> the generic type, must be an instance of an enum.
         */
        private static final class EnumBuilder<E extends Enum> {

            /**
             * The set used in building process of the enum.
             */
            private final Set<E> set = new HashSet<>();

            /**
             * Adds an array of values to the set if absent.
             *
             * @param e the value to enqueue to the set.
             * @return this builder for chaining.
             */
            private EnumBuilder<E> add(final E... e) {
                for (int i = e.length - 1; i >= 0; i--) {
                    set.add(e[i]);
                }
                return this;
            }

            /**
             * Removes an array of values to the set if prevent.
             *
             * @param e the value to remove from the set.
             * @return this builder for chaining.
             */
            private EnumBuilder<E> remove(final E... e) {
                for (int i = e.length - 1; i >= 0; i--) {
                    set.remove(e[i]);
                }
                return this;
            }

            /**
             * Builds the immutable set.
             *
             * @return immutable set of the generic type.
             */
            private ImmutableSet<E> build() {
                return Sets.immutableEnumSet(set);
            }

        }

    }
}
