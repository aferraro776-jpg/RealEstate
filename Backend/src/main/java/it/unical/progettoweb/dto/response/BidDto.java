package it.unical.progettoweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidDto {
    private Integer id;
    private int auctionId;
    private int userId;
    private double amount;
    private LocalDateTime placedAt;
}