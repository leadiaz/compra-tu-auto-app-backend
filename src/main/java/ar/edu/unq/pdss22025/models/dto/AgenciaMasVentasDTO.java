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
public class AgenciaMasVentasDTO {
    private Long concesionariaId;
    private String razonSocial;
    private Long cantidadVentas;
    private BigDecimal totalIngresos;
}

