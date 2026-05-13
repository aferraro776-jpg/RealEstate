package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.Dao;
import it.unical.progettoweb.dao.PhotoDao;
import it.unical.progettoweb.dao.PostDao;
import it.unical.progettoweb.mapper.PhotoRowMapper;
import it.unical.progettoweb.model.Photo;
import it.unical.progettoweb.proxy.PhotoCollection;
import it.unical.progettoweb.proxy.PhotoList;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PhotoDaoImpl implements PhotoDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Photo> rowMapper;


    @Override
    public Photo save(Photo photo) {
        jdbcTemplate.update(
                "INSERT INTO photos (id, url, post_id) VALUES (?, ?, ?)",
                photo.getId(),
                photo.getUrl(),
                photo.getPostId()
        );
        return photo;
    }

    @Override
    public Optional<Photo> get(Integer id) {
        try {
            Photo photo = jdbcTemplate.queryForObject(
                    "SELECT * FROM photos WHERE id = ?",
                    rowMapper, id
            );
            return Optional.ofNullable(photo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Photo> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM photos ORDER BY id",
                rowMapper
        );
    }

    @Override
    public Photo update(Photo photo) {
        jdbcTemplate.update(
                "UPDATE photos SET url=?, post_id=? WHERE id=?",
                photo.getUrl(),
                photo.getPostId(),
                photo.getId()
        );
        return photo;
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update(
                "DELETE FROM photos WHERE id = ?", id
        );
    }

    public List<Photo> getByPostId(Integer postId) {
        return jdbcTemplate.query(
                "SELECT * FROM photos WHERE post_id = ? ORDER BY id",
                rowMapper, postId
        );
    }
    @Override
    public List<Photo> findByPostId(int postId) {
        String sql = "SELECT id, url, post_id FROM photo WHERE post_id = ?";
        return jdbcTemplate.query(sql, rowMapper, postId);
    }

    @Override
    public PhotoCollection getPhotoCollectionForPost(int postId) {
        String sql = "SELECT id, url, post_id FROM photo WHERE post_id = ?";
        List<Photo> photos = jdbcTemplate.query(sql, rowMapper, postId);
        return new PhotoList(photos);
    }
}
