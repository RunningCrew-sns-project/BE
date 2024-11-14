let stompClient = null;
let isConnected = false;
let roomId = null;
let accessToken;
let refreshToken
let userEmail;
let userList = [];
const headerList = {
	'Authorization' : getAccessToken(),
	'RefreshToken' : getRefreshToken()
}


// 서버와 연결
function connect() {
	console.log("connect 함수 실행")

	if(isConnected) {
		console.log("이미 연결된 소켓")
		return;
	}

	const socket = new SockJS('http://localhost:8080/ws');
	// const socket = new WebSocket("ws://localhost:8080/ws");
	stompClient = Stomp.over(socket);

	console.log("headerList : " + headerList.Authorization)

	stompClient.connect(headerList, function (frame) {
		console.log(`connected to room : ${roomId}`)
		isConnected = true;

		// 채팅방에 입장하면 메시지 구독
		stompClient.subscribe('/sub/chat/room/' + roomId, function (message) {
			console.log(message)
			const chatMessage = JSON.parse(message.body)
			showMessage(chatMessage);

			updateUserList(chatMessage.sender);

			if (chatMessage.type === "LEAVE")
				removeUserFromList(chatMessage.sender);
		});

		const formattedDate = new Date().toISOString()

		// 채팅방에 입장 메시지 전송
		stompClient.send('/pub/chat/enterUser', {}, JSON.stringify({
			'type': "ENTER",
			'roomId': roomId,
			'sender': userEmail,
			'message' : `${userEmail} 입장`,
			'time': formattedDate
		}));

		// 클라이언트 상태를 로컬 스토리지에 저장
		localStorage.setItem(`isConnected`, true);
		localStorage.setItem(`currentRoomId`, roomId);

	});

}


// 메시지 화면에 표시
function showMessage(chat) {
	const chatMessagesDiv = document.getElementById('chat-messages');


	// 메시지를 감싸는 div 요소 생성 및 클래스 할당
	const messageWrapper = document.createElement("div");
	messageWrapper.className = 'message'; // 기본 message 클래스 할당

	const usernameElement = document.createElement('span');
	usernameElement.className = 'username'
	usernameElement.innerText = chat.userName;

	// p 요소에 텍스트 추가

	const timeMessageWrapper = document.createElement("div");
	timeMessageWrapper.className = 'time-message-wrapper';

	const messageElement = document.createElement('p');
	messageElement.innerText = chat.message

	const timeElement = document.createElement('span');
	timeElement.className = 'time'
	timeElement.innerText = formatDateToHHMMSS(chat.time)

	messageWrapper.appendChild(usernameElement)

	// 발신자에 따라 messageWrapper에 추가 클래스 할당
	if (chat.sender === userEmail) {
		messageWrapper.classList.add('right'); // message right 클래스 설정
		timeMessageWrapper.appendChild(timeElement)
		timeMessageWrapper.appendChild(messageElement)
	} else {
		messageWrapper.classList.add('left'); // message left 클래스 설정

		timeMessageWrapper.appendChild(messageElement)
		timeMessageWrapper.appendChild(timeElement)
	}

	messageWrapper.appendChild(timeMessageWrapper)

	// messageWrapper에 messageElement 추가하고 chatMessagesDiv에 추가
	chatMessagesDiv.appendChild(messageWrapper);

	// chatMessagesDiv.scrollTop = chatMessagesDiv.scrollHeight
}

