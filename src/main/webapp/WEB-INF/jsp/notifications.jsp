<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>FocusSphere - Notifications</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
    <style>
        .notification-item {
            border-left: 4px solid #7997E6;
            padding: 12px;
            margin-bottom: 8px;
            background: rgba(20, 31, 96, 0.48);
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.2s ease;
            border: 1px solid rgba(180, 196, 255, 0.14);
        }
        
        .notification-item:hover {
            background: rgba(30, 45, 120, 0.58);
            box-shadow: 0 0 18px rgba(121, 151, 230, 0.14);
        }
        
        .notification-item.unread {
            background: rgba(121, 151, 230, 0.18);
            border-color: rgba(202, 169, 243, 0.28);
            font-weight: 500;
        }
        
        .notification-item.type-message {
            border-left-color: #7997E6;
        }
        
        .notification-item.type-join-request {
            border-left-color: #F5C76A;
        }
        
        .notification-item.type-accepted {
            border-left-color: #37D6A3;
        }
        
        .notification-title {
            font-weight: 600;
            margin-bottom: 4px;
            color: #ECF2FF;
        }
        
        .notification-message {
            font-size: 0.9rem;
            color: rgba(220, 229, 255, 0.78);
            margin-bottom: 4px;
        }
        
        .notification-meta {
            font-size: 0.8rem;
            color: rgba(220, 229, 255, 0.54);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .notification-actions {
            display: flex;
            gap: 8px;
            margin-top: 8px;
        }
        
        .notification-actions button {
            padding: 10px 16px;
            font-size: 0.85rem;
            font-weight: 700;
            background: linear-gradient(165deg, rgba(202, 169, 243, 0.42), rgba(179, 122, 212, 0.36), rgba(32, 106, 188, 0.9));
            border: 1px solid rgba(202, 169, 243, 0.14);
            border-radius: 12px;
            cursor: pointer;
            transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease, filter 0.18s ease;
            color: #F5FAFF;
            box-shadow: 0 8px 18px rgba(8, 18, 64, 0.32);
        }
        
        .notification-actions button:hover {
            border-color: rgba(202, 169, 243, 0.28);
            box-shadow: 0 10px 22px rgba(8, 18, 64, 0.34);
            transform: translateY(-1px);
            filter: brightness(1.04);
        }
        
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: rgba(220, 229, 255, 0.54);
        }
        
        .notification-filters {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }
        
        .filter-btn {
            padding: 10px 16px;
            font-size: 0.95rem;
            font-weight: 700;
            background: linear-gradient(165deg, rgba(202, 169, 243, 0.42), rgba(179, 122, 212, 0.36), rgba(32, 106, 188, 0.9));
            border: 1px solid rgba(202, 169, 243, 0.14);
            border-radius: 12px;
            cursor: pointer;
            transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease, filter 0.18s ease;
            color: #F5FAFF;
            box-shadow: 0 8px 18px rgba(8, 18, 64, 0.32);
        }
        
        .filter-btn:hover {
            transform: translateY(-1px);
            border-color: rgba(202, 169, 243, 0.28);
            box-shadow: 0 10px 22px rgba(8, 18, 64, 0.34);
            filter: brightness(1.04);
        }
    </style>
</head>
<body>
<div class="app-shell">
    <aside class="sidebar">
        <div class="sidebar-brand">FocusSphere</div>
        <details class="sidebar-card" open>
            <summary>Main Menu</summary>
            <nav class="sidebar-nav">
                <a class="${activePage == 'dashboard' ? 'is-active' : ''}" href="/dashboard">Dashboard</a>
                <a class="${activePage == 'create' ? 'is-active' : ''}" href="/rooms/create">Create Room</a>
                <a class="${activePage == 'join' ? 'is-active' : ''}" href="/rooms/join">Join Room</a>
                <a class="${activePage == 'created' ? 'is-active' : ''}" href="/rooms/created">Created Rooms</a>
                <a class="${activePage == 'requests' ? 'is-active' : ''}" href="/requests/pending">Pending Requests</a>
                <a class="${activePage == 'notifications' ? 'is-active' : ''}" href="/notifications">Notifications</a>
                <a class="${activePage == 'reports' ? 'is-active' : ''}" href="/reports">Reports</a>
                <a href="/logout">Logout</a>
            </nav>
        </details>
        <div class="sidebar-note">Stay updated with room activities and join requests.</div>
    </aside>

