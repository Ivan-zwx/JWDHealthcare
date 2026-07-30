package hr.algebra.jwdhealthcare.repository.jdbc;

import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * JdbcTemplate bulk data access operations are provided for appointments.
 */
@Repository
@RequiredArgsConstructor
public class AppointmentBulkJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Counts past scheduled appointments eligible for bulk completion.
     *
     * @param currentTime the current timestamp used as the cutoff
     * @return eligible appointment count
     */
    public int countPastScheduledAppointments(LocalDateTime currentTime) {
        String sql = """
                select count(*)
                from [Appointment]
                where [Status] = ?
                  and [ScheduledAt] < ?
                """;

        Integer value = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                AppointmentStatus.SCHEDULED.name(),
                currentTime
        );

        if (value == null) {
            return 0;
        }

        return value;
    }

    /**
     * Marks past scheduled appointments as completed.
     *
     * @param currentTime the current timestamp used as the cutoff
     * @return the number of updated appointments
     */
    public int completePastScheduledAppointments(LocalDateTime currentTime) {
        String sql = """
                update [Appointment]
                set [Status] = ?
                where [Status] = ?
                  and [ScheduledAt] < ?
                """;

        return jdbcTemplate.update(
                sql,
                AppointmentStatus.COMPLETED.name(),
                AppointmentStatus.SCHEDULED.name(),
                currentTime
        );
    }
}