package hr.algebra.jwdhealthcare.controller.api;

import hr.algebra.jwdhealthcare.dto.view.PatientAppointmentViewDto;
import hr.algebra.jwdhealthcare.dto.view.PatientReminderViewDto;
import hr.algebra.jwdhealthcare.service.AppointmentService;
import hr.algebra.jwdhealthcare.service.ReminderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientApiControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private ReminderService reminderService;

    @InjectMocks
    private PatientApiController patientApiController;

    @Test
    void appointmentsReturnsAuthenticatedPatientAppointments() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "patient",
                null,
                "ROLE_PATIENT"
        );
        List<PatientAppointmentViewDto> appointments = List.of();

        when(appointmentService.findAllForPatientView("patient")).thenReturn(appointments);

        List<PatientAppointmentViewDto> result = patientApiController.appointments(authentication);

        assertSame(appointments, result);
        verify(appointmentService).findAllForPatientView("patient");
    }

    @Test
    void remindersReturnsAuthenticatedPatientReminders() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "patient",
                null,
                "ROLE_PATIENT"
        );
        List<PatientReminderViewDto> reminders = List.of();

        when(reminderService.findRemindersForPatient("patient")).thenReturn(reminders);

        List<PatientReminderViewDto> result = patientApiController.reminders(authentication);

        assertSame(reminders, result);
        verify(reminderService).findRemindersForPatient("patient");
    }
}