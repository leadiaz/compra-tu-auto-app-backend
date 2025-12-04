package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    /**
     * Busca todas las reseñas de un auto ordenadas por fecha de creación descendente.
     */
    @Query("SELECT r FROM Resena r WHERE r.auto = :auto ORDER BY r.createdAt DESC")
    List<Resena> findByAutoOrderByCreatedAtDesc(Auto auto);
    
    /**
     * Busca todas las reseñas de un auto por ID.
     */
    List<Resena> findByAutoId(Long autoId);
    
    /**
     * Busca todas las reseñas de un usuario.
     */
    List<Resena> findByUsuarioId(Long usuarioId);
    
    /**
     * Busca una reseña específica por usuario y auto.
     */
    Optional<Resena> findByUsuarioIdAndAutoId(Long usuarioId, Long autoId);
    
    /**
     * Verifica si existe una reseña para un usuario y auto específicos.
     */
    boolean existsByUsuarioIdAndAutoId(Long usuarioId, Long autoId);
    
    /**
     * Calcula el promedio de puntaje de un auto.
     */
    @Query("SELECT AVG(r.rating) FROM Resena r WHERE r.auto.id = :autoId")
    Double calcularPromedioPuntajePorAutoId(@Param("autoId") Long autoId);
    
    /**
     * Cuenta la cantidad de reseñas de un auto.
     */
    long countByAutoId(Long autoId);
    
    /**
     * Obtiene el top N de autos mejor rankeados (por promedio de puntaje y cantidad de reseñas).
     * Retorna autos con al menos una reseña, ordenados por promedio descendente y cantidad de reseñas descendente.
     */
    @Query(value = """
        SELECT a.id as autoId, a.marca, a.modelo, a.anio_modelo as anioModelo,
               COALESCE(AVG(r.rating), 0) as promedioPuntaje,
               COUNT(r.id) as cantidadResenas
        FROM auto a
        LEFT JOIN resena r ON a.id = r.auto_id
        GROUP BY a.id, a.marca, a.modelo, a.anio_modelo
        HAVING COUNT(r.id) > 0
        ORDER BY promedioPuntaje DESC, cantidadResenas DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopAutosMejorRanqueados(@Param("limit") int limit);
}
