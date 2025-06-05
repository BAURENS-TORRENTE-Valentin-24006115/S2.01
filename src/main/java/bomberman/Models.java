package bomberman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// Énumération des types de cellules
enum CellType {
    EMPTY, WALL, DESTRUCTIBLE_BLOCK, PLAYER, BOMB, EXPLOSION
}

// Classe Player
class Player {
    private int row, col;
    private int bombRange = 2;
    private int maxBombs = 1;

    public Player(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getBombRange() { return bombRange; }
    public int getMaxBombs() { return maxBombs; }
}

// Classe Bomb
class Bomb {
    private int row, col;
    private int range;
    private long timeCreated;
    private boolean isBlinking;

    public Bomb(int row, int col) {
        this.row = row;
        this.col = col;
        this.range = 2;
        this.timeCreated = System.currentTimeMillis();
        this.isBlinking = false;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getRange() { return range; }

    public void update() {
        long elapsed = System.currentTimeMillis() - timeCreated;
        // La bombe commence à clignoter après 2 secondes
        isBlinking = elapsed > 2000;
    }

    public boolean isBlinking() { return isBlinking; }
}

// Classe GameCell
class GameCell {
    private int row, col;
    private CellType type;
    private Rectangle view;
    private Bomb bomb;
    private boolean showingExplosion;

    public GameCell(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = CellType.EMPTY;
        this.view = new Rectangle(40, 40);
        this.showingExplosion = false;
        updateView();
    }

    public CellType getType() { return type; }

    public void setType(CellType type) {
        this.type = type;
        updateView();
    }

    public Rectangle getView() { return view; }

    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
        updateView();
    }

    public void showExplosion() {
        showingExplosion = true;
        updateView();
    }

    public void hideExplosion() {
        showingExplosion = false;
        updateView();
    }

    private void updateView() {
        if (showingExplosion) {
            view.setFill(Color.ORANGE);
            view.setStroke(Color.RED);
            view.setStrokeWidth(2);
            return;
        }

        switch (type) {
            case EMPTY:
                view.setFill(Color.LIGHTGREEN);
                view.setStroke(Color.DARKGREEN);
                view.setStrokeWidth(1);
                break;
            case WALL:
                view.setFill(Color.DARKGRAY);
                view.setStroke(Color.BLACK);
                view.setStrokeWidth(2);
                break;
            case DESTRUCTIBLE_BLOCK:
                view.setFill(Color.BROWN);
                view.setStroke(Color.SADDLEBROWN);
                view.setStrokeWidth(2);
                break;
            case PLAYER:
                view.setFill(Color.BLUE);
                view.setStroke(Color.DARKBLUE);
                view.setStrokeWidth(3);
                break;
        }

        // Afficher la bombe si présente
        if (bomb != null) {
            if (bomb.isBlinking()) {
                view.setFill(Color.RED);
            } else {
                view.setFill(Color.BLACK);
            }
            view.setStroke(Color.DARKRED);
            view.setStrokeWidth(2);
        }
    }
}