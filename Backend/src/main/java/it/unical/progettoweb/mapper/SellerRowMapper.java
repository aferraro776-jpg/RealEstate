package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.Seller;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SellerRowMapper extends PersonRowMapper<Seller> {

    @Override
    public Seller mapRow(ResultSet rs,int rowNum) throws SQLException {
        Seller seller = new Seller();
        mapPersonFields(seller, rs);
        seller.setVatNumber(rs.getString("vat_number"));
        seller.setBirthDate(rs.getDate("birth_date"));
        return seller;
    }
}

