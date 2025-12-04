package ar.edu.unq.pdss22025.models;

import ar.edu.unq.pdss22025.models.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * Entidad que representa una oferta de auto marcada como favorito por un usuario comprador.
 * Un usuario puede tener múltiples favoritos, pero solo uno por oferta.
 * Restricción única: (usuario_id, oferta_id)
 */
@Entity
@Table(name = "favorito",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_favorito_usuario_oferta", columnNames = {"usuario_id", "oferta_id"})
        },
        indexes = {
                @Index(name = "idx_favorito_usuario", columnList = "usuario_id"),
                @Index(name = "idx_favorito_oferta", columnList = "oferta_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorito_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorito_oferta"))
    private OfertaAuto oferta;

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
