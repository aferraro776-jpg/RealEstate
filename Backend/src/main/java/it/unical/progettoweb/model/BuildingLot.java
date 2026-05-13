package it.unical.progettoweb.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BuildingLot extends RealEstate {
    private Double cubature;
    private String plannedUse;
}