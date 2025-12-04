package ar.edu.unq.pdss22025.models.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrearOfertaRequest {
    
    @NotNull(message = "El ID del auto es requerido")
    private Long autoId;
    
    @NotNull(message = "El stock es requerido")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer stock;
    
    @NotNull(message = "El precio actual es requerido")
    @DecimalMin(value = "0.01", message = "El precio actual debe ser mayor a 0")
    private BigDecimal precioActual;
    
    @NotBlank(message = "La moneda es requerida")
    private String moneda;
}


