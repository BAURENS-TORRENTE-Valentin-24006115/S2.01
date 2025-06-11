package com.bomberman;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStatsTest {

    @Test
    void testInitialValues() {
        PlayerStats stats = new PlayerStats("Alice", 3, 2);
        assertEquals("Alice", stats.getPlayerName());
        assertEquals(3, stats.getVictories());
        assertEquals(2, stats.getDefeats());
    }

    @Test
    void testAddVictory() {
        PlayerStats stats = new PlayerStats("Bob", 1, 0);
        stats.addVictory();
        assertEquals(2, stats.getVictories());
    }

    @Test
    void testAddDefeat() {
        PlayerStats stats = new PlayerStats("Bob", 1, 0);
        stats.addDefeat();
        assertEquals(1, stats.getDefeats());
    }

    @Test
    void testToStringFormat() {
        PlayerStats stats = new PlayerStats("Eve", 5, 7);
        assertEquals("Eve,5,7", stats.toString());
    }
}