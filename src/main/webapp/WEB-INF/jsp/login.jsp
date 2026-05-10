<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>FocusSphere - Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="/css/app.css" />
</head>
<body>
<main class="page-centered">
<div class="card auth-card">
    <h2>FocusSphere</h2>

    <c:if test="${not empty error}">
        <div class="msg err">${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="msg ok">${success}</div>
    </c:if>

    <form method="post" action="/login">
        <label>Email</label>
        <input type="email" name="email" value="${loginForm.email}" maxlength="255" pattern="[A-Za-z0-9._%+-]{3,}@[A-Za-z0-9.-]+\\.com" title="Enter a valid .com email address." required />

        <label>Password</label>
        <input type="password" name="password" minlength="8" maxlength="64" required />

        <button class="btn btn-block" type="submit">Login</button>
    </form>

    <div class="auth-links">
        <a href="/register">Create account</a>
        <a href="/forgot-password">Forgot password</a>
    </div>
</div>
</main>
</body>
</html>
