package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.BidDao;
import it.unical.progettoweb.mapper.BidRowMapper;
import it.unical.progettoweb.model.Bid;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BidDaoImpl implements BidDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Bid> rowMapper;

    public BidDaoImpl(JdbcTemplate jdbcTemplate, BidRowMapper bidRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = bidRowMapper;
    }

    @Override
    public Bid save(Bid bid) {
        jdbcTemplate.update(
                "INSERT INTO bids (id, id_auction, id_user, amount, placed_at) VALUES (?, ?, ?, ?, NOW())",
                bid.getId(),
                bid.getAuctionId(),
                bid.getUserId(),
                bid.getAmount()
        );
        return bid;
    }

    @Override
    public Optional<Bid> get(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM bids WHERE id = ?", rowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Bid> getAll() {
        return jdbcTemplate.query("SELECT * FROM bids ORDER BY placed_at DESC", rowMapper);
    }

    @Override
    public Bid update(Bid bid) {
        jdbcTemplate.update(
                "UPDATE bids SET id_auction=?, id_user=?, amount=? WHERE id=?",
                bid.getAuctionId(),
                bid.getUserId(),
                bid.getAmount(),
                bid.getId()
        );
        return bid;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM bids WHERE id = ?", id);
    }

    @Override
    public List<Bid> findByAuctionId(int auctionId) {
        return jdbcTemplate.query(
                "SELECT * FROM bids WHERE id_auction = ? ORDER BY amount DESC",
                rowMapper, auctionId
        );
    }

    @Override
    public Optional<Bid> findBestBid(int auctionId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM bids WHERE id_auction = ? ORDER BY amount DESC LIMIT 1",
                            rowMapper, auctionId
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}