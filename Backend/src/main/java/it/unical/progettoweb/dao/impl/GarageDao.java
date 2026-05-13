package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.RealEstateDao;
import it.unical.progettoweb.mapper.RealEstateRowMapper;
import it.unical.progettoweb.model.Garage;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GarageDao implements RealEstateDao<Garage> {

    private final JdbcTemplate jdbcTemplate;
    private final RealEstateRowMapper realEstateRowMapper;
    private final RowMapper<Garage> rowMapper;

    public GarageDao(JdbcTemplate jdbcTemplate, RealEstateRowMapper realEstateRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.realEstateRowMapper = realEstateRowMapper;
        this.rowMapper = (rs, rowNum) -> {
            Garage g = new Garage();
            realEstateRowMapper.mapCommon(g, rs);
            g.setWidth(rs.getDouble("width"));
            g.setHeight(rs.getDouble("height"));
            g.setIsElectric(rs.getBoolean("is_electric"));
            return g;
        };
    }

    @Override
    public Garage save(Garage g) {
        jdbcTemplate.update(
                "INSERT INTO real_estate (id, title, description, square_metres, latit, longit, address, created_at, type, width, height, is_electric) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 'GARAGE', ?, ?, ?)",
                g.getId(), g.getTitle(), g.getDescription(), g.getSquareMetres(),
                g.getLatit(), g.getLongit(), g.getAddress(),
                g.getWidth(), g.getHeight(), g.getIsElectric()
        );
        return g;
    }

    @Override
    public Optional<Garage> get(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM real_estate WHERE id=? AND type='GARAGE'", rowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Garage> getAll() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='GARAGE' ORDER BY id", rowMapper);
    }

    @Override
    public Garage update(Garage g) {
        jdbcTemplate.update(
                "UPDATE real_estate SET title=?, description=?, square_metres=?, latit=?, longit=?, address=?, width=?, height=?, is_electric=? WHERE id=?",
                g.getTitle(), g.getDescription(), g.getSquareMetres(),
                g.getLatit(), g.getLongit(), g.getAddress(),
                g.getWidth(), g.getHeight(), g.getIsElectric(), g.getId()
        );
        return g;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM real_estate WHERE id=?", id);
    }

    @Override
    public List<Garage> findAllOrderBySquareMetresAsc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='GARAGE' ORDER BY square_metres ASC", rowMapper);
    }

    @Override
    public List<Garage> findAllOrderBySquareMetresDesc() {
        return jdbcTemplate.query("SELECT * FROM real_estate WHERE type='GARAGE' ORDER BY square_metres DESC", rowMapper);
    }

    @Override
    public List<Garage> findAllOrderByPriceAsc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_estate=r.id WHERE r.type='GARAGE' ORDER BY p.current_price ASC",
                rowMapper
        );
    }

    @Override
    public List<Garage> findAllOrderByPriceDesc() {
        return jdbcTemplate.query(
                "SELECT r.* FROM real_estate r JOIN posts p ON p.id_real_estate=r.id WHERE r.type='GARAGE' ORDER BY p.current_price DESC",
                rowMapper
        );
    }
}