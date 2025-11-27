package ar.edu.unq.pdss22025.models.usuario.menu;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class MenuAdmin implements Menu {

    @Override
    public List<MenuItem> getMenu() {
        List<MenuItem> items = new ArrayList<>();
        
        items.add(MenuItem.builder()
                .label("Usuarios")
                .icon("users")
                .route("/usuarios")
                .orden(1)
                .build());
        
        items.add(MenuItem.builder()
                .label("Concesionarias")
                .icon("store")
                .route("/concesionarias")
                .orden(2)
                .build());
        
        items.add(MenuItem.builder()
                .label("Autos")
                .icon("car")
                .route("/autos")
                .orden(3)
                .build());
        
        items.add(MenuItem.builder()
                .label("Compras")
                .icon("receipt")
                .route("/compras")
                .orden(4)
                .build());
        
        items.add(MenuItem.builder()
                .label("Favoritos")
                .icon("heart")
                .route("/favoritos")
                .orden(5)
                .build());
        
        items.add(MenuItem.builder()
                .label("Reseñas")
                .icon("star")
                .route("/resenas")
                .orden(6)
                .build());
        
        items.add(MenuItem.builder()
                .label("Reportes")
                .icon("chart-bar")
                .route("/reportes")
                .orden(7)
                .build());
        
        return items;
    }
}

