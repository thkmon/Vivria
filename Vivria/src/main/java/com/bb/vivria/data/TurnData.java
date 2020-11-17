package com.bb.vivria.data;

public class TurnData {

	private String sessionId = null;
	private String userNickName = null;
	
	// 턴 객체 만료여부
	private boolean bDisconnected = false;
	private boolean bGameOver = false;
	
	
	public String getSessionId() {
		return sessionId;
	}
	
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	
	public String getUserNickName() {
		return userNickName;
	}


	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}
	
	
	public boolean isbDisconnected() {
		return bDisconnected;
	}
	
	
	public void setbDisconnected(boolean bDisconnected) {
		this.bDisconnected = bDisconnected;
	}


	public boolean isbGameOver() {
		return bGameOver;
	}


	public void setbGameOver(boolean bGameOver) {
		this.bGameOver = bGameOver;
	}
}