<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Room Activity - FocusSphere</title>
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
                <a href="/rooms/${room.roomCode}">Chat Module</a>
                <a class="is-active" href="/rooms/${room.roomCode}/activity">Activity Module</a>
                <a href="/logout">Logout</a>
            </nav>
        </details>
        <div class="sidebar-note">Timers, schedules, and room activity tracking live here.</div>
    </aside>

<main class="page page-with-sidebar">
<c:if test="${not empty success}">
    <div class="msg ok">${success}</div>
</c:if>
<c:if test="${not empty error}">
    <div class="msg err">${error}</div>
</c:if>

<div class="card room-side">
    <h3>${room.roomName} Activity</h3>
    <p class="subtext">Track sessions, breaks, schedule plans, and activity history.</p>

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
            <c:if test="${isCreator}">
                <button class="btn" type="button" onclick="toggleScheduleBox()">Schedule</button>
            </c:if>
        </div>
    </c:if>

    <c:if test="${isCreator}">
        <div id="scheduleBox" class="card" style="display: none; padding: 14px; margin-bottom: 14px;">
            <h4>Create Room Schedule</h4>
            <form method="post" action="/rooms/${room.roomCode}/schedule">
                <label>Schedule Date</label>
                <input type="date" name="scheduleDate" required />

                <label>Start Time</label>
                <input type="time" name="scheduleTime" required />

                <label>Duration (minutes)</label>
                <select name="durationPreset">
                    <option value="25" selected>25 minutes</option>
                    <option value="30">30 minutes</option>
                    <option value="45">45 minutes</option>
                    <option value="60">60 minutes</option>
                    <option value="90">90 minutes</option>
                </select>

                <label>Or Custom Duration (minutes)</label>
                <input type="number" min="5" max="600" name="durationCustom" placeholder="Type custom duration" />

                <div class="inline-actions">
                    <button class="btn" type="submit">Save Schedule</button>
                    <button class="btn btn-secondary" type="button" onclick="toggleScheduleBox()">Close</button>
                </div>
            </form>
        </div>
    </c:if>

    <h4>Scheduled Sessions</h4>
    <c:if test="${empty schedules}">
        <p class="muted">No schedule added yet.</p>
    </c:if>
    <c:if test="${not empty schedules}">
        <div class="table-wrap" style="margin-bottom: 14px;">
            <table>
                <thead>
                <tr>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Duration</th>
                    <th>Created By</th>
                    <c:if test="${isCreator and not isAdminReadOnly}"><th>Action</th></c:if>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="schedule" items="${schedules}">
                    <tr class="schedule-row" data-schedule-date="${schedule.scheduleDate}" data-schedule-time="${schedule.scheduleTime}" data-schedule-duration="${schedule.durationMinutes}">
                        <td>${schedule.scheduleDate}</td>
                        <td>${schedule.scheduleTime}</td>
                        <td>${schedule.durationMinutes} minutes</td>
                        <td>${schedule.createdBy.name}</td>
                        <c:if test="${isCreator and not isAdminReadOnly}">
                            <td>
                                <form method="post" action="/rooms/${room.roomCode}/schedule/${schedule.id}/delete" style="display:inline;">
                                    <button class="btn btn-danger" type="submit">Delete</button>
                                </form>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <h4 style="margin-top: 14px;">Session Activity History</h4>
    <form method="get" action="/rooms/${room.roomCode}/activity" class="inline-actions" style="margin-bottom: 8px;">
        <select name="historyMonths">
            <option value="" ${historyMonths == null ? 'selected' : ''}>All history</option>
            <option value="1" ${historyMonths == 1 ? 'selected' : ''}>Past 1 month</option>
            <option value="2" ${historyMonths == 2 ? 'selected' : ''}>Past 2 months</option>
            <option value="3" ${historyMonths == 3 ? 'selected' : ''}>Past 3 months</option>
            <option value="6" ${historyMonths == 6 ? 'selected' : ''}>Past 6 months</option>
        </select>
        <input type="date" name="historyDate" value="${historyDate}" />
        <button class="btn" type="submit">Search</button>
        <a class="btn btn-secondary" href="/rooms/${room.roomCode}/activity">Reset</a>
    </form>

    <c:if test="${empty sessionActivities}">
        <p class="muted">No session activity found for selected period.</p>
    </c:if>
    <c:if test="${not empty sessionActivities}">
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Student</th>
                    <th>Session Start</th>
                    <th>Session End</th>
                    <th>Duration</th>
                    <th>Break Times</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="activity" items="${sessionActivities}">
                    <tr>
                        <td>${activity.user.name}</td>
                        <td>${activity.sessionStart}</td>
                        <td>${activity.sessionEnd == null ? 'In progress' : activity.sessionEnd}</td>
                        <td>${activity.durationSeconds == null ? '-' : activity.durationSeconds} sec</td>
                        <td>${empty activity.breakPeriods ? '-' : activity.breakPeriods}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
</div>
</main>
</div>

