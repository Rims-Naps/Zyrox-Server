package com.zenyte.game.world.entity.player.dialogue;

import com.zenyte.game.packet.out.IfSetAngle;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import lombok.Setter;
import lombok.val;
import mgi.types.config.npcs.NPCDefinitions;

/**
 * Created by Arham 4 on 2/15/2016.
 * <p>
 * Represents a singular message with an NPC talking.
 */
public class NPCMessage implements Message {
    
    private Expression expression;
    private final int npcId;
    private final String message;
    private final boolean showContinue;
    private final String npcName;

    @Setter private Runnable onDisplay;
    
    private Runnable runnable;
    
    public NPCMessage(final int npcId, final Expression expression, final String message) {
        this(npcId, expression, message, true, null);
    }
    
    public NPCMessage(final int npcId, final Expression expression, final String message, final boolean showContinue) {
        this(npcId, expression, message, showContinue, null);
    }
    
    public NPCMessage(final int npcId, final Expression expression, final String message, final String npcName) {
        this(npcId, expression, message, true, npcName);
    }
    
    public NPCMessage(final int npcId, final Expression expression, final String message, final boolean showContinue, final String npcName) {
        this.npcId = npcId;
        this.expression = expression;
        this.message = message;
        this.showContinue = showContinue;
        this.npcName = npcName;
    }
    
    @Override
    public void executeAction(final Runnable runnable) {
        this.runnable = runnable;
	}
	
	@Override
	public void execute(final Player player) {
		if (runnable != null) {
			runnable.run();
		}
	}

    @Override
    public void display(final Player player) {
        val baseDefs = NPCDefinitions.get(npcId);
        val transmogrifiedDefs = NPCDefinitions.get(player.getTransmogrifiedId(baseDefs, npcId));
        player.getInterfaceHandler().sendInterface(InterfacePosition.DIALOGUE, 231);
        player.getPacketDispatcher().sendComponentSettings(231, 3, -1, -1, AccessMask.CONTINUE);
        player.getPacketDispatcher().sendComponentNPCHead(231, 1, npcId);
        player.getPacketDispatcher().sendComponentText(231, 2, npcName == null ? transmogrifiedDefs.getName() : npcName);
        if (showContinue) {
            player.getPacketDispatcher().sendComponentText(231, 3, continueMessage(player));
        } else {
            player.getPacketDispatcher().sendComponentVisibility(231, 3, true);
        }
        player.getPacketDispatcher().sendComponentText(231, 4, message);
        player.getPacketDispatcher().sendClientScript(600, 1, 1, 16, 15138820);
    
        player.getPacketDispatcher().sendComponentAnimation(231, 1, expression.getId());
        val toString = expression.toString();
        if (toString.startsWith("HIGH_REV")) {
            val name = (npcName == null ? transmogrifiedDefs.getName() : npcName);
            val zoom = name.contains("General ") || name.equalsIgnoreCase("Tiny Thom") ? 1000 : 2500;
			player.send(new IfSetAngle(231, 1, 0, 1900, zoom));
		} else if (toString.startsWith("EASTER_BUNNY") || toString.startsWith("EASTER_BIRD")) {
            player.send(new IfSetAngle(231, 1, 0, 1900, 2500));
        }
        if (onDisplay != null) {
            onDisplay.run();
        }
    }
}