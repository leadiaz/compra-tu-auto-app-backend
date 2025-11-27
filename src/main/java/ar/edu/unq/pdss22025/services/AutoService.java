package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutoService {

    private final AutoRepository autoRepository;

    public AutoService(AutoRepository autoRepository) {
        this.autoRepository = autoRepository;
    }

    @Transactional
    public Auto crearAuto(String marca, String modelo, Integer anioModelo) {
        if (autoRepository.existsByMarcaIgnoreCaseAndModeloIgnoreCaseAndAnioModelo(marca, modelo, anioModelo)) {
            throw new IllegalStateException("No puede existir más de un auto con la combinación marca-modelo-año");
        }

        Auto auto = Auto.builder()
                .marca(marca)
                .modelo(modelo)
                .anioModelo(anioModelo)
                .build();

        return autoRepository.save(auto);
    }

    public List<Auto> listarAutos() {
        return autoRepository.findAll();
    }

    @Transactional
    public void eliminarAuto(Long id) {
        Auto auto = autoRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Auto con ID " + id + " no encontrado"));
        autoRepository.delete(auto);
    }

    /**
     * Obtiene todas las marcas únicas de los autos.
     */
    @Transactional(readOnly = true)
    public List<String> obtenerMarcas() {
        return autoRepository.findDistinctMarcas();
    }

    /**
     * Obtiene todos los modelos únicos de una marca específica.
     */
    @Transactional(readOnly = true)
    public List<String> obtenerModelosPorMarca(String marca) {
        return autoRepository.findDistinctModelosByMarca(marca);
    }
}

