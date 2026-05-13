package it.unical.progettoweb.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Bid {
    private Integer id;
    private int auctionId;
    private int userId;
    private double amount;
    private LocalDateTime placedAt;
}