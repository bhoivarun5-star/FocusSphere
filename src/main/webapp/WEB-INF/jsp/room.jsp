<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Room Chat - FocusSphere</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
</head>
<body>
<div class="app-shell">
    <aside class="sidebar">
        <div class="sidebar-brand">FocusSphere</div>
        <details class="sidebar-card" open>
            <summary>Room Menu</summary>
            <nav class="sidebar-nav">
                <c:choose>
                    <c:when test="${isAdminReadOnly}">
                        <a href="/admin/dashboard">Admin Dashboard</a>
                    </c:when>
                    <c:otherwise>
                        <a href="/dashboard">Dashboard</a>
                        <a href="/rooms/join">Rooms</a>
                    </c:otherwise>
                </c:choose>
                <a class="is-active" href="/rooms/${room.roomCode}">Chat Module</a>
                <a href="/rooms/${room.roomCode}/activity">Activity Module</a>
                <a href="/logout">Logout</a>
            </nav>
        </details>
        <div class="sidebar-note">Chat and activity tracking are now separate modules for cleaner focus.</div>
    </aside>

<main class="page page-with-sidebar">
    <c:if test="${not empty success}">
        <div class="msg ok">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="msg err">${error}</div>
    </c:if>

    <div class="room-layout">
        <div class="card room-main">
            <h3>Real-time Room Chat</h3>
            <div class="session-badge">Chat Module</div>
            <div id="chatBox" class="chat-box">
                <c:forEach var="msg" items="${messages}">
                    <div class="chat-item ${msg.sender.name eq sessionUser.name ? 'mine' : 'theirs'}">
                        <div class="bubble"><strong>${msg.sender.name}:</strong> ${msg.content}</div>
                        <div class="bubble-meta">${msg.sentAt}</div>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${isAdminReadOnly}">
                <p class="subtext">Admin monitor mode: chat is read-only.</p>
            </c:if>
            <c:if test="${not isAdminReadOnly}">
                <div class="chat-controls">
                    <input id="messageInput" type="text" placeholder="Type your message..." />
                    <button class="btn" type="button" onclick="sendMessage()">Send</button>
                </div>
            </c:if>
        </div>

        <div class="card room-side members">
            <h3>${room.roomName}</h3>
            <c:if test="${isCreator and room.visibility == 'PRIVATE'}">
                <p class="subtext"><strong>Private Access Code:</strong> ${room.privateAccessCode}</p>
            </c:if>

            <div class="stat-card" style="margin: 14px 0;">
                <div class="stat-label">Session Timer</div>
                <div id="sessionTimer" class="stat-value" style="font-size: 20px;">00:00:00</div>
                <div id="scheduleInfo" class="subtext" style="margin-top: 8px;">Timer is paused.</div>
            </div>

            <c:if test="${not isAdminReadOnly and isCreator}">
                <div class="inline-actions" style="margin-bottom: 14px; display: flex; gap: 8px; flex-wrap: wrap;">
                    <button class="btn" type="button" onclick="startTimer()">Start</button>
                    <button class="btn btn-danger" type="button" onclick="pauseTimer()">Stop</button>
                    <button class="btn" type="button" onclick="restartTimer()">Restart</button>
                    <button id="breakButton" class="btn btn-break" type="button" onclick="toggleBreak()">Break</button>
                </div>
            </c:if>

            <p class="subtext">Need timers, schedules, and history? Open the activity module for this room.</p>
            <a class="btn btn-secondary" href="/rooms/${room.roomCode}/activity">Open Activity Module</a>

            <h4 style="margin-top: 14px;">Students in the Room</h4>
            <ul class="list-clean">
                <c:forEach var="member" items="${members}">
                    <li>${member.name} (${member.rollNo})</li>
                </c:forEach>
            </ul>
        </div>
    </div>
</main>
</div>

