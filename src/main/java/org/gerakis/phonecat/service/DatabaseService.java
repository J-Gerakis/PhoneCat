package org.gerakis.phonecat.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.logging.log4j.util.Strings;
import org.gerakis.phonecat.infrastructure.PhoneEntity;
import org.gerakis.phonecat.infrastructure.SpecificationEntity;
import org.gerakis.phonecat.service.dto.*;
import org.gerakis.phonecat.service.mapper.PhoneMapper;
import org.gerakis.phonecat.service.mapper.SpecMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DatabaseService {

    @PersistenceContext
    private EntityManager entityManager;

    public Long addPhone(NewPhoneDTO newPhoneDTO) {
        return addPhone(newPhoneDTO.brand(), newPhoneDTO.model(), newPhoneDTO.specRefId());
    }

    public Long addPhone(String brand, String model, Long specRefId) {
        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setBrand(brand);
        phoneEntity.setModel(model);
        phoneEntity.setAvailable(true);
        phoneEntity.setBorrowerUsername(Strings.EMPTY);
        phoneEntity.setBorrowDate(null);
        phoneEntity.setSpecRefId(specRefId);
        entityManager.persist(phoneEntity);
        entityManager.flush();
        return phoneEntity.getPhoneId();
    }

    public Long addSpecification(NewSpecificationDTO newSpecificationDTO) {
        SpecificationEntity specEntity = new SpecificationEntity();
        specEntity.setBrandModel(newSpecificationDTO.brandModel().trim().toLowerCase());
        specEntity.setTechnology(newSpecificationDTO.technology());
        specEntity.setBands2g(newSpecificationDTO.bands2g());
        specEntity.setBands3g(newSpecificationDTO.bands3g());
        specEntity.setBands4g(newSpecificationDTO.bands4g());
        entityManager.persist(specEntity);
        entityManager.flush();
        return specEntity.getSpecRefId();
    }

    public Optional<SpecificationDTO> findSpecification(String brandModel) {
        Query namedQuery = entityManager.createNamedQuery("Spec.search");
        namedQuery.setParameter("brandmodel", brandModel);
        SpecificationEntity specEntity = (SpecificationEntity) namedQuery.getSingleResult();
        if(specEntity != null)
            return Optional.of(SpecMapper.entityToDto(specEntity));
        else
            return Optional.empty();
    }

    public void updatePhone(PhoneDTO phoneDTO) {
        entityManager.merge(PhoneMapper.DTOtoEntity(phoneDTO));
    }

    public Optional<PhoneDTO> getPhone(Long phoneId) {
        PhoneEntity phoneEntity = entityManager.find(PhoneEntity.class, phoneId);
        if (phoneEntity == null) {
            return Optional.empty();
        }
        return Optional.of(PhoneMapper.entityToDTO(phoneEntity));
    }

    public Optional<FullPhoneRecordDTO> getFullPhoneRecord(Long phoneId) {
        Query namedQuery = entityManager.createNamedQuery("Phone.findFullRecord");
        namedQuery.setParameter("id", phoneId);
        return Optional.of((FullPhoneRecordDTO) namedQuery.getSingleResult());
    }

    public List<FullPhoneRecordDTO> getAllPhones() {
        Query namedQuery = entityManager.createNamedQuery("Phone.search");
        return namedQuery.getResultList();
    }

    public void deletePhone(Long phoneId) {
        entityManager.remove(phoneId);
    }

}
