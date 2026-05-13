package it.unical.progettoweb.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApartmentRequest extends RealEstateRequest {
    private Integer floor;
    private Boolean hasElevator;
}
