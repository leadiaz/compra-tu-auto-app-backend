package ar.edu.unq.pdss22025.mapper;

import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.models.dto.FavoritoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface FavoritoMapper {

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }

    @Mappings({
            @Mapping(source = "usuario.id", target = "usuarioId"),
            @Mapping(source = "usuario.nombre", target = "usuarioNombre"),
            @Mapping(source = "oferta.id", target = "ofertaId"),
            @Mapping(source = "oferta.auto.id", target = "autoId"),
            @Mapping(source = "oferta.auto.marca", target = "autoMarca"),
            @Mapping(source = "oferta.auto.modelo", target = "autoModelo"),
            @Mapping(source = "oferta.auto.anioModelo", target = "autoAnioModelo"),
            @Mapping(source = "oferta.concesionaria.id", target = "concesionariaId"),
            @Mapping(source = "oferta.concesionaria.nombre", target = "concesionariaNombre"),
            @Mapping(source = "oferta.precioActual", target = "precioActual"),
            @Mapping(source = "oferta.moneda", target = "moneda"),
            @Mapping(source = "oferta.stock", target = "stock"),
            @Mapping(source = "createdAt", target = "fechaCreacion")
    })
    FavoritoResponse toResponse(Favorito entity);
}
