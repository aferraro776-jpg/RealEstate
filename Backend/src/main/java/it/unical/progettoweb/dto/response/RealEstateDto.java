package it.unical.progettoweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealEstateDto {
    private int id;
    private String title;
    private int numberOfRooms;
    private String description;
    private double squareMetres;
    private double latit;
    private double longit;
    private String address;
    private LocalDateTime createdAt;
    private String type;
}