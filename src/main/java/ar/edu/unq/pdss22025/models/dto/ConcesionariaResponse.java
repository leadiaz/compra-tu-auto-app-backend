package ar.edu.unq.pdss22025.models.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConcesionariaResponse {
    private Long id;
    private String nombre;
    private String cuit;
    private String telefono;
    private String email;
    private String direccion;
    private Boolean activa;
    private String usuarioNombre; // Nombre del usuario relacionado, si existe
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaActualizacion;

    public ConcesionariaResponse(Long id, String nombre, String cuit, String telefono, 
                                String email, String direccion, Boolean activa, 
                                String usuarioNombre, LocalDateTime fechaAlta, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.nombre = nombre;
        this.cuit = cuit;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.activa = activa;
        this.usuarioNombre = usuarioNombre;
        this.fechaAlta = fechaAlta;
        this.fechaActualizacion = fechaActualizacion;
    }
}

