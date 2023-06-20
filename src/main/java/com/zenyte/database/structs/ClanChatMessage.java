package com.zenyte.database.structs;

import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ClanChatMessage {

    public static final List<ClanChatMessage> list = new ArrayList<>();

    @Getter private final Player player;

    @Getter private final String message;

    @Getter private final String clan;

    @Getter private final Timestamp date = new Timestamp(System.currentTimeMillis());

    public ClanChatMessage(final Player player, final String message, final String clan) {
        this.player = player;
        this.message = message;
        this.clan = clan;
    }
}
