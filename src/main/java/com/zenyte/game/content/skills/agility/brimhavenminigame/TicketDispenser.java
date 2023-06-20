package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.HintArrow;
import com.zenyte.game.RuneDate;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.pyramidplunder.PlunderRoom;
import com.zenyte.game.content.pyramidplunder.PyramidPlunderConstants;
import com.zenyte.game.content.pyramidplunder.object.PlunderDoor;
import com.zenyte.game.content.pyramidplunder.object.SarcophagusOpenAction;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Cresinkel
 */
public class TicketDispenser implements ObjectAction {

    public static int BRIMHAVEN_AGILITY_PILLAR_TAGGED_VARBIT = 5962;
    public static int BRIMHAVEN_AGILITY_COLOR_VARBIT = 5965;
    public static int BRIMHAVEN_AGILITY_STREAK_VARBIT = 5966;

    public static final Location DISPENSER = new Location(2761, 9546, 3);

    public static final Item AGILITY_ARENA_TICKET = new Item(2996);

    public static void reset(final Player player) {
        player.getVarManager().sendBit(TicketDispenser.BRIMHAVEN_AGILITY_COLOR_VARBIT , 0);
        player.getVarManager().sendBit(TicketDispenser.BRIMHAVEN_AGILITY_STREAK_VARBIT, 0);
        player.getPacketDispatcher().resetHintArrow();
        player.getAttributes().remove("currentTargetedDispenserX");
        player.getAttributes().remove("currentTargetedDispenserY");
        if (player.inArea("Brimhaven Agility Arena")) {
            player.sendMessage("You were to slow resulting in ending your streak of " + player.getNumericAttribute("dispenserStreak").intValue() + " succesful runs.");
        }
        player.getAttributes().put("dispenserStreak", 0);
    }

    public static void newTargetDispenser(final Player player) {
        player.getPacketDispatcher().resetHintArrow();
        int rand;
        int randomX;
        int randomY;
        rand = Utils.random(4);
        randomX = (rand * 11) + DISPENSER.getX();
        if (rand == 4) {
            rand = Utils.random(3);
        } else {
            rand = Utils.random(4);
        }
        randomY = (rand * 11) + DISPENSER.getY();
        player.getPacketDispatcher().sendHintArrow(new HintArrow(randomX, randomY, (byte) 100));
        player.getAttributes().put("currentTargetedDispenserX", randomX);
        player.getAttributes().put("currentTargetedDispenserY", randomY);
        player.getAttributes().put("timeOfNewTargetDispenser", 0);
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        execute(player, object);
    }

    private final void execute(@NotNull final Player player, @NotNull final WorldObject object) {
        player.faceObject(object);
        if (!(object.getX() == player.getNumericAttribute("currentTargetedDispenserX").intValue()
            && object.getY() == player.getNumericAttribute("currentTargetedDispenserY").intValue())
            && player.getVarManager().getBitValue(TicketDispenser.BRIMHAVEN_AGILITY_STREAK_VARBIT) == 1) {
            player.sendMessage("This is not the targeted ticket dispenser, follow the arrow.");
            return;
        }
        if (player.getVarManager().getBitValue(TicketDispenser.BRIMHAVEN_AGILITY_STREAK_VARBIT) == 0) {
            player.getVarManager().sendBit(TicketDispenser.BRIMHAVEN_AGILITY_STREAK_VARBIT, 1);
            newTargetDispenser(player);
            return;
        }
        if (!player.getInventory().checkSpace()) {
            if (!player.getInventory().containsItem(AGILITY_ARENA_TICKET)) {
                player.sendMessage("Your inventory is full, make some space to be rewarded.");
                return;
            }
        }
        player.getInventory().addItem(AGILITY_ARENA_TICKET.getId(), DiaryReward.KARAMJA_GLOVES4.eligibleFor(player) && Utils.random(9) == 0 ? 2 : 1);
        newTargetDispenser(player);
        player.getAttributes().put("dispenserStreak", player.getNumericAttribute("dispenserStreak").intValue() + 1);
        if (player.getNumericAttribute("dispenserStreak").intValue() % 10 == 0) {
            player.getSkills().addXp(Skills.AGILITY, 500);
            player.sendMessage("You were rewarded with agility experience for keeping up a good streak.");
        }
        player.getAttributes().put("totalDispensers", player.getNumericAttribute("totalDispensers").intValue() + 1);
        val runs = player.getNumericAttribute("totalDispensers").intValue();
        player.sendMessage("You have completed "+ Colour.RED.wrap(runs) + (runs == 1 ? " run " : " runs ") + "of the Brimhaven Agility Arena minigame.");
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{3608, 3581};
    }

}
