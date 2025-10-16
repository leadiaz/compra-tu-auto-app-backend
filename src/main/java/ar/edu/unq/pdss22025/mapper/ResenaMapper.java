package ar.edu.unq.pdss22025.mapper;

import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.models.dto.ResenaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ResenaMapper {

    @Mappings({
            @Mapping(source = "auto.id", target = "autoId"),
            @Mapping(source = "usuario.id", target = "usuarioId"),
            @Mapping(source = "createdAt", target = "createdAt")
    })
    ResenaResponse toResponse(Resena entity);
}
