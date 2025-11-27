package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.exceptions.FavoritoYaExisteException;
import ar.edu.unq.pdss22025.exceptions.UsuarioNoValidoException;
import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.repositories.FavoritoRepository;
import ar.edu.unq.pdss22025.repositories.OfertaAutoRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar favoritos (ofertas de interés) de usuarios compradores.
 * Reglas de negocio:
 * - Solo usuarios COMPRADOR pueden crear/eliminar favoritos
 * - Un usuario solo puede tener un favorito por oferta (restricción única)
 * - ADMIN puede consultar globalmente pero no modificar
 */
@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final OfertaAutoRepository ofertaAutoRepository;

    public FavoritoService(FavoritoRepository favoritoRepository, 
                          UsuarioRepository usuarioRepository,
                          OfertaAutoRepository ofertaAutoRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.ofertaAutoRepository = ofertaAutoRepository;
    }

    /**
     * Lista todos los favoritos de un usuario.
     */
    @Transactional(readOnly = true)
    public List<Favorito> listarFavoritosDeUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario con ID " + usuarioId + " no encontrado"));
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Agrega una oferta como favorito para un usuario.
     * Valida que el usuario sea COMPRADOR y que no exista ya el favorito.
     */
    @Transactional
    public Favorito agregarFavorito(Long usuarioId, Long ofertaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario con ID " + usuarioId + " no encontrado"));

        // Validar que el usuario sea COMPRADOR
        if (!(usuario instanceof UsuarioComprador)) {
            throw new UsuarioNoValidoException("Solo los usuarios de tipo COMPRADOR pueden agregar favoritos");
        }

        OfertaAuto oferta = ofertaAutoRepository.findById(ofertaId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Oferta con ID " + ofertaId + " no encontrada"));

        // Validar que no exista ya el favorito
        if (favoritoRepository.existsByUsuarioIdAndOfertaId(usuarioId, ofertaId)) {
            throw new FavoritoYaExisteException("El usuario ya tiene esta oferta marcada como favorito");
        }

        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .oferta(oferta)
                .build();

        return favoritoRepository.save(favorito);
    }

    /**
     * Elimina un favorito de un usuario.
     * Solo el dueño del favorito o ADMIN pueden eliminar.
     */
    @Transactional
    public void eliminarFavorito(Long usuarioId, Long ofertaId) {
        Favorito favorito = favoritoRepository.findByUsuarioIdAndOfertaId(usuarioId, ofertaId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Favorito no encontrado para el usuario " + usuarioId + " y oferta " + ofertaId));

        favoritoRepository.delete(favorito);
    }

    /**
     * Lista todos los favoritos del sistema (para ADMIN).
     */
    @Transactional(readOnly = true)
    public List<Favorito> listarTodosLosFavoritos() {
        return favoritoRepository.findAll();
    }

    /**
     * Lista todos los usuarios que tienen una oferta como favorito (para ADMIN).
     */
    @Transactional(readOnly = true)
    public List<Favorito> listarFavoritosPorOferta(Long ofertaId) {
        OfertaAuto oferta = ofertaAutoRepository.findById(ofertaId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Oferta con ID " + ofertaId + " no encontrada"));
        return favoritoRepository.findByOferta(oferta);
    }
}
