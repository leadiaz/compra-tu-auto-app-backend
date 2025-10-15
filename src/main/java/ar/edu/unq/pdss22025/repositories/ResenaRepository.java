package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByUsuario(Usuario usuario);
    
    @Query("SELECT r FROM Resena r WHERE r.auto = :auto ORDER BY r.createdAt DESC")
    List<Resena> findByAutoOrderByCreatedAtDesc(Auto auto);
    
    List<Resena> findByAuto(Auto auto);
}
