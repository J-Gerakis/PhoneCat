package org.gerakis.phonecat.service;

import org.gerakis.phonecat.service.dto.NewPhoneDTO;
import org.gerakis.phonecat.service.dto.PhoneDTO;
import org.gerakis.phonecat.service.dto.SpecificationDTO;
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

    public Long addNewPhone(NewPhoneDTO newPhoneDTO) {
        //query fonoAPI here
        if(fonoActive) {
            SpecificationDTO specDto = apiCallService.getDeviceInfo(newPhoneDTO.brand(), newPhoneDTO.model());
            if(specDto != null) {
                databaseService.addSpecification(specDto);
            }
        }

        return databaseService.addPhone(newPhoneDTO);
    }

    public String addNewSpecification(SpecificationDTO specDto) {
        databaseService.addSpecification(specDto);
        return specDto.brandModel();
    }

    public List<PhoneDTO> getAllPhones() {
        //add filter criteria here
        return databaseService.getAllPhones();
    }
}
