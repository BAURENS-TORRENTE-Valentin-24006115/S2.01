package com.bomberman;

import org.junit.jupiter.api.*;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class StatsManagerTest {

    private StatsManager statsManager;
    private static final String TEST_FILE = "player_stats.txt";

    @BeforeEach
    void setUp() {
        // Supprime le fichier de stats avant chaque test pour un environnement propre
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
        statsManager = new StatsManager();
    }

    @AfterEach
    void tearDown() {
        // Nettoyage du fichier après chaque test
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testRecordVictoryAndDefeat() {
        String player = "Alice";
        statsManager.recordVictory(player);
        statsManager.recordDefeat(player);

        PlayerStats stats = statsManager.getPlayerStats(player);
        assertEquals(1, stats.getVictories());
        assertEquals(1, stats.getDefeats());
    }

    @Test
    void testSaveAndLoadStats() {
        String player = "Bob";
        statsManager.recordVictory(player);
        statsManager.saveStats();

        // Recharge les stats depuis le fichier
        StatsManager newManager = new StatsManager();
        PlayerStats stats = newManager.getPlayerStats(player);
        assertEquals(1, stats.getVictories());
        assertEquals(0, stats.getDefeats());
    }

    @Test
    void testGetPlayerStatsForUnknownPlayer() {
        PlayerStats stats = statsManager.getPlayerStats("Unknown");
        assertEquals(0, stats.getVictories());
        assertEquals(0, stats.getDefeats());
    }
}