package it.unical.progettoweb.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GarageRequest extends RealEstateRequest {
    private Double width;
    private Double height;
    private Boolean isElectric;
}
