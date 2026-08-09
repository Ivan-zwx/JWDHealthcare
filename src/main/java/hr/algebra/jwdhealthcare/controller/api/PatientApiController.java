package hr.algebra.jwdhealthcare.controller.api;

import hr.algebra.jwdhealthcare.dto.view.PatientAppointmentViewDto;
import hr.algebra.jwdhealthcare.dto.view.PatientReminderViewDto;
import hr.algebra.jwdhealthcare.service.AppointmentService;
import hr.algebra.jwdhealthcare.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Patient REST API endpoints are handled.
 */
@RestController
@RequiredArgsConstructor
public class PatientApiController {

    private final AppointmentService appointmentService;
    private final ReminderService reminderService;

    /**
     * Displays appointments for the authenticated patient.
     *
     * @param authentication the current API authentication
     * @return patient appointment data
     */
    @GetMapping("/api/patient/appointments")
    public List<PatientAppointmentViewDto> appointments(Authentication authentication) {
        return appointmentService.findAllForPatientView(authentication.getName());
    }

    /**
     * Displays generated reminders for the authenticated patient.
     *
     * @param authentication the current API authentication
     * @return patient reminder data
     */
    @GetMapping("/api/patient/reminders")
    public List<PatientReminderViewDto> reminders(Authentication authentication) {
        return reminderService.findRemindersForPatient(authentication.getName());
    }
}