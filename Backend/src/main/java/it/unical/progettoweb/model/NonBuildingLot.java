package it.unical.progettoweb.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NonBuildingLot extends RealEstate {
    private String cropType;
}