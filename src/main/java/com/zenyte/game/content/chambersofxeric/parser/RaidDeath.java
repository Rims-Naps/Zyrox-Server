package com.zenyte.game.content.chambersofxeric.parser;

public class RaidDeath {
    private String playerName;
    private String room;
    private String diedTo;
    private int pointsLost;

    public RaidDeath(String playerName, String room, String diedTo, int pointsLost) {
        this.playerName = playerName;
        this.room = room;
        this.diedTo = diedTo;
        this.pointsLost = pointsLost;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRoom() {
        return room;
    }

    public String getDiedTo() {
        return diedTo;
    }

    public int getPointsLost() {
        return pointsLost;
    }
}
