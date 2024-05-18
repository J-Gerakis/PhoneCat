package org.gerakis.phonecat.service.dto;

public record FullPhoneRecordDTO(
        PhoneDTO entry,
        SpecificationDTO specification
) { }
