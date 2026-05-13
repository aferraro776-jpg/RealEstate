package it.unical.progettoweb.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NonBuildingLotRequest extends RealEstateRequest {
    private String cropType;
}
