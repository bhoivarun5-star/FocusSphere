<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>FocusSphere - Register</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
</head>
<body>
<main class="page-centered">
<div class="card auth-card" style="max-width: 480px;">
    <div class="auth-page-brand">FocusSphere</div>
    <h2>Create Student Account</h2>
    <p class="subtext">Set up your account to join focus rooms and track your sessions.</p>

    <c:if test="${not empty error}">
        <div class="msg err">${error}</div>
    </c:if>

    <form method="post" action="/register">
        <label>Name</label>
        <input type="text" name="name" value="${registerForm.name}" minlength="2" maxlength="100" pattern="[A-Za-z][A-Za-z ]{1,99}" title="Letters and spaces only." required />

        <label>Email</label>
        <input type="email" name="email" value="${registerForm.email}" maxlength="255" pattern="[A-Za-z0-9._%+-]{3,}@[A-Za-z0-9.-]+\\.com" title="Enter a valid .com email address." required />

        <label>Phone No.</label>
        <input type="tel" name="phone" value="${registerForm.phone}" inputmode="numeric" maxlength="10" pattern="[0-9]{10}" title="Exactly 10 digits." required />

        <label>Roll No.</label>
        <input type="text" name="rollNo" value="${registerForm.rollNo}" inputmode="numeric" maxlength="30" pattern="[0-9]{3,30}" title="Digits only." required />

        <label>Password</label>
        <div style="display: flex; gap: 8px; align-items: center;">
            <input id="password" type="password" name="password" minlength="8" maxlength="64" required />
            <button class="btn btn-secondary" style="white-space: nowrap;" type="button" onclick="togglePassword()">Show/Hide</button>
        </div>
        <small class="subtext">Use 8+ characters with uppercase, lowercase, number, and symbol.</small>

        <button class="btn btn-block" style="margin-top: 14px;" type="submit">Register</button>
    </form>

    <p style="margin-top: 14px;"><a href="/login">Back to login</a></p>
</div>
</main>

<script>
    function togglePassword() {
        var passwordInput = document.getElementById('password');
        passwordInput.type = passwordInput.type === 'password' ? 'text' : 'password';
    }
</script>
</body>
</html>
