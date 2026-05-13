package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.RealEstateDao;
import it.unical.progettoweb.mapper.RealEstateRowMapper;
import it.unical.progettoweb.model.NonBuildingLot;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NonBuildingLotDao implements RealEstateDao<NonBuildingLot> {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<NonBuildingLot> rowMapper;

    public NonBuildingLotDao(JdbcTemplate jdbcTemplate, RealEstateRowMapper realEstateRowMapper) {
        this.jdbcTemplate = jdbcTemplate;

        this.rowMapper = (rs, rowNum) -> {
            NonBuildingLot n = new NonBuildingLot();
            realEstateRowMapper.mapCommon(n, rs);
            n.setCropType(rs.getString("cropType"));
            return n;
        };
    }

    @Override
    public NonBuildingLot save(NonBuildingLot n) {
        jdbcTemplate.update(
                "INSERT INTO \"realEstate\" (id, title, description, \"squareMetres\", latit, longit, address, \"createdAt\", type, \"cropType\") " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 'NON_BUILDING_LOT', ?)",
                n.getId(), n.getTitle(), n.getDescription(), n.getSquareMetres(),
                n.getLatit(), n.getLongit(), n.getAddress(),
                n.getCropType()
        );
        return n;
    }

    @Override
    public Optional<NonBuildingLot> get(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM \"realEstate\" WHERE id=? AND type='NON_BUILDING_LOT'", rowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<NonBuildingLot> getAll() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='NON_BUILDING_LOT' ORDER BY id", rowMapper);
    }

    @Override
    public NonBuildingLot update(NonBuildingLot n) {
        jdbcTemplate.update(
                "UPDATE real_estate SET title=?, description=?, square_metres=?, latit=?, longit=?, address=?, croptype=? WHERE id=?",
                n.getTitle(), n.getDescription(), n.getSquareMetres(),
                n.getLatit(), n.getLongit(), n.getAddress(),
                n.getCropType(), n.getId()
        );
        return n;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM real_estate WHERE id=?", id);
    }

    @Override
    public List<NonBuildingLot> findAllOrderBySquareMetresAsc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='NON_BUILDING_LOT' ORDER BY square_metres ASC", rowMapper);
    }

    @Override
    public List<NonBuildingLot> findAllOrderBySquareMetresDesc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='NON_BUILDING_LOT' ORDER BY square_metres DESC", rowMapper);
    }

    @Override
    public List<NonBuildingLot> findAllOrderByPriceAsc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_estate=r.id WHERE r.type='NON_BUILDING_LOT' ORDER BY p.current_price ASC",
                rowMapper
        );
    }

    @Override
    public List<NonBuildingLot> findAllOrderByPriceDesc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_erstate=r.id WHERE r.type='NON_BUILDING_LOT' ORDER BY p.current_price DESC",
                rowMapper
        );
    }
}