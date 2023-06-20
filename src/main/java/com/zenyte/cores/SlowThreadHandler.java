package com.zenyte.cores;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A hidden exception handler for logging silent thread death from the slow executor pool.
 * @author David O'Neill (dlo3)
 */
final class SlowThreadHandler implements Thread.UncaughtExceptionHandler {
	
	private static final Logger logger = LogManager.getLogger(SlowThreadHandler.class);

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        logger.fatal("(" + thread.getName() + ", slow pool) - Printing trace");
        throwable.printStackTrace();
    }

}
