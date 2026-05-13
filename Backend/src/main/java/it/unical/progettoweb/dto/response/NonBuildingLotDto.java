package it.unical.progettoweb.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NonBuildingLotDto extends RealEstateDto {
    private String cropType;

    public NonBuildingLotDto(int id, String title, int numberOfRooms, String description,
                             double squareMetres, double latit, double longit, String address,
                             LocalDateTime createdAt, String type,
                             String cropType) {
        super(id, title, numberOfRooms, description, squareMetres, latit, longit, address, createdAt, type);
        this.cropType = cropType;
    }
}