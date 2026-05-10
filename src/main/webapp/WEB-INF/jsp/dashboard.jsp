<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>FocusSphere - Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
</head>
<body>
<div class="app-shell">
    <aside class="sidebar">
        <div class="sidebar-brand">FocusSphere</div>
        <details class="sidebar-card" open>
            <summary>${isAdmin ? 'Admin Menu' : 'Main Menu'}</summary>
            <nav class="sidebar-nav">
                <a class="${activePage == 'dashboard' ? 'is-active' : ''}" href="${isAdmin ? '/admin/dashboard' : '/dashboard'}">Dashboard</a>
                <c:if test="${not isAdmin}">
                    <a class="${activePage == 'create' ? 'is-active' : ''}" href="/rooms/create">Create Room</a>
                    <a class="${activePage == 'join' ? 'is-active' : ''}" href="/rooms/join">Join Room</a>
                    <a class="${activePage == 'created' ? 'is-active' : ''}" href="/rooms/created">Created Rooms</a>
                    <a class="${activePage == 'requests' ? 'is-active' : ''}" href="/requests/pending">Pending Requests</a>
                    <a class="${activePage == 'notifications' ? 'is-active' : ''}" href="/notifications">Notifications</a>
                    <a class="${activePage == 'reports' ? 'is-active' : ''}" href="/reports">Reports</a>
                </c:if>
                <c:if test="${isAdmin}">
                    <a href="/admin/users">Manage Users</a>
                    <a href="/admin/rooms">Monitor Rooms</a>
                    <a href="/admin/analytics">Analytics</a>
                    <a href="/admin/system-report">System Report</a>
                </c:if>
                <a href="/logout">Logout</a>
            </nav>
        </details>
        <div class="sidebar-note">${isAdmin ? 'You can browse rooms like a user and still access admin controls from the same space.' : 'Calm focus space, quick navigation, and soft bioluminescent glow.'}</div>
    </aside>

<main class="page page-with-sidebar">
    <div class="page-topbar">
        <a class="profile-avatar" href="/profile" aria-label="Open profile" title="Profile">
            <span class="profile-avatar-inner">P</span>
        </a>
    </div>
    <div class="card">
        <h2>Available Rooms</h2>
        <p class="subtext">Discover rooms with quick stats before joining. Use sort and filter to find the right room faster.</p>

        <c:if test="${not empty sessionScope.flashFeatureMessage}">
            <div class="msg ok">${sessionScope.flashFeatureMessage}</div>
            <c:remove var="flashFeatureMessage" scope="session" />
        </c:if>

        <form method="get" action="${isAdmin ? '/admin/dashboard' : '/dashboard'}" class="table-controls">
            <div>
                <label for="search">Search</label>
                <input id="search" type="text" name="search" value="${search}" placeholder="Search by room, creator, topic..." />
            </div>
            <div>
                <label for="sortBy">Sort By</label>
                <select id="sortBy" name="sortBy">
                    <option value="name-asc" ${sortBy == 'name-asc' ? 'selected' : ''}>Room Name (A-Z)</option>
                    <option value="name-desc" ${sortBy == 'name-desc' ? 'selected' : ''}>Room Name (Z-A)</option>
                    <option value="members-desc" ${sortBy == 'members-desc' ? 'selected' : ''}>Most Members</option>
                    <option value="members-asc" ${sortBy == 'members-asc' ? 'selected' : ''}>Least Members</option>
                    <option value="messages-desc" ${sortBy == 'messages-desc' ? 'selected' : ''}>Most Active (Messages)</option>
                    <option value="visibility" ${sortBy == 'visibility' ? 'selected' : ''}>Visibility</option>
                </select>
            </div>
            <div>
                <label for="visibilityFilter">Visibility</label>
                <select id="visibilityFilter" name="visibilityFilter">
                    <option value="all" ${visibilityFilter == 'all' ? 'selected' : ''}>All</option>
                    <option value="public" ${visibilityFilter == 'public' ? 'selected' : ''}>Public</option>
                    <option value="private" ${visibilityFilter == 'private' ? 'selected' : ''}>Private</option>
                </select>
            </div>
            <div class="table-controls-action">
                <button class="btn btn-secondary" type="submit">Apply</button>
            </div>
        </form>

        <c:if test="${empty roomDiscoveryRows}">
            <p class="muted">No available rooms right now.</p>
        </c:if>
        <c:if test="${not empty roomDiscoveryRows}">
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Room</th>
                        <th>Description</th>
                        <th>Visibility</th>
                        <th>Created By</th>
                        <th>Members</th>
                        <th>Messages</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="room" items="${roomDiscoveryRows}">
                        <tr>
                            <td><a href="/rooms/${room.roomCode}">${room.roomName}</a></td>
                            <td>${empty room.description ? 'No description provided.' : room.description}</td>
                            <td>${empty room.visibility ? 'PUBLIC' : room.visibility}</td>
                            <td>${room.createdBy}</td>
                            <td>${room.memberCount}</td>
                            <td>${room.messageCount}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${room.owner or isAdmin}">
                                        <a class="btn" href="/rooms/${room.roomCode}">Open Chat</a>
                                        <a class="btn btn-secondary" href="/rooms/${room.roomCode}/activity">Activity</a>
                                    </c:when>
                                    <c:otherwise>
                                        <form method="post" action="/rooms/${room.roomCode}/request-join" style="display:inline;">
                                            <button class="btn btn-success" type="submit">Request Join</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
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
