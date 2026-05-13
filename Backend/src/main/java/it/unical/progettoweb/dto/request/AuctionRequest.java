package it.unical.progettoweb.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AuctionRequest {
    private int postId;
    private double startingPrice;
    private LocalDateTime endDate;
}