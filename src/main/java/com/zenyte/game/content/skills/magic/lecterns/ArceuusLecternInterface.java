package com.zenyte.game.content.skills.magic.lecterns;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 03/09/2019 08:17
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ArceuusLecternInterface extends Interface {
    @Override
    protected void attach() {
        put(25, "Lumbridge graveyard");
        put(27, "Draynor manor");
        put(29, "Battlefront");
        put(31, "Mind altar");
        put(33, "Salve graveyard");
        put(35, "Fenkenstrains castle");
        put(37, "West ardougne");
        put(39, "Harmony island");
        put(41, "Wilderness cemetery");
        put(43, "Barrows");
        put(44, "Ape atoll");
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    protected void build() {
        bind("Lumbridge graveyard", (player, slotId, itemId, option) -> create(player, ArceuusTablet.LUMBRIDGE_GRAVEYARD, option));
        bind("Draynor manor", (player, slotId, itemId, option) -> create(player, ArceuusTablet.DRAYNOR_MANOR, option));
        bind("Battlefront", (player, slotId, itemId, option) -> create(player, ArceuusTablet.BATTLEFRONT, option));
        bind("Mind altar", (player, slotId, itemId, option) -> create(player, ArceuusTablet.MIND_ALTAR, option));
        bind("Salve graveyard", (player, slotId, itemId, option) -> create(player, ArceuusTablet.SALVE_GRAVEYARD, option));
        bind("Fenkenstrains castle", (player, slotId, itemId, option) -> create(player, ArceuusTablet.FENKENSTRAINS_CASTLE, option));
        bind("West ardougne", (player, slotId, itemId, option) -> create(player, ArceuusTablet.WEST_ARDOUGNE, option));
        bind("Harmony island", (player, slotId, itemId, option) -> create(player, ArceuusTablet.HARMONY_ISLAND, option));
        bind("Wilderness cemetery", (player, slotId, itemId, option) -> create(player, ArceuusTablet.WILDERNESS_CEMETERY, option));
        bind("Barrows", (player, slotId, itemId, option) -> create(player, ArceuusTablet.BARROWS, option));
        bind("Ape atoll", (player, slotId, itemId, option) -> create(player, ArceuusTablet.APE_ATOLL, option));
    }

    private final void create(@NotNull final Player player, @NotNull final LecternTablet tablet, final int option) {
        player.getInterfaceHandler().closeInterface(getInterface());
        if (option == 3) {
            player.sendInputInt("How many tablets would you like to craft?", value -> player.getActionManager().setAction(new TabletCreation(tablet, value)));
            return;
        }
        player.getActionManager().setAction(new TabletCreation(tablet, option == 2 ? 5 : option == 4 ? 28 : 1));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.ARCEUUS_LECTERN;
    }
}
