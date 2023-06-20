package com.zenyte.game.content.wheeloffortune;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.plugins.events.InitializationEvent;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Tommeh | 27/02/2020 | 20:57
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 *
 * @author Cresinkel
 * Was never used by previous devs
 * Reused code in function of Well of goodwill
 * Added onto it
 *
 */
public class WheelOfFortune {

    private transient Player player;

    @Getter
    private Container container;
    @Setter
    @Getter
    private int spins;
    @Setter
    @Getter
    private boolean pendingReward;

    public WheelOfFortune(final Player player) {
        this.player = player;
        container = new Container(ContainerPolicy.NORMAL, ContainerType.SHOP, Optional.of(player));
    }

    @Subscribe
    public static final void onInitialization(final InitializationEvent event) {
        val player = event.getPlayer();
        val savedPlayer = event.getSavedPlayer();
        val wheel = savedPlayer.getWheelOfFortune();
        if (wheel == null) {
            return;
        }
        val thisWheel = player.getWheelOfFortune();
        if (wheel.container != null) {
            thisWheel.container.setContainer(wheel.container);
        }
        thisWheel.spins = wheel.spins;
        thisWheel.pendingReward = wheel.pendingReward;
    }

    public void roll() {
        container.clear();
        while (container.getSize() < 24) {
            val item = WheelOfFortune.generateItem();
            if (!container.contains(item.getId(), 1)) {
                container.add(new Item(item.getId(), 1));
            }
        }
        World.sendMessage(MessageType.FILTERABLE, "number of items: " + container.getSize());
    }

    public void spin() {
        if (spins <= 0) {
            player.sendMessage("You don't have enough spins to do that right now.");
            return;
        }
        spins--;
        pendingReward = true;
        refreshSpins();
    }

    public void claim(final boolean bank) {
        if (!pendingReward) {
            return;
        }
        val prize = getPrize();
        if (prize == null) {
            return;
        }
        if (bank) {
            if (!player.getBank().checkSpace()) {
                return;
            }
            pendingReward = false;
            GameInterface.WHEEL_OF_FORTUNE.open(player);
            player.getBank().add(prize);
        } else {
            if (!player.getInventory().checkSpace()) {
                return;
            }
            pendingReward = false;
            GameInterface.WHEEL_OF_FORTUNE.open(player);
            player.getInventory().addItem(prize);
        }
    }

    public void refreshSpins() {
        player.getVarManager().sendVar(3622, spins);
    }

    @NonNull
    public Item getPrize() {
        return container.get(15);
    }

    @NotNull
    private static final Item generateItem() {
        final int random = Utils.random(99);
        if (random == 0) {
            return new Item(ExtremelyRare.generate(), 1);
        } else if (random < 20) {
            return new Item(SuperRare.generate(), 1);
        } else {
            return new Item(Rare.generate(), 1);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ExtremelyRare { //make sure items have an examine

        INFERNO_SNAKE_PET(30003, 1),
        GOBLIN_PET(ItemId.CAVE_GOBLIN, 1);

        private final int id,weight;
        public static final WheelOfFortune.ExtremelyRare[] values = values();
        private static int total;
        static {
            for (val reward : values) {
                total += reward.weight;
            }
        }
        private static final int generate() {
            val random = Utils.random(total);
            int current = 0;
            for (val it : values) {
                if ((current += it.weight) >= random) {
                    return it.getId();
                }
            }
            return -1;
        }
    }
    @Getter
    @AllArgsConstructor
    public enum SuperRare {
        f(ItemId.RUNITE_ORE, 1),
        d(ItemId.ADAMANTITE_ORE, 1),
        z(ItemId.IRON_ORE, 1),
        o(ItemId.MITHRIL_ORE, 1),
        A(ItemId.AMETHYST, 1),
        D(ItemId.EMERALD, 1),
        S(ItemId.SAPPHIRE, 1),
        RUBY(ItemId.RUBY, 1),
        DRAGON_KITESHIELD(ItemId.ONYX, 1),
        DRAGON_PLATEBODY(ItemId.ZENYTE, 1);

        private final int id, weight;
        public static final WheelOfFortune.SuperRare[] values = values();
        private static int total;
        static {
            for (val reward : values) {
                total += reward.weight;
            }
        }
        private static final int generate() {
            val random = Utils.random(total);
            int current = 0;
            for (val it : values) {
                if ((current += it.weight) >= random) {
                    return it.getId();
                }
            }
            return -1;
        }
    }
    @Getter
    @AllArgsConstructor
    public enum Rare {
        P(ItemId.RANARR_SEED, 1),
        M(ItemId.RAKE, 1),
        O(ItemId.RAW_BIRD_MEAT, 1),
        I(ItemId.RANGER_GLOVES, 1),
        U(ItemId.RANGING_CAPE, 1),
        Y(ItemId.RANGERS_TIGHTS, 1),
        T(ItemId.RAIN_BOW, 1),
        G(ItemId.RADAS_BLESSING_1, 1),
        H(ItemId.RADAS_BLESSING_3, 1),
        L(ItemId.RADAS_BLESSING_2, 1),
        F(ItemId.RADAS_BLESSING_4, 1),
        Dd(ItemId.RADAS_JOURNEY, 1),
        Z(ItemId.RAINBOW_SCARF, 1),
        N(ItemId.SARADOMIN_DHIDE_BOOTS, 1),
        Q(ItemId.SARADOMIN_DHIDE, 1),
        B(ItemId.SARADOMIN_MAX_CAPE, 1),
        V(ItemId.SARADOMIN_COIF, 1),
        E(ItemId.SARADOMIN_CHAPS, 1),
        J(ItemId.SARADOMIN_ROBE_LEGS, 1),
        p(ItemId.TINY_ELF_CRYSTAL, 1),
        m(ItemId.CADANTINE_SEED, 1),
        o(ItemId.CADANTINE, 1),
        i(ItemId.CADANTINE_BLOOD_POTION_UNF, 1),
        u(ItemId.BLOOD_RUNE, 1),
        y(ItemId.BLOOD_SHARD, 1),
        t(ItemId.BODY_RUNE, 1),
        g(ItemId.ZAMORAK_MAX_CAPE, 1),
        h(ItemId.OPAL_MACHETE, 1),
        l(ItemId.MEDIUM_POUCH, 1),
        f(ItemId.VERACS_BRASSARD, 1),
        d(ItemId.CELASTRUS_SEED, 1),
        z(ItemId.FEATHER, 1),
        n(ItemId.GILDED_SPADE, 1),
        A(ItemId.ASSEMBLER_MAX_CAPE, 1),
        D(ItemId.RESEARCH_NOTES, 1),
        S(ItemId.ARCHERY_TARGET, 1),
        RUBY(ItemId.FARMERS_FORK, 1),
        DRAGON_KITESHIELD(ItemId.FISH_PIE, 1),
        DRAGON_PLATEBODY(ItemId.AMYLASE_CRYSTAL, 1);

        private final int id, weight;
        public static final WheelOfFortune.Rare[] values = values();
        private static int total;
        static {
            for (val reward : values) {
                total += reward.weight;
            }
        }
        private static final int generate() {
            val random = Utils.random(total);
            int current = 0;
            for (val it : values) {
                if ((current += it.weight) >= random) {
                    return it.getId();
                }
            }
            return -1;
        }
    }
}
