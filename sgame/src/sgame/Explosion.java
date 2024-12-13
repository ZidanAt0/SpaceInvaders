package sgame;

public class Explosion extends GameObject {
    private int step;
    private int animationCounter;
    private final int ANIMATION_SPEED = 3;
    private final int MAX_STEPS = 15;

    public Explosion(double x, double y, double size) {
        super(x, y, size, size);
        this.step = 0;
        this.animationCounter = 0;
    }

    public int getStep() {
        return step;
    }
    public int getAnimationCounter() {
        return animationCounter;
    }
}