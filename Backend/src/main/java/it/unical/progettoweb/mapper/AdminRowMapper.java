package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.Admin;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AdminRowMapper extends PersonRowMapper<Admin> {

    @Override
    public Admin mapRow(ResultSet rs,int rowNum) throws SQLException {
        Admin admin = new Admin();
        mapPersonFields(admin, rs);
        return admin;
    }
}
