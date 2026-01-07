<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <title>My Appointments</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/panel.css">

</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container">
    <h2>My Appointments</h2>

    <c:if test="${not empty successMessage}">
        <div class="message message-success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="message message-error">${errorMessage}</div>
    </c:if>

    <div class="card">
        <c:if test="${not empty appointments}">
            <table>
                <thead>
                    <tr>
                        <th>Department</th>
                        <th>Doctor</th>
                        <th>Date & Time</th>
                        <th>Status</th>
                        <th>Doctor Note</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${appointments}" var="app">
                        <tr>
                            <td><strong>${app.doctor.specialization}</strong></td>
                            <td>Dr. ${app.doctor.fullName}</td>
                            <td>
                                <div>${app.timeSlot.date}</div>
                                <small style="color:var(--primary); font-weight:bold;">${app.timeSlot.startTime}</small>
                            </td>
                            <td>
                                <span class="badge status-${app.status.name()}">${app.status}</span>
                            </td>
                            <td class="doctor-note">
                                <c:choose>
                                    <c:when test="${not empty app.doctorResponse}">${app.doctorResponse}</c:when>
                                    <c:otherwise><span style="color:#ccc;">No note</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:set var="stat" value="${app.status.name()}" />
                                <c:if test="${stat == 'NEW' || stat == 'CONFIRMED'}">
                                    <form method="post" action="/patient/appointment/cancel/${app.id}" 
                                          onsubmit="return confirm('Are you sure you want to cancel your appointment with Dr. ${app.doctor.fullName}?');">
                                        <button type="submit" class="btn-cancel">Cancel</button>
                                    </form>
                                </c:if>
                                <c:if test="${stat != 'NEW' && stat != 'CONFIRMED'}">
                                    <span style="color:#999; font-size:0.8rem;">No action</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty appointments}">
            <div class="empty-state">
                <p>You have no appointments yet.</p>
                <a href="/patient/dashboard" style="color: var(--primary); font-weight:bold; text-decoration:none;">
                    Book your first appointment &rarr;
                </a>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>
