package com.zenyte.database.impl;

import com.zenyte.database.DatabaseCredential;
import com.zenyte.database.DatabasePool;
import com.zenyte.database.SQLRunnable;
import com.zenyte.game.util.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Noele | Jun 19, 2018 : 12:14:15 AM
 * @see https://noeles.life || noele@zenyte.com
 */
@Slf4j
public class CheckAccount extends SQLRunnable {

	/*
	 * args[0] - username string
	 * args[1] - password string
	 */
	private final Object[] args;
	
	public CheckAccount(Object...args) {
		this.args = args;
	}
	
	@Override
	public void execute(final DatabaseCredential auth) {
		if(!(args[0] instanceof String) || !(args[1] instanceof String))
			return;
		
		final String user = args[0].toString();
		final String pass = args[1].toString();
		
		try(final Connection con = DatabasePool.getConnection(auth, "zenyte_forum");
			final PreparedStatement load = con.prepareStatement("SELECT * FROM core_members WHERE name = ?")) {
			load.setString(1, user);
			
			final ResultSet set = load.executeQuery();
			String hash = null;
			String salt = null;
			
			while(set.next()) {
				hash = set.getString(set.findColumn("members_pass_hash"));
				salt = set.getString(set.findColumn("members_pass_salt"));
			}
			
			if(hash == null || salt == null) { /* something went wrong! */ }
			if(BCrypt.hashpw(pass, salt).equals(hash)) { /* this should mean the player can login! */ }

		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
	}

	@Override
	public void prepare() {
		DatabasePool.submit(this);
	}

}
