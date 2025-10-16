package ar.edu.unq.pdss22025.models.usuario;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CONCESIONARIA")
public class UsuarioConcesionaria extends Usuario {
}

