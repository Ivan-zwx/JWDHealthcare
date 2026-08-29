package hr.algebra.jwdhealthcare.controller.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.reminders.initial-delay-ms=3600000",
        "app.reports.scheduler.initial-delay-ms=3600000"
})
@AutoConfigureMockMvc
class MvcPageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPageLoads() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void adminAppointmentsPageLoads() throws Exception {
        mockMvc.perform(get("/admin/appointments")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void adminCreateAppointmentPageLoads() throws Exception {
        mockMvc.perform(get("/admin/appointments/create")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void adminUsersPageLoads() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void adminReportsPageLoads() throws Exception {
        mockMvc.perform(get("/admin/reports")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void adminBulkUpdatesPageLoads() throws Exception {
        mockMvc.perform(get("/admin/bulk-updates")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void patientAppointmentsPageLoads() throws Exception {
        mockMvc.perform(get("/patient/appointments")
                        .with(user("patient").roles("PATIENT")))
                .andExpect(status().isOk());
    }

    @Test
    void patientCreateAppointmentPageLoads() throws Exception {
        mockMvc.perform(get("/patient/appointments/create")
                        .with(user("patient").roles("PATIENT")))
                .andExpect(status().isOk());
    }

    @Test
    void patientRemindersPageLoads() throws Exception {
        mockMvc.perform(get("/patient/reminders")
                        .with(user("patient").roles("PATIENT")))
                .andExpect(status().isOk());
    }

    @Test
    void doctorSchedulePageLoads() throws Exception {
        mockMvc.perform(get("/doctor/schedule")
                        .with(user("doctor").roles("DOCTOR")))
                .andExpect(status().isOk());
    }
}