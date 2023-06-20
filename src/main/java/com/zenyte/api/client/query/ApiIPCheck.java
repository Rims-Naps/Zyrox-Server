package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.Set;

/**
 * @author Kris | 07/06/2019 08:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class ApiIPCheck {

    public static final Set<String> invalidIPs = new ObjectOpenHashSet<>(100);
    public static final Set<String> validIPs = new ObjectOpenHashSet<>(100);


    public boolean execute(final String ip) {
        val url = APIClient.urlBuilder()
                .addPathSegment("ip").addPathSegment("check").addPathSegment(ip).build();

        val request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (val response = APIClient.CLIENT.newCall(request).execute()) {
            val responseBody = response.body();
            if (responseBody == null || !response.isSuccessful()) {
                return true;
            }
            val string = responseBody.string();
            if (string.equals("true")) {
                validIPs.add(ip);
                return true;
            }
            invalidIPs.add(ip);
            return false;
        } catch (IOException e) {
            log.error(Strings.EMPTY, e);
            return true;
        }
    }

}
