package ar.edu.unq.pdss22025.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioMasComprasDTO {
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String email;
    private Long cantidadCompras;
    private BigDecimal totalGastado;
}

