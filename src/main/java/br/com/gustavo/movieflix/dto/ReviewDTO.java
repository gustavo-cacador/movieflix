package br.com.gustavo.movieflix.dto;

import br.com.gustavo.movieflix.entities.Review;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReviewDTO {

    private Long id;
    private String text;
    private String userName;

    @JsonIgnore
    private Long movieId;

    public ReviewDTO() {
    }

    public ReviewDTO(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public ReviewDTO(Review entity) {
        id = entity.getId();
        text = entity.getText();

        if (entity.getUser() != null) {
            this.userName = entity.getUser().getName();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
}
