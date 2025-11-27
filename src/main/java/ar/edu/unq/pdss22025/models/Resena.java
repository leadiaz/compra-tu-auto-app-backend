package ar.edu.unq.pdss22025.models;

import ar.edu.unq.pdss22025.models.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * Entidad que representa una reseña de un auto realizada por un usuario.
 * Un usuario solo puede dejar una reseña por auto.
 * Restricción única: (usuario_id, auto_id)
 * El puntaje debe estar entre 0 y 10.
 */
@Entity
@Table(name = "resena",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_resena_usuario_auto", columnNames = {"usuario_id", "auto_id"})
        },
        indexes = {
                @Index(name = "idx_resena_usuario", columnList = "usuario_id"),
                @Index(name = "idx_resena_auto", columnList = "auto_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_resena_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auto_id", nullable = false, foreignKey = @ForeignKey(name = "fk_resena_auto"))
    private Auto auto;

    @NotNull
    @Min(0)
    @Max(10)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Size(max = 1000)
    @Column(name = "comentario", length = 1000)
    private String comentario;

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
