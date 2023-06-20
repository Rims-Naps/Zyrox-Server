package com.zenyte.game.content.skills.magic.lecterns;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 03/09/2019 07:30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RegularLecternInterface extends Interface {

    @Override
    protected void attach() {
        put(9, "Enchant onyx");
        put(4, "Enchant emerald");
        put(8, "Enchant dragonstone");
        put(3, "Enchant sapphire");
        put(6, "Enchant ruby");
        put(7, "Enchant diamond");

        put(12, "Lumbridge teleport");
        put(13, "Falador teleport");
        put(11, "Varrock teleport");
        put(16, "Watchtower teleport");
        put(15, "Ardougne teleport");
        put(14, "Camelot teleport");
        put(17, "House teleport");
        put(5, "Bones to bananas");
        put(10, "Bones to peaches");
    }

    @Override
    public void open(final Player player) {
        for (int i = 0; i < 2; i++) {
            player.getVarManager().sendVar(261 + i, 3);
        }
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    protected void build() {
        bind("Enchant onyx", (player, slotId, itemId, option) -> create(player, RegularTablet.ONYX_ENCHANTMENT, option));
        bind("Enchant emerald", (player, slotId, itemId, option) -> create(player, RegularTablet.EMERALD_ENCHANTMENT, option));
        bind("Enchant dragonstone", (player, slotId, itemId, option) -> create(player, RegularTablet.DRAGONSTONE_ENCHANTMENT, option));
        bind("Enchant sapphire", (player, slotId, itemId, option) -> create(player, RegularTablet.SAPPHIRE_ENCHANTMENT, option));
        bind("Enchant ruby", (player, slotId, itemId, option) -> create(player, RegularTablet.RUBY_ENCHANTMENT, option));
        bind("Enchant diamond", (player, slotId, itemId, option) -> create(player, RegularTablet.DIAMOND_ENCHANTMENT, option));

        bind("Lumbridge teleport", (player, slotId, itemId, option) -> create(player, RegularTablet.LUMBRIDGE_TELEPORT, option));
        bind("Falador teleport", (player, slotId, itemId, option) -> create(player, RegularTablet.FALADOR_TELEPORT, option));
        bind("Varrock teleport", (player, slotId, itemId, option) -> create(player, RegularTablet.VARROCK_TELEPORT, option));
        bind("Watchtower teleport", (player, slotId, itemId, option) -> create(player, RegularTablet.WATCHTOWER_TELEPORT, option));
        bind("Ardougne teleport", (player, slotId, itemId, option) -> create(player, RegularTablet.ARDOUGNE_TELEPORT, option));
        bind("Camelot teleport", (player, slotId, itemId, option) -> create(player, RegularTablet.CAMELOT_TELEPORT, option));
        bind("House teleport", (player, slotId, itemId, option) -> create(player, RegularTablet.TELEPORT_TO_HOUSE, option));
        bind("Bones to bananas", (player, slotId, itemId, option) -> create(player, RegularTablet.BONES_TO_BANANAS, option));
        bind("Bones to peaches", (player, slotId, itemId, option) -> create(player, RegularTablet.BONES_TO_PEACHES, option));
    }

    private final void create(@NotNull final Player player, @NotNull final LecternTablet tablet, final int option) {
        player.getInterfaceHandler().closeInterface(getInterface());
        if (option == 4) {
            player.sendInputInt("How many tablets would you like to craft?", value -> player.getActionManager().setAction(new TabletCreation(tablet, value)));
            return;
        }
        player.getActionManager().setAction(new TabletCreation(tablet, option == 2 ? 5 : option == 3 ? 28 : 1));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.REGULAR_LECTERN;
    }

}
