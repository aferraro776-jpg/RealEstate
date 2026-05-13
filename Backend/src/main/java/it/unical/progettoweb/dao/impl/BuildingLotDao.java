package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.RealEstateDao;
import it.unical.progettoweb.mapper.RealEstateRowMapper;
import it.unical.progettoweb.model.BuildingLot;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BuildingLotDao implements RealEstateDao<BuildingLot> {

    private final JdbcTemplate jdbcTemplate;
    private final RealEstateRowMapper realEstateRowMapper;
    private final RowMapper<BuildingLot> rowMapper;

    public BuildingLotDao(JdbcTemplate jdbcTemplate, RealEstateRowMapper realEstateRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.realEstateRowMapper = realEstateRowMapper;
        this.rowMapper = (rs, rowNum) -> {
            BuildingLot b = new BuildingLot();
            realEstateRowMapper.mapCommon(b, rs);
            b.setCubature(rs.getDouble("cubature"));
            b.setPlannedUse(rs.getString("landUse"));
            return b;
        };
    }

    @Override
    public BuildingLot save(BuildingLot b) {
        jdbcTemplate.update(
                "INSERT INTO real_estate (id, title, description, square_metres, latit, longit, address, created_at, type, cubature, land_use) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 'BUILDING_LOT', ?, ?)",
                b.getId(), b.getTitle(), b.getDescription(), b.getSquareMetres(),
                b.getLatit(), b.getLongit(), b.getAddress(),
                b.getCubature(), b.getPlannedUse()
        );
        return b;
    }

    @Override
    public Optional<BuildingLot> get(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM real_estate WHERE id=? AND type='BUILDING_LOT'", rowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<BuildingLot> getAll() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='BUILDING_LOT' ORDER BY id", rowMapper);
    }

    @Override
    public BuildingLot update(BuildingLot b) {
        jdbcTemplate.update(
                "UPDATE real_estate SET title=?, description=?, square_metres=?, latit=?, longit=?, address=?, cubature=?, land_use=? WHERE id=?",
                b.getTitle(), b.getDescription(), b.getSquareMetres(),
                b.getLatit(), b.getLongit(), b.getAddress(),
                b.getCubature(), b.getPlannedUse(), b.getId()
        );
        return b;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM real_estate WHERE id=?", id);
    }

    @Override
    public List<BuildingLot> findAllOrderBySquareMetresAsc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='BUILDING_LOT' ORDER BY square_metres ASC", rowMapper);
    }

    @Override
    public List<BuildingLot> findAllOrderBySquareMetresDesc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='BUILDING_LOT' ORDER BY square_metres DESC", rowMapper);
    }

    @Override
    public List<BuildingLot> findAllOrderByPriceAsc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_estate=r.id WHERE r.type='BUILDING_LOT' ORDER BY p.current_price ASC",
                rowMapper
        );
    }

    @Override
    public List<BuildingLot> findAllOrderByPriceDesc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_estate=r.id WHERE r.type='BUILDING_LOT' ORDER BY p.current_price DESC",
                rowMapper
        );
    }
}