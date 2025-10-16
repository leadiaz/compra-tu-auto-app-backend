package ar.edu.unq.pdss22025.models;

import ar.edu.unq.pdss22025.models.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "compra",
        indexes = {
                @Index(name = "idx_compra_oferta", columnList = "oferta_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Compra {

    @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false, updatable = false)
        @EqualsAndHashCode.Include
        private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false, foreignKey = @ForeignKey(name = "fk_compra_oferta"))
    private OfertaAuto oferta;

    // Campos adicionales típicos de una compra (pueden ajustarse luego)
    @NotNull
    @Column(name = "precio_unitario", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "total", nullable = false, precision = 15, scale = 2)
    private BigDecimal total;
    
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "comprador_id", nullable = false, foreignKey = @ForeignKey(name = "fk_compra_comprador"))
        private Usuario comprador;

    @CreatedDate
        @Column(name = "fecha_compra", nullable = false, updatable = false)
        private OffsetDateTime fechaCompra;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (fechaCompra == null) fechaCompra = now;
        if (updatedAt == null) updatedAt = now;
    }
}
