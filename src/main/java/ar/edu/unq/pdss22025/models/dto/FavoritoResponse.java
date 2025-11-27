package ar.edu.unq.pdss22025.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoResponse {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long ofertaId;
    private Long autoId;
    private String autoMarca;
    private String autoModelo;
    private Integer autoAnioModelo;
    private Long concesionariaId;
    private String concesionariaNombre;
    private BigDecimal precioActual;
    private String moneda;
    private Integer stock;
    private LocalDateTime fechaCreacion;
}
