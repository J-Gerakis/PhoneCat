package org.gerakis.phonecat.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.websocket.server.PathParam;
import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
import org.gerakis.phonecat.controller.dto.MessageDTO;
import org.gerakis.phonecat.service.CatalogMaintenanceService;
import org.gerakis.phonecat.service.dto.FullPhoneRecordDTO;
import org.gerakis.phonecat.service.dto.NewSpecificationDTO;
import org.gerakis.phonecat.util.DateAdapter;
import org.gerakis.phonecat.util.SearchParam;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController("Catalog Content Controller")
@RequestMapping(path = "${api.name}/${api.version}/phones")
public class CatalogController {

    private static final String MSG_PHONE_CREATED = "Phone added to catalog";
    private static final String MSG_PHONE_UPDATED = "Phone updated";
    private static final String MSG_PHONE_DELETED = "Phone deleted";
    private static final String MSG_PHONE_NOTFOUND = "Phone not found";
    private static final String MSG_SPECIFICATION = "Updated specification";

    private final CatalogMaintenanceService catalogService;

    private final Gson gson;

    public CatalogController(CatalogMaintenanceService catalogService) {
        this.catalogService = catalogService;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
                .serializeNulls()
                .create();
    }



    @PostMapping("add")
    public String addNewPhone(@RequestBody String body) {
        NewPhoneRequestDTO newPhoneReqDTO = gson.fromJson(body, NewPhoneRequestDTO.class);
        Long newPhoneId = catalogService.addNewPhone(newPhoneReqDTO);
        return gson.toJson(new MessageDTO(MSG_PHONE_CREATED, newPhoneId));
    }

    @DeleteMapping("delete/{phone_id}")
    public String deletePhone(@PathVariable("phone_id") Long phoneId) {
        catalogService.deletePhone(phoneId);
        return  gson.toJson(new MessageDTO(MSG_PHONE_DELETED, phoneId));
    }

    @PutMapping("newSpecification")
    public String addNewSpecification(@RequestBody String body) {
        NewSpecificationDTO newSpectDto = gson.fromJson(body, NewSpecificationDTO.class);
        Long newSpecId = catalogService.addNewSpecification(newSpectDto);
        return gson.toJson(new MessageDTO(MSG_SPECIFICATION, newSpecId));
    }

    @GetMapping("list")
    public String getList(@PathParam(SearchParam.BRAND) Optional<String> brand,
                          @PathParam(SearchParam.MODEL) Optional<String> model,
                          @PathParam(SearchParam.AVAILABLE) Optional<Boolean> available) {
        Map<String, Object> searchParam = new HashMap<>();
        brand.ifPresent(s -> searchParam.put(SearchParam.BRAND, s));
        model.ifPresent(s -> searchParam.put(SearchParam.MODEL, s));
        available.ifPresent(s -> searchParam.put(SearchParam.AVAILABLE, s));
        List<FullPhoneRecordDTO> list = catalogService.getAllPhones(searchParam);
        return gson.toJson(list);
    }

    @GetMapping("{phone_id}")
    public String getById(@PathVariable("phone_id") Long phoneId) {
        Optional<FullPhoneRecordDTO> record = catalogService.getFullPhoneRecord(phoneId);
        if(record.isPresent()) {
            return gson.toJson(record.get());
        } else return gson.toJson(new MessageDTO(MSG_PHONE_NOTFOUND, phoneId));
    }

}
