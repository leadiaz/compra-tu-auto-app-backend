package ar.edu.unq.pdss22025.mapper;

import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.models.dto.ResenaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface ResenaMapper {

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }

    @Mappings({
            @Mapping(source = "auto.id", target = "autoId"),
            @Mapping(source = "auto.marca", target = "autoMarca"),
            @Mapping(source = "auto.modelo", target = "autoModelo"),
            @Mapping(source = "auto.anioModelo", target = "autoAnioModelo"),
            @Mapping(source = "usuario.id", target = "usuarioId"),
            @Mapping(source = "usuario.nombre", target = "usuarioNombre"),
            @Mapping(source = "usuario.apellido", target = "usuarioApellido"),
            @Mapping(source = "rating", target = "puntaje"),
            @Mapping(source = "createdAt", target = "fechaCreacion"),
            @Mapping(source = "updatedAt", target = "fechaActualizacion")
    })
    ResenaResponse toResponse(Resena entity);
}
