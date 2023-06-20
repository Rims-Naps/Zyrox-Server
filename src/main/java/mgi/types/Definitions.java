package mgi.types;

import com.zenyte.GameEngine;
import lombok.val;
import mgi.types.clientscript.ClientScriptDefinitions;
import mgi.types.component.ComponentDefinitions;
import mgi.types.config.*;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.identitykit.IdentityKitDefinitions;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import mgi.types.skeleton.SkeletonDefinitions;
import mgi.types.sprite.SpriteGroupDefinitions;
import mgi.types.worldmap.MapElementDefinitions;
import mgi.utilities.ByteBuffer;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 6. apr 2018 : 19:21.33
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public interface Definitions {

    Class<?>[] HIGH_PRIORITY_DEFINITIONS = new Class<?>[] { ObjectDefinitions.class, NPCDefinitions.class, ItemDefinitions.class,
			AnimationDefinitions.class };

    Class<?>[] LOW_PRIORITY_DEFINITIONS = new Class<?>[] { EnumDefinitions.class, GraphicsDefinitions.class, IdentityKitDefinitions.class,
			InventoryDefinitions.class, OverlayDefinitions.class, ParamDefinitions.class, ClientScriptDefinitions.class,
			SkeletonDefinitions.class, UnderlayDefinitions.class, VarbitDefinitions.class, ComponentDefinitions.class,
			HitbarDefinitions.class, MapElementDefinitions.class, StructDefinitions.class, SpriteGroupDefinitions.class, VarclientDefinitions.class };

	static void loadDefinitions(final Class<?>[] definitions) {
		for (val clazz : definitions) {
			try {
				val instance = clazz.newInstance();
				if (instance instanceof Definitions) {
					((Definitions) instance).load();
				}
			} catch (final Exception e) {
                GameEngine.logger.error(Strings.EMPTY, e);
			}
		}
	}

	static Runnable load(final Class<?> clazz) {
	    return () -> {
	      try {
              val instance = clazz.newInstance();
              if (instance instanceof Definitions) {
                  ((Definitions) instance).load();
              }
          } catch (Exception e) {
	          GameEngine.logger.error(Strings.EMPTY, e);
          }
        };
    }

	void load();


	default void decode(final ByteBuffer buffer) {

	}

	default void decode(final ByteBuffer buffer, final int opcode) {

	}

	default ByteBuffer encode() {
		return null;
	}

	default void pack() {
	}

}
