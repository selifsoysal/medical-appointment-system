<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Medical Appointment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>

<div class="container">

    <div class="card">

        <h1 class="title">Medical Appointment System</h1>

        <div class="switch-buttons">
            <button id="patientBtn" class="switch-btn">Patient</button>
            <button id="doctorBtn" class="switch-btn">Doctor</button>
        </div>

        <!-- PATIENT LOGIN -->
        <form id="patientLoginForm" method="post" action="/patient/login" class="form" novalidate>
            <h2>Patient Login</h2>

            <input type="email" name="email" placeholder="Email" 
                   value="${emailValue != null ? emailValue : ''}">
            <input type="password" name="password" placeholder="Password">

            <button type="submit" class="primary-btn">Login</button>

            <c:if test="${not empty patientError}">
                <p class="error">${patientError}</p>
            </c:if>

            <p class="switch-text">
                Don't have an account?
                <a href="#" id="showRegister">Register</a>
            </p>
        </form>

        <!-- PATIENT REGISTER -->
        <form id="patientRegisterForm" method="post" action="/patient/register" class="form" style="display:none;" novalidate>
            <h2>Patient Register</h2>

            <input type="text" name="fullName" placeholder="Full Name" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="password" name="password" placeholder="Password" required>

            <button type="submit" class="primary-btn">Register</button>

            <c:if test="${not empty registerError}">
                <p class="error">${registerError}</p>
            </c:if>

            <p class="switch-text">
                Already have an account?
                <a href="#" id="showLogin">Login</a>
            </p>
        </form>

        <!-- DOCTOR LOGIN -->
        <form id="doctorForm" method="post" action="/doctor/login" class="form" style="display:none;" novalidate>
            <h2>Doctor Login</h2>

            <input type="email" name="email" placeholder="Email" 
                   value="${emailValue != null ? emailValue : ''}">
            <input type="password" name="password" placeholder="Password">

            <button type="submit" class="primary-btn">Login</button>

            <c:if test="${not empty doctorError}">
                <p class="error">${doctorError}</p>
            </c:if>
        </form>

    </div>
</div>

<script>
    const patientBtn = document.getElementById('patientBtn');
    const doctorBtn = document.getElementById('doctorBtn');

    const patientLoginForm = document.getElementById('patientLoginForm');
    const patientRegisterForm = document.getElementById('patientRegisterForm');
    const doctorForm = document.getElementById('doctorForm');

    const showRegister = document.getElementById('showRegister');
    const showLogin = document.getElementById('showLogin');

    const showForm = "${showForm != null ? showForm : ''}";

    function showPatientLogin() {
        patientLoginForm.style.display = 'block';
        patientRegisterForm.style.display = 'none';
        doctorForm.style.display = 'none';
    }

    function showPatientRegister() {
        patientLoginForm.style.display = 'none';
        patientRegisterForm.style.display = 'block';
        doctorForm.style.display = 'none';
    }

    function showDoctorLogin() {
        patientLoginForm.style.display = 'none';
        patientRegisterForm.style.display = 'none';
        doctorForm.style.display = 'block';
    }

    if (showForm === 'doctor') showDoctorLogin();
    else if (showForm === 'register') showPatientRegister();
    else showPatientLogin();

    patientBtn.onclick = showPatientLogin;
    doctorBtn.onclick = showDoctorLogin;

    showRegister.onclick = (e) => { e.preventDefault(); showPatientRegister(); }
    showLogin.onclick = (e) => { e.preventDefault(); showPatientLogin(); }
</script>

</body>
</html>
