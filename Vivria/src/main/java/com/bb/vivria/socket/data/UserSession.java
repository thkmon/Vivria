package com.bb.vivria.socket.data;

import javax.websocket.Session;

public class UserSession {
	
	private Session session = null;
	private String userNickName = "";
	private int userType = 0;
	
	
	/**
	 * 생성자
	 */
	public UserSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}
}
