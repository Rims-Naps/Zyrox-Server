package com.zenyte.game.content.partyroom;

import com.google.common.eventbus.Subscribe;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.constants.GameConstants;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.events.LoginEvent;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Set;

/**
 * @author Kris | 07/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BirthdayEventRewardList implements ScheduledExternalizable {
    private static final Set<String> usernames = new ObjectOpenHashSet<>();
    private static final String path = "data/birthday event.json";

    private static final void load(final BufferedReader reader) {
        final java.lang.String[] set = World.getGson().fromJson(reader, String[].class);
        if (set == null) return;
        for (final java.lang.String username : set) {
            usernames.add(Utils.formatUsername(username));
        }
    }

    public static final void reload() {
        CoresManager.getServiceProvider().submit(() -> {
            try {
                load(new BufferedReader(new FileReader(new File(path))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public static final void refreshAll() {
        for (final java.lang.String username : usernames) {
            World.getPlayer(username).ifPresent(BirthdayEventRewardList::addReward);
        }
    }

    private static final void addReward(@NotNull final Player player) {
        if (player.getTrackedHolidayItems().contains(ItemId.BIRTHDAY_CAKE)) {
            return;
        }
        player.getTrackedHolidayItems().add(ItemId.BIRTHDAY_CAKE);
        player.sendMessage(Colour.RS_GREEN.wrap("You have received a birthday cake for participating in the 2020 " + GameConstants.SERVER_NAME + " birthday event."));
        player.sendMessage(Colour.RS_GREEN.wrap("Should you lose the cake, you can reclaim it from Diango in Draynor Village."));
        player.getInventory().addOrDrop(new Item(ItemId.BIRTHDAY_CAKE));
    }

    @Subscribe
    public static final void onLogin(final LoginEvent event) {
        if (usernames.contains(event.getPlayer().getUsername())) {
            addReward(event.getPlayer());
        }
    }

    public static final void addUsername(@NotNull final String username) {
        if (usernames.add(Utils.formatUsername(username))) {
            CoresManager.getServiceProvider().submit(() -> {
                final java.lang.String[] array = usernames.toArray(new String[0]);
                final java.lang.String toJson = World.getGson().toJson(array);
                try {
                    final PrintWriter pw = new PrintWriter(path, "UTF-8");
                    pw.println(toJson);
                    pw.close();
                } catch (final Exception e) {
                    log.error(Strings.EMPTY, e);
                }
            });
            World.getPlayer(username).ifPresent(BirthdayEventRewardList::addReward);
        }
    }

    @Override
    public int writeInterval() {
        return 0;
    }

    @Override
    public void read(final BufferedReader reader) {
        load(reader);
    }

    @Override
    public void write() {
    }

    @Override
    public String path() {
        return path;
    }
}