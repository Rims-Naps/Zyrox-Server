package com.zenyte.database.impl;

import com.zenyte.Constants;
import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.SQLRunnable;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.sql.Connection;
import java.sql.PreparedStatement;


@Slf4j
public class PlayersOnline extends SQLRunnable {

    private int amount;

    public PlayersOnline(final int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(final DatabaseCredential auth) {
        try(final Connection con = DatabasePool.getConnection(auth, "zenyte_main");
            final PreparedStatement stmt = con.prepareStatement("UPDATE players_online SET online = ? WHERE world = ?")) {
            stmt.setInt(1, amount);
            stmt.setString(2, Constants.WORLD_PROFILE.getKey());
            stmt.execute();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }
}
