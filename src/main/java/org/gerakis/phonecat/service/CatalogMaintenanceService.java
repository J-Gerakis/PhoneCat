package org.gerakis.phonecat.service;

import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
import org.gerakis.phonecat.service.dto.*;
import org.gerakis.phonecat.service.mapper.SpecMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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
        NewSpecificationDTO specDto = null;
        NewPhoneDTO newPhoneDTO = new NewPhoneDTO(newPhoneReqDTO.brand(), newPhoneReqDTO.model(), null);
        //query fonoAPI here
        if(fonoActive) {
            specDto = apiCallService.getDeviceInfo(newPhoneReqDTO.brand(), newPhoneReqDTO.model());
        }
        if(specDto == null) {
            //check if exist else
            specDto = SpecMapper.fromNewPhoneRequest(newPhoneReqDTO);
        }
        Long specRefId = databaseService.addSpecification(specDto);
        newPhoneDTO = newPhoneDTO.withSpecRefId(specRefId);
        return databaseService.addPhone(newPhoneDTO);
    }

    public String addNewSpecification(NewSpecificationDTO specDto) {
        databaseService.addSpecification(specDto);
        return specDto.brandModel();
    }

    public List<PhoneDTO> getAllPhones() {
        //add filter criteria here
        return databaseService.getAllPhones();
    }

    public FullPhoneRecordDTO getFullPhoneRecord(Long phoneId) {
        return databaseService.getFullPhoneRecord(phoneId);
    }
}
