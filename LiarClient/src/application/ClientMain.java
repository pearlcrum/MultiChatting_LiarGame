package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ClientMain {

	public Socket LoginSocket;
	public Socket ChatSocket;
	public boolean bool;
	public String message;
	public static boolean serverNow=true;//초기값 true

	public static final String loginClassifier="~";
	public static final String CreateAvailableClassifier="|";
	public static final String CreateNotAvailableClassifier="@";
	public static final String GameStartClassifier="#";
	public static final String LiarClassifier="$";
	public static final String GameStartReturnClassifier="%%";
	public static final String ServerNotAvailable="**";
	public String nickname;
	public boolean getBoolean() {
		return bool;
	}

	public void startLiarGame() {
		// 여러개의 thread 필요 없기에 runnable 대신 thread 객채 사용
		// 왜? 여러 프로세스가 동시간대 같이 실행되고 있기 때문에, thread로 실행을 해야한다.
		// 멀티채팅에서 thread는 필수적이다.
		Thread thread = new Thread() {
			public void run() {
				try {
					sendChat(GameStartClassifier);
				} catch (Exception e) {
					if (!ChatSocket.isClosed()) {
						stopClient();// 오류 시
						System.out.println("[서버 접속 실패]");
						Platform.exit();// 프로그램 종료
					}
				}
			}
		};
		thread.start();
	}

	public void endLiarGame() {
		// 여러개의 thread 필요 없기에 runnable 대신 thread 객채 사용
		Thread thread = new Thread() {
			public void run() {
				try {
					sendChat("게임종료");
				} catch (Exception e) {
					if (!ChatSocket.isClosed()) {
						stopClient();// 오류 시
						System.out.println("[서버 접속 실패]");
						Platform.exit();// 프로그램 종료
					}
				}
			}
		};
		thread.start();
	}

	public void startClient(String userName, String password, String IP, int port) {
		// 여러개의 thread 필요 없기에 runnable 대신 thread 객채 사용
		try {
			System.out.println("startClient 시작");
			LoginSocket = new Socket(IP, port);// 소켓 새로 생성 꼭 체크
			ChatSocket = new Socket(IP, port + 1);
			System.out.println("소켓 연결 완료");
			
			sendLogin(userName, password);
			System.out.println("sendLogin 완료");
			boolean chk = receiveLogin();// 생성 가능이면 true
			System.out.println("chk의 값은" + chk);
			if (chk == true) {
				bool = true;
				nickname=userName;
			} else
				bool = false;
		} catch (Exception e) {
			if (!LoginSocket.isClosed()) {
				bool = false;
				stopClient();// 오류 시
				System.out.println("[서버 접속 실패]");
				Platform.exit();// 프로그램 종료
			}
			if (!ChatSocket.isClosed()) {
				bool = false;
				stopClient();// 오류 시
				System.out.println("[서버 접속 실패]");
				Platform.exit();// 프로그램 종료
			}
		}
	}

	// 클라이언트 프로그램 종료 메소드
	public void stopClient() {
		try {
			if (LoginSocket != null && !LoginSocket.isClosed()) {
				LoginSocket.close();
			}
			if (ChatSocket != null && !ChatSocket.isClosed()) {
				ChatSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 서버로부터 메세지를 전달 받는 메소드
	public boolean receiveLogin() {
		while (true) {
			// 계속 전달 받음
			try {
				InputStream in = LoginSocket.getInputStream();// 서버로부터 전달 받음
				byte[] buffer = new byte[512];
				int length = in.read(buffer);// read 함수로 실제 입력 받는다.
				if (length == -1)
					throw new IOException();
				String message = new String(buffer, 0, length, "UTF-8");
				if (message.equals(CreateAvailableClassifier)) {
					System.out.println("생성가능합니다!");
					return true;
				}else if(message.equals(CreateNotAvailableClassifier)) {
					return false;
				}
				else if(message.equals(ServerNotAvailable)){
					serverNow=false;
					return false;
				}else {
					serverNow=false;
					return false;
				}		
				}
			catch (Exception e) {
				stopClient();
				return false;
			}
		}
	}

	// 서버로 메세지를 전송하는 메소드
	public void sendLogin(String id, String password) {
		// 서버로 전달하기 위해서도 thread 필요, receive thread와 다름
		String message=id+loginClassifier+password;
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = LoginSocket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
					stopClient();
				}
			}
		};
		thread.start();
	}

	// 서버로 메세지를 전송하는 메소드
	public void sendChat(String message) {
		// 서버로 전달하기 위해서도 thread 필요, receive thread와 다름
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = ChatSocket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();//outputStream은 왜 flush 시켜야 하나?
				} catch (Exception e) {
					stopClient();
				}
			}
		};
		thread.start();
	}
	public void requestNum() {
		Thread thread = new Thread() {
			public void run() {
				try {
					sendChat("총인원수");
				} catch (Exception e) {
					if (!ChatSocket.isClosed()) {
						stopClient();// 오류 시
						System.out.println("[서버 접속 실패]");
						Platform.exit();// 프로그램 종료
					}
				}
			}
		};
		thread.start();
	}
}
