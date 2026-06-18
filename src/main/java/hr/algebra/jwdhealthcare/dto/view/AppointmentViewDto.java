package hr.algebra.jwdhealthcare.dto.view;

import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Appointment display data is transferred from services to MVC views.
 */
@Getter
@AllArgsConstructor
public class AppointmentViewDto {

    private final Integer idAppointment;
    private final String doctorName;
    private final String patientName;
    private final String reason;
    private final LocalDateTime createdAt;
    private final LocalDateTime scheduledAt;
    private final AppointmentStatus status;
    private final LocalDateTime reminderGeneratedAt;

    /**
     * Indicates whether the appointment can still be cancelled from the administrator view.
     *
     * @return true if the appointment is not already cancelled
     */
    public boolean isCancellable() {
        return status != AppointmentStatus.CANCELLED;
    }
}