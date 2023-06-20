package com.zenyte.game;

import com.zenyte.Constants;
import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.ui.testinterfaces.GameNoticeboardInterface;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.World;
import lombok.val;

import java.io.BufferedReader;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Cresinkel
 */
public class BonusCoxManager implements ScheduledExternalizable {

    public static long expirationDateCox;

    public static final void set(final long time) {
        expirationDateCox = time;
        GameNoticeboardInterface.refreshBonusCox();
    }

    public static final void checkIfFlip() {
        if (Constants.BOOSTED_COX) {
            if (expirationDateCox < System.currentTimeMillis()) {
                Constants.BOOSTED_COX = false;
                expirationDateCox = 0;
                GameNoticeboardInterface.refreshBonusCox();
                for (val player : World.getPlayers()) {
                    player.sendMessage("<col=FF0000><shad=000000>Chambers of Xeric is no longer boosted!</col></shad>");
                    player.getVarManager().sendVar(3804, 0);
                }
            }
        } else {
            if (expirationDateCox > 0) {
                Constants.BOOSTED_COX = true;
                GameNoticeboardInterface.refreshBonusCox();
                val date = new Date(BonusCoxManager.expirationDateCox).toString();
                for (val player : World.getPlayers()) {
                    player.sendMessage("<col=00FF00><shad=000000>Chambers of Xeric is boosted until " + date + "!</col></shad>");
                    player.getVarManager().sendVar(3804, Math.max(0, (int) TimeUnit.MILLISECONDS.toSeconds(BonusCoxManager.expirationDateCox - System.currentTimeMillis())));
                }
            }
        }
    }

    @Override
    public int writeInterval() {
        return 5;
    }

    @Override
    public void read(BufferedReader reader) {
        val expirationDate = World.getGson().fromJson(reader, Calendar.class);
        BonusCoxManager.expirationDateCox = expirationDate.getTimeInMillis();
    }

    @Override
    public void ifFileNotFoundOnRead() {
        write();
    }

    @Override
    public void write() {
        val calendar = Calendar.getInstance();
        calendar.setTimeInMillis(expirationDateCox);
        out(gson.toJson(calendar));
    }

    @Override
    public String path() {
        return "data/bonuscoxinfo.json";
    }
}