let hasMoreMessages = true;
let lastMessageTime = null
async function loadPreviousMessages(roomId){

	if(!hasMoreMessages){
		return;
	}
	try {
		console.log("lastMessageTime : " + lastMessageTime)
		const url = lastMessageTime
			? `http://localhost:8080/api/chat/message?roomId=${roomId}&limit=30&lastTime=${lastMessageTime}`
			: `http://localhost:8080/api/chat/message?roomId=${roomId}&limit=30`;

		const response = await fetch(url,{
			headers : headerList
		});

		const responseJson = await response.json();
		// data.forEach((message) => {
		// 	showMessage(message);
		// })

		const data = responseJson.success.responseData;
		data.forEach(message=>{
			console.log("응답데이터 : " + message.sender)
		})

		console.log("메세지 가져오기 : " + data);

		if(data.length > 0){
			lastMessageTime = data[data.length -1].time;
			// lastMessageTime = data[0].time;

			data.forEach((message) => {

				const chatMessagesDiv = document.getElementById('chat-messages');


				// 메시지를 감싸는 div 요소 생성 및 클래스 할당
				const messageWrapper = document.createElement("div");
				messageWrapper.className = 'message'; // 기본 message 클래스 할당

				const usernameElement = document.createElement('span');
				usernameElement.className = 'username'
				usernameElement.innerText = message.userName;

				// p 요소에 텍스트 추가

				const timeMessageWrapper = document.createElement("div");
				timeMessageWrapper.className = 'time-message-wrapper';

				const messageElement = document.createElement('p');
				messageElement.innerText = message.message

				const timeElement = document.createElement('span');
				timeElement.className = 'time'
				timeElement.innerText = formatDateToHHMMSS(message.time)

				messageWrapper.appendChild(usernameElement)

				// 발신자에 따라 messageWrapper에 추가 클래스 할당
				if (message.sender === userEmail) {
					messageWrapper.classList.add('right'); // message right 클래스 설정
					timeMessageWrapper.appendChild(timeElement)
					timeMessageWrapper.appendChild(messageElement)
				} else {
					messageWrapper.classList.add('left'); // message left 클래스 설정

					timeMessageWrapper.appendChild(messageElement)
					timeMessageWrapper.appendChild(timeElement)
				}

				messageWrapper.appendChild(timeMessageWrapper)

				// messageWrapper에 messageElement 추가하고 chatMessagesDiv에 추가

				chatMessagesDiv.appendChild(messageWrapper);// prepend 사용하여 위에 추가
			});
		}else
			hasMoreMessages = false;

		data.forEach((message) => {
			console.log("데이터입니다 : " + message.message)
		})

	}catch (error){
		console.log("메세지를 불러오는데 실패했습니다.")
	}

}

async function login() {
	const emailOrPhoneNumber = prompt("아이디를 입력하세요");
	const password = prompt("비밀번호를 입력하세요");

	try {
		const response = await fetch('http://localhost:8080/api/auth/login', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ emailOrPhoneNumber, password })
		});

		if (response.ok) {
			const res = await response.json();
			console.log("응답이요~ " + res.success)
			accessToken = "Bearer " + res.success.responseData.accessToken
			refreshToken = res.success.responseData.refreshToken;
			userEmail = emailOrPhoneNumber

			localStorage.setItem('accessToken', accessToken);
			localStorage.setItem('refreshToken', refreshToken);
			localStorage.setItem('userEmail', emailOrPhoneNumber);

			alert("로그인 성공");
			loadChatRooms();
		} else {
			alert('로그인 실패');
		}
	} catch (error) {
		console.error('Error logging in:', error);
	}
}

function getAccessToken() {
	return localStorage.getItem('accessToken');
}

function getRefreshToken() {
	return localStorage.getItem('refreshToken');
}

function getUserEmail() {
	return localStorage.getItem('userEmail');
}


// 서버로부터 채팅방 목록을 불러옴
async function loadChatRooms() {
	try {
		const response = await fetch('http://localhost:8080/api/chat/myRooms',{
			headers: headerList
		});  // ChatRoomResponse 데이터를 반환하는 API 호출
		// if(!response.ok) {
		// 	localStorage.removeItem('accessToken');
		// 	localStorage.removeItem('refreshToken');
		// 	localStorage.removeItem('userEmail');
		// 	login()
		// }

		const responseData = await response.json();
		console.log("loadChatRooms : " + responseData.success.responseData);
		const rooms = responseData.success.responseData


		displayChatRooms(rooms);
	} catch (error) {
		console.error('Error loading chat rooms:', error);
	}
}

// 채팅방 목록 표시
function displayChatRooms(rooms) {
	console.log(rooms);

	const chatRoomListDiv = document.getElementById('chat-room-list');
	chatRoomListDiv.innerHTML = '';  // 이전 항목들을 제거

	rooms.forEach(room => {
		if (room.roomId > 0) { // roomId가 유효한 경우에만 처리
			const roomElement = document.createElement('div');
			roomElement.innerHTML = `<button onclick="joinRoom(${room.roomId}, '${room.title}')">${room.title} (${room.userCount}명)</button>`;
			chatRoomListDiv.appendChild(roomElement);
		} else {
			console.error(`Invalid roomId: ${room.roomId}`); // roomId가 유효하지 않은 경우 에러 로그
		}
	});
}

