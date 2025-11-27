package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.exceptions.PuntajeInvalidoException;
import ar.edu.unq.pdss22025.exceptions.ResenaYaExisteException;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.models.dto.AutoRankingDTO;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
import ar.edu.unq.pdss22025.repositories.ResenaRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar reseñas de autos.
 * Reglas de negocio:
 * - Un usuario solo puede dejar una reseña por auto (restricción única)
 * - El puntaje debe estar entre 0 y 10
 * - Solo el autor puede editar/eliminar sus reseñas (o ADMIN)
 * - CONCESIONARIA puede ver reseñas de sus autos (solo lectura)
 */
@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final AutoRepository autoRepository;
    private final UsuarioRepository usuarioRepository;

    public ResenaService(ResenaRepository resenaRepository, 
                        AutoRepository autoRepository, 
                        UsuarioRepository usuarioRepository) {
        this.resenaRepository = resenaRepository;
        this.autoRepository = autoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea una nueva reseña para un auto.
     * Valida que el puntaje esté en rango y que no exista ya una reseña del usuario para ese auto.
     */
    @Transactional
    public Resena crearReseña(Long usuarioId, Long autoId, int puntaje, String comentario) {
        // Validar rango de puntaje
        if (puntaje < 0 || puntaje > 10) {
            throw new PuntajeInvalidoException("El puntaje debe estar entre 0 y 10");
        }

        Auto auto = autoRepository.findById(autoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Auto con ID " + autoId + " no encontrado"));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario con ID " + usuarioId + " no encontrado"));

        // Validar que no exista ya una reseña del usuario para ese auto
        if (resenaRepository.existsByUsuarioIdAndAutoId(usuarioId, autoId)) {
            throw new ResenaYaExisteException("El usuario ya tiene una reseña para este auto");
        }

        Resena resena = Resena.builder()
                .auto(auto)
                .usuario(usuario)
                .rating(puntaje)
                .comentario(comentario)
                .build();

        return resenaRepository.save(resena);
    }

    /**
     * Actualiza una reseña existente.
     * Solo el autor puede modificar (o ADMIN si se implementa moderación).
     */
    @Transactional
    public Resena actualizarReseña(Long usuarioId, Long autoId, int nuevoPuntaje, String nuevoComentario) {
        // Validar rango de puntaje
        if (nuevoPuntaje < 0 || nuevoPuntaje > 10) {
            throw new PuntajeInvalidoException("El puntaje debe estar entre 0 y 10");
        }

        Resena resena = resenaRepository.findByUsuarioIdAndAutoId(usuarioId, autoId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Reseña no encontrada para el usuario " + usuarioId + " y auto " + autoId));

        // Validar que el usuario sea el autor (o ADMIN - implementar si se requiere moderación)
        if (!resena.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalStateException("Solo el autor puede modificar su reseña");
        }

        resena.setRating(nuevoPuntaje);
        resena.setComentario(nuevoComentario);

        return resenaRepository.save(resena);
    }

    /**
     * Elimina una reseña.
     * Solo el autor o ADMIN pueden eliminar.
     */
    @Transactional
    public void eliminarReseña(Long usuarioId, Long autoId) {
        Resena resena = resenaRepository.findByUsuarioIdAndAutoId(usuarioId, autoId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Reseña no encontrada para el usuario " + usuarioId + " y auto " + autoId));

        // Validar que el usuario sea el autor (o ADMIN - implementar si se requiere moderación)
        if (!resena.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalStateException("Solo el autor puede eliminar su reseña");
        }

        resenaRepository.delete(resena);
    }

    /**
     * Lista todas las reseñas de un auto.
     */
    @Transactional(readOnly = true)
    public List<Resena> listarReseñasDeAuto(Long autoId) {
        Auto auto = autoRepository.findById(autoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Auto con ID " + autoId + " no encontrado"));
        return resenaRepository.findByAutoOrderByCreatedAtDesc(auto);
    }

    /**
     * Lista todas las reseñas de un usuario.
     */
    @Transactional(readOnly = true)
    public List<Resena> listarReseñasDeUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario con ID " + usuarioId + " no encontrado"));
        return resenaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Obtiene el promedio de puntaje de un auto.
     */
    @Transactional(readOnly = true)
    public double obtenerPromedioPuntajeAuto(Long autoId) {
        autoRepository.findById(autoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Auto con ID " + autoId + " no encontrado"));
        
        Double promedio = resenaRepository.calcularPromedioPuntajePorAutoId(autoId);
        return promedio != null ? promedio : 0.0;
    }

    /**
     * Obtiene el top 5 de autos mejor rankeados.
     * Ordenados por promedio de puntaje descendente y cantidad de reseñas descendente.
     */
    @Transactional(readOnly = true)
    public List<AutoRankingDTO> top5AutosMejorRanqueados() {
        List<Object[]> resultados = resenaRepository.findTopAutosMejorRanqueados(5);
        
        return resultados.stream()
                .map(row -> AutoRankingDTO.builder()
                        .autoId(((Number) row[0]).longValue())
                        .marca((String) row[1])
                        .modelo((String) row[2])
                        .anioModelo(((Number) row[3]).intValue())
                        .promedioPuntaje(((Number) row[4]).doubleValue())
                        .cantidadResenas(((Number) row[5]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Lista todas las reseñas del sistema (para ADMIN).
     */
    @Transactional(readOnly = true)
    public List<Resena> listarTodasLasReseñas() {
        return resenaRepository.findAll();
    }
}
