package org.gerakis.phonecat.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
import org.gerakis.phonecat.controller.dto.NewSpecificationDTO;
import org.gerakis.phonecat.service.dto.*;
import org.gerakis.phonecat.service.mapper.SpecMapper;
import org.gerakis.phonecat.util.SearchParam;
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

    public final DatabaseService databaseService;

    public final ApiCallService apiCallService;

    public CatalogMaintenanceService(DatabaseService databaseService, ApiCallService apiCallService) {
        this.databaseService = databaseService;
        this.apiCallService = apiCallService;
    }

    public Long addNewPhone(NewPhoneRequestDTO newPhoneReqDTO) {
        NewPhoneDTO newPhoneDTO = new NewPhoneDTO(newPhoneReqDTO.brand(), newPhoneReqDTO.model(), null);
        Long specRefId = getSpecification(newPhoneReqDTO);
        logger.debug("Spec id used: {}", specRefId);
        newPhoneDTO = newPhoneDTO.withSpecRefId(specRefId);
        return databaseService.addPhone(newPhoneDTO);
    }

    public Long addNewSpecification(NewSpecificationDTO specDto) {
        Long newSpecRefId = databaseService.addSpecification(specDto);
        logger.debug("Spec ref ID created: {}", newSpecRefId);
        databaseService.updatePhoneSpec(newSpecRefId, specDto.brand(), specDto.model());
        return newSpecRefId;
    }

    public void deletePhone(Long phoneId) {
        databaseService.deletePhone(phoneId);
    }

    public List<FullPhoneRecordDTO> getAllPhones(Map<String, String> filter) {
        List<FullPhoneRecordDTO> list = databaseService.getAllPhones();
        logger.debug("Pre filter list size {}", list.size());
        return list.stream().filter(s-> applyCriteria(s, filter)).toList();
    }

    public Optional<FullPhoneRecordDTO> getFullPhoneRecord(Long phoneId) {
        return databaseService.getFullPhoneRecord(phoneId);
    }

    private boolean applyCriteria(FullPhoneRecordDTO recordDTO, Map<String, String> filter) {
        boolean match = true;
        if(filter.containsKey(SearchParam.BRAND)) {
            match = match && recordDTO.brand().equalsIgnoreCase(filter.get(SearchParam.BRAND));
        }
        if(filter.containsKey(SearchParam.MODEL)) {
            match = match && recordDTO.model().equalsIgnoreCase(filter.get(SearchParam.MODEL));
        }
        if(filter.containsKey(SearchParam.AVAILABLE)) {
            match = match && recordDTO.isAvailable().equals(Boolean.parseBoolean(filter.get(SearchParam.AVAILABLE)));
        }
        return match;
    }

    private Long getSpecification(NewPhoneRequestDTO newPhoneReqDTO) {
        NewSpecificationDTO specDto = null;
        if(fonoActive) {
            logger.debug("Querying FonoAPI");
            specDto = apiCallService.getDeviceInfo(newPhoneReqDTO.brand(), newPhoneReqDTO.model());
        }
        if(specDto == null) {
            Optional<SpecificationDTO> existingSpec = databaseService.findSpecification(SpecMapper.formatBrandModel(newPhoneReqDTO.brand(), newPhoneReqDTO.model()));
            if(existingSpec.isPresent()) {
                logger.debug("found existing spec {},{}", existingSpec.get().specRefId(), existingSpec.get().brandModel());
                return existingSpec.get().specRefId();
            }
            // else get from user input
            specDto = SpecMapper.fromNewPhoneRequest(newPhoneReqDTO);
        }
        logger.debug("adding specification to db");
        return databaseService.addSpecification(specDto);
    }

}
