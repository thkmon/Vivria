package com.bb.vivria.service;

import javax.websocket.Session;

import com.bb.vivria.common.GameConst;
import com.bb.vivria.data.MessageException;
import com.bb.vivria.data.RoomData;
import com.bb.vivria.socket.data.UserSession;
import com.bb.vivria.socket.data.UserSessionList;
import com.bb.vivria.util.GameServiceUtil;
import com.bb.vivria.util.StringUtil;

public class GameService implements GameConst {
	
	
	/**
	 * 클라이언트 연결 성립시 호출되는 이벤트
	 */
	public void handleConnection(Session session) {
		if (session == null) {
			return;
		}
		
		String sessionId = session.getId();
		System.out.println("client is connected. sessionId == [" + sessionId + "]");
	}
	
	
	/**
	 * 클라이언트 연결 해제시 호출되는 이벤트
	 */
	public void handleDisconnection(Session session) {
		if (session == null) {
			return;
		}
		
		String sessionId = session.getId();
		System.out.println("client is disconnected. sessionId == [" + sessionId + "]");
		
		UserSession userSession = GameServiceUtil.getUserSession(session);
		if (userSession == null) {
			return;
		}
		
		// 웹소켓 연결 성립되어 있는 모든 사용자에게 메시지 전송
		String msg = "CHAT|***** [" + userSession.getUserNickName() + "] 님의 접속이 해제되었습니다. *****";
		sendMessageToAll(session, msg);
		return;
	}
	
	
	/**
	 * 클라이언트로부터 메시지를 받았을 때 호출되는 이벤트 
	 */
	public void handleClientMessage(String message, Session session) {
		if (session == null) {
			return;
		}
		
		if (message == null || message.length() == 0) {
			return;
		}
		
		String sessionId = session.getId();
		System.out.println("message is arrived. sessionId == [" + sessionId + "] / message == [" + message + "]");
		
		String messageKey = "";
		String messageValue = "";
		int pipeIndex = message.indexOf("|");
		if (pipeIndex > -1) {
			messageKey = message.substring(0, pipeIndex);
			messageValue = message.substring(pipeIndex + 1);
		}
		
		if (messageKey == null || messageKey.length() == 0) {
			return;
		}
		
		if (messageValue == null) {
			messageValue = "";
		}
		
		// 채팅 (언제나)
		if (messageKey.equals("CHAT")) {
			handleChat(messageValue, session);
			return;
		}
		
		// 방 아이디 : 최초접속시 (1)
		if (messageKey.equals("ROOM_ID")) {
			handleRoomId(messageValue, session);
			return;
		}
		
		// 유저 닉네임 : 최초접속시 (2)
		if (messageKey.equals("USER_NICK_NAME")) {
			handleUserNickName(messageValue, session);
			return;
		}
		
		// 유저 타입 : 최초접속시 (3)
		if (messageKey.equals("USER_TYPE")) {
			handleUserType(messageValue, session);
			return;
		}
		
		// 방장의 게임시작
		if (messageKey.equals("START_GAME")) {
			handleStartGame(messageValue, session);
			return;
		}
		
		// 방 참가자들의 게임 준비
		if (messageKey.equals("READY_TO_GAME")) {
			handleReadyToGame(messageValue, session);
			return;
		}
		
		// 유닛이동
		if (messageKey.equals("MOVE_UNIT")) {
			handleMoveUnit(messageValue, session);
			return;
		}
	}
	
	
	/**
	 * 웹소켓 연결 성립되어 있는 모든 사용자에게 메시지 전송
	 */
	private boolean sendMessageToAll(Session session, String message) {
		
		UserSessionList userSessionList = GameServiceUtil.getUserSessionListBySession(session);
		if (userSessionList == null) {
			return false;
		}

		int sessionCount = userSessionList.size();
		if (sessionCount < 1) {
			return false;
		}

		Session singleSession = null;

		for (int i = 0; i < sessionCount; i++) {
			singleSession = userSessionList.getOriginSession(i);
			if (singleSession == null) {
				continue;
			}

			if (!singleSession.isOpen()) {
				continue;
			}

			singleSession.getAsyncRemote().sendText(message);
		}

		return true;
	}
	
	
	private boolean sendMessageToOne(Session singleSession, String message) {
		
		if (singleSession == null) {
			return false;
		}

		if (!singleSession.isOpen()) {
			return false;
		}

		singleSession.getAsyncRemote().sendText(message);
		return true;
	}
	
	
	/**
	 * 유저 닉네임이 이미 사용중인지 체크한다.
	 */
	private boolean checkNickNameIsDulicated(Session session, String newNickName) {
		
		UserSessionList userSessionList = GameServiceUtil.getUserSessionListBySession(session);
		if (userSessionList == null) {
			return false;
		}

		int sessionCount = userSessionList.size();
		if (sessionCount < 1) {
			return false;
		}

		Session singleSession = null;

		for (int i = 0; i < sessionCount; i++) {
			singleSession = userSessionList.getOriginSession(i);
			if (singleSession == null) {
				continue;
			}

			if (!singleSession.isOpen()) {
				continue;
			}

			if (newNickName.equals(userSessionList.get(i).getUserNickName())) {
				return true;
			}
		}

		return false;
	}
	
	
	/**
	 * 유저 타입이 게이머(==1)인 유저의 인원수를 구한다.
	 * 
	 * @param session
	 * @return
	 */
	public int getCountOfUserTypeGamer(Session session) {
		int iResult = 0;
		
		UserSessionList userSessionList = GameServiceUtil.getUserSessionListBySession(session);
		if (userSessionList == null) {
			return 0;
		}

		int sessionCount = userSessionList.size();
		if (sessionCount < 1) {
			return 0;
		}

		Session singleSession = null;

		for (int i = 0; i < sessionCount; i++) {
			singleSession = userSessionList.getOriginSession(i);
			if (singleSession == null) {
				continue;
			}

			if (!singleSession.isOpen()) {
				continue;
			}

			if (userSessionList.get(i).getUserType() == USER_TYPE_GAMER) {
				iResult++;
			}
		}

		return iResult;
	}
	
	
	/**
	 * 모든 방 참가자들이 준비 상태인지 확인한다.
	 * 
	 * @param session
	 * @return
	 */
	private boolean checkAllGamersAreReadyToGame(Session session) {
		UserSessionList userSessionList = GameServiceUtil.getUserSessionListBySession(session);
		if (userSessionList == null) {
			return false;
		}

		int sessionCount = userSessionList.size();
		if (sessionCount < 1) {
			return false;
		}

		int gamerCount = 0;
		
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
			
			// 게이머 한정
			if (singleUserSession.getUserType() == USER_TYPE_GAMER) {
				// 방장
				if (singleUserSession.isRoomChief()) {
					gamerCount++;
					continue;
				}
				
				// 방 참가자
				if (singleUserSession.isReadyToGame()) {
					gamerCount++;
				} else {
					return false;
				}
				
			}
		}

