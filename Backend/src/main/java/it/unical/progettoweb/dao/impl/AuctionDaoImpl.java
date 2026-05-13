package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.AuctionDao;
import it.unical.progettoweb.mapper.AuctionRowMapper;
import it.unical.progettoweb.model.Auction;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuctionDaoImpl implements AuctionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Auction> rowMapper;

    public AuctionDaoImpl(JdbcTemplate jdbcTemplate, AuctionRowMapper auctionRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = auctionRowMapper;
    }

    @Override
    public Auction save(Auction auction) {
        jdbcTemplate.update(
                "INSERT INTO auctions (id, id_post, starting_price, current_best, end_date, is_closed, winner_id) " +
                        "VALUES (?, ?, ?, ?, ?, FALSE, NULL)",
                auction.getId(),
                auction.getPostId(),
                auction.getStartingPrice(),
                auction.getCurrentBest(),
                auction.getEndDate()
        );
        return auction;
    }

    @Override
    public Optional<Auction> get(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM auctions WHERE id = ?", rowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Auction> getAll() {
        return jdbcTemplate.query("SELECT * FROM auctions", rowMapper);
    }

    @Override
    public Auction update(Auction auction) {
        jdbcTemplate.update(
                "UPDATE auctions SET id_post=?, starting_price=?, current_best=?, end_date=?, is_closed=?, winner_id=? WHERE id=?",
                auction.getPostId(),
                auction.getStartingPrice(),
                auction.getCurrentBest(),
                auction.getEndDate(),
                auction.isClosed(),
                auction.getWinnerId(),
                auction.getId()
        );
        return auction;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM auctions WHERE id = ?", id);
    }

    @Override
    public Optional<Auction> findByPostId(int postId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM auctions WHERE id_post = ?", rowMapper, postId)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Auction> findAllOpen() {
        return jdbcTemplate.query(
                "SELECT * FROM auctions WHERE is_closed = FALSE",
                rowMapper
        );
    }

    @Override
    public void updateCurrentBest(int auctionId, double amount) {
        jdbcTemplate.update(
                "UPDATE auctions SET current_best = ? WHERE id = ?",
                amount, auctionId
        );
    }

    @Override
    public void setWinnerAndClose(int auctionId, int winnerId) {
        jdbcTemplate.update(
                "UPDATE auctions SET is_closed = TRUE, winner_id = ? WHERE id = ?",
                winnerId, auctionId
        );
    }

    @Override
    public void closeWithoutWinner(int auctionId) {
        jdbcTemplate.update("DELETE FROM auctions WHERE id = ?", auctionId);
    }
}