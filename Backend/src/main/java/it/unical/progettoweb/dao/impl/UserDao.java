package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.PersonDao;
import it.unical.progettoweb.mapper.UserRowMapper;
import it.unical.progettoweb.model.User;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserDao implements PersonDao<User> {

    private final JdbcTemplate jdbc;
    private final RowMapper<User> mapper;


    @Override
    public User save(User user) {
        jdbc.update(
                "INSERT INTO users (id, name, surname, password, email, birthdate, auth_provider, is_banned) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getPassword(),
                user.getEmail(),
                user.getBirthDate(),
                user.getAuthProvider(),
                user.isBanned()
        );
        return user;
    }

    @Override
    public Optional<User> get(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT * FROM users WHERE id = ?", mapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAll() {
        return jdbc.query("SELECT * FROM users", mapper);
    }

    @Override
    public User update(User user) {
        jdbc.update(
                "UPDATE users SET name=?, surname=?, password=?, email=?, birthdate=?, auth_provider=?, is_banned=? WHERE id=?",
                user.getName(),
                user.getSurname(),
                user.getPassword(),
                user.getEmail(),
                user.getBirthDate(),
                user.getAuthProvider(),
                user.isBanned(),
                user.getId()
        );
        return user;
    }

    @Override
    public void delete(Integer id) {
        jdbc.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject("SELECT * FROM users WHERE email = ?", mapper, email)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class, email
        );
        return count != null && count > 0;
    }
}