package com.bomberman;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire des sons et musiques du jeu Bomberman.
 * <p>
 * Permet de jouer, arrêter et configurer le volume des musiques et effets sonores.
 * </p>
 * @author Valentin B.
 */
public class StatsManager {
    private static final String STATS_FILE = "player_stats.txt";
    private Map<String, PlayerStats> playerStatsMap = new HashMap<>();

    public StatsManager() {
        loadStats();
    }

    /**
     * Classe représentant les statistiques d'un joueur.
     */
    private void loadStats() {
        File file = new File(STATS_FILE);
        if (!file.exists()) {
            return; // Le fichier sera créé lors de la première sauvegarde
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    try {
                        PlayerStats stats = new PlayerStats(
                                parts[0],
                                Integer.parseInt(parts[1]),
                                Integer.parseInt(parts[2])
                        );
                        playerStatsMap.put(parts[0], stats);
                    } catch (NumberFormatException e) {
                        System.err.println("Format invalide pour la ligne: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
        }
    }

    /**
     * Enregistre les statistiques des joueurs dans le fichier.
     * Chaque ligne contient le nom du joueur, le nombre de victoires et de défaites.
     */
    public void saveStats() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATS_FILE))) {
            for (PlayerStats stats : playerStatsMap.values()) {
                writer.write(stats.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des statistiques: " + e.getMessage());
        }
    }

    /**
     * Enregistre une victoire pour un joueur.
     * @param playerName le nom du joueur qui a gagné
     */
    public void recordVictory(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) return;

        PlayerStats stats = playerStatsMap.get(playerName);
        if (stats == null) {
            stats = new PlayerStats(playerName, 1, 0);
            playerStatsMap.put(playerName, stats);
        } else {
            stats.addVictory();
        }
        saveStats();
    }

    /**
     * Enregistre une défaite pour un joueur.
     * @param playerName le nom du joueur qui a perdu
     */
    public void recordDefeat(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) return;

        PlayerStats stats = playerStatsMap.get(playerName);
        if (stats == null) {
            stats = new PlayerStats(playerName, 0, 1);
            playerStatsMap.put(playerName, stats);
        } else {
            stats.addDefeat();
        }
        saveStats();
    }

    /**
     * Récupère les statistiques d'un joueur.
     * @param playerName le nom du joueur
     * @return les statistiques du joueur, ou un objet PlayerStats vide si le joueur n'existe pas
     */
    public PlayerStats getPlayerStats(String playerName) {
        return playerStatsMap.getOrDefault(playerName, new PlayerStats(playerName, 0, 0));
    }
}