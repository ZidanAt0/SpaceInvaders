package sgame;

import javafx.scene.image.Image;

public class Enemy extends Character {
    private Image image;
    private int shootInterval;
    
    public Enemy(double x, double y, Image image) {
        super(x, y, 40, 40, 30, 30, 0.5);
        this.image = image;
        this.shootInterval = 240;
    }

    public Image getImage() { return image; }
    public int getShootInterval() { return shootInterval; }

    @Override
    public void update() {
        setY(getY() + getSpeed());
    }

    @Override
    public void handleCollision() {
        setAlive(false);
    }
}
