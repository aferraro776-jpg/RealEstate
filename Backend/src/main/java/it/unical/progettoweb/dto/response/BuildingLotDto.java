package it.unical.progettoweb.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BuildingLotDto extends RealEstateDto {
    private Double cubature;
    private String plannedUse;

    public BuildingLotDto(int id, String title, int numberOfRooms, String description,
                          double squareMetres, double latit, double longit, String address,
                          LocalDateTime createdAt, String type,
                          Double cubature, String plannedUse) {
        super(id, title, numberOfRooms, description, squareMetres, latit, longit, address, createdAt, type);
        this.cubature = cubature;
        this.plannedUse = plannedUse;
    }
}