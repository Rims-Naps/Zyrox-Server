package com.zenyte.database.structs;


import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PrivateMessage {

    public static List<PrivateMessage> list = new ArrayList<>();

    @Getter private final Player player;

    @Getter private final String friend;

    @Getter private final String message;

    @Getter private final Timestamp date = new Timestamp(System.currentTimeMillis());

    public PrivateMessage(final Player player, final String friend, final String message) {
        this.player = player;
        this.friend = friend;
        this.message = message;
    }
}
