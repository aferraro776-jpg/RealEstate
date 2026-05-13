package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.User;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class UserRowMapper extends PersonRowMapper<User> {

    @Override
    public User mapRow(ResultSet rs,int rowNum) throws SQLException {
        User user = new User();
        mapPersonFields(user, rs);
        user.setBirthDate(rs.getDate("birthdate"));
        user.setAuthProvider(rs.getString("auth_provider"));
        user.setBanned(rs.getBoolean("is_banned"));
        return user;
    }
}

