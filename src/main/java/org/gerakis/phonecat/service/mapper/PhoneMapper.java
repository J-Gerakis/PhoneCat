package org.gerakis.phonecat.service.mapper;

import org.gerakis.phonecat.data.PhoneEntity;
import org.gerakis.phonecat.service.dto.PhoneDTO;

public class PhoneMapper {
    public static PhoneDTO entityToDTO(PhoneEntity phoneEntity) {
        return new PhoneDTO(
                phoneEntity.getPhoneId(),
                phoneEntity.getBrand(),
                phoneEntity.getModel(),
                phoneEntity.isAvailable(),
                phoneEntity.getBorrowerUsername(),
                phoneEntity.getBorrowDate(),
                SpecMapper.entityToDto(phoneEntity.getSpecRef())
        );
    }

    public static PhoneEntity DTOtoEntity(PhoneDTO phoneDTO) {
        PhoneEntity entity = new PhoneEntity();
        entity.setPhoneId(phoneDTO.phoneId());
        entity.setBrand(phoneDTO.brand());
        entity.setModel(phoneDTO.model());
        entity.setAvailable(phoneDTO.isAvailable());
        entity.setBorrowerUsername(phoneDTO.borrowerUsername());
        entity.setBorrowDate(phoneDTO.borrowDate());
        entity.setSpecRef(SpecMapper.dtoToEntity(phoneDTO.specRef()));
        return entity;
    }
}
