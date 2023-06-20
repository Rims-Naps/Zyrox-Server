package com.zenyte.game.world.region.area.godwars;

import com.zenyte.game.content.godwars.instance.GodwarsInstance;
import com.zenyte.game.content.godwars.npcs.*;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.CannonRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.RandomEventRestrictionPlugin;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 29. mai 2018 : 16:15:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class GodwarsDungeonArea extends Area implements CannonRestrictionPlugin, RandomEventRestrictionPlugin {
	public static final RSPolygon polygon = new RSPolygon(new int[][] { { 2815, 5377 }, { 2815, 5250 }, { 2953, 5250 }, { 2953, 5377 } });
	@Override
	public RSPolygon[] polygons() {
		return new RSPolygon[] { polygon };
	}

	public static final void enterArea(@NotNull final Player player) {
		CharacterLoop.forEach(player.getLocation(), 25, NPC.class, npc -> {
			if (!isChamberNPC(npc)) {
				if (npc.getCombat().getTarget() == player) {
					npc.getCombat().removeTarget();
				}
			}
		});
		player.getInterfaceHandler().sendInterface(InterfacePosition.OVERLAY, 406);
		refreshKillcount(player);
	}

	@Override
	public void enter(final Player player) {
		enterArea(player);
	}

	public static final void leaveArea(@NotNull final Player player) {
		CharacterLoop.forEach(player.getLocation(), 25, NPC.class, npc -> {
			if (isChamberNPC(npc)) {
				if (npc.getCombat().getTarget() == player) {
					npc.getCombat().removeTarget();
				}
			}
		});
		val area = GlobalAreaManager.getArea(player.getLocation());
		if (area instanceof GodwarsDungeonArea || area instanceof GodwarsInstance) {
			return;
		}
		resetKillcount(player);
		player.getInterfaceHandler().closeInterface(InterfacePosition.OVERLAY);
		if (player.getTemporaryAttributes().get("last hint arrow") != null) {
			player.getPacketDispatcher().resetHintArrow();
		}
	}

	@Override
	public void leave(final Player player, boolean logout) {
		leaveArea(player);
	}

	public static final boolean isChamberNPC(final NPC npc) {
	    return npc instanceof GodwarsBossMinion || npc instanceof KreeArra || npc instanceof GeneralGraardor || npc instanceof CommanderZilyana || npc instanceof KrilTsutsaroth;
    }

	public static void refreshKillcount(final Player player) {
        player.getVarManager().sendBit(3972, player.getNumericAttribute("SaradominKills").intValue());
        player.getVarManager().sendBit(3973, player.getNumericAttribute("ArmadylKills").intValue());
        player.getVarManager().sendBit(3975, player.getNumericAttribute("BandosKills").intValue());
        player.getVarManager().sendBit(3976, player.getNumericAttribute("ZamorakKills").intValue());
    }

	public static final void resetKillcount(final Player player) {
		player.getAttributes().remove("SaradominKills");
		player.getAttributes().remove("ArmadylKills");
		player.getAttributes().remove("BandosKills");
		player.getAttributes().remove("ZamorakKills");
	}

	@Override
	public String name() {
		return "Godwars Dungeon";
	}

}
