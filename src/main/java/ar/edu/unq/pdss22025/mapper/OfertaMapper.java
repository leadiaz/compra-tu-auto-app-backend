package ar.edu.unq.pdss22025.mapper;

import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.dto.OfertaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OfertaMapper {

    @Mappings({
            @Mapping(source = "auto.id", target = "autoId"),
            @Mapping(source = "concesionaria.id", target = "concesionariaId"),
            @Mapping(target = "titulo", ignore = true),
            @Mapping(target = "descripcion", ignore = true),
            @Mapping(source = "precioActual", target = "precio"),
            @Mapping(target = "estado", ignore = true)
    })
    OfertaResponse toResponse(OfertaAuto entity);
}
