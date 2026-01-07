<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Appointment System | Patient Panel</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/panel.css">


</head>
<body>

<jsp:include page="fragments/navbar.jsp"/>

<div class="container">

    <div style="text-align:center; margin-bottom:20px;">
        <h2>Book Appointment</h2>
        <p>Please select department, doctor and date.</p>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert-error">
            <strong>Error:</strong> ${errorMessage}
        </div>
    </c:if>

    <c:if test="${not empty successMessage}">
        <div class="card">
            ${successMessage}
        </div>
    </c:if>

    <div class="card">
        <form method="get" action="/patient/doctors">
            <label>Department</label>
            <select name="specialization" required onchange="this.form.submit()">
                <option value="">Select</option>
                <c:forEach items="${specializations}" var="spec">
                    <option value="${spec}" ${spec eq selectedSpecialization ? 'selected' : ''}>${spec}</option>
                </c:forEach>
            </select>
        </form>
    </div>

    <c:if test="${not empty doctors}">
        <div class="card">
            <form method="get" action="/patient/timeslots">
                <input type="hidden" name="specialization" value="${selectedSpecialization}">

                <label>Doctor</label>
                <select name="doctorId" required>
                    <option value="">Select</option>
                    <c:forEach items="${doctors}" var="doctor">
                        <option value="${doctor.id}" ${doctor.id eq selectedDoctorId ? 'selected' : ''}>
                            ${doctor.fullName}
                        </option>
                    </c:forEach>
                </select>

                <label>Date</label>
                <select name="date" required>
                    <c:forEach items="${availableDates}" var="d">
                        <option value="${d}" ${d eq selectedDate ? 'selected' : ''}>${d}</option>
                    </c:forEach>
                </select>

                <button class="btn-primary" type="submit">Show Time Slots</button>
            </form>
        </div>
    </c:if>

    <c:if test="${not empty timeSlots}">
        <div class="card">
            <h3>Available Time Slots</h3>

            <div class="slots-container">
                <c:forEach items="${timeSlots}" var="slot">
                    <c:choose>
                        <c:when test="${slot.available}">
                            <div class="slot-btn"
                                 onclick="showConfirmation('${slot.id}', '${slot.date}', '${slot.startTime}')">
                                <div>${slot.startTime}</div>
                                <div>${slot.date}</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="slot-btn taken">
                                <div>${slot.startTime}</div>
                                <div>${slot.date}</div>
                                <div>FULL</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </div>
    </c:if>

</div>

<div id="appointmentModal" class="modal-overlay">
    <div class="modal-content">
        <h3>Confirm Appointment</h3>

        <p><strong>Doctor:</strong> <span id="modalDoctorName"></span></p>
        <p><strong>Date:</strong> <span id="modalDate"></span></p>
        <p><strong>Time:</strong> <span id="modalTime"></span></p>

        <form method="post" action="/patient/appointment/confirm">
            <input type="hidden" id="modalSlotId" name="timeSlotId"/>
            <button type="submit" class="btn-primary">Confirm</button>
            <button type="button" onclick="closeModal()">Cancel</button>
        </form>
    </div>
</div>

<script>
    function getSelectedDoctorName() {
        const select = document.querySelector('select[name="doctorId"]');
        return select ? select.options[select.selectedIndex].text : '';
    }

    function showConfirmation(slotId, date, time) {
        document.getElementById('modalDoctorName').innerText = getSelectedDoctorName();
        document.getElementById('modalDate').innerText = date;
        document.getElementById('modalTime').innerText = time;
        document.getElementById('modalSlotId').value = slotId;
        document.getElementById('appointmentModal').style.display = 'flex';
    }

    function closeModal() {
        document.getElementById('appointmentModal').style.display = 'none';
    }

    window.onclick = function(e) {
        if (e.target.id === 'appointmentModal') closeModal();
    }
</script>

</body>
</html>
