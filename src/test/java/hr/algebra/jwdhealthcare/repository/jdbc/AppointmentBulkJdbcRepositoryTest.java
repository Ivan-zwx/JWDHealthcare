package hr.algebra.jwdhealthcare.repository.jdbc;

import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentBulkJdbcRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AppointmentBulkJdbcRepository appointmentBulkJdbcRepository;

    @Test
    void countPastScheduledAppointmentsReturnsJdbcTemplateCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                eq(AppointmentStatus.SCHEDULED.name()),
                eq(currentTime)
        )).thenReturn(2);

        int count = appointmentBulkJdbcRepository.countPastScheduledAppointments(currentTime);

        assertEquals(2, count);
    }

    @Test
    void countPastScheduledAppointmentsReturnsZeroWhenJdbcTemplateReturnsNull() {
        LocalDateTime currentTime = LocalDateTime.now();

        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                eq(AppointmentStatus.SCHEDULED.name()),
                eq(currentTime)
        )).thenReturn(null);

        int count = appointmentBulkJdbcRepository.countPastScheduledAppointments(currentTime);

        assertEquals(0, count);
    }

    @Test
    void completePastScheduledAppointmentsReturnsUpdatedRowCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        when(jdbcTemplate.update(
                anyString(),
                eq(AppointmentStatus.COMPLETED.name()),
                eq(AppointmentStatus.SCHEDULED.name()),
                eq(currentTime)
        )).thenReturn(3);

        int updatedCount = appointmentBulkJdbcRepository.completePastScheduledAppointments(currentTime);

        assertEquals(3, updatedCount);
        verify(jdbcTemplate).update(
                anyString(),
                eq(AppointmentStatus.COMPLETED.name()),
                eq(AppointmentStatus.SCHEDULED.name()),
                eq(currentTime)
        );
    }
}