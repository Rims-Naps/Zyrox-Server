package com.zenyte.game.content.theatreofblood;

import com.zenyte.game.content.theatreofblood.reward.RewardRoom;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.MaidenOfSugadintiRoom;
import com.zenyte.game.content.theatreofblood.boss.nylocas.NylocasRoom;
import com.zenyte.game.content.theatreofblood.boss.pestilentbloat.PestilentBloatRoom;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.ShadowRealmArea;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.SotetsegRoom;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.VerzikRoom;
import com.zenyte.game.content.theatreofblood.boss.xarpus.XarpusRoom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 5/22/2020 | 4:32 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public enum TheatreRoom {
    
    THE_MAIDEN_OF_SUGADINTI("The Maiden of Sugadinti", 1, 392, 552, 12, 7, MaidenOfSugadintiRoom.class),
    THE_PESTILENT_BLOAT("The Pestilent Bloat", 2, 408, 552, 8, 8, PestilentBloatRoom.class),
    THE_NYLOCAS("The Nylocas", 3, 408, 528, 8, 8, NylocasRoom.class),
    SOTETSEG("Sotetseg", 4, 408, 536, 8, 8, SotetsegRoom.class),
    SHADOW_REALM("Shadow Realm", 4, 416, 536, 8, 8, ShadowRealmArea.class),
    XARPUS("Xarpus", 5, 392, 544, 8, 8, XarpusRoom.class),
    VERZIK("The Final Challenge", 6, 392, 535, 8, 8, VerzikRoom.class),
    REWARD("Verzik Vitur's Vault", 7, 403, 538, 8, 8, RewardRoom.class);
    
    private final String name;
    private final int wave;
    private final int chunkX, chunkY;
    private final int sizeX, sizeY;
    private final Class<? extends TheatreArea> clazz;
}
