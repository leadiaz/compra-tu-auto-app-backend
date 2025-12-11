package ar.edu.unq.pdss22025.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfertaResponse {
    private Long id;
    private Long autoId;
    private Long concesionariaId;
    private String titulo;
    private String descripcion;
    private String precio;
    private String estado;
    private Integer stock;
    private java.math.BigDecimal precioActual;
    private String moneda;
    private java.time.OffsetDateTime fechaCreacion;
    private java.time.OffsetDateTime fechaActualizacion;
}
