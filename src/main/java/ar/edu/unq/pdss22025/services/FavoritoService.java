package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.repositories.FavoritoRepository;
import ar.edu.unq.pdss22025.repositories.OfertaAutoRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final OfertaAutoRepository ofertaAutoRepository;

    public FavoritoService(FavoritoRepository favoritoRepository, UsuarioRepository usuarioRepository, OfertaAutoRepository ofertaAutoRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.ofertaAutoRepository = ofertaAutoRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Favorito> obtenerPorUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Favorito definirFavorito(Long usuarioId, Long ofertaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));

        if (!(usuario instanceof UsuarioComprador)) {
            throw new IllegalStateException("Solo los usuarios de tipo COMPRADOR pueden definir un favorito");
        }

        OfertaAuto oferta = ofertaAutoRepository.findById(ofertaId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Oferta no encontrada"));

        favoritoRepository.findByUsuarioId(usuarioId).ifPresent(existing -> {
            favoritoRepository.delete(existing);
            favoritoRepository.flush();
        });

        Favorito nuevo = Favorito.builder()
                .usuario(usuario)
                .oferta(oferta)
                .build();
        return favoritoRepository.save(nuevo);
    }
}
