<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Created Rooms - FocusSphere</title>
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
    </aside>

<main class="page page-with-sidebar">
    <div class="card">
        <h2>Your Created Rooms</h2>
        <p class="subtext">Rooms you created are shown here.</p>

        <c:if test="${empty createdRooms}">
            <p class="muted">You have not created any rooms yet.</p>
        </c:if>
        <c:if test="${not empty createdRooms}">
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Room</th>
                        <th>Description</th>
                        <th>Visibility</th>
                        <th>Chat</th>
                        <th>Activity</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="room" items="${createdRooms}">
                        <tr>
                            <td>${room.roomName}</td>
                            <td>${empty room.roomDescription ? 'No description provided.' : room.roomDescription}</td>
                            <td>${empty room.visibility ? 'PUBLIC' : room.visibility}</td>
                            <td><a href="/rooms/${room.roomCode}">Open Chat</a></td>
                            <td><a href="/rooms/${room.roomCode}/activity">View Activity</a></td>
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