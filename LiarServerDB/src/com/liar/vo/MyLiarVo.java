package com.liar.vo;

public class MyLiarVo {

	private String userID;
	private String password;
	
	public MyLiarVo() {
		super();
	}
	public MyLiarVo(String userID, String password) {
		super();
		this.userID = userID;
		this.password = password;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
