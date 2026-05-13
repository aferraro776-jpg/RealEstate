package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.Post;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PostRowMapper implements RowMapper<Post> {

    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setTitle(rs.getString("title"));
        post.setDescription(rs.getString("description"));
        post.setPreviousPrice(rs.getDouble("previous_price"));
        post.setCurrentPrice(rs.getDouble("current_price"));
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        post.setSellerId(rs.getInt("id_seller"));
        post.setRealEstateId(rs.getInt("id_real_estate"));
        return post;
    }
}