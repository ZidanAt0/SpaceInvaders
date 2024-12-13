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

    public double getHitboxWidth() { return hitboxWidth; }
    public double getHitboxHeight() { return hitboxHeight; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }
    public Image[] getSprites() { return sprites; }
    public void setSprites(Image[] sprites) { this.sprites = sprites; }
    public int getCurrentFrame() { return currentFrame; }
    public void setCurrentFrame(int frame) { this.currentFrame = frame; }

    public abstract void handleCollision();
}