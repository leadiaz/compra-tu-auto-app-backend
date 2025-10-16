package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.ResenaResponse;
import ar.edu.unq.pdss22025.models.dto.CrearResenaRequest;
import ar.edu.unq.pdss22025.services.ResenaService;
import ar.edu.unq.pdss22025.mapper.ResenaMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/resenas")
@Validated
public class ResenaController {
    private final ResenaService resenaService;
    private final ResenaMapper resenaMapper;

    public ResenaController(ResenaService resenaService, ResenaMapper resenaMapper) {
        this.resenaService = resenaService;
        this.resenaMapper = resenaMapper;
    }

    @PostMapping
    public ResponseEntity<ResenaResponse> crearResena(@Valid @RequestBody CrearResenaRequest request) {
        try {
            var resena = resenaService.crear(request);
            return ResponseEntity.ok(resenaMapper.toResponse(resena));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/autos/{autoId}")
    public ResponseEntity<List<ResenaResponse>> getResenasByAuto(@PathVariable("autoId") @NotNull Long autoId) {
        List<ar.edu.unq.pdss22025.models.Resena> resenas;
        try {
            resenas = resenaService.listarPorAuto(autoId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
        if (resenas == null || resenas.isEmpty()) return ResponseEntity.notFound().build();
        List<ResenaResponse> response = resenas.stream().map(resenaMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
}
