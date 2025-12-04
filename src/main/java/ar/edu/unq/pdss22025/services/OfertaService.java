package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.exceptions.OfertaYaExisteException;
import ar.edu.unq.pdss22025.exceptions.UsuarioNoValidoException;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Concesionaria;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
import ar.edu.unq.pdss22025.repositories.OfertaAutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OfertaService {

    private final OfertaAutoRepository ofertaAutoRepository;
    private final AutoRepository autoRepository;

    public OfertaService(OfertaAutoRepository ofertaAutoRepository,
                        AutoRepository autoRepository) {
        this.ofertaAutoRepository = ofertaAutoRepository;
        this.autoRepository = autoRepository;
    }

    @Transactional(readOnly = true)
    public List<OfertaAuto> listarPorConcesionaria(Long concesionariaId) {
        return ofertaAutoRepository.findByConcesionariaId(concesionariaId);
    }

    @Transactional(readOnly = true)
    public List<OfertaAuto> listarPorAuto(Long autoId) {
        return ofertaAutoRepository.findByAutoId(autoId);
    }

    @Transactional
    public OfertaAuto crearOferta(Usuario usuario, Long autoId, Integer stock, BigDecimal precioActual, String moneda) {
        // Validar que el usuario es de tipo CONCESIONARIA
        if (!usuario.esConcesionaria()) {
            throw new UsuarioNoValidoException("Solo los usuarios de tipo CONCESIONARIA pueden crear ofertas");
        }

        UsuarioConcesionaria usuarioConcesionaria = (UsuarioConcesionaria) usuario;
        
        // Validar que el usuario tiene una concesionaria asociada
        Concesionaria concesionaria = usuarioConcesionaria.getConcesionaria();
        if (concesionaria == null) {
            throw new UsuarioNoValidoException("El usuario CONCESIONARIA no tiene una concesionaria asociada");
        }

        // Validar que la concesionaria está activa
        if (!concesionaria.getActiva()) {
            throw new UsuarioNoValidoException("La concesionaria asociada no está activa");
        }

        // Validar que el auto existe
        Auto auto = autoRepository.findById(autoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Auto con ID " + autoId + " no encontrado"));

        // Validar que no existe ya una oferta para esta combinación
        if (ofertaAutoRepository.existsByConcesionariaIdAndAutoId(concesionaria.getId(), autoId)) {
            throw new OfertaYaExisteException("Ya existe una oferta para esta concesionaria y este auto");
        }

        // Crear la oferta
        OfertaAuto oferta = OfertaAuto.builder()
                .concesionaria(concesionaria)
                .auto(auto)
                .stock(stock)
                .precioActual(precioActual)
                .moneda(moneda)
                .build();

        return ofertaAutoRepository.save(oferta);
    }
}
