package com.zenyte.game.world.entity.player.dialogue;

import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.var.VarCollection;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arham 4 on 2/15/2016.
 * <p>
 * Represents a singular option dialogue with onClick handling each option.
 */

/**
 * I was thinking about maybe even making a SubDialogue class for the option dialogue. Though, I think that's
 * complicating things.
 */
@Slf4j
public class OptionMessage implements Message {

    private static final int OPTION_INTERFACE = 219;
    
    private final Map<Integer, Runnable> optionActions = new HashMap<Integer, Runnable>();

    @Setter
    private String title;
    @Getter private final String[] options;

    public OptionMessage(final String title, final String... options) {
        this.title = title;
        this.options = options;
    }
    
    public OptionMessage setOptions(final Runnable... runnables) {
    	int index = 1;
    	for (final Runnable run : runnables) {
    		optionActions.put(index++, run);
    	}
    	return this;
    }

    public OptionMessage onOptionOne(final Runnable runnable) {
    	optionActions.put(1, runnable);
    	return this;
    }
    
    public OptionMessage onOptionTwo(final Runnable runnable) {
    	optionActions.put(2, runnable);
    	return this;
    }
    
    public OptionMessage onOptionThree(final Runnable runnable) {
    	optionActions.put(3, runnable);
    	return this;
    }
    
    public OptionMessage onOptionFour(final Runnable runnable) {
    	optionActions.put(4, runnable);
    	return this;
    }
    
    public OptionMessage onOptionFive(final Runnable runnable) {
    	optionActions.put(5, runnable);
    	return this;
    }
    
    void onClick(final int option) {
    	final Runnable action = optionActions.get(option);
    	if (action == null) {
			return;
		}
    	try {
            action.run();
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @Override
    public final void display(final Player player) {
        val builder = new StringBuilder();
        for (val option : options) {
            builder.append(option).append('|');
        }
        VarCollection.DIALOGUE_FULL_SIZE.update(player, 1);
		player.getPacketDispatcher().sendClientScript(2379);
		player.getInterfaceHandler().sendInterface(InterfacePosition.DIALOGUE, OPTION_INTERFACE);
		player.getPacketDispatcher().sendClientScript(58, title, builder.toString());
		player.getPacketDispatcher().sendComponentSettings(219, 1, 1, 5, AccessMask.CONTINUE);
    }
}