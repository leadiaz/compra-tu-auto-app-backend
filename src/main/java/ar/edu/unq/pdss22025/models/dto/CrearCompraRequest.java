package ar.edu.unq.pdss22025.models.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrearCompraRequest {
    private Long ofertaId;
    private Long compradorId; // Usuario comprador
    private BigDecimal precioCerrado;
}
