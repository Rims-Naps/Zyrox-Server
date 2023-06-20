package com.zenyte.game.content.skills.prayer.ectofuntus;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.skills.prayer.actions.Ashes;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.Skills;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author Kris | 24/06/2019 13:04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AshSanctifier extends ItemPlugin implements PairedItemOnItemPlugin {

    @Getter
    @RequiredArgsConstructor
    public enum CrusherType {
        ASH_SANCTIFIER(player -> player.getInventory().containsItem(ItemId.ASH_SANCTIFIER, 1), (player, ash) -> {
            if (!enabled(player) || getCharges(player) <= 0) {
                return false;
            }
            applyExperience(player, ash);
            return true;
        });

        private final Predicate<Player> predicate;
        private final CrushEffect effect;

        private static final CrusherType[] values = values();

        public static CrusherType get(final Player player) {
            for (val type : values) {
                if (!type.getPredicate().test(player)) {
                    continue;
                }
                return type;
            }
            return null;
        }

        public interface CrushEffect {
            boolean crush(final Player player, final Ashes ash);
        }
    }

    public static final int getCharges(@NotNull final Player player) {
        return player.getNumericAttribute("ashsanctifier charges").intValue();
    }

    public static final void addCharges(@NotNull final Player player, final int amount) {
        player.addAttribute("ashsanctifier charges", getCharges(player) + amount);
    }

    public static final boolean enabled(@NotNull final Player player) {
        return !player.getBooleanSetting(Setting.ASH_SANCTIFIER_INACTIVE);
    }

    private static final void applyExperience(@NotNull final Player player, final Ashes ash) {
        addCharges(player, -1);
        player.getSkills().addXp(Skills.PRAYER, (DiaryReward.RADAS_BLESSING4.eligibleFor(player) ? 1F : 0.5F) * ash.getXp());
    }

    @Override
    public void handle() {
        bind("Check", (player, item, container, slotId) -> {
            val charges = player.getNumericAttribute("ashsanctifier charges").intValue();
            player.sendMessage("Your ash sanctifier has " + charges + " charge" + (charges == 1 ? "" : "s") + " remaining.");
        });
        bind("Uncharge", (player, item, container, slotId) -> {
            val charges = getCharges(player);
            val deathRunes = (int) (charges / 10F);
            if (deathRunes > 0) {
                player.getInventory().addOrDrop(new Item(ItemId.DEATH_RUNE, deathRunes));
                player.sendMessage("You uncharge the ash sanctifier and receive " + deathRunes + " death runes.");
            } else {
                player.sendMessage("Your ash sanctifier has no charges remaining.");
            }
            addCharges(player, -charges);
        });
        bind("Activity", (player, item, container, slotId) -> {
            player.getSettings().toggleSetting(Setting.ASH_SANCTIFIER_INACTIVE);
            player.sendMessage(!enabled(player) ? "Your ash sanctifier is no longer scattering ashes." : "Your ash sanctifier is now scattering ashes.");
        });
    }

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val deathRunes = from.getId() == ItemId.DEATH_RUNE ? from : to;
        int eligibleCharges = deathRunes.getAmount() * 10;
        val sanctifierCharges = getCharges(player);
        if (sanctifierCharges + eligibleCharges < 0) {
            eligibleCharges = Integer.MAX_VALUE - sanctifierCharges;
            eligibleCharges -= eligibleCharges % 10;
        }
        if (eligibleCharges <= 0) {
            player.sendMessage("Your ash sanctifier can't hold anymore charges.");
            return;
        }
        player.getInventory().deleteItem(new Item(deathRunes.getId(), eligibleCharges / 10));
        addCharges(player, eligibleCharges);
        player.sendMessage("You add " + eligibleCharges + " charges in your ash sanctifier. It now holds " + getCharges(player) + " charges total.");
    }

    @Override
    public int[] getItems() {
        return new int[]{ItemId.ASH_SANCTIFIER};
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[]{
                ItemPair.of(ItemId.DEATH_RUNE, ItemId.ASH_SANCTIFIER)
        };
    }
}
