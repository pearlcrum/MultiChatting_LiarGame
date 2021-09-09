package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;

public class ServerController {
	
	@FXML
	private ToggleButton button;
	@FXML
	private TextArea textArea;
	
	Main main=new Main();
	
	public void click(ActionEvent event)
	{
		String IP="127.0.0.1";//�ڱ� �ڽ��� �ּ�
		int port=9876;
		if(button.getText().equals("�����ϱ�"))
		{
			main.startServer(IP,port);
			Platform.runLater(()->{
				String message= String.format("[���� ����]\n", IP, port);
				textArea.appendText(message);
				button.setText("�����ϱ�");//��ư�� ���� ���� �ٲ��ֱ�
			});
		}
		else
		{
			main.stopServer();
			Platform.runLater(()->{
				String message= String.format("[���� ����]\n", IP, port);
				textArea.appendText(message);
				button.setText("�����ϱ�");//��ư�� ���� ���� �ٲ��ֱ�
			});
		}
	}
}
