package ar.edu.unq.pdss22025.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auto",
        uniqueConstraints = @UniqueConstraint(name = "ux_auto_marca_modelo_anio", columnNames = {"marca", "modelo", "anio_modelo"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Auto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @NotBlank
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @NotNull
    @Column(name = "anio_modelo", nullable = false)
    private Integer anioModelo;

    @Builder.Default
    @OneToMany(mappedBy = "auto", fetch = FetchType.LAZY)
    private List<OfertaAuto> ofertas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "auto", fetch = FetchType.LAZY)
    private List<Resena> resenas = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }
}
