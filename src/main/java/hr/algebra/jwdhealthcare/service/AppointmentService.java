package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.Appointment;
import hr.algebra.jwdhealthcare.domain.AppointmentStatus;
import hr.algebra.jwdhealthcare.domain.Doctor;
import hr.algebra.jwdhealthcare.domain.Patient;
import hr.algebra.jwdhealthcare.dto.form.AppointmentFormDto;
import hr.algebra.jwdhealthcare.dto.form.PatientAppointmentFormDto;
import hr.algebra.jwdhealthcare.dto.view.AppointmentViewDto;
import hr.algebra.jwdhealthcare.dto.view.DoctorOptionDto;
import hr.algebra.jwdhealthcare.dto.view.PatientAppointmentViewDto;
import hr.algebra.jwdhealthcare.dto.view.PatientOptionDto;
import hr.algebra.jwdhealthcare.exception.AppointmentSchedulingException;
import hr.algebra.jwdhealthcare.repository.AppointmentRepository;
import hr.algebra.jwdhealthcare.repository.DoctorRepository;
import hr.algebra.jwdhealthcare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * Appointment business operations are coordinated between MVC controllers and repositories.
 */
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final int APPOINTMENT_SLOT_MINUTES = 60;

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    /**
     * Finds all appointments prepared for administrator display.
     *
     * @return appointment view data
     */
    @Transactional(readOnly = true)
    public List<AppointmentViewDto> findAllForAdminView() {
        return appointmentRepository.findAllForAdminList()
                .stream()
                .map(this::toAdminViewDto)
                .toList();
    }

    /**
     * Finds all appointments for the authenticated patient.
     *
     * @param username the authenticated username
     * @return patient appointment view data
     */
    @Transactional(readOnly = true)
    public List<PatientAppointmentViewDto> findAllForPatientView(String username) {
        Patient patient = findPatientByUsername(username);

        return appointmentRepository.findAllForPatientView(patient.getIdPatient())
                .stream()
                .map(this::toPatientViewDto)
                .toList();
    }

    /**
     * Creates a new administrator appointment form with default values.
     *
     * @return a form prepared for appointment creation
     */
    public AppointmentFormDto createEmptyForm() {
        AppointmentFormDto appointmentFormDto = new AppointmentFormDto();
        appointmentFormDto.setStatus(AppointmentStatus.SCHEDULED);
        appointmentFormDto.setScheduledAt(createDefaultScheduledAt());
        return appointmentFormDto;
    }

    /**
     * Creates a new patient appointment booking form with default values.
     *
     * @return a form prepared for patient appointment booking
     */
    public PatientAppointmentFormDto createEmptyPatientForm() {
        PatientAppointmentFormDto patientAppointmentFormDto = new PatientAppointmentFormDto();
        patientAppointmentFormDto.setScheduledAt(createDefaultScheduledAt());
        return patientAppointmentFormDto;
    }

    /**
     * Finds an appointment and converts it into editable administrator form data.
     *
     * @param idAppointment the appointment identifier
     * @return form data for the requested appointment
     */
    @Transactional(readOnly = true)
    public AppointmentFormDto findFormForEdit(Integer idAppointment) {
        Appointment appointment = findAppointmentForAdmin(idAppointment);

        AppointmentFormDto appointmentFormDto = new AppointmentFormDto();
        appointmentFormDto.setIdAppointment(appointment.getIdAppointment());
        appointmentFormDto.setDoctorId(appointment.getDoctor().getIdDoctor());
        appointmentFormDto.setPatientId(appointment.getPatient().getIdPatient());
        appointmentFormDto.setReason(appointment.getReason());
        appointmentFormDto.setScheduledAt(appointment.getScheduledAt());
        appointmentFormDto.setStatus(appointment.getStatus());

        return appointmentFormDto;
    }

    /**
     * Creates an appointment from submitted administrator form data.
     *
     * @param appointmentFormDto the submitted appointment form data
     */
    @Transactional
    public void create(AppointmentFormDto appointmentFormDto) {
        validateSchedulingRules(
                appointmentFormDto.getDoctorId(),
                appointmentFormDto.getPatientId(),
                appointmentFormDto.getScheduledAt(),
                appointmentFormDto.getStatus(),
                null,
                "patientId"
        );

        Doctor doctor = findDoctor(appointmentFormDto.getDoctorId());
        Patient patient = findPatient(appointmentFormDto.getPatientId());
        LocalDateTime scheduledAt = normalizeScheduledAt(appointmentFormDto.getScheduledAt());

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setReason(normalizeText(appointmentFormDto.getReason()));
        appointment.setCreatedAt(LocalDateTime.now().withNano(0));
        appointment.setScheduledAt(scheduledAt);
        appointment.setStatus(appointmentFormDto.getStatus());
        appointment.setReminderGeneratedAt(null);

        appointmentRepository.save(appointment);
    }

    /**
     * Creates an appointment for the authenticated patient.
     *
     * @param username the authenticated username
     * @param patientAppointmentFormDto the submitted patient appointment form data
     */
    @Transactional
    public void createForPatient(String username, PatientAppointmentFormDto patientAppointmentFormDto) {
        Patient patient = findPatientByUsername(username);

        validateSchedulingRules(
                patientAppointmentFormDto.getDoctorId(),
                patient.getIdPatient(),
                patientAppointmentFormDto.getScheduledAt(),
                AppointmentStatus.SCHEDULED,
                null,
                "scheduledAt"
        );

        Doctor doctor = findDoctor(patientAppointmentFormDto.getDoctorId());
        LocalDateTime scheduledAt = normalizeScheduledAt(patientAppointmentFormDto.getScheduledAt());

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setReason(normalizeText(patientAppointmentFormDto.getReason()));
        appointment.setCreatedAt(LocalDateTime.now().withNano(0));
        appointment.setScheduledAt(scheduledAt);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReminderGeneratedAt(null);

        appointmentRepository.save(appointment);
    }

    /**
     * Updates an existing appointment from submitted administrator form data.
     *
     * @param idAppointment the appointment identifier
     * @param appointmentFormDto the submitted appointment form data
     */
    @Transactional
    public void update(Integer idAppointment, AppointmentFormDto appointmentFormDto) {
        validateSchedulingRules(
                appointmentFormDto.getDoctorId(),
                appointmentFormDto.getPatientId(),
                appointmentFormDto.getScheduledAt(),
                appointmentFormDto.getStatus(),
                idAppointment,
                "patientId"
        );

        Appointment appointment = appointmentRepository.findById(idAppointment)
                .orElseThrow(() -> new IllegalArgumentException("Appointment was not found."));

        Doctor doctor = findDoctor(appointmentFormDto.getDoctorId());
        Patient patient = findPatient(appointmentFormDto.getPatientId());
        LocalDateTime scheduledAt = normalizeScheduledAt(appointmentFormDto.getScheduledAt());

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setReason(normalizeText(appointmentFormDto.getReason()));
        appointment.setScheduledAt(scheduledAt);
        appointment.setStatus(appointmentFormDto.getStatus());

        appointmentRepository.save(appointment);
    }

    /**
     * Cancels an appointment without deleting it.
     *
     * @param idAppointment the appointment identifier
     */
    @Transactional
    public void cancel(Integer idAppointment) {
        Appointment appointment = appointmentRepository.findById(idAppointment)
                .orElseThrow(() -> new IllegalArgumentException("Appointment was not found."));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    /**
     * Finds doctor options for appointment forms.
     *
     * @return doctor dropdown options
     */
    @Transactional(readOnly = true)
    public List<DoctorOptionDto> findDoctorOptions() {
        return doctorRepository.findAllWithUserAccount()
                .stream()
                .map(doctor -> new DoctorOptionDto(
                        doctor.getIdDoctor(),
                        "%s - %s".formatted(
                                doctor.getUserAccount().getFullName(),
                                doctor.getSpecialty()
                        )
                ))
                .toList();
    }

    /**
     * Finds patient options for administrator appointment forms.
     *
     * @return patient dropdown options
     */
    @Transactional(readOnly = true)
    public List<PatientOptionDto> findPatientOptions() {
        return patientRepository.findAllWithUserAccount()
                .stream()
                .map(patient -> new PatientOptionDto(
                        patient.getIdPatient(),
                        patient.getUserAccount().getFullName()
                ))
                .toList();
    }

    /**
     * Finds appointment statuses for administrator appointment forms.
     *
     * @return available appointment statuses
     */
    public List<AppointmentStatus> findStatusOptions() {
        return Arrays.asList(AppointmentStatus.values());
    }

    private void validateSchedulingRules(
            Integer doctorId,
            Integer patientId,
            LocalDateTime scheduledAt,
            AppointmentStatus status,
            Integer excludedAppointmentId,
            String patientConflictFieldName
    ) {
        if (doctorId == null || patientId == null || scheduledAt == null) {
            return;
        }

        LocalDateTime normalizedScheduledAt = normalizeScheduledAt(scheduledAt);

        if (!isAlignedToAppointmentSlot(normalizedScheduledAt)) {
            throw new AppointmentSchedulingException(
                    "scheduledAt",
                    "validation.appointment.scheduledAt.slot"
            );
        }

        if (status == null || status == AppointmentStatus.CANCELLED) {
            return;
        }

        if (doctorConflictExists(doctorId, normalizedScheduledAt, excludedAppointmentId)) {
            throw new AppointmentSchedulingException(
                    "doctorId",
                    "validation.appointment.doctor.conflict"
            );
        }

        if (patientConflictExists(patientId, normalizedScheduledAt, excludedAppointmentId)) {
            throw new AppointmentSchedulingException(
                    patientConflictFieldName,
                    "validation.appointment.patient.conflict"
            );
        }
    }

    private boolean doctorConflictExists(
            Integer doctorId,
            LocalDateTime scheduledAt,
            Integer excludedAppointmentId
    ) {
        if (excludedAppointmentId == null) {
            return appointmentRepository.doctorHasActiveAppointmentAt(
                    doctorId,
                    scheduledAt,
                    AppointmentStatus.CANCELLED
            );
        }

        return appointmentRepository.doctorHasAnotherActiveAppointmentAt(
                doctorId,
                scheduledAt,
                excludedAppointmentId,
                AppointmentStatus.CANCELLED
        );
    }

    private boolean patientConflictExists(
            Integer patientId,
            LocalDateTime scheduledAt,
            Integer excludedAppointmentId
    ) {
        if (excludedAppointmentId == null) {
            return appointmentRepository.patientHasActiveAppointmentAt(
                    patientId,
                    scheduledAt,
                    AppointmentStatus.CANCELLED
            );
        }

        return appointmentRepository.patientHasAnotherActiveAppointmentAt(
                patientId,
                scheduledAt,
                excludedAppointmentId,
                AppointmentStatus.CANCELLED
        );
    }

    private boolean isAlignedToAppointmentSlot(LocalDateTime scheduledAt) {
        return scheduledAt.getMinute() % APPOINTMENT_SLOT_MINUTES == 0;
    }

    private LocalDateTime normalizeScheduledAt(LocalDateTime scheduledAt) {
        return scheduledAt.withSecond(0).withNano(0);
    }

    private LocalDateTime createDefaultScheduledAt() {
        return LocalDateTime.now()
                .plusDays(1)
                .plusHours(1)
                .truncatedTo(ChronoUnit.HOURS);
    }

    private Appointment findAppointmentForAdmin(Integer idAppointment) {
        return appointmentRepository.findByIdForAdmin(idAppointment)
                .orElseThrow(() -> new IllegalArgumentException("Appointment was not found."));
    }

    private Doctor findDoctor(Integer idDoctor) {
        return doctorRepository.findById(idDoctor)
                .orElseThrow(() -> new IllegalArgumentException("Doctor was not found."));
    }

    private Patient findPatient(Integer idPatient) {
        return patientRepository.findById(idPatient)
                .orElseThrow(() -> new IllegalArgumentException("Patient was not found."));
    }

    private Patient findPatientByUsername(String username) {
        return patientRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Patient profile was not found."));
    }

    private AppointmentViewDto toAdminViewDto(Appointment appointment) {
        return new AppointmentViewDto(
                appointment.getIdAppointment(),
                appointment.getDoctor().getUserAccount().getFullName(),
                appointment.getPatient().getUserAccount().getFullName(),
                appointment.getReason(),
                appointment.getCreatedAt(),
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getReminderGeneratedAt()
        );
    }

    private PatientAppointmentViewDto toPatientViewDto(Appointment appointment) {
        return new PatientAppointmentViewDto(
                appointment.getIdAppointment(),
                appointment.getDoctor().getUserAccount().getFullName(),
                appointment.getDoctor().getSpecialty(),
                appointment.getReason(),
                appointment.getCreatedAt(),
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getReminderGeneratedAt()
        );
    }

    private String normalizeText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text.trim();
    }
}