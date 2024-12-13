package sgame;

public class Player extends Character {
    private double health;
    private double maxHealth;
    private boolean isInvulnerable;
    private long invulnerabilityTimer;
    private final long INVULNERABILITY_DURATION = 2_000_000_000;

    public Player(double x, double y) {
        super(x, y, 70, 70, 20, 20, 5);
        this.maxHealth = 100;
        this.health = maxHealth;
        this.isInvulnerable = false;
    }

    public double getHealth() { return health; }
    public double getMaxHealth() { return maxHealth; }
    public boolean isInvulnerable() { return isInvulnerable; }
    
    @Override
    public void update() {
        if (isInvulnerable && System.nanoTime() - invulnerabilityTimer > INVULNERABILITY_DURATION) {
            isInvulnerable = false;
        }
    }

    @Override
    public void handleCollision() {
        if (!isInvulnerable) {
            isInvulnerable = true;
            invulnerabilityTimer = System.nanoTime();
        }
    }
}
