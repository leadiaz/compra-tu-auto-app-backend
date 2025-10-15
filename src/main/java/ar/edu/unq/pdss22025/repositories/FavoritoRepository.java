package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    Optional<Favorito> findByUsuario(Usuario usuario);
    boolean existsByUsuario(Usuario usuario);
    List<Favorito> findByOferta(OfertaAuto oferta);
    Optional<Favorito> findByUsuarioId(Long usuarioId);
    long countByOfertaId(Long ofertaId);
}
