package com.zenyte.game.content.minigame.duelarena.interfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.minigame.duelarena.DuelSetting;
import com.zenyte.game.content.minigame.duelarena.DuelStage;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.Optional;

/**
 * @author Tommeh | 27-10-2018 | 20:28
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DuelSettingsInterface extends Interface {

    @Override
    protected void attach() {
        put(103, "Confirm");
        put(104, "Decline");
        put(108, "Store preset settings");
        put(109, "Load last duel settings");
        put(110, "Load preset settings");
        put(41, "No Ranged");
        put(54, "No Ranged2");
        put(42, "No Melee");
        put(55, "No Melee2");
        put(43, "No Magic");
        put(56, "No Magic2");
        put(49, "No Special Attack");
        put(62, "No Special Attack2");
        put(48, "No Fun Weapons");
        put(61, "No Fun Weapons2");
        put(37, "No Forfeit");
        put(50, "No Forfeit2");
        put(46, "No Prayers");
        put(59, "No Prayers2");
        put(44, "No Drinks");
        put(57, "No Drinks2");
        put(45, "No Food");
        put(58, "No Food2");
        put(38, "No Movement");
        put(51, "No Movement2");
        put(47, "Obstacles");
        put(60, "Obstacles2");
        put(39, "No Weapon Switching");
        put(52, "No Weapon Switching2");
        put(40, "Show Inventories");
        put(53, "Show Inventories2");
        put(69, "Head");
        put(70, "Back");
        put(71, "Neck");
        put(72, "Left hand");
        put(73, "Torso");
        put(74, "Right hand");
        put(75, "Leg");
        put(76, "Hand");
        put(77, "Feet");
        put(78, "Ring");
        put(79, "Ammunition");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        if (!replacement.isPresent() || !replacement.get().equals(GameInterface.DUEL_STAKING)) {
            Optional.ofNullable(player.getDuel()).ifPresent(duel -> duel.close(true));
        }
    }

    @Override
    protected void build() {
        bind("Confirm", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.confirm(DuelStage.SETTINGS);
        });
        bind("Decline", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.close(true);
        });
        bind("Store preset settings", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            player.getAttributes().put("duelPresetSettings", duel.getSettings());
            player.sendMessage("Stored preset settings overwritten.");
        });
        bind("Load last duel settings", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            val opponent = duel.getOpponent();
            if (opponent == null) {
                return;
            }
            val lastSettings = player.getNumericAttribute("lastDuelSettings").intValue();
            if (lastSettings == duel.getSettings()) {
                player.sendMessage("Last duel settings are identical to those already selected.");
                return;
            }
            duel.setRules(lastSettings);
            player.sendMessage("Last duel settings loaded.");
            opponent.sendMessage("Duel Option change - Opponent's preset options loaded!");
        });
        bind("Load preset settings", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            val opponent = duel.getOpponent();
            if (opponent == null) {
                return;
            }
            val settings = player.getNumericAttribute("duelPresetSettings").intValue();
            if (settings == duel.getSettings()) {
                player.sendMessage("Preset duel settings are identical to those already selected.");
                return;
            }
            duel.setRules(settings);
            player.sendMessage("Preset duel settings loaded.");
            opponent.sendMessage("Duel Option change - Opponent's last duel options loaded!");
        });
        bind("No Ranged", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_RANGED);
        });
        bind("No Ranged2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_RANGED);
        });
        bind("No Melee", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_MELEE);
        });
        bind("No Melee2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_MELEE);
        });
        bind("No Magic", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_MAGIC);
        });
        bind("No Magic2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_MAGIC);
        });
        bind("No Special Attack", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_SPECIAL_ATTACK);
        });
        bind("No Special Attack2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_SPECIAL_ATTACK);
        });
        bind("No Fun Weapons", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_FUN_WEAPONS);
        });
        bind("No Fun Weapons2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_FUN_WEAPONS);
        });
        bind("No Forfeit", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_FORFEIT);
        });
        bind("No Forfeit2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_FORFEIT);
        });
        bind("No Prayers", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_PRAYER);
        });
        bind("No Prayers2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_PRAYER);
        });
        bind("No Drinks", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_DRINKS);
        });
        bind("No Drinks2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_DRINKS);
        });
        bind("No Food", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_FOOD);
        });
        bind("No Food2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_FOOD);
        });
        bind("No Movement", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_MOVEMENT);
        });
        bind("No Movement2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_MOVEMENT);
        });
        bind("Obstacles", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.OBSTACLES);
        });
        bind("Obstacles2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.OBSTACLES);
        });
        bind("No Weapon Switching", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_WEAPON_SWITCH);
        });
        bind("No Weapon Switching2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NO_WEAPON_SWITCH);
        });
        bind("Show Inventories", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.SHOW_INVENTORIES);
        });
        bind("Show Inventories2", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.SHOW_INVENTORIES);
        });
        bind("Head", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.HEAD);
        });
        bind("Back", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.BACK);
        });
        bind("Neck", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.NECK);
        });
        bind("Left hand", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.LEFT_HAND);
        });
        bind("Torso", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.TORSO);
        });
        bind("Right hand", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.RIGHT_HAND);
        });
        bind("Leg", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.LEG);
        });
        bind("Hand", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.HAND);
        });
        bind("Feet", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.FEET);
        });
        bind("Ring", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.RING);
        });
        bind("Ammunition", player -> {
            val duel = player.getDuel();
            if (duel == null) {
                return;
            }
            duel.toggleRule(DuelSetting.AMMUNITION);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.DUEL_SETTINGS;
    }
}
