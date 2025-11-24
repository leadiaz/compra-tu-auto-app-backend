package ar.edu.unq.pdss22025.models.usuario.menu;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuConcesionaria implements Menu {

    @Override
    public List<MenuItem> getMenu() {
        List<MenuItem> items = new ArrayList<>();
        
        items.add(MenuItem.builder()
                .label("Mis Ofertas")
                .icon("store")
                .route("/ofertas")
                .orden(1)
                .build());
        
        items.add(MenuItem.builder()
                .label("Ventas")
                .icon("chart-line")
                .route("/ventas")
                .orden(2)
                .build());
        
        items.add(MenuItem.builder()
                .label("Clientes")
                .icon("users")
                .route("/clientes")
                .orden(3)
                .build());
        
        items.add(MenuItem.builder()
                .label("Reseñas")
                .icon("star")
                .route("/resenas")
                .orden(4)
                .build());
        
        return items;
    }
}

