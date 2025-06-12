package com.bomberman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/**
 * Classe pour gérer les sprites des joueurs dans le jeu Bomberman.
 * <p>
 * Permet de charger des spritesheets, d'extraire des sprites individuels,
 * de gérer les animations et de fournir des sprites de remplacement si nécessaire.
 * </p>
 * @author Thomas A. - Valentin B.
 */
public class SpriteManager {
    private final Image[] playerSpritesheets;  // Une spritesheet par joueur
    private static final int SPRITE_WIDTH = 28; // Largeur d'une case
    private static final int SPRITE_HEIGHT = 28; // Hauteur d'une case
    private static final int FRAMES_PER_ANIMATION = 3;

    // Nombre de lignes et colonnes dans chaque spritesheet
    private static final int ROWS = 6; // 6 lignes d'animation
    private static final int COLUMNS = 3; // 3 frames par animation

    // Représente une case spécifique dans la spritesheet
    public static class SpriteCell {
        private final int row;
        private final int column;

        public SpriteCell(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public int getRow() { return row; }
        public int getColumn() { return column; }
    }

    // Définir les animations avec leurs lignes correspondantes
    public enum Animation {
        WALK_DOWN(0),
        WALK_UP(1),
        WALK_LEFT(2),
        CELEBRATE(3),
        DEATH(4),
        WALK_RIGHT(5),
        IDLE(0); // Utilise WALK_DOWN pour idle

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

    public SpriteManager(Image[] playerSpritesheets) {
        this.playerSpritesheets = playerSpritesheets;
    }

    /**
     * Vérifie si les spritesheets sont correctement chargées
     */
    public boolean areSpritesSheetsLoaded() {
        if (playerSpritesheets == null) return false;

        for (Image sheet : playerSpritesheets) {
            if (sheet == null || sheet.isError()) return false;
        }
        return true;
    }

    /**
     * Extrait un sprite en utilisant une case spécifique
     */
    public Image getSprite(int playerIndex, SpriteCell cell) {
        if (playerSpritesheets == null || playerIndex >= playerSpritesheets.length ||
                playerSpritesheets[playerIndex] == null) {
            return createPlaceholderSprite(playerIndex);
        }

        Image spritesheet = playerSpritesheets[playerIndex];

        // Calculer les coordonnées dans la spritesheet
        int x = cell.getColumn() * SPRITE_WIDTH + playerIndex * 2; // Décalage pour chaque joueur
        int y = cell.getRow() * SPRITE_HEIGHT + cell.getRow();

        // S'assurer que les coordonnées sont dans les limites
        if (x + SPRITE_WIDTH > spritesheet.getWidth()) x = 0;
        if (y + SPRITE_HEIGHT > spritesheet.getHeight()) y = 0;

        // Extraire le sprite
        return new WritableImage(spritesheet.getPixelReader(),
                x, y, SPRITE_WIDTH, SPRITE_HEIGHT);
    }

    /**
     * Extrait un sprite en utilisant une animation et une frame
     * Cette méthode garde la compatibilité avec le code existant
     */
    public Image getSprite(int playerIndex, Animation animation, int frame) {
        int column = (animation == Animation.IDLE) ? 0 : (frame % FRAMES_PER_ANIMATION);
        return getSprite(playerIndex, new SpriteCell(animation.getRow(), column));
    }

