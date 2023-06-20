package com.zenyte.game.world.entity.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Notification {

    @Getter
    private final String title;

    @Getter
    private final String message;

    @Getter
    private final int colour;
}