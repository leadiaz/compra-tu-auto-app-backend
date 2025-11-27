package ar.edu.unq.pdss22025.models.usuario.menu;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuComprador implements Menu {

    @Override
    public List<MenuItem> getMenu() {
        List<MenuItem> items = new ArrayList<>();
        
        items.add(MenuItem.builder()
                .label("Ofertas")
                .icon("shopping-cart")
                .route("/dashboard/ofertas")
                .orden(1)
                .build());
        
        items.add(MenuItem.builder()
                .label("Mi Favorito")
                .icon("heart")
                .route("/dashboard/favoritos")
                .orden(2)
                .build());
        
        items.add(MenuItem.builder()
                .label("Mis Compras")
                .icon("receipt")
                .route("/dashboard/mis-compras")
                .orden(3)
                .build());
        
        return items;
    }
}

