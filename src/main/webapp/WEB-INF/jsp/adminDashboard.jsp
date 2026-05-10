<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>FocusSphere - Admin Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
</head>
<body>
<div class="app-shell">
    <aside class="sidebar">
        <div class="sidebar-brand">FocusSphere</div>
        <details class="sidebar-card" open>
            <summary>Admin Menu</summary>
            <nav class="sidebar-nav">
                <a class="${activeSection == 'dashboard' ? 'is-active' : ''}" href="/admin/dashboard">Available Rooms</a>
                <a class="${activeSection == 'users' ? 'is-active' : ''}" href="/admin/users">Manage Users</a>
                <a class="${activeSection == 'rooms' ? 'is-active' : ''}" href="/admin/rooms">Monitor Rooms</a>
                <a class="${activeSection == 'analytics' ? 'is-active' : ''}" href="/admin/analytics">Analytics</a>
                <a class="${activeSection == 'system-report' ? 'is-active' : ''}" href="/admin/system-report">System Report</a>
                <a href="/logout">Logout</a>
            </nav>
        </details>
        <div class="sidebar-note">Administrative monitoring with reduced visual noise.</div>
    </aside>

<main class="page page-with-sidebar">
<div class="card">
    <c:choose>
        <c:when test="${activeSection == 'users'}">
            <h2>Manage Users</h2>
            <p class="subtext">All registered users are shown here.</p>
            <c:if test="${empty users}">
                <p class="muted">No users found.</p>
            </c:if>
            <c:if test="${not empty users}">
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Roll No</th>
                            <th>Role</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>${u.name}</td>
                                <td>${u.email}</td>
                                <td>${u.phone}</td>
                                <td>${u.rollNo}</td>
                                <td>${u.role}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </c:when>
        <c:when test="${activeSection == 'rooms'}">
            <h2>Monitor Rooms</h2>
            <p class="subtext">Room activity and visibility overview.</p>
            <c:if test="${empty roomMonitorRows}">
                <p class="muted">No rooms found.</p>
            </c:if>
            <c:if test="${not empty roomMonitorRows}">
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Room Name</th>
                            <th>Visibility</th>
                            <th>Room Code</th>
                            <th>Created By</th>
                            <th>Members</th>
                            <th>Messages</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="row" items="${roomMonitorRows}">
                            <tr>
                                <td><a href="/rooms/${row.roomCode}">${row.roomName}</a></td>
                                <td>${row.visibility}</td>
                                <td>${row.roomCode}</td>
                                <td>${row.createdBy}</td>
                                <td>${row.memberCount}</td>
                                <td>${row.messageCount}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </c:when>
        <c:when test="${activeSection == 'analytics'}">
            <h2>Analytics</h2>
            <p class="subtext">Summary metrics for the platform.</p>
            <div class="grid-three" style="margin-bottom: 14px;">
                <div class="stat-card"><div class="stat-label">Total Users</div><div class="stat-value">${analytics.totalUsers}</div></div>
                <div class="stat-card"><div class="stat-label">Students</div><div class="stat-value">${analytics.studentUsers}</div></div>
                <div class="stat-card"><div class="stat-label">Admins</div><div class="stat-value">${analytics.adminUsers}</div></div>
                <div class="stat-card"><div class="stat-label">Total Rooms</div><div class="stat-value">${analytics.totalRooms}</div></div>
                <div class="stat-card"><div class="stat-label">Public / Private Rooms</div><div class="stat-value">${analytics.publicRooms} / ${analytics.privateRooms}</div></div>
                <div class="stat-card"><div class="stat-label">Messages</div><div class="stat-value">${analytics.totalMessages}</div></div>
                <div class="stat-card"><div class="stat-label">Memberships</div><div class="stat-value">${analytics.totalMemberships}</div></div>
                <div class="stat-card"><div class="stat-label">Active Sessions</div><div class="stat-value">${analytics.activeSessions}</div></div>
                <div class="stat-card"><div class="stat-label">Sessions Today</div><div class="stat-value">${analytics.sessionsToday}</div></div>
            </div>
        </c:when>
        <c:when test="${activeSection == 'system-report'}">
            <h2>System Report</h2>
            <p class="subtext">Operational and analytical snapshot for administrative decisions.</p>
            <div class="grid-three" style="margin-bottom: 14px;">
                <div class="stat-card"><div class="stat-label">Join Approval Rate</div><div class="stat-value">${systemReport.joinApprovalRate}</div></div>
                <div class="stat-card"><div class="stat-label">Avg Members / Room</div><div class="stat-value">${systemReport.avgMembersPerRoom}</div></div>
                <div class="stat-card"><div class="stat-label">Avg Messages / Room</div><div class="stat-value">${systemReport.avgMessagesPerRoom}</div></div>
                <div class="stat-card"><div class="stat-label">Avg Session Duration</div><div class="stat-value">${systemReport.avgSessionDurationMinutes} min</div></div>
                <div class="stat-card"><div class="stat-label">Schedules Today</div><div class="stat-value">${systemReport.schedulesToday}</div></div>
                <div class="stat-card"><div class="stat-label">Schedules Next 7 Days</div><div class="stat-value">${systemReport.schedulesNextWeek}</div></div>
            </div>
            <div class="table-wrap" style="margin-bottom: 14px;">
                <table>
                    <thead>
                    <tr>
                        <th>Metric</th>
                        <th>Value</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr><td>Generated At</td><td>${systemReport.generatedAt}</td></tr>
                    <tr><td>Pending Join Requests</td><td>${systemReport.pendingJoinRequests}</td></tr>
                    <tr><td>Approved Join Requests</td><td>${systemReport.approvedJoinRequests}</td></tr>
                    <tr><td>Rejected Join Requests</td><td>${systemReport.rejectedJoinRequests}</td></tr>
                    <tr><td>Join Approval Rate</td><td>${systemReport.joinApprovalRate}</td></tr>
                    <tr><td>Upcoming Schedules</td><td>${systemReport.upcomingSchedules}</td></tr>
                    <tr><td>Schedules Today</td><td>${systemReport.schedulesToday}</td></tr>
                    <tr><td>Schedules Next 7 Days</td><td>${systemReport.schedulesNextWeek}</td></tr>
                    <tr><td>Active Focus Sessions</td><td>${systemReport.activeSessions}</td></tr>
                    <tr><td>Average Members Per Room</td><td>${systemReport.avgMembersPerRoom}</td></tr>
                    <tr><td>Average Messages Per Room</td><td>${systemReport.avgMessagesPerRoom}</td></tr>
                    <tr><td>Average Session Duration (minutes)</td><td>${systemReport.avgSessionDurationMinutes}</td></tr>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <h2>Available Rooms</h2>
            <p class="subtext">Public rooms available for monitoring.</p>
            <c:if test="${not empty sessionScope.flashAdminMessage}">
                <div class="msg ok">${sessionScope.flashAdminMessage}</div>
                <c:remove var="flashAdminMessage" scope="session" />
            </c:if>
            <c:if test="${empty availableRooms}">
                <p class="muted">No rooms found.</p>
            </c:if>
            <c:if test="${not empty availableRooms}">
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Room Name</th>
                            <th>Description</th>
                            <th>Visibility</th>
                            <th>Created By</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="room" items="${availableRooms}">
                            <tr>
                                <td><a href="/rooms/${room.roomCode}">${room.roomName}</a></td>
                                <td>${empty room.roomDescription ? 'No description provided.' : room.roomDescription}</td>
                                <td>${empty room.visibility ? 'PUBLIC' : room.visibility}</td>
                                <td>${room.createdBy.name}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>
</main>
</div>
</body>
</html>
