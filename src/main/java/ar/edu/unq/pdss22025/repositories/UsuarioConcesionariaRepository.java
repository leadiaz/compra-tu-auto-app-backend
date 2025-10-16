package ar.edu.unq.pdss22025.repositories;

import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioConcesionariaRepository extends JpaRepository<UsuarioConcesionaria, Long> {
}
