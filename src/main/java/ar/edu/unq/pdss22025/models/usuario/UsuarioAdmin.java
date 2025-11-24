package ar.edu.unq.pdss22025.models.usuario;

import ar.edu.unq.pdss22025.models.usuario.menu.MenuAdmin;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;

@Entity
@DiscriminatorValue("ADMIN")
public class UsuarioAdmin extends Usuario {

    @PostLoad
    private void init() {
        initializeMenu();
    }

    public UsuarioAdmin() {
        super();
        initializeMenu();
    }

    @Override
    protected void initializeMenu() {
        this.menu = new MenuAdmin();
    }

    @Override
    public String getTipoUsuario() {
        return "ADMIN";
    }
}

