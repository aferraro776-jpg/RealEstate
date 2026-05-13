package it.unical.progettoweb.proxy;

import it.unical.progettoweb.dao.ReviewDao;
import it.unical.progettoweb.dao.impl.PhotoDaoImpl;
import it.unical.progettoweb.model.Photo;
import it.unical.progettoweb.model.Post;
import it.unical.progettoweb.model.Review;

import java.util.List;

public class PostProxy extends Post {
    private final PhotoDaoImpl photoDao;
    private boolean isPhotosCaches = false;
    private boolean isReviewsCaches = false;
    private final ReviewDao reviewDao;

    public PostProxy(PhotoDaoImpl photoDao, ReviewDao reviewDao) {
        this.photoDao = photoDao;
        this.reviewDao = reviewDao;
    }

    public List<Photo> getPhotos() {
        if (!isPhotosCaches && super.getId() != null) {
            List<Photo> photosCaches = photoDao.getByPostId(getId());
            super.setPhotos(photosCaches);
            isPhotosCaches = true;

        }
        return super.getPhotos();
    }

    public List<Review> getReviews() {
        if (!isReviewsCaches && super.getId() != null) {
            List<Review> reviewsChaches = reviewDao.findByPostId(getId());
            super.setReviews(reviewsChaches);
            isReviewsCaches = true;
        }
        return super.getReviews();
    }

}
