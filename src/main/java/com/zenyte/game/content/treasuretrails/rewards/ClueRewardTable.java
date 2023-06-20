package com.zenyte.game.content.treasuretrails.rewards;

import com.google.common.collect.ImmutableList;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.item.ImmutableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.plugins.renewednpc.MonkOfEntrana;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.zenyte.game.item.ItemId.*;

/**
 * @author Kris | 24/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class ClueRewardTable {

    protected final List<ImmutableItem> firelightersTable = tableOf(
            item(RED_FIRELIGHTER, 4, 10),
            item(GREEN_FIRELIGHTER, 4, 10),
            item(BLUE_FIRELIGHTER, 4, 10),
            item(PURPLE_FIRELIGHTER, 4, 10),
            item(WHITE_FIRELIGHTER, 4, 10)
    );

    protected final List<ImmutableItem> teleportsTable = tableOf(
            item(CHARGE_DRAGONSTONE_JEWELLERY_SCROLL, 5, 15),
            item(NARDAH_TELEPORT, 5, 15),
            item(MOS_LEHARMLESS_TELEPORT, 5, 15),
            item(MORTTON_TELEPORT, 5, 15),
            item(FELDIP_HILLS_TELEPORT, 5, 15),
            item(LUNAR_ISLE_TELEPORT, 5, 15),
            item(DIGSITE_TELEPORT, 5, 15),
            item(PISCATORIS_TELEPORT, 5, 15),
            item(PEST_CONTROL_TELEPORT, 5, 15),
            item(TAI_BWO_WANNAI_TELEPORT, 5, 15),
            item(LUMBERYARD_TELEPORT, 5, 15),
            item(ELF_CAMP_TELEPORT,5,15)
    );

    protected final List<ImmutableItem> scrollbookTable = tableOf(
            item(MASTER_SCROLL_BOOK_EMPTY)
    );

    protected final List<ImmutableItem> godPagesTable = tableOf(
            item(SARADOMIN_PAGE_1),
            item(SARADOMIN_PAGE_2),
            item(SARADOMIN_PAGE_3),
            item(SARADOMIN_PAGE_4),

            item(ZAMORAK_PAGE_1),
            item(ZAMORAK_PAGE_2),
            item(ZAMORAK_PAGE_3),
            item(ZAMORAK_PAGE_4),

            item(GUTHIX_PAGE_1),
            item(GUTHIX_PAGE_2),
            item(GUTHIX_PAGE_3),
            item(GUTHIX_PAGE_4),

            item(BANDOS_PAGE_1),
            item(BANDOS_PAGE_2),
            item(BANDOS_PAGE_3),
            item(BANDOS_PAGE_4),

            item(ARMADYL_PAGE_1),
            item(ARMADYL_PAGE_2),
            item(ARMADYL_PAGE_3),
            item(ARMADYL_PAGE_4),

            item(ANCIENT_PAGE_1),
            item(ANCIENT_PAGE_2),
            item(ANCIENT_PAGE_3),
            item(ANCIENT_PAGE_4)
    );

    protected final List<ImmutableItem> blessingsTable = tableOf(
            item(HOLY_BLESSING),
            item(UNHOLY_BLESSING),
            item(PEACEFUL_BLESSING),
            item(WAR_BLESSING),
            item(HONOURABLE_BLESSING),
            item(ANCIENT_BLESSING)
    );

    @Getter private final List<ImmutableItem> rewards = new ObjectArrayList<>();
    @Getter private final List<ImmutableItem> entranaRewards = new ObjectArrayList<>();


    private int totalWeight = 0, totalEntranaWeight = 0;

    public void calculate() {
        assert totalWeight == 0 : "Table has already been calculated.";
        defineTables();
        for (val reward : rewards) {
            totalWeight += (int) reward.getRate();
        }
        for (val reward : entranaRewards) {
            totalEntranaWeight += (int) reward.getRate();
        }
    }

    protected abstract void defineTables();

    protected void appendTable(final int rarity, final List<ImmutableItem> table) {
        val percDecrease = rarity > 200_000 ? 75 : rarity > 5_000 ? 50 : rarity > 500 ? 10 : 0;
        val adjustedRarity = (int) (rarity * (1 - (percDecrease / 100F)));
        val probability = 1D / adjustedRarity;//Get the probability of hitting this table.
        val weight = (int) (1_000_000 * probability);//Calculate a weight out of the probability, scaled to a weight of a million.
        for (val item : table) {
            rewards.add(new ImmutableItem(item.getId(), item.getMinAmount(), item.getMaxAmount(), weight));
            if (!MonkOfEntrana.isForbiddenOnEntrana(new Item(item.getId()))) {
                entranaRewards.add(new ImmutableItem(item.getId(), item.getMinAmount(), item.getMaxAmount(), weight));
            }
        }
    }

    protected abstract int minRolls();
    protected abstract int maxRolls();

    public final List<Item> roll(final boolean entrana) {
        return roll(minRolls(), maxRolls(), entrana);
    }

    public final List<Item> roll(final int min, final int max, final boolean entrana) {
        assert min <= max;
        val loot = new ObjectArrayList<Item>(max);
        val count = Utils.random(min, max);
        for (int i = 0; i < count; i++) {
            val roll = Utils.random(entrana ? totalEntranaWeight : totalWeight);
            int current = 0;
            for (val item : (entrana ? entranaRewards : rewards)) {
                if ((current += (int) item.getRate()) >= roll) {
                    val amount = Utils.random(item.getMinAmount(), item.getMaxAmount());
                    loot.add(new Item(item.getId(), amount));
                    if (item.getId() == ItemDefinitions.getOrThrow(SUPER_ATTACK4).getNotedOrDefault()) {
                        loot.add(new Item(ItemDefinitions.getOrThrow(SUPER_STRENGTH4).getNotedOrDefault(), amount));
                        loot.add(new Item(ItemDefinitions.getOrThrow(SUPER_DEFENCE4).getNotedOrDefault(), amount));
                    }
                    break;
                }
            }
        }
        val masterScrollRate = getMasterScrollRate();
        if (masterScrollRate > 0 && Utils.random(masterScrollRate - 1) == 0) {
            loot.add(new Item(ClueItem.MASTER.getScrollBox()));
        }
        return loot;
    }

    protected abstract int getMasterScrollRate();

    public void print() {
        for (val reward : rewards) {
            System.err.println(reward.getId() + ", " + ItemDefinitions.getOrThrow(reward.getId()).getName() + ", " + (1D / (reward.getRate() / totalWeight)));
        }
    }

    protected final ImmutableItem item(final int id) {
        return item(id, 1);
    }

    protected final ImmutableItem item(final int id, final int amount) {
        return item(id, amount, amount);
    }

    protected final ImmutableItem item(final int id, final int minAmount, final int maxAmount) {
        assert minAmount <= maxAmount;
        assert minAmount >= 1;
        return new ImmutableItem(id, minAmount, maxAmount);
    }

    protected final ImmutableList<ImmutableItem> tableOf(@NotNull final ImmutableItem... items) {
        return ImmutableList.<ImmutableItem>builder().add(items).build();
    }

    protected final ImmutableItem notedItem(final int id, final int amount) {
        return notedItem(id, amount, amount);
    }

    protected final ImmutableItem notedItem(final int id, final int minAmount, final int maxAmount) {
        assert minAmount <= maxAmount;
        assert minAmount >= 1;
        val notedId = ItemDefinitions.getOrThrow(id).getNotedOrDefault();
        return new ImmutableItem(notedId, minAmount, maxAmount);
    }

}