<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
<script>
    var roomCode = '${room.roomCode}';
    var isAdminReadOnly = '${isAdminReadOnly}' === 'true';
    var isCreator = '${isCreator}' === 'true';
    var senderEmail = '${sessionUser.email}';
    var currentUserName = '${sessionUser.name}';
    var socket = new SockJS('/chat');
    var stompClient = Stomp.over(socket);
    var elapsed = 0;
    var timerInterval = null;
    var breakActive = false;
    var breakPausedByUser = false;
    var timerEl = document.getElementById('sessionTimer');
    var breakButton = document.getElementById('breakButton');

    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/room/' + roomCode, function (message) {
            var payload = JSON.parse(message.body);
            appendMessage(payload.senderName, payload.content, payload.timestamp, payload.senderName === currentUserName);
        });
    });

    function sendMessage() {
        if (isAdminReadOnly) {
            return;
        }
        var input = document.getElementById('messageInput');
        if (!input) {
            return;
        }
        var content = input.value.trim();
        if (content.length === 0) {
            return;
        }

        stompClient.send('/app/chat.send', {}, JSON.stringify({
            roomCode: roomCode,
            senderEmail: senderEmail,
            content: content
        }));
        input.value = '';
    }

    function appendMessage(senderName, content, timestamp, isMine) {
        var chatBox = document.getElementById('chatBox');
        var item = document.createElement('div');
        item.className = 'chat-item ' + (isMine ? 'mine' : 'theirs');
        item.innerHTML = '<div class="bubble"><strong>' + escapeHtml(senderName) + ':</strong> ' + escapeHtml(content) + '</div>' +
            '<div class="bubble-meta">' + escapeHtml(timestamp) + '</div>';
        chatBox.appendChild(item);
        chatBox.scrollTop = chatBox.scrollHeight;
    }

    function escapeHtml(text) {
        var map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function(m) { return map[m]; });
    }

    function renderTimer() {
        if (!timerEl) {
            return;
        }
        var hours = String(Math.floor(elapsed / 3600)).padStart(2, '0');
        var minutes = String(Math.floor((elapsed % 3600) / 60)).padStart(2, '0');
        var seconds = String(elapsed % 60).padStart(2, '0');
        timerEl.textContent = hours + ':' + minutes + ':' + seconds;
    }

    function startTimer() {
        if (isAdminReadOnly || !isCreator || timerInterval !== null) {
            return;
        }
        breakActive = false;
        breakPausedByUser = false;
        updateBreakButton();
        fetch('/rooms/' + roomCode + '/sessions/start', { method: 'POST' })
            .then(function (res) { return res.text(); })
            .then(function (text) {
                document.getElementById('scheduleInfo').textContent = text;
            });
        timerInterval = setInterval(function () {
            elapsed += 1;
            renderTimer();
        }, 1000);
    }

    function pauseTimer() {
        if (isAdminReadOnly || !isCreator) {
            return;
        }
        if (timerInterval !== null) {
            clearInterval(timerInterval);
            timerInterval = null;
        }
        breakActive = false;
        breakPausedByUser = false;
        updateBreakButton();
        fetch('/rooms/' + roomCode + '/sessions/stop', { method: 'POST' })
            .then(function (res) { return res.text(); })
            .then(function (text) {
                document.getElementById('scheduleInfo').textContent = text;
            });
    }

    function restartTimer() {
        if (isAdminReadOnly || !isCreator) {
            return;
        }
        pauseTimer();
        elapsed = 0;
        renderTimer();
        document.getElementById('scheduleInfo').textContent = 'Timer restarted.';
    }

    function toggleBreak() {
        if (isAdminReadOnly || !isCreator) {
            return;
        }
        if (!breakActive) {
            fetch('/rooms/' + roomCode + '/sessions/break/start', { method: 'POST' })
                .then(function (res) { return res.text(); })
                .then(function (text) {
                    if (text !== 'Break started.') {
                        document.getElementById('scheduleInfo').textContent = text;
                        return;
                    }
                    if (timerInterval !== null) {
                        clearInterval(timerInterval);
                        timerInterval = null;
                    }
                    breakActive = true;
                    breakPausedByUser = true;
                    updateBreakButton();
                    document.getElementById('scheduleInfo').textContent = 'Break active. Timer paused.';
                });
            return;
        }

        fetch('/rooms/' + roomCode + '/sessions/break/end', { method: 'POST' })
            .then(function (res) { return res.text(); })
            .then(function (text) {
                if (text !== 'Break ended.') {
                    document.getElementById('scheduleInfo').textContent = text;
                    return;
                }
                breakActive = false;
                updateBreakButton();
                if (breakPausedByUser) {
                    breakPausedByUser = false;
                    timerInterval = setInterval(function () {
                        elapsed += 1;
                        renderTimer();
                    }, 1000);
                }
                document.getElementById('scheduleInfo').textContent = 'Break ended. Timer resumed.';
            });
    }

    function updateBreakButton() {
        if (!breakButton) {
            return;
        }
        if (breakActive) {
            breakButton.classList.add('is-break-active');
            breakButton.textContent = 'End Break';
        } else {
            breakButton.classList.remove('is-break-active');
            breakButton.textContent = 'Break';
        }
    }

    renderTimer();
</script>
</body>
</html>
