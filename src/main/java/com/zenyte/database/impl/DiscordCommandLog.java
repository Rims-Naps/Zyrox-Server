package com.zenyte.database.impl;

import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.SQLRunnable;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Slf4j
public class DiscordCommandLog extends SQLRunnable {
	
	/*
	 * Args table:
	 * 
	 * - args[0] - command identifier, w/o symbols like ;; or ::
	 * - args[1] - user discord id, to store as a String (its 18 char long)
	 */
	
	private final Object[] args;

	public DiscordCommandLog(Object... args) {
		this.args = args;
	}
	
	@Override
	public void execute(final DatabaseCredential auth) {
		if(!(args[0] instanceof String) || !(args[1] instanceof String))
			return;

		PreparedStatement pst = null;

		try {
			final Connection con = DatabasePool.getConnection(auth, "zenyte_main");
			PreparedStatement del;
			del = con.prepareStatement("DELETE FROM command_logs WHERE id=? AND command=?");
			del.setString(1, args[0].toString());
			del.setString(2, args[1].toString());
			del.execute();
			del.close();
				
			pst = con.prepareStatement("INSERT INTO command_logs (id, command) VALUES (?, ?)");
			pst.setString(1, args[0].toString());
			pst.setString(2, args[1].toString());
			pst.execute();
			pst.close();
			con.close();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
	}
}
