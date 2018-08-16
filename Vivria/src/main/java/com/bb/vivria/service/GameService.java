package com.bb.vivria.service;

import javax.websocket.Session;

import com.bb.vivria.socket.data.UserSession;
import com.bb.vivria.socket.data.UserSessionList;
import com.bb.vivria.util.GameServiceUtil;

public class GameService {
	
	
	public void handleConnection(Session session) {
		if (session == null) {
			return;
		}
		
		String sessionId = session.getId();
		System.out.println("client is connected. sessionId == [" + sessionId + "]");
	}
	
	
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
		
		// 채팅 (언제나)
		if (messageKey.equals("CHAT")) {
			String chatContent = messageValue;
			UserSession userSession = GameServiceUtil.getUserSession(session);
			if (userSession == null) {
				return;
			}
			
			String msg = "CHAT|[" + userSession.getUserNickName() + "] " + chatContent;
			sendMessageToAll(session, msg);
			return;
		}
		
		// 방 아이디 (최초접속시)
		if (messageKey.equals("ROOM_ID")) {
			String roomId = messageValue;
			if (roomId == null || roomId.length() == 0) {
				return;
			}
			
			GameServiceUtil.addUserSessionList(roomId, session);
			return;
		}
		
		// 유저 닉네임 (최초접속시)
		if (messageKey.equals("USER_NICK_NAME")) {
			UserSession userSession = GameServiceUtil.getUserSession(session);
			if (userSession == null) {
				return;
			}
			
			String newNickName = messageValue;
			if (newNickName == null || newNickName.length() == 0) {
				return;
			}
			
			// 닉네임 중복일 경우 뒤에 1 을 붙이기
			while (checkNickNameIsDulicated(session, newNickName)) {
				newNickName = newNickName + "1";
			}
			
			userSession.setUserNickName(newNickName);
			return;
		}
		
		// 유저 타입 (최초접속시)
		if (messageKey.equals("USER_TYPE")) {
			UserSession userSession = GameServiceUtil.getUserSession(session);
			if (userSession == null) {
				return;
			}
			
			if (messageValue == null || messageValue.length() == 0) {
				return;
			}
			
			int newUserType = 1;
			if (messageValue.equals("1") || messageValue.equals("2")) {
				newUserType = Integer.parseInt(messageValue);
			} else {
				return;
			}
			
			// 참여 모드가 1명도 없으면 참여 모드로 강제 전환(방장이기 때문)
			// 참여 모드가 4명 이상일 경우 관전 모드로 강제 전환
			int gamerUserCount = getCountOfUserTypeGamer(session);
			if (gamerUserCount < 1) {
				// 참여 모드
				newUserType = 1;
				
			} else if (gamerUserCount >= 4) {
				// 관전 모드
				newUserType = 2;
			}
			
			userSession.setUserType(newUserType);
			
			String msg = "";
			if (newUserType == 2) {
				msg = "CHAT|***** [" + userSession.getUserNickName() + "] (관전모드) 님이 접속하였습니다. *****";
			} else {
				msg = "CHAT|***** [" + userSession.getUserNickName() + "] 님이 접속하였습니다. *****";
			}

			sendMessageToAll(session, msg);
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

			userSessionList.getOriginSession(i).getAsyncRemote().sendText(message);
		}

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
	 * 유저 타입이 게이머(==1)인 유저의 명수를 구한다.
	 */
	private int getCountOfUserTypeGamer(Session session) {
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

			if (userSessionList.get(i).getUserType() == 1) {
				iResult++;
			}
		}

		return iResult;
	}
}
