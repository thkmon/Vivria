package com.bb.vivria.data;

import javax.websocket.Session;

import com.bb.vivria.common.GameConst;
import com.bb.vivria.socket.data.UserSession;
import com.bb.vivria.socket.data.UserSessionList;
import com.bb.vivria.util.GameServiceUtil;

public class RoomData implements GameConst {
	
	private UserSessionList userSessionList = null;
	private TileData[][] tileDataArray = null;
	private StringList gamerIdList = null;
	private int gamerTurnIndex = 0;
	
	// 게임시작되었는지 여부
	private boolean gameIsStarted = false;
	
	
	public RoomData() {
		userSessionList = new UserSessionList();
	}
	

	public UserSessionList getUserSessionList() {
		return userSessionList;
	}
	

	private void setUserSessionList(UserSessionList userSessionList) {
		this.userSessionList = userSessionList;
	}
	
	
	public boolean addUserSession(UserSession userSession) {
		if (userSession == null) {
			return false;
		}
		
		this.userSessionList.add(userSession);
		return true;
	}

	
	public UserSession getUserSession(int index) {
		return this.userSessionList.get(index);
	}


	public boolean isGameIsStarted() {
		return gameIsStarted;
	}


	private void setGameIsStarted(boolean gameIsStarted) {
		this.gameIsStarted = gameIsStarted;
	}
	
	
	public void startNewGame(Session session) {

		// 게이머 리스트 구한다.
		gamerIdList = createGamerIdList(session);
		
		gamerTurnIndex = 0;
		
		// 새 타일 데이터(맵)을 생성한다.
		int gamerCount = gamerIdList.size();
		tileDataArray = createTileData(gamerCount);
		
		// 게임시작
		this.gameIsStarted = true;
	}
	
	
	/**
	 * 새 타일 데이터(맵)을 생성한다.
	 * 
	 * @param gamerCount
	 */
	private TileData[][] createTileData(int gamerCount) {
		
		TileData[][] tileDataArray = null;
		
		tileDataArray = new TileData[11][11];
		
		for (int c=0; c<=10; c++) {
			for (int r=0; r<=10; r++) {
				tileDataArray[c][r] = new TileData();
			}
		}

		// 기본값은 2명임.
		
		// 게이머 2명 이상 (공통)
		if (gamerCount >= 2) {
			for (int c=0; c<=3; c++) {
				for (int r=0; r<=3; r++) {
					tileDataArray[c][r].setCanMove(false);
				}
			}
			
			for (int c=7; c<=10; c++) {
				for (int r=0; r<=3; r++) {
					tileDataArray[c][r].setCanMove(false);
				}
			}
			
			for (int c=0; c<=3; c++) {
				for (int r=7; r<=10; r++) {
					tileDataArray[c][r].setCanMove(false);
				}
			}
			
			for (int c=7; c<=10; c++) { 
				for (int r=7; r<=10; r++) {
					tileDataArray[c][r].setCanMove(false);
				}
			}
		}
		
		// 게이머 3명 이하
		if (gamerCount <= 3) {
			// 아래 영역을 닫는다.
			for (int c=4; c<=6; c++) {
				for (int r=7; r<=10; r++) {
					tileDataArray[c][r].setCanMove(false);
				}
			}		
		}
		
		// 게이머 2명 이하
		if (gamerCount <= 2) {
			// 위 영역을 닫는다.
			for (int c=4; c<=6; c++) {
				for (int r=0; r<=3; r++) {
					tileDataArray[c][r].setCanMove(false);
				}
			}
		}
		
		return tileDataArray;
	}
	
	

	private StringList createGamerIdList(Session session) {
		
		UserSessionList userSessionList = GameServiceUtil.getUserSessionListBySession(session);
		if (userSessionList == null) {
			return null;
		}

		int sessionCount = userSessionList.size();
		if (sessionCount < 1) {
			return null;
		}

		StringList resultList = new StringList();
		
		Session singleSession = null;

		for (int i = 0; i < sessionCount; i++) {
			singleSession = userSessionList.getOriginSession(i);
			if (singleSession == null) {
				continue;
			}

			if (!singleSession.isOpen()) {
				continue;
			}

			if (userSessionList.get(i).isRoomChief()) {
				String singleSessionId = singleSession.getId();
				if (singleSessionId != null && singleSessionId.length() > 0) {
					resultList.add(singleSessionId);
				}
				continue;
			}
			
			if (userSessionList.get(i).getUserType() == USER_TYPE_GAMER) {
				String singleSessionId = singleSession.getId();
				if (singleSessionId != null && singleSessionId.length() > 0) {
					resultList.add(singleSessionId);
				}
				continue;
			}
		}

		return resultList;
	}
}