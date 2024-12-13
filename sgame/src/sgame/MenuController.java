package sgame;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MenuController {
    @FXML private ImageView playgame;
    @FXML private ImageView exitgame;

    @FXML
    private void startgame(MouseEvent event) {
        try {
            Sgame.switchToGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void keluar(MouseEvent event) {
        Stage stage = (Stage) exitgame.getScene().getWindow();
        stage.close();
    }
}
