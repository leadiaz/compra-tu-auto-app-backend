package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.OfertaResponse;
import ar.edu.unq.pdss22025.services.OfertaService;
import ar.edu.unq.pdss22025.mapper.OfertaMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
@Validated
public class OfertaController {
    private final OfertaService ofertaService;
    private final OfertaMapper ofertaMapper;

    public OfertaController(OfertaService ofertaService, OfertaMapper ofertaMapper) {
        this.ofertaService = ofertaService;
        this.ofertaMapper = ofertaMapper;
    }

    @GetMapping
    public ResponseEntity<List<OfertaResponse>> getOfertasByConcesionaria(@RequestParam("concesionariaId") @NotNull Long concesionariaId) {
        List<ar.edu.unq.pdss22025.models.OfertaAuto> ofertas = ofertaService.listarPorConcesionaria(concesionariaId);
        if (ofertas == null || ofertas.isEmpty()) return ResponseEntity.notFound().build();
        List<OfertaResponse> response = ofertas.stream().map(ofertaMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/autos/{autoId}")
    public ResponseEntity<List<OfertaResponse>> getOfertasByAuto(@PathVariable("autoId") @NotNull Long autoId) {
        List<ar.edu.unq.pdss22025.models.OfertaAuto> ofertas = ofertaService.listarPorAuto(autoId);
        if (ofertas == null || ofertas.isEmpty()) return ResponseEntity.notFound().build();
        List<OfertaResponse> response = ofertas.stream().map(ofertaMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
}
