package sgame;

import javafx.scene.image.Image;

public abstract class Character extends GameObject {
    private double hitboxWidth;
    private double hitboxHeight;
    private double speed;
    private Image[] sprites;
    private int currentFrame;
    private boolean isAlive;

    public Character(double x, double y, double width, double height, 
                    double hitboxWidth, double hitboxHeight, double speed) {
        super(x, y, width, height);
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.speed = speed;
        this.isAlive = true;
        this.currentFrame = 0;
    }
}