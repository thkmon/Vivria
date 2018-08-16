package com.bb.vivria.socket.controller;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.bb.vivria.service.GameService;
import com.bb.vivria.socket.util.UserSessionUtil;

@ServerEndpoint("/websocket")
public class WebsocketController {
	
	/**
	 * 웹소켓 사용자 연결 성립하는 경우 호출
	 */
	@OnOpen
	public void handleOpen(Session session) {
		new GameService().handleConnection(session);
		
//		if (session != null) {
//			String sessionId = session.getId();
//			
//			System.out.println("client is connected. sessionId == [" + sessionId + "]");
//			UserSessionUtil.userSessionList.add(session);
//			
//			// 웹소켓 연결 성립되어 있는 모든 사용자에게 메시지 전송
//			sendMessageToAll("***** [USER-" + sessionId + "] is connected. *****");
//		}
	}
	

	/**
	 * 웹소켓 메시지(From Client) 수신하는 경우 호출
	 */
	@OnMessage
	public String handleMessage(String message, Session session) {
		new GameService().handleClientMessage(message, session);
		return null;
		
//		if (message == null) {
//			message = "";
//		}
//		
//		if (session == null) {
//			return null;
//		}
//		
//		if (message.startsWith("user_nick_name")) {
//			
//		}
//		
//		String sessionId = session.getId();
//		System.out.println("message is arrived. sessionId == [" + sessionId + "] / message == [" + message + "]");
//
//		// 웹소켓 연결 성립되어 있는 모든 사용자에게 메시지 전송
//		sendMessageToAll("[USER-" + sessionId + "] " + message);
//		return null;
	}
	

	/**
	 * 웹소켓 사용자 연결 해제하는 경우 호출
	 */
	@OnClose
	public void handleClose(Session session) {
		new GameService().handleDisconnection(session);
//		if (session != null) {
//			String sessionId = session.getId();
//			System.out.println("client is disconnected. sessionId == [" + sessionId + "]");
//			
//			// 웹소켓 연결 성립되어 있는 모든 사용자에게 메시지 전송
//			sendMessageToAll("***** [USER-" + sessionId + "] is disconnected. *****");
//		}
	}

	
	/**
	 * 웹소켓 에러 발생하는 경우 호출
	 */
	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace();
	}
	
	
//	/**
//	 * 웹소켓 연결 성립되어 있는 모든 사용자에게 메시지 전송
//	 */
//	private boolean sendMessageToAll(String message) {
//		if (UserSessionUtil.userSessionList == null) {
//			return false;
//		}
//
//		int sessionCount = UserSessionUtil.userSessionList.size();
//		if (sessionCount < 1) {
//			return false;
//		}
//
//		Session singleSession = null;
//
//		for (int i = 0; i < sessionCount; i++) {
//			singleSession = UserSessionUtil.userSessionList.getSession(i);
//			if (singleSession == null) {
//				continue;
//			}
//
//			if (!singleSession.isOpen()) {
//				continue;
//			}
//
//			UserSessionUtil.userSessionList.getSession(i).getAsyncRemote().sendText(message);
//		}
//
//		return true;
//	}
}
