package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.minigame.duelarena.DuelSetting;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 24/10/2018 14:03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CombatTabInterface extends Interface {
    @Override
    protected void attach() {
        put(3, "Attack style 1");
        put(7, "Attack style 2");
        put(11, "Attack style 3");
        put(15, "Attack style 4");
        put(20, "Defensive autocast");
        put(25, "Autocast");
        put(29, "Auto retaliate");
        put(35, "Special attack");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
        player.getCombatDefinitions().refresh();
    }

    @Override
    public boolean isInterruptedOnLock() {
        return false;
    }

    @Override
    protected void build() {
        bind("Attack style 1", player -> {
            player.getCombatDefinitions().setStyle(0);
            player.getCombatDefinitions().setAutocastSpell(null);
        });
        bind("Attack style 2", player -> {
            player.getCombatDefinitions().setStyle(1);
            player.getCombatDefinitions().setAutocastSpell(null);
        });
        bind("Attack style 3", player -> {
            player.getCombatDefinitions().setStyle(2);
            player.getCombatDefinitions().setAutocastSpell(null);
        });
        bind("Attack style 4", player -> {
            player.getCombatDefinitions().setAutocastSpell(null);
            player.getCombatDefinitions().setStyle(3);
            player.getCombatDefinitions().refresh();
        });
        bind("Defensive autocast", player -> {
            if (player.isLocked()) {
                return;
            }
            player.getCombatDefinitions().setDefensiveAutocast(true);
            GameInterface.AUTOCAST_TAB.open(player);
        });
        bind("Autocast", player -> {
            if (player.isLocked()) {
                return;
            }
            player.getCombatDefinitions().setDefensiveAutocast(false);
            GameInterface.AUTOCAST_TAB.open(player);
        });
        bind("Auto retaliate", player -> {
            if (player.isLocked()) {
                return;
            }
            player.getCombatDefinitions().setAutoRetaliate(!player.getCombatDefinitions().isAutoRetaliate());
        });
        bind("Special attack", player -> {
            if (player.isLocked()) {
                return;
            }
            val duel = player.getDuel();
            if (duel != null && duel.hasRule(DuelSetting.NO_SPECIAL_ATTACK) && duel.inDuel()) {
                player.sendMessage("Use of special attacks has been turned off for this duel.");
                return;
            }
            player.getCombatDefinitions().setSpecial(!player.getCombatDefinitions().isUsingSpecial(), false);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.COMBAT_TAB;
    }
}
