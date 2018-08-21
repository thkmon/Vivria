package com.bb.vivria.util;

import javax.websocket.Session;

import com.bb.vivria.common.GameConst;
import com.bb.vivria.data.MessageException;
import com.bb.vivria.data.RoomData;
import com.bb.vivria.data.RoomDataList;
import com.bb.vivria.socket.data.RoomDataMap;
import com.bb.vivria.socket.data.StringMap;
import com.bb.vivria.socket.data.UserSession;
import com.bb.vivria.socket.data.UserSessionList;

public class GameServiceUtil implements GameConst {
	
	private static RoomDataList roomDataList = new RoomDataList();
	
	private static StringMap sessionIdAndRoomIdMap = new StringMap();
	private static RoomDataMap roomDataMap = new RoomDataMap();
	
	
	public static RoomDataList getRoomDataList() {
		return roomDataList;
	}


	private static void setRoomDataList(RoomDataList roomDataList) {
		GameServiceUtil.roomDataList = roomDataList;
	}

	
	public static synchronized RoomData makeNewRoom(String roomName) {
		String roomId = DateUtil.getTodayDateTime();
		
		RoomData roomData = new RoomData();
		roomData.setRoomId(roomId);
		roomData.setRoomName(roomName);
		
		// 방을 생성한다.
		roomDataMap.put(roomId, roomData);
		roomDataList.add(roomData);
		
		return roomData;
	}
	
	
	public static RoomData getRoomData(String roomId) {
		if (roomId == null || roomId.length() == 0) {
			return null;
			
		}
		
		RoomData roomData = roomDataMap.get(roomId);
		if (roomData == null) {
			return null;
		}
		
		if (roomData.isbClosed()) {
			return null;
		}
		
		return roomData;
	}

	public static synchronized UserSession addUserSessionList(String roomId, Session session) throws MessageException, Exception {
		if (session == null) {
			return null;
		}
		
		if (roomId == null || roomId.length() == 0) {
			return null;
		}
		
		String sessionId = session.getId();
		
		if (sessionIdAndRoomIdMap.get(sessionId) == null) {
			sessionIdAndRoomIdMap.put(sessionId, roomId);
		}
		
		// 기존에 방이 없으면 오류낸다.
		if (roomDataMap.get(roomId) == null) {
			throw new MessageException("존재하지 않는 방입니다.");
		}
		
		UserSession userSession = new UserSession(session);
		roomDataMap.get(roomId).addUserSession(userSession);
		
		return userSession;
	}
	
	
	public static UserSessionList getUserSessionListByRoomId(String roomId) {
		return roomDataMap.get(roomId).getUserSessionList();
	}
	
	
	public static UserSessionList getUserSessionListBySession(Session session) {
		String sessionId = session.getId();
		String roomId = sessionIdAndRoomIdMap.get(sessionId);
		return getUserSessionListByRoomId(roomId);
	}
	
	
	/**
	 * 게임방 파괴한다.
	 * 
	 * @param session
	 */
	public static void destoryGameRoom(Session session) {
		if (session == null) {
			return;
		}
		
		String sessionId = session.getId();
		String roomId = sessionIdAndRoomIdMap.get(sessionId);

		// 방을 파괴한다.
		sessionIdAndRoomIdMap.put(sessionId, null);
		roomDataMap.put(roomId, null);
		
		if (roomDataList != null) {
			RoomData roomData = null;
			
			int count = roomDataList.size();
			int lastIndex = count - 1;
			for (int i=lastIndex; i>=0; i--) {
				roomData = roomDataList.get(i);
				
				if (roomData == null) {
					continue;
				}
				
				if (roomData.getRoomId() != null && roomData.getRoomId().equals(roomId)) {
					roomDataList.remove(i);
					break;
				}
			}
		}
	}
	
	
	public static UserSession getUserSession(Session session) {
		if (session == null) {
			return null;
		}
		
		String sessionId = session.getId();
		String roomId = sessionIdAndRoomIdMap.get(sessionId);
		if (roomId == null || roomId.length() == 0) {
			return null;
		}
		
		UserSessionList userSessionList = getUserSessionListByRoomId(roomId);
		if (userSessionList == null) {
			return null;
		}
		
		return userSessionList.getUserSession(sessionId);
	}
	
	
	public static RoomData getRoomData(Session session) {
		if (session == null) {
			return null;
		}
		
		String sessionId = session.getId();
		String roomId = sessionIdAndRoomIdMap.get(sessionId);
		if (roomId == null || roomId.length() == 0) {
			return null;
		}
		
		return roomDataMap.get(roomId);
	}
	
	
	public static boolean checkSessionIsTurnNow(Session session) {
		if (session == null) {
			return false;
		}
		
		RoomData roomData = getRoomData(session); 
		
		boolean bTurnNow = roomData.checkSessionIsTurnNow(session);
		return bTurnNow;
	}
	
	
	public static String getUserListString(Session session) {
		if (session == null) {
			return "";
		}
		
		RoomData roomData = getRoomData(session); 
		
		String userListString = roomData.getUserListString(session);
		return userListString;
	}
	
}
