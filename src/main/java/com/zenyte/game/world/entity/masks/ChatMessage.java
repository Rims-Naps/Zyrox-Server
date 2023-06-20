package com.zenyte.game.world.entity.masks;

import com.zenyte.game.util.Huffman;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 6. nov 2017 : 14:28.18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
@Getter
@Slf4j
public class ChatMessage {

    private int effects;
    private String chatText;
    private boolean autotyper;

    private int offset;
    private final byte[] compressedArray = new byte[0xFF];

    public ChatMessage set(final String text, final int effects, final boolean autotyper) {
        return set(text, effects, autotyper, true);
    }

    public ChatMessage set(final String text, final int effects, final boolean autotyper, final boolean compress) {
        this.chatText = text;
        this.effects = effects;
        this.autotyper = autotyper;
        if (compress) {
            try {
                compress();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        }
        return this;
    }

    public static byte[] replaceOddCharacters(CharSequence var0) {
        int var1 = var0.length();
        byte[] var2 = new byte[var1];

        for(int var3 = 0; var3 < var1; ++var3) {
            char var4 = var0.charAt(var3);
            if(var4 > 0 && var4 < 128 || var4 >= 160 && var4 <= 255) {
                var2[var3] = (byte)var4;
            } else if(var4 == 8364) {
                var2[var3] = -128;
            } else if(var4 == 8218) {
                var2[var3] = -126;
            } else if(var4 == 402) {
                var2[var3] = -125;
            } else if(var4 == 8222) {
                var2[var3] = -124;
            } else if(var4 == 8230) {
                var2[var3] = -123;
            } else if(var4 == 8224) {
                var2[var3] = -122;
            } else if(var4 == 8225) {
                var2[var3] = -121;
            } else if(var4 == 710) {
                var2[var3] = -120;
            } else if(var4 == 8240) {
                var2[var3] = -119;
            } else if(var4 == 352) {
                var2[var3] = -118;
            } else if(var4 == 8249) {
                var2[var3] = -117;
            } else if(var4 == 338) {
                var2[var3] = -116;
            } else if(var4 == 381) {
                var2[var3] = -114;
            } else if(var4 == 8216) {
                var2[var3] = -111;
            } else if(var4 == 8217) {
                var2[var3] = -110;
            } else if(var4 == 8220) {
                var2[var3] = -109;
            } else if(var4 == 8221) {
                var2[var3] = -108;
            } else if(var4 == 8226) {
                var2[var3] = -107;
            } else if(var4 == 8211) {
                var2[var3] = -106;
            } else if(var4 == 8212) {
                var2[var3] = -105;
            } else if(var4 == 732) {
                var2[var3] = -104;
            } else if(var4 == 8482) {
                var2[var3] = -103;
            } else if(var4 == 353) {
                var2[var3] = -102;
            } else if(var4 == 8250) {
                var2[var3] = -101;
            } else if(var4 == 339) {
                var2[var3] = -100;
            } else if(var4 == 382) {
                var2[var3] = -98;
            } else if(var4 == 376) {
                var2[var3] = -97;
            } else {
                var2[var3] = 63;
            }
        }

        return var2;
    }

    private void compress() {
        val text = replaceOddCharacters(chatText);
        final int len = text.length;
        compressedArray[0] = (byte) len;
        offset = 1 + Huffman.compress(text, 0, len, compressedArray, 1);
    }

}
