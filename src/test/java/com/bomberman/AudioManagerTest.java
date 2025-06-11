// src/test/java/com/bomberman/AudioManagerTest.java
package com.bomberman;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AudioManagerTest {

    @Test
    void testAudioManagerInitialization() {
        assertDoesNotThrow(() -> {
            AudioManager audioManager = new AudioManager();
            assertNotNull(audioManager);
        });
    }

    @Test
    void testPlayEffectWithInvalidName() {
        AudioManager audioManager = new AudioManager();
        // Ne doit pas lancer d'exception même si l'effet n'existe pas
        assertDoesNotThrow(() -> audioManager.playEffect("not_exist.mp3"));
    }

    @Test
    void testPlayMusicWithInvalidFile() {
        AudioManager audioManager = new AudioManager();
        // Ne doit pas lancer d'exception même si la musique n'existe pas
        assertDoesNotThrow(() -> audioManager.playMusic("not_exist.mp3"));
    }

    @Test
    void testStopMusicWithoutMusic() {
        AudioManager audioManager = new AudioManager();
        // Ne doit pas lancer d'exception même si aucune musique n'est lancée
        assertDoesNotThrow(audioManager::stopMusic);
    }
}