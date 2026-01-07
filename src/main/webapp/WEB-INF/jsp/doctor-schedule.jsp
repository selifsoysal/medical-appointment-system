<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <title>Doctor Panel | Schedule</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/panel.css">
    <style>
        .filter-section { margin-bottom: 20px; display: flex; align-items: center; gap: 10px; }
        .filter-section input[type="date"] { padding: 8px; border-radius: 4px; border: 1px solid #ccc; }
    </style>
</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container">
    <h2>Manage Schedule</h2>

    <c:if test="${not empty successMessage}">
        <div class="message success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="message error">${errorMessage}</div>
    </c:if>

    <div class="card">
        <h3>Filter by Date</h3>
        <form method="get" action="${pageContext.request.contextPath}/doctor/schedule" class="filter-section">
            <label for="filterDate">Select Day:</label>
            <input type="date" id="filterDate" name="date" 
                   value="${selectedDate}" 
                   onchange="this.form.submit()"/>
            <small style="color: #666;">(Showing available slots for today and future)</small>
        </form>
    </div>

    <div class="card">
        <h3>My Time Slots for <strong>${selectedDate}</strong> (${timeSlots != null ? timeSlots.size() : 0})</h3>

        <c:if test="${not empty timeSlots}">
            <table>
                <thead>
                <tr>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${timeSlots}" var="slot">
                    <tr>
                        <td>${slot.date}</td>
                        <td><strong>${slot.startTime}</strong></td>

                        <td>
                            <c:choose>
                                <c:when test="${slot.appointment == null}">
                                    <span class="available">Available</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="taken">Booked (${slot.appointment.patient.fullName})</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/doctor/timeslot/delete">
                                <input type="hidden" name="timeSlotId" value="${slot.id}"/>
                                <input type="hidden" name="date" value="${selectedDate}"/>
                                <button type="submit"
                                        class="btn-delete"
                                        onclick="return confirm('Delete this time slot?')">
                                    Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty timeSlots}">
            <p style="color:#888;">No available time slots found for this date.</p>
        </c:if>
    </div>

    <div class="card">
        <h3>Add New Time Slot</h3>
        <form method="post" action="${pageContext.request.contextPath}/doctor/timeslot/add">
            <label>Date</label><br/>
            <input type="date" name="date"
                   value="${selectedDate}"
                   min="${today != null ? today : ''}"
                   required/><br/>

            <label>Start Time</label><br/>
            <input type="time" name="startTime" required/><br/>

            <button type="submit" class="btn-add">Add Slot</button>
        </form>
    </div>

</div>
</body>
</html>