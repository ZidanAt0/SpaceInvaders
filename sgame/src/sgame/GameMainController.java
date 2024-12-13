package sgame;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;

public class GameMainController implements Initializable {
    @FXML
    private AnchorPane gamePane;
    
    private static final double CANVAS_WIDTH = 650;
    private static final double CANVAS_HEIGHT = 750;
    
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> playerBullets;
    private List<Bullet> enemyBullets;
    private List<Explosion> explosions;
    private boolean gameOver;

    private static final int INITIAL_SPAWN_INTERVAL = 200;
    private static final int MIN_SPAWN_INTERVAL = 50;
    private static final int SPAWN_SPEEDUP_THRESHOLD = 10;
    private static final int SPAWN_INTERVAL_DECREASE = 30;

    private Random random;
    private int currentSpawnInterval;
    private int spawnCounter;
    private int score;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGameElements();
        setupCanvas();
        setupControls();
        setupEnemySystem();
        startGameLoop();
    }

    private void initializeGameElements() {
        player = new Player((CANVAS_WIDTH - 70) / 2, CANVAS_HEIGHT - 80);
        
        enemies = new ArrayList<>();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        explosions = new ArrayList<>();
        gameOver = false;
    }

    private void setupCanvas() {
        gameCanvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gamePane.getChildren().add(gameCanvas);
        gc = gameCanvas.getGraphicsContext2D();
    }

    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                
                if (!gameOver) {
                    updateGameState();
                    drawGameElements();
                }
            }
        }.start();
    }
    
    private void setupControls() {
        gamePane.setOnMouseMoved(event -> {
            if (player.isAlive() && !gameOver) {
                double newX = Math.min(CANVAS_WIDTH - player.getWidth() / 2, 
                                     Math.max(player.getWidth() - 85, event.getX() - player.getWidth() / 1.5));
                double newY = Math.min(CANVAS_HEIGHT - player.getHeight() - 10, 
                                     Math.max((2.0 / 3.0) * CANVAS_HEIGHT, event.getY() - player.getHeight() / 2));
                player.setX(newX);
                player.setY(newY);
            }
        });

        gamePane.setOnMouseClicked(event -> {
            if (!gameOver && player.isAlive()) {
                shootPlayerBullet();
            }
        });
        gamePane.setFocusTraversable(true);
    }

    private void updateGameState() {
        updateSpawnInterval();
        handleEnemySpawning();

        if (player.isAlive()) {
            checkCollisions();
        }
        
        player.update();
        enemies.forEach(Enemy::update);
        updateBullets();
        
        enemies.forEach(this::handleEnemyShooting);
    }

    private void checkCollisions() {
        enemies.removeIf(enemy -> {
            if (isColliding(player, enemy)) {
                handlePlayerCollision(20);
                createExplosion(enemy.getX(), enemy.getY(), enemy.getWidth());
                return true;
            }
            if (enemy.getY() > CANVAS_HEIGHT) {
                handlePlayerCollision(10);
                return true;
            }
            return false;
        });

        for (Bullet bullet : new ArrayList<>(playerBullets)) {
            for (Enemy enemy : new ArrayList<>(enemies)) {
                if (isBulletColliding(bullet, enemy)) {
                    playerBullets.remove(bullet);
                    enemies.remove(enemy);
                    createExplosion(enemy.getX(), enemy.getY(), enemy.getWidth());
                    score++;
                    break;
                }
            }
        }

        enemyBullets.removeIf(bullet -> {
            if (isBulletColliding(bullet, player)) {
                handlePlayerCollision(20);
                return true;
            }
            return false;
        });
    }

    private boolean isColliding(Character char1, Character char2) {
        double hitboxOffset1X = (char1.getWidth() - char1.getHitboxWidth()) / 2;
        double hitboxOffset1Y = (char1.getHeight() - char1.getHitboxHeight()) / 2;
        double hitboxOffset2X = (char2.getWidth() - char2.getHitboxWidth()) / 2;
        double hitboxOffset2Y = (char2.getHeight() - char2.getHitboxHeight()) / 2;

        return (char1.getX() + hitboxOffset1X) < (char2.getX() + char2.getWidth() - hitboxOffset2X) &&
               (char1.getX() + char1.getWidth() - hitboxOffset1X) > (char2.getX() + hitboxOffset2X) &&
               (char1.getY() + hitboxOffset1Y) < (char2.getY() + char2.getHeight() - hitboxOffset2Y) &&
               (char1.getY() + char1.getHeight() - hitboxOffset1Y) > (char2.getY() + hitboxOffset2Y);
    }

    private boolean isBulletColliding(Bullet bullet, Character character) {
        double hitboxOffsetX = (character.getWidth() - character.getHitboxWidth()) / 2;
        double hitboxOffsetY = (character.getHeight() - character.getHitboxHeight()) / 2;

        return bullet.getX() < (character.getX() + character.getWidth() - hitboxOffsetX) &&
               (bullet.getX() + bullet.getWidth()) > (character.getX() + hitboxOffsetX) &&
               bullet.getY() < (character.getY() + character.getHeight() - hitboxOffsetY) &&
               (bullet.getY() + bullet.getHeight()) > (character.getY() + hitboxOffsetY);
    }

    private void handlePlayerCollision(double damage) {
        if (!player.isInvulnerable()) {
            player.takeDamage(damage);
            if (!player.isAlive()) {
                createExplosion(player.getX(), player.getY(), player.getWidth());
                gameOver = true;
            }
        }
    }

    private void createExplosion(double x, double y, double size) {
        explosions.add(new Explosion(x, y, size));
    }

    private void updateExplosions() {
        explosions.removeIf(explosion -> {
            explosion.update();
            return explosion.isFinished();
        });
    }



    private void shootPlayerBullet() {
        Bullet bullet = new Bullet(
            player.getX() + player.getWidth() / 2 - 2.5,
            player.getY() - 5,
            Color.RED,
            5,
            false
        );
        playerBullets.add(bullet);
    }

    private void updateBullets() {
        playerBullets.removeIf(bullet -> {
            bullet.update();
            return bullet.isOffScreen(CANVAS_HEIGHT);
        });

        enemyBullets.removeIf(bullet -> {
            bullet.update();
            return bullet.isOffScreen(CANVAS_HEIGHT);
        });
    }

    private void setupEnemySystem() {
        random = new Random();
        currentSpawnInterval = INITIAL_SPAWN_INTERVAL;
        spawnCounter = 0;
        score = 0;
    }

    private void updateSpawnInterval() {
        int speedupLevel = score / SPAWN_SPEEDUP_THRESHOLD;
        int newInterval = INITIAL_SPAWN_INTERVAL - (speedupLevel * SPAWN_INTERVAL_DECREASE);
        currentSpawnInterval = Math.max(newInterval, MIN_SPAWN_INTERVAL);
    }

    private void handleEnemySpawning() {
        if (spawnCounter++ >= currentSpawnInterval) {
            spawnEnemy();
            spawnCounter = 0;
        }
        
        enemies.removeIf(enemy -> enemy.getY() > CANVAS_HEIGHT);
    }

    private void spawnEnemy() {
        double enemyX = random.nextDouble() * (CANVAS_WIDTH - 40);
        Enemy enemy = new Enemy(enemyX, 0, null);
        enemies.add(enemy);
    }

    private void handleEnemyShooting(Enemy enemy) {
        if (random.nextInt(240) == 0) {
            Bullet bullet = new Bullet(
                enemy.getX() + enemy.getWidth() / 2,
                enemy.getY() + enemy.getHeight(),
                Color.YELLOW,
                1.5,
                true
            );
            enemyBullets.add(bullet);
        }
    }

    private void drawGameElements() {

    }
}
