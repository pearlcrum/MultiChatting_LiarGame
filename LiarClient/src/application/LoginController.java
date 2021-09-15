package application;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

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

	public static boolean serverAvailable=true;
	
	String IP = "127.0.0.1";//218.239.185.202
	int port = 9876;

	public LoginController() {
		System.out.println("Controller Constuctor called");
		loginSource = new ClientMain();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) { // �����ʿ�
		System.out.println("Controller initialize called");
		try {
			cancleBtn.setOnAction(e -> {
				cancleBtnPressed(e);
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	// popup �߻� �ڵ�
	
	//��й�ȣ null�� üũ ���� null�̸� true�� ��ȯ
	boolean checkString(String str) {
		  return str == null || str.isEmpty();
		}
	
	@FXML
	private void Login() {
		System.out.println("Login button pressed!");
		Alert alert = new Alert(AlertType.INFORMATION);
		String ID = idField.getText();
		String PW = pwField.getText();
		
		if(ClientMain.serverNow==false)
		{
			alert.setHeaderText("�˸�");
			alert.setContentText("���� ������ ���� ���Դϴ�. ��� �Ŀ� �����ϼ���");
			alert.show();
			loginSource.stopClient();
			closeStage();
			return;
		}else if(ClientMain.serverNow==true && checkString(PW) != true && checkString(ID) != true) { //��й�ȣ null�� üũ
			System.out.println("login startClient ȣ��");
			loginSource.startClient(ID, PW,IP, port);
			if(ClientMain.serverNow==false)
			{
				alert.setHeaderText("�˸�");
				alert.setContentText("���� ������ ���� ���Դϴ�. ��� �Ŀ� �����ϼ���");
				alert.show();
				loginSource.stopClient();
				closeStage();
				return;
			}
			System.out.println("login startClient ȣ�� �Ϸ�");
			//������ �õ��� ���̴�.
			boolean check = loginSource.getBoolean();
			System.out.println(check);
			if (check == true) {
				try {
					FXMLLoader loader = new FXMLLoader();
					//loader.setLocation(getClass().getResource("WaitingRoom.fxml"));
					loader.setLocation(getClass().getResource("Chatroom_final.fxml"));
					Parent root;
					try {
						root = (Parent) loader.load();
						Scene scene = new Scene(root);
						//scene.getStylesheets().add(getClass().getResource("WaitingRoom.css").toString());
						scene.getStylesheets().add(getClass().getResource("Chatroom_final.css").toString());
						ChatroomController pop = loader.getController();
						pop.initData(loginSource, ID);

						Stage stage = new Stage();
						stage.setScene(scene);

						stage.setResizable(false);
						stage.setOnCloseRequest(event -> {
							pop.logout();
						});
						stage.setTitle("LiarGame - Chatting Room");
						stage.show();
						closeStage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				alert.setHeaderText("�˸�");
				alert.setContentText("�α��ο� �����߽��ϴ�.(�ߺ� ID)");
				alert.show();
			}
		}
		else { //��й�ȣ null�̸� ����â �˸�
			System.out.println("��й�ȣ ���Է�");
			alert.setHeaderText("�˸�");
			alert.setContentText("�α��ο� �����߽��ϴ�.(ID �Ǵ� Password ���Է�)");
			alert.show();
		}
		ClearTextField();
	}

	public static void terminate() {
		// X������ ���� ȣ���.
		System.exit(0);
	}

	// �Ķ���� ����
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
			e.printStackTrace();
		}
	}

	// ����â �ݴ� �ڵ�
	public void closeStage() {
		Stage stage11 = (Stage) loginBtn.getScene().getWindow();
		Platform.runLater(() -> {
			stage11.close();
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
	}
}
