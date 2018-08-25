package com.bb.vivria.data;

import javax.websocket.Session;

import com.bb.vivria.common.GameConst;
import com.bb.vivria.socket.data.UserSession;
import com.bb.vivria.socket.data.UserSessionList;
import com.bb.vivria.util.GameServiceUtil;

public class RoomData implements GameConst {
	
	private String roomId = null;
	private String roomName = null;
	private UserSessionList userSessionList = null;
	private TileData[][] tileDataArray = null;
	private TurnDataList turnDataList = null;
	
	// 패배한 유저 네임을 저장해둔다.
	private String defeatUserName = null;
	
	private boolean bClosed = false;
	
	// 게임시작되었는지 여부
	private boolean gameIsStarted = false;

	
	public String getRoomId() {
		return roomId;
	}


	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}


	public String getRoomName() {
		return roomName;
	}


	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}


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
	
	
	public boolean isbClosed() {
		return bClosed;
	}


	public void setbClosed(boolean bClosed) {
		this.bClosed = bClosed;
	}
	

	public String getDefeatUserName() {
		return defeatUserName;
	}


	public void setDefeatUserName(String defeatUserName) {
		this.defeatUserName = defeatUserName;
	}


	/**
	 * 게임시작
	 * 
	 * @param session
	 */
	public void startNewGame(Session session) throws MessageException, Exception {

		// 게이머 리스트 구한다.
		turnDataList = createGamerIdList(session);
		
		// 새 타일 데이터(맵)을 생성한다.
		int gamerCount = turnDataList.size();
		tileDataArray = createTileData(gamerCount);
		
		// 게임시작
		this.gameIsStarted = true;
	}
	
	
	/**
	 * 다음턴 지정
	 * 
	 * @return
	 */
	public void setNextTurn() throws MessageException, Exception {
		turnDataList.getNextTurnIndex();
	}
	
	
	private TurnDataList getTurnDataList() {
		return turnDataList;
	}


	public void setTurnDataList(TurnDataList turnDataList) {
		this.turnDataList = turnDataList;
	}


	/**
	 * 연결 끊기면 턴 조정
	 * 
	 * @param sessionIdToRemove
	 */
	public void removeSessionOfTurn(String sessionIdToRemove) {
		if (turnDataList == null || turnDataList.size() == 0) {
			return;
		}
		
		turnDataList.setTurnIsOver(sessionIdToRemove, false);
	}
	
	
	/**
	 * 턴을 되살린다.
	 * 
	 * @param sessionIdToRevive
	 * @param newSessionId
	 */
	public void reviveSessionOfTurn(String sessionIdToRevive, String newSessionId) {
		if (turnDataList == null || turnDataList.size() == 0) {
			return;
		}
		
		turnDataList.reviveOveredTurn(sessionIdToRevive, newSessionId);
	}
	
	
	/**
	 * 클라이언트 화면에 맵 그리기
	 * 
	 * @return
	 */
	public String getMapStringForDraw() {
		if (tileDataArray == null) {
			return null;
		}
		
		// - 비브리아 있는지 없는지 => 통행불가/통행가능
		// - 색상
		// - 어느 유저의 비브리아인지 
		// - 숫자가 몇인지
		
		StringBuffer buff = new StringBuffer();
		
		for (int r=0; r<=10; r++) {
			for (int c=0; c<=10; c++) {
				if (buff.length() > 0) {
					buff.append(";");
				}
				
				buff.append(tileDataArray[r][c].toString());
			}
		}
		
		return buff.toString();
	}
	
	
	public void moveUnit(int col1, int row1, int col2, int row2) throws MessageException {

		int deltaCol = col2 - col1;
		if (deltaCol < 0) {
			deltaCol = deltaCol * -1;
		}
		
		int deltaRow = row2 - row1;
		if (deltaRow < 0) {
			deltaRow = deltaRow * -1;
		}
		
		int deltaPoint = deltaCol + deltaRow;

		if (deltaPoint == 0) {
			throw new MessageException("자기자신을 찍어서 뭐 어쩌겠단건지?");
		}
		
		TileData tile1 = tileDataArray[col1][row1];
		TileData tile2 = tileDataArray[col2][row2];
		
		if (!tile1.isCanMove() || !tile2.isCanMove()) {
			throw new MessageException("이동할 수 없는 타일입니다.");
		}
		
		if (tile1.getGamerIndex() == -1) {
			throw new MessageException("타일은 제어할 수 없습니다.");
		}
		
		if (tile1.getGamerIndex() != turnDataList.getCurrentTurnIndex()) {
			throw new MessageException("남의 캐릭터는 제어할 수 없습니다.");
		}
		
		int gamerIndex1 = tile1.getGamerIndex();
		int gamerIndex2 = tile2.getGamerIndex();
		
		int vivriaCount1 = tile1.getVivriaCount();
		int vivriaCount2 = tile2.getVivriaCount();

		int pointCanMove = 0;
		
		// 번식모드
		boolean breedingMode = false;
				
		// 이동모드
		boolean moveMode = false;
		
		if (vivriaCount2 == 0) {
			moveMode = true;
		}
		
		// (4) 비브리아 크기가 1~3이면 3칸 이동, 4~6은 2칸이동, 7~9는 1칸 이동, 10과 왕은 이동 불가.
		if (vivriaCount1 == 0) {
			throw new MessageException("빈칸은 이동시킬 수 없습니다.");
			
		} else if (1 <= vivriaCount1 && vivriaCount1 <= 3) {
			pointCanMove = 3;
			
		} else if (4 <= vivriaCount1 && vivriaCount1 <= 6) {
			pointCanMove = 2;
			
		} else if (7 <= vivriaCount1 && vivriaCount1 <= 9) {
			pointCanMove = 1;
			
		} else if (10 == vivriaCount1 || tile1.isKingVivria() == true) {
			pointCanMove = 1;
			breedingMode = true;
		}
		
		if (deltaPoint > pointCanMove) {
			if (breedingMode) {
				throw new MessageException("그렇게 멀리 번식할 수 없습니다.");
			} else if (moveMode) {
				throw new MessageException("그렇게 멀리 이동할 수 없습니다.");
			} else {
				throw new MessageException("그렇게 멀리 이동할 수 없습니다.");
			}
		}
		
		// * 목표는 상대방 왕 비브리아를 잡는 것.
		// - (1) 비브리아는 1~10까지의 크기를 가지는데 아군끼리 합체할 수 있다.
		// - (2) 적의 비브리아를 공격하면 숫자가 같거나 크면 흡수하고, 작으면 흡수 당한다.
		// - (3) 비브리아 크기가 10을 초과하면 배가 터져 죽는다.
		// - (4) 비브리아 크기가 1~3이면 3칸 이동, 4~6은 2칸이동, 7~9는 1칸 이동, 10과 왕은 이동 불가.
		// - (5)크기가 10인 비브리아나 왕비브리아는 번식을 할 수 있는데,
		// (5-1) 빈칸에 번식하면 크기가 1인 비브리아가 생기고,
		// (5-2) 아군의 자리에 하면 아군의 크기가 1 커지고,
		// (5-3) 적이 있는 곳에 번식을 하면 무조건 아군이 되고, 적의 크기에서 1을 더한 크기가 된다.
		
		// 번식모드
		if (breedingMode) {
			// (5-1) 빈칸에 번식하면 크기가 1인 비브리아가 생기고,
			if (vivriaCount2 == 0) {
				tile2.setGamerIndex(tile1.getGamerIndex());
				tile2.setVivriaCount(1);
				return;
			}
			
			// 번식했을 때 크기가 10 초과하면 터진다.
			int newCount = vivriaCount2 + 1;
			
			if (newCount > 10) {
				removeVivriaUnit(tile2);
				return;
			}
			
			// (5-2) 아군의 자리에 하면 아군의 크기가 1 커지고,
			// (5-3) 적이 있는 곳에 번식을 하면 무조건 아군이 되고, 적의 크기에서 1을 더한 크기가 된다.
			tile2.setGamerIndex(tile1.getGamerIndex());
			tile2.setVivriaCount(newCount);
			return;
		}
		
		
		// 이동모드
		if (moveMode) {
			// 타일1 값을 타일2에 옮기고, 타일1 지운다.
			tile2.setGamerIndex(gamerIndex1);
			tile2.setVivriaCount(vivriaCount1);
			
			removeVivriaUnit(tile1);
			return;
		}


		// 번식모드도 이동모드도 아니면... 공격모드다.
		int newCount = vivriaCount1 + vivriaCount2;
		if (newCount > 10) {
			// (3) 비브리아 크기가 10을 초과하면 배가 터져 죽는다.
			boolean bKingIsDead = tile2.isKingVivria();
			
			removeVivriaUnit(tile1);
			removeVivriaUnit(tile2);
			
			if (bKingIsDead) {
				int deadKingIndex = gamerIndex2;
				this.doProcessWhenKingIsDead(deadKingIndex);
			}
			
			return;
			
		} else {
			removeVivriaUnit(tile1);
			tile2.setVivriaCount(newCount);
			
			// (1) 비브리아는 1~10까지의 크기를 가지는데 아군끼리 합체할 수 있다.
			if (gamerIndex1 != gamerIndex2) {
				// (2) 적의 비브리아를 공격하면 숫자가 같거나 크면 흡수하고, 작으면 흡수 당한다.
				if (vivriaCount1 >= vivriaCount2) {
					tile2.setGamerIndex(gamerIndex1);
				} else {
					tile2.setGamerIndex(gamerIndex2);
				}
			}
			
			return;
		}
	}
	
	
	private void removeVivriaUnit(TileData tile) {
		tile.setGamerIndex(-1);
		tile.setVivriaCount(0);
		tile.setKingVivria(false);
	}
	
	
	/**
	 * 새 타일 데이터(맵)을 생성한다.
	 * 
	 * @param gamerCount
	 */
	private TileData[][] createTileData(int gamerCount) {
		
		TileData[][] tileDataArray = null;
		
		tileDataArray = new TileData[11][11];
		
		for (int r=0; r<=10; r++) {
			for (int c=0; c<=10; c++) {
				tileDataArray[r][c] = new TileData();
			}
		}

		// 기본값은 2명임.
		
		// 게이머 2명 이상 (공통)
		if (gamerCount >= 2) {
			for (int r=0; r<=3; r++) {
				for (int c=0; c<=3; c++) {
					tileDataArray[r][c].setCanMove(false);
				}
			}
			
			for (int r=7; r<=10; r++) {
				for (int c=0; c<=3; c++) {
					tileDataArray[r][c].setCanMove(false);
				}
			}
			
			for (int r=0; r<=3; r++) {
				for (int c=7; c<=10; c++) {
					tileDataArray[r][c].setCanMove(false);
				}
			}
			
			for (int r=7; r<=10; r++) { 
				for (int c=7; c<=10; c++) {
					tileDataArray[r][c].setCanMove(false);
				}
			}
			
			createTileDataForPlayer1(tileDataArray);
			createTileDataForPlayer2(tileDataArray);
			
		}
		
		// 게이머 3명 이하
		if (gamerCount <= 3) {
			// 아래 영역을 닫는다.
			for (int r=7; r<=10; r++) {
				for (int c=4; c<=6; c++) {
					tileDataArray[r][c].setCanMove(false);
				}
			}
			
		} else {
			createTileDataForPlayer4(tileDataArray);
		}
		
		// 게이머 2명 이하
		if (gamerCount <= 2) {
			// 위 영역을 닫는다.
			for (int r=0; r<=3; r++) {
				for (int c=4; c<=6; c++) {
					tileDataArray[r][c].setCanMove(false);
				}
			}
			
		} else {
			createTileDataForPlayer3(tileDataArray);
		}
		
		return tileDataArray;
	}
	
	
	private void createTileDataForPlayer1(TileData[][] tileDataArray) {
		int playerNumber = 0;
		
		for (int r=4; r<=6; r++) {
			for (int c=0; c<=1; c++) {
				tileDataArray[r][c].setGamerIndex(playerNumber);
				tileDataArray[r][c].setVivriaCount(1);
			}
		}
		
		tileDataArray[5][0].setGamerIndex(playerNumber);
		tileDataArray[5][0].setVivriaCount(10);
		tileDataArray[5][0].setKingVivria(true);
	}
	
	
	private void createTileDataForPlayer2(TileData[][] tileDataArray) {
		int playerNumber = 1;
		
		for (int r=4; r<=6; r++) {
			for (int c=9; c<=10; c++) {
				tileDataArray[r][c].setGamerIndex(playerNumber);
				tileDataArray[r][c].setVivriaCount(1);
			}
		}
		
		tileDataArray[5][10].setGamerIndex(playerNumber);
		tileDataArray[5][10].setVivriaCount(10);
		tileDataArray[5][10].setKingVivria(true);
	}
	
	
	private void createTileDataForPlayer3(TileData[][] tileDataArray) {
		int playerNumber = 2;
		
		for (int r=0; r<=1; r++) {
			for (int c=4; c<=6; c++) {
				tileDataArray[r][c].setGamerIndex(playerNumber);
				tileDataArray[r][c].setVivriaCount(1);
			}
		}
		
		tileDataArray[0][5].setGamerIndex(playerNumber);
		tileDataArray[0][5].setVivriaCount(10);
		tileDataArray[0][5].setKingVivria(true);
	}
	
	
	private void createTileDataForPlayer4(TileData[][] tileDataArray) {
		int playerNumber = 3;
		
		for (int r=9; r<=10; r++) {
			for (int c=4; c<=6; c++) {
				tileDataArray[r][c].setGamerIndex(playerNumber);
				tileDataArray[r][c].setVivriaCount(1);
			}
		}
		
		tileDataArray[10][5].setGamerIndex(playerNumber);
		tileDataArray[10][5].setVivriaCount(10);
		tileDataArray[10][5].setKingVivria(true);
	}
	
	
	/**
	 * 모든 게이머(방장 포함)의 아이디를 수집해서 StringList로 만든다.
	 * 
	 * @param session
	 * @return
	 */
	private TurnDataList createGamerIdList(Session session) throws MessageException, Exception {
		
		UserSessionList userSessionList = GameServiceUtil.getUserSessionListBySession(session);
		if (userSessionList == null) {
			return null;
		}

		int sessionCount = userSessionList.size();
		if (sessionCount < 1) {
			return null;
		}

		TurnDataList resultList = new TurnDataList();
		
		UserSession singleUserSession = null;
		Session singleSession = null;

		for (int i = 0; i < sessionCount; i++) {
			singleSession = userSessionList.getOriginSession(i);
			if (singleSession == null) {
				continue;
			}

			if (!singleSession.isOpen()) {
				continue;
			}

			singleUserSession = userSessionList.get(i);
			if (singleUserSession == null) {
				continue;
			}

			if (singleUserSession.isRoomChief() == true || singleUserSession.getUserType() == USER_TYPE_GAMER) {
				String singleSessionId = singleSession.getId();
				if (singleSessionId != null && singleSessionId.length() > 0) {
					
					TurnData turnData = new TurnData();
					turnData.setSessionId(singleSessionId);
					turnData.setUserNickName(singleUserSession.getUserNickName());
					
					resultList.add(turnData);
				}
				continue;
			}
		}

		return resultList;
	}
	
	
	public String getUserListString(Session session) throws MessageException, Exception {
		
		UserSessionList userSessionList = GameServiceUtil.getUserSessionListBySession(session);
		if (userSessionList == null) {
			return null;
		}

		int sessionCount = userSessionList.size();
		if (sessionCount < 1) {
			return null;
		}

		StringBuffer resultBuff = new StringBuffer();
		
		UserSession singleUserSession = null;
		Session singleSession = null;

		for (int i = 0; i < sessionCount; i++) {
			singleSession = userSessionList.getOriginSession(i);
			if (singleSession == null) {
				continue;
			}

			if (!singleSession.isOpen()) {
				continue;
			}
			
			singleUserSession = userSessionList.get(i);
			if (singleUserSession == null) {
				continue;
			}
			
			resultBuff.append(singleUserSession.getUserNickName());

			if (singleUserSession.isRoomChief()) {
				resultBuff.append("(방장)");
				
			} else if (singleUserSession.getUserType() == USER_TYPE_GAMER) {
				// resultBuff.append("(참가)");
				
			} else if (singleUserSession.getUserType() == USER_TYPE_OBSERVER) {
				resultBuff.append("(관전)");
			}
			
			resultBuff.append(";");
		}

		return resultBuff.toString();
	}
	
	
	
	public boolean checkSessionIsTurnNow(Session session) throws MessageException, Exception {
		if (turnDataList == null || turnDataList.size() == 0) {
			return false;
		}
		
		int currentTurnIndex = turnDataList.getCurrentTurnIndex();
		if (currentTurnIndex < 0) {
			return false;
		}
		
		String gamerId = turnDataList.get(currentTurnIndex).getSessionId();
		
		if (gamerId.equals(session.getId())) {
			return true;
		}
		
		return false;
	}
	
	
	public int getCurrentTurnIndex() {
		if (turnDataList == null || turnDataList.size() == 0) {
			return -1;
		}
		
		int currentTurnIndex = turnDataList.getCurrentTurnIndex();
		if (currentTurnIndex < 0) {
			return -1;
		}
		
		return currentTurnIndex;
	}
	
	
	public String getCurrentTurnUserName() {
		if (turnDataList == null || turnDataList.size() == 0) {
			return null;
		}
		
		int currentTurnIndex = turnDataList.getCurrentTurnIndex();
		if (currentTurnIndex < 0) {
			return null;
		}
		
		return turnDataList.get(currentTurnIndex).getUserNickName();
	}
	
	
	public TurnData getCurrentTurn() {
		if (turnDataList == null || turnDataList.size() == 0) {
			return null;
		}
		
		int currentTurnIndex = turnDataList.getCurrentTurnIndex();
		if (currentTurnIndex < 0) {
			return null;
		}
		
		return turnDataList.get(currentTurnIndex);
	}
	
	
	public void doProcessWhenKingIsDead(int kingIndex) {
		if (turnDataList == null || turnDataList.size() == 0) {
			return;
		}
		
		String tempDefeatUserName = turnDataList.doProcessWhenKingIsDead(kingIndex);
		if (tempDefeatUserName != null && tempDefeatUserName.length() > 0) {
			defeatUserName = tempDefeatUserName;
		}
	}
	
	
	public String getVictoryUserName() throws MessageException, Exception {
		if (turnDataList == null || turnDataList.size() == 0) {
			return null;
		}
		
		return turnDataList.getVictoryUserName();
	}
	
	
	public TurnData getTurnData(String sessionId) {
		if (turnDataList == null || turnDataList.size() == 0) {
			return null;
		}
		
		return turnDataList.getTurnData(sessionId);
	}
}