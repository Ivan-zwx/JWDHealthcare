package hr.algebra.jwdhealthcare.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Appointment status aggregate data is transferred from JdbcTemplate queries to report services.
 */
@Getter
@AllArgsConstructor
public class AppointmentStatusReportRowDto {

    private final String status;
    private final int appointmentCount;
}