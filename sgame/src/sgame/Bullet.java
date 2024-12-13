package sgame;

import javafx.scene.paint.Color;
public class Bullet extends GameObject {
    private Color color;
    private double speed;
    private boolean movingDown;

    public Bullet(double x, double y, Color color, double speed, boolean movingDown) {
        super(x, y, 5, 5);
        this.color = color;
        this.speed = speed;
        this.movingDown = movingDown;
    }

    public Color getColor() { 
        return color;
    }
    public double getSpeed() {
        return speed;
    }
    public boolean isMovingDown() {
        return movingDown;
    }

    @Override
    public void update() {
        if (movingDown) {
            setY(getY() + speed);
        } else {
            setY(getY() - speed);
        }
    }

    public boolean isOffScreen(double canvasHeight) {
        if (movingDown) {
                return getY() > canvasHeight;
            }else {
                return getY() < -getHeight();
            }
    }
}
