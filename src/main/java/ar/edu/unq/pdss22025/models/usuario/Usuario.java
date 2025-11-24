package ar.edu.unq.pdss22025.models.usuario;

import ar.edu.unq.pdss22025.models.Concesionaria;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "usuario", indexes = {
        @Index(name = "ux_usuario_email", columnList = "email", unique = true)
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", length = 20)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @NotNull
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = Boolean.TRUE;

    // Relación uno a uno con Concesionaria
    @OneToOne
    @JoinColumn(name = "concesionaria_id", unique = true, foreignKey = @ForeignKey(name = "fk_usuario_concesionaria"))
    private Concesionaria concesionaria;

    // Nota: La relación con Favorito se elimina de la clase base. Solo UsuarioComprador podrá tenerla.

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

    /**
     * Obtiene el rol del usuario basado en su tipo de instancia.
     * @return El rol correspondiente (COMPRADOR, CONCESIONARIA o ADMIN)
     */
    public Rol getRol() {
        if (this instanceof UsuarioAdmin) {
            return Rol.ADMIN;
        }
        if (this instanceof UsuarioConcesionaria) {
            return Rol.CONCESIONARIA;
        }
        if (this instanceof UsuarioComprador) {
            return Rol.COMPRADOR;
        }
        // Fallback por defecto (no debería ocurrir en producción)
        return Rol.COMPRADOR;
    }
}
