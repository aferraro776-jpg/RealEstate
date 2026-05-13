package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.Review;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<Review> {

    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setTitle(rs.getString("title"));
        review.setDescription(rs.getString("description"));
        review.setRating(rs.getInt("rating"));
        review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        review.setUserId(rs.getInt("id_user"));
        review.setPostId(rs.getInt("id_post"));
        return review;
    }
}