package org.gerakis.phonecat.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import jakarta.websocket.server.PathParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
import org.gerakis.phonecat.controller.dto.MessageDTO;
import org.gerakis.phonecat.exception.PhoneCatException;
import org.gerakis.phonecat.service.CatalogMaintenanceService;
import org.gerakis.phonecat.service.dto.FullPhoneRecordDTO;
import org.gerakis.phonecat.controller.dto.NewSpecificationDTO;
import org.gerakis.phonecat.util.DateAdapter;
import org.gerakis.phonecat.util.FilterUtil;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController("Catalog Content Controller")
@RequestMapping(path = "${api.name}/${api.version}/phones")
public class CatalogController {

    public final static Logger logger = LogManager.getLogger(CatalogController.class);

    private static final String MSG_PHONE_CREATED = "Phone added to catalog";
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
    public String addNewPhone(@RequestBody String body) throws PhoneCatException {
        try {
            logger.debug(body);
            NewPhoneRequestDTO newPhoneReqDTO = gson.fromJson(body, NewPhoneRequestDTO.class);
            if(newPhoneReqDTO.brand().isEmpty() || newPhoneReqDTO.model().isEmpty()) {
                throw new PhoneCatException("Brand and model required to add a new phone");
            }
            Long newPhoneId = catalogService.addNewPhone(newPhoneReqDTO);
            logger.debug("phone created {}", newPhoneId);
            return gson.toJson(new MessageDTO(MSG_PHONE_CREATED, newPhoneId));
        } catch (JsonSyntaxException jse) {
            logger.error(jse);
            throw new PhoneCatException("Malformed payload", jse);
        }
    }

    @DeleteMapping("delete/{phone_id}")
    public String deletePhone(@PathVariable("phone_id") Long phoneId) throws PhoneCatException {
        if(phoneId < 1) {
            throw new PhoneCatException("Phone id must be positive");
        }
        catalogService.deletePhone(phoneId);
        return gson.toJson(new MessageDTO(MSG_PHONE_DELETED, phoneId));
    }

    @PutMapping("newSpecification")
    public String addNewSpecification(@RequestBody String body) throws PhoneCatException {
        try {
            logger.debug(body);
            NewSpecificationDTO newSpectDto = gson.fromJson(body, NewSpecificationDTO.class);
            if(newSpectDto.brand().isEmpty() || newSpectDto.model().isEmpty()) {
                throw new PhoneCatException("Brand and model required to create a new specification");
            }
            Long newSpecId = catalogService.addOrUpdateNewSpecification(newSpectDto);
            return gson.toJson(new MessageDTO(MSG_SPECIFICATION, newSpecId));
        } catch (JsonSyntaxException jse) {
            logger.error(jse);
            throw new PhoneCatException("Malformed payload", jse);
        }
    }

    @GetMapping("list")
    public String getList(@PathParam(FilterUtil.BRAND) Optional<String> brand,
                          @PathParam(FilterUtil.MODEL) Optional<String> model,
                          @PathParam(FilterUtil.AVAILABLE) Optional<String> available) {
        Map<String, String> filterParam = new HashMap<>();
        brand.ifPresent(s -> filterParam.put(FilterUtil.BRAND, s));
        model.ifPresent(s -> filterParam.put(FilterUtil.MODEL, s));
        available.ifPresent(s -> filterParam.put(FilterUtil.AVAILABLE, s));
        logger.debug("Filter param: {}, {}, {}", brand, model, available);
        List<FullPhoneRecordDTO> list = catalogService.getAllPhones(filterParam);
        logger.debug("List size: {}", list.size());
        return gson.toJson(list);
    }

    @GetMapping("{phone_id}")
    public String getById(@PathVariable("phone_id") Long phoneId) throws PhoneCatException {
        if(phoneId < 1) {
            logger.warn("negative phone Id");
            throw new PhoneCatException("Phone id must be positive");
        }
        Optional<FullPhoneRecordDTO> record = catalogService.getFullPhoneRecord(phoneId);
        if(record.isPresent()) {
            logger.debug("phone found: {}", phoneId);
            return gson.toJson(record.get());
        } else return gson.toJson(new MessageDTO(MSG_PHONE_NOTFOUND, phoneId));
    }

}
