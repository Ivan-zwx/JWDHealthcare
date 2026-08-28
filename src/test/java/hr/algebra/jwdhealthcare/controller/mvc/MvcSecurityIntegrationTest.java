package hr.algebra.jwdhealthcare.controller.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.reminders.initial-delay-ms=3600000",
        "app.reports.scheduler.initial-delay-ms=3600000"
})
@AutoConfigureMockMvc
class MvcSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousUserIsRedirectedFromAdminUsersToLogin() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void adminCanOpenAdminUsersPage() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void doctorCannotOpenAdminUsersPage() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .with(user("doctor").roles("DOCTOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void patientCannotOpenDoctorSchedulePage() throws Exception {
        mockMvc.perform(get("/doctor/schedule")
                        .with(user("patient").roles("PATIENT")))
                .andExpect(status().isForbidden());
    }
}