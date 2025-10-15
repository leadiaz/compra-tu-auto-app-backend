package ar.edu.unq.pdss22025.mapper;

import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.models.dto.FavoritoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface FavoritoMapper {

    @Mappings({
            @Mapping(source = "usuario.id", target = "usuarioId"),
            @Mapping(source = "oferta.id", target = "ofertaId")
    })
    FavoritoResponse toResponse(Favorito entity);
}
