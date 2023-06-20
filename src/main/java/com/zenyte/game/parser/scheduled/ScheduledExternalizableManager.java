package com.zenyte.game.parser.scheduled;

import com.zenyte.Constants;
import com.zenyte.cores.CoresManager;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.util.Strings;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Kris | 16. juuni 2018 : 16:22:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class ScheduledExternalizableManager {

	private static final List<ScheduledExternalizable> scheduled = new LinkedList<ScheduledExternalizable>();
	private static int minutesPassed;
	public static final MutableBoolean status = new MutableBoolean();
	
	static {
		CoresManager.slowExecutor.scheduleAtFixedRate(() -> {
			save();
			minutesPassed++;
		}, 1, 1, TimeUnit.MINUTES);
	}

	public static final synchronized void save() {
	    try {
	        if (Constants.WORLD_PROFILE.isDevelopment()) {
	            return;
            }
	        status.setTrue();
            for (val scheduled : ScheduledExternalizableManager.scheduled) {
                val interval = scheduled.writeInterval();
                if (interval <= 0) {
                    continue;
                }
                if (minutesPassed % interval == 0) {
                    try {
                        scheduled.writeAndOutput();
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                }
            }
            status.setFalse();
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }
	
	public static final void add(final Class<? extends ScheduledExternalizable> c) {
		try {
			if (c.isAnonymousClass() || c.isMemberClass()) {
				return;
			}
			val instance = c.newInstance();
			scheduled.add(instance);
			instance.read();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
}
