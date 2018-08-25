package com.bb.vivria.data;

public class TurnData {

	private String sessionId = null;
	private String userNickName = null;
	
	// 턴 객체 만료여부
	private boolean bIsOver = false;
	private boolean bKingIsDead = false;
	
	
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


	public boolean isbIsOver() {
		return bIsOver;
	}


	public void setbIsOver(boolean bIsOver) {
		this.bIsOver = bIsOver;
	}


	public boolean isbKingIsDead() {
		return bKingIsDead;
	}


	public void setbKingIsDead(boolean bKingIsDead) {
		this.bKingIsDead = bKingIsDead;
	}
	
	
}
