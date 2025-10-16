package ar.edu.unq.pdss22025.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "oferta_auto",
        uniqueConstraints = @UniqueConstraint(name = "ux_oferta_auto_concesionaria_auto", columnNames = {"concesionaria_id", "auto_id"}),
        indexes = {
                @Index(name = "idx_oferta_auto_concesionaria", columnList = "concesionaria_id"),
                @Index(name = "idx_oferta_auto_auto", columnList = "auto_id")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class OfertaAuto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concesionaria_id", nullable = false, foreignKey = @ForeignKey(name = "fk_oferta_auto_concesionaria"))
    private Concesionaria concesionaria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auto_id", nullable = false, foreignKey = @ForeignKey(name = "fk_oferta_auto_auto"))
    private Auto auto;

    @NotNull
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull
    @Column(name = "precio_actual", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioActual;

    @NotBlank
    @Column(name = "moneda", nullable = false, length = 10)
    private String moneda;

    // Compras asociadas (no cascade remove para preservar integridad histórica)
        @Builder.Default
        @OneToMany(mappedBy = "oferta", fetch = FetchType.LAZY)
        private List<Compra> compras = new ArrayList<>();

    // Favoritos que referencian esta oferta
        @Builder.Default
        @OneToMany(mappedBy = "oferta", fetch = FetchType.LAZY)
        private List<Favorito> favoritos = new ArrayList<>();

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
