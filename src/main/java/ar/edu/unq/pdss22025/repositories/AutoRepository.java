package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Long> {
    boolean existsByMarcaIgnoreCaseAndModeloIgnoreCaseAndAnioModelo(String marca, String modelo, Integer anioModelo);
    
    /**
     * Obtiene todas las marcas únicas de los autos, ordenadas alfabéticamente.
     */
    @Query("SELECT DISTINCT a.marca FROM Auto a ORDER BY a.marca ASC")
    List<String> findDistinctMarcas();
    
    /**
     * Obtiene todos los modelos únicos de una marca específica, ordenados alfabéticamente.
     */
    @Query("SELECT DISTINCT a.modelo FROM Auto a WHERE UPPER(a.marca) = UPPER(:marca) ORDER BY a.modelo ASC")
    List<String> findDistinctModelosByMarca(String marca);
}
