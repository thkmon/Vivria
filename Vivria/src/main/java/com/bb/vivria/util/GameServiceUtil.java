package com.bb.vivria.util;

import javax.websocket.Session;

import com.bb.vivria.common.GameConst;
import com.bb.vivria.data.RoomData;
import com.bb.vivria.socket.data.RoomDataMap;
import com.bb.vivria.socket.data.StringMap;
import com.bb.vivria.socket.data.UserSession;
import com.bb.vivria.socket.data.UserSessionList;

public class GameServiceUtil implements GameConst {
	
	private static StringMap sessionIdAndRoomIdMap = new StringMap();
	private static RoomDataMap roomDataMap = new RoomDataMap();
	
	
	public static UserSession addUserSessionList(String roomId, Session session) {
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
		
		// 기존에 방이 없으면 방을 생성한다.
		if (roomDataMap.get(roomId) == null) {
			roomDataMap.put(roomId, new RoomData());
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
	
}
