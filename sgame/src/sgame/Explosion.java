package sgame;

public class Explosion extends GameObject {
    private int step;
    private int animationCounter;
    private final int ANIMATION_SPEED = 3;
    private final int MAX_STEPS = 15;

    public Explosion(double x, double y, double size) {
        super(x - size/4, y - size/4, size * 1.2 , size *1.2);
        this.step = 0;
        this.animationCounter = 0;
    }

    public int getStep() {
        return step;
    }
    public int getAnimationCounter() {
        return animationCounter;
    }

    @Override
    public void update() {
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            step++;
            animationCounter = 0;
        }
    }

    public boolean isFinished(){
        return step >= MAX_STEPS;
    }
}