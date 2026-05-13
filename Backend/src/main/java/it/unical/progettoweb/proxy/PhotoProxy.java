package it.unical.progettoweb.proxy;

import it.unical.progettoweb.mapper.PhotoRowMapper;
import it.unical.progettoweb.model.Photo;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class PhotoProxy implements PhotoCollection {

    private final int postId;
    private final JdbcTemplate jdbcTemplate;
    private final PhotoRowMapper photoRowMapper;
    private PhotoList realPhotoList = null;

    public PhotoProxy(int postId, JdbcTemplate jdbcTemplate, PhotoRowMapper photoRowMapper) {
        this.postId = postId;
        this.jdbcTemplate = jdbcTemplate;
        this.photoRowMapper = photoRowMapper;
    }

    @Override
    public List<Photo> getPhotos() {
        if (realPhotoList == null) {
            String sql = "SELECT * FROM photos WHERE post_id = ? ORDER BY id";
            List<Photo> photosFromDb = jdbcTemplate.query(sql, photoRowMapper, postId);
            realPhotoList = new PhotoList(photosFromDb);
        }

        return realPhotoList.getPhotos();
    }
}