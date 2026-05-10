<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Room - FocusSphere</title>
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
                <a class="${activePage == 'notifications' ? 'is-active' : ''}" href="/notifications">Notifications</a>
                <a class="${activePage == 'reports' ? 'is-active' : ''}" href="/reports">Reports</a>
                <a href="/logout">Logout</a>
            </nav>
        </details>
        <div class="sidebar-note">Create a glowing workspace for focused sessions.</div>
    </aside>

<main class="page page-with-sidebar">
<div class="card create-room-card">
    <h2>Create Focus Room</h2>
    <p class="subtext">Create a room and choose whether it is public or private.</p>

    <c:if test="${not empty error}">
        <div class="msg err">${error}</div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="msg ok">${success}</div>
    </c:if>

    <form method="post" action="/rooms/create">
        <label>Room Name</label>
        <input type="text" name="roomName" placeholder="Example: Physics Revision" required />

        <label>Room Description</label>
        <textarea name="roomDescription" rows="4" maxlength="400" placeholder="Optional description for room purpose and session goal."></textarea>

        <label>Room Visibility</label>
        <select name="visibility" required>
            <option value="PUBLIC" selected>Public (visible to everyone)</option>
            <option value="PRIVATE">Private (join only with access code)</option>
        </select>

        <button class="btn btn-block" style="margin-top: 14px;" type="submit">Create Room</button>
    </form>

    <p style="margin-top: 14px;"><a href="/dashboard">Back to Dashboard</a></p>
</div>
</main>
</div>
</body>
</html>
