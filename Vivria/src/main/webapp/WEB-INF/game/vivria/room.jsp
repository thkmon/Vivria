<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>웹소켓 테스트 페이지</title>
<script type="text/javascript">
String.prototype.trim = function() {
    return this.replace(/(^\s*)|(\s*$)/g, "");
}

var g_selectMode = false;
var g_selectedTile = "";

var g_webSocket = null;
window.onload = function() {
	
	var userNickName = "";
	var userType = "";
	
	var checkNameAndType = false;
	
	if (checkNameAndType) {
		while (userNickName == null || userNickName.trim().length == 0) {
			userNickName = window.prompt("닉네임을 입력해주세요.", "");
		}
		
		userNickName = userNickName.trim();
		
		
		while (userType != "1" && userType != "2") {
			userType = window.prompt("유저 타입을 입력해주세요. (게이머 == 1, 관전자 == 2)", "");
		}
		
	} else {
		userNickName = "noname";
		userType = "1";
	}
	
	g_webSocket = new WebSocket("ws://localhost:8080/websocket");
	
	
	/**
	 * 웹소켓 사용자 연결 성립하는 경우 호출
	 */
	g_webSocket.onopen = function(message) {
		g_webSocket.send("ROOM_ID|" + "20180817");
		g_webSocket.send("USER_NICK_NAME|" + userNickName);
		
		g_webSocket.send("USER_TYPE|" + userType);

		addLineToChatBox("Server is connected.");
		inputMsgBox.focus();
	};
	
	
	/**
	 * 웹소켓 메시지(From Server) 수신하는 경우 호출
	 */
	g_webSocket.onmessage = function(message) {
		
		if (message == null) {
			return false;
		}
		
		if (message.data == null || message.data.length == 0) {
			return false;
		}
		
		var messageKey = "";
		var messageValue = "";
		
		var messageData = message.data;
		var pipeIndex = messageData.indexOf("|");
		if (pipeIndex > -1) {
			messageKey = messageData.substring(0, pipeIndex);
			messageValue = messageData.substring(pipeIndex + 1);
		} else {
			messageKey = messageData;
			messageValue = "";
		}
		
		if (messageKey == "CHAT") {
			addLineToChatBox(messageValue);
			return;
		}
		
		if (messageKey == "MESSAGE") {
			alert(messageValue);
			return;
		}
		
		if (messageKey == "SET_TYPE_CHIEF") {
			document.getElementById("startGameButton").style.display = "";
			document.getElementById("readyToGameButton").style.display = "none";
			addLineToChatBox(messageValue);
			return;
			
		} else if (messageKey == "SET_TYPE_GAMER") {
			document.getElementById("startGameButton").style.display = "none";
			document.getElementById("readyToGameButton").style.display = "";
			addLineToChatBox(messageValue);
			return;
			
		} else if (messageKey == "SET_TYPE_OBSERVER") {
			document.getElementById("startGameButton").style.display = "none";
			document.getElementById("readyToGameButton").style.display = "none";
			addLineToChatBox(messageValue);
			return;
		}
	
		if (messageKey == "DRAW_MAP") {
			var nextPipeIndex = messageValue.indexOf("|");
			if (nextPipeIndex > -1) {
				var nextTurnNickName = messageValue.substring(0, nextPipeIndex);
				messageValue = messageValue.substring(nextPipeIndex + 1);
				
				addLineToChatBox("***** [" + nextTurnNickName + "]님의 턴입니다. *****");
			}
			
			drawMap(messageValue);
			return;
		}
		
		alert("기타메시지 : " + messageData);
	};


	/**
	 * 웹소켓 사용자 연결 해제하는 경우 호출
	 */
	g_webSocket.onclose = function(message) {
		addLineToChatBox("Server is disconnected.");
	};


	/**
	 * 웹소켓 에러 발생하는 경우 호출
	 */
	g_webSocket.onerror = function(message) {
		addLineToChatBox("Error!");
	};
}


/**
 * 채팅 박스영역에 내용 한 줄 추가
 */
function addLineToChatBox(_line) {
	if (_line == null) {
		_line = "";
	}
	
	var chatBoxArea = document.getElementById("chatBoxArea");
	chatBoxArea.value += _line + "\n";
	chatBoxArea.scrollTop = chatBoxArea.scrollHeight;	
}


/**
 * Send 버튼 클릭하는 경우 호출 (서버로 메시지 전송)
 */
function sendButton_onclick() {
	var inputMsgBox = document.getElementById("inputMsgBox");
	if (inputMsgBox == null || inputMsgBox.value == null || inputMsgBox.value.length == 0) {
		return false;
	}
	
	var chatBoxArea = document.getElementById("chatBoxArea");
	
	if (g_webSocket == null || g_webSocket.readyState == 3) {
		chatBoxArea.value += "Server is disconnected.\n";
		return false;
	}
	
	// 서버로 메시지 전송
	g_webSocket.send("CHAT|" + inputMsgBox.value);
	inputMsgBox.value = "";
	inputMsgBox.focus();
	
	return true;
}


/**
 * Disconnect 버튼 클릭하는 경우 호출
 */
function disconnectButton_onclick() {
	if (g_webSocket != null) {
		g_webSocket.close();	
	}
}


/**
 * inputMsgBox 키입력하는 경우 호출
 */
