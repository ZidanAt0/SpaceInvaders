package sgame;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MenuController implements Initializable {
    private MediaPlayer mediaPlayer;

    @FXML private AnchorPane menuPane;
    @FXML private ImageView playgame;
    @FXML private ImageView exitgame;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Initialize menu background music
            Media menuSound = new Media(getClass().getResource("/sgame/sfx/menu.mp3").toExternalForm());
            mediaPlayer = new MediaPlayer(menuSound);
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
            mediaPlayer.setVolume(0.5);
            mediaPlayer.play();
        } catch (Exception e) {
            showError("Tidak dapat memuat musik background. Game akan dilanjutkan tanpa musik.");
        }
    }

    @FXML
    private void startgame(MouseEvent event) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            Sgame.switchToGame();
        } catch (Exception e) {
            showError("Gagal memulai game. Silakan coba lagi.");
        }
    }

    @FXML
    private void keluar(MouseEvent event) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            Stage stage = (Stage) exitgame.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.exit(0);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
