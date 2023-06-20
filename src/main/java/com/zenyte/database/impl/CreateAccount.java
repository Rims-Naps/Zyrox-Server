package com.zenyte.database.impl;

import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.SQLRunnable;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
public class CreateAccount extends SQLRunnable {

	private Player player;
	
	public CreateAccount(final Player player) {
		this.player = player;
	}

	@Override
	public void execute(final DatabaseCredential auth) {
		final PlayerInformation info = player.getPlayerInformation();

		try(final Connection con = DatabasePool.getConnection(auth, "zenyte_main");
			final PreparedStatement pst = con.prepareStatement("INSERT INTO core_members (name, members_pass_hash, members_pass_salt, member_group_id, joined, ip_address, last_visit)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)",  Statement.RETURN_GENERATED_KEYS)) {
			
			pst.setString(1, info.getDisplayname());
			pst.setString(2, info.getPlainPassword());
			//pst.setString(3, info.getSalt());
			pst.setInt(4, 3);
			pst.setLong(5, System.currentTimeMillis() / 1000);
			pst.setString(6, info.getIp());
			pst.setLong(7, System.currentTimeMillis() / 1000);
			pst.execute();
			
			ResultSet result = pst.getGeneratedKeys();
			result.next();
			info.setUserIdentifier(result.getInt(1));
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
	}
}
