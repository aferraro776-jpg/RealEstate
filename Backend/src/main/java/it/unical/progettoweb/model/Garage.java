package it.unical.progettoweb.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Garage extends RealEstate {
    private Double width;
    private Double height;
    private Boolean isElectric;
}