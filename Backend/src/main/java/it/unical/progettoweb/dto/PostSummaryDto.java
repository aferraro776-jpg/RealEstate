package it.unical.progettoweb.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostSummaryDto {
    private int postId;
    private String title;
    private String description;
    private double currentPrice;
    private double previousPrice;
    private String transactionType;
    private boolean isAuction;
    private int realEstateId;
    private String realEstateType;
    private double squareMeters;
    private String address;
}

