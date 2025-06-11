package com.bomberman;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.bomberman.BotAI;

import java.net.URL;
import java.util.*;

public class BombermanGame implements Initializable {

    @FXML private GridPane gameGrid;
    @FXML private Label player1Label;
    @FXML private Label player2Label;
    @FXML private Label player3Label;
    @FXML private Label player4Label;
    @FXML private Label winnerLabel;

    private static final int GRID_SIZE = 15;
    private static final int CELL_SIZE = 40;
    private static final int MOVEMENT_DELAY = 200; // ms entre chaque mouvement

    // Images du jeu
    private Image wallImage;
    private Image destructibleBlockImage;
    private Image bombImage;
    private Image explosionImage;
    private Image spritesheetImage;

    // Système de sprites
    private SpriteManager spriteManager;
    private StatsManager statsManager;

    // Variable pour la musique
    private AudioManager audioManager;

    private Player[] players = new Player[4];
    private boolean[][] walls;
    private boolean[][] destructibleBlocks;
    private List<Bomb> bombs = new ArrayList<>();
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private Map<Player, Long> lastMoveTime = new HashMap<>();

    private List<PowerUp> powerUps = new ArrayList<>();
    private Image[] powerUpImages = new Image[PowerUp.Type.values().length];

    private Timeline gameLoop;
    private boolean gameEnded = false;
    private boolean alternativeStyle = false; // Pour basculer entre les styles
    private boolean soloMode = false;
    private BotAI botAI = new BotAI(this);

    /**
     * Définit les noms des joueurs pour le mode multijoueur
     * @param playerNames tableau contenant les noms des joueurs (jusqu'à 4)
     */
    public void setPlayerNames(String[] playerNames) {
        // Sauvegarder les noms des joueurs
        String[] savedNames = new String[4];
        for (int i = 0; i < playerNames.length && i < 4; i++) {
            if (playerNames[i] != null && !playerNames[i].trim().isEmpty()) {
                savedNames[i] = playerNames[i].trim();
            }
        }

        // Réinitialiser le jeu
        restartGame();

        // Restaurer les noms des joueurs après réinitialisation
        for (int i = 0; i < savedNames.length; i++) {
            if (savedNames[i] != null) {
                players[i].name = savedNames[i];
            }
        }

        // Mettre à jour l'interface
        updateUI();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statsManager = new StatsManager();
        loadImages();
        initializeGame();
        setupGameLoop();
        updateUI();
        audioManager = new AudioManager();

        // Démarrer la musique de fond
        audioManager.playMusic("background_music.mp3");
    }

    public void enableSoloMode() {
        soloMode = true;
        restartGame();
    }

    public void setAlternativeStyle(boolean alternativeStyle) {
        this.alternativeStyle = alternativeStyle;
        // Recharger les images si nécessaire, que le jeu soit en mode solo ou non
        if (!gameEnded) {
            loadImages();
        }
    }

