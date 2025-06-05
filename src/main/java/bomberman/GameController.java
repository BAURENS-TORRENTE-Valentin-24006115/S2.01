package bomberman;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class GameController {

    private GridPane gameGrid;

    private static final int GRID_SIZE = 15;
    private static final int CELL_SIZE = 40;

    private GameCell[][] grid;
    private Player player;
    private List<Bomb> bombs;
    private Set<String> pressedKeys;
    private AnimationTimer gameLoop;

    // Setter pour la grille de jeu
    public void setGameGrid(GridPane gameGrid) {
        this.gameGrid = gameGrid;
    }

    public void initialize() {
        initializeGame();
        setupKeyHandlers();
        startGameLoop();
    }

    public void initialize(URL location, ResourceBundle resources) {
        initializeGame();
        setupKeyHandlers();
        startGameLoop();
    }

    private void initializeGame() {
        grid = new GameCell[GRID_SIZE][GRID_SIZE];
        bombs = new ArrayList<>();
        pressedKeys = new HashSet<>();

        // Initialiser la grille
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                grid[row][col] = new GameCell(row, col);
                gameGrid.add(grid[row][col].getView(), col, row);
            }
        }

        // Générer les obstacles fixes (murs indestructibles)
        generateWalls();

        // Générer les obstacles destructibles (comme Super Bomberman)
        generateDestructibleBlocks();

        // Initialiser le joueur
        player = new Player(1, 1);
        updatePlayerPosition();
    }

    private void generateWalls() {
        // Murs du périmètre
        for (int i = 0; i < GRID_SIZE; i++) {
            grid[0][i].setType(CellType.WALL);
            grid[GRID_SIZE-1][i].setType(CellType.WALL);
            grid[i][0].setType(CellType.WALL);
            grid[i][GRID_SIZE-1].setType(CellType.WALL);
        }

        // Murs intérieurs (pattern classique Bomberman)
        for (int row = 2; row < GRID_SIZE-1; row += 2) {
            for (int col = 2; col < GRID_SIZE-1; col += 2) {
                grid[row][col].setType(CellType.WALL);
            }
        }
    }

    private void generateDestructibleBlocks() {
        Random random = new Random();

        // Générer des blocs destructibles (environ 60% des cases libres)
        for (int row = 1; row < GRID_SIZE-1; row++) {
            for (int col = 1; col < GRID_SIZE-1; col++) {
                if (grid[row][col].getType() == CellType.EMPTY) {
                    // Ne pas placer de blocs près du spawn du joueur
                    if ((row == 1 && col == 1) || (row == 1 && col == 2) ||
                            (row == 2 && col == 1)) {
                        continue;
                    }

                    if (random.nextDouble() < 0.6) {
                        grid[row][col].setType(CellType.DESTRUCTIBLE_BLOCK);
                    }
                }
            }
        }
    }

    private void setupKeyHandlers() {
        gameGrid.setOnKeyPressed(this::handleKeyPressed);
        gameGrid.setOnKeyReleased(this::handleKeyReleased);
        gameGrid.setFocusTraversable(true);
    }

    private void handleKeyPressed(KeyEvent event) {
        pressedKeys.add(event.getCode().toString());

        switch (event.getCode()) {
            case SPACE:
                placeBomb();
                break;
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode().toString());
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_000_000) { // ~60 FPS
                    updateGame();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void updateGame() {
        handleMovement();
        updateBombs();
    }

    private void handleMovement() {
        int newRow = player.getRow();
        int newCol = player.getCol();

        if (pressedKeys.contains("UP") || pressedKeys.contains("Z")) {
            newRow--;
        } else if (pressedKeys.contains("DOWN") || pressedKeys.contains("S")) {
            newRow++;
        } else if (pressedKeys.contains("LEFT") || pressedKeys.contains("Q")) {
            newCol--;
        } else if (pressedKeys.contains("RIGHT") || pressedKeys.contains("D")) {
            newCol++;
        }

        if (canMoveTo(newRow, newCol)) {
            player.setPosition(newRow, newCol);
            updatePlayerPosition();
        }
    }

    private boolean canMoveTo(int row, int col) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return false;
        }

        CellType cellType = grid[row][col].getType();
        return cellType == CellType.EMPTY || cellType == CellType.PLAYER;
    }

    private void placeBomb() {
        int row = player.getRow();
        int col = player.getCol();

        // Vérifier si une bombe existe déjà à cette position
        for (Bomb bomb : bombs) {
            if (bomb.getRow() == row && bomb.getCol() == col) {
                return;
            }
        }

        Bomb bomb = new Bomb(row, col);
        bombs.add(bomb);
        grid[row][col].setBomb(bomb);

        // Timer pour l'explosion
        Timeline explosionTimer = new Timeline(new KeyFrame(
                Duration.seconds(3),
                e -> explodeBomb(bomb)
        ));
        explosionTimer.play();
    }

    private void explodeBomb(Bomb bomb) {
        int row = bomb.getRow();
        int col = bomb.getCol();

        // Retirer la bombe
        bombs.remove(bomb);
        grid[row][col].setBomb(null);

        // Créer l'explosion
        createExplosion(row, col);

        // Explosion dans les 4 directions
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int dir = 0; dir < 4; dir++) {
            for (int range = 1; range <= bomb.getRange(); range++) {
                int newRow = row + dx[dir] * range;
                int newCol = col + dy[dir] * range;

                if (newRow < 0 || newRow >= GRID_SIZE ||
                        newCol < 0 || newCol >= GRID_SIZE) {
                    break;
                }

                CellType cellType = grid[newRow][newCol].getType();

                if (cellType == CellType.WALL) {
                    break;
                } else if (cellType == CellType.DESTRUCTIBLE_BLOCK) {
                    grid[newRow][newCol].setType(CellType.EMPTY);
                    createExplosion(newRow, newCol);
                    break;
                } else {
                    createExplosion(newRow, newCol);

                    // Vérifier si le joueur est touché
                    if (newRow == player.getRow() && newCol == player.getCol()) {
                        handlePlayerHit();
                    }
                }
            }
        }
    }

    private void createExplosion(int row, int col) {
        grid[row][col].showExplosion();

        // L'explosion disparaît après 500ms
        Timeline explosionEnd = new Timeline(new KeyFrame(
                Duration.millis(500),
                e -> grid[row][col].hideExplosion()
        ));
        explosionEnd.play();
    }

    private void handlePlayerHit() {
        System.out.println("Joueur touché!");
        // Ici vous pouvez ajouter la logique de game over ou de perte de vie
    }

    private void updatePlayerPosition() {
        // Retirer le joueur de toutes les cases
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (grid[row][col].getType() == CellType.PLAYER) {
                    grid[row][col].setType(CellType.EMPTY);
                }
            }
        }

        // Placer le joueur à sa nouvelle position
        grid[player.getRow()][player.getCol()].setType(CellType.PLAYER);
    }

    private void updateBombs() {
        for (Bomb bomb : bombs) {
            bomb.update();
        }
    }
}