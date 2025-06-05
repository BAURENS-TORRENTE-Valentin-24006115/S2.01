package bomberman;

import javafx.scene.image.Image;

public class Obstacle extends Image {

    private boolean destructible;

    public Obstacle(String obstacleImagePath, boolean isDestructible) {
        super(obstacleImagePath);
        this.destructible = isDestructible;
    }
}