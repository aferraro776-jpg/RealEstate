package it.unical.progettoweb.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VillaDto extends RealEstateDto {
    private Boolean hasGarden;
    private Boolean hasPool;
    private Integer numberOfFloors;

    public VillaDto(int id, String title, int numberOfRooms, String description,
                    double squareMetres, double latit, double longit, String address,
                    LocalDateTime createdAt, String type,
                    Boolean hasGarden, Boolean hasPool, Integer numberOfFloors) {
        super(id, title, numberOfRooms, description, squareMetres, latit, longit, address, createdAt, type);
        this.hasGarden = hasGarden;
        this.hasPool = hasPool;
        this.numberOfFloors = numberOfFloors;
    }
}

