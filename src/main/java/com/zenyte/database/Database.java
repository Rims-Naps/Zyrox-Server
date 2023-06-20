package com.zenyte.database;

import java.util.HashMap;

import com.zenyte.Constants;
import lombok.Getter;

public class Database {
	
	public static HashMap<String, Database> databases = new HashMap<>();
	public static DatabasePool pool;
	
	@Getter private final DatabaseDetails details;
	
	public Database(final DatabaseDetails details) {
		this.details = details;
	}
	
	public enum DatabaseDetails {
		MAIN_LOCAL(DatabaseCredential.LOCAL, "zenyte_main"),
		FORUM_LOCAL(DatabaseCredential.LOCAL, "zenyte_forum"),

		MAIN_BETA(DatabaseCredential.BETA, "zenyte_main"),
		FORUM_BETA(DatabaseCredential.BETA, "zenyte_forum")
		;
		
		@Getter private DatabaseCredential auth;
		@Getter private String database;
		
		public static final DatabaseDetails[] VALUES = values();
		
		DatabaseDetails(final DatabaseCredential auth, final String database) {
			this.auth = auth;
			this.database = database;
		}
	}
	
	public static void preload() {
		external : for(final DatabaseDetails entry : DatabaseDetails.VALUES) {
			internal : for (DatabaseCredential auth : Constants.FAILOVER.getNodes())
				if (auth != entry.getAuth())
					continue external;

			if (entry.getDatabase() != null)
				databases.put(entry.getDatabase(), new Database(entry));
		}
	}
}
