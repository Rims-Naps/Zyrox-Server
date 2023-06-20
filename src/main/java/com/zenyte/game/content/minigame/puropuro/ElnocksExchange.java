package com.zenyte.game.content.minigame.puropuro;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.hunter.plugins.ImplingJarPlugin;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.packet.out.IfSetAngle;
import com.zenyte.game.packet.out.IfSetModel;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;

import java.util.Comparator;
import java.util.Optional;

/**
 * @author Corey
 * @since 01/02/2020
 */
public class ElnocksExchange extends Interface {
    
    private static final String IMP_REPELLANT = "Imp repellant";
    private static final String MAGIC_BUTTERFLY_NET = "Magic butterfly net";
    private static final String JAR_GENERATOR = "Jar generator";
    private static final String IMPLING_JARS = "Three impling jars";
    private static final String OK = "Ok";
    
    private static final String ATTRIBUTE = "elnock_selected";
    
    private static final Object2IntOpenHashMap<String> productMapping = new Object2IntOpenHashMap<String>() {{
//        put(IMP_REPELLANT, ItemId.IMP_REPELLENT);
        put(MAGIC_BUTTERFLY_NET, ItemId.MAGIC_BUTTERFLY_NET);
        put(JAR_GENERATOR, ItemId.JAR_GENERATOR);
        put(IMPLING_JARS, ItemId.IMPLING_JAR);
    }};
    
    private static final Int2ObjectOpenHashMap<Item[]> prices = new Int2ObjectOpenHashMap<Item[]>() {{
//        put(ItemId.IMP_REPELLENT, new Item[]{
//                new Item(ImplingJarPlugin.ImplingJar.BABY.getItemId(), 3),
//                new Item(ImplingJarPlugin.ImplingJar.YOUNG.getItemId(), 2),
//                new Item(ImplingJarPlugin.ImplingJar.GOURMET.getItemId(), 1),
//        });
        
        put(ItemId.MAGIC_BUTTERFLY_NET, new Item[]{
                new Item(ImplingJarPlugin.ImplingJar.GOURMET.getItemId(), 3),
                new Item(ImplingJarPlugin.ImplingJar.EARTH.getItemId(), 2),
                new Item(ImplingJarPlugin.ImplingJar.ESSENCE.getItemId(), 1),
        });
        
        put(ItemId.JAR_GENERATOR, new Item[]{
                new Item(ImplingJarPlugin.ImplingJar.ESSENCE.getItemId(), 3),
                new Item(ImplingJarPlugin.ImplingJar.ECLECTIC.getItemId(), 2),
                new Item(ImplingJarPlugin.ImplingJar.NATURE.getItemId(), 1),
        });
    }};
    
    @Override
    protected void attach() {
        put(114, IMP_REPELLANT);
        put(117, MAGIC_BUTTERFLY_NET);
        put(120, JAR_GENERATOR);
        put(123, IMPLING_JARS);
        put(126, OK);
    }
    
    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        
        // imp repellant
        player.send(new IfSetModel(id, 113, 26331));
        player.send(new IfSetAngle(id, 113, 138, 1452, 510));
        
        // net
        player.send(new IfSetModel(id, 116, 26327));
        player.send(new IfSetAngle(id, 116, 152, 1956, 1420));
    
        // generator
        player.send(new IfSetModel(id, 119, 26329));
        player.send(new IfSetAngle(id, 119, 1757, 1591, 830));
    
        // impling jars
        player.send(new IfSetModel(id, 122, 26324));
        player.send(new IfSetAngle(id, 122, 280, 1756, 560));
    
        // imp repellant has been disabled, 698 = disabled sprite
        player.getPacketDispatcher().sendClientScript(44, id << 16 | getComponent(IMP_REPELLANT), 698);
    
        disable(player, MAGIC_BUTTERFLY_NET);
        disable(player, JAR_GENERATOR);
        disable(player, IMPLING_JARS);
    }
    
    @Override
    protected void build() {
        bind(IMP_REPELLANT, player -> {
            player.sendMessage("The imps are not interested in taking your implings. There is no need to buy any repellant.");
        });
    
        bind(MAGIC_BUTTERFLY_NET, player -> selectOption(player, MAGIC_BUTTERFLY_NET));
        bind(JAR_GENERATOR, player -> selectOption(player, JAR_GENERATOR));
        bind(IMPLING_JARS, player -> selectOption(player, IMPLING_JARS));
        bind(OK, player -> {
            val selected = player.getTemporaryAttributes().get(ATTRIBUTE);
        
            if (selected == null) {
                player.sendMessage("You need to select an option.");
                return;
            }
        
            val product = productMapping.getInt(selected);
            val price = prices.get(product);
    
            if (product == ItemId.IMPLING_JAR) {
                if (player.getInventory().getFreeSlots() < 3) {
                    player.sendMessage("You don't have enough inventory space to purchase this.");
                    return;
                }
        
                val jar = ImplingJarPlugin.ImplingJar.jars.values().stream()
                                  .filter(implingJar -> player.getInventory().containsItem(implingJar.getJarItem()))
                                  .min(Comparator.comparingInt(ImplingJarPlugin.ImplingJar::ordinal));
        
                if (jar.isPresent()) {
                    player.getInventory().deleteItems(jar.get().getJarItem());
                    player.getInventory().addItem(new Item(ItemId.IMPLING_JAR, 3));
                    player.sendMessage("You have purchased: Impling jar x3");
                } else {
                    player.sendMessage("Your implings are not suitable to be exchanged for that.");
                }
            
            } else {
                if (player.getInventory().containsItems(price)) {
                    if (product == ItemId.JAR_GENERATOR && player.containsItem(ItemId.JAR_GENERATOR)) {
                        player.sendMessage("You already have one of these. Use up the rest of the charges before purchasing another.");
                        return;
                    }
                    player.getInventory().deleteItems(price);
    
                    if (product == ItemId.JAR_GENERATOR) {
                        player.getInventory().addItem(new Item(product, 1, 100));
                    } else {
                        player.getInventory().addItem(new Item(product));
                    }
    
                    player.sendMessage("You have purchased: " + ItemDefinitions.nameOf(product) + " x1");
                } else {
                    player.sendMessage("You don't have the required implings in jars to trade for this.");
                }
            }
        
        });
    }
    
    @Override
    public GameInterface getInterface() {
        return GameInterface.ELNOCKS_EXCHANGE;
    }
    
    private void selectOption(final Player player, final String option) {
        val selected = player.getTemporaryAttributes().get(ATTRIBUTE);
        
        if (selected != null) {
            if (selected.equals(option)) {
                return;
            }
            disable(player, (String) selected);
        }
    
        player.getPacketDispatcher().sendClientScript(44, id << 16 | getComponent(option), 699);
        player.addTemporaryAttribute(ATTRIBUTE, option);
    }
    
    @Override
    public void close(Player player, Optional<GameInterface> replacement) {
        super.close(player, replacement);
        player.getTemporaryAttributes().remove(ATTRIBUTE);
    }
    
    private void disable(final Player player, final String component) {
        player.getPacketDispatcher().sendClientScript(44, id << 16 | getComponent(component), 697);
    }
    
}
