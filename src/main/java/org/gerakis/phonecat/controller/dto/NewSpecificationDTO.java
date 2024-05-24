package org.gerakis.phonecat.controller.dto;

import com.google.gson.annotations.SerializedName;
import org.apache.logging.log4j.util.Strings;

import static org.gerakis.phonecat.util.FilterUtil.ALPHANUM_ONLY;

public record NewSpecificationDTO(
        @SerializedName("brand") String brand,
        @SerializedName("model") String model,
        String technology,
        @SerializedName("_2g_bands") String bands2g,
        @SerializedName("_3g_bands") String bands3g,
        @SerializedName("_4g_bands") String bands4g
) {
    public NewSpecificationDTO {
        //sanitization
        brand = Strings.isEmpty(brand) ? brand : brand.strip().replaceAll(ALPHANUM_ONLY, "");
        model = Strings.isEmpty(model) ? model : model.strip().replaceAll(ALPHANUM_ONLY, "");
        technology = Strings.isEmpty(technology) ? technology : technology.strip().replaceAll(ALPHANUM_ONLY, "");
        bands2g = Strings.isEmpty(bands2g) ? bands2g : bands2g.strip().replaceAll(ALPHANUM_ONLY, "");
        bands3g = Strings.isEmpty(bands3g) ? bands3g : bands3g.strip().replaceAll(ALPHANUM_ONLY, "");
        bands4g = Strings.isEmpty(bands4g) ? bands4g : bands4g.strip().replaceAll(ALPHANUM_ONLY, "");
    }
}
