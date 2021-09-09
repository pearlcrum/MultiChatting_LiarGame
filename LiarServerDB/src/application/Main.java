package application;

import static common.JDBCTemplate.*;
import com.biz.*;


import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	public static ExecutorService threadPool;
	public static Vector<Client> clients = new Vector<Client>();
	ServerSocket loginServerSocket;// 9876
	ServerSocket chatServerSocket;// 9877
	ServerSocket voteServerSocket;// 9878
	public static Connection conn;
	MyLiarBiz biz=new MyLiarBiz();
	
	public void startServer(String IP, int port) {
		try {
			loginServerSocket = new ServerSocket();
			chatServerSocket = new ServerSocket();

			loginServerSocket.bind(new InetSocketAddress(IP, port));
			chatServerSocket.bind(new InetSocketAddress(IP, port + 1));
			
			biz.create_Liar_Table();//���̺� ����
		} catch (Exception e) {
			e.printStackTrace();
			if (!loginServerSocket.isClosed() || !chatServerSocket.isClosed()) {
				stopServer();
			}
			return;
		}
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket LoginSocket = loginServerSocket.accept();
						Socket chatSocket = chatServerSocket.accept();
						clients.add(new Client(LoginSocket,chatSocket));//���� �� ���� ���ο� ģ���� �޾��ش�.
						System.out.println("[Ŭ���̾�Ʈ ����]");
					} catch (Exception e) {
						if (!loginServerSocket.isClosed() || !chatServerSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
			}

		};
		// threadPool �ʱ�ȭ
		threadPool = Executors.newCachedThreadPool();
		// client�� ��ٸ��� thread�� ����ش�. �� �ȿ� ù��° thread�� Ŭ���̾�Ʈ ���� ��ٸ��� thread
		threadPool.submit(thread);

	}

	public void stopServer() {
		try {
			Iterator<Client> iterator=clients.iterator();
			while(iterator.hasNext()) {
				//�ϳ��� ��� client �� �����Ѵ�.
				Client client=iterator.next();
				client.LoginSocket.close();
				client.ChatSocket.close();
				//client.VoteSocket.close();
				iterator.remove();
			}
			//��� client�� ���� ������ ��������, ���� ��ü ���� ���� �ݾ��ش�.
			if(loginServerSocket!=null && !loginServerSocket.isClosed()) {
				loginServerSocket.close();
			}
			if(chatServerSocket!=null && !chatServerSocket.isClosed()) {
				chatServerSocket.close();
			}
			//������ Ǯ �����ϱ�
			if(threadPool!=null && !threadPool.isShutdown()) {
				threadPool.shutdown();//�ڿ� �Ҵ� ����
			}
			biz.drop_Liar_Table();			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("Server.fxml"));
			Scene scene = new Scene(root, 600, 400);
			primaryStage.setTitle("[ä�� ����]");
			primaryStage.setOnCloseRequest(event->stopServer());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	
	
}
