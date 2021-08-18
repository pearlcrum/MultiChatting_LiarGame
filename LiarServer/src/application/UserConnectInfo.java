package application;

import java.util.Vector;

public class UserConnectInfo {
	private String serverIP;
	private int port;
	
	public Vector<Integer> vec=new Vector<Integer>();
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
		vec.add(1);
	}
	public void deleteUser(String nickName)
	{
		MyDB.deleteUser(nickName);
		if(vec.size()>0)
			vec.removeElementAt(0);
	}
	public boolean checkLogin(String nickName) {
		boolean check = myDB.findByUserId(nickName);
		if (check == true) //�����ϸ� true
			return true;
		else  //������ false
			return false;
	}

}