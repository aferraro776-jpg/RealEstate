package it.unical.progettoweb.dao;

import it.unical.progettoweb.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao extends Dao<Review, Integer> {
    List<Review> findByPostId(int realEstateId);
    List<Review> findByUserId(int userId);
    Double getAverageRatingForPost(int postId);
    boolean existByUserIdAndPostId(int userId, int postId);
}