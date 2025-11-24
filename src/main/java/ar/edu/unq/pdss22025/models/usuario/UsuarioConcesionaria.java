package ar.edu.unq.pdss22025.models.usuario;

import ar.edu.unq.pdss22025.models.usuario.menu.MenuConcesionaria;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;

@Entity
@DiscriminatorValue("CONCESIONARIA")
public class UsuarioConcesionaria extends Usuario {

    @PostLoad
    private void init() {
        initializeMenu();
    }

    public UsuarioConcesionaria() {
        super();
        initializeMenu();
    }

    @Override
    protected void initializeMenu() {
        this.menu = new MenuConcesionaria();
    }

    @Override
    public String getTipoUsuario() {
        return "CONCESIONARIA";
    }
}

