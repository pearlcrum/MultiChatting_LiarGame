package application;

import java.util.Vector;

public class UserConnectInfo {
	private String serverIP;
	private int port;
	
	MyDB myDB=new MyDB();
	public UserConnectInfo(String serverIP, int port) {
		super();
		this.serverIP = serverIP;
		this.port = port;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	public void addUser(String nickName,String serverIP, int port) {
		MyDB.addUser(nickName, new UserConnectInfo(serverIP,port));
	}
	public void deleteUser(String nickName)
	{
		MyDB.deleteUser(nickName);
	}
	public boolean checkLogin(String nickName) {
		boolean check = myDB.findByUserId(nickName);
		if (check == true) //존재하면 true
			return true;
		else  //없으면 false
			return false;
	}

}
