package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.mapper.FavoritoMapper;
import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.models.dto.CrearFavoritoRequest;
import ar.edu.unq.pdss22025.models.dto.ErrorResponse;
import ar.edu.unq.pdss22025.models.dto.FavoritoResponse;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.services.FavoritoService;
import ar.edu.unq.pdss22025.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar favoritos (autos de interés).
 * Endpoints para COMPRADOR y ADMIN.
 */
@RestController
@RequestMapping("/favoritos")
@Tag(name = "favorito-controller", description = "Operaciones para gestionar favoritos (autos de interés)")
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final FavoritoMapper favoritoMapper;
    private final UsuarioService usuarioService;

    public FavoritoController(FavoritoService favoritoService, 
                              FavoritoMapper favoritoMapper,
                              UsuarioService usuarioService) {
        this.favoritoService = favoritoService;
        this.favoritoMapper = favoritoMapper;
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene todos los favoritos del usuario autenticado (COMPRADOR).
     */
    @GetMapping
    @PreAuthorize("hasRole('COMPRADOR')")
    @Operation(summary = "Listar mis favoritos", description = "Devuelve la lista de favoritos del usuario autenticado (COMPRADOR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de favoritos"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo COMPRADOR puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<FavoritoResponse>> listarMisFavoritos() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
        
        List<Favorito> favoritos = favoritoService.listarFavoritosDeUsuario(usuario.getId());
        List<FavoritoResponse> responses = favoritos.stream()
                .map(favoritoMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Crea un nuevo favorito para el usuario autenticado (COMPRADOR).
     */
    @PostMapping
    @PreAuthorize("hasRole('COMPRADOR')")
    @Operation(summary = "Agregar favorito", description = "Agrega una oferta como favorito para el usuario autenticado (COMPRADOR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Favorito creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo COMPRADOR puede crear favoritos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El usuario ya tiene esta oferta como favorito",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FavoritoResponse> agregarFavorito(@Valid @RequestBody CrearFavoritoRequest request) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
        
        Favorito favorito = favoritoService.agregarFavorito(usuario.getId(), request.getOfertaId());
        FavoritoResponse response = favoritoMapper.toResponse(favorito);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Elimina un favorito del usuario autenticado (COMPRADOR).
     */
    @DeleteMapping("/{ofertaId}")
    @PreAuthorize("hasRole('COMPRADOR')")
    @Operation(summary = "Eliminar favorito", description = "Elimina un favorito del usuario autenticado (COMPRADOR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Favorito eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo COMPRADOR puede eliminar favoritos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Favorito no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminarFavorito(
            @Parameter(description = "ID de la oferta a eliminar de favoritos", required = true)
            @PathVariable Long ofertaId) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
        
        favoritoService.eliminarFavorito(usuario.getId(), ofertaId);
        
        return ResponseEntity.noContent().build();
    }

    // ========== Endpoints para ADMIN ==========

    /**
     * Lista todos los favoritos del sistema (ADMIN).
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los favoritos", description = "Devuelve todos los favoritos del sistema. Solo ADMIN puede acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de favoritos"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<FavoritoResponse>> listarTodosLosFavoritos() {
        List<Favorito> favoritos = favoritoService.listarTodosLosFavoritos();
        List<FavoritoResponse> responses = favoritos.stream()
                .map(favoritoMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Lista todos los usuarios que tienen una oferta como favorito (ADMIN).
     */
    @GetMapping("/admin/oferta/{ofertaId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar favoritos por oferta", description = "Devuelve todos los usuarios que tienen una oferta como favorito. Solo ADMIN puede acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de favoritos para la oferta"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<FavoritoResponse>> listarFavoritosPorOferta(
            @Parameter(description = "ID de la oferta", required = true)
            @PathVariable Long ofertaId) {
        List<Favorito> favoritos = favoritoService.listarFavoritosPorOferta(ofertaId);
        List<FavoritoResponse> responses = favoritos.stream()
                .map(favoritoMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
}

