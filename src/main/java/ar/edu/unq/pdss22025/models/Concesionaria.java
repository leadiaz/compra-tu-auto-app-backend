package ar.edu.unq.pdss22025.models;

import ar.edu.unq.pdss22025.models.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "concesionaria", indexes = {
        @Index(name = "ux_concesionaria_cuit", columnList = "cuit", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Concesionaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @NotBlank
    @Column(name = "cuit", nullable = false, length = 20)
    private String cuit;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "direccion")
    private String direccion;

    // Relación uno a uno con Usuario (inversa)
    @OneToOne(mappedBy = "concesionaria")
    private Usuario usuario;

    @Builder.Default
    @Column(name = "activa", nullable = false)
    private Boolean activa = Boolean.TRUE;

    @Builder.Default
    @OneToMany(mappedBy = "concesionaria", fetch = FetchType.LAZY)
    private List<OfertaAuto> ofertas = new ArrayList<>();

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
