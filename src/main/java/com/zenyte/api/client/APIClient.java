package com.zenyte.api.client;

import com.google.gson.Gson;
import com.zenyte.Constants;
import com.zenyte.GameEngine;
import com.zenyte.api.client.query.SendWorldInfo;
import com.zenyte.api.model.World;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.world.entity.player.Player;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Corey
 * @since 22/11/18
 */
@Slf4j
public class APIClient {
    
    public static final Gson GSON = new Gson();

    private static final Dispatcher dispatcher = new Dispatcher();
    static {
        dispatcher.setMaxRequests(200);
        dispatcher.setMaxRequestsPerHost(100);
    }
    public static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .writeTimeout(5000, TimeUnit.MILLISECONDS)
                                                      .dispatcher(dispatcher)
                                                      .addNetworkInterceptor(chain -> {
                                                          val userAgentRequest = chain.request()
                                                                                         .newBuilder()
                                                                                         .addHeader("Authorization", "Bearer " + Constants.WORLD_PROFILE.getApi().getToken())
                                                                                         .addHeader("User-Agent", "Zenyte/Game Server")
                                                                                         .build();
                                                          return chain.proceed(userAgentRequest);
                                                      })
                                                      .build();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    public static RequestBody jsonBody(final Object payload) {
        final String jsonString = toJson(payload);
        return RequestBody.create(jsonString, JSON);
    }
    
    public static HttpUrl.Builder urlBuilder() {
        return new HttpUrl.Builder()
                       .scheme(Constants.WORLD_PROFILE.getApi().getScheme())
                       .host(Constants.WORLD_PROFILE.getApi().getHost())
                       .port(Constants.WORLD_PROFILE.getApi().getPort());
    }
    
    public static String toJson(final Object object) {
        return GSON.toJson(object);
    }
    
    public static <T> T fromJson(final Class<T> clazz, final String json) {
        return GSON.fromJson(json, clazz);
    }
    
    public static void startTasks() {
        // world info
        if (!Constants.WORLD_PROFILE.isPrivate()) {
            CoresManager.getServiceProvider().scheduleRepeatingTask(() -> {
                val uptime = (int) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - GameEngine.SERVER_START_TIME);
                val usernames = com.zenyte.game.world.World.getPlayers().stream().map(Player::getName).collect(Collectors.toList());
    
                val world = new World(Constants.WORLD_PROFILE.getNumber(),
                        Constants.WORLD_PROFILE.getKey(),
                        Constants.WORLD_PROFILE.getHost(),
                        uptime,
                        Constants.WORLD_PROFILE.getActivity(),
                        usernames.size(),
                        usernames,
                        Constants.WORLD_PROFILE.getFlags(),
                        Constants.WORLD_PROFILE.getLocation());
                new SendWorldInfo(world).execute();
                
            }, 1, 15, TimeUnit.SECONDS);
        }
    }
    
}
