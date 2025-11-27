package ar.edu.unq.pdss22025.models.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarResenaRequest {
    
    @NotNull(message = "El puntaje es obligatorio")
    @Min(value = 0, message = "El puntaje debe ser mayor o igual a 0")
    @Max(value = 10, message = "El puntaje debe ser menor o igual a 10")
    private Integer puntaje;
    
    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    private String comentario;
}

