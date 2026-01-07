<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Medical Appointment</title>
    <style>
        :root {
            --nav-bg: #ffffff;
            --nav-text: #333333;
            --primary-blue: #007bff;
            --accent-blue: #0056b3;
            --light-gray: #f8f9fa;
        }

        body {
            margin: 0;
            padding-top: 70px; /* Navbar height */
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f7f6;
        }

        .navbar {
            width: 100%;
            height: 70px;
            background-color: var(--nav-bg);
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 10%; 
            position: fixed;
            top: 0;
            left: 0;
            z-index: 1000;
            box-sizing: border-box;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        /* Left Side: Logo/Title */
        .nav-left h3 {
            margin: 0;
            color: var(--primary-blue);
            font-size: 1.4rem;
            letter-spacing: -0.5px;
            cursor: default; /* Link olmadığı için imleci varsayılan yap */
        }

        /* Right Side: Links */
        .nav-right {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .nav-right a {
            color: var(--nav-text);
            text-decoration: none;
            font-weight: 500;
            font-size: 0.95rem;
            transition: all 0.3s ease;
            padding: 8px 12px;
            border-radius: 6px;
        }

        .nav-right a:hover {
            background-color: var(--light-gray);
            color: var(--primary-blue);
        }

        /* User Greeting */
        .user-greeting {
            font-size: 0.9rem;
            color: #666;
            border-right: 1px solid #ddd;
            padding-right: 15px;
            margin-right: 5px;
        }

        .user-greeting b {
            color: var(--nav-text);
        }

        /* Logout Button */
        .logout-btn {
            color: #dc3545 !important;
        }

        .logout-btn:hover {
            background-color: #fff5f5 !important;
        }

        /* Login Buttons */
        .login-btn {
            border: 1px solid var(--primary-blue);
            color: var(--primary-blue) !important;
        }

        .login-btn:hover {
            background-color: var(--primary-blue) !important;
            color: white !important;
        }

        @media (max-width: 768px) {
            .navbar { padding: 0 20px; }
            .user-greeting { display: none; }
        }
    </style>
</head>
<body>

<nav class="navbar">
    <div class="nav-left">
        <h3>MedicalApp</h3>
    </div>
    
    <div class="nav-right">
        <c:choose>
            <c:when test="${not empty sessionScope.loggedPatient}">
                <span class="user-greeting">Welcome, <b>${sessionScope.loggedPatient.fullName}</b></span>
                <a href="/patient/dashboard">Book Appointment</a>
                <a href="/patient/profile">My Profile</a>
                <a href="/patient/logout" class="logout-btn">Logout</a>
            </c:when>

            <c:when test="${not empty sessionScope.loggedDoctor}">
                <span class="user-greeting">Dr. <b>${sessionScope.loggedDoctor.fullName}</b></span>
                <a href="/doctor/dashboard">View Appointments</a>
                <a href="/doctor/schedule">Manage Schedule</a>
                <a href="/doctor/logout" class="logout-btn">Logout</a>
            </c:when>

            <c:otherwise>
                <a href="/patient/login" class="login-btn">Patient Login</a>
                <a href="/doctor/login" class="login-btn">Doctor Login</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>

</body>
</html>