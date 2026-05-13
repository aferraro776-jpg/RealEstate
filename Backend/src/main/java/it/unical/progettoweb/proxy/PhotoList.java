package it.unical.progettoweb.proxy;

import it.unical.progettoweb.model.Photo;

import java.util.List;

public class PhotoList implements PhotoCollection {

    private final List<Photo> photos;

    public PhotoList(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public List<Photo> getPhotos() {
        return photos;
    }
}