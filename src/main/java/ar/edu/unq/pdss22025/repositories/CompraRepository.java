package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Compra;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByOferta(OfertaAuto oferta);
    List<Compra> findByCompradorId(Long compradorId);
}
