package com.bb.vivria.socket.data;

import javax.websocket.Session;

public class UserSession {
	
	private Session session = null;
	private String userNickName = "";
	private int userType = 0;
	
	// 방장
	private boolean roomChief = false;
	
	// 게임준비여부(방장은 불가)
	private boolean readyToGame = false;
	
	// 다시 되살릴 경우 유렁유저(고스트)로 set하고, 또 다시 되살릴 수 없게 만든다.
	// 그래야 중복이 없다.
	private boolean bCanRevive = false;
	
	
	/**
	 * 생성자
	 */
	public UserSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}
	
	public void setSession(Session session) {
		this.session = session;
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

	public boolean isRoomChief() {
		return roomChief;
	}

	public void setRoomChief(boolean roomChief) {
		this.roomChief = roomChief;
	}

	public boolean isReadyToGame() {
		return readyToGame;
	}

	public void setReadyToGame(boolean readyToGame) {
		this.readyToGame = readyToGame;
	}

	public boolean isbCanRevive() {
		return bCanRevive;
	}

	public void setbCanRevive(boolean bCanRevive) {
		this.bCanRevive = bCanRevive;
	}
}
