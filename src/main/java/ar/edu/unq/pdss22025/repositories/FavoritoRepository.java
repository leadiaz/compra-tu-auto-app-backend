package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    
    /**
     * Busca todos los favoritos de un usuario.
     */
    List<Favorito> findByUsuarioId(Long usuarioId);
    
    /**
     * Busca un favorito específico por usuario y oferta.
     */
    Optional<Favorito> findByUsuarioIdAndOfertaId(Long usuarioId, Long ofertaId);
    
    /**
     * Verifica si existe un favorito para un usuario y oferta específicos.
     */
    boolean existsByUsuarioIdAndOfertaId(Long usuarioId, Long ofertaId);
    
    /**
     * Busca todos los favoritos de un usuario (usando la entidad Usuario).
     */
    List<Favorito> findByUsuario(Usuario usuario);
    
    /**
     * Busca todos los favoritos de una oferta.
     */
    List<Favorito> findByOferta(OfertaAuto oferta);
    
    /**
     * Cuenta cuántos usuarios tienen una oferta como favorito.
     */
    long countByOfertaId(Long ofertaId);
    
    /**
     * Elimina un favorito por usuario y oferta.
     */
    void deleteByUsuarioIdAndOfertaId(Long usuarioId, Long ofertaId);
}