// 채팅방 입장
async function joinRoom(id, title) {
	roomId = id;

	console.log("joinroom 함수에서 룸 아이디"+roomId)

	document.getElementById('chat-room').style.display = 'block';
	document.getElementById('chat-controls').style.display = 'block';
	document.getElementById('chat-room-title').innerText = title;

	const chatMessagesDiv = document.getElementById('chat-messages');
	chatMessagesDiv.innerHTML = '';

	try {
		userList = await getUserList();  // 채팅방 유저 리스트 불러오기


		console.log("응답 유저 리스트 : " + userList)
		displayUserList(userList);  // 유저 목록 표시

		console.log("연결된 유저 리스트 : " + userList)
		console.log("내 이름 : " + userEmail)

		if(!stompClient){
			connect()
		}
		// connect(userList);  // 서버 연결 후 메시지 구독
		loadChatRooms();
		loadPreviousMessages(roomId);

	} catch (error) {
		console.error('Error loading user list:', error);
	}
}



async function getUserList(){

	const response = await fetch(`http://localhost:8080/api/chat/userlist/${roomId}`,{
		headers: headerList
	});
	const data = await response.json();
	console.log(data.success.responseData)
	return data.success.responseData;
}

// 유저 목록 표시
function displayUserList(userList) {
	console.log("userList ::::::" + userList)
	const userListDiv = document.getElementById('user-list');
	userListDiv.innerHTML = '';  // 이전 유저 목록 제거

	// 유저 목록을 동적으로 생성하여 오른쪽에 표시
	const userListHeader = document.createElement('h4');
	userListHeader.textContent = "참여한 유저:";
	userListDiv.appendChild(userListHeader);

	const addedUsers = new Set(); // 추가된 유저를 추적하기 위해 Set 사용

	// 자신의 이름을 먼저 추가
	if (userEmail) {
		const myUserElement = document.createElement('p');
		myUserElement.textContent = userEmail + " <나>";  // 현재 사용자 표시
		userListDiv.appendChild(myUserElement);
		addedUsers.add(userEmail); // 추가된 유저로 등록
	}

	// 나머지 유저들을 추가
	userList.forEach(user => {
		addedUsers.forEach(value => {
			console.log("추가된 유저 : " + value)
		})

		if (!addedUsers.has(user)) { // 이미 추가된 유저가 아닌 경우에만 추가
			const userElement = document.createElement('p');
			userElement.textContent = user;
			userElement.dataset.username = user; // dataset.username 설정
			userListDiv.appendChild(userElement);
			addedUsers.add(user);  // 추가된 유저로 등록
		}
	});
}

// 유저 목록 업데이트 함수
async function updateUserList(newUser) {
	const userListDiv = document.getElementById('user-list');

	// 기존에 유저가 목록에 있는지 확인
	const existingUser = Array.from(userListDiv.getElementsByTagName('p')).find(user => user.dataset.username === newUser);
	if (existingUser) {
		console.log(`User ${newUser} is already in the list`);
		return;  // 이미 유저가 목록에 있는 경우 함수 종료
	}

	userList = await getUserList();
	displayUserList(userList);
	loadChatRooms()
}

// 유저 목록에서 특정 유저를 제거하는 함수
async function removeUserFromList(userToRemove) {
	const userListDiv = document.getElementById('user-list');
	const users = userListDiv.getElementsByTagName('p');

	for (let user of users) {
		if (user.dataset.username === userToRemove) {
			userListDiv.removeChild(user);  // 유저를 목록에서 제거
			break;  // 제거한 후 반복 종료
		}
	}

	const response = await getUserList();
	userList = await response.json();
	displayUserList(userList);
	loadChatRooms()
}


// 채팅방 생성
async function createChatRoom() {
	const roomName = document.getElementById('room-name').value.trim();
	if (!roomName) {
		alert('채팅방 이름을 입력하세요.');
		return;
	}
	console.log(roomName)

	try {
		const response = await fetch('http://localhost:8080/api/chat/createRoom', {
			method: 'POST',
			headers: headerList,
			body: roomName,  // roomName을 JSON 형태로 전달
		});

		if (response.ok) {
			const result = await response.text();  // 응답 텍스트 읽기
			alert(result);  // 채팅방 생성 성공 메시지 표시
			loadChatRooms();  // 채팅방 목록 새로 고침
		} else {
			alert('채팅방 생성에 실패했습니다.');
		}
	} catch (error) {
		console.error('Error creating chat room:', error);
		alert('채팅방 생성 중 오류가 발생했습니다.');
	}
}

