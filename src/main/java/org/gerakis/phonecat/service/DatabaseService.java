package org.gerakis.phonecat.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.gerakis.phonecat.controller.dto.NewSpecificationDTO;
import org.gerakis.phonecat.data.PhoneEntity;
import org.gerakis.phonecat.data.SpecificationEntity;
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

    public final static Logger logger = LogManager.getLogger(DatabaseService.class);

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
        logger.debug("Creating new phone entry");
        entityManager.persist(phoneEntity);
        entityManager.flush();
        return phoneEntity.getPhoneId();
    }

    public Long addSpecification(NewSpecificationDTO newSpecificationDTO) {
        SpecificationEntity specEntity = new SpecificationEntity();
        specEntity.setBrandModel(SpecMapper.formatBrandModel(newSpecificationDTO.brand(), newSpecificationDTO.model()));
        specEntity.setTechnology(newSpecificationDTO.technology());
        specEntity.setBands2g(newSpecificationDTO.bands2g());
        specEntity.setBands3g(newSpecificationDTO.bands3g());
        specEntity.setBands4g(newSpecificationDTO.bands4g());
        logger.debug("creating new Spec entry");
        entityManager.persist(specEntity);
        entityManager.flush();
        return specEntity.getSpecRefId();
    }

    public void updatePhoneSpec(Long specRefId, String brand, String model) {
        logger.debug("updating phone specification ref id");
        Query namedQuery = entityManager.createNamedQuery("Phone.updateSpec");
        namedQuery.setParameter("brand", brand);
        namedQuery.setParameter("model", model);
        namedQuery.setParameter("spec_ref_id", specRefId);
        namedQuery.executeUpdate();
    }

    public Optional<SpecificationDTO> findSpecification(String brandModel) {
        Query namedQuery = entityManager.createNamedQuery("Spec.search");
        namedQuery.setParameter("brandmodel", brandModel);
        logger.debug("Looking up specification entry {}", brandModel);
        SpecificationEntity specEntity = (SpecificationEntity) namedQuery.getSingleResult();
        if(specEntity != null)
            return Optional.of(SpecMapper.entityToDto(specEntity));
        else
            return Optional.empty();
    }

    public void updatePhone(PhoneDTO phoneDTO) {
        logger.debug("updating phone status");
        entityManager.merge(PhoneMapper.DTOtoEntity(phoneDTO));
    }

    public Optional<PhoneDTO> getPhone(Long phoneId) {
        PhoneEntity phoneEntity = entityManager.find(PhoneEntity.class, phoneId);
        if (phoneEntity == null) {
            logger.debug("phone not found in DB {}", phoneId);
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
        logger.debug("executing query: {}", namedQuery.toString());
        return namedQuery.getResultList();
    }

    public void deletePhone(Long phoneId) {
        logger.debug("deleting phone entity id:{}", phoneId);
        entityManager.remove(phoneId);
    }

}
