package org.gerakis.phonecat.service;

import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
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
        newPhoneDTO = newPhoneDTO.withSpecRefId(specRefId);
        return databaseService.addPhone(newPhoneDTO);
    }

    public Long addNewSpecification(NewSpecificationDTO specDto) {
        return databaseService.addSpecification(specDto);
    }

    public void deletePhone(Long phoneId) {
        databaseService.deletePhone(phoneId);
    }

    public List<FullPhoneRecordDTO> getAllPhones(Map<String, Object> filter) {
        List<FullPhoneRecordDTO> list = databaseService.getAllPhones();
        return list.stream().filter(s-> applyCriteria(s, filter)).toList();
    }

    public Optional<FullPhoneRecordDTO> getFullPhoneRecord(Long phoneId) {
        return databaseService.getFullPhoneRecord(phoneId);
    }

    private boolean applyCriteria(FullPhoneRecordDTO recordDTO, Map<String, Object> filter) {
        boolean match = true;
        if(filter.containsKey(SearchParam.BRAND)) {
            match = match && recordDTO.brand().equalsIgnoreCase(filter.get(SearchParam.BRAND).toString());
        }
        if(filter.containsKey(SearchParam.MODEL)) {
            match = match && recordDTO.model().equalsIgnoreCase(filter.get(SearchParam.MODEL).toString());
        }
        if(filter.containsKey(SearchParam.AVAILABLE)) {
            match = match && recordDTO.isAvailable().equals(filter.get(SearchParam.AVAILABLE));
        }
        return match;
    }

    private Long getSpecification(NewPhoneRequestDTO newPhoneReqDTO) {
        NewSpecificationDTO specDto = null;
        if(fonoActive) {
            specDto = apiCallService.getDeviceInfo(newPhoneReqDTO.brand(), newPhoneReqDTO.model());
        }
        if(specDto == null) {
            Optional<SpecificationDTO> existingSpec = databaseService.findSpecification(SpecMapper.formatBrandModel(newPhoneReqDTO.brand(), newPhoneReqDTO.model()));
            if(existingSpec.isPresent()) {
                return existingSpec.get().specRefId();
            }
            // else get from user input
            specDto = SpecMapper.fromNewPhoneRequest(newPhoneReqDTO);
        }
        return databaseService.addSpecification(specDto);
    }

}
