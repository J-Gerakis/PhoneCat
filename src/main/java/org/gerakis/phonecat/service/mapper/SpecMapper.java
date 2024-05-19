package org.gerakis.phonecat.service.mapper;

import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
import org.gerakis.phonecat.infrastructure.SpecificationEntity;
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

    public static SpecificationDTO fromNewPhoneRequest(NewPhoneRequestDTO newRequestDTO) {
        return new SpecificationDTO(
                formatBrandModel(newRequestDTO.brand(), newRequestDTO.model()),
                newRequestDTO.technology(),
                newRequestDTO.bands2g(),
                newRequestDTO.bands3g(),
                newRequestDTO.bands4g()
        );
    }

    public static String formatBrandModel(String brand, String model) {
        return (brand.toLowerCase() + "_" + model.toLowerCase()).replaceAll(" ", "_");
    }

}
