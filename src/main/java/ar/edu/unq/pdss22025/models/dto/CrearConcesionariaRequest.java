package ar.edu.unq.pdss22025.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearConcesionariaRequest {
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    
    @NotBlank(message = "El CUIT es requerido")
    private String cuit;
    
    private String telefono;
    
    private String email;
    
    private String direccion;
    
    private Long usuarioId; // Opcional: ID del usuario de tipo CONCESIONARIA a relacionar
}

