package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.repositories.OfertaAutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OfertaService {

    private final OfertaAutoRepository ofertaAutoRepository;

    public OfertaService(OfertaAutoRepository ofertaAutoRepository) {
        this.ofertaAutoRepository = ofertaAutoRepository;
    }

    public List<OfertaAuto> listarPorConcesionaria(Long concesionariaId) {
        return ofertaAutoRepository.findByConcesionariaId(concesionariaId);
    }

    public List<OfertaAuto> listarPorAuto(Long autoId) {
        return ofertaAutoRepository.findByAutoId(autoId);
    }
}
