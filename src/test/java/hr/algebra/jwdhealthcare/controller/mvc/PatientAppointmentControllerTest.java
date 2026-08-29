package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.form.PatientAppointmentFormDto;
import hr.algebra.jwdhealthcare.exception.AppointmentSchedulingException;
import hr.algebra.jwdhealthcare.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientAppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private PatientAppointmentController patientAppointmentController;

    @Test
    void listAddsAuthenticatedPatientAppointmentsToModel() {
        Principal principal = () -> "patient";

        when(appointmentService.findAllForPatientView("patient")).thenReturn(List.of());

        Model model = new ExtendedModelMap();

        String viewName = patientAppointmentController.list(principal, model);

        assertEquals("patient/appointments/list", viewName);
        assertEquals(List.of(), model.asMap().get("appointments"));
        verify(appointmentService).findAllForPatientView("patient");
    }

    @Test
    void createFormAddsFormOptionsAndReturnsFormView() {
        PatientAppointmentFormDto formDto = new PatientAppointmentFormDto();

        when(appointmentService.createEmptyPatientForm()).thenReturn(formDto);
        when(appointmentService.findDoctorOptions()).thenReturn(List.of());

        Model model = new ExtendedModelMap();

        String viewName = patientAppointmentController.createForm(model);

        assertEquals("patient/appointments/form", viewName);
        assertSame(formDto, model.asMap().get("patientAppointmentForm"));
        assertEquals(List.of(), model.asMap().get("doctorOptions"));
    }

    @Test
    void createWithValidationErrorsReturnsFormView() {
        Principal principal = () -> "patient";
        PatientAppointmentFormDto formDto = new PatientAppointmentFormDto();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(appointmentService.findDoctorOptions()).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = patientAppointmentController.create(
                formDto,
                bindingResult,
                principal,
                model,
                redirectAttributes
        );

        assertEquals("patient/appointments/form", viewName);
        assertSame(formDto, model.asMap().get("patientAppointmentForm"));
        verify(appointmentService, never()).createForPatient("patient", formDto);
    }

    @Test
    void createSuccessRedirectsToPatientAppointments() {
        Principal principal = () -> "patient";
        PatientAppointmentFormDto formDto = new PatientAppointmentFormDto();

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = patientAppointmentController.create(
                formDto,
                bindingResult,
                principal,
                model,
                redirectAttributes
        );

        assertEquals("redirect:/patient/appointments", viewName);
        assertEquals(
                "patient.appointments.message.created",
                redirectAttributes.getFlashAttributes().get("successMessageKey")
        );
        verify(appointmentService).createForPatient("patient", formDto);
    }

    @Test
    void createSchedulingExceptionReturnsFormView() {
        Principal principal = () -> "patient";
        PatientAppointmentFormDto formDto = new PatientAppointmentFormDto();

        doThrow(new AppointmentSchedulingException("scheduledAt", "validation.appointment.conflict"))
                .when(appointmentService)
                .createForPatient("patient", formDto);
        when(appointmentService.findDoctorOptions()).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = patientAppointmentController.create(
                formDto,
                bindingResult,
                principal,
                model,
                redirectAttributes
        );

        assertEquals("patient/appointments/form", viewName);
        assertSame(formDto, model.asMap().get("patientAppointmentForm"));
        verify(bindingResult).rejectValue("scheduledAt", "validation.appointment.conflict");
    }
}