		if (gamerCount > 1) {
			return true;
		}
		
		return false;
	}
	
	
	// 채팅 (언제나)
	private void handleChat(String messageValue, Session session) {
		String chatContent = messageValue;
		UserSession userSession = GameServiceUtil.getUserSession(session);
		if (userSession == null) {
			return;
		}
		
		String msg = "CHAT|[" + userSession.getUserNickName() + "] " + chatContent;
		sendMessageToAll(session, msg);
	}
	

	// 방 아이디 : 최초접속시 (1)
	private void handleRoomId(String messageValue, Session session) {
		String roomId = messageValue;
		if (roomId == null || roomId.length() == 0) {
			return;
		}
		
		GameServiceUtil.addUserSessionList(roomId, session);
		return;		
	}
	
	
	// 유저 닉네임 : 최초접속시 (2)
	private void handleUserNickName(String messageValue, Session session) {
		UserSession userSession = GameServiceUtil.getUserSession(session);
		if (userSession == null) {
			return;
		}
		
		String newNickName = messageValue;
		if (newNickName == null || newNickName.length() == 0) {
			return;
		}
		
		int loopCount = 0;
		
		// 닉네임 중복일 경우 뒤에 1 을 붙이기
		while (checkNickNameIsDulicated(session, newNickName)) {
			newNickName = newNickName + "1";
			
			loopCount++;
			if (loopCount > 10) {
				newNickName = newNickName + "99";
				break;
			}
		}
		
		userSession.setUserNickName(newNickName);
	}
	
	
	// 유저 타입 : 최초접속시 (3)
	private void handleUserType(String messageValue, Session session) {
		UserSession userSession = GameServiceUtil.getUserSession(session);
		if (userSession == null) {
			return;
		}
		
		if (messageValue == null || messageValue.length() == 0) {
			return;
		}
		
		// userType == 1 : 게이머
		// userType == 2 : 옵저버(관전모드)
		int newUserType = 1;
		if (messageValue.equals("1") || messageValue.equals("2")) {
			newUserType = Integer.parseInt(messageValue);
		} else {
			return;
		}
		
		// 게이머 모드가 1명도 없으면 게이머 모드로 강제 전환(방장이기 때문)
		// 게이머 모드가 4명 이상일 경우 관전 모드로 강제 전환
		int gamerCount = getCountOfUserTypeGamer(session);
		if (gamerCount < 1) {
			// 게이머 모드
			newUserType = 1;
			
			// 방장으로 설정
			userSession.setRoomChief(true);
			
			// 방장은 언제나 게임 준비상태임
			userSession.setReadyToGame(true);
			
		} else if (gamerCount >= 4) {
			// 관전 모드
			newUserType = 2;
		}
		
		userSession.setUserType(newUserType);
		
		String msg = "";
		if (newUserType == USER_TYPE_OBSERVER) {
			msg = "CHAT|***** [" + userSession.getUserNickName() + "] (관전모드) 님이 접속하였습니다. *****";
		} else {
			msg = "CHAT|***** [" + userSession.getUserNickName() + "] 님이 접속하였습니다. *****";
		}

		sendMessageToAll(session, msg);
	}
	
	
	/**
	 * 방장의 게임시작
	 * 
	 * @param messageValue
	 * @param session
	 */
	private void handleStartGame(String messageValue, Session session) {
		UserSession userSession = GameServiceUtil.getUserSession(session);
		if (userSession == null) {
			return;
		}
		
		RoomData roomData = GameServiceUtil.getRoomData(session);
		if (roomData == null) {
			return;
		}
		
		// 방장 외에는 게임 시작할 수 없다.
		if (!userSession.isRoomChief()) {
			return;
		}
		
		// 게임이 이미 시작되었으면 시작할 수 없다.
		if (roomData.isGameIsStarted()) {
			return;
		}
		
		// 게이머
		boolean bAllGamersAreReadyToGame = checkAllGamersAreReadyToGame(session);
		if (!bAllGamersAreReadyToGame) {
			return;
		}
		
		// 게임시작
		roomData.startNewGame(session);
		
		// 클라이언트 화면에 맵 그리기
		drawMap(session);
		
		// 다음턴 지정
		setNextTurn(session);
	}
	
	
	/**
	 * 방 참가자들의 게임 준비
	 * 
	 * @param messageValue
	 * @param session
	 */
	private void handleReadyToGame(String messageValue, Session session) {
		UserSession userSession = GameServiceUtil.getUserSession(session);
		if (userSession == null) {
			return;
		}
		
		RoomData roomData = GameServiceUtil.getRoomData(session);
		if (roomData == null) {
			return;
		}
		
		// 방장은 게임 준비할 수 없다.
		if (userSession.isRoomChief()) {
			return;
		}
		
		// 게임이 이미 시작되었으면 준비할 수 없다.
		if (roomData.isGameIsStarted()) {
			return;
		}
		
		// 게임 준비 상태로 설정. 이미 준비 상태일 경우 준비 해제한다.
		if (!userSession.isReadyToGame()) {
			userSession.setReadyToGame(true);
		} else {
			userSession.setReadyToGame(false);
		}
	}
	
	
	/**
	 * 유닛이동
	 * 
	 * @param messageValue
	 * @param session
	 */
	private void handleMoveUnit(String messageValue, Session session) {
		UserSession userSession = GameServiceUtil.getUserSession(session);
		if (userSession == null) {
			return;
		}
		
		RoomData roomData = GameServiceUtil.getRoomData(session);
		if (roomData == null) {
			return;
		}
		
		// 게임이 아직 시작되지 않았으면 그만둔다.
		if (!roomData.isGameIsStarted()) {
			return;
		}
		
		boolean bTurn = GameServiceUtil.checkSessionIsTurnNow(session);
		if (!bTurn) {
			sendMessageToOne(session, "MESSAGE|" + "현재 턴이 아닙니다.");
			return;
		}
		
		// 유닛이동
		moveUtnit(session, messageValue);
		
		// 클라이언트 화면에 맵 그리기
		drawMap(session);
	}
	
	
	/**
	 * 클라이언트 화면에 맵 그리기
	 * 
	 * @param session
	 */
	public void drawMap(Session session) {
		
		RoomData roomData = GameServiceUtil.getRoomData(session);
		String mapString = roomData.getMapStringForDraw();
		
		sendMessageToAll(session, "DRAW_MAP|" + mapString);
	}
	
	
	/**
	 * 유닛이동
	 * 
	 * @param session
	 */
	public void moveUtnit(Session session, String messageValue) {
		
		// 0,0-1,0
		
		String leftStr = StringUtil.cutLeft(messageValue, "-");
		String rightStr = StringUtil.cutRight(messageValue, "-");
		
		int col1 = StringUtil.parseInt(StringUtil.cutLeft(leftStr, ","));
		int row1 = StringUtil.parseInt(StringUtil.cutRight(leftStr, ","));
		
		int col2 = StringUtil.parseInt(StringUtil.cutLeft(rightStr, ","));
		int row2 = StringUtil.parseInt(StringUtil.cutRight(rightStr, ","));
		
		RoomData roomData = GameServiceUtil.getRoomData(session);
		
		try {
			roomData.moveUnit(col1, row1, col2, row2);
			
		} catch (MessageException e) {
			sendMessageToOne(session, "MESSAGE|" + e.getMessage());
		}
	}
	
	
	/**
	 * 다음턴 지정
	 * 
	 * @param session
	 */
	public void setNextTurn(Session session) {
		
		RoomData roomData = GameServiceUtil.getRoomData(session);
		int nextTurnIndex = roomData.getNextTurnIndex();
		
		sendMessageToAll(session, "SET_TURN|" + nextTurnIndex);
	}
}
