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
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGameElements();
        setupCanvas();
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

    private void updateGameState() {
        player.update();
        enemies.forEach(Enemy::update);
    }

    private void drawGameElements() {

    }
}