package org.gerakis.phonecat.service.dto;

public record NewPhoneDTO(
        String brand,
        String model,
        Long specRefId
) {
    public NewPhoneDTO withSpecRefId(Long specRefId) {
        return new NewPhoneDTO(brand, model, specRefId);
    }
}
