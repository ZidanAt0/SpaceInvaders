
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
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
    private Image[] enemyImages;
    private Image[] playerFrames;
    private Image explosionImage;
    
    private int currentSpawnInterval;
    private int spawnCounter;

    private static int highScore = 0;
    private int score;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGameElements();
        loadResources();
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

    private void loadResources() {
        enemyImages = new Image[5];
        for (int i = 0; i < 5; i++) {
            enemyImages[i] = new Image(getClass().getResource("/sgame/asset/" + (i + 1) + ".png").toExternalForm());
        }

        playerFrames = new Image[6];
        for (int i = 0; i < 6; i++) {
            playerFrames[i] = new Image(getClass().getResource("/sgame/Player/Player_frame_" + (i + 1) + ".png").toExternalForm());
        }
        player.setSprites(playerFrames);
        explosionImage = new Image(getClass().getResource("/sgame/asset/explosion.png").toExternalForm());
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

                if (gameOver) {
                    drawGameOver();
                    return;
                }
                updateGameState();
                checkCollisions();
                drawGameElements();
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
        if (!player.isAlive()) {
            return;
        }

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
        Enemy enemy = new Enemy(enemyX, 0, enemyImages[random.nextInt(enemyImages.length)]);
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
        if (player.isAlive()) {
            if (!player.isInvulnerable() || (System.nanoTime() / 100_000_000) % 2 == 0) {
                gc.drawImage(player.getSprites()[player.getCurrentFrame()], 
                           player.getX(), player.getY(), 
                           player.getWidth(), player.getHeight());
                player.setCurrentFrame((player.getCurrentFrame() + 1) % player.getSprites().length);
            }
        }

        drawHealthBar();
        drawScore();

    }

    private void drawHealthBar() {
        final double HEALTHBAR_WIDTH = 150;
        final double HEALTHBAR_HEIGHT = 20;
        final double HEALTHBAR_X = 13;
        final double HEALTHBAR_Y = 17;

        gc.setFill(Color.BLACK);
        gc.fillRect(HEALTHBAR_X - 3, HEALTHBAR_Y - 3, 
                   HEALTHBAR_WIDTH + 6, HEALTHBAR_HEIGHT + 6);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(HEALTHBAR_X - 2, HEALTHBAR_Y - 2, 
                     HEALTHBAR_WIDTH + 4, HEALTHBAR_HEIGHT + 4);

        double healthPercentage = player.getHealth() / player.getMaxHealth();
        double healthBarWidth = HEALTHBAR_WIDTH * healthPercentage;

        gc.setFill(Color.RED);
        gc.fillRect(HEALTHBAR_X, HEALTHBAR_Y, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT);
        gc.setFill(Color.GREEN);
        gc.fillRect(HEALTHBAR_X, HEALTHBAR_Y, healthBarWidth, HEALTHBAR_HEIGHT);
    }

    private void drawGameOver(){
        gc.setFill(new Color(0, 0, 0, 0.5));
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        updateHighScore(score);

        gc.setFont(Font.font("Arial", 40));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        
        gc.fillText("Game Over", CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 - 40);
        gc.fillText("Score: " + score, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 + 20);
        gc.fillText("High Score: " + highScore, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 + 60);
        
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Click to Play Again", CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 + 100);
    }

    private void drawScore(){
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Score: " + score, 10, 60);
    }

    private static void updateHighScore(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;
        }
    }

}

