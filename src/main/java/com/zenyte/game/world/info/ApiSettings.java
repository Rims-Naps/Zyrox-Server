package com.zenyte.game.world.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Corey
 * @since 20/05/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiSettings {
    private boolean enabled;
    private String scheme;
    private String host;
    private int port;
    private String token;
}
