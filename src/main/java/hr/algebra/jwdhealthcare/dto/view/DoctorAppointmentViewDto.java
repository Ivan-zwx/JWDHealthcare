package hr.algebra.jwdhealthcare.dto.view;

import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Doctor appointment display data is transferred from services to MVC views.
 */
@Getter
@AllArgsConstructor
public class DoctorAppointmentViewDto {

    private final Integer idAppointment;
    private final String patientName;
    private final String reason;
    private final LocalDateTime createdAt;
    private final LocalDateTime scheduledAt;
    private final AppointmentStatus status;
    private final LocalDateTime reminderGeneratedAt;

    /**
     * Indicates whether the medical record can be edited from the doctor schedule.
     *
     * @return true if the appointment is not cancelled
     */
    public boolean isRecordEditable() {
        return status != AppointmentStatus.CANCELLED;
    }
}