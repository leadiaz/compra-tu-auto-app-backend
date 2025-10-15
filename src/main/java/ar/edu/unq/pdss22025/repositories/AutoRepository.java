package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Long> {
    Optional<Auto> findByMarcaAndModeloAndAnioModelo(String marca, String modelo, Integer anioModelo);
    boolean existsByMarcaAndModeloAndAnioModelo(String marca, String modelo, Integer anioModelo);
}
