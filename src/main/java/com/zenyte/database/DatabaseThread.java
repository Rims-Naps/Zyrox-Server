package com.zenyte.database;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

@Slf4j
public class DatabaseThread extends Thread {

	public static volatile boolean ENABLED = true;

	@Override
	public void run() {
		try {
			try {
				Database.preload();
				Database.pool = new DatabasePool();
			} catch (final Exception e) {
                log.error(Strings.EMPTY, e);
            }
		
			while(ENABLED) {
				QueryExecutor.process();
				try {
					Thread.sleep(600);
				} catch (final Exception e) {
                    log.error(Strings.EMPTY, e);
                }
			}
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
	}

}
