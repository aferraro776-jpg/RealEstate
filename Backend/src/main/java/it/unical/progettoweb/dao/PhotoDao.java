package it.unical.progettoweb.dao;

import it.unical.progettoweb.model.Photo;
import it.unical.progettoweb.proxy.PhotoCollection;

import java.util.List;

public interface PhotoDao extends Dao<Photo, Integer> {
    List<Photo> findByPostId(int postId);
    PhotoCollection getPhotoCollectionForPost(int postId);
}