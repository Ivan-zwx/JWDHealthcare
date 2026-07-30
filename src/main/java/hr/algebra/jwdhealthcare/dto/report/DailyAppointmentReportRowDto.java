package hr.algebra.jwdhealthcare.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Daily appointment aggregate data is transferred from JdbcTemplate queries to report services.
 */
@Getter
@AllArgsConstructor
public class DailyAppointmentReportRowDto {

    private final LocalDate appointmentDate;
    private final int appointmentCount;
}