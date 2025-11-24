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
                .route("/ofertas")
                .orden(1)
                .build());
        
        items.add(MenuItem.builder()
                .label("Mi Favorito")
                .icon("heart")
                .route("/favorito")
                .orden(2)
                .build());
        
        items.add(MenuItem.builder()
                .label("Mis Compras")
                .icon("receipt")
                .route("/compras")
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

