package org.gerakis.phonecat.service.mapper;

import org.gerakis.phonecat.infrastructure.SpecificationEntity;
import org.gerakis.phonecat.service.dto.FonoApiResponseDTO;
import org.gerakis.phonecat.service.dto.SpecificationDTO;

public class SpecMapper {
    public static SpecificationEntity dtoToEntity(SpecificationDTO dto) {
        SpecificationEntity entity = new SpecificationEntity();
        entity.setBrandModel(dto.brandModel());
        entity.setTechnology(dto.technology());
        entity.setBands2g(dto.bands2g());
        entity.setBands3g(dto.bands3g());
        entity.setBands4g(dto.bands4g());
        return entity;
    }
}
