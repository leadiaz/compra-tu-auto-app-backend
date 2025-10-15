package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Concesionaria;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfertaAutoRepository extends JpaRepository<OfertaAuto, Long> {
    List<OfertaAuto> findByConcesionariaId(Long concesionariaId);
    List<OfertaAuto> findByAutoId(Long autoId);
}
