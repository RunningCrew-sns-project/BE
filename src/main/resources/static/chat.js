let stompClients = {};
let isConnected = {};
let roomId = null;
let accessToken;
let refreshToken
let userEmail;
let userList = [];


// 서버와 연결
function connect(userList) {
	if(isConnected[roomId]) {
		console.log("이미 연결된 소켓")
		return;
	}

	console.log(userList, userEmail)
	if(userList.includes(userEmail)) {
		return;
	}

	const socket = new SockJS('http://localhost:8080/ws');
	// const socket = new WebSocket("ws://localhost:8080/ws");
	stompClients[roomId] = Stomp.over(socket);
	const headers = {
		'Authorization': getAccessToken(),
		'RefreshToken': getRefreshToken()
	};

	stompClients[roomId].connect(headers, function (frame) {
		console.log(`connected to room : ${roomId}`)
		isConnected[roomId] = true;

		// 채팅방에 입장하면 메시지 구독
		stompClients[roomId].subscribe('/sub/chat/room/' + roomId, function (message) {
			console.log(message)
			const chatMessage = JSON.parse(message.body)
			showMessage(chatMessage);
			if(chatMessage.type === "ENTER")
				updateUserList(chatMessage.sender);
			else if (chatMessage.type === "LEAVE")
				removeUserFromList(chatMessage.sender);
		});

		const formattedDate = new Date().toISOString();  // ISO 포맷으로 현재 시간

		// 채팅방에 입장 메시지 전송
		stompClients[roomId].send('/pub/chat/enterUser', {}, JSON.stringify({
			'type': "ENTER",
			'roomId': roomId,
			'sender': userEmail,
			'time': formattedDate
		}));

		// 클라이언트 상태를 로컬 스토리지에 저장
		localStorage.setItem(`isConnected`, true);
		localStorage.setItem(`currentRoomId`, roomId);
	});
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
			console.log(res)
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

function getUsername() {
	return localStorage.getItem('userEmail');
}


// 서버로부터 채팅방 목록을 불러옴
async function loadChatRooms() {
	try {
		const response = await fetch('http://localhost:8080/chat/rooms',{
			headers:{
				'Content-Type': 'application/json',
				'Authorization': accessToken,
				'RefreshToken': refreshToken
			}
		});  // ChatRoomResponse 데이터를 반환하는 API 호출
		const rooms = await response.json();
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
	document.getElementById('chat-room').style.display = 'block';
	document.getElementById('chat-controls').style.display = 'block';
	document.getElementById('chat-room-title').innerText = title;

	const chatMessagesDiv = document.getElementById('chat-messages');
	chatMessagesDiv.innerHTML = '';

	try {
		const response = await getUserList();  // 채팅방 유저 리스트 불러오기
		userList = await response.json();
		displayUserList(userList);  // 유저 목록 표시
		console.log("연결된 유저 리스트 : " + userList)
		console.log("내 이름 : " + userEmail)

		if(!stompClients[roomId]){
			connect(userList)
		}
		// connect(userList);  // 서버 연결 후 메시지 구독
		loadChatRooms();

	} catch (error) {
		console.error('Error loading user list:', error);
	}
}

async function getUserList(){

	return await fetch(`http://localhost:8080/chat/userlist/${roomId}`,{
		headers: {
			'Content-Type': 'application/json',
			'Authorization': localStorage.getItem('accessToken'),
			'RefreshToken': localStorage.getItem('refreshToken'),
		}
	});
}

// 유저 목록 표시
function displayUserList(userList) {

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
		console.log(addedUsers)
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

	const response = await getUserList();
	userList = await response.json();
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
		const response = await fetch('http://localhost:8080/chat/createRoom', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': accessToken,
				'RefreshToken': refreshToken
			},
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

// 메시지 화면에 표시
function showMessage(chat) {
	const chatMessagesDiv = document.getElementById('chat-messages');
	const messageElement = document.createElement('p');
	if(chat.sender === userEmail)
		messageElement.className = 'message right'
	else
		messageElement.className = 'message left';

	messageElement.innerText = `${chat.message}`;
	chatMessagesDiv.appendChild(messageElement);
}

// 메시지 전송
function sendMessage() {
	console.log("sendMessage 호출")
	const messageBox = document.getElementById('message-box');
	const formattedDate = new Date().toISOString();
	const message = messageBox.value.trim();

	console.log("메시지 박스 값:", message);
	console.log("roomId 값:", roomId);
	console.log("stompClients:", stompClients);

	if (message && stompClients[roomId]) {
		console.log("조건문")
		stompClients[roomId].send('/pub/chat/sendMessage', {}, JSON.stringify({
			'type': "TALK",
			'roomId': roomId,
			'sender': userEmail,
			'message': message,
			'time': formattedDate
		}));
		// showMessage(message)
		messageBox.value = '';  // 메시지 보낸 후 입력창 초기화
	}
	console.log("sendMessage 호출 끝")
}

// 채팅방 퇴장
async function leaveRoom() {
	const formattedDate = new Date().toISOString();

	if (stompClients[roomId]) {

		stompClients[roomId].send('/pub/chat/leaveUser', {}, JSON.stringify({
			'type': "LEAVE",
			'roomId': roomId,
			'sender': userEmail,
			'time': formattedDate
		}));

		removeUserFromList(userEmail)

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

		stompClients[roomId].disconnect(() => {
			console.log("Disconnected");
			isConnected[roomId] = false;
			delete stompClients[roomId];
			localStorage.removeItem(`isConnected`);
			localStorage.removeItem(`currentRoomId`);
		});
	}

	const chatMessagesDiv = document.getElementById('chat-messages');
	chatMessagesDiv.innerHTML = '';
	const userListDiv = document.getElementById('user-list');
	userListDiv.innerHTML = '';

	loadChatRooms()
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

function initializeStompClients() {
	const savedRoomId = localStorage.getItem('currentRoomId');
	const savedIsConnected = localStorage.getItem('isConnected') === 'true';

	if (savedIsConnected && savedRoomId) {
		roomId = savedRoomId;
		// 기존의 stompClient를 다시 연결
		connect(userList);
	}
}


// 페이지 로드 시 채팅방 목록 불러오기
window.onload = () => {
	accessToken = getAccessToken();
	refreshToken = getRefreshToken();
	userEmail = getUsername();

	if (accessToken && userEmail) {
		loadChatRooms();
		initializeStompClients();
	} else {
		login(); // 로그인 함수 호출
	}
};