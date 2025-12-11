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
public class AutoMasVendidoDTO {
    private Long autoId;
    private String marca;
    private String modelo;
    private Integer anioModelo;
    private Long cantidadVentas;
    private BigDecimal precioPromedio;
    private BigDecimal totalIngresos;
}

