package ar.edu.unq.pdss22025.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponse {
    private Long id;
    private Long ofertaId;
    private Long compradorId;
    private BigDecimal precioCerrado;
    private OffsetDateTime fechaCompra;
}
