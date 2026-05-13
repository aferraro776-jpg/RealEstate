package it.unical.progettoweb.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApartmentDto extends RealEstateDto {
    private Integer floor;
    private Boolean hasElevator;

    public ApartmentDto(int id, String title, int numberOfRooms, String description,
                        double squareMetres, double latit, double longit, String address,
                        LocalDateTime createdAt, String type,
                        Integer floor, Boolean hasElevator) {
        super(id, title, numberOfRooms, description, squareMetres, latit, longit, address, createdAt, type);
        this.floor = floor;
        this.hasElevator = hasElevator;
    }
}