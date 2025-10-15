package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.FavoritoResponse;
import ar.edu.unq.pdss22025.services.FavoritoService;
import ar.edu.unq.pdss22025.mapper.FavoritoMapper;
import ar.edu.unq.pdss22025.models.dto.CompraResponse;
import ar.edu.unq.pdss22025.services.CompraService;
import ar.edu.unq.pdss22025.mapper.CompraMapper;
import ar.edu.unq.pdss22025.models.Usuario;
import ar.edu.unq.pdss22025.models.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.UsuarioComprador;
import ar.edu.unq.pdss22025.models.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.models.dto.CrearUsuarioRequest;
import ar.edu.unq.pdss22025.models.dto.UsuarioResponse;
import ar.edu.unq.pdss22025.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.constraints.NotNull;


@RestController
@RequestMapping("/api/usuarios")
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
    public ResponseEntity<FavoritoResponse> getFavorito(@PathVariable("usuarioId") @NotNull Long usuarioId) {
        try {
            var favoritoOpt = favoritoService.obtenerPorUsuario(usuarioId);
            return favoritoOpt.map(favorito -> ResponseEntity.ok(favoritoMapper.toResponse(favorito))).orElseGet(() -> ResponseEntity.noContent().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{usuarioId}/favorito/{ofertaId}")
    public ResponseEntity<FavoritoResponse> setFavorito(
            @PathVariable("usuarioId") @NotNull Long usuarioId,
            @PathVariable("ofertaId") @NotNull Long ofertaId) {
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
    public ResponseEntity<List<CompraResponse>> getComprasByUsuario(@PathVariable("usuarioId") @NotNull Long usuarioId) {
        List<ar.edu.unq.pdss22025.models.Compra> compras = compraService.listarPorCompradorId(usuarioId);
        if (compras == null || compras.isEmpty()) return ResponseEntity.notFound().build();
        List<CompraResponse> response = compras.stream().map(compraMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
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
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
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
    public ResponseEntity<?> obtenerUsuariosPorTipo(@PathVariable String tipoUsuario) {
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
