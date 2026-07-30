package hr.algebra.jwdhealthcare.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Bulk update preview data is transferred from services to administrator MVC views.
 */
@Getter
@AllArgsConstructor
public class AdminBulkUpdatePreviewDto {

    private final int pastScheduledAppointmentCount;
    private final LocalDateTime currentTime;
}