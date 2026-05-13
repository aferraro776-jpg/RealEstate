package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.Photo;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PhotoRowMapper implements RowMapper<Photo> {

    @Override
    public Photo mapRow(ResultSet rs, int rowNum) throws SQLException {
        Photo photo = new Photo();
        photo.setId(rs.getInt("id"));
        photo.setUrl(rs.getString("url"));
        photo.setPostId(rs.getInt("post_id"));
        return photo;
    }
}