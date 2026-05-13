package it.unical.progettoweb.dao;

import it.unical.progettoweb.model.Bid;
import java.util.List;
import java.util.Optional;

public interface BidDao extends Dao<Bid, Integer> {

    List<Bid> findByAuctionId(int auctionId);
    Optional<Bid> findBestBid(int auctionId);
}