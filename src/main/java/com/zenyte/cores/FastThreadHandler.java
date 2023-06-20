package com.zenyte.cores;

import org.apache.logging.log4j.LogManager;

/**
 * @author David O'Neill
 */
final class FastThreadHandler implements Thread.UncaughtExceptionHandler {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FastThreadHandler.class);

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        logger.fatal("(" + thread.getName() + ", fast pool) - Printing trace");
        throwable.printStackTrace();
    }
}