    /**
     * Crée un sprite de remplacement coloré si la spritesheet n'est pas disponible
     */
    /**
     * Crée un sprite de remplacement coloré si la spritesheet n'est pas disponible
     */
    private Image createPlaceholderSprite(int playerIndex) {
        int[] colors = {0xFFFFFFFF, 0xFFFF69B4, 0xFFFF4500, 0xFF0000FF}; // Blanc, Rose, Orange/Rouge, Bleu

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
     * Retourne toutes les cases d'une animation spécifique
     */
    public SpriteCell[] getCellsForAnimation(Animation animation) {
        SpriteCell[] cells = new SpriteCell[FRAMES_PER_ANIMATION];
        for (int i = 0; i < FRAMES_PER_ANIMATION; i++) {
            cells[i] = new SpriteCell(animation.getRow(), i);
        }
        return cells;
    }

    /**
     * Retourne une case spécifique dans la grille
     */
    public SpriteCell getCell(int row, int column) {
        return new SpriteCell(row, column);
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

    /**
     * Retourne le nombre total de lignes dans la spritesheet
     */
    public int getTotalRows() {
        return ROWS;
    }

    /**
     * Retourne le nombre total de colonnes dans la spritesheet
     */
    public int getTotalColumns() {
        return COLUMNS;
    }
}

/**
 * Classe pour gérer les animations des joueurs
 * Compatible avec le nouveau système de cases
 */
class PlayerAnimator {
    private final ImageView imageView;
    private final SpriteManager spriteManager;
    private final int playerIndex;
    private Timeline animationTimeline;
    private int currentFrame = 0;
    private SpriteManager.Animation currentAnimation;
    private SpriteManager.SpriteCell[] currentAnimationCells;
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
     * Met à jour l'image avec le sprite actuel
     */
    private void updateSprite() {
        if (currentAnimationCells != null && isAnimating) {
            // Utiliser le système de cases pour l'animation
            imageView.setImage(
                    spriteManager.getSprite(playerIndex, currentAnimationCells[currentFrame])
            );
        } else {
            // Utiliser le système d'animation original
            imageView.setImage(
                    spriteManager.getSprite(playerIndex, currentAnimation, currentFrame)
            );
        }
    }

    /**
     * Afficher une case spécifique
     */
    public void showCell(SpriteManager.SpriteCell cell) {
        dispose(); // Arrête toute animation en cours
        isAnimating = false;
        imageView.setImage(spriteManager.getSprite(playerIndex, cell));
    }

    /**
     * Arrête les animations en cours
     */
    public void dispose() {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
        isAnimating = false;
    }

    /**
     * Démarre une animation selon la direction
     */
    public void startDirectionAnimation(SpriteManager.Direction direction) {
        currentAnimation = SpriteManager.getAnimationForDirection(direction);
        currentAnimationCells = spriteManager.getCellsForAnimation(currentAnimation);
        startAnimation(125, true); // 125ms par frame, en boucle
    }

    /**
     * Positionne le personnage en idle
     */
    public void idle() {
        dispose();
        currentAnimation = SpriteManager.Animation.IDLE;
        currentFrame = 0; // Frame du milieu pour idle
        updateSprite();
    }

    /**
     * Démarre l'animation de mort
     */
    public void playDeathAnimation() {
        dispose();
        currentAnimation = SpriteManager.Animation.DEATH;
        currentAnimationCells = spriteManager.getCellsForAnimation(currentAnimation);
        startAnimation(200, false); // 200ms par frame, pas de boucle
        // Après la dernière frame, on fait disparaitre le joueur
        animationTimeline.setOnFinished(e -> {
            imageView.setImage(null); // Effacer l'image après la mort
            isAnimating = false; // Marquer comme non animé
        });
    }

    /**
     * Démarre l'animation de célébration
     */
    public void celebrate() {
        dispose();
        currentAnimation = SpriteManager.Animation.CELEBRATE;
        currentAnimationCells = spriteManager.getCellsForAnimation(currentAnimation);
        startAnimation(300, true); // 300ms par frame, en boucle
    }

    /**
     * Démarre une animation avec les paramètres spécifiés
     */
    /**
     * Démarre une animation avec les paramètres spécifiés
     */
    private void startAnimation(int frameDuration, boolean loop) {
        dispose();
        currentFrame = 0;
        isAnimating = true;

        animationTimeline = new Timeline(
                new KeyFrame(Duration.millis(frameDuration), e -> {
                    updateSprite();
                    if (currentAnimationCells != null) {
                        currentFrame = (currentFrame + 1) % currentAnimationCells.length;
                    } else {
                        currentFrame = (currentFrame + 1) % 3;
                    }
                })
        );

        if (loop) {
            animationTimeline.setCycleCount(Timeline.INDEFINITE);
        } else {
            animationTimeline.setCycleCount(currentAnimationCells != null ?
                    currentAnimationCells.length : 3);
        }

        animationTimeline.play();
    }

    /**
     * Retourne une séquence de cases personnalisée pour créer une animation
     */
    public SpriteManager.SpriteCell[] createCustomAnimationSequence(int[] rows, int[] columns) {
        if (rows.length != columns.length) {
            throw new IllegalArgumentException("Les tableaux de lignes et colonnes doivent avoir la même taille");
        }

        SpriteManager.SpriteCell[] cells = new SpriteManager.SpriteCell[rows.length];
        for (int i = 0; i < rows.length; i++) {
            cells[i] = new SpriteManager.SpriteCell(rows[i], columns[i]);
        }
        return cells;
    }

    /**
     * Joue une animation personnalisée à partir d'une séquence de cases
     */
    public void playCustomAnimation(SpriteManager.SpriteCell[] cells, int frameDuration, boolean loop) {
        this.currentAnimationCells = cells;
        startAnimation(frameDuration, loop);
    }

    /**
     * Affiche une séquence de cellules personnalisée
     */
    public void showCustomCells(SpriteManager.SpriteCell[] cells, int frameDuration, boolean loop) {
        dispose();
        currentAnimationCells = cells;
        startAnimation(frameDuration, loop);
    }
}