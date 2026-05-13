package it.unical.progettoweb.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GarageDto extends RealEstateDto {
    private Double width;
    private Double height;
    private Boolean isElectric;

    public GarageDto(int id, String title, int numberOfRooms, String description,
                     double squareMetres, double latit, double longit, String address,
                     LocalDateTime createdAt, String type,
                     Double width, Double height, Boolean isElectric) {
        super(id, title, numberOfRooms, description, squareMetres, latit, longit, address, createdAt, type);
        this.width = width;
        this.height = height;
        this.isElectric = isElectric;
    }
}

