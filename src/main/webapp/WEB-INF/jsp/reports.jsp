<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>FocusSphere - Monthly Reports</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
    <style>
        .report-selector { 
            display: flex; gap: 10px; margin-bottom: 20px; align-items: flex-end; flex-wrap: wrap; 
        }
        .report-selector select { 
            padding: 8px 12px; border: 1px solid rgba(180, 196, 255, 0.18); 
            border-radius: 12px; background: rgba(7, 12, 49, 0.8); 
            color: #ECF2FF; cursor: pointer; font-weight: 600; 
            transition: all 0.3s ease;
        }
        .report-selector select:hover { 
            border-color: rgba(202, 169, 243, 0.52);
            box-shadow: 0 0 12px rgba(121, 151, 230, 0.2);
        }
        .report-selector select:focus { 
            border-color: rgba(202, 169, 243, 0.52); 
            box-shadow: 0 0 0 3px rgba(121, 151, 230, 0.18); 
            outline: none; 
        }
        .report-selector button { 
            padding: 10px 16px; font-weight: 700; 
            background: linear-gradient(165deg, rgba(202, 169, 243, 0.42), rgba(179, 122, 212, 0.36), rgba(32, 106, 188, 0.9)); 
            color: #F5FAFF; border: 1px solid rgba(202, 169, 243, 0.14); 
            border-radius: 12px; cursor: pointer; 
            transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease, filter 0.18s ease; 
            box-shadow: 0 8px 18px rgba(8, 18, 64, 0.32); 
        }
        .report-selector button:hover { 
            transform: translateY(-2px);
            filter: brightness(1.1);
            box-shadow: 0 12px 24px rgba(121, 151, 230, 0.3);
        }
        .report-selector button:active { 
            transform: translateY(0);
        }

        .report-container { display: none; }
        .report-container.active { display: block; }

        .report-section { 
            background: rgba(20, 31, 96, 0.48); 
            border: 1px solid rgba(180, 196, 255, 0.14); 
            border-radius: 12px; 
            padding: 20px; 
            margin-bottom: 20px;
            box-shadow: 0 4px 12px rgba(8, 18, 64, 0.18);
            transition: all 0.3s ease;
        }
        .report-section:hover {
            border-color: rgba(180, 196, 255, 0.24);
            box-shadow: 0 6px 18px rgba(121, 151, 230, 0.15);
        }

        .section-title { 
            font-size: 1.3rem; 
            font-weight: 800; 
            color: #CAA9F3; 
            margin-bottom: 15px;
        }

        .summary-grid { 
            display: grid; 
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); 
            gap: 15px; 
        }
        .summary-card { 
            background: linear-gradient(135deg, rgba(202, 169, 243, 0.08), rgba(121, 151, 230, 0.08));
            border-left: 3px solid #CAA9F3; 
            padding: 15px; 
            border-radius: 8px; 
            box-shadow: 0 0 14px rgba(121, 151, 230, 0.08);
            transition: all 0.3s ease;
        }
        .summary-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 18px rgba(121, 151, 230, 0.18);
            border-left-color: #7997E6;
        }
        .summary-label { 
            font-size: 0.8rem; 
            color: rgba(220, 229, 255, 0.64); 
            margin-bottom: 5px; 
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .summary-value { 
            font-size: 1.8rem; 
            font-weight: 800; 
            color: #CAA9F3;
            text-shadow: 0 2px 8px rgba(202, 169, 243, 0.2);
        }
        .summary-sub { 
            font-size: 0.75rem; 
            color: rgba(220, 229, 255, 0.54); 
            margin-top: 5px;
        }

        .participation-summary {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 15px;
        }
        .participation-item {
            background: rgba(7, 12, 49, 0.4);
            padding: 12px;
            border-radius: 8px;
            border: 1px solid rgba(180, 196, 255, 0.1);
        }
        .participation-label {
            font-size: 0.9rem;
            color: rgba(220, 229, 255, 0.64);
            margin-bottom: 8px;
        }
        .participation-content {
            font-size: 1.2rem;
            font-weight: 700;
            color: #7997E6;
        }
        .trend-badge {
            display: inline-block;
            background: linear-gradient(135deg, rgba(202, 169, 243, 0.2), rgba(121, 151, 230, 0.2));
            border: 1px solid rgba(202, 169, 243, 0.3);
            border-radius: 6px;
            padding: 6px 12px;
            font-size: 0.85rem;
            color: #CAA9F3;
            margin-top: 8px;
        }

        .sessions-table-wrapper {
            overflow-x: auto;
        }
        .sessions-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9rem;
        }
        .sessions-table thead {
            background: rgba(7, 12, 49, 0.6);
            border-bottom: 2px solid rgba(121, 151, 230, 0.2);
        }
        .sessions-table th {
            padding: 12px;
            text-align: left;
            font-weight: 700;
            color: #CAA9F3;
            text-transform: uppercase;
            font-size: 0.85rem;
            letter-spacing: 0.5px;
        }
        .sessions-table td {
            padding: 12px;
            border-bottom: 1px solid rgba(180, 196, 255, 0.1);
            color: rgba(220, 229, 255, 0.8);
        }
        .sessions-table tbody tr:hover {
            background: rgba(121, 151, 230, 0.08);
        }
        .session-date {
            font-weight: 600;
            color: #7997E6;
        }
        .session-room {
            color: #CAA9F3;
        }
        .session-duration {
            font-weight: 600;
        }
        .break-count {
            background: rgba(202, 169, 243, 0.1);
            border: 1px solid rgba(202, 169, 243, 0.2);
            padding: 2px 6px;
            border-radius: 4px;
            display: inline-block;
        }

        .chart-wrapper {
            position: relative;
            height: 350px;
            margin: 20px 0;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: rgba(220, 229, 255, 0.54);
        }
        .empty-state-icon {
            font-size: 3rem;
            margin-bottom: 15px;
            opacity: 0.5;
        }
        .empty-state-text {
            font-size: 1.1rem;
            margin-bottom: 8px;
        }
        .empty-state-subtext {
            font-size: 0.9rem;
            opacity: 0.7;
        }

        .consistency-indicator {
            display: flex;
            align-items: center;
            gap: 12px;
        }
        .score-bar {
            flex: 1;
            height: 8px;
            background: rgba(7, 12, 49, 0.6);
            border-radius: 4px;
            overflow: hidden;
        }
        .score-fill {
            height: 100%;
            background: linear-gradient(90deg, #7997E6, #CAA9F3);
            border-radius: 4px;
            transition: width 0.3s ease;
        }

        .rooms-list {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }
        .room-badge {
            background: rgba(121, 151, 230, 0.15);
            border: 1px solid rgba(121, 151, 230, 0.3);
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.9rem;
            color: #7997E6;
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .room-badge::before {
            content: "🏠";
            font-size: 0.9rem;
        }

        label { 
            font-weight: 600; 
            font-size: 0.95rem; 
            color: #CAA9F3;
        }
        .month-year-selector { 
            display: flex; 
            gap: 10px; 
            align-items: center; 
            flex-wrap: wrap; 
        }

        .loading {
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 60px 20px;
        }
        .spinner {
            width: 40px;
            height: 40px;
            border: 3px solid rgba(121, 151, 230, 0.2);
            border-top-color: #CAA9F3;
            border-radius: 50%;
            animation: spin 0.8s linear infinite;
        }
        @keyframes spin {
            to { transform: rotate(360deg); }
        }

        .no-data-message {
            text-align: center;
            padding: 40px;
            color: rgba(220, 229, 255, 0.54);
            font-size: 1rem;
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
                <a href="/dashboard">Dashboard</a>
                <a href="/rooms/create">Create Room</a>
                <a href="/rooms/join">Join Room</a>
                <a href="/rooms/created">Created Rooms</a>
                <a href="/requests/pending">Pending Requests</a>
                <a href="/notifications">Notifications</a>
                <a class="is-active" href="/reports">Reports</a>
                <a href="/logout">Logout</a>
            </nav>
        </details>
        <div class="sidebar-note">📊 Track your focus activity and growth trends every month.</div>
    </aside>

    <main class="page page-with-sidebar">
        <div class="page-topbar">
            <a class="profile-avatar" href="/profile" aria-label="Open profile" title="Profile">
                <span class="profile-avatar-inner">P</span>
            </a>
        </div>

        <div class="card">
            <h2>📊 Monthly Activity Reports</h2>
            <p class="subtext">Analyze your focus sessions, room participation, and productivity growth by month.</p>

            <div class="report-selector">
                <div class="month-year-selector">
                    <label for="monthSelect">Select Month:</label>
                    <select id="monthSelect" name="month">
                        <option value="1">January</option>
                        <option value="2">February</option>
                        <option value="3">March</option>
                        <option value="4">April</option>
                        <option value="5">May</option>
                        <option value="6">June</option>
                        <option value="7">July</option>
                        <option value="8">August</option>
                        <option value="9">September</option>
                        <option value="10">October</option>
                        <option value="11">November</option>
                        <option value="12">December</option>
                    </select>
                    
                    <label for="yearSelect">Select Year:</label>
                    <select id="yearSelect" name="year">
                        <option value="2024">2024</option>
                        <option value="2025">2025</option>
                        <option value="2026">2026</option>
                        <option value="2027">2027</option>
                    </select>
                    
                    <button class="btn" onclick="generateReport()">📈 Generate Report</button>
                </div>
            </div>

            <div id="report-content">
                <div class="empty-state">
                    <div class="empty-state-icon">📅</div>
                    <div class="empty-state-text">Select a month and year to view your report</div>
                    <div class="empty-state-subtext">Your focus statistics will appear here</div>
                </div>
            </div>
        </div>
    </main>
</div>

<script>
    let chart = null;

    function generateReport() {
        const month = document.getElementById('monthSelect').value;
        const year = document.getElementById('yearSelect').value;
        
        if (!month || !year) {
            alert('Please select both month and year');
            return;
        }

        const reportContent = document.getElementById('report-content');
        reportContent.innerHTML = '<div class="loading"><div class="spinner"></div></div>';

        const url = '/api/reports/monthly-data?month=' + month + '&year=' + year;
        fetch(url)
            .then(function(response) { return response.json(); })
            .then(function(data) {
                if (data && data.hasData) {
                    renderReport(data);
                } else {
                    const monthName = getMonthName(month);
                    let html = '<div class="report-container active"><div class="no-data-message">';
                    html = html + '<p>No focus activity recorded for ' + monthName + ' ' + year + '</p>';
                    html = html + '<p style="margin-top: 10px; font-size: 0.9rem;">Start a focus session to begin tracking your activity!</p>';
                    html = html + '</div></div>';
                    reportContent.innerHTML = html;
                }
            })
            .catch(function(error) {
                console.error('Error:', error);
                reportContent.innerHTML = '<div class="no-data-message">Error loading report. Please try again.</div>';
            });
    }

    function renderReport(d) {
        const reportContent = document.getElementById('report-content');
        
        const totMin = Math.floor((d.totalSessionSeconds || 0) / 60);
        const avgMin = Math.floor(d.averageSessionMinutes || 0);
        const maxMin = Math.floor((d.maxSessionSeconds || 0) / 60);

        let h = '<div class="report-container active">';
        
        h = h + '<div class="report-section">';
        h = h + '<div class="section-title">Report Summary - ' + d.monthName + ' ' + d.year + '</div>';
        h = h + '<div class="summary-grid">';
        h = h + '<div class="summary-card"><div class="summary-label">Total Sessions</div><div class="summary-value">' + (d.totalSessions || 0) + '</div><div class="summary-sub">Room activities</div></div>';
        h = h + '<div class="summary-card"><div class="summary-label">Focus Rooms Joined</div><div class="summary-value">' + (d.totalFocusRoomsJoined || 0) + '</div><div class="summary-sub">Unique rooms</div></div>';
        h = h + '<div class="summary-card"><div class="summary-label">Total Focus Time</div><div class="summary-value">' + totMin + 'm</div><div class="summary-sub">' + Math.floor(totMin / 60) + 'h ' + (totMin % 60) + 'm</div></div>';
        h = h + '<div class="summary-card"><div class="summary-label">Average Session</div><div class="summary-value">' + avgMin + 'm</div><div class="summary-sub">Per session</div></div>';
        h = h + '<div class="summary-card"><div class="summary-label">Total Breaks</div><div class="summary-value">' + (d.totalBreaks || 0) + '</div><div class="summary-sub">Rest periods</div></div>';
        h = h + '<div class="summary-card"><div class="summary-label">Longest Session</div><div class="summary-value">' + maxMin + 'm</div><div class="summary-sub">Maximum duration</div></div>';
        h = h + '</div></div>';

        if (d.focusRoomNames && d.focusRoomNames.length > 0) {
            h = h + '<div class="report-section"><div class="section-title">Focus Rooms Participated In</div><div class="rooms-list">';
            for (let r = 0; r < d.focusRoomNames.length; r++) {
                h = h + '<div class="room-badge">' + d.focusRoomNames[r] + '</div>';
            }
            h = h + '</div></div>';
        }

        h = h + '<div class="report-section"><div class="section-title">Participation Overview</div><div class="participation-summary">';
        h = h + '<div class="participation-item"><div class="participation-label">Days with Activity</div><div class="participation-content">' + (d.daysWithActivity || 0) + ' days</div><div class="trend-badge">' + (d.participationTrend || 'N/A') + '</div></div>';
        h = h + '<div class="participation-item"><div class="participation-label">Participation Rate</div><div class="participation-content">' + Math.round(d.participationPercentage || 0) + '%</div>';
        h = h + '<div style="margin-top: 8px;"><div class="consistency-indicator"><div class="score-bar"><div class="score-fill" style="width: ' + (d.participationPercentage || 0) + '%"></div></div></div></div></div>';
        h = h + '<div class="participation-item"><div class="participation-label">Consistency Score</div><div class="participation-content">' + Math.round(d.consistencyScore || 0) + '/100</div>';
        h = h + '<div style="margin-top: 8px;"><div class="consistency-indicator"><div class="score-bar"><div class="score-fill" style="width: ' + (d.consistencyScore || 0) + '%"></div></div></div></div></div>';
        h = h + '<div class="participation-item"><div class="participation-label">Growth Trend</div><div class="participation-content">' + (d.growthTrend || 'N/A') + '</div></div>';
        h = h + '</div></div>';

        if (d.sessionDetails && d.sessionDetails.length > 0) {
            h = h + '<div class="report-section"><div class="section-title">Session Details</div>';
            h = h + '<div class="sessions-table-wrapper"><table class="sessions-table"><thead><tr>';
            h = h + '<th>Session Date</th><th>Focus Room Name</th><th>Session Duration</th><th>Breaks</th></tr></thead><tbody>';
            for (let s = 0; s < d.sessionDetails.length; s++) {
                h = h + '<tr><td class="session-date">' + formatDate(d.sessionDetails[s].sessionDate) + '</td>';
                h = h + '<td class="session-room">' + d.sessionDetails[s].roomName + '</td>';
                h = h + '<td class="session-duration">' + formatDuration(d.sessionDetails[s].durationSeconds) + '</td>';
                h = h + '<td><span class="break-count">' + (d.sessionDetails[s].breakCount > 0 ? d.sessionDetails[s].breakCount + ' break(s)' : 'No breaks') + '</span></td></tr>';
            }
            h = h + '</tbody></table></div></div>';
        }

        h = h + '<div class="report-section"><div class="section-title">Daily Focus Performance Graph</div><div class="chart-wrapper"><canvas id="performanceChart"></canvas></div></div>';
        h = h + '</div>';

        reportContent.innerHTML = h;

        if (d.dailyDataPoints && d.dailyDataPoints.length > 0) {
            renderPerformanceChart(d.dailyDataPoints);
        }
    }

    function renderPerformanceChart(dataPoints) {
        const ctx = document.getElementById('performanceChart').getContext('2d');
        
        const labels = dataPoints.map(function(d) { return 'Day ' + d.day; });
        const sessionData = dataPoints.map(function(d) { return d.sessionMinutes || 0; });
        const focusQualityData = dataPoints.map(function(d) { return d.focusQuality || 0; });

        if (chart) {
            chart.destroy();
        }

        chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Session Duration (minutes)',
                        data: sessionData,
                        borderColor: '#7997E6',
                        backgroundColor: 'rgba(121, 151, 230, 0.1)',
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4,
                        pointBackgroundColor: '#CAA9F3',
                        pointBorderColor: '#7997E6',
                        pointRadius: 4,
                        pointHoverRadius: 6,
                        yAxisID: 'y'
                    },
                    {
                        label: 'Focus Quality Score',
                        data: focusQualityData,
                        borderColor: '#CAA9F3',
                        backgroundColor: 'rgba(202, 169, 243, 0.1)',
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4,
                        pointBackgroundColor: '#7997E6',
                        pointBorderColor: '#CAA9F3',
                        pointRadius: 4,
                        pointHoverRadius: 6,
                        yAxisID: 'y1'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    mode: 'index',
                    intersect: false
                },
                plugins: {
                    legend: {
                        labels: {
                            color: 'rgba(220, 229, 255, 0.8)',
                            font: { weight: '600', size: 12 }
                        }
                    }
                },
                scales: {
                    y: {
                        type: 'linear',
                        display: true,
                        position: 'left',
                        title: {
                            display: true,
                            text: 'Minutes',
                            color: 'rgba(220, 229, 255, 0.64)'
                        },
                        grid: { color: 'rgba(180, 196, 255, 0.08)' },
                        ticks: { color: 'rgba(220, 229, 255, 0.64)' }
                    },
                    y1: {
                        type: 'linear',
                        display: true,
                        position: 'right',
                        title: {
                            display: true,
                            text: 'Focus Quality (0-100)',
                            color: 'rgba(220, 229, 255, 0.64)'
                        },
                        grid: { drawOnChartArea: false },
                        ticks: { color: 'rgba(220, 229, 255, 0.64)' }
                    },
                    x: {
                        grid: { color: 'rgba(180, 196, 255, 0.08)' },
                        ticks: { color: 'rgba(220, 229, 255, 0.64)' }
                    }
                }
            }
        });
    }

    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    }

    function formatDuration(seconds) {
        if (!seconds) return '0m';
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const mins = minutes % 60;
        if (hours > 0) return hours + 'h ' + mins + 'm';
        return mins + 'm';
    }

    function getMonthName(month) {
        const months = ['', 'January', 'February', 'March', 'April', 'May', 'June',
                       'July', 'August', 'September', 'October', 'November', 'December'];
        return months[parseInt(month)] || 'Unknown';
    }

    window.addEventListener('DOMContentLoaded', function() {
        const now = new Date();
        document.getElementById('monthSelect').value = (now.getMonth() + 1).toString();
        document.getElementById('yearSelect').value = now.getFullYear().toString();
        generateReport();
    });
</script>
</body>
</html>
