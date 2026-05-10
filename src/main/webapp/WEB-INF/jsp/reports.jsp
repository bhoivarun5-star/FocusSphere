<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>FocusSphere - Reports</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
    <style>
        .report-selector { display: flex; gap: 10px; margin-bottom: 20px; align-items: flex-end; flex-wrap: wrap; }
        .report-selector select { padding: 8px 12px; border: 1px solid rgba(180, 196, 255, 0.18); border-radius: 12px; background: rgba(7, 12, 49, 0.8); color: #ECF2FF; cursor: pointer; font-weight: 600; }
        .report-selector select:focus { border-color: rgba(202, 169, 243, 0.52); box-shadow: 0 0 0 3px rgba(121, 151, 230, 0.18); outline: none; }
        .report-selector button { padding: 10px 16px; font-weight: 700; background: linear-gradient(165deg, rgba(202, 169, 243, 0.42), rgba(179, 122, 212, 0.36), rgba(32, 106, 188, 0.9)); color: #F5FAFF; border: 1px solid rgba(202, 169, 243, 0.14); border-radius: 12px; cursor: pointer; transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease, filter 0.18s ease; box-shadow: 0 8px 18px rgba(8, 18, 64, 0.32); }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-bottom: 20px; }
        .stat-box { background: rgba(7, 12, 49, 0.8); border-left: 4px solid #7997E6; padding: 15px; border-radius: 8px; box-shadow: 0 0 14px rgba(121, 151, 230, 0.08); }
        .stat-label { font-size: 0.85rem; color: rgba(220, 229, 255, 0.54); margin-bottom: 5px; text-transform: uppercase; }
        .stat-value { font-size: 1.8rem; font-weight: 800; color: #CAA9F3; }
        .report-card { background: rgba(20, 31, 96, 0.48); border: 1px solid rgba(180, 196, 255, 0.14); border-radius: 12px; padding: 20px; margin-bottom: 20px; }
        .empty-report { text-align: center; padding: 40px 20px; color: rgba(220, 229, 255, 0.54); }
        label { font-weight: 600; font-size: 0.95rem; }
        .month-year-selector { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
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
        <div class="sidebar-note">Track your focus activity and growth over time.</div>
    </aside>

<main class="page page-with-sidebar">
    <div class="page-topbar">
        <a class="profile-avatar" href="/profile" aria-label="Open profile" title="Profile">
            <span class="profile-avatar-inner">P</span>
        </a>
    </div>

    <div class="card">
        <h2>Monthly Activity Reports</h2>
        <p class="subtext">View detailed summaries of your focus sessions and room activities by month.</p>

        <!-- Report Selector -->
        <div class="report-selector">
            <div class="month-year-selector">
                <label for="monthSelect">Select Month:</label>
                <select id="monthSelect" name="month">
                    <c:forEach var="i" begin="1" end="12">
                        <option value="${i}" ${i == selectedMonth ? 'selected' : ''}>
                            <c:choose>
                                <c:when test="${i == 1}">January</c:when>
                                <c:when test="${i == 2}">February</c:when>
                                <c:when test="${i == 3}">March</c:when>
                                <c:when test="${i == 4}">April</c:when>
                                <c:when test="${i == 5}">May</c:when>
                                <c:when test="${i == 6}">June</c:when>
                                <c:when test="${i == 7}">July</c:when>
                                <c:when test="${i == 8}">August</c:when>
                                <c:when test="${i == 9}">September</c:when>
                                <c:when test="${i == 10}">October</c:when>
                                <c:when test="${i == 11}">November</c:when>
                                <c:when test="${i == 12}">December</c:when>
                            </c:choose>
                        </option>
                    </c:forEach>
                </select>
                
                <label for="yearSelect">Select Year:</label>
                <select id="yearSelect" name="year">
                    <option value="2024" ${selectedYear == 2024 ? 'selected' : ''}>2024</option>
                    <option value="2025" ${selectedYear == 2025 ? 'selected' : ''}>2025</option>
                    <option value="2026" ${selectedYear == 2026 ? 'selected' : ''}>2026</option>
                    <option value="2027" ${selectedYear == 2027 ? 'selected' : ''}>2027</option>
                </select>
                
                <button class="btn" onclick="generateReport()">Generate Report</button>
            </div>
        </div>

        <!-- Current Report -->
        <div id="current-report">
            <div class="empty-report"><p>Select a month and year to view your report</p></div>
        </div>
    </div>
</main>
</div>

<script>
function generateReport() {
    const month = document.getElementById('monthSelect').value;
    const year = document.getElementById('yearSelect').value;
    
    fetch(`/api/reports/current?month=${month}&year=${year}`)
        .then(response => response.json())
        .then(data => {
            if (data && data.id) {
                displayReport(data);
            } else {
                document.getElementById('current-report').innerHTML = '<div class="empty-report"><p>No report data available for this period</p></div>';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('current-report').innerHTML = '<div class="empty-report"><p>Unable to load report</p></div>';
        });
}

function displayReport(report) {
    const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                       'July', 'August', 'September', 'October', 'November', 'December'];
    
    let html = `
        <div class="report-card">
            <h3>${monthNames[report.reportMonth - 1]} ${report.reportYear} Report</h3>
            <div class="stats-grid">
                <div class="stat-box">
                    <div class="stat-label">Total Sessions</div>
                    <div class="stat-value">${report.totalSessions || 0}</div>
                </div>
                <div class="stat-box">
                    <div class="stat-label">Total Focus Minutes</div>
                    <div class="stat-value">${report.totalFocusMinutes || 0}</div>
                </div>
                <div class="stat-box">
                    <div class="stat-label">Average Duration</div>
                    <div class="stat-value">${report.averageDurationMinutes || 0}</div>
                </div>
                <div class="stat-box">
                    <div class="stat-label">Longest Session</div>
                    <div class="stat-value">${report.maxDurationMinutes || 0}</div>
                </div>
            </div>
            <div style="margin-top: 20px;">
                <h4>📊 Activity Details</h4>
                <div style="background: rgba(7, 12, 49, 0.8); padding: 15px; border-radius: 8px; white-space: pre-wrap; font-family: monospace; font-size: 0.9rem; color: rgba(220, 229, 255, 0.78);">
                    ${report.sessionDetails || 'No activity recorded'}
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('current-report').innerHTML = html;
}
</script>
</body>
</html>
    <style>
        .report-selector {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
            align-items: flex-end;
        }
        
        .report-selector select {
            padding: 8px 12px;
            border: 1px solid rgba(180, 196, 255, 0.18);
            border-radius: 12px;
            background: rgba(7, 12, 49, 0.8);
            color: #ECF2FF;
            cursor: pointer;
            font-weight: 600;
        }
        
        .report-selector select:focus {
            border-color: rgba(202, 169, 243, 0.52);
            box-shadow: 0 0 0 3px rgba(121, 151, 230, 0.18), 0 0 22px rgba(121, 151, 230, 0.18);
            outline: none;
        }
        
        .report-selector button {
            padding: 10px 16px;
            font-weight: 700;
            background: linear-gradient(165deg, rgba(202, 169, 243, 0.42), rgba(179, 122, 212, 0.36), rgba(32, 106, 188, 0.9));
            color: #F5FAFF;
            border: 1px solid rgba(202, 169, 243, 0.14);
            border-radius: 12px;
            cursor: pointer;
            transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease, filter 0.18s ease;
            box-shadow: 0 8px 18px rgba(8, 18, 64, 0.32);
        }
        
        .report-selector button:hover {
            transform: translateY(-1px);
            border-color: rgba(202, 169, 243, 0.28);
            box-shadow: 0 10px 22px rgba(8, 18, 64, 0.34);
            filter: brightness(1.04);
        }
        
        .report-card {
            background: rgba(20, 31, 96, 0.48);
            border: 1px solid rgba(180, 196, 255, 0.14);
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: var(--glow-soft);
        }
        
        .report-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            border-bottom: 2px solid rgba(121, 151, 230, 0.28);
            padding-bottom: 10px;
        }
        
        .report-title {
            font-size: 1.3rem;
            font-weight: 600;
            color: #ECF2FF;
        }
        
        .report-generated {
            font-size: 0.85rem;
            color: rgba(220, 229, 255, 0.54);
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 20px;
        }
        
        .stat-box {
            background: rgba(7, 12, 49, 0.8);
            border-left: 4px solid #7997E6;
            padding: 15px;
            border-radius: 8px;
            border: 1px solid rgba(180, 196, 255, 0.14);
            box-shadow: 0 0 14px rgba(121, 151, 230, 0.08);
        }
        
        .stat-label {
            font-size: 0.85rem;
            color: rgba(220, 229, 255, 0.54);
            margin-bottom: 5px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .stat-value {
            font-size: 1.8rem;
            font-weight: 800;
            color: #CAA9F3;
        }
        
        .details-section {
            margin-top: 20px;
        }
        
        .details-header {
            font-size: 1rem;
            font-weight: 600;
            margin-bottom: 10px;
            color: #ECF2FF;
            border-bottom: 1px solid rgba(180, 196, 255, 0.14);
            padding-bottom: 5px;
        }
        
        .details-content {
            background: rgba(7, 12, 49, 0.8);
            padding: 15px;
            border-radius: 8px;
            border: 1px solid rgba(180, 196, 255, 0.14);
            white-space: pre-wrap;
            font-family: monospace;
            font-size: 0.9rem;
            color: rgba(220, 229, 255, 0.78);
            max-height: 400px;
            overflow-y: auto;
        }
        
        .empty-report {
            text-align: center;
            padding: 40px 20px;
            color: rgba(220, 229, 255, 0.54);
        }
        
        .previous-reports {
            margin-top: 30px;
        }
        
        .previous-reports h3 {
            margin-bottom: 15px;
            color: #ECF2FF;
        }
        
        .report-list-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px;
            background: rgba(20, 31, 96, 0.48);
            margin-bottom: 8px;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.2s;
            border: 1px solid rgba(180, 196, 255, 0.14);
        }
        
        .report-list-item:hover {
            background: rgba(30, 45, 120, 0.58);
            box-shadow: 0 0 18px rgba(121, 151, 230, 0.14);
            transform: translateX(2px);
        }
        
        .report-info {
            flex: 1;
        }
        
        .report-info-title {
            font-weight: 600;
            color: #ECF2FF;
        }
        
        .report-info-date {
            font-size: 0.85rem;
            color: rgba(220, 229, 255, 0.54);
        }
        
        .month-year-selector {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        label {
            margin: 0;
            font-weight: 600;
            font-size: 0.95rem;
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
        <div class="sidebar-note">Track your focus activity and growth over time.</div>
    </aside>

<main class="page page-with-sidebar">
    <div class="page-topbar">
        <a class="profile-avatar" href="/profile" aria-label="Open profile" title="Profile">
            <span class="profile-avatar-inner">P</span>
        </a>
    </div>

    <div class="card">
        <h2>Monthly Activity Reports</h2>
        <p class="subtext">View detailed summaries of your focus sessions and room activities by month.</p>

        <c:if test="${not empty sessionScope.flashFeatureMessage}">
            <div class="msg ok">${sessionScope.flashFeatureMessage}</div>
            <c:remove var="flashFeatureMessage" scope="session" />
        </c:if>

        <!-- Report Selector -->
        <div class="report-selector">
            <div class="month-year-selector">
                <label for="monthSelect">Select Month:</label>
                <select id="monthSelect" name="month">
                    <c:forEach var="entry" items="${monthNames}">
                        <option value="${entry.key}" ${entry.key == selectedMonth ? 'selected' : ''}>
                            ${entry.value}
                        </option>
                    </c:forEach>
                </select>
                
                <label for="yearSelect" style="margin-left: 10px;">Select Year:</label>
                <select id="yearSelect" name="year">
                    <option value="2024" ${selectedYear == 2024 ? 'selected' : ''}>2024</option>
                    <option value="2025" ${selectedYear == 2025 ? 'selected' : ''}>2025</option>
                    <option value="2026" ${selectedYear == 2026 ? 'selected' : ''}>2026</option>
                    <option value="2027" ${selectedYear == 2027 ? 'selected' : ''}>2027</option>
                </select>
                
                <button class="btn" onclick="generateReport()">Generate Report</button>
            </div>
        </div>

        <!-- Current Report -->
        <div id="current-report">
            <!-- Report will be loaded here via JavaScript -->
        </div>
    </div>
</main>
</div>

<script>
function generateReport() {
    const month = document.getElementById('monthSelect').value;
    const year = document.getElementById('yearSelect').value;
    
    fetch(`/api/reports/current?month=${month}&year=${year}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data) {
            displayReport(data);
        } else {
            document.getElementById('current-report').innerHTML = '<div class="empty-report"><p>No report data available</p></div>';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById('current-report').innerHTML = '<div class="empty-report"><p>Could not load report</p></div>';
    });
}

function displayReport(report) {
    const monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"];
    
    const generatedDate = new Date(report.generatedAt).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    
    let html = `
        <div class="report-card">
            <div class="report-header">
                <div>
                    <div class="report-title">
                        ${monthNames[report.reportMonth - 1]} ${report.reportYear} Report
                    </div>
                    <div class="report-generated">
                        Generated on ${generatedDate}
                    </div>
                </div>
            </div>

            <div class="stats-grid">
                <div class="stat-box">
                    <div class="stat-label">Total Sessions</div>
                    <div class="stat-value">${report.totalSessions}</div>
                </div>
                
                <div class="stat-box">
                    <div class="stat-label">Total Focus Minutes</div>
                    <div class="stat-value">${report.totalFocusMinutes}</div>
                </div>
                
                <div class="stat-box">
                    <div class="stat-label">Average Duration</div>
                    <div class="stat-value">${report.averageDurationMinutes}</div>
                </div>
                
                <div class="stat-box">
                    <div class="stat-label">Longest Session</div>
                    <div class="stat-value">${report.maxDurationMinutes}</div>
                </div>
            </div>

            <div class="details-section">
                <div class="details-header">📊 Activity Details & Room Breakdown</div>
    `;
    
    if (!report.sessionDetails || report.sessionDetails.trim() === '') {
        html += `
                <div class="empty-report">
                    <p>No activity recorded for this month.</p>
                    <p>Participate in focus rooms to generate activity data.</p>
                </div>
        `;
    } else {
        html += `
                <div class="details-content">${escapeHtml(report.sessionDetails)}</div>
        `;
    }
    
    html += `
            </div>
        </div>
    `;
    
    document.getElementById('current-report').innerHTML = html;
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

// Load report on page load
document.addEventListener('DOMContentLoaded', function() {
    const month = document.getElementById('monthSelect').value;
    const year = document.getElementById('yearSelect').value;
    
    if (month && year) {
        generateReport();
    }
});
</script>
</body>
</html>