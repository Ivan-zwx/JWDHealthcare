package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.view.PatientReminderViewDto;
import hr.algebra.jwdhealthcare.service.ReminderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientReminderControllerTest {

    @Mock
    private ReminderService reminderService;

    @InjectMocks
    private PatientReminderController patientReminderController;

    @Test
    void remindersAddsRemindersToModelAndReturnsListView() {
        Principal principal = () -> "patient";
        List<PatientReminderViewDto> reminders = List.of();

        when(reminderService.findRemindersForPatient("patient")).thenReturn(reminders);

        Model model = new ExtendedModelMap();

        String viewName = patientReminderController.reminders(principal, model);

        assertEquals("patient/reminders/list", viewName);
        assertSame(reminders, model.asMap().get("reminders"));
        verify(reminderService).findRemindersForPatient("patient");
    }
}