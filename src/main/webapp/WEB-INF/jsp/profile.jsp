<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Profile - FocusSphere</title>
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
        <h2>User Profile</h2>
        <p class="subtext">View your account details and update your profile information.</p>

        <c:if test="${not empty success}">
            <div class="msg ok">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="msg err">${error}</div>
        </c:if>

        <div class="grid-two">
            <div class="card profile-info-card">
                <h3>Profile Details</h3>
                <p><strong>Name:</strong> ${user.name}</p>
                <p><strong>Email:</strong> ${user.email}</p>
                <p><strong>Join Date:</strong> ${empty user.joinDate ? '-' : user.joinDate}</p>
                <a class="btn" href="/focus-stats">View Focus Stats</a>
            </div>

            <div class="card profile-form-card">
                <h3>Update Profile</h3>
                <form method="post" action="/profile/update">
                    <label>Name</label>
                    <input type="text" name="name" value="${user.name}" required />

                    <label>Email</label>
                    <input type="email" name="email" value="${user.email}" required />

                    <button class="btn" type="submit" style="margin-top: 12px;">Update Profile</button>
                </form>
            </div>
        </div>

        <div class="card" style="margin-top: 16px;">
            <h3>Rooms You Are Part Of</h3>
            <c:if test="${empty currentJoinedRooms}">
                <p class="muted">You are not currently part of any room.</p>
            </c:if>
            <c:if test="${not empty currentJoinedRooms}">
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Room</th>
                            <th>Visibility</th>
                            <th>Open</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="room" items="${currentJoinedRooms}">
                            <tr>
                                <td>${room.roomName}</td>
                                <td>${empty room.visibility ? 'PUBLIC' : room.visibility}</td>
                                <td><a href="/rooms/${room.roomCode}">Open Chat</a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>

        <div class="card" style="margin-top: 16px;">
            <h3>Room Join/Leave History</h3>
            <c:if test="${empty roomMembershipHistory}">
                <p class="muted">No room history available yet.</p>
            </c:if>
            <c:if test="${not empty roomMembershipHistory}">
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Room</th>
                            <th>Joined On</th>
                            <th>Left On</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="entry" items="${roomMembershipHistory}">
                            <tr>
                                <td>${entry.room.roomName}</td>
                                <td>${entry.joinedAt}</td>
                                <td>${entry.leftAt == null ? '-' : entry.leftAt}</td>
                                <td>${entry.leftAt == null ? 'Currently Joined' : 'Left Room'}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
    </div>
</main>
</div>
</body>
</html>
