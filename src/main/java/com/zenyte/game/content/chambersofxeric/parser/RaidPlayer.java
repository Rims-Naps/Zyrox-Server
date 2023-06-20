package com.zenyte.game.content.chambersofxeric.parser;

import lombok.ToString;

@ToString
public class RaidPlayer
{
    String playerName;
    int points;

    public RaidPlayer(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPoints() {
        return points;
    }

    public RaidPlayer(String playerName, int points) {
        this.playerName = playerName;
        this.points = points;
    }
}
