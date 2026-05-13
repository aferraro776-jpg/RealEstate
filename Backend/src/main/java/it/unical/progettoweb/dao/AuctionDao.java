package it.unical.progettoweb.dao;

import it.unical.progettoweb.model.Auction;
import java.util.List;
import java.util.Optional;

public interface AuctionDao extends Dao<Auction, Integer> {

    Optional<Auction> findByPostId(int postId);
    List<Auction> findAllOpen();
    void updateCurrentBest(int auctionId, double amount);
    void setWinnerAndClose(int auctionId, int winnerId);
    void closeWithoutWinner(int auctionId);
}