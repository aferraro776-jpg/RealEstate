package it.unical.progettoweb.model;

import lombok.Data;

@Data
public class Photo {
    private int id;
    private String url;
    private int postId;
}
