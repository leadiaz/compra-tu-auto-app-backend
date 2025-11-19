package ar.edu.unq.pdss22025.models.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String usuario;
    private String password; // renombrado para evitar problemas con caracteres especiales y seguir convención del proyecto

    public LoginRequest() {}

    public LoginRequest(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }
}
