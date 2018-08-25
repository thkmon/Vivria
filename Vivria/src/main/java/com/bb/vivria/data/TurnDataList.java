package com.bb.vivria.data;

import java.util.ArrayList;

public class TurnDataList extends ArrayList<TurnData> {
	
	private int turnIndex = 0;

	
	public int getCurrentTurnIndex() {
		int count = this.size();
		if (count == 0) {
			return -1;
		}
		
		int lastIndex = count - 1;
		
		// 1. 리스트에서 턴 인덱스부터 찾기
		for (int i=turnIndex; i<=lastIndex; i++) {
			if (this.get(i) == null) {
				continue;
			}
			
			if (this.get(i).isbIsOver()) {
				continue;
			}
			
			// 턴 인덱스 반영
			turnIndex = i;
			return turnIndex;
		}
		
		// 2. 리스트에서 0부터 턴 인덱스 포함 전까지 찾기
		for (int i=0; i<turnIndex; i++) {
			if (this.get(i) == null) {
				continue;
			}
			
			if (this.get(i).isbIsOver()) {
				continue;
			}
			
			turnIndex = i;
			return turnIndex;
		}
		
		return -1;
	}

	
	public int getNextTurnIndex() throws MessageException, Exception {
		
		int count = this.size();
		if (count == 0) {
			return -1;
		}
		
		int lastIndex = count - 1;
		
		// 1. 리스트에서 턴 인덱스 1칸 다음부터 찾기
		for (int i=(turnIndex + 1); i<=lastIndex; i++) {
			if (this.get(i) == null) {
				continue;
			}
			
			if (this.get(i).isbIsOver()) {
				continue;
			}
			
			// 턴 인덱스 반영
			turnIndex = i;
			return turnIndex;
		}
		
		// 2. 리스트에서 0부터 턴 인덱스 포함까지 찾기
		for (int i=0; i<=turnIndex; i++) {
			if (this.get(i) == null) {
				continue;
			}
			
			if (this.get(i).isbIsOver()) {
				continue;
			}
			
			turnIndex = i;
			return turnIndex;
		}
		
		return -1;
	}
	
	
	public String getVictoryUserName() throws MessageException, Exception {
		
		int count = this.size();
		if (count == 0) {
			return null;
		}
		
		String validUserName = null;
		int validUserCount = 0;
		
		TurnData turnData = null;
		for (int i=0; i<count; i++) {
			turnData = this.get(i);
			if (turnData == null) {
				continue;
			}
			
			if (turnData.isbIsOver()) {
				continue;
			}
			
			if (turnData.getUserNickName() != null && turnData.getUserNickName().length() > 0) {
				validUserName = turnData.getUserNickName();
				validUserCount++;
			}
		}
		
		if (validUserCount == 1) {
			return validUserName;
		}
		
		return null;
	}
	
	
	/**
	 * 접속 해제된 세션에 대해서 턴 객체 되살린다.
	 * 
	 * @param sessionIdToRevive
	 * @param newSessionId
	 */
	public void reviveOveredTurn(String sessionIdToRevive, String newSessionId) {
		if (this.size() == 0) {
			return;
		}
		
		if (sessionIdToRevive == null || sessionIdToRevive.length() == 0) {
			return;
		}
		
		if (newSessionId == null || newSessionId.length() == 0) {
			return;
		}
		
		TurnData turnData = null;
		int count = this.size();
		for (int i=0; i<count; i++) {
			turnData = this.get(i);
			if (turnData == null) {
				continue;
			}
			
			String singleSessionId = turnData.getSessionId();
			if (singleSessionId != null && singleSessionId.equals(sessionIdToRevive)) {
				this.get(i).setSessionId(newSessionId);
				this.get(i).setbIsOver(false);
				break;
			}
		}
	}
	
	
	/**
	 * 접속 해제된 세션에 대해서 턴 객체 만료시킨다.
	 * 
	 * @param turnOverSessionId
	 * @param bKingIsDead
	 */
	public void setTurnIsOver(String turnOverSessionId, boolean bKingIsDead) {
		if (this.size() == 0) {
			return;
		}
		
		if (turnOverSessionId == null || turnOverSessionId.length() == 0) {
			return;
		}
		
		TurnData turnData = null;
		int count = this.size();
		for (int i=0; i<count; i++) {
			turnData = this.get(i);
			if (turnData == null) {
				continue;
			}
			
			String singleSessionId = turnData.getSessionId();
			if (singleSessionId != null && singleSessionId.equals(turnOverSessionId)) {
				this.get(i).setbIsOver(true);
				this.get(i).setbKingIsDead(bKingIsDead);
				break;
			}
		}
	}
	
	
	public String doProcessWhenKingIsDead(int kingIndex) {
		if (kingIndex < 0) {
			return null;
		}
		
		// 왕이 죽은 게이머의 턴을 만료시킨다.
		String sessionIdToRemove = this.get(kingIndex).getSessionId();
		String userNickName = this.get(kingIndex).getUserNickName();
		setTurnIsOver(sessionIdToRemove, true);
		
		return userNickName;
	}
	
	
	public TurnData getTurnData(String sessionId) {
		if (this.size() == 0) {
			return null;
		}
		
		if (sessionId == null || sessionId.length() == 0) {
			return null;
		}
		
		TurnData turnData = null;
		int count = this.size();
		for (int i=0; i<count; i++) {
			turnData = this.get(i);
			if (turnData == null) {
				continue;
			}
			
			String singleSessionId = turnData.getSessionId();
			if (singleSessionId != null && singleSessionId.equals(sessionId)) {
				return this.get(i);
			}
		}
		
		return null;
	}
}
