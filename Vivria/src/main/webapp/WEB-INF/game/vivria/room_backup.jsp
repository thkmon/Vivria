<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>비브리아</title>
<script type="text/javascript">
var g_userNickName = "${userNickName}";
var g_userType = "${userType}";
var g_roomId = "${roomId}";

var g_defeat = false;
var g_victory = false;

var g_gameStart = false;
var g_selectMode = false;
var g_selectedTile = "";
var g_tileArray = null;

var g_webSocket = null;


String.prototype.trim = function() {
    return this.replace(/(^\s*)|(\s*$)/g, "");
}


// 뒤로 가기 방지
function forbidBackCommand() {
	history.pushState(null, null, location.href);
	window.onpopstate = function(event) {	
		history.go(1);
	}
}


// 마우스 우클릭 이벤트 변경 (마우스 우클릭시 타일의 선택해제 실행)
function replaceMouseRightEvent() {
	document.body.oncontextmenu = function() {
		// 마우스 우클릭시 타일의 선택해제 실행
		releaseSelection();
		return false;
	}
}


window.onload = function() {
	
	if (location != null && location.href != null && location.href.indexOf("localhost") > -1) {
		g_webSocket = new WebSocket("ws://localhost:8080/websocket");
	} else {
		g_webSocket = new WebSocket("ws://ddoc.kr:18080/websocket");
	}

	// 뒤로 가기 방지
	forbidBackCommand();
	
	// 마우스 우클릭 이벤트 변경 (마우스 우클릭시 타일의 선택해제 실행)
	replaceMouseRightEvent();
	
	/**
	 * 웹소켓 사용자 연결 성립하는 경우 호출
	 */
	g_webSocket.onopen = function(message) {
		g_webSocket.send("ROOM_ID|" + g_roomId);
		g_webSocket.send("USER_NICK_NAME|" + g_userNickName);
		
		g_webSocket.send("USER_TYPE|" + g_userType);

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
		
		var data = message.data;
		if (data.indexOf("/+/") > -1) {
			var dataArray = data.split("/+/");
			var dataCount = dataArray.length;
			for (var i=0; i<dataCount; i++) {
				handleServerMessage(dataArray[i]);
			}
			
		} else {
			handleServerMessage(data);
		}
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


// 서버로부터 도착한 메시지 처리
function handleServerMessage(_data) {
	var messageKey = "";
	var messageValue = "";
	
	// var messageData = message.data;
	var messageData = _data;
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
		
		// 서버로 메시지 전송
		g_webSocket.send("REFRESH_MAP");
		return;
	}

	if (messageKey == "DRAW_MAP") {
		if (!g_gameStart) {
			document.getElementById("startGameButton").style.display = "none";
			document.getElementById("readyToGameButton").style.display = "none";
			document.getElementById("releaseSelectionButton").style.display = "";
			g_gameStart = true;
		} 
		
		drawMap(messageValue);
		return;
	}
	
	
	if (messageKey == "SET_TURN") {
		addLineToChatBox("***** [" + messageValue + "]님의 턴입니다. *****");
		
		var turnDiv = document.getElementById("turnDiv");
		turnDiv.innerText = "[" + messageValue + "]님의 턴";
		return;
	}
	
	
	if (messageKey == "SET_USERLIST") {
		if (messageValue == null || messageValue.length == 0) {
			return;
		}
		
		var userListArea = document.getElementById("userListArea");
		userListArea.value = "";
		
		if (messageValue.indexOf(";") > -1) {
			var userNameArray = messageValue.split(";");
			var userCount = userNameArray.length;
			for (var i=0; i<userCount; i++) {
				userListArea.value += userNameArray[i] + "\n";		
			}
		} else {
			userListArea.value += messageValue + "\n";
		}
		return;
	}
	
	
	if (messageKey == "DEFEAT") {
		if (g_userNickName != null && messageValue != null && g_userNickName == messageValue) {
			g_defeat = true;
		}
		
		addLineToChatBox("***** [" + messageValue + "]님이 게임에서 패배하였습니다. *****");
		return;
	}
	
	
	if (messageKey == "VICTORY") {
		if (g_userNickName != null && messageValue != null && g_userNickName == messageValue) {
			g_victory = true;
		}
		
		addLineToChatBox("***** [" + messageValue + "]님이 게임에서 승리하였습니다. *****");
		return;
	}
	
	
	alert("기타메시지 : " + messageData);
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
	
	if (g_webSocket == null || g_webSocket.readyState == 3) {
		addLineToChatBox("Server is disconnected.");
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
	g_tileArray = _messageData.split(";");
	
	var elem = null;
	var elem2 = null;
	var index = 0;
	for (var r=0; r<=10; r++) {
		for (var c=0; c<=10; c++) {
			singleTile = g_tileArray[index];
			
			elem = document.getElementById("tile" + r + "_" + c);
			elem2 = document.getElementById("tile_span" + r + "_" + c);
			
			if (singleTile == "XXX") {
				if (g_victory == true) {
					elem.style.backgroundImage = "url('/resources/img/vivria/victory.png')";
				} else if (g_defeat == true) {
					elem.style.backgroundImage = "url('/resources/img/vivria/defeat.png')";
				} else {
					elem.style.backgroundImage = "url('/resources/img/vivria/xtile.png')";
				}
				elem2.innerText = "";
				
			} else if (singleTile == "OOO") {
				elem.style.backgroundImage = "url('/resources/img/vivria/otile.png')";
				elem2.innerText = "";
				
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
				
				var tileText = "";
				var number = "";
				var lastChar = singleTile.substring(2, 3);
				
				if (lastChar == "K") {
					number = "4";
					tileText = "K";
					
				} else if (lastChar == "A") {
					number = "5";
					tileText = "10";
					
				} else {
					var vivriaCount = parseInt(lastChar, 10);
					tileText = vivriaCount + "";
					
					if (1 <= vivriaCount && vivriaCount <= 3) {
						number = "1";
						
					} else if (4 <= vivriaCount && vivriaCount <= 6) {
						number = "2";
					
					} else if (7 <= vivriaCount && vivriaCount <= 9) {
						number = "3";
					}
				}
				
				elem.style.backgroundImage = "url('/resources/img/vivria/" + color + number + ".png')";
				elem2.innerText = tileText;
			}
			
			index++;
		}
	}
}


function tile_onclick(_row, _col) {
	if (g_selectMode) {
		if (g_selectedTile == (_row + "_" + _col)) {
			releaseSelection();
			return;
		}
		
		// 서버로 메시지 전송
		var moveText = g_selectedTile + "|" + _row + "_" + _col;
		// alert(moveText);
		g_webSocket.send("MOVE_UNIT|" + moveText);
		releaseSelection();
			
	} else {
		var elem = document.getElementById("tile" + _row + "_" + _col);
		if (elem != null) {
			showTileMark(_row, _col);
			
			elem.style.border = "1px solid blue";
			g_selectedTile = _row + "_" + _col;
			g_selectMode = true;
		}
	}
}


function showTileMark(_row, _col) {
	
	if (g_tileArray == null) {
		return false;
	}
	
	var index = getTileIndex(_row, _col);
	var singleTile = g_tileArray[index];
	
	if (singleTile == null || singleTile.length == 0) {
		return false;
	}
	
	if (singleTile == "XXX" || singleTile == "OOO") {
		return false;
	}
	
	var point = 0;
	var lastChar = singleTile.substring(2, 3);
	
	// 비브리아 크기가 1~3이면 3칸 이동, 4~6은 2칸이동, 7~9는 1칸 이동, 10과 왕은 이동 불가(1칸 번식)
	if (lastChar == "K" || lastChar == "A") {
		point = 1;
		
	} else {
		var vivriaCount = parseInt(lastChar, 10);
		if (1 <= vivriaCount && vivriaCount <= 3) {
			point = 3;
			
		} else if (4 <= vivriaCount && vivriaCount <= 6) {
			point = 2;
		
		} else if (7 <= vivriaCount && vivriaCount <= 9) {
			point = 1;
		}
	}
	
	var bRow = _row - point;
	var eRow = _row + point; 
	var bCol = _col - point;
	var eCol = _col + point;
	
	if (bRow < 0) {
		bRow = 0;
	}
	
	if (bCol < 0) {
		bCol = 0;
	}
	
	if (eRow > 10) {
		eRow = 10;
	}
	
	if (eCol > 10) {
		eCol = 10;
	}
	
	var elem = null;
	var rGap = 0;
	var cGap = 0;
	for (var r=bRow; r<=eRow; r++) {
		for (var c=bCol; c<=eCol; c++) {
			rGap = _row - r;
			if (rGap < 0) {
				rGap = rGap * -1;
			}
			
			cGap = _col - c;
			if (cGap < 0) {
				cGap = cGap * -1;
			}
			
			if (rGap + cGap > point) {
				continue;
			}
			
			index = getTileIndex(r, c);
			singleTile = g_tileArray[index];
			
			if (singleTile == "XXX") {
				continue;
			}
			
			elem = document.getElementById("tile_cover" + r + "_" + c);
			if (elem != null) {
				elem.style.display = "";
			}
		}
	}
}


function getTileIndex(_row, _col) {
	return (_row * 11) + _col;
}


// 선택해제 버튼 클릭시
function releaseSelectionButton_onclick() {
	releaseSelection();
}


// 타일의 선택해제
function releaseSelection() {
	var elem = null;
	var elem2 = null;
	for (var r=0; r<=10; r++) {
		for (var c=0; c<=10; c++) {
			elem = document.getElementById("tile" + r + "_" + c);
			if (elem != null) {
				elem.style.border = "1px solid #b9b5f9";
			}
			
			elem2 = document.getElementById("tile_cover" + r + "_" + c);
			if (elem2 != null) {
				elem2.style.display = "none";
			}
		}
	}
	
	g_selectedTile = "";
	g_selectMode = false;
}


// 최상단
function scrollUpButton_onclick() {
	var chatBoxArea = document.getElementById("chatBoxArea");
	chatBoxArea.scrollTop = 0;
}


// 최하단
function scrollDownButton_onclick() {
	var chatBoxArea = document.getElementById("chatBoxArea");
	chatBoxArea.scrollTop = chatBoxArea.scrollHeight;
}
</script>
<style type="text/css">
	.tile_cover {
		width: 50px;
		height: 50px;
		border: 1px solid #b9b5f9;
		cursor: pointer;
		background: rgba(0, 0, 255, 0.5);
	}
	
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
		height: 32px;
	}
	
	.turnDiv {
/* 		border: 1px solid #000000; */
	}
</style>
</head>
<body>
	<table style="border: 0px solid #000000; width: 100%; max-width: 1024px; min-width: 600px;">
		<tr>
			<td style="width: 100%;">
				<!-- <input id="disconnectButton" class="basic_button" value="연결끊기" type="button" onclick="disconnectButton_onclick()"> -->
				<input id="readyToGameButton" class="basic_button" value="게임준비" type="button" style="display: none;" onclick="readyToGameButton_onclick()">
				<input id="startGameButton" class="basic_button" value="게임시작" type="button" style="display: none;" onclick="startGameButton_onclick()">
				<input id="releaseSelectionButton" class="basic_button" value="선택해제" type="button" style="display: none;" onclick="releaseSelectionButton_onclick()">
				<br>
				<br>
				<div id="turnDiv" class="turnDiv" style="width: 300px; font-size: 20px;"></div>
				<br>
				<br>
				<div style="border: 0px solid #000000; width: 580px; height: 580px; margin: 0 auto;">
					<%
						for (int r=0; r<=10; r++) {
							for (int c=0; c<=10; c++) {
								if (c == 0) {
									out.print("<div>");
								}
								
								String tileCoverDiv = "<span id=\"tile_span" + r + "_" + c + "\" style=\"position: absolute;\"></span>" +
													  "<div id=\"tile_cover" + r + "_" + c + "\" class=\"tile_cover lfloat\" style=\"display: none;\"></div>";
								out.print("<div id=\"tile" + r + "_" + c + "\" class=\"tile lfloat\" onclick=\"tile_onclick(" + r + "," + c + ")\">" + tileCoverDiv + "</div>");
								
								if (c == 10) {
									out.print("</div>");
								}
							}
						}
					%>
				</div>
				<br>
				<table style="border: 0px solid #000000; width: 100%; max-width: 1024px; min-width: 600px;">
					<tr>
						<td>
							<input id="inputMsgBox" style="width: 100%; height: 28px;" type="text" onkeypress="inputMsgBox_onkeypress()">
						</td>
						<td style="width: 200px; text-align: right;">
							<input id="sendButton" class="basic_button" value="전송" type="button" onclick="sendButton_onclick()">
							&nbsp;
							<input id="scrollUpButton" class="basic_button" value="최상단" type="button" onclick="scrollUpButton_onclick()">
							<input id="scrollDownButton" class="basic_button" value="최하단" type="button" onclick="scrollDownButton_onclick()">
						</td>
					</tr>
				</table>
				<table style="border: 0px solid #000000; width: 100%; max-width: 1024px; min-width: 600px;">
					<tr>
						<td style="width: 200px;">
							<textarea id="userListArea" style="width: 100%;" rows="10" cols="50" readonly="readonly"></textarea>
						</td>
						<td>
							<textarea id="chatBoxArea" style="width: 100%;" rows="10" cols="50" readonly="readonly"></textarea>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<br>
</body>
</html>