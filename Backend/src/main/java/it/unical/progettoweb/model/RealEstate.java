package it.unical.progettoweb.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public abstract class RealEstate {
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
