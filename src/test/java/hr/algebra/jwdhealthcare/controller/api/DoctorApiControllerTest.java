package hr.algebra.jwdhealthcare.controller.api;

import hr.algebra.jwdhealthcare.dto.view.DoctorAppointmentViewDto;
import hr.algebra.jwdhealthcare.service.DoctorService;
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
class DoctorApiControllerTest {

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorApiController doctorApiController;

    @Test
    void scheduleReturnsAuthenticatedDoctorSchedule() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "doctor",
                null,
                "ROLE_DOCTOR"
        );
        List<DoctorAppointmentViewDto> schedule = List.of();

        when(doctorService.findScheduleForDoctor("doctor")).thenReturn(schedule);

        List<DoctorAppointmentViewDto> result = doctorApiController.schedule(authentication);

        assertSame(schedule, result);
        verify(doctorService).findScheduleForDoctor("doctor");
    }
}