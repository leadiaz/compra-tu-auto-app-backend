package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.mapper.ResenaMapper;
import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.models.dto.ActualizarResenaRequest;
import ar.edu.unq.pdss22025.models.dto.AutoRankingDTO;
import ar.edu.unq.pdss22025.models.dto.CrearResenaRequest;
import ar.edu.unq.pdss22025.models.dto.ErrorResponse;
import ar.edu.unq.pdss22025.models.dto.ResenaResponse;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.services.ResenaService;
import ar.edu.unq.pdss22025.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar reseñas de autos.
 * Endpoints para COMPRADOR, CONCESIONARIA y ADMIN.
 */
@RestController
@RequestMapping("/resenas")
@Validated
@Tag(name = "resena-controller", description = "Operaciones relacionadas con reseñas de autos")
public class ResenaController {
    
    private final ResenaService resenaService;
    private final ResenaMapper resenaMapper;
    private final UsuarioService usuarioService;

    public ResenaController(ResenaService resenaService, 
                           ResenaMapper resenaMapper,
                           UsuarioService usuarioService) {
        this.resenaService = resenaService;
        this.resenaMapper = resenaMapper;
        this.usuarioService = usuarioService;
    }

    // ========== Endpoints para COMPRADOR ==========

    /**
     * Lista todas las reseñas del usuario autenticado (COMPRADOR).
     */
    @GetMapping("/mias")
    @PreAuthorize("hasRole('COMPRADOR')")
    @Operation(summary = "Listar mis reseñas", description = "Devuelve todas las reseñas del usuario autenticado (COMPRADOR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de reseñas del usuario"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo COMPRADOR puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ResenaResponse>> listarMisResenas() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
        
        List<Resena> resenas = resenaService.listarReseñasDeUsuario(usuario.getId());
        List<ResenaResponse> responses = resenas.stream()
                .map(resenaMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Crea una nueva reseña para el usuario autenticado (COMPRADOR).
     */
    @PostMapping
    @PreAuthorize("hasRole('COMPRADOR')")
    @Operation(summary = "Crear reseña", description = "Crea una reseña para un auto por parte del usuario autenticado (COMPRADOR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o puntaje fuera de rango",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo COMPRADOR puede crear reseñas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Auto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El usuario ya tiene una reseña para este auto",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ResenaResponse> crearResena(@Valid @RequestBody CrearResenaRequest request) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
        
        Resena resena = resenaService.crearReseña(
                usuario.getId(), 
                request.getAutoId(), 
                request.getPuntaje(), 
                request.getComentario()
        );
        
        ResenaResponse response = resenaMapper.toResponse(resena);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza una reseña del usuario autenticado (COMPRADOR).
     */
    @PutMapping("/{autoId}")
    @PreAuthorize("hasRole('COMPRADOR')")
    @Operation(summary = "Actualizar reseña", description = "Actualiza una reseña del usuario autenticado para un auto (COMPRADOR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o puntaje fuera de rango",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo COMPRADOR puede actualizar sus reseñas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ResenaResponse> actualizarResena(
            @Parameter(description = "ID del auto", required = true)
            @PathVariable Long autoId,
            @Valid @RequestBody ActualizarResenaRequest request) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
        
        Resena resena = resenaService.actualizarReseña(
                usuario.getId(), 
                autoId, 
                request.getPuntaje(), 
                request.getComentario()
        );
        
        ResenaResponse response = resenaMapper.toResponse(resena);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina una reseña del usuario autenticado (COMPRADOR).
     */
    @DeleteMapping("/{autoId}")
    @PreAuthorize("hasRole('COMPRADOR')")
    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña del usuario autenticado para un auto (COMPRADOR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reseña eliminada exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo COMPRADOR puede eliminar sus reseñas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminarResena(
            @Parameter(description = "ID del auto", required = true)
            @PathVariable Long autoId) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
        
        resenaService.eliminarReseña(usuario.getId(), autoId);
        
        return ResponseEntity.noContent().build();
    }

    // ========== Endpoints para consulta general (COMPRADOR, CONCESIONARIA, ADMIN) ==========

    /**
     * Lista todas las reseñas de un auto (visible para todos los roles autenticados).
     * Permite que CONCESIONARIA vea reseñas de sus autos.
     */
    @GetMapping("/auto/{autoId}")
    @PreAuthorize("hasAnyRole('COMPRADOR', 'CONCESIONARIA', 'ADMIN')")
    @Operation(summary = "Listar reseñas por auto", description = "Obtiene todas las reseñas asociadas a un auto. Visible para COMPRADOR, CONCESIONARIA y ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de reseñas para el auto"),
            @ApiResponse(responseCode = "403", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Auto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ResenaResponse>> listarResenasPorAuto(
            @Parameter(description = "ID del auto", required = true)
            @PathVariable("autoId") @NotNull Long autoId) {
        List<Resena> resenas = resenaService.listarReseñasDeAuto(autoId);
        List<ResenaResponse> responses = resenas.stream()
                .map(resenaMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    // ========== Endpoints para ADMIN / Reportes ==========

    /**
     * Lista todas las reseñas del sistema (ADMIN).
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las reseñas", description = "Devuelve todas las reseñas del sistema. Solo ADMIN puede acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de todas las reseñas"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ResenaResponse>> listarTodasLasResenas() {
        List<Resena> resenas = resenaService.listarTodasLasReseñas();
        List<ResenaResponse> responses = resenas.stream()
                .map(resenaMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Obtiene el Top 5 de autos mejor rankeados (ADMIN).
     */
    @GetMapping("/admin/top-autos-rankeados")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Top 5 autos mejor rankeados", description = "Devuelve el Top 5 de autos mejor rankeados según promedio de puntaje y cantidad de reseñas. Solo ADMIN puede acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top 5 de autos mejor rankeados"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AutoRankingDTO>> top5AutosMejorRanqueados() {
        List<AutoRankingDTO> top5 = resenaService.top5AutosMejorRanqueados();
        return ResponseEntity.ok(top5);
    }
}

