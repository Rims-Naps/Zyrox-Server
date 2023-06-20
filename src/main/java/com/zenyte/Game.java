package com.zenyte;

import com.zenyte.cores.WorldThread;
import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.ClientProtLoader;
import com.zenyte.game.util.Huffman;
import mgi.types.Definitions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.tools.jagcached.cache.Cache;

import java.nio.ByteBuffer;

/**
 * @author Tommeh | 28 jul. 2018 | 13:03:30
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
public class Game {

    @Getter
    @Setter
    private static mgi.tools.jagcached.cache.Cache cacheMgi;
    @Getter
    public static ByteBuffer checksumBuffer;
    @Getter
    public static ClientProtDecoder[] decoders = new ClientProtDecoder[256];

    public static int[] crc;

    public static final long getCurrentCycle() {
        return WorldThread.WORLD_CYCLE;
    }

    public static void load() {
        cacheMgi = Cache.openCache("./data/cache/");
        crc = cacheMgi.getCrcs();
        checksumBuffer = ByteBuffer.wrap(cacheMgi.generateInformationStoreDescriptor().getBuffer());
        Huffman.load();
        ClientProtLoader.load();
        for (val clazz : Definitions.HIGH_PRIORITY_DEFINITIONS) {
            Definitions.load(clazz).run();
        }
    }
}



