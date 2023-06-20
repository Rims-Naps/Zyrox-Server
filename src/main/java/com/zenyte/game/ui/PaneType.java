package com.zenyte.game.ui;

import mgi.types.config.enums.EnumDefinitions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tommeh | 1 apr. 2018 | 20:00:07
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@AllArgsConstructor
public enum PaneType {

    FIXED(548, 1129),
    RESIZABLE(161, 1130),
    SIDE_PANELS(164, 1131),
    FULL_SCREEN(165, 1132),
    ORB_OF_OCULUS(16, -1),
    GAME_SCREEN(80, -1),
    CHATBOX(162, -1),
    JOURNAL_TAB_HEADER(629, -1),
    MOBILE(601, 1745);

    @Getter private final int id;
    @Getter private final int enumId;
    
    public final EnumDefinitions getEnum() {
    	if (enumId == -1) {
    		throw new RuntimeException("No enum exists for the pane: " + this + ".");
    	}
    	return EnumDefinitions.get(enumId);
    }

}