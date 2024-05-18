package org.gerakis.phonecat.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.util.Strings;
import org.gerakis.phonecat.infrastructure.PhoneEntity;
import org.gerakis.phonecat.service.dto.NewPhoneDTO;
import org.gerakis.phonecat.service.dto.PhoneDTO;
import org.gerakis.phonecat.service.dto.SpecificationDTO;
import org.gerakis.phonecat.service.mapper.PhoneMapper;
import org.gerakis.phonecat.service.mapper.SpecMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DatabaseService {

    @PersistenceContext
    private EntityManager entityManager;

    public Long addPhone(NewPhoneDTO newPhoneDTO) {
        return addPhone(newPhoneDTO.brand(), newPhoneDTO.model());
    }

    public Long addPhone(String brand, String model) {
        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setBrand(brand);
        phoneEntity.setModel(model);
        phoneEntity.setAvailable(true);
        phoneEntity.setBorrowerUsername(Strings.EMPTY);
        phoneEntity.setBorrowDate(null);
        entityManager.persist(phoneEntity);
        entityManager.flush();
        return phoneEntity.getPhoneId();
    }

    public void addSpecification(SpecificationDTO specificationDTO) {
        entityManager.persist(SpecMapper.dtoToEntity(specificationDTO));
    }

    public void update(PhoneDTO phoneDTO) {
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

    public List<PhoneDTO> getAllPhones() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
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
