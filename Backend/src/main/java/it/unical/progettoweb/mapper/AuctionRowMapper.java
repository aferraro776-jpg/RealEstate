package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.Auction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AuctionRowMapper implements RowMapper<Auction> {

    @Override
    public Auction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Auction auction = new Auction();
        auction.setId(rs.getInt("id"));
        auction.setPostId(rs.getInt("id_post"));
        auction.setStartingPrice(rs.getDouble("starting_price"));
        auction.setCurrentBest(rs.getDouble("current_best"));
        auction.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
        auction.setClosed(rs.getBoolean("is_closed"));

        int winnerId = rs.getInt("winner_id");
        auction.setWinnerId(rs.wasNull() ? null : winnerId);

        return auction;
    }
}