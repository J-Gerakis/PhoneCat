package org.gerakis.phonecat.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
import org.gerakis.phonecat.controller.dto.NewSpecificationDTO;
import org.gerakis.phonecat.data.PhoneCatRepository;
import org.gerakis.phonecat.service.dto.*;
import org.gerakis.phonecat.service.mapper.SpecMapper;
import org.gerakis.phonecat.util.FilterUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CatalogMaintenanceService {

    public final static Logger logger = LogManager.getLogger(CatalogMaintenanceService.class);

    @Value("${fonoapi.active}")
    private boolean fonoActive;

    public final PhoneCatRepository phoneCatRepository;

    public final ApiCallService apiCallService;

    public CatalogMaintenanceService(PhoneCatRepository databaseService, ApiCallService apiCallService) {
        this.phoneCatRepository = databaseService;
        this.apiCallService = apiCallService;
    }

    public Long addNewPhone(NewPhoneRequestDTO newPhoneReqDTO) {
        NewPhoneDTO newPhoneDTO = new NewPhoneDTO(newPhoneReqDTO.brand(), newPhoneReqDTO.model(), null);
        Long specRefId = getOrCreateSpecification(newPhoneReqDTO);
        logger.debug("Spec id used: {}", specRefId);
        newPhoneDTO = newPhoneDTO.withSpecRefId(specRefId);
        return phoneCatRepository.addPhone(newPhoneDTO);
    }

    public Long addOrUpdateNewSpecification(NewSpecificationDTO specDto) {
        //check if a spec already exist for this brand/model combination
        Long specRefId = phoneCatRepository.findAndUpdateSpecification(specDto);
        if(specRefId == null) {
            specRefId = phoneCatRepository.addSpecification(specDto);
            logger.debug("Spec ref ID created: {}", specRefId);
            //update the spec ref id field for all phones of this brand/model
            phoneCatRepository.updatePhoneSpec(specRefId, specDto.brand(), specDto.model());
        } else {
            logger.debug("Spec ref updated: {}", specRefId);
        }

        return specRefId;
    }

    public void deletePhone(Long phoneId) {
        phoneCatRepository.deletePhone(phoneId);
    }

    public List<PhoneDTO> getAllPhones(Map<String, String> filter) {
        List<PhoneDTO> list = phoneCatRepository.getAllPhones(filter);
        logger.debug("Pre filter list size {}", list.size());
        return list; //todo: replace filter with query parameters
        //return list.stream().filter(s-> applyCriteria(s, filter)).toList();
    }

//    public Optional<FullPhoneRecordDTO> getFullPhoneRecord(Long phoneId) {
//        return phoneCatRepository.getFullPhoneRecord(phoneId);
//    }

    public Optional<PhoneDTO> getPhone(Long phoneId) {
        return phoneCatRepository.getPhone(phoneId);
    }

//    private boolean applyCriteria(PhoneDTO recordDTO, Map<String, String> filter) {
//        boolean match = true;
//        if(filter.containsKey(FilterUtil.BRAND)) {
//            match = match && recordDTO.brand().equalsIgnoreCase(filter.get(FilterUtil.BRAND));
//        }
//        if(filter.containsKey(FilterUtil.MODEL)) {
//            match = match && recordDTO.model().equalsIgnoreCase(filter.get(FilterUtil.MODEL));
//        }
//        if(filter.containsKey(FilterUtil.AVAILABLE)) {
//            match = match && recordDTO.isAvailable().equals(Boolean.parseBoolean(filter.get(FilterUtil.AVAILABLE)));
//        }
//        return match;
//    }

    private Long getOrCreateSpecification(NewPhoneRequestDTO newPhoneReqDTO) {
        NewSpecificationDTO specDto = null;
        if(fonoActive) {
            logger.debug("Querying FonoAPI");
            specDto = apiCallService.getDeviceInfo(newPhoneReqDTO.brand(), newPhoneReqDTO.model());
        }
        if(specDto == null) {
            Optional<SpecificationDTO> existingSpec = phoneCatRepository.findSpecification(newPhoneReqDTO.brand(), newPhoneReqDTO.model());
            if(existingSpec.isPresent()) {
                logger.debug("found existing spec {},{} {}", existingSpec.get().specRefId(), existingSpec.get().brand(), existingSpec.get().model());
                return existingSpec.get().specRefId();
            }
            // else get from user input
            specDto = SpecMapper.fromNewPhoneRequest(newPhoneReqDTO);
        }
        logger.debug("adding specification to db");
        return phoneCatRepository.addSpecification(specDto);
    }

}
