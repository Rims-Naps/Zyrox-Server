package com.zenyte.database.impl;

import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.SQLRunnable;
import com.zenyte.game.content.vote.VoteHandler;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
public class CheckVote extends SQLRunnable {

    private Player player;

    public CheckVote(final Player player) {
        this.player = player;
    }

    @Override
    public void execute(final DatabaseCredential auth) {
        int amount = 0;
        final PlayerInformation info = player.getPlayerInformation();

        try(final Connection con = DatabasePool.getConnection(auth, "zenyte_main");
            final PreparedStatement stmt = con.prepareStatement("SELECT * FROM votes WHERE claimed = 0 AND userid = ?");
            final PreparedStatement stmt2 = con.prepareStatement("UPDATE votes SET claimed = 1 WHERE userid = ? and claimed = 0")) {
            stmt.setInt(1, info.getUserIdentifier());

            try(final ResultSet set = stmt.executeQuery()) {

                while (set.next()) {
                    amount++;
                }

                stmt2.setInt(1, info.getUserIdentifier());
                stmt2.execute();

            }

            VoteHandler.claim(player, amount);

        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }

    }
}
