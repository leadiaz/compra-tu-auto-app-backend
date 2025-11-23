package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.dto.CrearResenaRequest;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
import ar.edu.unq.pdss22025.repositories.ResenaRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final AutoRepository autoRepository;
    private final UsuarioRepository usuarioRepository;

    public ResenaService(ResenaRepository resenaRepository, AutoRepository autoRepository, UsuarioRepository usuarioRepository) {
        this.resenaRepository = resenaRepository;
        this.autoRepository = autoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Resena crear(CrearResenaRequest request) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating fuera de rango (1..5)");
        }

        Auto auto = autoRepository.findById(request.getAutoId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Auto no encontrado"));
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));

        Resena resena = Resena.builder()
                .auto(auto)
                .usuario(usuario)
                .rating(request.getRating())
                .comentario(request.getComentario())
                .build();

        return resenaRepository.save(resena);
    }

    @Transactional(readOnly = true)
    public List<Resena> listarPorAuto(Long autoId) {
        Auto auto = autoRepository.findById(autoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Auto no encontrado"));
        return resenaRepository.findByAutoOrderByCreatedAtDesc(auto);
    }
}
