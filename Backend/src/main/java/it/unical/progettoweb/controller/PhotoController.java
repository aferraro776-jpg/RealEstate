package it.unical.progettoweb.controller;

import it.unical.progettoweb.dao.PhotoDao;
import it.unical.progettoweb.dao.impl.PhotoDaoImpl;
import it.unical.progettoweb.model.Photo;
import it.unical.progettoweb.proxy.PhotoCollection;
import it.unical.progettoweb.service.CloudflareR2Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoDao photoDao;
    private final CloudflareR2Service r2Service;

    public PhotoController(PhotoDaoImpl photoDao, CloudflareR2Service r2Service) {
        this.photoDao = photoDao;
        this.r2Service = r2Service;
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Photo>> getByPost(@PathVariable int postId) {
        PhotoCollection collection = photoDao.getPhotoCollectionForPost(postId);
        return ResponseEntity.ok(collection.getPhotos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Photo> getById(@PathVariable int id) {
        return photoDao.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadAndCreate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("postId") int postId) {

        try {
            String imageUrl = r2Service.uploadFile(file);

            Photo photo = new Photo();
            photo.setId(generateUniqueId());
            photo.setUrl(imageUrl);
            photo.setPostId(postId);

            photoDao.save(photo);

            return ResponseEntity.ok(photo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'upload: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        return photoDao.get(id).map(existing -> {
            r2Service.deleteFile(existing.getUrl());

            photoDao.delete(id);

            return ResponseEntity.ok("Photo deleted");
        }).orElse(ResponseEntity.notFound().build());
    }

    private int generateUniqueId() {
        return new java.util.Random().nextInt(89999) + 10000;
    }
}