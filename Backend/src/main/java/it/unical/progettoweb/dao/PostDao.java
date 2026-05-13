package it.unical.progettoweb.dao;

import it.unical.progettoweb.model.Post;

import java.util.List;

public interface PostDao extends Dao<Post, Integer> {
    List<Post> findBySellerId(int sellerId);
    List<Post> findByRealEstateId(int realEstateId);
    List<Post> findAllOrderByPriceAsc();
    List<Post> findAllOrderByPriceDesc();
    void reducePrice(int postId, double newPrice);
}