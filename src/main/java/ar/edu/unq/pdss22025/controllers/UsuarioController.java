package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.FavoritoResponse;
import ar.edu.unq.pdss22025.services.FavoritoService;
import ar.edu.unq.pdss22025.mapper.FavoritoMapper;
import ar.edu.unq.pdss22025.models.dto.CompraResponse;
import ar.edu.unq.pdss22025.services.CompraService;
import ar.edu.unq.pdss22025.mapper.CompraMapper;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.dto.CrearUsuarioRequest;
import ar.edu.unq.pdss22025.models.dto.MenuResponse;
import ar.edu.unq.pdss22025.models.dto.UsuarioResponse;
import ar.edu.unq.pdss22025.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import ar.edu.unq.pdss22025.models.dto.ErrorResponse;


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
    @PreAuthorize("hasRole('COMPRADOR') or hasRole('ADMIN')")
    @Operation(summary = "Obtener favorito del usuario", description = "Obtiene el favorito de un usuario si existe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Favorito encontrado"),
        @ApiResponse(responseCode = "204", description = "El usuario no tiene favorito"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FavoritoResponse> getFavorito(@Parameter(description = "ID del usuario", required = true) @PathVariable("usuarioId") @NotNull Long usuarioId) {
        var favoritoOpt = favoritoService.obtenerPorUsuario(usuarioId);
        return favoritoOpt.map(favorito -> ResponseEntity.ok(favoritoMapper.toResponse(favorito))).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/{usuarioId}/favorito/{ofertaId}")
    @PreAuthorize("hasRole('COMPRADOR') or hasRole('ADMIN')")
    @Operation(summary = "Definir favorito", description = "Define la oferta indicada como favorito del usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Favorito creado o actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario u oferta no existe",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Operación no procesable por reglas de negocio",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FavoritoResponse> setFavorito(
            @Parameter(description = "ID del usuario", required = true) @PathVariable("usuarioId") @NotNull Long usuarioId,
            @Parameter(description = "ID de la oferta", required = true) @PathVariable("ofertaId") @NotNull Long ofertaId) {
        var favorito = favoritoService.definirFavorito(usuarioId, ofertaId);
        return ResponseEntity.ok(favoritoMapper.toResponse(favorito));
    }
    @GetMapping("/{usuarioId}/compras")
    @PreAuthorize("hasRole('COMPRADOR') or hasRole('ADMIN')")
    @Operation(summary = "Listar compras por usuario", description = "Lista las compras realizadas por un usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de compras"),
        @ApiResponse(responseCode = "404", description = "No se encontraron compras para el usuario",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
        @ApiResponse(responseCode = "400", description = "Solicitud inválida o error en la creación",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody CrearUsuarioRequest request) {
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
            usuario.getTipoUsuario()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
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
                    usuario.getTipoUsuario()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuario por id", description = "Devuelve un usuario por su id. Solo ADMIN puede acceder a cualquier usuario. Los demás usuarios pueden acceder a su propio perfil.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado para ver este usuario"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@Parameter(description = "ID del usuario", required = true) @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        // Si no es admin, verificar que está consultando su propio usuario
        if (!isAdmin && auth != null) {
            String email = auth.getName();
            Usuario usuarioAutenticado = usuarioService.obtenerUsuarioPorId(id)
                    .orElse(null);
            if (usuarioAutenticado == null || !usuarioAutenticado.getEmail().equals(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuario -> {
                    UsuarioResponse response = new UsuarioResponse(
                        usuario.getId(),
                        usuario.getEmail(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getCreatedAt() != null ? usuario.getCreatedAt().toLocalDateTime() : null,
                        usuario.getActivo(),
                        usuario.getTipoUsuario()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/por-tipo/{tipoUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios por tipo", description = "Filtra usuarios por tipo (ADMIN | CONCESIONARIA | COMPRADOR).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado filtrado"),
        @ApiResponse(responseCode = "400", description = "Tipo inválido",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<UsuarioResponse>> obtenerUsuariosPorTipo(@Parameter(description = "Tipo de usuario (ADMIN|CONCESIONARIA|COMPRADOR)", required = true) @PathVariable String tipoUsuario) {
        List<Usuario> usuarios = usuarioService.obtenerUsuariosPorTipo(tipoUsuario);
        List<UsuarioResponse> responses = usuarios.stream()
                .map(usuario -> new UsuarioResponse(
                    usuario.getId(),
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getCreatedAt() != null ? usuario.getCreatedAt().toLocalDateTime() : null,
                    usuario.getActivo(),
                    usuario.getTipoUsuario()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/mi-menu")
    @PreAuthorize("hasAnyRole('COMPRADOR', 'CONCESIONARIA', 'ADMIN')")
    @Operation(summary = "Obtener menú del usuario autenticado", description = "Obtiene el menú del usuario actualmente autenticado según su rol.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menú obtenido exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MenuResponse> obtenerMiMenu() {
        return usuarioService.obtenerMenuUsuarioAutenticado()
                .map(items -> {
                    MenuResponse response = MenuResponse.builder()
                            .items(items)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
