package ar.edu.unq.pdss22025.models.usuario;

import ar.edu.unq.pdss22025.models.usuario.menu.MenuComprador;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;

@Entity
@DiscriminatorValue("COMPRADOR")
public class UsuarioComprador extends Usuario {

    @PostLoad
    private void init() {
        initializeMenu();
    }

    public UsuarioComprador() {
        super();
        initializeMenu();
    }

    @Override
    protected void initializeMenu() {
        this.menu = new MenuComprador();
    }

    @Override
    public String getTipoUsuario() {
        return "COMPRADOR";
    }
}

