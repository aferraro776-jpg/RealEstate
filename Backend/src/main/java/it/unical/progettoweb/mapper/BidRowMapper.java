package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.Bid;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BidRowMapper implements RowMapper<Bid> {

    @Override
    public Bid mapRow(ResultSet rs, int rowNum) throws SQLException {
        Bid bid = new Bid();
        bid.setId(rs.getInt("id"));
        bid.setAuctionId(rs.getInt("id_auction"));
        bid.setUserId(rs.getInt("id_user"));
        bid.setAmount(rs.getDouble("amount"));
        bid.setPlacedAt(rs.getTimestamp("placed_at").toLocalDateTime());
        return bid;
    }
}