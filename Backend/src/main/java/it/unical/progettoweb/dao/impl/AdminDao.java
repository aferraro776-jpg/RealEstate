package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.PersonDao;
import it.unical.progettoweb.mapper.AdminRowMapper;
import it.unical.progettoweb.model.Admin;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminDao implements PersonDao<Admin> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Admin> rowMapper;

    public AdminDao(JdbcTemplate jdbc, AdminRowMapper mapper) {
        this.jdbcTemplate = jdbc;
        this.rowMapper = mapper;
    }

    @Override
    public Admin save(Admin admin) {
        jdbcTemplate.update(
                "INSERT INTO admins (id, name, surname, email, password) VALUES (?, ?, ?, ?, ?)",
                admin.getId(),
                admin.getName(),
                admin.getSurname(),
                admin.getEmail(),
                admin.getPassword()
        );
        return admin;
    }

    @Override
    public Optional<Admin> get(Integer id) {
        try {
            Admin admin = jdbcTemplate.queryForObject(
                    "SELECT * FROM admins WHERE id = ?",
                    rowMapper, id
            );
            return Optional.ofNullable(admin);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Admin> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM admins ORDER BY id",
                rowMapper
        );
    }

    @Override
    public Admin update(Admin admin) {
        jdbcTemplate.update(
                "UPDATE admins SET name=?, surname=?, email=?, password=? WHERE id=?",
                admin.getName(),
                admin.getSurname(),
                admin.getEmail(),
                admin.getPassword(),
                admin.getId()
        );
        return admin;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM admins WHERE id = ?", id);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        try {
            Admin admin = jdbcTemplate.queryForObject(
                    "SELECT * FROM admins WHERE email = ?",
                    rowMapper, email
            );
            return Optional.ofNullable(admin);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM admins WHERE email = ?",
                Integer.class, email
        );
        return count != null && count > 0;
    }
}
