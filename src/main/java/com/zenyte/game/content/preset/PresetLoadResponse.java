package com.zenyte.game.content.preset;

import com.zenyte.game.util.Colour;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 17/09/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@AllArgsConstructor
public enum PresetLoadResponse {

    FLAWLESS_LOAD(Colour.RS_GREEN.wrap("successfully")),
    ALTERNATE_LOAD(Colour.YELLOW.wrap("with alterations")),
    INCOMPLETE_LOAD("<col=df0b08>partially</col>");

    private final String response;
}
