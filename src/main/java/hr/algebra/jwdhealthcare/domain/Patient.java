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

/**
 * A patient profile is represented for users with the patient role.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPatient")
    private Integer idPatient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserAccountID", nullable = false)
    private UserAccount userAccount;

    @Column(name = "Address", length = 300)
    private String address;

    @Column(name = "Phone", length = 300)
    private String phone;
}