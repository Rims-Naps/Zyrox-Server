package com.zenyte.game.content.pyramidplunder.object;

import com.zenyte.game.content.pyramidplunder.PlunderRoom;
import com.zenyte.game.content.pyramidplunder.area.PyramidPlunderArea;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.packet.out.LocAnim;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import mgi.types.config.ObjectDefinitions;
import org.jetbrains.annotations.NotNull;

/**
 * @author Christopher
 * @since 4/1/2020
 */
public class PlunderDoor implements ObjectAction {
    public static final int ROOM_VARBIT = 2377;
    public static final int THIEVING_LEVEL_VARBIT = 2376;
    private static final Animation pickDoorAnim = new Animation(832);
    private static final SoundEffect pickDoorSound = new SoundEffect(87);
    private static final Animation doorMovingAnimation = new Animation(4338);

    public static void reset(Player player) {
        for (int doorId : PlunderRoom.doors) {
            player.getVarManager().sendBit(ObjectDefinitions.getOrThrow(doorId).getVarbitId(), 0);
        }
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val currentRoom = PlunderRoom.get(player.getVarManager().getBitValue(ROOM_VARBIT));
        if (currentRoom.hasNext()) {
            handleNextRoomDoor(player, object, currentRoom);
        } else {
            openLastDoorDialogue(player);
        }
    }

    private void handleNextRoomDoor(final Player player, final WorldObject object, final PlunderRoom currentRoom) {
        val nextRoom = currentRoom.next();
        if (check(player, object, currentRoom, nextRoom)) {
            val usingLockpick = player.getInventory().containsItem(ItemId.LOCKPICK);
            player.lock(2);
            player.setAnimation(pickDoorAnim);
            player.sendSound(pickDoorSound);
            player.sendFilteredMessage(usingLockpick ? "You use your lockpick and attempt to open the door."
                    : "You attempt to open the door. Lockpicks would make it easier...");
            WorldTasksManager.schedule(() -> {
                if (!player.inArea(PyramidPlunderArea.class)) {
                    return;
                }
                if (Utils.random(usingLockpick ? 7 : 5) == 0) {
                    player.sendFilteredMessage("Your attempt fails.");
                } else {
                    openDoor(player, object, currentRoom, nextRoom, usingLockpick);
                }
            });
        }
    }

    private boolean check(final Player player, final WorldObject object, final PlunderRoom currentRoom,
                          final PlunderRoom nextRoom) {
        if (!player.getSkills().checkRealLevel(Skills.THIEVING, nextRoom.getLevel(), "enter the next room")) {
            return false;
        }
        if (player.getVarManager().getBitValue(object.getDefinitions().getVarbitId()) == 1) {
            val isCorrectDoor = currentRoom.getCurrentDoor() == object.getId();
            if (isCorrectDoor) {
                openCorrectDoor(player, object, nextRoom);
            } else {
                player.sendFilteredMessage("You've already opened this door and it leads to a dead end.");
            }
            return false;
        }
        return true;
    }

    private void openDoor(final Player player, final WorldObject object, final PlunderRoom currentRoom,
                          final PlunderRoom nextRoom, final boolean usingLockpick) {
        val isCorrectDoor = currentRoom.getCurrentDoor() == object.getId();
        if (isCorrectDoor) {
            openCorrectDoor(player, object, nextRoom);
        } else {
            openDeadEnd(player, object);
        }
        player.getSkills().addXp(Skills.THIEVING, usingLockpick ? currentRoom.getDoorExp() / 2 :
                currentRoom.getDoorExp());
    }

    private void openCorrectDoor(final Player player, final WorldObject object, PlunderRoom nextRoom) {
        player.lock(3);
        openDoor(player, object);
        WorldTasksManager.schedule(() -> {
            player.setLocation(nextRoom.getStart());
            PlunderRoom.reset(player);
        }, 1);
    }

    private void openDeadEnd(final Player player, final WorldObject object) {
        openDoor(player, object);
        player.sendFilteredMessage("This door leads to a dead end.");
    }

    private final void openDoor(@NotNull final Player player, final WorldObject object) {
        //Personalized to this player only.
        player.sendZoneUpdate(object.getX(), object.getY(), new LocAnim(object, doorMovingAnimation));
        player.getVarManager().sendBit(object.getDefinitions().getVarbitId(), 1);
    }

    private void openLastDoorDialogue(final Player player) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                val currentRoom = PlunderRoom.get(player.getVarManager().getBitValue(PlunderDoor.ROOM_VARBIT));
                if (!currentRoom.hasNext()) {
                    plain("This is the last room, opening this door will cause you to leave the game. Do you really " +
                            "want to leave the tomb?");
                }
                options("Leave the Tomb?",
                        new DialogueOption("Yes, I'm out of here.", () -> PlunderRoomExit.leave(player)),
                        new DialogueOption("Ah, I think I'll stay a little longer here."));
            }
        });
    }

    @Override
    public int getDelay() {
        return 1;
    }

    @Override
    public Object[] getObjects() {
        return PlunderRoom.doors.toArray();
    }
}
