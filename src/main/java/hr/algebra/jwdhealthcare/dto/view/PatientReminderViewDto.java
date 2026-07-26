package hr.algebra.jwdhealthcare.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Patient reminder display data is transferred from services to MVC views.
 */
@Getter
@AllArgsConstructor
public class PatientReminderViewDto {

    private final Integer idAppointment;
    private final String doctorName;
    private final String doctorSpecialty;
    private final String reason;
    private final LocalDateTime scheduledAt;
    private final LocalDateTime reminderGeneratedAt;
}