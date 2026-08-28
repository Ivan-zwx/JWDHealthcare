package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.dto.view.AdminBulkUpdatePreviewDto;
import hr.algebra.jwdhealthcare.repository.jdbc.AppointmentBulkJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBulkUpdateServiceTest {

    @Mock
    private AppointmentBulkJdbcRepository appointmentBulkJdbcRepository;

    @InjectMocks
    private AdminBulkUpdateService adminBulkUpdateService;

    @Test
    void createPastAppointmentCompletionPreviewReturnsRepositoryCount() {
        when(appointmentBulkJdbcRepository.countPastScheduledAppointments(any(LocalDateTime.class)))
                .thenReturn(2);

        AdminBulkUpdatePreviewDto preview = adminBulkUpdateService.createPastAppointmentCompletionPreview();

        assertEquals(2, preview.getPastScheduledAppointmentCount());
        assertNotNull(preview.getCurrentTime());
    }

    @Test
    void completePastScheduledAppointmentsReturnsUpdatedCount() {
        when(appointmentBulkJdbcRepository.completePastScheduledAppointments(any(LocalDateTime.class)))
                .thenReturn(3);

        int updatedCount = adminBulkUpdateService.completePastScheduledAppointments();

        assertEquals(3, updatedCount);
        verify(appointmentBulkJdbcRepository).completePastScheduledAppointments(any(LocalDateTime.class));
    }
}