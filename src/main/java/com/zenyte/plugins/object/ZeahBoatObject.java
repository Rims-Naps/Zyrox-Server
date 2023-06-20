package com.zenyte.plugins.object;

import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.OptionDialogue;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Tommeh | 17/11/2019 | 19:02
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ZeahBoatObject implements ObjectAction {

    @Getter
    @RequiredArgsConstructor
    private enum ZeahBoat {
        MOLCH(new Location(1342, 3646, 0)),
        BATTLEFRONT(new Location(1384, 3665, 0)),
        MOLCH_ISLAND(new Location(1369, 3639, 0)),
        SHAYZIEN(new Location(1408, 3612, 0));

        private final Location location;

        private static final ZeahBoat[] values = values();

        private static ZeahBoat get(final Player player) {
            double previousDistance = 50;
            ZeahBoat previousBoat = null;
            for (val boat : values) {
                val distance = player.getLocation().getDistance(boat.getLocation());
                if (distance < previousDistance) {
                    previousBoat = boat;
                    previousDistance = distance;
                }
            }
            return previousBoat;
        }

        @Override
        public String toString() {
            return TextUtils.capitalize(name().toLowerCase().replaceAll("_", " "));
        }
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val currentBoat = ZeahBoat.get(player);
        if (currentBoat == null) {
            return;
        }
        val boats = new ArrayList<ZeahBoat>(4);
        Collections.addAll(boats, ZeahBoat.values);
        boats.remove(currentBoat);
        val options = new ArrayList<String>(4);
        boats.forEach(b -> options.add(b.toString()));
        options.add("Cancel");
        val runnables = new ArrayList<Runnable>(4);
        boats.forEach(b -> {
            runnables.add(new Runnable() {
                @Override
                public void run() {
                    player.lock();
                    player.getDialogueManager().start(new PlainChat(player, "You travel to " + (b.equals(ZeahBoat.BATTLEFRONT) ? "the " : "") + b.toString() + ".", false));
                    new FadeScreen(player, () -> {
                        player.unlock();
                        player.setLocation(b.getLocation());
                        player.getInterfaceHandler().closeInterfaces();
                    }).fade(3);
                }
            });
        });
        player.getDialogueManager().start(new OptionDialogue(player, "Where would you like to go?", options.toArray(new String[options.size()]), runnables.toArray(new Runnable[runnables.size()])));
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{33614};
    }
}
