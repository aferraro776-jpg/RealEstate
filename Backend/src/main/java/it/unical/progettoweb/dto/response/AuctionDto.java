package it.unical.progettoweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDto {
    private Integer id;
    private int postId;
    private double startingPrice;
    private double currentBest;
    private LocalDateTime endDate;
    private boolean closed;
    private Integer winnerId;
    private List<BidDto> bids;
}