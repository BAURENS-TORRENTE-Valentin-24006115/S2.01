package com.bomberman;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private MediaPlayer musicPlayer;
    private Map<String, MediaPlayer> soundEffects;
    private double musicVolume = 0.5;
    private double effectsVolume = 0.8;
    private boolean musicEnabled = true;
    private boolean effectsEnabled = true;

    private MediaPlayer currentEffect;

    public AudioManager() {
        soundEffects = new HashMap<>();
        loadSoundEffects();
    }

    private void loadSoundEffects() {
        soundEffects = new HashMap<>();
        loadEffect("place_bomb.mp3");
        loadEffect("explosion.mp3");
        loadEffect("pickup.mp3");
        loadEffect("death.mp3");
        loadEffect("win.mp3");
    }

    private void loadEffect(String filename) {
        try {
            String resourcePath = "/sounds/" + filename;
            URL resource = getClass().getResource(resourcePath);

            if (resource == null) {
                System.err.println("ERREUR: Ressource introuvable: " + resourcePath);
                return;
            }

            Media sound = new Media(resource.toString());
            MediaPlayer player = new MediaPlayer(sound);
            // Préconfigurer le volume
            player.setVolume(effectsVolume);
            soundEffects.put(filename, player);
            System.out.println("Son chargé avec succès: " + filename);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du son " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playMusic(String musicFile) {
        if (!musicEnabled) return;

        try {
            // Arrêter la musique en cours si elle existe
            if (musicPlayer != null) {
                musicPlayer.stop();
            }

            URL resource = getClass().getResource("/music/" + musicFile);
            if (resource != null) {
                Media music = new Media(resource.toString());
                musicPlayer = new MediaPlayer(music);
                musicPlayer.setVolume(musicVolume);
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                musicPlayer.play();
            } else {
                System.err.println("Fichier musique non trouvé: " + musicFile);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture de la musique: " + e.getMessage());
        }
    }

    public void playEffect(String effectName) {
        if (!effectsEnabled) return;

        System.out.println("Tentative de lecture du son: " + effectName);

        // Créer un nouveau MediaPlayer à chaque fois au lieu de réutiliser
        try {
            MediaPlayer originalPlayer = soundEffects.get(effectName);
            if (originalPlayer == null) {
                System.err.println("Effet sonore non trouvé: " + effectName);
                return;
            }

            // Créer un nouveau MediaPlayer avec le même média
            Media media = originalPlayer.getMedia();
            currentEffect = new MediaPlayer(media);
            currentEffect.setVolume(effectsVolume);

            // Définir une action à la fin pour permettre au GC de récupérer l'objet
            currentEffect.setOnEndOfMedia(() -> {
                currentEffect.dispose();
            });

            // Jouer immédiatement
            currentEffect.play();

        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du son: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    public void setMusicVolume(double volume) {
        this.musicVolume = volume;
        if (musicPlayer != null) {
            musicPlayer.setVolume(volume);
        }
    }

    public void setEffectsVolume(double volume) {
        this.effectsVolume = volume;
        for (MediaPlayer player : soundEffects.values()) {
            player.setVolume(volume);
        }
    }

    public void toggleMusic() {
        musicEnabled = !musicEnabled;
        if (!musicEnabled && musicPlayer != null) {
            musicPlayer.pause();
        } else if (musicPlayer != null) {
            musicPlayer.play();
        }
    }

    public void toggleEffects() {
        effectsEnabled = !effectsEnabled;
    }
}