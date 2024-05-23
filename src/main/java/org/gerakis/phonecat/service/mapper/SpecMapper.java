package org.gerakis.phonecat.service.mapper;

import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
import org.gerakis.phonecat.data.SpecificationEntity;
import org.gerakis.phonecat.controller.dto.NewSpecificationDTO;
import org.gerakis.phonecat.service.dto.SpecificationDTO;

public class SpecMapper {

    public static SpecificationDTO entityToDto (SpecificationEntity entity) {
        return new SpecificationDTO(
                entity.getSpecRefId(),
                entity.getBrand(),
                entity.getModel(),
                entity.getTechnology(),
                entity.getBands2g(),
                entity.getBands3g(),
                entity.getBands4g()
        );
    }

    public static NewSpecificationDTO fromNewPhoneRequest(NewPhoneRequestDTO newRequestDTO) {
        return new NewSpecificationDTO(
                newRequestDTO.brand(),
                newRequestDTO.model(),
                newRequestDTO.technology(),
                newRequestDTO.bands2g(),
                newRequestDTO.bands3g(),
                newRequestDTO.bands4g()
        );
    }

}
