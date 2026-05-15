package it.unical.progettoweb.service;

import it.unical.progettoweb.dao.PostDao;
import it.unical.progettoweb.dao.ReviewDao;
import it.unical.progettoweb.dao.impl.UserDao;
import it.unical.progettoweb.dto.request.ReviewRequest;
import it.unical.progettoweb.dto.response.ReviewDto;
import it.unical.progettoweb.model.Review;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewDao reviewDao;
    private final PostDao postDao;
    private final UserDao userDao;

    public ReviewDto create(ReviewRequest request, int userId){
        postDao.get(request.getPostId()).orElseThrow(()-> new RuntimeException("Post not found"));

        if(reviewDao.existByUserIdAndPostId(userId, request.getPostId()))
            throw new RuntimeException("Hai già recensito questo post");

        if(request.getRating() < 1 || request.getRating() > 5)
            throw new RuntimeException("La tua valutazione deve essere compresa tra 1 e 5");

        Review review = new Review();
        review.setId(generateUniqueId());
        review.setTitle(request.getTitle());
        review.setDescription(request.getDescription());
        review.setRating(request.getRating());
        review.setUserId(userId);
        review.setPostId(request.getPostId());

        return toDto(reviewDao.save(review));
    }

    public ReviewDto update(int id, ReviewRequest request, int userId) {
        Review existing = reviewDao.get(id)
                .orElseThrow(() -> new RuntimeException("Recensione non trovata"));


        if (existing.getUserId() != userId)
            throw new SecurityException("Non puoi modificare la recensione di un altro utente");

        if (request.getRating() < 1 || request.getRating() > 5)
            throw new IllegalArgumentException("Il rating deve essere tra 1 e 5");

        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setRating(request.getRating());

        return toDto(reviewDao.update(existing));
    }
    public void delete(int id, int userId, String role) {
        Review existing = reviewDao.get(id)
                .orElseThrow(() -> new RuntimeException("Recensione non trovata"));

        if (!role.equals("ADMIN") && existing.getUserId() != userId)
            throw new SecurityException("Non puoi eliminare la recensione di un altro utente");

        reviewDao.delete(id);
    }
    public List<ReviewDto> getByPostId(int postId) {
        return reviewDao.findByPostId(postId).stream().map(this::toDto).toList();
    }

    public List<ReviewDto> getByUserId(int userId) {
        return reviewDao.findByUserId(userId).stream().map(this::toDto).toList();
    }

    public Double getAverageRating(int postId) {
        return reviewDao.getAverageRatingForPost(postId);
    }
    public ReviewDto getById(int id) {
        return toDto(reviewDao.get(id).orElseThrow(() -> new RuntimeException("Recensione non trovata")));
    }

    private int generateUniqueId() {
        int id;
        do {
            id = new Random().nextInt(89999) + 10000;
        } while (reviewDao.get(id).isPresent()); // stesso pattern di PostService
        return id;
    }
    private ReviewDto toDto(Review r) {
        String buyerName = userDao.get(r.getUserId())
                .map(user -> user.getName() + " " + user.getSurname())
                .orElse("Utente Sconosciuto");
        return new ReviewDto(r.getId(), r.getTitle(), r.getDescription(),buyerName,
                r.getRating(), r.getCreatedAt(), r.getUserId(), r.getPostId());
    }

    public List<ReviewDto> getAll(){
        return reviewDao.getAll().stream().map(this::toDto).toList();
    }
}
