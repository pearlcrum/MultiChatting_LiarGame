package application;

import static common.JDBCTemplate.*;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.liar.biz.*;

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
	public static Connection conn;
	MyLiarBiz biz=new MyLiarBiz();
	
	public void startServer(String IP, int port) {
		try {
			loginServerSocket = new ServerSocket();
			chatServerSocket = new ServerSocket();

			loginServerSocket.bind(new InetSocketAddress(IP, port));
			chatServerSocket.bind(new InetSocketAddress(IP, port + 1));
			
			biz.create_Liar_Table();//테이블 생성
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
						clients.add(new Client(LoginSocket,chatSocket));//접속 시 마다 새로운 친구들 받아준다.
						System.out.println("[클라이언트 접속]");
					} catch (Exception e) {
						if (!loginServerSocket.isClosed() || !chatServerSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
			}

		};
		// threadPool 초기화
		threadPool = Executors.newCachedThreadPool();
		// client를 기다리는 thread를 담아준다. 그 안에 첫번째 thread로 클라이언트 접속 기다리는 thread
		threadPool.submit(thread);

	}

	public void stopServer() {
		try {
			Iterator<Client> iterator=clients.iterator();
			while(iterator.hasNext()) {
				//하나씩 모든 client 에 접근한다.
				Client client=iterator.next();
				client.LoginSocket.close();
				client.ChatSocket.close();
				//client.VoteSocket.close();
				iterator.remove();
			}
			//모든 client에 대한 연결이 끊겼으니, 서버 객체 소켓 또한 닫아준다.
			if(loginServerSocket!=null && !loginServerSocket.isClosed()) {
				loginServerSocket.close();
			}
			if(chatServerSocket!=null && !chatServerSocket.isClosed()) {
				chatServerSocket.close();
			}
			//쓰레드 풀 종료하기
			if(threadPool!=null && !threadPool.isShutdown()) {
				threadPool.shutdown();//자원 할당 해제
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
			primaryStage.setTitle("[채팅 서버]");
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
