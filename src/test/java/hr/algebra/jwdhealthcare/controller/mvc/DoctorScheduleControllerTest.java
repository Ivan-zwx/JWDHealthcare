package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.form.MedicalRecordFormDto;
import hr.algebra.jwdhealthcare.service.DoctorService;
import hr.algebra.jwdhealthcare.service.MedicalRecordService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleControllerTest {

    @Mock
    private DoctorService doctorService;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private DoctorScheduleController doctorScheduleController;

    @Test
    void scheduleAddsAuthenticatedDoctorAppointmentsToModel() {
        Principal principal = () -> "doctor";

        when(doctorService.findScheduleForDoctor("doctor")).thenReturn(List.of());

        Model model = new ExtendedModelMap();

        String viewName = doctorScheduleController.schedule(principal, model);

        assertEquals("doctor/schedule", viewName);
        assertEquals(List.of(), model.asMap().get("appointments"));
        verify(doctorService).findScheduleForDoctor("doctor");
    }

    @Test
    void medicalRecordFormAddsAppointmentAndFormToModel() {
        Principal principal = () -> "doctor";
        MedicalRecordFormDto formDto = new MedicalRecordFormDto();

        when(medicalRecordService.findFormForDoctorAppointment("doctor", 10)).thenReturn(formDto);
        when(doctorService.findAppointmentForDoctorView("doctor", 10)).thenReturn(null);

        Model model = new ExtendedModelMap();

        String viewName = doctorScheduleController.medicalRecordForm(10, principal, model);

        assertEquals("doctor/medical-record-form", viewName);
        assertSame(formDto, model.asMap().get("medicalRecordForm"));
        verify(medicalRecordService).findFormForDoctorAppointment("doctor", 10);
        verify(doctorService).findAppointmentForDoctorView("doctor", 10);
    }

    @Test
    void saveMedicalRecordWithValidationErrorsReturnsFormView() {
        Principal principal = () -> "doctor";
        MedicalRecordFormDto formDto = new MedicalRecordFormDto();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(doctorService.findAppointmentForDoctorView("doctor", 10)).thenReturn(null);

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = doctorScheduleController.saveMedicalRecord(
                10,
                formDto,
                bindingResult,
                principal,
                model,
                redirectAttributes
        );

        assertEquals("doctor/medical-record-form", viewName);
        assertSame(formDto, model.asMap().get("medicalRecordForm"));
        verify(doctorService).findAppointmentForDoctorView("doctor", 10);
        verify(medicalRecordService, never()).saveForDoctorAppointment("doctor", 10, formDto);
    }

    @Test
    void saveMedicalRecordSuccessRedirectsToSchedule() {
        Principal principal = () -> "doctor";
        MedicalRecordFormDto formDto = new MedicalRecordFormDto();

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = doctorScheduleController.saveMedicalRecord(
                10,
                formDto,
                bindingResult,
                principal,
                model,
                redirectAttributes
        );

        assertEquals("redirect:/doctor/schedule", viewName);
        assertEquals(
                "doctor.schedule.message.recordSaved",
                redirectAttributes.getFlashAttributes().get("successMessageKey")
        );
        verify(medicalRecordService).saveForDoctorAppointment("doctor", 10, formDto);
    }
}