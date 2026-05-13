package it.unical.progettoweb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class CloudflareR2Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final String publicUrl;

    public CloudflareR2Service(
            S3Client s3Client,
            @Value("${cloudflare.r2.bucket-name}") String bucketName,
            @Value("${cloudflare.r2.public-url}") String publicUrl) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.publicUrl = publicUrl.endsWith("/") ? publicUrl : publicUrl + "/";
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg";
        String extension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String objectKey = UUID.randomUUID().toString() + extension;

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return publicUrl + objectKey;
    }

    public void deleteFile(String fileUrl) {
        try {
            String objectKey = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteReq);
        } catch (Exception e) {
            System.err.println("Errore durante l'eliminazione da R2: " + e.getMessage());
        }
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}