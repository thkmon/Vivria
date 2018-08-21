package com.bb.vivria.data;

import java.util.ArrayList;

public class TurnDataList extends ArrayList<TurnData> {
	
	private int turnIndex = 0;

	public int getTurnIndex() {
		return turnIndex;
	}

	private void setTurnIndex(int turnIndex) {
		this.turnIndex = turnIndex;
	}
	
	
	public int getNextTurnIndex() {
		turnIndex++;
		
		if (turnIndex < 0) {
			turnIndex = 0;
		}
		
		int lastIndex = this.size() - 1;
		if (turnIndex > lastIndex) {
			turnIndex = 0;
		}
		
		return turnIndex;
	}
	
	
	public void removeTurn(String sessionIdToRemove) {
		if (this.size() == 0) {
			return;
		}
		
		if (sessionIdToRemove == null || sessionIdToRemove.length() == 0) {
			return;
		}
		
		TurnData turnData = null;
		int count = this.size();
		int lastIndex = count - 1;
		for (int i=lastIndex; i>=0; i--) {
			turnData = this.get(i);
			if (turnData == null) {
				continue;
			}
			
			String singleSessionId = turnData.getSessionId();
			if (singleSessionId != null && singleSessionId.equals(sessionIdToRemove)) {
				this.remove(i);
				
				// 현재 턴 지웠으면 턴 증가
				getNextTurnIndex();
				
				return;
			}
		}
	}
}
