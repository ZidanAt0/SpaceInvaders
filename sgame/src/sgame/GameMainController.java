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
        
        player.update();
        enemies.forEach(Enemy::update);
        updateBullets();
        
        enemies.forEach(this::handleEnemyShooting);
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
    
    private void drawGameElements() {

    }
}
