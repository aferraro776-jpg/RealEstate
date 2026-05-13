package it.unical.progettoweb.scheduler;

import it.unical.progettoweb.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionService auctionService;

    @Scheduled(fixedDelay = 60000)
    public void chiudiAsteScadute() {
        auctionService.closeExpiredAuctions();
    }
}