<main class="page page-with-sidebar">
    <div class="page-topbar">
        <a class="profile-avatar" href="/profile" aria-label="Open profile" title="Profile">
            <span class="profile-avatar-inner">P</span>
        </a>
    </div>

    <div class="card">
        <h2>Notifications</h2>
        <p class="subtext">Messages from focus rooms you're part of and join requests for your created rooms.</p>

        <c:if test="${not empty sessionScope.flashFeatureMessage}">
            <div class="msg ok">${sessionScope.flashFeatureMessage}</div>
            <c:remove var="flashFeatureMessage" scope="session" />
        </c:if>

        <div class="notification-filters">
            <button class="filter-btn active" onclick="filterNotifications('all')">All</button>
            <button class="filter-btn" onclick="filterNotifications('unread')">Unread (${unreadCount})</button>
            <button class="filter-btn" onclick="filterNotifications('message')">Messages</button>
            <button class="filter-btn" onclick="filterNotifications('join-request')">Join Requests</button>
        </div>

        <div id="notifications-container">
            <c:choose>
                <c:when test="${empty notifications}">
                    <div class="empty-state">
                        <p>📭 No notifications yet</p>
                        <p style="font-size: 0.9rem;">You'll see messages from your rooms and join requests here.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="notification" items="${notifications}">
                        <div class="notification-item ${!notification.isRead ? 'unread' : ''} type-${notification.type.toString().toLowerCase().replace('_', '-')}" 
                             data-type="${notification.type.toString().toLowerCase().replace('_', '-')}"
                             data-read="${notification.isRead}">
                            <div class="notification-title">${notification.title}</div>
                            <div class="notification-message">${notification.message}</div>
                            <div class="notification-meta">
                                <span>
                                    <c:if test="${not empty notification.room}">
                                        Room: <strong>${notification.room.roomName}</strong> • 
                                    </c:if>
                                    <fmt:formatDate value="${notification.createdAt}" pattern="MMM dd, HH:mm" />
                                </span>
                            </div>
                            <c:if test="${not notification.isRead}">
                                <div class="notification-actions">
                                    <button onclick="markAsRead(${notification.id})">Mark as Read</button>
                                    <button onclick="deleteNotification(${notification.id})">Delete</button>
                                </div>
                            </c:if>
                            <c:if test="${notification.isRead}">
                                <div class="notification-actions">
                                    <button onclick="deleteNotification(${notification.id})">Delete</button>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>
</div>

<script>
function markAsRead(notificationId) {
    fetch(`/api/notifications/${notificationId}/read`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert('Error: ' + data.message);
        }
    })
    .catch(error => console.error('Error:', error));
}

function deleteNotification(notificationId) {
    if (confirm('Are you sure you want to delete this notification?')) {
        fetch(`/api/notifications/${notificationId}/delete`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                location.reload();
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => console.error('Error:', error));
    }
}

function filterNotifications(filter) {
    const items = document.querySelectorAll('.notification-item');
    const buttons = document.querySelectorAll('.filter-btn');
    
    // Update active button
    buttons.forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    // Filter items
    items.forEach(item => {
        if (filter === 'all') {
            item.style.display = '';
        } else if (filter === 'unread') {
            item.style.display = item.dataset.read === 'false' ? '' : 'none';
        } else if (filter === 'message') {
            item.style.display = item.dataset.type === 'message' ? '' : 'none';
        } else if (filter === 'join-request') {
            item.style.display = item.dataset.type === 'join-request' ? '' : 'none';
        }
    });
}
</script>
</body>
</html>