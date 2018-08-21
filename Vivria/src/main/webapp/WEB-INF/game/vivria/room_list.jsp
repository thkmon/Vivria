<%@page import="com.bb.vivria.data.RoomData"%>
<%@page import="com.bb.vivria.data.RoomDataList"%>
<%@page import="com.bb.vivria.util.GameServiceUtil"%>
<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>비브리아 대기실</title>
<script type="text/javascript">
	// 새로고침
	function refreshThisPage() {
// 		location.href = "/game/vivria";
		sendPost("/game/vivria");
	}
	
	
	// 방 개설 / 방 입장
	function enterToRoom(_roomId) {
		
		var roomName = "";
		var userNickName = "";
		var userType = "";
		
		// _roomId 값이 존재할 경우 방 입장.
		// _roomId 값이 존재하지 않을 경우 방 개설.
		if (_roomId == null || _roomId.length == 0) {
			// 방 제목 결정
			roomName = window.prompt("방 제목을 입력해주세요.", "");
			
			if (roomName == null || roomName.trim().length == 0) {
				alert("정확하게 입력해주세요.");
				return false;
				
			} else {
				roomName = roomName.trim();
			}
		}
		
		// 닉네임 결정
		userNickName = window.prompt("닉네임을 입력해주세요.", "");
		
		if (userNickName == null || userNickName.trim().length == 0) {
			alert("정확하게 입력해주세요.");
			return false;
			
		} else {
			userNickName = userNickName.trim();
		}
		
		// 유저 타입 결정
		userType = window.prompt("유저 타입을 입력해주세요. (게이머 == 1, 관전자 == 2)", "");
		
		if (userType != "1" && userType != "2") {
			alert("정확하게 입력해주세요.");
			return false;
		}
		
		
		var keyArray = null;
		var valueArray = null;
		
		// _roomId 값이 존재할 경우 방 입장.
		// _roomId 값이 존재하지 않을 경우 방 개설.
		if (_roomId != null && _roomId.length > 0) {
			keyArray = ["roomId", "userNickName", "userType"];
			valueArray = [_roomId, userNickName, userType];
		} else {
			keyArray = ["roomName", "userNickName", "userType"];
			valueArray = [roomName, userNickName, userType];
		}

		sendPost("/game/vivria/room", keyArray, valueArray);
	}
	
	
	function reviseParamValue(_param) {
		if (_param == null || _param.length == 0) {
			return "";
		}
		
		_param = encodeURIComponent(_param);
		
		return _param;
	}
	
	
	// post 방식으로 페이지 이동
	function sendPost(_url, _keyArray, _valueArray) {
		try {
			var formObj = document.getElementById("post_form");
			if (formObj != null) {
				// 기존 오브젝트 삭제
				iframeObj.parentNode.removeChild(formObj);
			}
			
			formObj = document.createElement("form");
				
			formObj.setAttribute("action", _url);
			formObj.setAttribute("id", "post_form");
			formObj.setAttribute("name", "post_form");
			formObj.setAttribute("method", "post");
			formObj.setAttribute("style", "width: 1px; height: 1px; display: none;");
			
			if (_keyArray != null && _keyArray.length > 0) {
				if (_valueArray != null && _valueArray.length > 0) {
					var len = _keyArray.length;
					for (var i=0; i<len; i++) {
						var inputObj = document.createElement("input");
						inputObj.setAttribute("name", _keyArray[i]);
						inputObj.setAttribute("value", reviseParamValue(_valueArray[i]));
						
						formObj.appendChild(inputObj);
					}
				}
			}
			
			document.getElementsByTagName("body")[0].appendChild(formObj);
			
			formObj.submit();
			
		} catch (e) {
			alert("오류 : " + e);
		}
	}
</script>
<style type="text/css">
	.roomDiv {
		width: 100%;
		max-width: 800px;
		border: 1px solid #000000;
		padding-top: 10px;
		padding-bottom: 10px;
	}
	
	
	.basic_button {
		cursor: pointer;
		height: 32px;
	}
	
	
	.text_link {
		color: blue;
		text-decoration: none;
		cursor: pointer;
	}
	
	
	.text_link:hover {
		color: blue;
		text-decoration: underline;
		cursor: pointer;
	}
</style>
</head>
<body>
	<h1>비브리아 대기실</h1>
	<input type="button" class="basic_button" value="새로고침" onclick="refreshThisPage()">
	<input type="button" class="basic_button" value="방 개설" onclick="enterToRoom()">
	<br><br>
	<div class="roomDiv">
		<%
			RoomDataList roomDataList = GameServiceUtil.getRoomDataList();
			if (roomDataList != null && roomDataList.size() > 0) {
				
				RoomData roomData = null;
				int roomCount = roomDataList.size();
				for (int i=0; i<roomCount; i++) {
					roomData = roomDataList.get(i);
					if (roomData == null) {
						continue;
					}
					
					if (roomData.isbClosed()) {
						continue;
					}
					
					String roomName = roomData.getRoomName();
					if (roomName == null || roomName.length() == 0) {
						continue;
					}
					
					String roomId = roomData.getRoomId();
					if (roomId == null || roomId.length() == 0) {
						continue;
					}
					
					out.print("<h1><span class=\"text_link\" onclick=\"enterToRoom('" + roomId + "')\">" + roomName + "</span></h1>");
				}
			} else {
				out.print("현재 개설된 방 없음.");
			}
		%>
	</div>
	<br>
	<span style="text-decoration: underline;">
</body>
</html>