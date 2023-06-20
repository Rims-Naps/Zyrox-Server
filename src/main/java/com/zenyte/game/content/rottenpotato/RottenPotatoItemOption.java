package com.zenyte.game.content.rottenpotato;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Christopher
 * @since 3/27/2020
 */
@RequiredArgsConstructor
@Getter
public enum RottenPotatoItemOption {
    NONE("None", "None"),
    UTILITY("Utility", "Utility"),
    PUNISHMENT("Punishment", "Punishment");
    private final String itemOption;
    private final String dialogueTitle;
}
