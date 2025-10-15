package ar.edu.unq.pdss22025.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class UsuarioAdmin extends Usuario {
}

