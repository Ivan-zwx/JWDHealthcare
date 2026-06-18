package hr.algebra.jwdhealthcare.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A generated report summary is represented for administrative review.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDReport")
    private Integer idReport;

    @Column(name = "Title", nullable = false, length = 300)
    private String title;

    @Column(name = "Summary", nullable = false, columnDefinition = "nvarchar(max)")
    private String summary;

    @Column(name = "GeneratedAt", nullable = false)
    private LocalDateTime generatedAt;
}