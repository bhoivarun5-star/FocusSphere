<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Join Room - FocusSphere</title>
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
        <div class="sidebar-note">Browse public rooms or request access to private ones.</div>
    </aside>

<main class="page page-with-sidebar">
<div class="card">
    <h2>Join Focus Room</h2>
    <p class="subtext">Public rooms are listed below and can be requested directly. Private rooms require a code shared by the creator.</p>

    <c:if test="${not empty error}">
        <div class="msg err">${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="msg ok">${success}</div>
    </c:if>

    <form method="post" action="/rooms/join">
        <label>Enter Private Access Code (only for private rooms)</label>
        <input type="text" name="roomCode" placeholder="Example: A1B2C3D4" required />
        <button class="btn btn-success btn-block" type="submit">Send Join Request</button>
    </form>

    <h3>Public Rooms Created By Others</h3>
    <c:if test="${empty rooms}">
        <p class="muted">No rooms available yet.</p>
    </c:if>
    <c:if test="${not empty rooms}">
        <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>Room Name</th>
                <th>Description</th>
                <th>Visibility</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="room" items="${rooms}">
                <tr>
                    <td>${room.roomName}</td>
                    <td>${empty room.roomDescription ? '-' : room.roomDescription}</td>
                    <td>${empty room.visibility ? 'PUBLIC' : room.visibility}</td>
                    <td>
                        <form method="post" action="/rooms/${room.roomCode}/request-join">
                            <button class="btn btn-success" type="submit">Request</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </div>
    </c:if>

    <p style="margin-top: 14px;"><a href="/dashboard">Back to Dashboard</a></p>
</div>
</main>
</div>
</body>
</html>
