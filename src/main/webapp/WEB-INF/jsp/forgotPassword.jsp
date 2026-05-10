<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>FocusSphere - Forgot Password</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
</head>
<body>
<main class="page-centered">
<div class="card auth-card" style="max-width: 460px;">
    <div class="auth-page-brand">FocusSphere</div>
    <h2>Forgot Password</h2>
    <p class="subtext">Verify your student identity before setting a new password.</p>

    <c:if test="${not empty error}">
        <div class="msg err">${error}</div>
    </c:if>

    <form method="post" action="/forgot-password">
        <label>Email</label>
        <input type="email" name="email" value="${forgotPasswordForm.email}" maxlength="255" pattern="[A-Za-z0-9._%+-]{3,}@[A-Za-z0-9.-]+\\.com" title="Enter a valid .com email address." required />

        <label>Phone No.</label>
        <input type="text" name="phone" value="${forgotPasswordForm.phone}" inputmode="numeric" maxlength="10" pattern="[0-9]{10}" title="Exactly 10 digits." required />

        <label>Roll No.</label>
        <input type="text" name="rollNo" value="${forgotPasswordForm.rollNo}" inputmode="numeric" maxlength="30" pattern="[0-9]{3,30}" title="Digits only." required />

        <label>New Password</label>
        <input type="password" name="newPassword" minlength="8" maxlength="64" required />
        <small class="subtext">Use 8+ characters with uppercase, lowercase, number, and symbol.</small>

        <label>Confirm Password</label>
        <input type="password" name="confirmPassword" minlength="8" maxlength="64" required />

        <button class="btn btn-block" type="submit">Reset Password</button>
    </form>

    <p style="margin-top: 14px;"><a href="/login">Back to login</a></p>
</div>
</main>
</body>
</html>