    private void loadImages() {
        try {
            if (alternativeStyle) {
                // Charger les images des éléments du jeu
                wallImage = new Image(getClass().getResourceAsStream("/images/wall_alt.png"));
                destructibleBlockImage = new Image(getClass().getResourceAsStream("/images/destructible_block_alt.png"));
                bombImage = new Image(getClass().getResourceAsStream("/images/bomb.png"));
                explosionImage = new Image(getClass().getResourceAsStream("/images/explosion.png"));

                // Charger les spritesheets individuelles pour chaque joueur
                Image[] playerSpritesheets = new Image[4];
                playerSpritesheets[0] = new Image(getClass().getResourceAsStream("/images/players_spritesheets/player1_spritesheet_alt.png"));
                playerSpritesheets[1] = new Image(getClass().getResourceAsStream("/images/players_spritesheets/player2_spritesheet_alt.png"));
                playerSpritesheets[2] = new Image(getClass().getResourceAsStream("/images/players_spritesheets/player3_spritesheet_alt.png"));
                playerSpritesheets[3] = new Image(getClass().getResourceAsStream("/images/players_spritesheets/player4_spritesheet_alt.png"));

                spriteManager = new SpriteManager(playerSpritesheets);

            } else {
                // Charger les images des éléments du jeu
                wallImage = new Image(getClass().getResourceAsStream("/images/wall.jpg"));
                destructibleBlockImage = new Image(getClass().getResourceAsStream("/images/destructible_block.jpg"));
                bombImage = new Image(getClass().getResourceAsStream("/images/bomb.png"));
                explosionImage = new Image(getClass().getResourceAsStream("/images/explosion.png"));

                // Charger les spritesheets individuelles pour chaque joueur
                Image[] playerSpritesheets = new Image[4];
                playerSpritesheets[0] = new Image(getClass().getResourceAsStream("/images/players_spritesheets/player1_spritesheet.png"));
                playerSpritesheets[1] = new Image(getClass().getResourceAsStream("/images/players_spritesheets/player2_spritesheet.png"));
                playerSpritesheets[2] = new Image(getClass().getResourceAsStream("/images/players_spritesheets/player3_spritesheet.png"));
                playerSpritesheets[3] = new Image(getClass().getResourceAsStream("/images/players_spritesheets/player4_spritesheet.png"));

                spriteManager = new SpriteManager(playerSpritesheets);
            }

            // Vérifier si les images sont correctement chargées
            if (spriteManager.areSpritesSheetsLoaded()) {
                System.out.println("Toutes les spritesheets des joueurs sont chargées correctement");
            } else {
                System.err.println("Certaines spritesheets n'ont pas pu être chargées");
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
            e.printStackTrace();
            // Créer des images de remplacement si les fichiers ne sont pas trouvés
            createPlaceholderImages();
        }

        // Charger les power-ups
        try {
            powerUpImages[PowerUp.Type.BOMB_UP.ordinal()] = new Image(getClass().getResourceAsStream("/images/powerup_bomb.png"));
            powerUpImages[PowerUp.Type.FIRE_UP.ordinal()] = new Image(getClass().getResourceAsStream("/images/powerup_fire.png"));
            powerUpImages[PowerUp.Type.KICK_BOMB.ordinal()] = new Image(getClass().getResourceAsStream("/images/powerup_kick.png"));
            powerUpImages[PowerUp.Type.INVINCIBLE.ordinal()] = new Image(getClass().getResourceAsStream("/images/powerup_invincible.png"));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images de power-ups: " + e.getMessage());
            createPlaceholderPowerUpImages();
        }
    }

    private void createPlaceholderImages() {
        // Images de remplacement colorées (32x32 pixels)
        wallImage = createColoredImage(32, 32, 0xFF7F8C8D);
        destructibleBlockImage = createColoredImage(32, 32, 0xFFE67E22);
        bombImage = createColoredImage(28, 28, 0xFF2C3E50);
        explosionImage = createColoredImage(32, 32, 0xFFF1C40F);

        // Créer un SpriteManager avec une spritesheet nulle (utilisera les placeholders)
        spriteManager = new SpriteManager(null);
    }

    private void createPlaceholderPowerUpImages() {
        // Images de remplacement pour les power-ups
        powerUpImages[PowerUp.Type.BOMB_UP.ordinal()] = createColoredImage(32, 32, 0xFF3498DB);
        powerUpImages[PowerUp.Type.FIRE_UP.ordinal()] = createColoredImage(32, 32, 0xFFE74C3C);
        powerUpImages[PowerUp.Type.KICK_BOMB.ordinal()] = createColoredImage(32, 32, 0xFF2ECC71);
        powerUpImages[PowerUp.Type.INVINCIBLE.ordinal()] = createColoredImage(32, 32, 0xFF9B59B6);
    }

    private Image createColoredImage(int width, int height, int color) {
        javafx.scene.image.WritableImage image = new javafx.scene.image.WritableImage(width, height);
        javafx.scene.image.PixelWriter pixelWriter = image.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelWriter.setArgb(x, y, color);
            }
        }
        return image;
    }

    private void initializeGame() {
        gameGrid.getChildren().clear();
        walls = new boolean[GRID_SIZE][GRID_SIZE];
        destructibleBlocks = new boolean[GRID_SIZE][GRID_SIZE];
        gameEnded = false;

        // Vider les listes de bombes et de power-ups
        bombs.clear();
        powerUps.clear();

        // Nettoyer les anciens animateurs
        for (Player player : players) {
            if (player != null && player.animator != null) {
                player.animator.dispose();
            }
        }

        players[0] = new Player(1, 1, 0, "Joueur 1");
        players[0].isBot = false;
        players[1] = new Player(GRID_SIZE - 2, 1, 1,"Bot 2");
        players[1].isBot = soloMode;
        players[2] = new Player(1, GRID_SIZE - 2, 2,"Bot 3");
        players[2].isBot = soloMode;
        players[3] = new Player(GRID_SIZE - 2, GRID_SIZE - 2, 3, "Bot 4");
        players[3].isBot = soloMode;

        // Créer le terrain
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                StackPane cell = new StackPane();
                cell.getStyleClass().add("game-cell");

                // Murs du périmètre et murs fixes
                if (x == 0 || x == GRID_SIZE - 1 || y == 0 || y == GRID_SIZE - 1 ||
                        (x % 2 == 0 && y % 2 == 0)) {
                    walls[x][y] = true;
                    ImageView wallView = new ImageView(wallImage);
                    wallView.setFitWidth(CELL_SIZE);
                    wallView.setFitHeight(CELL_SIZE);
                    wallView.setPreserveRatio(true);
                    cell.getChildren().add(wallView);
                }
                // Blocs destructibles aléatoires (éviter les zones de spawn)
                else if (!isSpawnArea(x, y) && Math.random() < 0.5) {
                    destructibleBlocks[x][y] = true;
                    ImageView blockView = new ImageView(destructibleBlockImage);
                    blockView.setFitWidth(CELL_SIZE);
                    blockView.setFitHeight(CELL_SIZE);
                    blockView.setPreserveRatio(true);
                    cell.getChildren().add(blockView);
                }

                gameGrid.add(cell, x, y);
            }
        }

        // Créer les joueurs visuellement avec les sprites animés
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];

            // Créer l'ImageView pour le joueur
            player.visual = new ImageView();
            player.visual.setFitWidth(CELL_SIZE - 4);
            player.visual.setFitHeight(CELL_SIZE - 4);
            player.visual.setPreserveRatio(true);

            // Créer l'animateur pour ce joueur
            player.animator = new PlayerAnimator(player.visual, spriteManager, player.playerIndex);
            player.animator.idle(); // Commencer en idle

            StackPane playerCell = (StackPane) getNodeFromGridPane(player.x, player.y);
            playerCell.getChildren().add(player.visual);
            lastMoveTime.put(player, 0L);
        }
    }

    private boolean isSpawnArea(int x, int y) {
        // Zone de spawn pour chaque joueur (3x3 autour de chaque coin)
        return (x <= 2 && y <= 2) || // Joueur 1
                (x >= GRID_SIZE - 3 && y <= 2) || // Joueur 2
                (x <= 2 && y >= GRID_SIZE - 3) || // Joueur 3
                (x >= GRID_SIZE - 3 && y >= GRID_SIZE - 3); // Joueur 4
    }

    private void setupGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), e -> gameUpdate()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private void gameUpdate() {
        if (!gameEnded) {
            handleInput();
            checkWinCondition();
        }
    }

    private void handleInput() {
        long currentTime = System.currentTimeMillis();

        // Joueur 1 (ZQSD + A)
        if (players[0].alive && currentTime - lastMoveTime.get(players[0]) > MOVEMENT_DELAY) {
            SpriteManager.Direction direction = SpriteManager.Direction.IDLE;
            boolean moved = false;

            if (pressedKeys.contains(KeyCode.Z)) {
                direction = SpriteManager.Direction.UP;
                moved = movePlayer(players[0], 0, -1);
            }
            else if (pressedKeys.contains(KeyCode.S)) {
                direction = SpriteManager.Direction.DOWN;
                moved = movePlayer(players[0], 0, 1);
            }
            else if (pressedKeys.contains(KeyCode.Q)) {
                direction = SpriteManager.Direction.LEFT;
                moved = movePlayer(players[0], -1, 0);
            }
            else if (pressedKeys.contains(KeyCode.D)) {
                direction = SpriteManager.Direction.RIGHT;
                moved = movePlayer(players[0], 1, 0);
            }

            if (moved) {
                lastMoveTime.put(players[0], currentTime);
                players[0].animator.startDirectionAnimation(direction);
            } else if (direction == SpriteManager.Direction.IDLE) {
                players[0].animator.idle();
            }
        }

        if (!soloMode) {
            // Joueur 2 (Flèches + Espace)
            if (players[1].alive && currentTime - lastMoveTime.get(players[1]) > MOVEMENT_DELAY) {
                SpriteManager.Direction direction = SpriteManager.Direction.IDLE;
                boolean moved = false;

                if (pressedKeys.contains(KeyCode.UP)) {
                    direction = SpriteManager.Direction.UP;
                    moved = movePlayer(players[1], 0, -1);
                } else if (pressedKeys.contains(KeyCode.DOWN)) {
                    direction = SpriteManager.Direction.DOWN;
                    moved = movePlayer(players[1], 0, 1);
                } else if (pressedKeys.contains(KeyCode.LEFT)) {
                    direction = SpriteManager.Direction.LEFT;
                    moved = movePlayer(players[1], -1, 0);
                } else if (pressedKeys.contains(KeyCode.RIGHT)) {
                    direction = SpriteManager.Direction.RIGHT;
                    moved = movePlayer(players[1], 1, 0);
                }

                if (moved) {
                    lastMoveTime.put(players[1], currentTime);
                    players[1].animator.startDirectionAnimation(direction);
                } else if (direction == SpriteManager.Direction.IDLE) {
                    players[1].animator.idle();
                }
            }

            // Joueur 3 (YGHJ + U)
            if (players[2].alive && currentTime - lastMoveTime.get(players[2]) > MOVEMENT_DELAY) {
                SpriteManager.Direction direction = SpriteManager.Direction.IDLE;
                boolean moved = false;

                if (pressedKeys.contains(KeyCode.Y)) {
                    direction = SpriteManager.Direction.UP;
                    moved = movePlayer(players[2], 0, -1);
                } else if (pressedKeys.contains(KeyCode.H)) {
                    direction = SpriteManager.Direction.DOWN;
                    moved = movePlayer(players[2], 0, 1);
                } else if (pressedKeys.contains(KeyCode.G)) {
                    direction = SpriteManager.Direction.LEFT;
                    moved = movePlayer(players[2], -1, 0);
                } else if (pressedKeys.contains(KeyCode.J)) {
                    direction = SpriteManager.Direction.RIGHT;
                    moved = movePlayer(players[2], 1, 0);
                }

                if (moved) {
                    lastMoveTime.put(players[2], currentTime);
                    players[2].animator.startDirectionAnimation(direction);
                } else if (direction == SpriteManager.Direction.IDLE) {
                    players[2].animator.idle();
                }
            }

            // Joueur 4 (OKLM + I)
            if (players[3].alive && currentTime - lastMoveTime.get(players[3]) > MOVEMENT_DELAY) {
                SpriteManager.Direction direction = SpriteManager.Direction.IDLE;
                boolean moved = false;

                if (pressedKeys.contains(KeyCode.O)) {
                    direction = SpriteManager.Direction.UP;
                    moved = movePlayer(players[3], 0, -1);
                } else if (pressedKeys.contains(KeyCode.L)) {
                    direction = SpriteManager.Direction.DOWN;
                    moved = movePlayer(players[3], 0, 1);
                } else if (pressedKeys.contains(KeyCode.K)) {
                    direction = SpriteManager.Direction.LEFT;
                    moved = movePlayer(players[3], -1, 0);
                } else if (pressedKeys.contains(KeyCode.M)) {
                    direction = SpriteManager.Direction.RIGHT;
                    moved = movePlayer(players[3], 1, 0);
                }

                if (moved) {
                    lastMoveTime.put(players[3], currentTime);
                    players[3].animator.startDirectionAnimation(direction);
                } else if (direction == SpriteManager.Direction.IDLE) {
                    players[3].animator.idle();
                }
            }
        }

        if (soloMode) {
            for (int i = 1; i < players.length; i++) {
                if (players[i].isBot && players[i].alive) {
                    botAI.updateBot(players[i], Arrays.asList(players), bombs, walls, destructibleBlocks);
                }
            }
        }
    }

    boolean movePlayer(Player player, int dx, int dy) {
        if (!player.alive) return false;

        int newX = player.x + dx;
        int newY = player.y + dy;

        // Vérifier si le joueur peut se déplacer directement à cette position
        if (canMoveTo(newX, newY)) {
            // Retirer le joueur de l'ancienne position
            StackPane oldCell = (StackPane) getNodeFromGridPane(player.x, player.y);
            oldCell.getChildren().remove(player.visual);

            // Déplacer vers la nouvelle position
            player.x = newX;
            player.y = newY;
            StackPane newCell = (StackPane) getNodeFromGridPane(player.x, player.y);
            newCell.getChildren().add(player.visual);

            // Vérifier si un power-up est disponible à cette position
            checkForPowerUp(player, newX, newY);

            return true;
        }
        // Si le joueur ne peut pas se déplacer directement mais a la capacité de pousser les bombes
        else if (player.canKickBombs) {
            // Vérifier si une bombe se trouve à la position cible
            for (Bomb bomb : bombs) {
                if (bomb.x == newX && bomb.y == newY) {
                    // Essayer de pousser la bombe dans la même direction
                    if (tryPushBomb(bomb, dx, dy)) {
                        // Si la bombe a été poussée, permettre au joueur de se déplacer
                        StackPane oldCell = (StackPane) getNodeFromGridPane(player.x, player.y);
                        oldCell.getChildren().remove(player.visual);

                        player.x = newX;
                        player.y = newY;
                        StackPane newCell = (StackPane) getNodeFromGridPane(player.x, player.y);
                        newCell.getChildren().add(player.visual);

                        // Vérifier si un power-up est disponible à cette position
                        checkForPowerUp(player, newX, newY);

                        return true;
                    }
                    break;
                }
            }
        }

        return false;
    }

    private void checkForPowerUp(Player player, int x, int y) {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            if (powerUp.getX() == x && powerUp.getY() == y && powerUp.isActive()) {
                // Collecter le power-up
                StackPane cell = (StackPane) getNodeFromGridPane(x, y);
                cell.getChildren().remove(powerUp.getVisual());
                // Jouer l'effet sonore de collecte de power-up
                audioManager.playEffect("pickup.mp3");

                // Appliquer l'effet
                player.applyPowerUp(powerUp.getType());

                // Afficher un effet visuel temporaire
                showPowerUpEffect(player, powerUp.getType());

                // Retirer de la liste
                powerUp.collect();
                iterator.remove();
                break;
            }
        }
    }

    private void showPowerUpEffect(Player player, PowerUp.Type type) {
        // Créer un texte flottant montrant le type de power-up collecté
        Label effectLabel = new Label();

        switch (type) {
            case BOMB_UP: effectLabel.setText("+1 BOMB"); break;
            case FIRE_UP: effectLabel.setText("+1 BLAST POWER"); break;
            case KICK_BOMB: effectLabel.setText("BOMB KICKER"); break;
            case INVINCIBLE: effectLabel.setText("INVINCIBILITY"); break;
        }

        effectLabel.getStyleClass().add("power-up-text");
        StackPane cell = (StackPane) getNodeFromGridPane(player.x, player.y);
        cell.getChildren().add(effectLabel);

        // Animation de texte flottant puis disparaissant
        TranslateTransition floatUp = new TranslateTransition(Duration.millis(1000), effectLabel);
        floatUp.setByY(-40);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), effectLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(200));

        ParallelTransition parallel = new ParallelTransition(floatUp, fadeOut);
        parallel.setOnFinished(e -> cell.getChildren().remove(effectLabel));
        parallel.play();
    }

    private boolean canMoveTo(int x, int y) {
        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) return false;
        if (walls[x][y] || destructibleBlocks[x][y]) return false;

        // Vérifier s'il y a une bombe
        for (Bomb bomb : bombs) {
            if (bomb.x == x && bomb.y == y) return false;
        }

        return true;
    }

    private boolean tryPushBomb(Bomb bomb, int dx, int dy) {
        // Position initiale
        final int startX = bomb.x;
        final int startY = bomb.y;

        // Récupérer le joueur qui pousse la bombe
        Player pusher = null;
        for (Player p : players) {
            if (p.alive && p.x == startX - dx && p.y == startY - dy) {
                pusher = p;
                break;
            }
        }

        // Calculer toutes les positions intermédiaires
        List<int[]> path = new ArrayList<>();
        int nextX = startX + dx;
        int nextY = startY + dy;

        while (canMoveTo(nextX, nextY)) {
            // Vérifier si un autre joueur (différent du pusher) se trouve à cette position
            boolean playerInPath = false;
            for (Player player : players) {
                if (player != pusher && player.alive && player.x == nextX && player.y == nextY) {
                    playerInPath = true;
                    break;
                }
            }

            // Si un joueur bloque le chemin, arrêter la progression
            if (playerInPath) {
                break;
            }

            path.add(new int[]{nextX, nextY});
            nextX += dx;
            nextY += dy;
        }

        // Si la bombe ne peut pas bouger, retourner false
        if (path.isEmpty()) {
            return false;
        }

        // Retirer la bombe de sa position actuelle
        StackPane oldCell = (StackPane) getNodeFromGridPane(bomb.x, bomb.y);
        oldCell.getChildren().remove(bomb.visual);

        // Premier mouvement immédiat : déplacer directement vers la première position
        int firstTargetX = path.get(0)[0];
        int firstTargetY = path.get(0)[1];

        // Mettre à jour la position logique
        bomb.x = firstTargetX;
        bomb.y = firstTargetY;

        // Ajouter à la nouvelle cellule
        StackPane firstCell = (StackPane) getNodeFromGridPane(firstTargetX, firstTargetY);
        firstCell.getChildren().add(bomb.visual);
        bomb.visual.toFront();

        // S'il n'y a qu'une seule position, on a terminé
        if (path.size() == 1) {
            return true;
        }

        // Créer une séquence de pauses pour les mouvements restants
        SequentialTransition sequentialTransition = new SequentialTransition();

        for (int i = 1; i < path.size(); i++) {
            final int index = i;
            final int targetX = path.get(i)[0];
            final int targetY = path.get(i)[1];

            // Créer une pause de 0.25 seconde
            PauseTransition pause = new PauseTransition(Duration.seconds(0.25));

            // Action après la pause
            pause.setOnFinished(e -> {
                // Vérifier si la bombe a explosé pendant le déplacement
                if (!bombs.contains(bomb)) {
                    sequentialTransition.stop();
                    if (bomb.visual != null) {
                        bomb.visual.setVisible(false);
                    }
                    return;
                }

                // Retirer la bombe de la cellule précédente
                StackPane prevCell = (StackPane) getNodeFromGridPane(path.get(index-1)[0], path.get(index-1)[1]);
                prevCell.getChildren().remove(bomb.visual);

                // Mettre à jour la position logique
                bomb.x = targetX;
                bomb.y = targetY;

                // Ajouter à la nouvelle cellule
                StackPane newCell = (StackPane) getNodeFromGridPane(targetX, targetY);
                newCell.getChildren().add(bomb.visual);

                // S'assurer que la bombe reste au premier plan
                bomb.visual.toFront();
            });

            // Ajouter la pause à la séquence
            sequentialTransition.getChildren().add(pause);
        }

        // Jouer la séquence complète si elle contient des transitions
        if (!sequentialTransition.getChildren().isEmpty()) {
            sequentialTransition.play();
        }

        return true;
    }

    void placeBomb(Player player) {
        if (gameEnded || !player.alive) return;

        // Vérifier s'il n'y a pas déjà une bombe à cette position
        for (Bomb bomb : bombs) {
            if (bomb.x == player.x && bomb.y == player.y) return;
        }

        // Limite de bombes par joueur
        long playerBombs = bombs.stream().filter(b -> b.owner == player).count();
        if (playerBombs >= player.bombLimit) return;

        // Vérifier s'il n'y a pas déjà une bombe à cette position
        for (Bomb bomb : bombs) {
            if (bomb.x == player.x && bomb.y == player.y) return;
        }

        Bomb newBomb = new Bomb(player.x, player.y, player);
        bombs.add(newBomb);

        ImageView bombView = new ImageView(bombImage);
        bombView.setFitWidth(CELL_SIZE - 8);
        bombView.setFitHeight(CELL_SIZE - 8);
        bombView.setPreserveRatio(true);
        newBomb.visual = bombView;

        StackPane cell = (StackPane) getNodeFromGridPane(player.x, player.y);
        cell.getChildren().add(bombView);

        // Animation de pulsation de la bombe
        ScaleTransition pulse = new ScaleTransition(Duration.millis(500), bombView);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(6);
        pulse.play();

        // Exploser après 3 secondes
        Timeline bombTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> explodeBomb(newBomb)));
        bombTimer.play();

        // Jouer l'effet sonore de la bombe
        audioManager.playEffect("place_bomb.mp3");
    }

    private void explodeBomb(Bomb bomb) {
        // Vérifier si la bombe existe toujours et n'est pas déjà en train d'exploser
        if (bomb.exploding || !bombs.contains(bomb)) {
            return;
        }

        // Marquer la bombe comme étant en cours d'explosion
        bomb.exploding = true;

        // Retirer la bombe de la liste
        bombs.remove(bomb);

        // Retirer la bombe visuellement
        StackPane bombCell = (StackPane) getNodeFromGridPane(bomb.x, bomb.y);
        bombCell.getChildren().remove(bomb.visual);

        // Liste des cellules touchées par l'explosion
        List<int[]> explosionCells = new ArrayList<>();
        explosionCells.add(new int[]{bomb.x, bomb.y});

        // Utiliser le rayon d'explosion du propriétaire de la bombe
        int radius = bomb.owner.explosionRadius;

        // Explosion dans les 4 directions
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : directions) {
            for (int i = 1; i <= radius; i++) {
                int x = bomb.x + dir[0] * i;
                int y = bomb.y + dir[1] * i;

                if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE || walls[x][y]) break;

                explosionCells.add(new int[]{x, y});

                // Vérifier si une autre bombe est touchée
                for (int j = 0; j < bombs.size(); j++) {
                    Bomb otherBomb = bombs.get(j);
                    if (otherBomb.x == x && otherBomb.y == y && !otherBomb.exploding) {
                        // Explosion en chaîne retardée de 200ms
                        final Bomb bombToExplode = otherBomb;
                        Timeline chainReaction = new Timeline(new KeyFrame(Duration.millis(200),
                                e -> explodeBomb(bombToExplode)));
                        chainReaction.play();
                        break;
                    }
                }

                // Détruire les blocs destructibles
                if (destructibleBlocks[x][y]) {
                    destroyBlock(x, y);
                    break;
                }
            }

            // Jouer le son d'explosion
            audioManager.playEffect("explosion.mp3");
        }

        // Afficher les explosions
        showExplosion(explosionCells);

        // Vérifier si des joueurs sont touchés
        for (int[] cell : explosionCells) {
            for (Player player : players) {
                if (player.x == cell[0] && player.y == cell[1]) {
                    killPlayer(player);
                }
            }
        }
    }

    private void destroyBlock(int x, int y) {
        if (destructibleBlocks[x][y]) {
            destructibleBlocks[x][y] = false;
            StackPane cell = (StackPane) getNodeFromGridPane(x, y);

            // Trouver et retirer le bloc destructible
            cell.getChildren().removeIf(node -> node instanceof ImageView &&
                    ((ImageView) node).getImage() == destructibleBlockImage);

            // 40% de chance de générer un power-up
            if (Math.random() < 0.4) {
                createPowerUp(x, y, cell);
            }
        }
    }

    private void createPowerUp(int x, int y, StackPane cell) {
        // Choisir un type de power-up aléatoire
        PowerUp.Type type = PowerUp.Type.values()[(int)(Math.random() * PowerUp.Type.values().length)];
        PowerUp powerUp = new PowerUp(x, y, type);

        // Créer la représentation visuelle
        ImageView powerUpView = new ImageView(powerUpImages[type.ordinal()]);
        powerUpView.setFitWidth(30);
        powerUpView.setFitHeight(30);
        powerUpView.setPreserveRatio(true);

        // Ajouter à la cellule et à la liste
        powerUp.setVisual(powerUpView);
        cell.getChildren().add(powerUpView);
        powerUps.add(powerUp);
    }

    private void showExplosion(List<int[]> cells) {
        for (int[] cell : cells) {
            ImageView explosionView = new ImageView(explosionImage);
            explosionView.setFitWidth(CELL_SIZE);
            explosionView.setFitHeight(CELL_SIZE);
            explosionView.setPreserveRatio(true);

            StackPane cellPane = (StackPane) getNodeFromGridPane(cell[0], cell[1]);
            cellPane.getChildren().add(explosionView);

            // Animation d'explosion
            FadeTransition fade = new FadeTransition(Duration.millis(500), explosionView);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> cellPane.getChildren().remove(explosionView));
            fade.play();
        }
    }

    private void killPlayer(Player player) {
        if (!player.alive) return;

        // Vérifier si le joueur est invincible
        if (player.isInvincible) {
            // Ne pas tuer le joueur invincible

            // Afficher un effet visuel pour indiquer que l'invincibilité a protégé le joueur
            Label shieldLabel = new Label("PROTECTED!");
            shieldLabel.getStyleClass().add("shield-text");
            StackPane cell = (StackPane) getNodeFromGridPane(player.x, player.y);
            cell.getChildren().add(shieldLabel);

            // Animation de l'effet de protection
            FadeTransition fadeOut = new FadeTransition(Duration.millis(800), shieldLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setDelay(Duration.millis(300));
            fadeOut.setOnFinished(e -> cell.getChildren().remove(shieldLabel));
            fadeOut.play();

            return;
        }

        player.alive = false;

        // Jouer l'effet sonore de mort
        audioManager.playEffect("death.mp3");

        // Jouer l'animation de mort
        player.animator.playDeathAnimation();

        updateUI();
    }

    private void checkWinCondition() {
        if (gameEnded) return;

        long aliveCount = Arrays.stream(players).filter(p -> p.alive).count();

        if (aliveCount <= 1) {
            gameEnded = true;
            gameLoop.stop();

            Player winner = Arrays.stream(players).filter(p -> p.alive).findFirst().orElse(null);

            // Mettre à jour les statistiques des joueurs
            for (Player player : players) {
                if (player != null && !player.isBot) {
                    if (player == winner) {
                        // Victoire pour le gagnant
                        statsManager.recordVictory(player.name);
                    } else {
                        // Défaite pour les autres
                        statsManager.recordDefeat(player.name);
                    }
                }
            }

            if (winner != null) {
                // Récupérer les stats du gagnant
                PlayerStats winnerStats = statsManager.getPlayerStats(winner.name);

                // Afficher un message avec les statistiques
                winnerLabel.setText(winner.name + " GAGNE! (V: " + winnerStats.getVictories()
                        + " - D: " + winnerStats.getDefeats() + ")");
                winnerLabel.getStyleClass().add("winner-text");

                // Jouer le son de victoire
                audioManager.stopMusic();
                audioManager.playEffect("win.mp3");

                // Jouer la musique de victoire après un délai
                Timeline victoryMusic = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                    audioManager.playMusic("victory_music.mp3");
                }));
                victoryMusic.play();

                // Animation de célébration pour le gagnant
                winner.animator.celebrate();

                // Animation de victoire
                ScaleTransition victory = new ScaleTransition(Duration.millis(1000), winnerLabel);
                victory.setFromX(1.0);
                victory.setFromY(1.0);
                victory.setToX(1.5);
                victory.setToY(1.5);
                victory.setAutoReverse(true);
                victory.setCycleCount(Timeline.INDEFINITE);
                victory.play();
            } else {
                winnerLabel.setText("MATCH NUL!");
            }
        }
    }

    private void updateUI() {
        player1Label.setText(players[0].name + (players[0].alive ? " ✓" : " ✗"));
        player1Label.getStyleClass().removeAll("dead-player");
        if (!players[0].alive) player1Label.getStyleClass().add("dead-player");

        player2Label.setText(players[1].name + (players[1].alive ? " ✓" : " ✗"));
        player2Label.getStyleClass().removeAll("dead-player");
        if (!players[1].alive) player2Label.getStyleClass().add("dead-player");

        player3Label.setText(players[2].name + (players[2].alive ? " ✓" : " ✗"));
        player3Label.getStyleClass().removeAll("dead-player");
        if (!players[2].alive) player3Label.getStyleClass().add("dead-player");

        player4Label.setText(players[3].name + (players[3].alive ? " ✓" : " ✗"));
        player4Label.getStyleClass().removeAll("dead-player");
        if (!players[3].alive) player4Label.getStyleClass().add("dead-player");
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        pressedKeys.add(event.getCode());

        // Bombes pour chaque joueur
        if (event.getCode() == KeyCode.A && players[0].alive) placeBomb(players[0]);
        if (event.getCode() == KeyCode.SPACE && players[1].alive) placeBomb(players[1]);
        if (event.getCode() == KeyCode.U && players[2].alive) placeBomb(players[2]);
        if (event.getCode() == KeyCode.I && players[3].alive) placeBomb(players[3]);

        // Restart game
        if (event.getCode() == KeyCode.R && gameEnded) {
            restartGame();
        }
    }

    @FXML
    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    private void restartGame() {
        winnerLabel.setText("");
        winnerLabel.getStyleClass().removeAll("winner-text");

        // Sauvegarder les noms des joueurs avant la réinitialisation
        String[] savedNames = new String[4];
        for (int i = 0; i < 4; i++) {
            if (players[i] != null) {
                savedNames[i] = players[i].name;
            }
        }

        // Arrêter toutes les animations en cours
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Nettoyer les anciennes ressources
        bombs.clear();
        powerUps.clear();

        initializeGame();

        // Restaurer les noms des joueurs après l'initialisation
        for (int i = 0; i < 4; i++) {
            if (savedNames[i] != null && !savedNames[i].isEmpty()) {
                players[i].name = savedNames[i];
            }
        }

        setupGameLoop();
        updateUI();
    }

    private javafx.scene.Node getNodeFromGridPane(int col, int row) {
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    public class Player {
        public PlayerAnimator animator;
        int x, y;
        boolean alive = true;
        ImageView visual;
        int playerIndex;
        String name;
        boolean isBot = false;

        // Attributs pour les power-ups
        int bombLimit = 2;
        int explosionRadius = 2;
        int speed = MOVEMENT_DELAY;
        boolean canKickBombs = false;
        boolean isInvincible = false;

        Player(int x, int y, int playerIndex, String name) {
            this.x = x;
            this.y = y;
            this.playerIndex = playerIndex;
            this.name = name;
        }

        void applyPowerUp(PowerUp.Type type) {
            switch (type) {
                case BOMB_UP:
                    bombLimit++;
                    break;
                case FIRE_UP:
                    explosionRadius++;
                    break;
                case KICK_BOMB:
                    canKickBombs = true;
                    break;
                case INVINCIBLE:
                    isInvincible = true;
                    // Animation de clignotement
                    FadeTransition blink = new FadeTransition(Duration.millis(200), visual);
                    blink.setFromValue(0.6);
                    blink.setToValue(1.0);
                    blink.setCycleCount(25); // ~5 secondes
                    blink.setAutoReverse(true); // Assure un effet de clignotement
                    blink.setOnFinished(e -> {
                        isInvincible = false;
                        visual.setOpacity(1.0); // Rétablir l'opacité normale à la fin
                    });
                    blink.play();
                    break;
            }
        }

        public void useCustomAnimation(String name) {
            switch (name) {
                case "idle":
                    // Animation d'attente
                    animator.showCell(new SpriteManager.SpriteCell(0, 1));
                    break;

                case "victory":
                    // Animation de victoire/célébration
                    SpriteManager.SpriteCell[] victoryCells = {
                            new SpriteManager.SpriteCell(3, 0),
                            new SpriteManager.SpriteCell(3, 1)
                    };
                    animator.showCustomCells(victoryCells, 300, true);
                    break;

                case "moving_down":
                    // Animation de déplacement vers le bas
                    SpriteManager.SpriteCell[] downCells = {
                            new SpriteManager.SpriteCell(0, 1),
                            new SpriteManager.SpriteCell(0, 2)
                    };
                    animator.showCustomCells(downCells, 150, true);
                    break;

                case "moving_up":
                    // Animation de déplacement vers le haut
                    SpriteManager.SpriteCell[] upCells = {
                            new SpriteManager.SpriteCell(1, 1),
                            new SpriteManager.SpriteCell(1, 2)
                    };
                    animator.showCustomCells(upCells, 150, true);
                    break;

                case "moving_left":
                    // Animation de déplacement vers la gauche
                    SpriteManager.SpriteCell[] leftCells = {
                            new SpriteManager.SpriteCell(2, 1),
                            new SpriteManager.SpriteCell(2, 2)
                    };
                    animator.showCustomCells(leftCells, 150, true);
                    break;

                case "moving_right":
                    // Animation de déplacement vers la droite
                    SpriteManager.SpriteCell[] rightCells = {
                            new SpriteManager.SpriteCell(5, 1),
                            new SpriteManager.SpriteCell(5, 2)
                    };
                    animator.showCustomCells(rightCells, 150, true);
                    break;

                case "death":
                    // Animation de mort
                    SpriteManager.SpriteCell[] deathCells = {
                            new SpriteManager.SpriteCell(4, 0),
                            new SpriteManager.SpriteCell(4, 1),
                    };
                    animator.showCustomCells(deathCells, 200, false);
                    break;
            }
        }
    }

    public class Bomb {
        int x, y;
        ImageView visual;
        Player owner;
        boolean exploding = false;  // Pour éviter les explosions multiples

        Bomb(int x, int y, Player owner) {
            this.x = x;
            this.y = y;
            this.owner = owner;
        }
    }

}