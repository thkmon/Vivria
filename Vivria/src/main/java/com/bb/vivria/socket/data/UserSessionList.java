package com.bb.vivria.socket.data;

import java.util.ArrayList;

import javax.websocket.Session;

public class UserSessionList extends ArrayList<UserSession> {
	
	public boolean add(Session session) {
		if (session == null) {
			return false;
		}
		
		UserSession userSession = new UserSession(session);
		this.add(userSession);
		return true;
	}
	
	
	
	
	public Session getOriginSession(int index) {
		return this.get(index).getSession();
	}
	
	
	public UserSession getUserSession(String sessionId) {
		if (sessionId == null || sessionId.length() == 0) {
			return null;
		}
		
		UserSession singleUserSession = null;
		Session singleSession = null;
		int count = this.size();
		for (int i=0; i<count; i++) {
			singleUserSession = this.get(i);
			if (singleUserSession == null) {
				continue;
			}
			
			singleSession = singleUserSession.getSession();
			if (singleSession == null) {
				continue;
			}
			
			if (singleSession.getId() == null) {
				continue;
			}
			
			if (singleSession.getId().equals(sessionId)) {
				return singleUserSession;
			}
		}
		
		return null;
	}
}
