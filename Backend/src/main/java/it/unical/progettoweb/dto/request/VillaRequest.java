package it.unical.progettoweb.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VillaRequest extends RealEstateRequest {
    private Boolean hasGarden;
    private Boolean hasPool;
    private Integer numberOfFloors;
}
