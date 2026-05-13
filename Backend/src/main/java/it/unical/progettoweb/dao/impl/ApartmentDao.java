package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.RealEstateDao;
import it.unical.progettoweb.mapper.RealEstateRowMapper;
import it.unical.progettoweb.model.Apartment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ApartmentDao implements RealEstateDao<Apartment> {

    private final JdbcTemplate jdbcTemplate;
    private final RealEstateRowMapper realEstateRowMapper;
    private final RowMapper<Apartment> rowMapper;

    public ApartmentDao(JdbcTemplate jdbcTemplate, RealEstateRowMapper realEstateRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.realEstateRowMapper = realEstateRowMapper;
        this.rowMapper = (rs, rowNum) -> {
            Apartment a = new Apartment();
            realEstateRowMapper.mapCommon(a, rs);
            a.setNumberOfRooms(rs.getInt("numberOfRooms"));
            a.setFloor(rs.getInt("floor"));
            a.setHasElevator(rs.getBoolean("hasElevator"));
            return a;
        };
    }

    @Override
    public Apartment save(Apartment a) {
        jdbcTemplate.update(
                "INSERT INTO real_estate (id, title, description, square_metres, latit, longit, address, created_at, type, number_of_rooms, floor, has_elevator) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 'APARTMENT', ?, ?, ?)",
                a.getId(), a.getTitle(), a.getDescription(), a.getSquareMetres(),
                a.getLatit(), a.getLongit(), a.getAddress(),
                a.getNumberOfRooms(), a.getFloor(), a.getHasElevator()
        );
        return a;
    }

    @Override
    public Optional<Apartment> get(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM real_estate WHERE id=? AND type='APARTMENT'", rowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Apartment> getAll() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='APARTMENT' ORDER BY id", rowMapper);
    }

    @Override
    public Apartment update(Apartment a) {
        jdbcTemplate.update(
                "UPDATE real_estate SET title=?, description=?, square_metres=?, latit=?, longit=?, address=?, number_of_rooms=?, floor=?, has_elevator=? WHERE id=?",
                a.getTitle(), a.getDescription(), a.getSquareMetres(),
                a.getLatit(), a.getLongit(), a.getAddress(),
                a.getNumberOfRooms(), a.getFloor(), a.getHasElevator(), a.getId()
        );
        return a;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM real_estate WHERE id=?", id);
    }

    @Override
    public List<Apartment> findAllOrderBySquareMetresAsc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='APARTMENT' ORDER BY square_metres ASC", rowMapper);
    }

    @Override
    public List<Apartment> findAllOrderBySquareMetresDesc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='APARTMENT' ORDER BY square_metres DESC", rowMapper);
    }

    @Override
    public List<Apartment> findAllOrderByPriceAsc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_estate=r.id WHERE r.type='APARTMENT' ORDER BY p.current_price ASC",
                rowMapper
        );
    }

    @Override
    public List<Apartment> findAllOrderByPriceDesc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_estate=r.id WHERE r.type='APARTMENT' ORDER BY p.current_price DESC",
                rowMapper
        );
    }
}
//...