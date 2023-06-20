package com.zenyte.game.content.minigame.puropuro;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.hunter.node.Impling;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.Comparator;

/**
 * @author Corey
 * @since 29/01/2020
 */
public class ImplingTrackerInterface extends Interface {
    
    @Override
    protected void attach() {
        put(13, "Surface World");
        put(18, "Puro-Puro");
    
        put(4, "First");
        put(6, "Second");
        put(5, "Third");
        put(3, "Fourth");
        put(8, "Fifth");
        put(10, "Sixth");
        put(9, "Seventh");
        put(7, "Eighth");
    }
    
    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
        sendImplingCounts(player);
    }
    
    @Override
    protected void build() {
    }
    
    @Override
    public GameInterface getInterface() {
        return GameInterface.IMPLING_TRACKER;
    }
    
    private void sendImplingCounts(final Player player) {
        val surfaceWorldBuilder = new StringBuilder();
        val puroPuroBuilder = new StringBuilder();
        
        Impling.implings.values().stream()
                .sorted(Comparator.comparingInt(Impling::getLevel))
                .forEach(i -> {
                    
                    surfaceWorldBuilder.append(i.formattedName())
                            .append(":<br>")
                            .append("<col=ffffff>")
                            .append(player.getNumericAttribute(Impling.SURFACE_IMPLING_TRACKER_ATTRIBUTE_KEY + i.getNpcId()).intValue());
                    
                    puroPuroBuilder.append(i.formattedName())
                            .append(":<br>")
                            .append("<col=ffffff>")
                            .append(player.getNumericAttribute(Impling.PURO_IMPLING_TRACKER_ATTRIBUTE_KEY + i.getNpcId()).intValue());
                    
                    if (i.ordinal() != Impling.implings.size() - 1) {
                        puroPuroBuilder.append("|");
                        surfaceWorldBuilder.append("|");
                    }
                });
        
        player.getPacketDispatcher().sendClientScript(1327,
                id << 16 | getComponent("First"),
                id << 16 | getComponent("Second"),
                id << 16 | getComponent("Third"),
                id << 16 | getComponent("Fourth"),
                id << 16 | getComponent("Fifth"),
                id << 16 | getComponent("Sixth"),
                id << 16 | getComponent("Seventh"),
                id << 16 | getComponent("Eighth"),
                surfaceWorldBuilder.toString(),
                puroPuroBuilder.toString());
    }
    
}
