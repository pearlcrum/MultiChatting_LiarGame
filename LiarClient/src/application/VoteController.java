package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class VoteController implements Initializable {
	@FXML
	public Label myLabel;
	@FXML
	public ComboBox<String> combobox;
	@FXML
	public Button vote;
	
	ObservableList<String> list;
	ClientMain clientSource;
	
	public void initFunc(ObservableList<String> arr,ClientMain clientSource) {
		list = arr;
		combobox.setItems(list);
		this.clientSource=clientSource;
		System.out.println(list);
	}

	public void closeStage() {
		Stage stage11 = (Stage) vote.getScene().getWindow();
		Platform.runLater(() -> {
			stage11.close();
		});
	}
	@FXML
	public void voteConfirm() {
		clientSource.sendChat("��ǥ:"+(String)(combobox.getValue()));
		closeStage();
	}
	//�� ��ǥ�� �������� ������.//chat Socket�̿�
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		;
	}

	public void comboChange(ActionEvent event) {
		myLabel.setText(combobox.getValue());
	}

}