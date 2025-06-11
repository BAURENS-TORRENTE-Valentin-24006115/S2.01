package com.bomberman;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StatsManager {
    private static final String STATS_FILE = "player_stats.txt";
    private Map<String, PlayerStats> playerStatsMap = new HashMap<>();

    public StatsManager() {
        loadStats();
    }

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

    public PlayerStats getPlayerStats(String playerName) {
        return playerStatsMap.getOrDefault(playerName, new PlayerStats(playerName, 0, 0));
    }
}