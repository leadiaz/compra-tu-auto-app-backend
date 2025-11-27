package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AutoRepository extends JpaRepository<Auto, Long> {
    boolean existsByMarcaIgnoreCaseAndModeloIgnoreCaseAndAnioModelo(String marca, String modelo, Integer anioModelo);
}
