package hr.algebra.jwdhealthcare.controller.api;

import hr.algebra.jwdhealthcare.dto.api.ApiStatusResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiStatusControllerTest {

    private final ApiStatusController apiStatusController = new ApiStatusController();

    @Test
    void adminStatusReturnsAdminResponse() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "admin",
                null,
                "ROLE_ADMIN"
        );

        ApiStatusResponseDto response = apiStatusController.adminStatus(authentication);

        assertEquals("admin", response.getArea());
        assertEquals("admin", response.getUsername());
        assertEquals("Administrator API access is available.", response.getMessage());
    }

    @Test
    void doctorStatusReturnsDoctorResponse() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "doctor",
                null,
                "ROLE_DOCTOR"
        );

        ApiStatusResponseDto response = apiStatusController.doctorStatus(authentication);

        assertEquals("doctor", response.getArea());
        assertEquals("doctor", response.getUsername());
        assertEquals("Doctor API access is available.", response.getMessage());
    }

    @Test
    void patientStatusReturnsPatientResponse() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "patient",
                null,
                "ROLE_PATIENT"
        );

        ApiStatusResponseDto response = apiStatusController.patientStatus(authentication);

        assertEquals("patient", response.getArea());
        assertEquals("patient", response.getUsername());
        assertEquals("Patient API access is available.", response.getMessage());
    }
}