// 이벤트 리스너 추가
document.getElementById('create-room-btn').addEventListener('click', createChatRoom);

function formatDateToHHMMSS(isoString) {
	// ISO 문자열을 Date 객체로 변환
	const date = new Date(isoString);

	// 시간을 HH:mm:ss 형식으로 포맷팅
	const pad = (n) => (n < 10 ? '0' + n : n); // 한 자리 수를 두 자리로 포맷

	const month = date.getMonth() + 1;
	const day = date.getDate();

	const hours = pad(date.getHours());
	const minutes = pad(date.getMinutes());

	return `${month}/${day} ${hours}:${minutes}`;
}

// 메시지 전송
function sendMessage() {
	console.log("sendMessage 호출")
	const messageBox = document.getElementById('message-box');
	const date = new Date();
	const kstOffset = 9 * 60 * 60 * 1000; // 9시간을 밀리초로 변환
	const formattedDate = new Date(date.getTime() + kstOffset).toISOString();

	// const formattedDate = new Date().toISOString()
	console.log("sendmessage time : " + formattedDate)
	const message = messageBox.value.trim();

	console.log("메시지 박스 값:", message);
	console.log("roomId 값:", roomId);
	console.log("stompClient:", stompClient);

	if (message && stompClient) {
		console.log("조건문")
		stompClient.send('/pub/chat/sendMessage', {}, JSON.stringify({
			'type': "TALK",
			'roomId': roomId,
			'sender': userEmail,
			'message': message,
			'time': formattedDate
		}));
		messageBox.value = '';  // 메시지 보낸 후 입력창 초기화
	}
	console.log("sendMessage 호출 끝")
}


// 채팅방 퇴장
async function leaveRoom() {
	const formattedDate = new Date().toISOString()

	if (stompClient) {

		stompClient.send('/pub/chat/leaveUser', {}, JSON.stringify({
			'type': "LEAVE",
			'roomId': roomId,
			'sender': userEmail,
			'time': formattedDate
		}));

		await removeUserFromList(userEmail)

		try {
			// UI 업데이트
			const chatMessagesDiv = document.getElementById('chat-messages');
			chatMessagesDiv.innerHTML = '';
			const userListDiv = document.getElementById('user-list');
			userListDiv.innerHTML = '';

			// 방 목록 다시 로드
			loadChatRooms();

		} catch (error) {
			console.error('Error leaving room:', error);
		}

		stompClient.disconnect(() => {
			console.log("Disconnected");
			isConnected = false;
			localStorage.removeItem(`isConnected`);
			localStorage.removeItem(`currentRoomId`);
		});
	}

	const chatMessagesDiv = document.getElementById('chat-messages');
	chatMessagesDiv.innerHTML = '';
	const userListDiv = document.getElementById('user-list');
	userListDiv.innerHTML = '';
}

// 이벤트 리스너 설정
document.getElementById('send-btn').addEventListener('click', sendMessage);
document.getElementById('leave-btn').addEventListener('click', leaveRoom);

document.getElementById('message-box').addEventListener('keydown', function(event) {
	if (event.key === 'Enter') { // 엔터 키가 눌렸는지 확인
		event.preventDefault(); // 기본 엔터키 동작(줄바꿈) 방지
		if(!event.repeat)
			sendMessage(); // 메시지 전송
	}
});


// 페이지 로드 시 채팅방 목록 불러오기
window.onload = () => {
	accessToken = getAccessToken();
	refreshToken = getRefreshToken();
	userEmail = getUserEmail();

	if(accessToken && userEmail) {
		loadChatRooms()
	}
	else
		login()
};

const chatMessages = document.getElementById('chat-messages');
let isLoading = false;

chatMessages.addEventListener('scroll', () => {
	// 스크롤이 맨 위에 도달했는지 확인
	if (chatMessages.scrollTop + chatMessages.clientHeight >= chatMessages.scrollHeight && !isLoading) {
		isLoading = true; // 로딩 상태로 설정
		loadPreviousMessages(roomId).then(() => {
			isLoading = false; // 로딩이 끝난 후 상태를 원래대로 되돌림
		});
	}
});
