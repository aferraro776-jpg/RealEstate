package it.unical.progettoweb.proxy;

import it.unical.progettoweb.mapper.BidRowMapper;
import it.unical.progettoweb.model.Bid;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class BidProxy implements BidCollection {

    private final int auctionId;
    private final JdbcTemplate jdbcTemplate;
    private final BidRowMapper bidRowMapper;
    private BidList realBidList = null;

    public BidProxy(int auctionId, JdbcTemplate jdbcTemplate, BidRowMapper bidRowMapper) {
        this.auctionId = auctionId;
        this.jdbcTemplate = jdbcTemplate;
        this.bidRowMapper = bidRowMapper;
    }

    @Override
    public List<Bid> getBids() {
        if (realBidList == null) {
            String sql = "SELECT * FROM bids WHERE id_auction = ? ORDER BY amount DESC";
            List<Bid> bidsFromDb = jdbcTemplate.query(sql, bidRowMapper, auctionId);
            realBidList = new BidList(bidsFromDb);
        }
        return realBidList.getBids();
    }
}