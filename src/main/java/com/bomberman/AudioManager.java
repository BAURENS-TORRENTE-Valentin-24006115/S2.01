package com.bomberman;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Gestionnaire des sons et musiques du jeu Bomberman.
 * <p>
 * Permet de jouer, arrêter et configurer le volume des musiques et effets sonores.
 * </p>
 * @author Valentin B. - Thomas A.
 */
public class AudioManager {
    private MediaPlayer musicPlayer;
    private Map<String, Media> soundEffects;
    private double musicVolume = 0.5;
    private double effectsVolume = 0.8;
    private boolean musicEnabled = true;
    private boolean effectsEnabled = true;

    /**
     * Constructeur de la classe AudioManager.
     * Initialise le gestionnaire de sons et charge les effets sonores.
     */
    public AudioManager() {
        soundEffects = new HashMap<>();
        loadSoundEffects();
    }

    /**
     * Charge les effets sonores par défaut.
     * <p>
     * Les fichiers doivent être placés dans le répertoire /sounds/ du classpath.
     * </p>
     */
    private void loadSoundEffects() {
        soundEffects.clear();
        loadEffect("place_bomb.mp3");
        loadEffect("explosion.mp3");
        loadEffect("pickup.mp3");
        loadEffect("death.mp3");
        loadEffect("win.mp3");
    }

    /**
     * Charge un effet sonore à partir d'un fichier.
     *
     * @param filename nom du fichier audio (dans /sounds/)
     */
    private void loadEffect(String filename) {
        try {
            String resourcePath = "/sounds/" + filename;
            URL resource = getClass().getResource(resourcePath);

            if (resource == null) {
                System.err.println("ERREUR: Ressource introuvable: " + resourcePath);
                return;
            }

            Media sound = new Media(resource.toString());
            soundEffects.put(filename, sound);
            System.out.println("Son chargé avec succès: " + filename);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du son " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Joue une musique de fond (remplace la précédente si besoin).
     *
     * @param musicFile nom du fichier audio (dans /sounds/)
     */
    public void playMusic(String musicFile) {
        if (!musicEnabled) return;

        try {
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

    /**
     * Joue un effet sonore.
     *
     * @param effectName nom de l'effet sonore (doit correspondre à une clé dans soundEffects)
     */
    public void playEffect(String effectName) {
        if (!effectsEnabled) return;

        Media media = soundEffects.get(effectName);
        if (media == null) {
            System.err.println("Effet sonore non trouvé: " + effectName);
            return;
        }

        try {
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(effectsVolume);
            player.setOnEndOfMedia(player::dispose);
            player.setOnError(player::dispose);
            player.play();
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du son: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Arrête la musique de fond en cours.
     */
    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }
}