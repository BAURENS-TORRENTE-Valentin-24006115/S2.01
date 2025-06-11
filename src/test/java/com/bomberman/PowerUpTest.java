package com.bomberman;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PowerUpTest {

    @Test
    void testPowerUpCreationAndGetters() {
        PowerUp powerUp = new PowerUp(3, 5, PowerUp.Type.BOMB_UP);

        assertEquals(3, powerUp.getX());
        assertEquals(5, powerUp.getY());
        assertEquals(PowerUp.Type.BOMB_UP, powerUp.getType());
        assertTrue(powerUp.isActive());

        powerUp.collect();
        assertFalse(powerUp.isActive());
    }
}