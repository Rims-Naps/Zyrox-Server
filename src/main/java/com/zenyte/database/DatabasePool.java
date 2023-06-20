package com.zenyte.database;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.zenyte.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

@Slf4j
public class DatabasePool {
	
	private static HashMap<DatabaseCredential, HashMap<String, BoneCP>> pools = new HashMap<>();
	public static boolean DEBUG = true;

	public final int MINIMUM_DATABASE_CONNECTIONS = 1;
	public final int MAXIMUM_DATABASE_CONNECTIONS = 4;
	public final int PARTITION_COUNT = 4;
	public final int ACQUIRE_INCREMENT = 10;

	public DatabasePool() throws IOException {
		// init our hashmaps for pool filling
		for(DatabaseCredential auth : Constants.FAILOVER.getNodes())
			pools.put(auth, new HashMap<>());

		int total = 0;
		for (Database database : Database.databases.values())  {

			try {
				BoneCPConfig config = new BoneCPConfig();
				config.setJdbcUrl("jdbc:mysql://"+database.getDetails().getAuth().getHost()+":3306/" + database.getDetails().getDatabase()
						+ "?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
				config.setUsername(database.getDetails().getAuth().getUser());
				config.setPassword(database.getDetails().getAuth().getPass());
				
				config.setMinConnectionsPerPartition(MINIMUM_DATABASE_CONNECTIONS);
				config.setMaxConnectionsPerPartition(MAXIMUM_DATABASE_CONNECTIONS);
				config.setAcquireIncrement(ACQUIRE_INCREMENT);
				config.setPartitionCount(PARTITION_COUNT);
				config.setIdleConnectionTestPeriodInMinutes(1);
				
				BoneCP connection = new BoneCP(config);
				pools.get(database.getDetails().getAuth()).put(database.getDetails().getDatabase(), connection);
			} catch (SQLException ex) {
				DatabaseThread.ENABLED = false;
				log.error("SQL failed to connect on startup, it has been disabled!"+ex);
			}


			for (HashMap<String, BoneCP> poolSet : pools.values())
				for(BoneCP pool : poolSet.values())
					total += pool.getTotalCreatedConnections();
		}
		log.info("Configured connection pool with "+total+" total connections!");
	}

	public static Connection getConnection(final DatabaseCredential auth, final String database) throws SQLException {
		BoneCP pool = getPool(auth, database);
		return pool.getConnection();
	}
	
	public static BoneCP getPool(final DatabaseCredential auth, final String name) {
		return pools.get(auth).get(name);
	}
	
	public static void submit(SQLRunnable query) {
		// for monitoring query execution times
		long start =0 , end = 0;

		if(DEBUG)
			start = System.currentTimeMillis();

		if(query == null)
			return;

		/* Runs this query on all enabled "nodes" (or hosts) in the failover config */
		for(DatabaseCredential auth : Constants.FAILOVER.getNodes()) {
			try {
				query.execute(auth);

				if(DEBUG) {
					end = System.currentTimeMillis();
					log.info("Query ["+query.getClass().getSimpleName()+"] took approximately " + (end-start) + "ms to execute.");
				}

			} catch (final Exception e) {
                log.error(Strings.EMPTY, e);
            }
		}
	}
	
}
