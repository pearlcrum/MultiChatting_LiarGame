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
		// 우리가 생성한 FXML 파일을 불러올 FXML 로더이다 . 이로더에 FXML을 저장하고
		
		root = loader.load();
		// 로더에 저장된 정보를 root 에 로딩한뒤
		Scene scene=new Scene(root);
		scene.getStylesheets().add(getClass().getResource("Login.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);

		// FMXL 정보가 로딩된 root가 담긴 씬을 스테이지에 설정한다.
		primaryStage.setTitle("LiarGame Login!");
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
