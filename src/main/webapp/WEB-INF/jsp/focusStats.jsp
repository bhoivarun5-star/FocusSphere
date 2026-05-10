<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Focus Stats - FocusSphere</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
</head>
<body>
<div class="app-shell">
    <aside class="sidebar">
        <div class="sidebar-brand">FocusSphere</div>
        <details class="sidebar-card" open>
            <summary>Main Menu</summary>
            <nav class="sidebar-nav">
                <a class="${activePage == 'dashboard' ? 'is-active' : ''}" href="/dashboard">Available Rooms</a>
                <a class="${activePage == 'create' ? 'is-active' : ''}" href="/rooms/create">Create Room</a>
                <a class="${activePage == 'join' ? 'is-active' : ''}" href="/rooms/join">Join Room</a>
                <a class="${activePage == 'created' ? 'is-active' : ''}" href="/rooms/created">Created Rooms</a>
                <a class="${activePage == 'requests' ? 'is-active' : ''}" href="/requests/pending">Pending Requests</a>
                <a href="/logout">Logout</a>
            </nav>
        </details>
    </aside>

<main class="page page-with-sidebar">
    <div class="card">
        <h2>Focus Stats</h2>
        <p class="subtext">Add your focus sessions and track your consistency over time.</p>

        <c:if test="${not empty success}">
            <div class="msg ok">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="msg err">${error}</div>
        </c:if>

        <div class="grid-two">
            <div class="card">
                <h3>Session Input</h3>
                <form method="post" action="/focus-stats/sessions">
                    <label>Session Date</label>
                    <input type="date" name="sessionDate" required />

                    <label>Duration (minutes)</label>
                    <input type="number" name="durationMinutes" min="1" max="1440" required />

                    <label>Notes (optional)</label>
                    <textarea name="notes" rows="3" maxlength="600" placeholder="What did you work on?"></textarea>

                    <button class="btn" type="submit" style="margin-top: 12px;">Save Session</button>
                </form>
            </div>

            <div class="card">
                <h3>Calculated Stats</h3>
                <div class="grid-three">
                    <div class="stat-card">
                        <div class="stat-label">Total Sessions</div>
                        <div class="stat-value">${totalSessions}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Total Focus Time</div>
                        <div class="stat-value">${totalFocusMinutes} min</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Average Duration</div>
                        <div class="stat-value">${averageSessionDuration} min</div>
                    </div>
                </div>

                <h4 style="margin-top: 14px;">Session Duration Chart</h4>
                <c:if test="${empty sessions}">
                    <p class="muted">No sessions added yet.</p>
                </c:if>
                <c:if test="${not empty sessions}">
                    <div class="mini-chart">
                        <c:forEach var="s" items="${sessions}" begin="0" end="6">
                            <div class="mini-chart-row">
                                <div class="mini-chart-label">${s.sessionDate}</div>
                                <div class="mini-chart-bar-wrap">
                                    <div class="mini-chart-bar" style="width: ${(s.durationMinutes * 100) / maxDuration}%;"></div>
                                </div>
                                <div class="mini-chart-value">${s.durationMinutes}m</div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </div>

        <h3 style="margin-top: 16px;">Session History</h3>
        <c:if test="${empty sessions}">
            <p class="muted">No focus sessions available.</p>
        </c:if>
        <c:if test="${not empty sessions}">
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Date</th>
                        <th>Duration</th>
                        <th>Notes</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="s" items="${sessions}">
                        <tr>
                            <td>${s.sessionDate}</td>
                            <td>${s.durationMinutes} min</td>
                            <td>${empty s.notes ? '-' : s.notes}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</main>
</div>
</body>
</html>
