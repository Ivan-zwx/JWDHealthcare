package hr.algebra.jwdhealthcare.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Doctor appointment aggregate data is transferred from JdbcTemplate queries to report services.
 */
@Getter
@AllArgsConstructor
public class DoctorAppointmentReportRowDto {

    private final String doctorName;
    private final String specialty;
    private final int scheduledCount;
    private final int completedCount;
    private final int cancelledCount;
    private final int totalCount;
}