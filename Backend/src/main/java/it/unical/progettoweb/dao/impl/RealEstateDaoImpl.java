package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.mapper.RealEstateRowMapper;
import it.unical.progettoweb.model.RealEstate;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class RealEstateDaoImpl {

    private final JdbcTemplate jdbcTemplate;
    private final RealEstateRowMapper realEstateRowMapper;

    public Optional<RealEstate> findById(int id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM real_estate WHERE id = ?",
                            realEstateRowMapper, id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<RealEstate> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM real_estate ORDER BY id",
                realEstateRowMapper
        );
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM real_estate WHERE id = ?", id);
    }
}
