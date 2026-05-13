package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.RealEstateDao;
import it.unical.progettoweb.mapper.RealEstateRowMapper;
import it.unical.progettoweb.model.Villa;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VillaDao implements RealEstateDao<Villa> {

    private final JdbcTemplate jdbcTemplate;
    private final RealEstateRowMapper realEstateRowMapper;
    private final RowMapper<Villa> rowMapper;

    public VillaDao(JdbcTemplate jdbcTemplate, RealEstateRowMapper realEstateRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.realEstateRowMapper = realEstateRowMapper;
        this.rowMapper = (rs, rowNum) -> {
            Villa v = new Villa();
            realEstateRowMapper.mapCommon(v, rs);
            v.setNumberOfRooms(rs.getInt("numberOfRooms"));
            v.setHasGarden(rs.getBoolean("hasGarden"));
            v.setHasPool(rs.getBoolean("hasPool"));
            v.setNumberOfFloors(rs.getInt("numberOfFloors"));
            return v;
        };
    }

    @Override
    public Villa save(Villa v) {
        jdbcTemplate.update(
                "INSERT INTO real_estate (id, title, description, square_metres, latit, longit, address, created_at, type, number_of_rooms, has_garden, has_pool, number_of_floors) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 'VILLA', ?, ?, ?, ?)",
                v.getId(), v.getTitle(), v.getDescription(), v.getSquareMetres(),
                v.getLatit(), v.getLongit(), v.getAddress(),
                v.getNumberOfRooms(), v.getHasGarden(), v.getHasPool(), v.getNumberOfFloors()
        );
        return v;
    }

    @Override
    public Optional<Villa> get(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM real_estate WHERE id=? AND type='VILLA'", rowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Villa> getAll() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='VILLA' ORDER BY id", rowMapper);
    }

    @Override
    public Villa update(Villa v) {
        jdbcTemplate.update(
                "UPDATE real_estate SET title=?, description=?, square_metres=?, latit=?, longit=?, address=?, number_of_rooms=?, has_garden=?, has_pool=?, number_of_floors=? WHERE id=?",
                v.getTitle(), v.getDescription(), v.getSquareMetres(),
                v.getLatit(), v.getLongit(), v.getAddress(),
                v.getNumberOfRooms(), v.getHasGarden(), v.getHasPool(), v.getNumberOfFloors(), v.getId()
        );
        return v;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM real_estate WHERE id=?", id);
    }

    @Override
    public List<Villa> findAllOrderBySquareMetresAsc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='VILLA' ORDER BY square_metres ASC", rowMapper);
    }

    @Override
    public List<Villa> findAllOrderBySquareMetresDesc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='VILLA' ORDER BY square_metres DESC", rowMapper);
    }

    @Override
    public List<Villa> findAllOrderByPriceAsc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_estate=r.id WHERE r.type='VILLA' ORDER BY p.current_price ASC",
                rowMapper
        );
    }

    @Override
    public List<Villa> findAllOrderByPriceDesc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_estate=r.id WHERE r.type='VILLA' ORDER BY p.current_price DESC",
                rowMapper
        );
    }
}