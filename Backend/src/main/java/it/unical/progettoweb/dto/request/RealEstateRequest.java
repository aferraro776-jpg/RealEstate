package it.unical.progettoweb.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApartmentRequest.class,      name = "APARTMENT"),
        @JsonSubTypes.Type(value = VillaRequest.class,          name = "VILLA"),
        @JsonSubTypes.Type(value = GarageRequest.class,         name = "GARAGE"),
        @JsonSubTypes.Type(value = BuildingLotRequest.class,    name = "BUILDING_LOT"),
        @JsonSubTypes.Type(value = NonBuildingLotRequest.class, name = "NON_BUILDING_LOT")
})
@Data
public abstract class RealEstateRequest {
    private String type;
    private String title;
    private int numberOfRooms;
    private String description;
    private double squareMetres;
    private String street;
    private String civicNumber;
    private String city;
    private String cap;
    private String province;
}