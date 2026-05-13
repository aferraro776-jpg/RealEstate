package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.ReviewDao;
import it.unical.progettoweb.mapper.ReviewRowMapper;
import it.unical.progettoweb.model.Review;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Review> rowMapper;


    @Override
    public Review save(Review review) {
        jdbcTemplate.update(
                "INSERT INTO reviews (id, title, description, rating, created_at, id_user, id_post) " +
                        "VALUES (?, ?, ?, ?, CURRENT_DATE, ?, ?)",
                review.getId(),
                review.getTitle(),
                review.getDescription(),
                review.getRating(),
                review.getUserId(),
                review.getPostId()
        );
        return review;
    }
    @Override
    public Optional<Review> get(Integer id) {
        try {
            Review review = jdbcTemplate.queryForObject(
                    "SELECT * FROM reviews WHERE id = ?", rowMapper, id
            );
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public List<Review> getAll() {
        return jdbcTemplate.query("SELECT * FROM reviews ORDER BY created_at DESC", rowMapper);
    }

    @Override
    public Review update(Review review) {
        jdbcTemplate.update(
                "UPDATE reviews SET title=?, description=?, rating=?, id_user=?, id_post=? WHERE id=?",
                review.getTitle(),
                review.getDescription(),
                review.getRating(),
                review.getUserId(),
                review.getPostId(),
                review.getId()
        );
        return review;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM reviews WHERE id = ?", id);
    }

    @Override
    public List<Review> findByPostId(int postId) {
        return jdbcTemplate.query(
                "SELECT * FROM reviews WHERE id_post = ? ORDER BY created_at DESC",
                rowMapper, postId
        );
    }

    @Override
    public List<Review> findByUserId(int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM reviews WHERE id_user = ? ORDER BY created_at DESC",
                rowMapper, userId
        );
    }

    @Override
    public Double getAverageRatingForPost(int postId) {
        Double avg = jdbcTemplate.queryForObject(
                "SELECT AVG(rating) FROM reviews WHERE id_post = ?",
                Double.class, postId
        );
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    @Override
    public boolean existByUserIdAndPostId(int userId, int postId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reviews WHERE id_user = ? AND id_post = ?",
                Integer.class, userId, postId
        );
        return count != null && count > 0;
    }
}