function inputMsgBox_onkeypress() {
	if (event == null) {
		return false;
	}
	
	// 엔터키 누를 경우 서버로 메시지 전송
	var keyCode = event.keyCode || event.which;
	if (keyCode == 13) {
		sendButton_onclick();
	}
}


function startGameButton_onclick() {
	g_webSocket.send("START_GAME|");
}


function readyToGameButton_onclick() {
	g_webSocket.send("READY_TO_GAME|");
}


function drawMap(_messageData) {
	if (_messageData == null || _messageData.length == 0) {
		return false;
	}
	
	var singleTile = "";
	var tileArray = _messageData.split(";");
	
	var elem = null;
	var index = 0;
	for (var r=0; r<=10; r++) {
		for (var c=0; c<=10; c++) {
			singleTile = tileArray[index];
			
			elem = document.getElementById("tile" + r + "_" + c);
			
			if (singleTile == "XXX") {
				elem.style.backgroundImage = "url('/resources/img/vivria/xtile.png')";
				
			} else if (singleTile == "OOO") {
				elem.style.backgroundImage = "url('/resources/img/vivria/otile.png')";
				
			} else {
				
				var firstChar = singleTile.substring(0, 1);
				var color = "";
				if (firstChar == "R") {
					color = "red";
					
				} else if (firstChar == "Y") {
					color = "yellow";
					
				} else if (firstChar == "B") {
					color = "blue";
					
				} else if (firstChar == "G") {
					color = "green";
				}
				
				var number = "";
				var lastChar = singleTile.substring(2, 3);
				
				if (lastChar == "K") {
					number = "4";
					
				} else if (lastChar == "A") {
					number = "5";
					
				} else {
					var vivriaCount = parseInt(lastChar, 10);
					if (1 <= vivriaCount && vivriaCount <= 3) {
						number = "1";
						
					} else if (4 <= vivriaCount && vivriaCount <= 6) {
						number = "2";
					
					} else if (7 <= vivriaCount && vivriaCount <= 9) {
						number = "3";
					}
				}
				
				elem.style.backgroundImage = "url('/resources/img/vivria/" + color + number + ".png')";
				
			}
			
			index++;
		}
	}
}


function tile_onclick(_row, _col) {
	if (g_selectMode) {
		var elem = document.getElementById("tile" + g_selectedTile);
		elem.style.border = "1px solid #b9b5f9";
		
		if (g_selectedTile == (_row + "_" + _col)) {
			g_selectMode = false;
			return;
		}
		
		// 서버로 메시지 전송
		var moveText = g_selectedTile + "|" + _row + "_" + _col;
		alert(moveText);
		g_webSocket.send("MOVE_UNIT|" + moveText);
		g_selectMode = false;
			
	} else {
		var elem = document.getElementById("tile" + _row + "_" + _col);
		if (elem != null) {
			elem.style.border = "1px solid blue";
			g_selectedTile = _row + "_" + _col;
			g_selectMode = true;
		}
	}
}


// 선택해제
function releaseSelectionButton_onclick() {
	
	var elem = null;
	for (var r=0; r<=10; r++) {
		for (var c=0; c<=10; c++) {
			elem = document.getElementById("tile" + r + "_" + c);
			if (elem != null) {
				elem.style.border = "1px solid #b9b5f9";
			}
		}
	}
	
	g_selectedTile = "";
	g_selectMode = false;
}
</script>
<style type="text/css">
	.tile {
		width: 50px;
		height: 50px;
		border: 1px solid #b9b5f9;
		cursor: pointer;
	}
	
	.tile:hover {
		width: 50px;
		height: 50px;
		border: 1px solid blue;
		cursor: pointer;
	}
	
	.lfloat {
		float: left;
	}
	
	.basic_button {
		cursor: pointer;
	}
</style>
</head>
<body>
	<!-- <input id="disconnectButton" class="basic_button" value="연결끊기" type="button" onclick="disconnectButton_onclick()"> -->
	<input id="readyToGameButton" class="basic_button" value="게임준비" type="button" style="display: none;" onclick="readyToGameButton_onclick()">
	<input id="startGameButton" class="basic_button" value="게임시작" type="button" style="display: none;" onclick="startGameButton_onclick()">
	<input id="releaseSelectionButton" class="basic_button" value="선택해제" type="button" onclick="releaseSelectionButton_onclick()">
	<br>
	<div style="border: 0px solid #000000; width: 600px; height: 600px; margin: 0 auto;">
		<%
			for (int r=0; r<=10; r++) {
				for (int c=0; c<=10; c++) {
					if (c == 0) {
						out.print("<div>");
					}
					
					out.print("<div id=\"tile" + r + "_" + c + "\" class=\"tile lfloat\" onclick=\"tile_onclick(" + r + "," + c + ")\">" + r + "," + c + "</div>");
					
					if (c == 10) {
						out.print("</div>");
					}
				}
			}
		%>
	</div>
	<br>
	<input id="inputMsgBox" style="width: 250px;" type="text" onkeypress="inputMsgBox_onkeypress()">
	<input id="sendButton" class="basic_button" value="전송" type="button" onclick="sendButton_onclick()">
	<br>
	<textarea id="chatBoxArea" style="width: 100%;" rows="10" cols="50" readonly="readonly"></textarea>
</body>
</html>