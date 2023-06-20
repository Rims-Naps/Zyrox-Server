package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.tournament.plugins.TournamentLobby;
import com.zenyte.game.content.tournament.preset.TournamentPreset;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import mgi.types.config.enums.Enums;
import mgi.types.config.items.ItemDefinitions;
import lombok.val;

import java.util.Optional;

/**
 * @author Tommeh | 27/05/2019 | 14:38
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TournamentPresetsInterface extends Interface {

    @Override
    protected void attach() {
        put(2, "Presets");
        put(4, "Inventory Setup");
        put(5, "Equipment Setup");
        put(39, "Select");
        put(43, "Take Item");
        put(46, "View");
        put(47, "Apply");
    }

    @Override
    public void open(Player player) {
        if (player.getAttributes().get("was inside tournament lobby") == null) {
            player.sendMessage("Cannot view the tournament supplies/presets from outside of the lobby.");
            return;
        }
        val builder = new StringBuilder();
        for (val preset : TournamentPreset.values()) {
            builder.append(preset.toString()).append("|");
        }
        player.getInterfaceHandler().sendInterface(this);
        player.getPacketDispatcher().sendClientScript(10504, builder.toString(), 1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Take Item"), 0, Enums.TOURNAMENT_ITEMS_ENUM.getValues().size(), AccessMask.CLICK_OP1, AccessMask.CLICK_OP2, AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP10);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Inventory Setup"), 0, 28, AccessMask.CLICK_OP10);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Select"), 0, 50, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("View"), -1, 1, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Apply"), -1, 1, AccessMask.CLICK_OP1);
    }

    @Override
    protected void build() {
        bind("Presets", player -> player.getVarManager().sendVar(261, 0));
        bind("Select", (player, slotId, itemId, option) -> {
            if (!player.inArea("Tournament Lobby") || !player.getAttributes().containsKey("was inside tournament lobby")) {
                return;
            }
            player.getVarManager().sendVar(261, slotId + 1);
            val preset = TournamentPreset.get(slotId);
            val container = new Container(ContainerPolicy.NORMAL, ContainerType.TOURNAMENT, Optional.of(player));

            for (val entry : preset.getEquipment().getItems().entrySet()) {
                container.set(entry.getKey(), entry.getValue().getT());
            }

            for (val item : preset.getInventory().getItems()) {
                container.add(item.getT());
            }
            val builder = new StringBuilder();
            for (val entry : preset.getSkills().getSkills().entrySet()) {
                val skill = entry.getKey();
                val level = entry.getValue();
                if (!Skills.isCombatSkill(skill)) {
                    continue;
                }
                builder.append(skill).append("|");
                builder.append(level).append("|");
            }
            container.refresh(player);
            player.getPacketDispatcher().sendClientScript(10517, preset.toString(), builder.toString(), preset.getSpellbook().ordinal());
        });
        bind("Apply", player -> {
            val index = player.getVarManager().getValue(261) - 1;
            val preset = TournamentPreset.get(index);
            if (!player.inArea("Tournament Lobby") || !player.getAttributes().containsKey("was inside tournament lobby")) {
                return;
            }
            preset.apply(player);
        });
        bind("Inventory Setup", (player, slotId, itemId, option) -> Examine.sendItemExamine(player, id));
        bind("Equipment Setup",(player, slotId, itemId, option) -> Examine.sendItemExamine(player, id));
        bind("Take Item",(player, slotId, itemId, option) -> {
            val optionalId = Enums.TOURNAMENT_ITEMS_ENUM.getValue(slotId);
            if (!optionalId.isPresent()) {
                return;
            }
            if (!player.inArea("Tournament Lobby") || !player.getAttributes().containsKey("was inside tournament lobby")) {
                return;
            }
            val lobby = (TournamentLobby) player.getArea();
            val id = optionalId.getAsInt();
            int maximumAmountAllowed = Integer.MAX_VALUE;
            if (ItemDefinitions.get(id).getName().toLowerCase().startsWith("saradomin brew")) {
                val count =
                        player.getInventory().getAmountOf(6685) + player.getInventory().getAmountOf(6687) + player.getInventory().getAmountOf(6689) + player.getInventory().getAmountOf(6691);
                val maximumBrews = lobby.getPreset().getMaximumBrews();
                maximumAmountAllowed = Math.max(0, maximumBrews - count);
            }
            if (option == 10) {
                Examine.sendItemExamine(player, id);
                return;
            } else if (option == 4) {
                val maxAmount = maximumAmountAllowed;
                player.sendInputInt("How many would you like to take?", amount -> {
                    if (amount > maxAmount) {
                        player.sendMessage("You can only carry a maximum of " + lobby.getPreset().getMaximumBrews() + " saradomin brews with you in this fight.");
                    }
                    takeItem(player, id, Math.min(maxAmount, amount));
                });
            } else {
                int amount = option == 2 ? 5 : option == 3 ? 10 : 1;
                if (option == 1 && ItemDefinitions.get(id).isStackable()) {
                    amount = 10000;
                }
                if (amount > maximumAmountAllowed) {
                    amount = maximumAmountAllowed;
                    player.sendMessage("You can only carry a maximum of " + lobby.getPreset().getMaximumBrews() +
                                    " saradomin brew" + (lobby.getPreset().getMaximumBrews() == 1 ? "" : "s") +
                                    " with you in this fight."
                    );
                }
                takeItem(player, id, amount);
            }
        });
    }

    private void takeItem(final Player player, final int id, int amount) {
        if (amount <= 0) {
            return;
        }
        val added = player.getInventory().addItem(new Item(id, amount)).getSucceededAmount();
        if (added < amount) {
            player.sendMessage("Not enough space in your inventory.");
        }
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TOURNAMENT_PRESETS;
    }
}
