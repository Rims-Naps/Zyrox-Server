package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
 @Slf4j
 public class TotalDonatedRequest {

   private final String username;

    public TotalDonatedRequest(final String username) {
       this.username = username.replaceAll("_", " ");
   }

   public int execute() {
       val http = APIClient.CLIENT;
       val url = APIClient.urlBuilder()
               .addPathSegment("account")
                .addPathSegment("spent")
               .addPathSegment(username).build();

      val request = new Request.Builder()
               .url(url)
              .get()
               .build();

        try {
           try (val response = http.newCall(request).execute()) {
               val responseBody = response.body();
               if (responseBody == null || !response.isSuccessful()) {
                    return -1;
              }
               val body = responseBody.string();
               // If empty besides {}
                if (body.length() == 0) {
                  return -1;
                }

                return NumberUtils.isDigits(body) ? Integer.parseInt(body) : 0;
             }
        } catch (final Exception e) {
           log.error(Strings.EMPTY, e);
       }

       return -1;
    }
 }
