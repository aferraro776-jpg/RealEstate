package it.unical.progettoweb.model;

import it.unical.progettoweb.model.RealEstate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Apartment extends RealEstate {
    private Integer floor;
    private Boolean hasElevator;
}