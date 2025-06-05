package com.bomberman;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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

    private Player[] players = new Player[4];
    private boolean[][] walls;
    private boolean[][] destructibleBlocks;
    private List<Bomb> bombs = new ArrayList<>();
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private Map<Player, Long> lastMoveTime = new HashMap<>();

    private Timeline gameLoop;
    private boolean gameEnded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeGame();
        setupGameLoop();
        updateUI();
    }

    private void initializeGame() {
        gameGrid.getChildren().clear();
        walls = new boolean[GRID_SIZE][GRID_SIZE];
        destructibleBlocks = new boolean[GRID_SIZE][GRID_SIZE];
        gameEnded = false;

        // Initialiser les joueurs
        players[0] = new Player(1, 1, "player1", "Joueur 1 (ZQSD + A)");
        players[1] = new Player(GRID_SIZE - 2, 1, "player2", "Joueur 2 (↑↓←→ + Espace)");
        players[2] = new Player(1, GRID_SIZE - 2, "player3", "Joueur 3 (YGHJ + U)");
        players[3] = new Player(GRID_SIZE - 2, GRID_SIZE - 2, "player4", "Joueur 4 (OKLM + I)");

        // Créer le terrain
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                StackPane cell = new StackPane();
                cell.getStyleClass().add("game-cell");

                // Murs du périmètre et murs fixes
                if (x == 0 || x == GRID_SIZE - 1 || y == 0 || y == GRID_SIZE - 1 ||
                        (x % 2 == 0 && y % 2 == 0)) {
                    walls[x][y] = true;
                    Rectangle wall = new Rectangle(CELL_SIZE, CELL_SIZE);
                    wall.getStyleClass().add("wall");
                    cell.getChildren().add(wall);
                }
                // Blocs destructibles aléatoires (éviter les zones de spawn)
                else if (!isSpawnArea(x, y) && Math.random() < 0.5) {
                    destructibleBlocks[x][y] = true;
                    Rectangle block = new Rectangle(CELL_SIZE, CELL_SIZE);
                    block.getStyleClass().add("destructible-block");
                    cell.getChildren().add(block);
                }

                gameGrid.add(cell, x, y);
            }
        }

        // Créer les joueurs visuellement
        for (Player player : players) {
            player.visual = new Rectangle(CELL_SIZE - 4, CELL_SIZE - 4);
            player.visual.getStyleClass().add(player.styleClass);
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
            updateBombs();
            checkWinCondition();
        }
    }

    private void handleInput() {
        long currentTime = System.currentTimeMillis();

        // Joueur 1 (ZQSD + A)
        if (players[0].alive && currentTime - lastMoveTime.get(players[0]) > MOVEMENT_DELAY) {
            if (pressedKeys.contains(KeyCode.Z)) { movePlayer(players[0], 0, -1); lastMoveTime.put(players[0], currentTime); }
            else if (pressedKeys.contains(KeyCode.S)) { movePlayer(players[0], 0, 1); lastMoveTime.put(players[0], currentTime); }
            else if (pressedKeys.contains(KeyCode.Q)) { movePlayer(players[0], -1, 0); lastMoveTime.put(players[0], currentTime); }
            else if (pressedKeys.contains(KeyCode.D)) { movePlayer(players[0], 1, 0); lastMoveTime.put(players[0], currentTime); }
        }

        // Joueur 2 (Flèches + Espace)
        if (players[1].alive && currentTime - lastMoveTime.get(players[1]) > MOVEMENT_DELAY) {
            if (pressedKeys.contains(KeyCode.UP)) { movePlayer(players[1], 0, -1); lastMoveTime.put(players[1], currentTime); }
            else if (pressedKeys.contains(KeyCode.DOWN)) { movePlayer(players[1], 0, 1); lastMoveTime.put(players[1], currentTime); }
            else if (pressedKeys.contains(KeyCode.LEFT)) { movePlayer(players[1], -1, 0); lastMoveTime.put(players[1], currentTime); }
            else if (pressedKeys.contains(KeyCode.RIGHT)) { movePlayer(players[1], 1, 0); lastMoveTime.put(players[1], currentTime); }
        }

        // Joueur 3 (YGHJ + U)
        if (players[2].alive && currentTime - lastMoveTime.get(players[2]) > MOVEMENT_DELAY) {
            if (pressedKeys.contains(KeyCode.Y)) { movePlayer(players[2], 0, -1); lastMoveTime.put(players[2], currentTime); }
            else if (pressedKeys.contains(KeyCode.H)) { movePlayer(players[2], 0, 1); lastMoveTime.put(players[2], currentTime); }
            else if (pressedKeys.contains(KeyCode.G)) { movePlayer(players[2], -1, 0); lastMoveTime.put(players[2], currentTime); }
            else if (pressedKeys.contains(KeyCode.J)) { movePlayer(players[2], 1, 0); lastMoveTime.put(players[2], currentTime); }
        }

        // Joueur 4 (OKLM + I)
        if (players[3].alive && currentTime - lastMoveTime.get(players[3]) > MOVEMENT_DELAY) {
            if (pressedKeys.contains(KeyCode.O)) { movePlayer(players[3], 0, -1); lastMoveTime.put(players[3], currentTime); }
            else if (pressedKeys.contains(KeyCode.L)) { movePlayer(players[3], 0, 1); lastMoveTime.put(players[3], currentTime); }
            else if (pressedKeys.contains(KeyCode.K)) { movePlayer(players[3], -1, 0); lastMoveTime.put(players[3], currentTime); }
            else if (pressedKeys.contains(KeyCode.M)) { movePlayer(players[3], 1, 0); lastMoveTime.put(players[3], currentTime); }
        }
    }

    private void movePlayer(Player player, int dx, int dy) {
        if (!player.alive) return;

        int newX = player.x + dx;
        int newY = player.y + dy;

        if (canMoveTo(newX, newY)) {
            // Retirer le joueur de l'ancienne position
            StackPane oldCell = (StackPane) getNodeFromGridPane(player.x, player.y);
            oldCell.getChildren().remove(player.visual);

            // Déplacer vers la nouvelle position
            player.x = newX;
            player.y = newY;
            StackPane newCell = (StackPane) getNodeFromGridPane(player.x, player.y);
            newCell.getChildren().add(player.visual);
        }
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

    private void placeBomb(Player player) {
        if (!player.alive) return;

        // Vérifier s'il n'y a pas déjà une bombe à cette position
        for (Bomb bomb : bombs) {
            if (bomb.x == player.x && bomb.y == player.y) return;
        }

        // Limite de bombes par joueur
        long playerBombs = bombs.stream().filter(b -> b.owner == player).count();
        if (playerBombs >= 2) return;

        Bomb newBomb = new Bomb(player.x, player.y, player);
        bombs.add(newBomb);

        Rectangle bombRect = new Rectangle(CELL_SIZE - 8, CELL_SIZE - 8);
        bombRect.getStyleClass().add("bomb");
        newBomb.visual = bombRect;

        StackPane cell = (StackPane) getNodeFromGridPane(player.x, player.y);
        cell.getChildren().add(bombRect);

        // Animation de pulsation de la bombe
        ScaleTransition pulse = new ScaleTransition(Duration.millis(500), bombRect);
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
    }

    private void explodeBomb(Bomb bomb) {
        if (!bombs.contains(bomb)) return;

        bombs.remove(bomb);

        // Retirer la bombe visuelle
        StackPane bombCell = (StackPane) getNodeFromGridPane(bomb.x, bomb.y);
        bombCell.getChildren().remove(bomb.visual);

        // Créer l'explosion
        List<int[]> explosionCells = new ArrayList<>();
        explosionCells.add(new int[]{bomb.x, bomb.y});

        // Explosion dans les 4 directions
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : directions) {
            for (int i = 1; i <= 2; i++) {
                int x = bomb.x + dir[0] * i;
                int y = bomb.y + dir[1] * i;

                if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE || walls[x][y]) break;

                explosionCells.add(new int[]{x, y});

                if (destructibleBlocks[x][y]) {
                    destroyBlock(x, y);
                    break;
                }
            }
        }

        // Afficher l'explosion
        showExplosion(explosionCells);

        // Vérifier si des joueurs sont touchés
        for (int[] cell : explosionCells) {
            for (Player player : players) {
                if (player.alive && cell[0] == player.x && cell[1] == player.y) {
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
            cell.getChildren().removeIf(node ->
                    node instanceof Rectangle && node.getStyleClass().contains("destructible-block"));
        }
    }

    private void showExplosion(List<int[]> cells) {
        for (int[] cell : cells) {
            Rectangle explosion = new Rectangle(CELL_SIZE, CELL_SIZE);
            explosion.getStyleClass().add("explosion");

            StackPane cellPane = (StackPane) getNodeFromGridPane(cell[0], cell[1]);
            cellPane.getChildren().add(explosion);

            // Animation d'explosion
            FadeTransition fade = new FadeTransition(Duration.millis(500), explosion);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> cellPane.getChildren().remove(explosion));
            fade.play();
        }
    }

    private void killPlayer(Player player) {
        if (!player.alive) return;

        player.alive = false;

        // Faire disparaître le joueur
        FadeTransition death = new FadeTransition(Duration.millis(500), player.visual);
        death.setFromValue(1.0);
        death.setToValue(0.0);
        death.setOnFinished(e -> {
            StackPane cell = (StackPane) getNodeFromGridPane(player.x, player.y);
            cell.getChildren().remove(player.visual);
        });
        death.play();

        updateUI();
    }

    private void checkWinCondition() {
        if (gameEnded) return;

        long aliveCount = Arrays.stream(players).filter(p -> p.alive).count();

        if (aliveCount <= 1) {
            gameEnded = true;
            gameLoop.stop();

            Player winner = Arrays.stream(players).filter(p -> p.alive).findFirst().orElse(null);
            if (winner != null) {
                winnerLabel.setText(winner.name + " GAGNE!");
                winnerLabel.getStyleClass().add("winner-text");

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

    private void updateBombs() {
        // Les bombes sont gérées par leurs propres timelines
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
        initializeGame();
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

    private static class Player {
        int x, y;
        boolean alive = true;
        Rectangle visual;
        String styleClass;
        String name;

        Player(int x, int y, String styleClass, String name) {
            this.x = x;
            this.y = y;
            this.styleClass = styleClass;
            this.name = name;
        }
    }

    private static class Bomb {
        int x, y;
        Rectangle visual;
        Player owner;

        Bomb(int x, int y, Player owner) {
            this.x = x;
            this.y = y;
            this.owner = owner;
        }
    }
}