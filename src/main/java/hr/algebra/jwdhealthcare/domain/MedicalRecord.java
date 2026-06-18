package hr.algebra.jwdhealthcare.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A medical record is represented for a completed or reviewed appointment.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MedicalRecord")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDMedicalRecord")
    private Integer idMedicalRecord;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AppointmentID", nullable = false)
    private Appointment appointment;

    @Column(name = "Diagnosis", length = 1000)
    private String diagnosis;

    @Column(name = "Treatment", length = 1000)
    private String treatment;

    @Column(name = "Notes", length = 1000)
    private String notes;

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}