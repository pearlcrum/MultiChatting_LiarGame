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
	public static boolean serverNow=true;//�ʱⰪ true

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
		// �������� thread �ʿ� ���⿡ runnable ��� thread ��ä ���
		// ��? ���� ���μ����� ���ð��� ���� ����ǰ� �ֱ� ������, thread�� ������ �ؾ��Ѵ�.
		// ��Ƽä�ÿ��� thread�� �ʼ����̴�.
		Thread thread = new Thread() {
			public void run() {
				try {
					sendChat(GameStartClassifier);
				} catch (Exception e) {
					if (!ChatSocket.isClosed()) {
						stopClient();// ���� ��
						System.out.println("[���� ���� ����]");
						Platform.exit();// ���α׷� ����
					}
				}
			}
		};
		thread.start();
	}

	public void endLiarGame() {
		// �������� thread �ʿ� ���⿡ runnable ��� thread ��ä ���
		Thread thread = new Thread() {
			public void run() {
				try {
					sendChat("��������");
				} catch (Exception e) {
					if (!ChatSocket.isClosed()) {
						stopClient();// ���� ��
						System.out.println("[���� ���� ����]");
						Platform.exit();// ���α׷� ����
					}
				}
			}
		};
		thread.start();
	}

	public void startClient(String userName, String password, String IP, int port) {
		// �������� thread �ʿ� ���⿡ runnable ��� thread ��ä ���
		try {
			System.out.println("startClient ����");
			LoginSocket = new Socket(IP, port);// ���� ���� ���� �� üũ
			ChatSocket = new Socket(IP, port + 1);
			System.out.println("���� ���� �Ϸ�");
			
			sendLogin(userName, password);
			System.out.println("sendLogin �Ϸ�");
			boolean chk = receiveLogin();// ���� �����̸� true
			System.out.println("chk�� ����" + chk);
			if (chk == true) {
				bool = true;
				nickname=userName;
			} else
				bool = false;
		} catch (Exception e) {
			if (!LoginSocket.isClosed()) {
				bool = false;
				stopClient();// ���� ��
				System.out.println("[���� ���� ����]");
				Platform.exit();// ���α׷� ����
			}
			if (!ChatSocket.isClosed()) {
				bool = false;
				stopClient();// ���� ��
				System.out.println("[���� ���� ����]");
				Platform.exit();// ���α׷� ����
			}
		}
	}

	// Ŭ���̾�Ʈ ���α׷� ���� �޼ҵ�
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

	// �����κ��� �޼����� ���� �޴� �޼ҵ�
	public boolean receiveLogin() {
		while (true) {
			// ��� ���� ����
			try {
				InputStream in = LoginSocket.getInputStream();// �����κ��� ���� ����
				byte[] buffer = new byte[512];
				int length = in.read(buffer);// read �Լ��� ���� �Է� �޴´�.
				if (length == -1)
					throw new IOException();
				String message = new String(buffer, 0, length, "UTF-8");
				if (message.equals(CreateAvailableClassifier)) {
					System.out.println("���������մϴ�!");
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

	// ������ �޼����� �����ϴ� �޼ҵ�
	public void sendLogin(String id, String password) {
		// ������ �����ϱ� ���ؼ��� thread �ʿ�, receive thread�� �ٸ�
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

	// ������ �޼����� �����ϴ� �޼ҵ�
	public void sendChat(String message) {
		// ������ �����ϱ� ���ؼ��� thread �ʿ�, receive thread�� �ٸ�
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = ChatSocket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();//outputStream�� �� flush ���Ѿ� �ϳ�?
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
					sendChat("���ο���");
				} catch (Exception e) {
					if (!ChatSocket.isClosed()) {
						stopClient();// ���� ��
						System.out.println("[���� ���� ����]");
						Platform.exit();// ���α׷� ����
					}
				}
			}
		};
		thread.start();
	}
}
