package ar.edu.unq.pdss22025.models.usuario.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    private String label;
    private String icon;
    private String route;
    private Integer orden;
}

