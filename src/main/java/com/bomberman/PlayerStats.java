package com.bomberman;

/**
 * Représente les statistiques d'un joueur (nom, victoires, défaites).
 * <p>
 * Utilisé par StatsManager pour la gestion des scores.
 * </p>
 * @author Valentin B.
 */
public class PlayerStats {
    private String playerName;
    private int victories;
    private int defeats;

    /**
     * Constructeur pour initialiser les statistiques d'un joueur.
     *
     * @param playerName le nom du joueur
     * @param victories le nombre de victoires
     * @param defeats le nombre de défaites
     */
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