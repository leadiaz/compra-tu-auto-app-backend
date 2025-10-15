package ar.edu.unq.pdss22025.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COMPRADOR")
public class UsuarioComprador extends Usuario {
}

