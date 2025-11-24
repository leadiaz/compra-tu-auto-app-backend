package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.LoginRequest;
import ar.edu.unq.pdss22025.models.dto.LoginResponse;
import ar.edu.unq.pdss22025.models.dto.UsuarioResponse;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.services.JwtService;
import ar.edu.unq.pdss22025.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ar.edu.unq.pdss22025.models.dto.ErrorResponse;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth-controller", description = "Operaciones de autenticación")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica un usuario por email y password y devuelve un token JWT junto con su UsuarioResponse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.autenticar(request.getUsuario(), request.getPassword());
        String tipoUsuario = usuario.getTipoUsuario();
        
        UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCreatedAt() != null ? usuario.getCreatedAt().toLocalDateTime() : null,
                usuario.getActivo(),
                tipoUsuario
        );
        
        String token = jwtService.generateToken(usuario.getId(), usuario.getEmail(), tipoUsuario);
        LoginResponse response = new LoginResponse(token, usuarioResponse);
        
        return ResponseEntity.ok(response);
    }
}

