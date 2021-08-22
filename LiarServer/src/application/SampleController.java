package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;

public class SampleController {
	
	@FXML
	private ToggleButton button;
	@FXML
	private TextArea textArea;
	
	Main main=new Main();
	
	public void click(ActionEvent event)
	{
		String IP="127.0.0.1";//자기 자신의 주소
		int port=9876;
		if(button.getText().equals("시작하기"))
		{
			main.startServer(IP,port);
			Platform.runLater(()->{
				String message= String.format("[서버 시작]\n", IP, port);
				textArea.appendText(message);
				button.setText("종료하기");//버튼에 쓰인 정보 바꿔주기
			});
		}
		else
		{
			main.stopServer();
			Platform.runLater(()->{
				String message= String.format("[서버 종료]\n", IP, port);
				textArea.appendText(message);
				button.setText("시작하기");//버튼에 쓰인 정보 바꿔주기
			});
		}
	}
}
