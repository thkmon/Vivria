package com.bb.vivria.socket.controller;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.bb.vivria.data.MessageException;
import com.bb.vivria.service.GameService;

@ServerEndpoint("/websocket")
public class WebsocketController {
	
	/**
	 * 웹소켓 사용자 연결 성립하는 경우 호출
	 */
	@OnOpen
	public void handleOpen(Session session) {
		new GameService().handleConnection(session);
	}
	

	/**
	 * 웹소켓 메시지(From Client) 수신하는 경우 호출
	 */
	@OnMessage
	public String handleMessage(String message, Session session) {
		
		GameService gameService = null;
		
		try {
			gameService = new GameService();
			gameService.handleClientMessage(message, session);
		
		} catch (MessageException e) {
			gameService.sendMessageToOne(session, "MESSAGE|" + e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
			gameService.sendMessageToOne(session, "MESSAGE|" + e.getMessage());
		}
		
		return null;
	}
	

	/**
	 * 웹소켓 사용자 연결 해제하는 경우 호출
	 */
	@OnClose
	public void handleClose(Session session) {
		
		GameService gameService = null;
		
		try {
			gameService = new GameService();
			gameService.handleDisconnection(session);
		
		} catch (MessageException e) {
			gameService.sendMessageToOne(session, "MESSAGE|" + e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
			gameService.sendMessageToOne(session, "MESSAGE|" + e.getMessage());
		}
	}

	
	/**
	 * 웹소켓 에러 발생하는 경우 호출
	 */
	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace();
	}
}
