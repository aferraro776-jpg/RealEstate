package it.unical.progettoweb.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuctionRequest {
    private int postId;
    private double startingPrice;
    private int durationDays;

    public LocalDateTime getEndDate() {
        return LocalDateTime.now().plusDays(durationDays);
    }
}