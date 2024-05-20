package org.gerakis.phonecat.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.gerakis.phonecat.controller.dto.NewPhoneRequestDTO;
import org.gerakis.phonecat.service.CatalogMaintenanceService;
import org.gerakis.phonecat.service.dto.NewPhoneDTO;
import org.gerakis.phonecat.service.dto.PhoneDTO;
import org.gerakis.phonecat.util.DateAdapter;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("Catalog Content Controller")
@RequestMapping(path = "${api.name}/${api.version}/phones")
public class CatalogController {

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
        return catalogService.addNewPhone(newPhoneReqDTO).toString();
    }

    @GetMapping("list")
    public String getList() {
        List<PhoneDTO> list = catalogService.getAllPhones();
        return gson.toJson(list);
    }

    @GetMapping("{phone_id}")
    public String getById(@PathVariable("phone_id") Long phoneId) {
        return gson.toJson(catalogService.getFullPhoneRecord(phoneId));
    }

}
