package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class PersonRowMapper<T extends Person> implements RowMapper<T>{
    @Override
    public abstract T mapRow(ResultSet resultSet,int rowNum) throws SQLException;

    protected void mapPersonFields(T person, ResultSet rs) throws SQLException {
        person.setId(rs.getInt("id"));
        person.setName(rs.getString("name"));
        person.setSurname(rs.getString("surname"));
        person.setEmail(rs.getString("email"));
        person.setPassword(rs.getString("password"));
    }
}
