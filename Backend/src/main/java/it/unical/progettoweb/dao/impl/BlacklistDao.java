package it.unical.progettoweb.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BlacklistDao {

    private final JdbcTemplate jdbcTemplate;

    public BlacklistDao(JdbcTemplate jdbc) {
        this.jdbcTemplate = jdbc;
    }

    public void ban(String email) {
        jdbcTemplate.update(
                "INSERT INTO blacklist (email) VALUES (?)",
                email
        );
    }


    public void unban(String email) {
        jdbcTemplate.update(
                "DELETE FROM blacklist WHERE email = ?",
                email
        );
    }


    public boolean isBanned(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM blacklist WHERE email = ?",
                Integer.class, email
        );
        return count != null && count > 0;
    }


    public List<String> getAll() {
        return jdbcTemplate.query(
                "SELECT email FROM blacklist",
                (rs, rowNum) -> rs.getString("email")
        );
    }


    public Optional<String> findByEmail(String email) {
        try {
            String result = jdbcTemplate.queryForObject(
                    "SELECT email FROM blacklist WHERE email = ?",
                    String.class, email
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}