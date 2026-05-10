<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Pending Requests - FocusSphere</title>
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
        <h2>Pending Requests</h2>
        <p class="subtext">Requests waiting for your approval.</p>

        <c:if test="${empty pendingRequestsForMyRooms}">
            <p class="muted">No pending requests right now.</p>
        </c:if>
        <c:if test="${not empty pendingRequestsForMyRooms}">
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Room</th>
                        <th>Requester</th>
                        <th>Requested At</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="req" items="${pendingRequestsForMyRooms}">
                        <tr>
                            <td>${req.room.roomName}</td>
                            <td>${req.requester.name} (${req.requester.email})</td>
                            <td>${req.requestedAt}</td>
                            <td>
                                <form method="post" action="/rooms/requests/${req.id}/approve" style="display:inline;">
                                    <button class="btn btn-success" type="submit">Approve</button>
                                </form>
                                <form method="post" action="/rooms/requests/${req.id}/reject" style="display:inline; margin-left:8px;">
                                    <button class="btn btn-secondary" type="submit">Reject</button>
                                </form>
                            </td>
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