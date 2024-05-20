package org.gerakis.phonecat.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.util.Strings;
import org.gerakis.phonecat.infrastructure.PhoneEntity;
import org.gerakis.phonecat.infrastructure.SpecificationEntity;
import org.gerakis.phonecat.service.dto.*;
import org.gerakis.phonecat.service.mapper.PhoneMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        specEntity.setBrandModel(newSpecificationDTO.brandModel());
        specEntity.setTechnology(newSpecificationDTO.technology());
        specEntity.setBands2g(newSpecificationDTO.bands2g());
        specEntity.setBands3g(newSpecificationDTO.bands3g());
        specEntity.setBands4g(newSpecificationDTO.bands4g());
        entityManager.persist(specEntity);
        entityManager.flush();
        return specEntity.getSpecRefId();
    }

    public void updatePhone(PhoneDTO phoneDTO) {
        entityManager.merge(PhoneMapper.DTOtoEntity(phoneDTO));
    }

    public PhoneDTO getPhone(Long phoneId) {
        PhoneEntity phoneEntity = entityManager.find(PhoneEntity.class, phoneId);
        if (phoneEntity == null) {
            return null;
            //throw exception?
        }
        return PhoneMapper.entityToDTO(phoneEntity);
    }

    public FullPhoneRecordDTO getFullPhoneRecord(Long phoneId) {
        Query namedQuery = entityManager.createNamedQuery("Phone.findFullRecord");
        namedQuery.setParameter("id", phoneId);
        return (FullPhoneRecordDTO) namedQuery.getSingleResult();
    }

    public List<PhoneDTO> getAllPhones() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder(); //change for Full Phone record (typed query)
        CriteriaQuery<PhoneDTO> query = cb.createQuery(PhoneDTO.class);
        Root<PhoneEntity> root = query.from(PhoneEntity.class);
        query.select(cb.construct(PhoneDTO.class,
                root.get("phoneId"),
                root.get("brand"),
                root.get("model"),
                root.get("isAvailable"),
                root.get("borrowerUsername"),
                root.get("borrowDate")
        ));
        return entityManager.createQuery(query).getResultList();
    }


}
