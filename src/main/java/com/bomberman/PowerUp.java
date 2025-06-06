package com.bomberman;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class PowerUp {
    private static final int DURATION_SECONDS = 15; // Durée des effets temporaires

    public enum Type {
        BOMB_UP,       // +1 bombe maximum
        FIRE_UP,       // +1 portée d'explosion
        KICK_BOMB,     // Capacité à pousser les bombes
        INVINCIBLE     // Invincibilité temporaire (5 secondes)
    }

    private int x, y;
    private Type type;
    private boolean active = true;
    private ImageView visual;

    public PowerUp(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Type getType() { return type; }
    public boolean isActive() { return active; }
    public void collect() { this.active = false; }
    public ImageView getVisual() { return visual; }

    public void setVisual(ImageView visual) {
        this.visual = visual;

        // Animation de pulsation
        ScaleTransition pulse = new ScaleTransition(Duration.millis(1000), visual);
        pulse.setFromX(0.9);
        pulse.setFromY(0.9);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        pulse.play();
    }
}