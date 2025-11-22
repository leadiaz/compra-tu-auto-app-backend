package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.LoginRequest;
import ar.edu.unq.pdss22025.models.dto.UsuarioResponse;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.usuario.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth-controller", description = "Operaciones de autenticación")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica un usuario por email y password y devuelve su UsuarioResponse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<UsuarioResponse> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.autenticar(request.getUsuario(), request.getPassword());
        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCreatedAt() != null ? usuario.getCreatedAt().toLocalDateTime() : null,
                usuario.getActivo(),
                tipoDe(usuario)
        );
        return ResponseEntity.ok(response);
    }

    private String tipoDe(Usuario u) {
        if (u instanceof UsuarioAdmin) return "ADMIN";
        if (u instanceof UsuarioConcesionaria) return "CONCESIONARIA";
        if (u instanceof UsuarioComprador) return "COMPRADOR";
        return "USUARIO";
    }
}

