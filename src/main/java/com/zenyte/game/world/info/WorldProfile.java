package com.zenyte.game.world.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zenyte.api.model.WorldLocation;
import com.zenyte.api.model.WorldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Corey
 * @since 20/05/19
 */
@Data
public class WorldProfile {
    
    @JsonIgnore
    private String key;
    private int number;
    private String host;
    private int port;
    private String activity;
    private boolean isPrivate;
    private boolean development;
    private boolean verifyPasswords;
    private WorldLocation location;
    private List<WorldType> flags;
    private ApiSettings api;
    public WorldProfile() {
    
    }
    public WorldProfile(String key,
                        int number,
                        String host,
                        int port,
                        String activity,
                        boolean isPrivate,
                        boolean development,
                        boolean verifyPasswords,
                        WorldLocation location,
                        List<WorldType> flags,
                        ApiSettings api) {
        this.key = key;
        this.number = number;
        this.host = host;
        this.port = port;
        this.activity = activity;
        this.isPrivate = isPrivate;
        this.development = development;
        this.verifyPasswords = verifyPasswords;
        this.location = location;
        this.flags = flags;
        this.api = api;
    }
    
    public WorldProfile(String key) throws IOException {
        this.key = key;
        WorldProfile world;
        final LoaderOptions loaderOptions = new LoaderOptions();
        final Yaml yaml = new Yaml(new Constructor(Worlds.class, loaderOptions));
        try (final BufferedReader br = new BufferedReader(new FileReader(new File("worlds.yml")))) {
            Worlds worlds = yaml.load(br);
            world = worlds.worlds.get(key);
            this.number = world.number;
            this.host = world.host;
            this.port = world.port;
            this.activity = world.activity;
            this.isPrivate = world.isPrivate;
            this.development = world.development;
            this.verifyPasswords = world.verifyPasswords;
            this.location = world.location;
            this.flags = world.flags;
            this.api = world.api;
        }
    }
    
    public boolean isBeta() {
        return this.flags.contains(WorldType.BETA);
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Worlds {
        public Map<String, WorldProfile> worlds;
    }
    
    
}
