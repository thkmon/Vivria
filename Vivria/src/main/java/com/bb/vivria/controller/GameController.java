package com.bb.vivria.controller;

import java.net.URLDecoder;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bb.vivria.data.RoomData;
import com.bb.vivria.util.GameServiceUtil;
import com.bb.vivria.util.StringUtil;

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
	
	
	/**
	 * 새로운 방 생성하기
	 * 
	 * @param request
	 * @param response
	 * @param locale
	 * @param model
	 * @return
	 */
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
			
			// 방 생성
			String roomName = request.getParameter("roomName");
				
			if (roomName == null || roomName.length() == 0) {
				return "game/vivria/wrong_access";
			} else {
				roomName = URLDecoder.decode(roomName, "UTF-8");
			}
			
			RoomData roomData = GameServiceUtil.makeNewRoom(roomName);
			String roomId = roomData.getRoomId();
			
			// 세션에 방 정보 세팅하기
			boolean isSet = setRoomInfoToSession(request, roomData, userNickName, userType);
			if (!isSet) {
				return "game/vivria/wrong_access";
			}
			
			// 새로 생성한 방으로 이동 (브라우저 주소값을 교체하면서 gameVivriaRoomById 메서드가 실행됨) 
			return "redirect:/game/vivria/room/" + roomId;
			
		} catch (Exception e) {
			e.printStackTrace();
			return "game/vivria/wrong_access";
		}
	}
	
	
	/**
	 * 기존 방에 접속하기
	 * 
	 * @param request
	 * @param response
	 * @param locale
	 * @param model
	 * @param roomId
	 * @return
	 */
	@RequestMapping(value = "/game/vivria/room/{roomId}", method = {RequestMethod.GET, RequestMethod.POST})
	public String gameVivriaRoomById(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model, @PathVariable String roomId) {

		try {
			String userNickName = request.getParameter("userNickName");
			String userType = request.getParameter("userType");
			
			if (StringUtil.isEmpty(userNickName) && StringUtil.isEmpty(userType)) {
				// 파라미터가 없는 경우 (1) 새로 생성한 방으로 이동 (2) 새로고침한 케이스
				String sessinRoomId = StringUtil.parseString(request.getSession().getAttribute("roomId"));
				if (sessinRoomId.equals(roomId)) {
					userNickName = StringUtil.parseString(request.getSession().getAttribute("userNickName"));
					userType = StringUtil.parseString(request.getSession().getAttribute("userType"));
				}
			}
			
			if (userNickName == null || userNickName.length() == 0) {
				return "game/vivria/wrong_access";
			} else {
				userNickName = URLDecoder.decode(userNickName, "UTF-8");
			}
			
			if (userType == null || userType.length() == 0) {
				return "game/vivria/wrong_access";
			}
			
			// 기존 방에 접속
			RoomData roomData = GameServiceUtil.getRoomData(roomId);
			if (roomData == null) {
				// throw new MessageException("존재하지 않는 방입니다.");
				return "game/vivria/wrong_access";
			}
			
			if (roomData.isbClosed()) {
				// throw new MessageException("종료된 방입니다.");
				return "game/vivria/wrong_access";
			}
			
			// 세션에 방 정보 세팅하기
			boolean isSet = setRoomInfoToSession(request, roomData, userNickName, userType);
			if (!isSet) {
				return "game/vivria/wrong_access";
			}
			
			String roomName = StringUtil.nullToEmpty(roomData.getRoomName());
			
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
	
	
	/**
	 * 세션에 방 정보 세팅하기
	 * 
	 * @param request
	 * @param roomData
	 * @param userNickName
	 * @param userType
	 * @return
	 */
	private boolean setRoomInfoToSession(HttpServletRequest request, RoomData roomData, String userNickName, String userType) {
		if (roomData == null) {
			return false;
		}
		
		String roomId = roomData.getRoomId();
		if (StringUtil.isEmpty(roomId)) {
			return false;
		}
		
		String roomName = StringUtil.nullToEmpty(roomData.getRoomName());
		
		request.getSession().setAttribute("roomId", roomId);
		request.getSession().setAttribute("roomName", roomName);
		request.getSession().setAttribute("userNickName", userNickName);
		request.getSession().setAttribute("userType", userType);
		
		return true;
	}
}