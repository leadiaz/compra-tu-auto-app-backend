package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByCompradorId(Long compradorId);

    /**
     * Obtiene el Top 5 de autos más vendidos (sin filtros de fecha)
     */
    @Query(value = """
        SELECT a.id as autoId, a.marca, a.modelo, a.anio_modelo as anioModelo,
               COUNT(c.id) as cantidadVentas,
               COALESCE(AVG(c.precio_unitario), 0) as precioPromedio,
               COALESCE(SUM(c.total), 0) as totalIngresos
        FROM compra c
        INNER JOIN oferta_auto oa ON c.oferta_id = oa.id
        INNER JOIN auto a ON oa.auto_id = a.id
        GROUP BY a.id, a.marca, a.modelo, a.anio_modelo
        ORDER BY cantidadVentas DESC, totalIngresos DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5AutosMasVendidos();

    /**
     * Obtiene el Top 5 de autos más vendidos (con filtros de fecha)
     */
    @Query(value = """
        SELECT a.id as autoId, a.marca, a.modelo, a.anio_modelo as anioModelo,
               COUNT(c.id) as cantidadVentas,
               COALESCE(AVG(c.precio_unitario), 0) as precioPromedio,
               COALESCE(SUM(c.total), 0) as totalIngresos
        FROM compra c
        INNER JOIN oferta_auto oa ON c.oferta_id = oa.id
        INNER JOIN auto a ON oa.auto_id = a.id
        WHERE c.fecha_compra >= :fechaDesde
          AND c.fecha_compra <= :fechaHasta
        GROUP BY a.id, a.marca, a.modelo, a.anio_modelo
        ORDER BY cantidadVentas DESC, totalIngresos DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5AutosMasVendidos(@Param("fechaDesde") OffsetDateTime fechaDesde, 
                                             @Param("fechaHasta") OffsetDateTime fechaHasta);

    /**
     * Obtiene el Top 5 de usuarios con más compras (sin filtros de fecha)
     */
    @Query(value = """
        SELECT u.id as usuarioId, u.nombre, u.apellido, u.email,
               COUNT(c.id) as cantidadCompras,
               COALESCE(SUM(c.total), 0) as totalGastado
        FROM compra c
        INNER JOIN usuario u ON c.comprador_id = u.id
        GROUP BY u.id, u.nombre, u.apellido, u.email
        ORDER BY cantidadCompras DESC, totalGastado DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5UsuariosMasCompras();

    /**
     * Obtiene el Top 5 de usuarios con más compras (con filtros de fecha)
     */
    @Query(value = """
        SELECT u.id as usuarioId, u.nombre, u.apellido, u.email,
               COUNT(c.id) as cantidadCompras,
               COALESCE(SUM(c.total), 0) as totalGastado
        FROM compra c
        INNER JOIN usuario u ON c.comprador_id = u.id
        WHERE c.fecha_compra >= :fechaDesde
          AND c.fecha_compra <= :fechaHasta
        GROUP BY u.id, u.nombre, u.apellido, u.email
        ORDER BY cantidadCompras DESC, totalGastado DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5UsuariosMasCompras(@Param("fechaDesde") OffsetDateTime fechaDesde,
                                               @Param("fechaHasta") OffsetDateTime fechaHasta);

    /**
     * Obtiene el Top 5 de agencias con más ventas (sin filtros de fecha)
     */
    @Query(value = """
        SELECT con.id as concesionariaId, con.nombre as razonSocial,
               COUNT(c.id) as cantidadVentas,
               COALESCE(SUM(c.total), 0) as totalIngresos
        FROM compra c
        INNER JOIN oferta_auto oa ON c.oferta_id = oa.id
        INNER JOIN concesionaria con ON oa.concesionaria_id = con.id
        GROUP BY con.id, con.nombre
        ORDER BY cantidadVentas DESC, totalIngresos DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5AgenciasMasVentas();

    /**
     * Obtiene el Top 5 de agencias con más ventas (con filtros de fecha)
     */
    @Query(value = """
        SELECT con.id as concesionariaId, con.nombre as razonSocial,
               COUNT(c.id) as cantidadVentas,
               COALESCE(SUM(c.total), 0) as totalIngresos
        FROM compra c
        INNER JOIN oferta_auto oa ON c.oferta_id = oa.id
        INNER JOIN concesionaria con ON oa.concesionaria_id = con.id
        WHERE c.fecha_compra >= :fechaDesde
          AND c.fecha_compra <= :fechaHasta
        GROUP BY con.id, con.nombre
        ORDER BY cantidadVentas DESC, totalIngresos DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5AgenciasMasVentas(@Param("fechaDesde") OffsetDateTime fechaDesde,
                                              @Param("fechaHasta") OffsetDateTime fechaHasta);
}
