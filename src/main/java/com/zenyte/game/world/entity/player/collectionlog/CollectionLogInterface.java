package com.zenyte.game.world.entity.player.collectionlog;

import com.google.common.base.Preconditions;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.StructDefinitions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Kris | 12/03/2019 23:03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CollectionLogInterface extends Interface {

    private static final int CATEGORY_SCRIPT = 2389;
    private static final int BUILD_INTERFACE_SCRIPT = 2730;
    public static final int STRUCT_POINTER_ENUM_CAT = 683;
    public static final int STRUCT_POINTER_SUB_ENUM_CAT_NAME = 689;
    public static final int STRUCT_POINTER_SUB_ENUM_CAT = 690;
    private static final String ATTR_KEY = "COLLECTION_LOG_CATEGORY";

    @Override
    protected void attach() {
        put(4, CLCategoryType.BOSS.category());
        put(5, CLCategoryType.RAIDS.category());
        put(6, CLCategoryType.CLUES.category());
        put(7, CLCategoryType.MINIGAMES.category());
        put(8, CLCategoryType.OTHER.category());

        put(10, CLCategoryType.BOSS + " " + CLContainerComponent.CONTAINER);
        put(11, CLCategoryType.BOSS + " " + CLContainerComponent.CONTAINER_OPTIONS);
        put(12, CLCategoryType.BOSS + " " + CLContainerComponent.CONTAINER_TEXT);
        put(13, CLCategoryType.BOSS + " " + CLContainerComponent.CONTAINER_SCROLLBAR);

        put(14, CLCategoryType.RAIDS + " " + CLContainerComponent.CONTAINER);
        put(15, CLCategoryType.RAIDS + " " + CLContainerComponent.CONTAINER_OPTIONS);
        put(16, CLCategoryType.RAIDS + " " + CLContainerComponent.CONTAINER_TEXT);
        put(21, CLCategoryType.RAIDS + " " + CLContainerComponent.CONTAINER_SCROLLBAR);

        put(22, CLCategoryType.CLUES + " " + CLContainerComponent.CONTAINER);
        put(30, CLCategoryType.CLUES + " " + CLContainerComponent.CONTAINER_OPTIONS);
        put(31, CLCategoryType.CLUES + " " + CLContainerComponent.CONTAINER_TEXT);
        put(23, CLCategoryType.CLUES + " " + CLContainerComponent.CONTAINER_SCROLLBAR);

        put(24, CLCategoryType.MINIGAMES + " " + CLContainerComponent.CONTAINER);
        put(25, CLCategoryType.MINIGAMES + " " + CLContainerComponent.CONTAINER_OPTIONS);
        put(34, CLCategoryType.MINIGAMES + " " + CLContainerComponent.CONTAINER_TEXT);
        put(26, CLCategoryType.MINIGAMES + " " + CLContainerComponent.CONTAINER_SCROLLBAR);

        put(27, CLCategoryType.OTHER + " " + CLContainerComponent.CONTAINER);
        put(32, CLCategoryType.OTHER + " " + CLContainerComponent.CONTAINER_OPTIONS);
        put(33, CLCategoryType.OTHER + " " + CLContainerComponent.CONTAINER_TEXT);
        put(28, CLCategoryType.OTHER + " " + CLContainerComponent.CONTAINER_SCROLLBAR);
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        val container = player.getCollectionLog().getContainer();
        container.setFullUpdate(true);
        player.getPacketDispatcher().sendUpdateItemContainer(container);
        populate(player, CLCategoryType.BOSS, 0);
    }

    private final void populate(@NotNull final Player player, @NotNull final CLCategoryType type,
                                final int subCategory) {
        val dispatcher = player.getPacketDispatcher();
        val options = new IntArrayList(5);
        options.add(id << 16 | getComponent(type + " " + CLContainerComponent.CONTAINER));
        options.add(id << 16 | getComponent(type + " " + CLContainerComponent.CONTAINER_OPTIONS));
        options.add(id << 16 | getComponent(type + " " + CLContainerComponent.CONTAINER_TEXT));
        options.add(id << 16 | getComponent(type + " " + CLContainerComponent.CONTAINER_SCROLLBAR));
        options.add(type.struct());
        options.add(subCategory);
        val struct = Objects.requireNonNull(StructDefinitions.get(type.struct()));
        val optional = struct.getValue(STRUCT_POINTER_ENUM_CAT);
        val enumId = optional.orElseThrow(RuntimeException::new);
        assert enumId instanceof Integer;
        val categoryEnum = EnumDefinitions.getIntEnum((Integer) enumId);
        val length = categoryEnum.getSize();
        Preconditions.checkArgument(subCategory >= 0 && subCategory < length);
        val value = categoryEnum.getValue(subCategory).orElseThrow(RuntimeException::new);
        val subCatStruct = Objects.requireNonNull(StructDefinitions.get(value));
        val name = subCatStruct.getValue(STRUCT_POINTER_SUB_ENUM_CAT_NAME).orElseThrow(RuntimeException::new).toString();
        val function = CollectionLogCategory.getFunction(name);
        player.getVarManager().sendVarInstant(2048, function == null ? 0 : function.apply(player));
        dispatcher.sendComponentSettings(getInterface(), getComponent(type + " " + CLContainerComponent.CONTAINER_OPTIONS), 0, length, AccessMask.CLICK_OP1);
        dispatcher.sendClientScript(CATEGORY_SCRIPT, type.ordinal());
        dispatcher.sendClientScript(BUILD_INTERFACE_SCRIPT, options.toArray());
        player.getTemporaryAttributes().put(ATTR_KEY, type);
    }

    @Override
    protected void build() {
        bind(CLCategoryType.BOSS.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.BOSS, 0));
        bind(CLCategoryType.RAIDS.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.RAIDS, 0));
        bind(CLCategoryType.CLUES.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.CLUES, 0));
        bind(CLCategoryType.MINIGAMES.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.MINIGAMES, 0));
        bind(CLCategoryType.OTHER.category(), (player, slotId, itemId, option) -> populate(player, CLCategoryType.OTHER, 0));
        bind(CLCategoryType.BOSS + " " + CLContainerComponent.CONTAINER_OPTIONS, (player, slotId, itemId, option) -> populate(player, getCurrentCategory(player), slotId));
        bind(CLCategoryType.RAIDS + " " + CLContainerComponent.CONTAINER_OPTIONS, (player, slotId, itemId, option) -> populate(player, getCurrentCategory(player), slotId));
        bind(CLCategoryType.CLUES + " " + CLContainerComponent.CONTAINER_OPTIONS, (player, slotId, itemId, option) -> populate(player, getCurrentCategory(player), slotId));
        bind(CLCategoryType.MINIGAMES + " " + CLContainerComponent.CONTAINER_OPTIONS, (player, slotId, itemId, option) -> populate(player, getCurrentCategory(player), slotId));
        bind(CLCategoryType.OTHER + " " + CLContainerComponent.CONTAINER_OPTIONS, (player, slotId, itemId, option) -> populate(player, getCurrentCategory(player), slotId));
    }

    @NotNull
    private CLCategoryType getCurrentCategory(@NotNull final Player player) {
        val categoryAttr = player.getTemporaryAttributes().get(ATTR_KEY);
        Preconditions.checkArgument(categoryAttr instanceof CLCategoryType);
        return (CLCategoryType) categoryAttr;
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.COLLECTION_LOG;
    }
}
