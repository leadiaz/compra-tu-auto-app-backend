package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Concesionaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcesionariaRepository extends JpaRepository<Concesionaria, Long> {
    Optional<Concesionaria> findByCuit(String cuit);
    boolean existsByCuit(String cuit);
}
