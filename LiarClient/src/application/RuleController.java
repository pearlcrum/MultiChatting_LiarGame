package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RuleController implements Initializable{

	@FXML private Button close;
	@FXML
	public void closePop() {
		closeStage();
	}

	public void closeStage() {
		Stage stage11 = (Stage) close.getScene().getWindow();
		Platform.runLater(() -> {
			stage11.close();
			//loginSource.stopClient();
		});
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		;
	}
}
