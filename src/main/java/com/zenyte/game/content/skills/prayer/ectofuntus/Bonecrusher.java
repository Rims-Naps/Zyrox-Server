package com.zenyte.game.content.skills.prayer.ectofuntus;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.skills.prayer.actions.Bones;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author Kris | 24/06/2019 13:04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Bonecrusher extends ItemPlugin implements PairedItemOnItemPlugin {

    @Getter
    @RequiredArgsConstructor
    public enum CrusherType {
        BONECRUSHER_NECKLACE(player -> player.getInventory().containsItem(ItemId.BONECRUSHER_NECKLACE, 1) ||
                player.getEquipment().getId(EquipmentSlot.AMULET) == ItemId.BONECRUSHER_NECKLACE, (player, bone, burying) -> {
            if (burying) {
                if (getCharges(player) <= 0) {
                    return false;
                }
                restorePrayer(player, bone);
                return true;
            }
            if (!enabled(player) || getCharges(player) <= 0) {
                return false;
            }
            if (player.inArea("Catacombs of Kourend") || (player.getNumericTemporaryAttribute("bonecrusher_necklace_effect_delay").longValue() <= Utils.currentTimeMillis() && player.getEquipment().getId(EquipmentSlot.AMULET) == ItemId.BONECRUSHER_NECKLACE) || player.getEquipment().getId(EquipmentSlot.AMULET) == ItemId.DRAGONBONE_NECKLACE) {
                restorePrayer(player, bone);
            }
            applyExperience(player, bone);
            return true;
        }),
        BONECRUSHER(player -> player.getInventory().containsItem(ItemId.BONECRUSHER, 1), (player, bone, burying) -> {
            if (!enabled(player) || getCharges(player) <= 0) {
                return false;
            }
            if (player.inArea("Catacombs of Kourend") || player.getEquipment().getId(EquipmentSlot.AMULET) == ItemId.DRAGONBONE_NECKLACE) {
                restorePrayer(player, bone);
            }
            applyExperience(player, bone);
            return true;
        }),
        DRAGONBONE_NECKLACE(player -> player.getEquipment().getId(EquipmentSlot.AMULET) == ItemId.DRAGONBONE_NECKLACE, (player, bone, burying) -> {
            restorePrayer(player, bone);
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
            boolean crush(final Player player, final Bones bone, final boolean burying);
        }
    }

    public static final int getCharges(@NotNull final Player player) {
        return player.getNumericAttribute("bonecrusher charges").intValue();
    }

    public static final void addCharges(@NotNull final Player player, final int amount) {
        player.addAttribute("bonecrusher charges", getCharges(player) + amount);
    }

    public static final boolean enabled(@NotNull final Player player) {
        return !player.getBooleanSetting(Setting.BONECRUSHING_INACTIVE);
    }

    private static final void applyExperience(@NotNull final Player player, final Bones bone) {
        addCharges(player, -1);
        player.getSkills().addXp(Skills.PRAYER, (DiaryReward.MORYTANIA_LEGS4.eligibleFor(player) ? 1F : 0.5F) * bone.getXp());
    }

    public static final void restorePrayer(@NotNull final Player player, final Bones bone) {
        if (bone.equals(Bones.BONES)) {
            player.getPrayerManager().restorePrayerPoints(1);
        } else if (bone.equals(Bones.BIG_BONES)) {
            player.getPrayerManager().restorePrayerPoints(2);
        } else if (bone.equals(Bones.DRAGON_BONES) || bone.equals(Bones.WYRM_BONES) || bone.equals(Bones.DRAKE_BONES) || bone.equals(Bones.HYDRA_BONES)) {
            player.getPrayerManager().restorePrayerPoints(4);
        } else if (bone.equals(Bones.SUPERIOR_DRAGON_BONES)) {
            player.getPrayerManager().restorePrayerPoints(5);
        }
    }

    @Override
    public void handle() {
        bind("Check", (player, item, container, slotId) -> {
            val charges = player.getNumericAttribute("bonecrusher charges").intValue();
            player.sendMessage("Your bonecrusher has " + charges + " charge" + (charges == 1 ? "" : "s") + " remaining.");
        });
        bind("Uncharge", (player, item, container, slotId) -> {
            val charges = getCharges(player);
            val ectotokens = (int) (charges / 25F);
            if (ectotokens > 0) {
                player.getInventory().addOrDrop(new Item(4278, ectotokens));
                player.sendMessage("You uncharge the bonecrusher and receive " + ectotokens + " ecto-tokens.");
            } else {
                player.sendMessage("Your bonecrusher has no charges remaining.");
            }
            addCharges(player, -charges);
        });
        bind("Activity", (player, item, container, slotId) -> {
            player.getSettings().toggleSetting(Setting.BONECRUSHING_INACTIVE);
            player.sendMessage(!enabled(player) ? "Your bonecrusher is no longer crushing bones." : "Your bonecrusher is now crushing bones.");
        });
    }

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val tokens = from.getId() == 4278 ? from : to;
        int eligibleCharges = tokens.getAmount() * 25;
        val crusherCharges = getCharges(player);
        if (crusherCharges + eligibleCharges < 0) {
            eligibleCharges = Integer.MAX_VALUE - crusherCharges;
            eligibleCharges -= eligibleCharges % 25;
        }
        if (eligibleCharges <= 0) {
            player.sendMessage("Your bonecrusher can't hold anymore charges.");
            return;
        }
        player.getInventory().deleteItem(new Item(tokens.getId(), eligibleCharges / 25));
        addCharges(player, eligibleCharges);
        player.sendMessage("You add " + eligibleCharges + " charges in your bonecrusher. It now holds " + getCharges(player) + " charges total.");
    }

    @Override
    public int[] getItems() {
        return new int[]{ItemId.BONECRUSHER};
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[]{
                ItemPair.of(ItemId.ECTOTOKEN, ItemId.BONECRUSHER)
        };
    }
}
