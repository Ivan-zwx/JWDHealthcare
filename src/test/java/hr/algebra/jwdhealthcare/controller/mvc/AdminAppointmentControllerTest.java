package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.form.AppointmentFormDto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AdminAppointmentController adminAppointmentController;

    @Test
    void listAddsAppointmentsToModelAndReturnsListView() {
        when(appointmentService.findAllForAdminView()).thenReturn(List.of());

        Model model = new ExtendedModelMap();

        String viewName = adminAppointmentController.list(model);

        assertEquals("admin/appointments/list", viewName);
        assertEquals(List.of(), model.asMap().get("appointments"));
        verify(appointmentService).findAllForAdminView();
    }

    @Test
    void createFormAddsFormOptionsAndReturnsFormView() {
        AppointmentFormDto formDto = new AppointmentFormDto();

        when(appointmentService.createEmptyForm()).thenReturn(formDto);
        stubAdminFormOptions();

        Model model = new ExtendedModelMap();

        String viewName = adminAppointmentController.createForm(model);

        assertEquals("admin/appointments/form", viewName);
        assertSame(formDto, model.asMap().get("appointmentForm"));
        assertEquals(false, model.asMap().get("editMode"));
    }

    @Test
    void createWithValidationErrorsReturnsFormView() {
        AppointmentFormDto formDto = new AppointmentFormDto();

        when(bindingResult.hasErrors()).thenReturn(true);
        stubAdminFormOptions();

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = adminAppointmentController.create(
                formDto,
                bindingResult,
                model,
                redirectAttributes
        );

        assertEquals("admin/appointments/form", viewName);
        assertSame(formDto, model.asMap().get("appointmentForm"));
        assertEquals(false, model.asMap().get("editMode"));
        verify(appointmentService, never()).create(formDto);
    }

    @Test
    void createSuccessRedirectsToAppointmentList() {
        AppointmentFormDto formDto = new AppointmentFormDto();

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = adminAppointmentController.create(
                formDto,
                bindingResult,
                model,
                redirectAttributes
        );

        assertEquals("redirect:/admin/appointments", viewName);
        assertEquals(
                "appointments.message.created",
                redirectAttributes.getFlashAttributes().get("successMessageKey")
        );
        verify(appointmentService).create(formDto);
    }

    @Test
    void createSchedulingExceptionReturnsFormView() {
        AppointmentFormDto formDto = new AppointmentFormDto();

        doThrow(new AppointmentSchedulingException("scheduledAt", "validation.appointment.fullHour"))
                .when(appointmentService)
                .create(formDto);
        stubAdminFormOptions();

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = adminAppointmentController.create(
                formDto,
                bindingResult,
                model,
                redirectAttributes
        );

        assertEquals("admin/appointments/form", viewName);
        verify(bindingResult).rejectValue("scheduledAt", "validation.appointment.fullHour");
        assertSame(formDto, model.asMap().get("appointmentForm"));
    }

    @Test
    void editFormAddsFormOptionsAndReturnsFormView() {
        AppointmentFormDto formDto = new AppointmentFormDto();

        when(appointmentService.findFormForEdit(10)).thenReturn(formDto);
        stubAdminFormOptions();

        Model model = new ExtendedModelMap();

        String viewName = adminAppointmentController.editForm(10, model);

        assertEquals("admin/appointments/form", viewName);
        assertSame(formDto, model.asMap().get("appointmentForm"));
        assertEquals(true, model.asMap().get("editMode"));
    }

    @Test
    void updateWithValidationErrorsReturnsFormView() {
        AppointmentFormDto formDto = new AppointmentFormDto();

        when(bindingResult.hasErrors()).thenReturn(true);
        stubAdminFormOptions();

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = adminAppointmentController.update(
                10,
                formDto,
                bindingResult,
                model,
                redirectAttributes
        );

        assertEquals("admin/appointments/form", viewName);
        assertEquals(10, formDto.getIdAppointment());
        assertEquals(true, model.asMap().get("editMode"));
        verify(appointmentService, never()).update(10, formDto);
    }

    @Test
    void updateSuccessRedirectsToAppointmentList() {
        AppointmentFormDto formDto = new AppointmentFormDto();

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = adminAppointmentController.update(
                10,
                formDto,
                bindingResult,
                model,
                redirectAttributes
        );

        assertEquals("redirect:/admin/appointments", viewName);
        assertEquals(10, formDto.getIdAppointment());
        assertEquals(
                "appointments.message.updated",
                redirectAttributes.getFlashAttributes().get("successMessageKey")
        );
        verify(appointmentService).update(10, formDto);
    }

    @Test
    void updateSchedulingExceptionReturnsFormView() {
        AppointmentFormDto formDto = new AppointmentFormDto();

        doThrow(new AppointmentSchedulingException("scheduledAt", "validation.appointment.conflict"))
                .when(appointmentService)
                .update(10, formDto);
        stubAdminFormOptions();

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = adminAppointmentController.update(
                10,
                formDto,
                bindingResult,
                model,
                redirectAttributes
        );

        assertEquals("admin/appointments/form", viewName);
        assertEquals(10, formDto.getIdAppointment());
        verify(bindingResult).rejectValue("scheduledAt", "validation.appointment.conflict");
    }

    @Test
    void cancelCancelsAppointmentAndRedirects() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = adminAppointmentController.cancel(10, redirectAttributes);

        assertEquals("redirect:/admin/appointments", viewName);
        assertEquals(
                "appointments.message.cancelled",
                redirectAttributes.getFlashAttributes().get("successMessageKey")
        );
        verify(appointmentService).cancel(10);
    }

    private void stubAdminFormOptions() {
        when(appointmentService.findDoctorOptions()).thenReturn(List.of());
        when(appointmentService.findPatientOptions()).thenReturn(List.of());
        when(appointmentService.findStatusOptions()).thenReturn(List.of());
    }
}