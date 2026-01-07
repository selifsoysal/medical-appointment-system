<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <title>Doctor Panel | Appointments</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/base.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/panel.css">

</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container">
    <h2>Welcome, Dr. ${sessionScope.loggedDoctor.fullName}</h2>
    <p style="color:#666;">Manage today's and upcoming appointments here.</p>

    <c:if test="${not empty successMessage}">
        <div class="message message-success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="message message-error">${errorMessage}</div>
    </c:if>
    

    <div class="card">
        <h3>Patient Appointments</h3>
        <c:if test="${not empty appointments}">
            <table>
                <thead>
                    <tr>
                        <th>Patient Name</th>
                        <th>Date & Time</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${appointments}" var="app">
                        <tr>
                            <td><strong>${app.patient.fullName}</strong></td>
                            <td>
                                <div>${app.timeSlot.date}</div>
                                <small style="color:var(--primary); font-weight:bold;">${app.timeSlot.startTime}</small>
                            </td>
                            <td>
                                <span class="badge status-${app.status.name()}">${app.status}</span>
                            </td>
                            <td>
                                <form method="post" action="/doctor/appointment/update">
                                    <input type="hidden" name="appointmentId" value="${app.id}"/>
                                    <select name="status">
                                        <option value="NEW" ${app.status.name()=='NEW' ? 'selected' : ''}>NEW</option>
                                        <option value="CONFIRMED" ${app.status.name()=='CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                                        <option value="REJECTED" ${app.status.name()=='REJECTED' ? 'selected' : ''}>REJECTED</option>
                                    </select>
                                    <textarea name="doctorResponse" placeholder="Add a note...">${app.doctorResponse}</textarea>
                                    <button type="submit" class="btn-update">Update</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty appointments}">
            <div class="empty-state">
                <p>No appointments yet.</p>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>
