package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	FXMLLoader loader;
	Parent root;
	@Override
	public void start(Stage primaryStage) throws Exception {
		loader = new FXMLLoader(getClass().getResource("Login.fxml"));
		// �츮�� ������ FXML ������ �ҷ��� FXML �δ��̴� . �̷δ��� FXML�� �����ϰ�
		
		root = loader.load();
		// �δ��� ����� ������ root �� �ε��ѵ�
		Scene scene=new Scene(root);
		scene.getStylesheets().add(getClass().getResource("Login.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);

		// FMXL ������ �ε��� root�� ��� ���� ���������� �����Ѵ�.
		primaryStage.setTitle("LiarGame Login!");
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
