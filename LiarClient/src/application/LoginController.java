package application;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	@FXML
	private PasswordField pwField;
	@FXML
	private TextField idField;
	@FXML
	private CheckBox rememChk;
	@FXML
	private Button loginBtn;
	@FXML
	private Button cancleBtn;

	ClientMain loginSource;

	String IP = "127.0.0.1";
	int port = 9876;

	public LoginController() {
		System.out.println("Controller Constuctor called");
		loginSource = new ClientMain();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) { // 이해필요
		System.out.println("Controller initialize called");
		try {
			// loginBtn.setOnAction(e->{LoginBtnPressed(e);});
			cancleBtn.setOnAction(e -> {
				cancleBtnPressed(e);
			});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	// popup 발생 코드
	@FXML
	private void Login() {
		System.out.println("Login button pressed!");
		Alert alert = new Alert(AlertType.INFORMATION);
		String ID = idField.getText();
		String PW = pwField.getText();
		System.out.println("login startClient 호출");
		loginSource.startClient(ID, IP, port);
		System.out.println("login startClient 호출 완료");
		boolean check = loginSource.getBoolean();
		System.out.println(check);
		if (check == true) {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("Chatroom_final.fxml"));
				Parent root;
				try {
					root = (Parent) loader.load();
					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("Chatroom_final.css").toString());
					ChatroomController pop = loader.getController();
					pop.initData(loginSource, ID);

					Stage stage = new Stage();
					stage.setScene(scene);

					stage.setResizable(false);
					stage.setOnCloseRequest(event -> {
						pop.logout();
					});
					stage.show();
					closeStage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * FXMLLoader loader = new FXMLLoader(getClass().getResource("Chatroom.fxml"));
				 * Parent root = (Parent) loader.load(); Stage stage = new Stage();
				 * stage.setTitle("popup"); Scene scene=new Scene(root);
				 * scene.getStylesheets().add(getClass().getResource("Chatroom.css").toString())
				 * ;
				 * 
				 * stage.setResizable(false); stage.setScene(scene); stage.show(); closeStage();
				 * }catch(Exception e) { e.printStackTrace(); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			alert.setHeaderText("알림");
			alert.setContentText("로그인에 실패했습니다.(중복 ID)");
			alert.show();
		}
		ClearTextField();
	}

	public static void terminate() {
		// X누르면 여기 호출됨.
		System.exit(0);
	}

	// 파라미터 전달
	private void sendData(ClientMain loginSource) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Chatroom_final.fxml"));
		Parent root;
		try {
			root = (Parent) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("Chatroom_final.css").toString());
			ChatroomController pop = loader.getController();
			pop.initData(loginSource, idField.getText());

			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setOnCloseRequest(event -> {
				pop.logout();
			});
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 기존창 닫는 코드
	public void closeStage() {
		Stage stage11 = (Stage) loginBtn.getScene().getWindow();
		Platform.runLater(() -> {
			stage11.close();
			// loginSource.stopClient();
		});
	}

	private void cancleBtnPressed(ActionEvent event) {
		loginSource.stopClient();
		closeStage();
	}

	private void ClearTextField() {
		if (!rememChk.isSelected())
			idField.clear();
		pwField.clear();
		pwField.clear();
	}
}
