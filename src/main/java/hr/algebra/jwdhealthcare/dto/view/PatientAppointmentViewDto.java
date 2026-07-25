package hr.algebra.jwdhealthcare.dto.view;

import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Patient appointment display data is transferred from services to MVC views.
 */
@Getter
@AllArgsConstructor
public class PatientAppointmentViewDto {

    private final Integer idAppointment;
    private final String doctorName;
    private final String doctorSpecialty;
    private final String reason;
    private final LocalDateTime createdAt;
    private final LocalDateTime scheduledAt;
    private final AppointmentStatus status;
    private final LocalDateTime reminderGeneratedAt;
}