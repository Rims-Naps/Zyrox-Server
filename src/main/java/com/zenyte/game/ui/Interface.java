package com.zenyte.game.ui;

import com.zenyte.Constants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.component.ComponentDefinitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import lombok.val;

import java.util.Optional;

/**
 * @author Kris | 19/10/2018 01:15
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class Interface {

    /**
     * A map of <Int, String> containing bitpacked componentId & slot combinations, attached to our defined names for the components.
     */
    protected final Int2ObjectOpenHashMap<String> components = new Int2ObjectOpenHashMap<>();

    /**
     * A map of <Int, Handler> containing bitpacked componentId & slot combinations, attached to our defined handlers for the components.
     */
    protected final Int2ObjectOpenHashMap<Handler> handlers = new Int2ObjectOpenHashMap<>();

    /**
     * A map of <Int, SwitchPlugin> containing bitpacked component ids, attached to the handler for the switch action.
     */
    final Int2ObjectOpenHashMap<SwitchHandler> switchHandlers = this instanceof SwitchPlugin ? new Int2ObjectOpenHashMap<>() : null;

    /**
     * The default handler that's executed on the absense of a specific handler.
     */
    protected final DefaultClickHandler defaultClickHandler = getDefaultHandler();

    /**
     * The interface id.
     */
    protected final int id = getInterface().getId();
    /**
     * A reversed version of {@Link components}
     */
    private final Object2IntOpenHashMap<String> reverseComponents = new Object2IntOpenHashMap<>();

    protected DefaultClickHandler getDefaultHandler() {
        return null;
    }

    /**
     * Gets the reversed keyset of the attached buttons.
     * @return a copy of the reversed keyset.
     */
    protected ObjectSet<String> getReverseKeyset() {
        return new ObjectArraySet<>(reverseComponents.keySet());
    }

    /**
     * Binds a handler to the component requested - finds component based on the input name; the component has to previously be attached
     * through {@Link #put(componentId, slotId, name)} method.
     *
     * @param name    name of the component
     * @param handler the handler that's executed upon clicking the given component.
     * @return this object, to permit chaining.
     */
    protected Interface bind(final String name, final Handler handler) {
        val id = getComponentBitpacked(name);
        handlers.put(id, handler);
        return this;
    }

    /**
     * Binds a handler to the component requested - finds component based on the input name; the component has to previously be attached
     * through {@Link #put(componentId, slotId, name)} method.
     *
     * @param name    name of the component
     * @param handler the handler that's executed upon clicking the given component.
     * @return this object, to permit chaining.
     */
    protected Interface bind(final String name, final BasicHandler handler) {
        val id = getComponentBitpacked(name);
        handlers.put(id, handler);
        return this;
    }

    /**
     * A base method used for attaching names to components; this HAS to be used in a non-dynamic way, meaning there cannot be dynamic code!
     * The component ids as well as slot ids(optional) must be written as actual numbers, otherwise automatic update will NOT work.
     */
    protected abstract void attach();

    /**
     * A base method for opening this interface, allows executing a series of different calls; exists within the interface plugin to enable
     * automatic updating of the components that may be referenced through the opening method.
     *
     * @param player the player that is opening the interface.
     */
    public abstract void open(final Player player);

    /**
     * A base method that's executed after this interface closes, if it was open in the first place.
     *
     * @param player      the player interacting with the interface.
     * @param replacement the replacement interface, if another one is being opened & it's causing the closing of this
     *                    interface.
     */
    public void close(final Player player, final Optional<GameInterface> replacement) {
        /* Empty by default */
    }

    /**
     * Whether or not the interface buttons can be clicked whilst being in the locked state.
     *
     * @return whether the interface is interrupted on lock or not.
     */
    public boolean isInterruptedOnLock() {
        return true;
    }

    /**
     * A base method for building the interface - which effectively is just binding handlers to pre-defined components.
     */
    protected abstract void build();

    /**
     * Gets the GameInterface object of this interface. Contains information about the position of the interface.
     *
     * @return the GameInterface representation of this interface.
     */
    public abstract GameInterface getInterface();

    /**
     * Closes this interface for the player.
     *
     * @param player the player for whom to close the interface.
     */
    protected void close(final Player player) {
        player.getInterfaceHandler().closeInterface(getInterface().getPosition());
    }

    /**
     * Click method that is executed when a player clicks on a button. Finds a matching handler for the clicked component, prioritizing
     * pre-defined slot components over the basic non-slot based component.
     *
     * @param player      the player that clicked the interface.
     * @param componentId the component that was clicked.
     * @param slotId      the slotId that was clicked.
     * @param itemId      the itemId that was clicked.
     * @param option      the optionId that was clicked.
     * @return whether a handler for that component/slot id combination exists or not.
     */
    public boolean click(final Player player, final int componentId, final int slotId, final int itemId, final int option) {
        Handler handler = handlers.get((slotId & 0xFFFF) | ((componentId & 0xFFFF) << 16));
        if (handler == null && (slotId & 0xFFFF) != 0xFFFF) {
            handler = handlers.get((0xFFFF) | ((componentId & 0xFFFF) << 16));
        }
        if (handler == null) {
            if (defaultClickHandler != null) {
                defaultClickHandler.run(player, componentId, slotId, itemId, option);
                return true;
            }
            return false;
        }
        if (isInterruptedOnLock() && player.isLocked())
            return true;
        handler.handle(player, slotId, itemId, option);
        return true;
    }

    /**
     * Switches an item from one component to another.
     *
     * @param player        the player switching the item.
     * @param fromComponent the COMPONENT from which the item is being switched.
     * @param toComponent   the component to which the item is being switched.
     * @param fromSlot      the slot from which the item is being switched.
     * @param toSlot        the slot to which the item is being switched.
     * @return whether or not there was a switch handler for this interface and for these components.
     */
    public boolean switchItem(final Player player, final int fromComponent, final int toComponent, final int fromSlot, final int toSlot) {
        if (!(this instanceof SwitchPlugin))
            throw new RuntimeException("The interface " + getInterface() + " hasn't implemented the switch plugin.");
        val plugin = switchHandlers.get(fromComponent | toComponent << 16);
        if (plugin == null) {
            if (Constants.WORLD_PROFILE.isDevelopment()) {
                System.err.println("Missing switch handler for components " + fromComponent + "->" + toComponent + " on interface " + getInterface().name() + ".");
            }
            return false;
        }
        plugin.switchItem(player, fromSlot, toSlot);
        return true;
    }

    /**
     * Ties the name of the component to the component/slot id bitpacked value. The slot id will be pre-defined as 0xFFFF.
     *
     * @param componentId the id of the component.
     * @param name        the name of the component.
     */
    protected void put(final int componentId, final String name) {
        put(componentId, 0xFFFF, name);
    }

    /**
     * Ties the name of the component to the component/slot id bitpacked value.
     *
     * @param componentId the id of the component.
     * @param slotId      the slot id of the component.
     * @param name        the name of the component.
     */
    protected void put(final int componentId, final int slotId, final String name) {
        if (components.containsKey((slotId & 0xFFFF) | ((componentId & 0xFFFF) << 16))) {
            throw new RuntimeException("Overriding component: " + id + " - " + components.get((slotId & 0xFFFF) | (componentId & 0xFFFF) << 16) + " with name " + name + ".");
        }
        if (reverseComponents.containsKey(name)) {
            throw new RuntimeException("Duplicate strings aren't permitted.");
        }
        components.put((slotId & 0xFFFF) | ((componentId & 0xFFFF) << 16), name);
        reverseComponents.put(name, (slotId & 0xFFFF) | ((componentId & 0xFFFF) << 16));
    }

    /**
     * Gets the component id(and component id alone) for the name requested. Throws a runtime exception if no component
     * of the requested name is bound yet.
     *
     * @param value the name of the compoonent.
     * @return the id of the respective component.
     */
    public int getComponent(final String value) {
        val componentId = reverseComponents.getOrDefault(value, -1);
        if (componentId == -1)
            throw new RuntimeException("Couldn't find a matching component for string '" + value + "'.");
        return (componentId >> 16) & 0xFFFF;
    }

    /**
     * Gets the component id(and component id alone) for the name requested. Throws a runtime exception if no component
     * of the requested name is bound yet.
     *
     * @param componentId the id of the compoonent.
     * @param slotId the id of the slot.
     * @return the id of the respective component.
     */
    public Optional<String> getComponentName(final int componentId, final int slotId) {
        /*val componentId = reverseComponents.getOrDefault(value, -1);
        if (componentId == -1)
            throw new RuntimeException("Couldn't find a matching component for string '" + value + "'.");
        return (componentId >> 16) & 0xFFFF;*/
        return Optional.ofNullable(components.get((slotId & 0xFFFF) | ((componentId & 0xFFFF) << 16)));
    }

    /**
     * Gets the bitpacked component/slot id value based on the component name.
     *
     * @param value the name of the component
     * @return the bitpacked component/slot id value.
     */
    private int getComponentBitpacked(final String value) {
        val componentId = reverseComponents.getOrDefault(value, -1);
        if (componentId == -1)
            throw new RuntimeException("Couldn't find a matching component for string '" + value + "'.");
        return componentId;
    }

    /**
     * Gets a safe copy of the {@Link #components} map, meaning changes are not reflected on the actual map.
     *
     * @return a safe copy of the map.
     */
    Int2ObjectOpenHashMap<String> getComponentInfoCopy() {
        return new Int2ObjectOpenHashMap<>(components);
    }

    protected String getOptionString(final int componentId, final int optionId) {
        val defs = ComponentDefinitions.get(getInterface().getId(), componentId);
        return defs.getActions() == null || defs.getActions().length <= (optionId - 1) ? "null"
                : defs.getActions()[optionId - 1];
    }

    /**
     * A functional interface used for defining interface clicks.
     */
    @FunctionalInterface
    protected interface Handler {

        /**
         * Handles a click performed by the player on an interface component.
         *
         * @param player the player clicking the component.
         * @param slotId the slot id of the component, or -1 upon absence of one.
         * @param itemId the item id of the component, or -1 upon absence of one.
         * @param option the option clicked, ranging from 1 to 10.
         */
        void handle(final Player player, final int slotId, final int itemId, final int option);
    }

    /**
     * A functional interface used for defining basic interface clicks without any metadata.
     */
    @FunctionalInterface
    protected interface BasicHandler extends Handler {

        /**
         * Redirects the method call to {@code handle(final Player player)}.
         */
        @Override
        default void handle(final Player player, final int slotId, final int itemId, final int option) {
            handle(player);
        }

        /**
         * Handles the click on a component. Contains no metadata for the components that do not require it.
         *
         * @param player the player clicking the component.
         */
        void handle(final Player player);
    }

    @FunctionalInterface
    public interface SwitchHandler {
        void switchItem(final Player player, final int fromSlot, final int toSlot);
    }

    @FunctionalInterface
    public interface DefaultClickHandler {
        void run(final Player player, final int componentId, final int slotId, final int itemId, final int optionId);

    }

}
