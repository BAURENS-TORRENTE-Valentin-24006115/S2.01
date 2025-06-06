package com.bomberman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class SpriteManager {
    private Image spritesheet;
    private static final int SPRITE_WIDTH = 16;
    private static final int SPRITE_HEIGHT = 24;
    private static final int COLUMNS_PER_PLAYER = 3;
    private static final int TOTAL_COLUMNS = 12;
    private static final int TOTAL_ROWS = 6;

    // Définir les animations disponibles
    public enum Animation {
        IDLE(0),
        WALK_DOWN(0),
        WALK_UP(1),
        WALK_RIGHT(2),
        CELEBRATE(3),
        DEATH(4),
        WALK_LEFT(5);

        private final int row;

        Animation(int row) {
            this.row = row;
        }

        public int getRow() {
            return row;
        }
    }

    public enum Direction {
        DOWN, UP, LEFT, RIGHT, IDLE
    }

    public SpriteManager(Image spritesheet) {
        this.spritesheet = spritesheet;
    }

    /**
     * Extrait un sprite spécifique de la spritesheet
     * @param playerIndex Index du joueur (0-3)
     * @param animation Type d'animation
     * @param frame Frame de l'animation (0-2, sauf pour IDLE qui n'a qu'une frame)
     * @return Image du sprite
     */
    public Image getSprite(int playerIndex, Animation animation, int frame) {
        if (spritesheet == null) {
            return createPlaceholderSprite(playerIndex);
        }

        int baseColumn = playerIndex * COLUMNS_PER_PLAYER;
        int column = baseColumn;

        // Pour l'animation IDLE, on prend seulement la première colonne
        if (animation == Animation.IDLE) {
            column = baseColumn;
        } else {
            // Pour les autres animations, on cycle entre les 3 frames
            column = baseColumn + (frame % COLUMNS_PER_PLAYER);
        }

        int row = animation.getRow();

        // Calculer les coordonnées dans la spritesheet
        int x = column * SPRITE_WIDTH;
        int y = row * SPRITE_HEIGHT;

        // Extraire le sprite
        WritableImage sprite = new WritableImage(spritesheet.getPixelReader(),
                x, y, SPRITE_WIDTH, SPRITE_HEIGHT);

        return sprite;
    }

    /**
     * Crée un sprite de remplacement coloré si la spritesheet n'est pas disponible
     */
    private Image createPlaceholderSprite(int playerIndex) {
        int[] colors = {0xFF3498DB, 0xFFE74C3C, 0xFF27AE60, 0xFF9B59B6}; // Bleu, Rouge, Vert, Violet

        WritableImage image = new WritableImage(SPRITE_WIDTH, SPRITE_HEIGHT);
        var pixelWriter = image.getPixelWriter();

        int color = colors[playerIndex % colors.length];

        for (int x = 0; x < SPRITE_WIDTH; x++) {
            for (int y = 0; y < SPRITE_HEIGHT; y++) {
                pixelWriter.setArgb(x, y, color);
            }
        }

        return image;
    }

    /**
     * Détermine l'animation appropriée basée sur la direction
     */
    public static Animation getAnimationForDirection(Direction direction) {
        switch (direction) {
            case UP: return Animation.WALK_UP;
            case DOWN: return Animation.WALK_DOWN;
            case LEFT: return Animation.WALK_LEFT;
            case RIGHT: return Animation.WALK_RIGHT;
            case IDLE:
            default: return Animation.IDLE;
        }
    }
}

/**
 * Classe pour gérer les animations des joueurs
 */
class PlayerAnimator {
    private ImageView imageView;
    private SpriteManager spriteManager;
    private int playerIndex;
    private Timeline animationTimeline;
    private int currentFrame = 0;
    private SpriteManager.Animation currentAnimation;
    private boolean isAnimating = false;

    public PlayerAnimator(ImageView imageView, SpriteManager spriteManager, int playerIndex) {
        this.imageView = imageView;
        this.spriteManager = spriteManager;
        this.playerIndex = playerIndex;
        this.currentAnimation = SpriteManager.Animation.IDLE;

        // Initialiser avec le sprite idle
        updateSprite();
    }

    /**
     * Démarre une animation
     */
    public void startAnimation(SpriteManager.Animation animation) {
        if (currentAnimation == animation && isAnimating) {
            return; // Animation déjà en cours
        }

        stopAnimation();
        currentAnimation = animation;
        currentFrame = 0;

        if (animation == SpriteManager.Animation.IDLE) {
            updateSprite();
            return;
        }

        // Créer une timeline pour l'animation
        animationTimeline = new Timeline(
                new KeyFrame(Duration.millis(200), e -> {
                    currentFrame = (currentFrame + 1) % 3; // Cycle entre 0, 1, 2
                    updateSprite();
                })
        );

        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
        isAnimating = true;
    }

    /**
     * Démarre une animation de direction basée sur le mouvement
     */
    public void startDirectionAnimation(SpriteManager.Direction direction) {
        SpriteManager.Animation animation = SpriteManager.getAnimationForDirection(direction);
        startAnimation(animation);
    }

    /**
     * Arrête l'animation en cours
     */
    public void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
        }
        isAnimating = false;
    }

    /**
     * Joue une animation de célébration
     */
    public void celebrate() {
        startAnimation(SpriteManager.Animation.CELEBRATE);
    }

    /**
     * Joue une animation de mort
     */
    public void playDeathAnimation() {
        stopAnimation();
        currentAnimation = SpriteManager.Animation.DEATH;
        currentFrame = 0;

        // Animation de mort avec fade out
        animationTimeline = new Timeline(
                new KeyFrame(Duration.millis(150), e -> {
                    if (currentFrame < 3) {
                        updateSprite();
                        currentFrame++;
                    } else {
                        // Fade out après l'animation de mort
                        imageView.setOpacity(imageView.getOpacity() - 0.1);
                        if (imageView.getOpacity() <= 0.1) {
                            stopAnimation();
                        }
                    }
                })
        );

        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
        isAnimating = true;
    }

    /**
     * Revient à l'animation idle
     */
    public void idle() {
        startAnimation(SpriteManager.Animation.IDLE);
    }

    /**
     * Met à jour le sprite affiché
     */
    private void updateSprite() {
        Image sprite = spriteManager.getSprite(playerIndex, currentAnimation, currentFrame);
        imageView.setImage(sprite);
    }

    /**
     * Nettoie les ressources
     */
    public void dispose() {
        stopAnimation();
    }
}