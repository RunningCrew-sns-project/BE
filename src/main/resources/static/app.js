// STOMP 클라이언트 설정
const socket = new WebSocket('ws://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
	console.log('Connected: ' + frame);

	// 채팅방 구독 처리 (예: 방 ID가 1인 경우)
	stompClient.subscribe('/sub/chat/room/1', function (messageOutput) {
		const message = JSON.parse(messageOutput.body);
		displayMessage(message);
	});
});

// 메시지 전송 처리
document.getElementById('sendButton').addEventListener('click', function () {
	const messageInput = document.getElementById('messageInput');
	const chatMessage = {
		roomId: 1,
		sender: 'user1', // 사용자 이름 또는 ID를 동적으로 설정할 수 있습니다.
		message: messageInput.value,
		type: 'TALK'
	};
	stompClient.send('/pub/chat/sendMessage', {}, JSON.stringify(chatMessage));
	messageInput.value = ''; // 메시지 입력 필드 초기화
});

// 메시지 화면에 표시
function displayMessage(message) {
	const messagesDiv = document.getElementById('messages');
	const messageDiv = document.createElement('div');
	messageDiv.classList.add('message');
	messageDiv.innerText = `${message.sender}: ${message.message}`;
	messagesDiv.appendChild(messageDiv);
	messagesDiv.scrollTop = messagesDiv.scrollHeight; // 스크롤을 아래로 이동
}
