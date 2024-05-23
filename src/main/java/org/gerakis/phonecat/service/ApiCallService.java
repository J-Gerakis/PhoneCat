package org.gerakis.phonecat.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gerakis.phonecat.service.dto.FonoApiResponseDTO;
import org.gerakis.phonecat.controller.dto.NewSpecificationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiCallService {

    public final static Logger logger = LogManager.getLogger(ApiCallService.class);

    @Value("${fonoapi.token}")
    private String token;

    @Value("${fonoapi.url.getdevice}")
    private String getDeviceUrl;

    private final RestTemplate template;
    private final Gson gson;

    public ApiCallService() {
        this.template = new RestTemplate();
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    public NewSpecificationDTO getDeviceInfo(String brand, String model) {
        Map<String, String> params = new HashMap<>();
        params.put("brand", brand);
        params.put("device", model);
        params.put("token", token);
        params.put("position", "0");
        try{

            String content = template.getForObject(getDeviceUrl, String.class, params);
            logger.debug(content);
            FonoApiResponseDTO responseDTO = gson.fromJson(content, FonoApiResponseDTO.class);
            if(responseDTO != null){
                return new NewSpecificationDTO(
                        brand,
                        model,
                        responseDTO.technology(),
                        responseDTO._2gs_bands(),
                        responseDTO._3gs_bands(),
                        responseDTO._4gs_bands()
                );
            }
            logger.warn("no response from FonoAPI");

        } catch (RestClientException | JsonSyntaxException ex){
            logger.error(ex);
        }
        return null;
    }

}
