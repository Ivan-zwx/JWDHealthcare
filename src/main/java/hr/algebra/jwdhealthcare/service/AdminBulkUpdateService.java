package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.dto.view.AdminBulkUpdatePreviewDto;
import hr.algebra.jwdhealthcare.repository.jdbc.AppointmentBulkJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Administrator bulk update operations are coordinated between MVC controllers and JdbcTemplate repositories.
 */
@Service
@RequiredArgsConstructor
public class AdminBulkUpdateService {

    private final AppointmentBulkJdbcRepository appointmentBulkJdbcRepository;

    /**
     * Creates a preview of appointments eligible for bulk completion.
     *
     * @return bulk update preview data
     */
    @Transactional(readOnly = true)
    public AdminBulkUpdatePreviewDto createPastAppointmentCompletionPreview() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        int count = appointmentBulkJdbcRepository.countPastScheduledAppointments(currentTime);

        return new AdminBulkUpdatePreviewDto(count, currentTime);
    }

    /**
     * Completes past scheduled appointments in one bulk update.
     *
     * @return the number of updated appointments
     */
    @Transactional
    public int completePastScheduledAppointments() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        return appointmentBulkJdbcRepository.completePastScheduledAppointments(currentTime);
    }
}