package com.bb.vivria.controller;

import java.net.URLDecoder;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bb.vivria.data.RoomData;
import com.bb.vivria.util.GameServiceUtil;

@Controller
public class GameController {
	
	
	@RequestMapping(value = "/game/vivria", method = {RequestMethod.GET, RequestMethod.POST})
	public String gameVivria(Locale locale, Model model) {
		
		return "game/vivria/room_list";
	}
	
	
	@RequestMapping(value = "/game/vivria/room", method = RequestMethod.GET)
	public String gameVivriaRoomByGet(Locale locale, Model model) {
		return "game/vivria/room_list";
	}
	
	
	@RequestMapping(value = "/game/vivria/room", method = RequestMethod.POST)
	public String gameVivriaRoom(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) {

		try {
			String userNickName = request.getParameter("userNickName");
			String userType = request.getParameter("userType");
			
			if (userNickName == null || userNickName.length() == 0) {
				return "game/vivria/wrong_access";
			} else {
				userNickName = URLDecoder.decode(userNickName, "UTF-8");
			}
			
			if (userType == null || userType.length() == 0) {
				return "game/vivria/wrong_access";
			}
			
			String roomId = request.getParameter("roomId");
			String roomName = null;
			
			if (roomId != null && roomId.length() > 0) {
				// 1. 기존 방에 접속
				RoomData roomData = GameServiceUtil.getRoomData(roomId);
				if (roomData == null) {
					// throw new MessageException("존재하지 않는 방입니다.");
					return "game/vivria/wrong_access";
				}
				
				if (roomData.isbClosed()) {
					// throw new MessageException("종료된 방입니다.");
					return "game/vivria/wrong_access";
				}
				
				roomName = roomData.getRoomName();
				
			} else {
				// 2. 방 생성
				roomName = request.getParameter("roomName");
				
				if (roomName == null || roomName.length() == 0) {
					return "game/vivria/wrong_access";
				} else {
					roomName = URLDecoder.decode(roomName, "UTF-8");
				}
				
				RoomData roomData = GameServiceUtil.makeNewRoom(roomName);
				roomId = roomData.getRoomId();
			}
			
			model.addAttribute("roomId", roomId);
			model.addAttribute("roomName", roomName);
			model.addAttribute("userNickName", userNickName);
			model.addAttribute("userType", userType);
			
			return "game/vivria/room";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "game/vivria/wrong_access";
		}
	}
}