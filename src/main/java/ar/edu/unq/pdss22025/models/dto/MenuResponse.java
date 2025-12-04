package ar.edu.unq.pdss22025.models.dto;

import ar.edu.unq.pdss22025.models.usuario.menu.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {
    private List<MenuItem> items;
}