<script>
    var roomCode = '${room.roomCode}';
    var isAdminReadOnly = '${isAdminReadOnly}' === 'true';
    var isCreator = '${isCreator}' === 'true';
    var elapsed = 0;
    var timerInterval = null;
    var autoStopTimeout = null;
    var breakActive = false;
    var breakPausedByUser = false;
    var timerEl = document.getElementById('sessionTimer');
    var breakButton = document.getElementById('breakButton');

    function renderTimer() {
        var hours = String(Math.floor(elapsed / 3600)).padStart(2, '0');
        var minutes = String(Math.floor((elapsed % 3600) / 60)).padStart(2, '0');
        var seconds = String(elapsed % 60).padStart(2, '0');
        timerEl.textContent = hours + ':' + minutes + ':' + seconds;
    }

    function startTimer(isAuto) {
        if (isAdminReadOnly || !isCreator) {
            return;
        }
        if (timerInterval !== null) {
            return;
        }
        breakActive = false;
        breakPausedByUser = false;
        updateBreakButton();
        fetch('/rooms/' + roomCode + '/sessions/start', { method: 'POST' })
            .then(function (res) { return res.text(); })
            .then(function (text) {
                if (isAuto && text === 'Session started.') {
                    document.getElementById('scheduleInfo').textContent = 'Scheduled session started automatically.';
                } else {
                    document.getElementById('scheduleInfo').textContent = text;
                }
            });
        timerInterval = setInterval(function () {
            elapsed += 1;
            renderTimer();
        }, 1000);
    }

    function pauseTimer(isAuto) {
        if (isAdminReadOnly || !isCreator) {
            return;
        }
        if (timerInterval !== null) {
            clearInterval(timerInterval);
            timerInterval = null;
        }
        if (autoStopTimeout !== null) {
            clearTimeout(autoStopTimeout);
            autoStopTimeout = null;
        }
        breakActive = false;
        breakPausedByUser = false;
        updateBreakButton();
        fetch('/rooms/' + roomCode + '/sessions/stop', { method: 'POST' })
            .then(function (res) { return res.text(); })
            .then(function (text) {
                if (isAuto && text === 'Session stopped.') {
                    document.getElementById('scheduleInfo').textContent = 'Scheduled session completed automatically.';
                } else {
                    document.getElementById('scheduleInfo').textContent = text;
                }
                setTimeout(function () { window.location.reload(); }, 500);
            });
    }

    function restartTimer() {
        if (isAdminReadOnly || !isCreator) {
            return;
        }
        pauseTimer(false);
        elapsed = 0;
        renderTimer();
        breakActive = false;
        breakPausedByUser = false;
        updateBreakButton();
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

    function toggleScheduleBox() {
        var box = document.getElementById('scheduleBox');
        if (!box) {
            return;
        }
        box.style.display = box.style.display === 'none' ? 'block' : 'none';
    }

    function setupScheduledAutomation() {
        if (isAdminReadOnly || !isCreator) {
            return;
        }

        var rows = Array.prototype.slice.call(document.querySelectorAll('.schedule-row'));
        if (!rows.length) {
            return;
        }

        var now = new Date();
        var activeWindow = null;
        var nearestFuture = null;

        rows.forEach(function (row) {
            var dateValue = row.getAttribute('data-schedule-date');
            var timeValue = row.getAttribute('data-schedule-time');
            var durationMinutes = parseInt(row.getAttribute('data-schedule-duration') || '0', 10);
            if (!dateValue || !timeValue || !durationMinutes) {
                return;
            }

            var startAt = new Date(dateValue + 'T' + timeValue);
            var endAt = new Date(startAt.getTime() + (durationMinutes * 60 * 1000));

            if (now >= startAt && now < endAt) {
                if (!activeWindow || startAt > activeWindow.startAt) {
                    activeWindow = { startAt: startAt, endAt: endAt };
                }
            } else if (startAt > now) {
                if (!nearestFuture || startAt < nearestFuture.startAt) {
                    nearestFuture = { startAt: startAt, endAt: endAt };
                }
            }
        });

        if (activeWindow) {
            elapsed = Math.max(0, Math.floor((now.getTime() - activeWindow.startAt.getTime()) / 1000));
            renderTimer();
            startTimer(true);
            var remainingMs = Math.max(1000, activeWindow.endAt.getTime() - now.getTime());
            autoStopTimeout = setTimeout(function () {
                pauseTimer(true);
            }, remainingMs);
            document.getElementById('scheduleInfo').textContent = 'Scheduled session is currently running.';
            return;
        }

        if (nearestFuture) {
            var delayMs = Math.max(1000, nearestFuture.startAt.getTime() - now.getTime());
            var remainingMinutes = Math.ceil(delayMs / 60000);
            document.getElementById('scheduleInfo').textContent = 'Next scheduled session starts in about ' + remainingMinutes + ' minute(s).';
            setTimeout(function () {
                elapsed = 0;
                renderTimer();
                startTimer(true);
                var durationMs = Math.max(1000, nearestFuture.endAt.getTime() - nearestFuture.startAt.getTime());
                autoStopTimeout = setTimeout(function () {
                    pauseTimer(true);
                }, durationMs);
            }, delayMs);
        }
    }

    renderTimer();
    setupScheduledAutomation();
</script>
</body>
</html>
