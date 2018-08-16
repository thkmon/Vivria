package com.bb.vivria.util;

import javax.websocket.Session;

import com.bb.vivria.socket.data.RoomIdAndUserSessionListMap;
import com.bb.vivria.socket.data.StringMap;
import com.bb.vivria.socket.data.UserSession;
import com.bb.vivria.socket.data.UserSessionList;

public class GameServiceUtil {
	
	private static StringMap sessionIdAndRoomIdMap = new StringMap();
	private static RoomIdAndUserSessionListMap roomIdAndUserSessionListMap = new RoomIdAndUserSessionListMap();
	
	
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
		
		if (roomIdAndUserSessionListMap.get(roomId) == null) {
			roomIdAndUserSessionListMap.put(roomId, new UserSessionList());
		}
		
		UserSession userSession = new UserSession(session);
		roomIdAndUserSessionListMap.get(roomId).add(userSession);
		
		return userSession;
	}
	
	
	public static UserSessionList getUserSessionListByRoomId(String roomId) {
		return roomIdAndUserSessionListMap.get(roomId);
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
	
	
	
}
