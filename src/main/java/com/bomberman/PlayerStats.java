package com.bomberman;

public class PlayerStats {
    private String playerName;
    private int victories;
    private int defeats;

    public PlayerStats(String playerName, int victories, int defeats) {
        this.playerName = playerName;
        this.victories = victories;
        this.defeats = defeats;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getVictories() {
        return victories;
    }

    public int getDefeats() {
        return defeats;
    }

    public void addVictory() {
        victories++;
    }

    public void addDefeat() {
        defeats++;
    }

    @Override
    public String toString() {
        return playerName + "," + victories + "," + defeats;
    }
}