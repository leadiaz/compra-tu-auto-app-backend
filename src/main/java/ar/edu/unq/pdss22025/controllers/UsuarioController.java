package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.FavoritoResponse;
import ar.edu.unq.pdss22025.services.FavoritoService;
import ar.edu.unq.pdss22025.mapper.FavoritoMapper;
import ar.edu.unq.pdss22025.models.dto.CompraResponse;
import ar.edu.unq.pdss22025.services.CompraService;
import ar.edu.unq.pdss22025.mapper.CompraMapper;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.usuario.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.models.dto.CrearUsuarioRequest;
import ar.edu.unq.pdss22025.models.dto.UsuarioResponse;
import ar.edu.unq.pdss22025.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;


@RestController
@RequestMapping("usuarios")
@Tag(name = "usuario-controller", description = "Operaciones relacionadas con usuarios, favoritos y compras")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final CompraService compraService;
    private final CompraMapper compraMapper;
    private final FavoritoService favoritoService;
    private final FavoritoMapper favoritoMapper;

    public UsuarioController(UsuarioService usuarioService, CompraService compraService, CompraMapper compraMapper, FavoritoService favoritoService, FavoritoMapper favoritoMapper) {
        this.usuarioService = usuarioService;
        this.compraService = compraService;
        this.compraMapper = compraMapper;
        this.favoritoService = favoritoService;
        this.favoritoMapper = favoritoMapper;
    }
    @GetMapping("/{usuarioId}/favorito")
    @Operation(summary = "Obtener favorito del usuario", description = "Obtiene el favorito de un usuario si existe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Favorito encontrado"),
        @ApiResponse(responseCode = "204", description = "El usuario no tiene favorito"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<FavoritoResponse> getFavorito(@Parameter(description = "ID del usuario", required = true) @PathVariable("usuarioId") @NotNull Long usuarioId) {
        try {
            var favoritoOpt = favoritoService.obtenerPorUsuario(usuarioId);
            return favoritoOpt.map(favorito -> ResponseEntity.ok(favoritoMapper.toResponse(favorito))).orElseGet(() -> ResponseEntity.noContent().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{usuarioId}/favorito/{ofertaId}")
    @Operation(summary = "Definir favorito", description = "Define la oferta indicada como favorito del usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Favorito creado o actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario u oferta no existe"),
        @ApiResponse(responseCode = "422", description = "Operación no procesable por reglas de negocio")
    })
    public ResponseEntity<FavoritoResponse> setFavorito(
            @Parameter(description = "ID del usuario", required = true) @PathVariable("usuarioId") @NotNull Long usuarioId,
            @Parameter(description = "ID de la oferta", required = true) @PathVariable("ofertaId") @NotNull Long ofertaId) {
        try {
            var favorito = favoritoService.definirFavorito(usuarioId, ofertaId);
            return ResponseEntity.ok(favoritoMapper.toResponse(favorito));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }
    @GetMapping("/{usuarioId}/compras")
    @Operation(summary = "Listar compras por usuario", description = "Lista las compras realizadas por un usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de compras"),
        @ApiResponse(responseCode = "404", description = "No se encontraron compras para el usuario")
    })
    public ResponseEntity<List<CompraResponse>> getComprasByUsuario(@Parameter(description = "ID del usuario", required = true) @PathVariable("usuarioId") @NotNull Long usuarioId) {
        List<ar.edu.unq.pdss22025.models.Compra> compras = compraService.listarPorCompradorId(usuarioId);
        if (compras == null || compras.isEmpty()) return ResponseEntity.notFound().build();
        List<CompraResponse> response = compras.stream().map(compraMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario (ADMIN | CONCESIONARIA | COMPRADOR)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida o error en la creación")
    })
    public ResponseEntity<?> crearUsuario(@RequestBody CrearUsuarioRequest request) {
        try {
            Usuario usuario = usuarioService.crearUsuario(
                request.getEmail(),
                request.getPassword(),
                request.getNombre(),
                request.getApellido(),
                request.getTipoUsuario()
            );
            UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCreatedAt() != null ? usuario.getCreatedAt().toLocalDateTime() : null,
                usuario.getActivo(),
                tipoDe(usuario)
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios del sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de usuarios")
    })
    public ResponseEntity<List<UsuarioResponse>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        List<UsuarioResponse> responses = usuarios.stream()
                .map(usuario -> new UsuarioResponse(
                    usuario.getId(),
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getCreatedAt() != null ? usuario.getCreatedAt().toLocalDateTime() : null,
                    usuario.getActivo(),
                    tipoDe(usuario)
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por id", description = "Devuelve un usuario por su id.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> obtenerUsuarioPorId(@Parameter(description = "ID del usuario", required = true) @PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuario -> {
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
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/por-tipo/{tipoUsuario}")
    @Operation(summary = "Listar usuarios por tipo", description = "Filtra usuarios por tipo (ADMIN | CONCESIONARIA | COMPRADOR).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado filtrado"),
        @ApiResponse(responseCode = "400", description = "Tipo inválido")
    })
    public ResponseEntity<?> obtenerUsuariosPorTipo(@Parameter(description = "Tipo de usuario (ADMIN|CONCESIONARIA|COMPRADOR)", required = true) @PathVariable String tipoUsuario) {
        try {
            List<Usuario> usuarios = usuarioService.obtenerUsuariosPorTipo(tipoUsuario);
            List<UsuarioResponse> responses = usuarios.stream()
                    .map(usuario -> new UsuarioResponse(
                        usuario.getId(),
                        usuario.getEmail(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getCreatedAt() != null ? usuario.getCreatedAt().toLocalDateTime() : null,
                        usuario.getActivo(),
                        tipoDe(usuario)
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private String tipoDe(Usuario u) {
        if (u instanceof UsuarioAdmin) return "ADMIN";
        if (u instanceof UsuarioConcesionaria) return "CONCESIONARIA";
        if (u instanceof UsuarioComprador) return "COMPRADOR";
        return "USUARIO";
    }
}
